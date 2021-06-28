package ccbb.hrbeu.exonimpact.genestructure;

import java.util.ArrayList;

import ccbb.hrbeu.exonimpact.sequencefeaturewrapper.Extractor_sequence;
import htsjdk.tribble.annotation.Strand;

public class Transcript {
	
	
	public enum ASTYPE {
		SE, A5SS, A3SS, RI, UNKNOWN
	}
	
	
	String transcript_id = "";
	
	String gene_id="";
	
	String protein_id="";
	
	String chr = "";
	Strand strand = Strand.NEGATIVE;

	int tx_start = -1;
	int tx_end = -1;

	int target_start = -1;
	int target_end = -1;

	int cds_start = -1;
	int cds_end = -1;

	public ArrayList<Exon> flank_exons=new ArrayList<Exon>();
	
	ArrayList<Exon> exons = new ArrayList<Exon>();

	boolean is_protein_coding = false;
	
	ASTYPE as_type=ASTYPE.UNKNOWN;

	public Transcript() {

	}

	public String getTranscript_id() {
		return transcript_id;
	}

	public void setTranscript_id(String transcript_id) {
		this.transcript_id = transcript_id;
	}

	public int getTx_start() {
		if(tx_start==-1)
			tx_start=exons.get(0).getExonBegCoorPos();
		
		return tx_start;
	}

	public void setTx_start(int tx_start) {
		this.tx_start = tx_start;
	}

	public int getTx_end() {
		if(tx_end==-1)
			tx_end=exons.get(exons.size()-1).getExonEndCoorPos();
		return tx_end;
	}

	public void setTx_end(int tx_end) {
		this.tx_end = tx_end;
	}

	public ArrayList<Exon> getExons() {
		return exons;
	}

	public void setExons(ArrayList<Exon> arrayList) {
		this.exons = arrayList;
	}

	public void addExon(Exon e) {
		exons.add(e);
	}

	public boolean isIs_protein_coding() {
		return is_protein_coding;
	}

	public void setIs_protein_coding(boolean is_protein_coding) {
		this.is_protein_coding = is_protein_coding;
	}

	public String getChr() {
		return chr;
	}

	public void setChr(String chr) {
		this.chr = chr;
	}

	public Strand getStrand() {
		return strand;
	}

	public void setStrand(Strand strand2) {
		this.strand = strand2;
	}

	public int getTarget_start() {
		return target_start;
	}

	public void setTarget_start(int target_start) {
		this.target_start = target_start;
	}

	public int getTarget_end() {
		return target_end;
	}

	public void setTarget_end(int target_end) {
		this.target_end = target_end;
	}

	public String get_sequence_of_transcript() {
		// TODO Auto-generated method stub
		if(tx_start==-1)
			tx_start=exons.get(0).getExonBegCoorPos();
		
		if(tx_end==-1)
			tx_end=exons.get(exons.size()-1).getExonEndCoorPos();
		
		return Extractor_sequence.get_instance().getSequence(chr, tx_start, tx_end);
	}

	
	public ASTYPE get_as_type() {
		// TODO Auto-generated method stub
		
		return as_type;
	}

	public void set_as_type(ASTYPE asType) {
		// TODO Auto-generated method stub
		this.as_type=asType;
		
	}

	public int getCds_start() {
		return cds_start;
	}

	public void setCds_start(int cds_start) {
		this.cds_start = cds_start;
	}

	public int getCds_end() {
		return cds_end;
	}

	public void setCds_end(int cds_end) {
		this.cds_end = cds_end;
	}

	public String getGene_id() {
		return gene_id;
	}

	public void setGene_id(String gene_id) {
		this.gene_id = gene_id;
	}

	public String getProtein_id() {
		return protein_id;
	}

	public void setProtein_id(String protein_id) {
		this.protein_id = protein_id;
	}
	
}
