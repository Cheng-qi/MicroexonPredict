package ccbb.hrbeu.exonimpact.genestructure;

public class Ptm_site {

	int position = -1;
	String modification = "";
	String transcipt_id = "";
	String uniprot_id = "";

	public Ptm_site(String transcript_id, String uniprot_id, int position, String modification) {
		this.position = position;
		this.modification = modification;
		this.transcipt_id = transcript_id;
		this.uniprot_id = uniprot_id;

	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getModification() {
		return modification;
	}

	public void setModification(String modification) {
		this.modification = modification;
	}

	public String getTranscipt_id() {
		return transcipt_id;
	}

	public void setTranscipt_id(String transcipt_id) {
		this.transcipt_id = transcipt_id;
	}

	public String getUniprot_id() {
		return uniprot_id;
	}

	public void setUniprot_id(String uniprot_id) {
		this.uniprot_id = uniprot_id;
	}

}
