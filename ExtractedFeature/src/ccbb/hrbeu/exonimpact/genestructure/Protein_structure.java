package ccbb.hrbeu.exonimpact.genestructure;

import java.util.ArrayList;

public class Protein_structure {

	String transcript_id = "";
	String aa = "";

	public Protein_structure(){
		
	}
	
	ArrayList<Double> beta_sheet = new ArrayList<Double>();

	ArrayList<Double> random_coil = new ArrayList<Double>();

	ArrayList<Double> alpha_helix = new ArrayList<Double>();

	ArrayList<Double> asa = new ArrayList<Double>();

	ArrayList<Double> disorder = new ArrayList<Double>();

	public int get_size(){
		return aa.length();
	}
	
	public Protein_structure(String transcript_id, String aa, ArrayList<Double> beta_sheet,
			ArrayList<Double> random_coil, ArrayList<Double> alpha_helix, ArrayList<Double> asa,
			ArrayList<Double> disorder) {
		
		
		this.transcript_id = transcript_id;
		this.aa = aa;
		this.beta_sheet = beta_sheet;
		this.random_coil = random_coil;
		this.alpha_helix = alpha_helix;
		this.asa = asa;
		this.disorder = disorder;
	}

	public String getAa() {
		return aa;
	}

	public void setAa(String aa) {
		this.aa = aa;
	}

	public ArrayList<Double> getBeta_sheet() {
		return beta_sheet;
	}

	public void setBeta_sheet(ArrayList<Double> beta_sheet) {
		this.beta_sheet = beta_sheet;
	}

	public ArrayList<Double> getRandom_coil() {
		return random_coil;
	}

	public void setRandom_coil(ArrayList<Double> random_coil) {
		this.random_coil = random_coil;
	}

	public ArrayList<Double> getAlpha_helix() {
		return alpha_helix;
	}

	public void setAlpha_helix(ArrayList<Double> alpha_helix) {
		this.alpha_helix = alpha_helix;
	}

	public ArrayList<Double> getAsa() {
		return asa;
	}

	public void setAsa(ArrayList<Double> asa) {
		this.asa = asa;
	}

	public ArrayList<Double> getDisorder() {
		return disorder;
	}

	public void setDisorder(ArrayList<Double> disorder) {
		this.disorder = disorder;
	}

}
