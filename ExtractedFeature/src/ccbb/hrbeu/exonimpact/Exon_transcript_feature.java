package ccbb.hrbeu.exonimpact;

import ccbb.hrbeu.exonimpact.genestructure.Pfam_domain;
import ccbb.hrbeu.exonimpact.genestructure.Protein_structure;
import ccbb.hrbeu.exonimpact.genestructure.Ptm_site;
import ccbb.hrbeu.exonimpact.genestructure.Transcript;
import ccbb.hrbeu.exonimpact.proteinfeaturewrapper.Extractor_Pfam_feature;
import ccbb.hrbeu.exonimpact.proteinfeaturewrapper.Extractor_Ptm_feature;
import ccbb.hrbeu.exonimpact.proteinfeaturewrapper.Extractor_structure_feature;
import ccbb.hrbeu.exonimpact.sequencefeaturewrapper.Extractor;
import ccbb.hrbeu.exonimpact.sequencefeaturewrapper.Extractor_phylop;
import ccbb.hrbeu.exonimpact.sequencefeaturewrapper.Extractor_phylop_online;
import ccbb.hrbeu.exonimpact.util.Tris;
import ccbb.hrbeu.exonimpact.genestructure.Exon;
import ccbb.hrbeu.exonimpact.genestructure.Match_status;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Exon_transcript_feature {

	private static Logger log = Logger.getLogger(Exon_transcript_feature.class);
	
	public static ArrayList<String> feature_names1=new ArrayList<String>();
	
	public static ArrayList<String> feature_names2=new ArrayList<String>();
	
	static{
		feature_names1.add("raw_input");feature_names1.add("transcript_id");feature_names1.add("gene_id");
		feature_names1.add("is_match");feature_names1.add("exon_id");feature_names1.add("is_protein");
		feature_names1.add("target_region");feature_names1.add("target_region_length");feature_names1.add("target_region_in_protein");
		
//		feature_names1.add("phylop"); 
		feature_names1.add("min_phylop");feature_names1.add("max_phylop");feature_names1.add("ave_phylop");
		
//		feature_names1.add("ss_max_most_prob");feature_names1.add("ss_min_most_prob");feature_names1.add("ss_ave_most_prob");
		feature_names1.add("ss_E");feature_names1.add("ss_C");feature_names1.add("ss_H");
		feature_names1.add("ss_max_beta_sheet");feature_names1.add("ss_min_beta_sheet");feature_names1.add("ss_ave_beta_sheet");
		feature_names1.add("ss_max_random_coil");feature_names1.add("ss_min_random_coil");feature_names1.add("ss_ave_random_coil");
		feature_names1.add("ss_max_alpha_helix");feature_names1.add("ss_min_alpha_helix");feature_names1.add("ss_ave_alpha_helix");
		
		feature_names1.add("ave_asa");feature_names1.add("min_asa");feature_names1.add("max_asa");
		
		feature_names1.add("disorder_min_score");feature_names1.add("disorder_max_score");
//		feature_names1.add("disorder_region_ave_score");feature_names1.add("structure_region_ave_score");
//		feature_names1.add("time_of_switch");
//		feature_names1.add("disorder_region_ave_len");feature_names1.add("disorder_region_min_len");feature_names1.add("disorder_region_max_len");
//		feature_names1.add("structure_region_ave_len");feature_names1.add("structure_region_min_len");feature_names1.add("structure_region_max_len");
		feature_names1.add("disorder_ave_score");
		
//		feature_names1.add("max_percent_in_domain");feature_names1.add("percent_in_exon");
		
//		feature_names1.add("normilized_ptm");
		
		feature_names1.add("proteinLength");
	}
	
	static{
		feature_names2.add("raw_input");feature_names2.add("transcript_id");feature_names2.add("gene_id");
		feature_names2.add("is_match");feature_names2.add("exon_id");feature_names2.add("is_protein");
		feature_names2.add("target_region");feature_names2.add("target_region_length");feature_names2.add("target_region_in_protein");
		
		feature_names2.add("min_phylop");feature_names2.add("max_phylop");feature_names2.add("ave_phylop");
		
		feature_names2.add("ss_1");feature_names2.add("ss_2");feature_names2.add("ss_3");
		feature_names2.add("ss_6");feature_names2.add("ss_5");feature_names2.add("ss_4");
		feature_names2.add("ss_9");feature_names2.add("ss_8");feature_names2.add("ss_7");
		feature_names2.add("ss_12");feature_names2.add("ss_11");feature_names2.add("ss_10");
		
		feature_names2.add("asa_1");feature_names2.add("asa_2");feature_names2.add("asa_3");
		
		feature_names2.add("disorder_1");feature_names2.add("disorder_2");
		feature_names2.add("disorder_3");feature_names2.add("disorder_4");
		feature_names2.add("disorder_5");
		feature_names2.add("disorder_6");feature_names2.add("disorder_9");feature_names2.add("disorder_8");
		feature_names2.add("disorder_7");feature_names2.add("disorder_11");feature_names2.add("disorder_10");
		feature_names2.add("disorder_12");
		
		feature_names2.add("pfam2");feature_names2.add("pfam1");
		
		feature_names2.add("ptm");
		
		feature_names2.add("proteinLength");
		
		feature_names2 = feature_names1;

	}
//	public static feature_names2 = feature_names1;

	private int exon_index=-1;

	private String gene_id;
	
	public Exon_transcript_feature(String raw_input2, Transcript iter_transcript, Transcript fragment,
			boolean is_protein_coding, Match_status is_match2, int exon_index,
			Tris<String, Integer, Integer> exon_region_in_genome2,
			Tris<String, Integer, Integer> exon_region_in_protein2, ArrayList<Extractor> feature_extractors2) throws ClassNotFoundException, SQLException, IOException, InterruptedException {
		// TODO Auto-generated constructor stub
		
		this.gene_id=iter_transcript.getGene_id();
		this.miso_frag=fragment;
		this.transcript=iter_transcript;
		this.transcript_id = iter_transcript.getTranscript_id();
		this.raw_input = raw_input2;
		this.is_protein = is_protein_coding;
		this.exon_index=exon_index;
		this.is_match = is_match2;
		this.exon_region_in_genome = exon_region_in_genome2;
		this.exon_region_in_protein = exon_region_in_protein2;

		if (is_match.tell_match()) {
			log.trace("build feature");
			build_feature();
		}
	}

	ArrayList<Extractor> Feature_extractors = new ArrayList<Extractor>();

	
	Transcript transcript=null;
	Transcript miso_frag=null;
	
	private Boolean is_protein = false;
	private Match_status is_match = null;

	private Tris<String, Integer, Integer> exon_region_in_genome = new Tris<String, Integer, Integer>();
	private Tris<String, Integer, Integer> exon_region_in_protein = new Tris<String, Integer, Integer>();

	private String transcript_id = "";
	private String exon_id = "";
	private String raw_input = "";

	/**
	 * The features to input.
	 * 
	 * private Double avg_phylop_score = -1.0; private Double normalized_ptm =
	 * -1.0; ArrayList<Double> Pfam1 = new ArrayList<Double>(); ArrayList
	 * <Double> pfam2 = new ArrayList<Double>(); ArrayList<Double> asa_feature =
	 * new ArrayList<Double>(); ArrayList<Double> disorder_features = new
	 * ArrayList<Double>(); ArrayList<Double> ss_features = new ArrayList
	 * <Double>();
	 */

	ArrayList<Double> features = new ArrayList<Double>();

	/**
	 * The raw features.
	 */
	ArrayList<Double> phylop_scores_target_region = new ArrayList<Double>();
	ArrayList<Double> phylop_scores_fragment = new ArrayList<Double>();

	ArrayList<Pfam_domain> pfam_domains = new ArrayList<Pfam_domain>();
	ArrayList<Ptm_site> ptm_sites = new ArrayList<Ptm_site>();

	Protein_structure protein_structures = new Protein_structure();
	
	void build_feature() throws SQLException, ClassNotFoundException, IOException, InterruptedException {
		log.trace("Get raw features from database.");
		Get_features();
		
		if(protein_structures.get_size()==0||(exon_region_in_protein.getValue3()-exon_region_in_protein.getValue2()<0 ) ){
			log.error("The transcrpt's protein sturctures is zero or target protein region is 0: "+transcript_id);
			return;
		}
		log.trace("Calculate features using database.");
		Cal_features();
		
	}
	
	void Get_features() throws SQLException, ClassNotFoundException, IOException, InterruptedException {
		
		log.trace("get phylop for the whole fragment!");
		
		//phylop_scores_fragment=Extractor_phylop.get_instance().extract(miso_frag.getChr(),miso_frag.getTx_start(),miso_frag.getTx_end() );
		//transcript.getTx_start(), transcript.getTx_end() );
		//log.trace("get phylop for the target region!");

		phylop_scores_target_region = Extractor_phylop.get_instance().extract(exon_region_in_genome.getValue1(),exon_region_in_genome.getValue2(),
				exon_region_in_genome.getValue3() );
		
		//phylop_scores_fragment=Extractor_phylop_online.extract(miso_frag.getChr(),	miso_frag.getTx_start(),miso_frag.getTx_end());
		
		//phylop_scores_target_region=Extractor_phylop_online.extract(exon_region_in_genome.getValue1(),	exon_region_in_genome.getValue2(), exon_region_in_genome.getValue3());
		
		
		log.trace("got phylop for the target region!");
		
		
		protein_structures = Extractor_structure_feature.get_instance().get_structure_features(transcript_id);

		pfam_domains = Extractor_Pfam_feature.get_instance().get_pfam_domains(transcript_id);
		ptm_sites = Extractor_Ptm_feature.get_instance().get_ptm_sites(transcript_id);

	}

	void Cal_features() {
		
		// add phylop features
		features.addAll(Feature_calculator.calculator_phylop(phylop_scores_target_region) );

		
		if(protein_structures.get_size()<exon_region_in_protein.getValue3()){
			log.error("The transcirpt's protein coding region for amino acid has problem :"+transcript_id );
			return;
		}
		
		
		if(protein_structures.getAlpha_helix().size()<exon_region_in_protein.getValue3()){
			log.error("The transcirpt's protein coding region for ss has problem :"+transcript_id );
			return;
		}
		
		// add secondary structure features
		features.addAll(Feature_calculator.calculator_ss(protein_structures.getBeta_sheet(),
				protein_structures.getRandom_coil(), protein_structures.getAlpha_helix(),
				exon_region_in_protein.getValue2(), exon_region_in_protein.getValue3()));
		
		if(protein_structures.getAsa().size()<exon_region_in_protein.getValue3()){
			log.error("The transcirpt's protein coding region for asa has problem :"+transcript_id );
			return;
		}
		// add asa features
		features.addAll(Feature_calculator.calculator_asa(protein_structures.getAsa(), protein_structures.getAa(),
				exon_region_in_protein.getValue2(), exon_region_in_protein.getValue3()));
		
		
		if(protein_structures.getDisorder().size()<exon_region_in_protein.getValue3()){
			log.error("The transcirpt's protein coding region for disorder has problem :"+transcript_id );
			return;
		}
		// add disorder features
		features.addAll(Feature_calculator.calculator_disorder(protein_structures.getDisorder(),
				exon_region_in_protein.getValue2(), exon_region_in_protein.getValue3()));

		//pfam and ptm can be none;
		// add pfam features
//		features.addAll(Feature_calculator.calculator_pfam(pfam_domains, exon_region_in_protein.getValue2(),
//				exon_region_in_protein.getValue3()));

		// add ptm features
//		features.addAll(Feature_calculator.calculator_ptm(ptm_sites, exon_region_in_protein.getValue2(),
//				exon_region_in_protein.getValue3()));
		
		
		features.add((double) ( protein_structures.get_size()) );


	}
	
	public void build_xml(String file_name ) throws ParserConfigurationException {
		log.trace("build the genome part of the XML");
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		
		db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		
		// Document doc = db.parse(new File("meng.xml"));
		Element tableRoot = doc.createElement("root");
		doc.appendChild(tableRoot);
		
		Element eventRoot = doc.createElement("event");
		tableRoot.appendChild(eventRoot);
		
		Element posNode = doc.createElement("position");
		posNode.setTextContent(raw_input);
		eventRoot.appendChild(posNode);
		
		Element strandNode = doc.createElement("strand");
		strandNode.setTextContent(transcript.getStrand().toString() );
		eventRoot.appendChild(strandNode);
		
		Element exonsNode = doc.createElement("alternativeExons");
		Iterator<Exon> iteExon = miso_frag.getExons().iterator();
		
		
		while (iteExon.hasNext()) {
			Exon exon = iteExon.next();
			Element exonNode = doc.createElement("exon");
			exonNode.setAttribute("start", "" + exon.getExonBegCoorPos());
			exonNode.setAttribute("end", "" + exon.getExonEndCoorPos());
			exonNode.setAttribute("modBy3", "" + new Boolean(exon.getExonLength()%3==0).toString() );
			exonNode.setAttribute("IfAlternative", "" + exon.getIfAlternative());
			exonNode.setTextContent(exon.getSequenceAsString() );
			exonsNode.appendChild(exonNode);
			
		}
		eventRoot.appendChild(exonsNode);
		log.trace("build over of exons in the miso");
		
		
		Element geneSeqNode = doc.createElement("geneSequence");
		geneSeqNode.setAttribute("start", "" + miso_frag.getTx_start());
		geneSeqNode.setAttribute("end", "" + miso_frag.getTx_end());
		geneSeqNode.setTextContent(miso_frag.get_sequence_of_transcript());
		eventRoot.appendChild(geneSeqNode);
		log.trace("build over of geneSequence in the miso");
		
		
		Element asTypeNode = doc.createElement("asType");
		asTypeNode.setTextContent(miso_frag.get_as_type().toString() );
		eventRoot.appendChild(asTypeNode);
		//sequence

		Element seqNode = doc.createElement("sequenceOfExons");
		seqNode.setTextContent(miso_frag.get_sequence_of_transcript());
		eventRoot.appendChild(seqNode);
		
		log.trace("build over of sequenceOfExons in the miso");
		
		Element conservationNode = doc.createElement("conservation");
		String result = convert_arr_to_str(phylop_scores_fragment);
		log.trace("The size of phylop score vector: "+phylop_scores_fragment.size() );

		conservationNode.setTextContent(result);
		eventRoot.appendChild(conservationNode);
		
		log.trace("build the protein part of the XML");
		processXMLProtein(doc,tableRoot,eventRoot);
		
		log.trace("build the transcript part of the XML");
		processXMLTranscript(doc,eventRoot);
		log.trace("output file name is: "+file_name);
		
		log.trace("curent directory is: "+System.getProperty("user.dir") );
		outputXML(doc,(file_name+".xml") );//miso_frag.getExons().get(0).toString()+".xml") );
	}
	
	private void processXMLProtein(Document doc, Element tableRoot, Element eventRoot){
		Element proteinNode = doc.createElement("isoform");
		eventRoot.appendChild(proteinNode);
		log.trace("The length of protein: "+this.protein_structures.get_size());
		Element proteinSeqNode = doc.createElement("proteinSequence");
		
		//log.trace(protein_structures.getAa());
		proteinSeqNode.setTextContent(protein_structures.getAa().toString());
		proteinNode.appendChild(proteinSeqNode);
		
		//log.trace("protein_sequence: "+protein_structures.getAa());
		Element proteinFrameNode = doc.createElement("frame");
		proteinFrameNode.setTextContent(0 + " ");
		proteinNode.appendChild(proteinFrameNode);
		
		Element asRegionNode=doc.createElement("asRegion");
		asRegionNode.setAttribute("start",""+ miso_frag.getTarget_start() );
		asRegionNode.setAttribute("end", ""+miso_frag.getTarget_end() );
		proteinNode.appendChild(asRegionNode);
		
		ArrayList<Double> norm_asa=new ArrayList<Double>();
		
		if(protein_structures.getAsa().size()>0){
			double asa_max=Collections.max(protein_structures.getAsa() );
			double asa_min=Collections.min(protein_structures.getAsa() );
			for(Double ite_val:protein_structures.getAsa() ){
				norm_asa.add( (ite_val-asa_min)/asa_max);
				
			}
		}
		
		Element asa_node=doc.createElement("asa");
		String asa_str=convert_arr_to_str(norm_asa);
		asa_node.setTextContent(asa_str);
		proteinNode.appendChild(asa_node);
		
		Element random_coil_node=doc.createElement("random_coil");
		String random_coil_str=convert_arr_to_str(protein_structures.getRandom_coil() );
		random_coil_node.setTextContent(random_coil_str);
		proteinNode.appendChild(random_coil_node);
		
		Element beta_sheet_node=doc.createElement("beta_sheet");
		String beta_sheet_str=convert_arr_to_str(protein_structures.getBeta_sheet());
		beta_sheet_node.setTextContent(beta_sheet_str);
		proteinNode.appendChild(beta_sheet_node);
		
		Element alpha_helix_node=doc.createElement("alpha_helix");
		String alpha_helix_str=convert_arr_to_str(protein_structures.getAlpha_helix());
		alpha_helix_node.setTextContent(alpha_helix_str);
		proteinNode.appendChild(alpha_helix_node);
		
		
		Element pfamsNode = doc.createElement("pfam_domains");
		for (Pfam_domain itePfam : pfam_domains) {

			Element pfamNode = doc.createElement("pfam_domain");
			// p.setTextContent(pfamInfo.get(j));
			pfamNode.setAttribute("start", "" + itePfam.getStart() );
			pfamNode.setAttribute("end", "" + itePfam.getEnd()  );
			pfamNode.setAttribute("family", "" + itePfam.getFamily()  );
			pfamNode.setAttribute("description", itePfam.getName()  );
			pfamsNode.appendChild(pfamNode);
		}
		proteinNode.appendChild(pfamsNode);
		
		
		//ptm_sites.add(new Ptm_site(this.transcript_id,"test",89,"a") );
		
		Element ptmsNode = doc.createElement("ptms");
		for (Ptm_site itePtm : ptm_sites) {
			
			Element ptmNode = doc.createElement("ptm");
			// p.setTextContent(pfamInfo.get(j));
			ptmNode.setAttribute("start", "" + itePtm.getPosition() );
			ptmNode.setAttribute("modification", itePtm.getModification()  );
			ptmsNode.appendChild(ptmNode);
		}
		proteinNode.appendChild(ptmsNode);
		
		
		Element disValueNode = doc.createElement("disProt_value");
		String disorder_score_str = "";
		String disprotStr="";
		
		for (int j = 0; j < protein_structures.getDisorder().size(); ++j) {

			disorder_score_str += protein_structures.getDisorder().get(j);
			if(protein_structures.getDisorder().get(j)>0.5 ){
				disprotStr+="D";
			}else{
				disprotStr+="S";				
			}
			if (protein_structures.getDisorder().size() - 1 != j)
				disorder_score_str += " ";
		}
		
		//log.trace(disorder_score_str);
		disValueNode.setTextContent(disorder_score_str);
		disValueNode.setAttribute("disOrder_string", disprotStr );
		disValueNode.setAttribute("disOrder_type", "Unknown" );
		disValueNode.setAttribute("disOrder_type_In_AS", "Unknown" );
		
		proteinNode.appendChild(disValueNode);
		
		Element uniprotptmsNode = doc.createElement("uniprotptm");
		// String ptmStr="";
		for (int j = 0; j < ptm_sites.size(); ++j) {
			Element p = doc.createElement("field" + j);
			// ptmStr += ptms.get(j);
			//TODO! wrong here, forget how I do this.
			p.setTextContent(ptm_sites.get(j).getModification() );
			uniprotptmsNode.appendChild(p);
			
		}
		
		// ptmsNode.setTextContent(ptmStr);
		proteinNode.appendChild(uniprotptmsNode);
		
	}
	
	private void processXMLTranscript(Document doc, Element tableRoot) {
		
		Element transcriptsNode = doc.createElement("transcripts");
		Element exonsNode = doc.createElement("transcript");
		exonsNode.setAttribute("ID", transcript.getTranscript_id() );

			Iterator<Exon> iteExon = transcript.getExons().iterator();
			while (iteExon.hasNext()) {
				Exon exon = iteExon.next();
				Element exonNode = doc.createElement("exon");
				
				exonNode.setAttribute("start", "" + exon.getExonBegCoorPos());
				exonNode.setAttribute("end", "" + exon.getExonEndCoorPos());
				exonNode.setAttribute("cds_start", ""+exon.getCds_start() );
				exonNode.setAttribute("cds_end", ""+exon.getCds_end() );
				exonNode.setAttribute("strand", transcript.getStrand().toString() );
				exonNode.setAttribute("modBy3", "" + exon.getModBy3());
				exonNode.setAttribute("IfAlternative",
						"" + exon.getIfAlternative());
				exonNode.setTextContent(exon.getSequenceAsString());
				exonsNode.appendChild(exonNode);
			}
			transcriptsNode.appendChild(exonsNode);

		
		tableRoot.appendChild(transcriptsNode);

	}
	
	public void outputXML(Document doc, String fileName) {
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();

			DOMSource source = new DOMSource(doc);
			File xml_file = new File(fileName);
			StreamResult result = new StreamResult(xml_file);

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);
			xml_file.setExecutable(true, false);
			xml_file.setReadable(true, false);
			xml_file.setWritable(true, false);
			
		} catch (TransformerConfigurationException e) {
			System.out.println(e.getMessage());
		} catch (TransformerException e) {
			System.out.println(e.getMessage());
		}
	}
	
	// writeStringToFile(File file, String data, boolean append)
	void output(LinkedList<StringBuilder> output_str) throws IOException {
		StringBuilder line=new StringBuilder();
		
		
		line.append(raw_input.replaceAll("\\t", ":")+",");
		line.append(transcript_id+",");
		line.append(gene_id+",");
		line.append(is_match+",");
		
		line.append(this.transcript_id+":"+exon_index+",");
		line.append(is_protein+",");
		
		line.append(exon_region_in_genome+",");
		
		line.append((exon_region_in_genome.getValue3()-exon_region_in_genome.getValue2()+1)+ ",");
		
		line.append(exon_region_in_protein+",");
	
		for (Double ite_feature_val : features) {
			line.append(ite_feature_val+",");
		}
		
		//line.substring(0, line.length()-2);
		line.deleteCharAt(line.length()-1);
		//FileUtils.writeStringToFile(new File(output_str), "\n", true);
		output_str.add(line);
		
	}

	private String convert_arr_to_str(ArrayList<Double> vals){
		String result="";
		
		for (int j = 0; j < vals.size(); ++j) {

			result += vals.get(j);

			if (vals.size() - 1 != j)
				result += " ";
		}
		
		return result;
	}
	
	public boolean isIs_protein() {
		return is_protein;
	}

	public void setIs_protein(boolean is_protein) {
		this.is_protein = is_protein;
	}

	public Match_status isIs_match() {
		return is_match;
	}

	public void setIs_match(Match_status is_match) {
		this.is_match = is_match;
	}

	public String getTranscript_id() {
		return transcript_id;
	}

	public void setTranscript_id(String transcript_id) {
		this.transcript_id = transcript_id;
	}

	public String getExon_id() {
		return exon_id;
	}

	public void setExon_id(String exon_id) {
		this.exon_id = exon_id;
	}

	public String getRaw_input() {
		return raw_input;
	}

	public void setRaw_input(String raw_input) {
		this.raw_input = raw_input;
	}
	

	public ArrayList<Pfam_domain> getPfam_domains() {
		return pfam_domains;
	}

	public void setPfam_domains(ArrayList<Pfam_domain> pfam_domains) {
		this.pfam_domains = pfam_domains;
	}

	public ArrayList<Ptm_site> getPtm_sites() {
		return ptm_sites;
	}

	public void setPtm_sites(ArrayList<Ptm_site> ptm_sites) {
		this.ptm_sites = ptm_sites;
	}

}
