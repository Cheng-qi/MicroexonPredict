package ccbb.hrbeu.exonimpact.genestructure;

import org.apache.log4j.Logger;

import htsjdk.tribble.bed.FullBEDFeature;

public class ExtendBEDFeature extends FullBEDFeature {

	Logger log = Logger.getLogger(ExtendBEDFeature.class);

	boolean is_protein_coding = false;
	
	String protein_id=null;
	
	String gene_id=null;

	public ExtendBEDFeature(String chr, int start, int end) {
		super(chr, start, end);
		// TODO Auto-generated constructor stub
	}

	public boolean isIs_protein_coding() {
		return is_protein_coding;
	}

	public void setIs_protein_coding(boolean is_protein_coding) {
		this.is_protein_coding = is_protein_coding;
	}
	
	public Transcript transcript=new Transcript();

	public String getProtein_id() {
		return protein_id;
	}

	public void setProtein_id(String protein_id) {
		this.protein_id = protein_id;
	}

	public String getGene_id() {
		return gene_id;
	}

	public void setGene_id(String gene_id) {
		this.gene_id = gene_id;
	}
	
}
