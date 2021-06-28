package ccbb.hrbeu.exonimpact.sequencefeaturewrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import ccbb.hrbeu.exonimpact.genestructure.ExtendBEDFeature;
import ccbb.hrbeu.exonimpact.genestructure.ExternBEDCodec;
import ccbb.hrbeu.exonimpact.genestructure.Transcript;
import ccbb.hrbeu.exonimpact.util.Tris;
import htsjdk.tribble.AbstractFeatureReader;
import htsjdk.tribble.CloseableTribbleIterator;
import htsjdk.tribble.annotation.Strand;
import htsjdk.tribble.index.Index;
import htsjdk.tribble.index.IndexFactory;
import htsjdk.tribble.readers.LineIterator;

public class Bed_region_extractor implements Extractor {

	Logger log = Logger.getLogger(Bed_region_extractor.class);

	private Index index;

	private AbstractFeatureReader<ExtendBEDFeature, LineIterator> source;

	private static Bed_region_extractor instance = null;

	public static Bed_region_extractor get_instance() {
		if (instance != null)
			return instance;
		else
			instance = new Bed_region_extractor();

		return instance;
	}

	public void build_index(String path_to_bed) {
		index = IndexFactory.createIntervalIndex(new File(path_to_bed), new ExternBEDCodec());
		//IndexFactory.cre
		source = (AbstractFeatureReader<ExtendBEDFeature, LineIterator>) AbstractFeatureReader
				.getFeatureReader(path_to_bed, new ExternBEDCodec(), index);

	}

	@SuppressWarnings("deprecation")
	public ArrayList<Transcript> getTranscripts(String chr, long beg, long end) throws IOException {
		ArrayList<Transcript> transcripts = new ArrayList<Transcript>();
			CloseableTribbleIterator<ExtendBEDFeature> iter = source.query(chr, (int) beg, (int) end);
			
			while (iter.hasNext()) {
				ExtendBEDFeature cur_iter = iter.next();
				
				Transcript t = cur_iter.transcript;
				
				t.setGene_id(cur_iter.getGene_id()) ;
				t.setProtein_id(cur_iter.getProtein_id());
				
				t.setIs_protein_coding(cur_iter.isIs_protein_coding());
				
				t.setTx_start(cur_iter.getStart());
				t.setTx_end(cur_iter.getEnd());
				t.setChr(cur_iter.getChr());
				t.setStrand(cur_iter.getStrand());
				t.setTranscript_id(cur_iter.getName());
				transcripts.add(t);
				
			}

		return transcripts;
		
	}

	public Tris<String,Integer,Integer> get_transcript_exon_region(String chr, long beg, 
			long end,String transcript_id,int exon_index,boolean only_as_exon) throws IOException {
		Tris<String,Integer,Integer> exon_region=new Tris<String,Integer,Integer>("",-1,-1);
		
			CloseableTribbleIterator<ExtendBEDFeature> iter = source.query(chr, (int) beg, (int) end);
			
			while (iter.hasNext()) {
				ExtendBEDFeature cur_iter = iter.next();
				
				Transcript t = cur_iter.transcript;
				if(cur_iter.getName().equals(transcript_id) ){
					
					t.setIs_protein_coding(cur_iter.isIs_protein_coding());
					
					//the equal here is to remove the first and last exon.
					if(only_as_exon&&(exon_index<=1 || exon_index>=t.getExons().size() ) ) {
						return exon_region;
					}
					
					if(cur_iter.getStrand().equals(Strand.NEGATIVE))
						exon_index=t.getExons().size()-exon_index+1;
						
					int exon_beg=t.getExons().get(exon_index-1).getExonBegCoorPos();
					int exon_end=t.getExons().get(exon_index-1).getExonEndCoorPos();
					
					exon_region.setValue1(chr);
					exon_region.setValue2(exon_beg);
					exon_region.setValue3(exon_end);
					return exon_region;
				}
				
			}
		
		return exon_region;
		
	}
	
	@Override
	public void extract() {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		Bed_region_extractor.get_instance()
				.build_index("E:\\limeng\\splicingSNP\\miso_events\\refGene_extern_hg19.bed");
		ArrayList<Transcript> trans;
		try {
			trans = Bed_region_extractor.get_instance().getTranscripts("chr1", 910578, 917497);
			
			for (Transcript iter_trans : trans) {

				System.out.print(iter_trans.getChr() + "\t");
				System.out.print(iter_trans.getTx_start() + "\t");
				System.out.print(iter_trans.getTx_end() + "\t");

				System.out.print(iter_trans.getExons().get(0).getExonBegCoorPos() + "\t");
				System.out.println(iter_trans.getExons().get(0).getExonEndCoorPos());

			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
