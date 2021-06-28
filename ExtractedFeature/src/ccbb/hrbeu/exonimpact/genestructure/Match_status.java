package ccbb.hrbeu.exonimpact.genestructure;

public enum Match_status {
	whole_match,
	partial_match,
	indel_match,
	not_match;
	
	
	
	int exon_index=0;
	
	public boolean tell_match(){
		if(this.equals(not_match))
			return false;
		return true;
		
	}

	public int getExon_index() {
		return exon_index;
	}

	public void setExon_index(int exon_index) {
		this.exon_index = exon_index;
	}


}
