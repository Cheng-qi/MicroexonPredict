package ccbb.hrbeu.exonimpact;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;

import ccbb.hrbeu.exonimpact.genestructure.Bed_region_map;
import ccbb.hrbeu.exonimpact.genestructure.Exon;
import ccbb.hrbeu.exonimpact.genestructure.Match_status;
import ccbb.hrbeu.exonimpact.genestructure.Transcript;
import ccbb.hrbeu.exonimpact.genestructure.Transcript.ASTYPE;
import ccbb.hrbeu.exonimpact.sequencefeaturewrapper.Bed_decoder;
import ccbb.hrbeu.exonimpact.sequencefeaturewrapper.Bed_region_extractor;
import ccbb.hrbeu.exonimpact.sequencefeaturewrapper.Extractor;
import ccbb.hrbeu.exonimpact.sequencefeaturewrapper.Miso_decoder;
import ccbb.hrbeu.exonimpact.util.Tris;
import htsjdk.tribble.annotation.Strand;

public class Exon_feature {

	Logger log = Logger.getLogger(Exon_feature.class);

	public Exon_feature(String input) throws SQLException, ClassNotFoundException, IOException, InterruptedException {
		raw_input = input;
		build_feautre();
	}

	ArrayList<Extractor> Feature_extractors = new ArrayList<Extractor>();
	ArrayList<Exon_transcript_feature> exon_transcript_features = new ArrayList<Exon_transcript_feature>();
	String raw_input = "";

	boolean is_protein = false;
	Match_status is_match = Match_status.not_match;

	Tris<String, Integer, Integer> exon_region_in_genome = new Tris<String, Integer, Integer>();
	Tris<String, Integer, Integer> exon_region_in_protein = new Tris<String, Integer, Integer>();
	
	String Transcript_id = "";
	String Exon_id = "";
	
	public void build_feautre() throws SQLException, ClassNotFoundException, IOException, InterruptedException {
		
		Transcript fragment =null;
		if(Bed_region_map.is_transcript_id(raw_input) ){
			fragment=new Transcript();
			
			String[] line_arr=raw_input.split(":");
			
			String t_transcript_id=line_arr[0];
			int exon_index=Integer.parseInt(line_arr[1]);
			if(exon_index<=0){
				log.error("The input is: "+raw_input+" , probelm!!");
				
			}
			
Tris<String, Integer, Integer> t_transcript_region = Bed_region_map.get_instance().name_region_map.get(t_transcript_id);
			
			if(t_transcript_region==null){
				return;
			}
			
Tris<String, Integer, Integer> exon_region = Bed_region_extractor.get_instance().
			get_transcript_exon_region(t_transcript_region.getValue1(),t_transcript_region.getValue2(),
					t_transcript_region.getValue3(), t_transcript_id, exon_index,true);
			
			if(exon_region.getValue2()==-1){
				log.error("error input"+raw_input+" don't have annotations");
			}
			
			fragment.setChr(t_transcript_region.getValue1() );
			fragment.setTranscript_id(t_transcript_id);
			fragment.setTx_start(t_transcript_region.getValue2() );
			fragment.setTx_end(t_transcript_region.getValue3() );
			
			fragment.setTarget_start(exon_region.getValue2() );
			fragment.setTarget_end(exon_region.getValue3() );
			
			
		}else if(Bed_decoder.is_bed(raw_input) ){
			fragment = Bed_decoder.get_instance().get_transcript(raw_input);
			
		}else if(Bed_decoder.is_exon_region(raw_input)){
			log.trace("waning, the input must same with BED 0-based coordinate");
			raw_input=raw_input.replaceAll(":", "\\\t");
			log.trace("the processed input is:"+raw_input);
			fragment = Bed_decoder.get_instance().get_transcript(raw_input);
			
		}else if(Miso_decoder.tellASType(raw_input)!=ASTYPE.UNKNOWN){
			fragment = Miso_decoder.get_instance().get_transcript(raw_input);
			
		}else{
			log.error("The input: "+raw_input+" is not bed format or miso format");
			return;
		}
		
		ArrayList<Transcript> mapped_transcripts = Bed_region_extractor.get_instance().getTranscripts(fragment.getChr(),
				fragment.getTarget_start(), fragment.getTarget_end());

		log.trace("number of transcripts mapped for the input is: " + mapped_transcripts.size() + " ");

		for (Transcript iter_transcript : mapped_transcripts) {
			if( (!fragment.getTranscript_id().equals("") ) &&
					!(fragment.getTranscript_id().equals(iter_transcript.getTranscript_id()) ) ){
				
				continue;
			}
			
			
			log.trace("The mapped transcript is: " + iter_transcript.getTranscript_id());
			// if doesn��t match, it should return a pair of <0,0>
			exon_region_in_genome.setValue1(fragment.getChr());
			exon_region_in_genome.setValue2(fragment.getTarget_start());
			exon_region_in_genome.setValue3(fragment.getTarget_end());

			is_match = miso_match(fragment, iter_transcript);

			if (is_match.tell_match()) {
				log.trace("The input is: " + raw_input + " and is match");
				exon_region_in_protein = covert_to_protein_region(exon_region_in_genome, iter_transcript);

			} else {
				log.trace("The input is: " + raw_input + " and not match");

			}
			// Pair
			// match_protein_region=covert_to_protein_region(match_genome_region,
			// iter_transcript);

			exon_transcript_features.add(new Exon_transcript_feature(raw_input, iter_transcript,fragment,
					iter_transcript.isIs_protein_coding(), is_match,is_match.getExon_index(),
					exon_region_in_genome, exon_region_in_protein,
					Feature_extractors));
		}
		
	}

	private Match_status miso_match(Transcript fragment, Transcript transcript) {
		// TODO Auto-generated method stub
		// TODO!!! compare CDS and miso here, should be exon and miso!!!!!!!!!!!!
		Match_status m = Match_status.not_match;
		
		String chr=fragment.getChr();
		int target_start = fragment.getTarget_start();
		int target_end = fragment.getTarget_end();
		
		for(Exon ite_exon :transcript.getExons()){
			int exon_start = ite_exon.getExonBegCoorPos();
			int exon_end = ite_exon.getExonEndCoorPos();
			if(exon_start<=target_start&&exon_end>=target_end){
				m=Match_status.indel_match;
				return m;
			}
			
		}
		
		
		String target_str=chr+":"+target_start+"-"+target_end;
		
		HashMap<String,Integer> all_exon=new HashMap<String,Integer>();
		
		int i=0;
		for (Exon ite_exon : transcript.getExons() ) {

			int exon_start = ite_exon.getExonBegCoorPos();
			int exon_end = ite_exon.getExonEndCoorPos();
			String one_str=chr+":"+exon_start+"-"+exon_end;
			if(transcript.getStrand().equals(Strand.POSITIVE) ){
				all_exon.put(one_str,++i);
			}else{
				all_exon.put(one_str,transcript.getExons().size()-i );
				i++;
			}
			
		}
		
		if(!all_exon.containsKey(target_str)){
			//return Match_status.not_match;
			
			return Match_status.not_match;
		}
		
		
		for(i=0;i<fragment.flank_exons.size();++i){
			
			int flank_start = fragment.flank_exons.get(i).getExonBegCoorPos();
			int flank_end = fragment.flank_exons.get(i).getExonEndCoorPos();
			
			String one_str=chr+":"+flank_start+"-"+flank_end;
			
			if(!all_exon.containsKey(one_str) ){
				m=Match_status.partial_match;
				m.setExon_index(all_exon.get(target_str).intValue() );
				return m;
			}
			
		}
		
		m=Match_status.whole_match;
		m.setExon_index(all_exon.get(target_str).intValue() );
		
		return m;
	}
	
	public Tris<String, Integer, Integer> covert_to_protein_region(
			Tris<String, Integer, Integer> exon_region_in_genome,Transcript trans) {

		int region_start = exon_region_in_genome.getValue2();
		int region_end = exon_region_in_genome.getValue3();

		double protein_len = 0;
		double start_len = 0;
		double end_len = 0;

		int ts_start=trans.getCds_start();
		int ts_end=trans.getCds_end();
		
		for (Exon ite_exon : trans.getExons()) {
			int exon_start = ite_exon.getExonBegCoorPos();
			int exon_end = ite_exon.getExonEndCoorPos();

			if (  region_start>=exon_start && region_start<=exon_end) {
				start_len = protein_len + (region_start - exon_start) +1;
			}
			
			if (region_end >= exon_start && region_end <= exon_end) {
				end_len = protein_len + (region_end - exon_start) ;
			}
			
			int cur_protein_len = (exon_end>ts_end?ts_end:exon_end) - (exon_start>ts_start?exon_start:ts_start) ;
			//log.trace("exon_start="+exon_start+" exon_end="+exon_end+" cur_protein_len="+cur_protein_len+ " ts_start="+ts_start+" ts_end="+ts_end);
			
			if(cur_protein_len>0)
				protein_len+=cur_protein_len+1;
			
		}
		
		protein_len/=3;start_len/=3;end_len/=3;
		
		int start=-1,end=-1;
		
		//log.trace("protein length info= "+protein_len+" start="+start_len+" end="+end_len);
		
		if (trans.getStrand().equals(Strand.NEGATIVE)) {
			//a stop codon in the end of the protein.
			protein_len-=1;
			start_len-=1;
			end_len-=1;
			
	start=Math.floor(protein_len - end_len +1)>0?(int)Math.floor(protein_len - end_len +1):1;
	end=Math.ceil(protein_len - start_len+1)<=protein_len?(int)Math.ceil(protein_len - start_len+1):(int)protein_len;
			
			return new Tris<String, Integer, Integer>(trans.getTranscript_id(), start,end );
		}
		
		start=Math.floor(start_len)>0?(int)Math.floor(start_len):1;
		end=Math.ceil(end_len)<=protein_len?(int)Math.ceil(end_len):(int)protein_len;
		
		return new Tris<String, Integer, Integer>(trans.getTranscript_id(), start, end );
		
		
	}

	public void output(LinkedList<StringBuilder> output_str) throws IOException {
		for (Exon_transcript_feature ite_exon_trans_fea : exon_transcript_features) {
			ite_exon_trans_fea.output(output_str);
		}

	}

	public ArrayList<Extractor> getFeature_extractors() {
		return Feature_extractors;
	}

	public void setFeature_extractors(ArrayList<Extractor> feature_extractors) {
		Feature_extractors = feature_extractors;
	}

	public ArrayList<Exon_transcript_feature> getExon_transcript_features() {
		return exon_transcript_features;
	}

	public void setExon_transcript_features(ArrayList<Exon_transcript_feature> exon_transcript_features) {
		this.exon_transcript_features = exon_transcript_features;
	}

	public String getRaw_input() {
		return raw_input;
	}

	public void setRaw_input(String raw_input) {
		this.raw_input = raw_input;
	}

	public String getTranscript_id() {
		return Transcript_id;
	}

	public void setTranscript_id(String transcript_id) {
		Transcript_id = transcript_id;
	}

	public String getExon_id() {
		return Exon_id;
	}

	public void setExon_id(String exon_id) {
		Exon_id = exon_id;
	}

	public void build_xml(String file_name) throws ParserConfigurationException {
		// TODO Auto-generated method stub
		for(Exon_transcript_feature ite_each_feature:exon_transcript_features){
			
			ite_each_feature.build_xml(file_name);
			
		}
	}

	public void build_xml(String file_name,String transcript_id) throws ParserConfigurationException {
		// TODO Auto-generated method stub
		for(Exon_transcript_feature ite_each_feature:exon_transcript_features){
			if(ite_each_feature.getTranscript_id().equals(transcript_id) )
				ite_each_feature.build_xml(file_name);	
		}
		
	}

	public static void main(String[] args){
		
		
	}
	
}
