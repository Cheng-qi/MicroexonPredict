package ccbb.hrbeu.exonimpact.genestructure;

import ccbb.hrbeu.exonimpact.sequencefeaturewrapper.Extractor_sequence;

public class Exon {
	
	public Exon(String torg, String tchr, String tseq, int tbegPos, int tendPos) {
		org = torg;
		chr=tchr;
		sequence = tseq;
		begPos = tbegPos;
		endPos = tendPos;
	}

	public Exon(String tchr, int tbegPos, int tendPos) {
		this.chr=tchr;
		this.begPos = tbegPos;
		this.endPos = tendPos;
		
	}
	
	public Exon(String tchr, int tbegPos, int tendPos,String tseq) {
		this.chr=tchr ;
		this.begPos = tbegPos;
		this.endPos = tendPos;
		this.sequence=tseq;
	}
	
	public Exon(String torg, String tchr, String tseq) {
		org = torg;
		chr=tchr;
		chr=tchr;
		sequence = tseq;

	}

	public Exon(String tseq) {
		sequence = tseq;
	}

	public void setExonBegCoorPos(int tbegpos) {
		begPos = tbegpos;
	}

	public void setExonEndCoorPos(int tendpos) {
		endPos = tendpos;
	}

	public void setAlternative(boolean tisAlternative) {
		isAlternative = tisAlternative;

	}

	public boolean getIfAlternative() {
		return isAlternative;
	}

	private String name;

	private String sequence;

	private boolean isAlternative = false;

	private String org = "";

	private String chr = "";

	private int begPos = -1;

	private int endPos = -1;

	private int cds_start=-1;
	
	private int cds_end=-1;
	
	private int modBy3 = -1;

	public int getModBy3() {

		return (endPos - begPos + 1) % 3;

	}

	public int getExonBegCoorPos() {
		return begPos;

	}

	public int getExonEndCoorPos() {
		return endPos;

	}

	public int getExonLength() {
		return endPos - begPos + 1;

	}

	public String getSequenceAsString() {
		this.sequence=Extractor_sequence.get_instance().getSequence(chr, begPos, endPos);

		return sequence;

	}
	
	public String toString(){
		return ""+chr+"-"+begPos+"-"+endPos;
		
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
	
}
