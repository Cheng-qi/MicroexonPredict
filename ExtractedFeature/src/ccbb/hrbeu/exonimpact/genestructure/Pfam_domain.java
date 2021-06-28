package ccbb.hrbeu.exonimpact.genestructure;

public class Pfam_domain {

	String transcript_id = "";
	int start = -1;
	int end = -1;
	String family = "";
	String name = "";
	String clan = "";

	public Pfam_domain(String transcript_id, int start, int end, String family, String name, String clan) {
		this.transcript_id = transcript_id;
		this.start = start;
		this.end = end;
		this.family = family;
		this.name = name;
		this.clan = clan;
	}

	public String getTranscript_id() {
		return transcript_id;
	}

	public void setTranscript_id(String transcript_id) {
		this.transcript_id = transcript_id;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClan() {
		return clan;
	}

	public void setClan(String clan) {
		this.clan = clan;
	}

}
