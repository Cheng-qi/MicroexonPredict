package ccbb.hrbeu.exonimpact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import ccbb.hrbeu.exonimpact.genestructure.Pfam_domain;
import ccbb.hrbeu.exonimpact.genestructure.Ptm_site;

public class Feature_calculator {

	static Logger log = Logger.getLogger(Feature_calculator.class);

	private static HashMap<Integer, Integer> asa_norm = new HashMap<Integer, Integer>();

	static {
		asa_norm.put((int) 'A', 115);
		asa_norm.put((int) 'C', 135);
		asa_norm.put((int) 'D', 150);
		asa_norm.put((int) 'E', 190);
		asa_norm.put((int) 'F', 210);
		asa_norm.put((int) 'G', 75);
		asa_norm.put((int) 'H', 195);
		asa_norm.put((int) 'I', 175);
		asa_norm.put((int) 'K', 200);
		asa_norm.put((int) 'L', 170);
		asa_norm.put((int) 'M', 185);
		asa_norm.put((int) 'N', 160);
		asa_norm.put((int) 'P', 145);
		asa_norm.put((int) 'Q', 180);
		asa_norm.put((int) 'R', 225);
		asa_norm.put((int) 'S', 115);
		asa_norm.put((int) 'T', 140);
		asa_norm.put((int) 'V', 155);
		asa_norm.put((int) 'W', 255);
		asa_norm.put((int) 'Y', 230);
		asa_norm.put((int) 'X', 100);
	}

	public static double asa_norm(double asa, char aa) {

		if (!asa_norm.containsKey((int) aa)) {
			log.trace("Size of asa: " + asa_norm.size() + " The aa can't be norm: " + aa);
			return asa;
		}

		double norm_asa = -1;
		norm_asa =asa / asa_norm.get((int) aa) * 100.0;

		return norm_asa;
	}

	public static ArrayList<Double> calculator_phylop(ArrayList<Double> arr) {
		ArrayList<Double> ret =new ArrayList<Double>();
		double ave = 0;
		double max =-5000,min=5000;
		int count = 0;
		
		
//		max=arr.get(0);
//		min=arr.get(0);
		for (int i = 0; i < arr.size(); i++) {
//			max=arr.get(0);
//			min=arr.get(0);
			if(arr.get(i)>max){max = arr.get(i);}
			if(arr.get(i)<min){min = arr.get(i);}
			count++;
			ave += arr.get(i);
		}
		if(count==0){ave = 0;max=0;min=0;}
		else{ave = ave / count;}
		ret.add(min);
		ret.add(max);
		ret.add(ave);

		return ret;
	}
	
	public static double ave_of_list(List<Double> arr) {
		double ave = 0;
		int count = 0;
		
		for (Double i:arr) {
			count++;
			ave += i.doubleValue();
		}

		return count == 0 ? count : (ave / count);
	}

	public static double ave_of_list_int(ArrayList<Integer> arr) {
		double ave = 0;
		int count = 0;

		for (int i = 0; i < arr.size(); i++) {
			count++;
			ave += arr.get(i);
		}

		return count == 0 ? count : (ave / count);
	}

	public static double sum_of_list_int(ArrayList<Integer> arr) {
		double ave = 0;
		
		for (int i = 0; i < arr.size(); i++) {
			ave += arr.get(i);
		}

		return ave;
	}
	
	public static ArrayList<Double> calculator_disorder(ArrayList<Double> arr, int start, int end) {
		ArrayList<Double> ret = new ArrayList<Double>();
		
		start = Math.max(start-2,1);//cq加20200804
		end = Math.min(end+2, arr.size());//cq加20200804
		
		int region_len=end-start+1;
		Double min_val = Double.MAX_VALUE;
		Double max_val = Double.MIN_VALUE;
		Double ave_disorder_reginon_score = 0.0;
		Double ave_structure_reginon_score = 0.0;
		int time_switch = 0;
		Double ave_disorder = 0.0;

		Double ave_disorder_length = 0.0;
		Double max_disorder_length = 0.0;
		Double min_disorder_length = 0.0;

		Double ave_strucutre_length = 0.0;
		Double max_strucutre_length = 0.0;
		Double min_strucutre_length = 0.0;

		ArrayList<Integer> structure_regions_length = new ArrayList<Integer>();
		ArrayList<Integer> disorder_regions_length = new ArrayList<Integer>();
		//structure_regions_length.add(0);disorder_regions_length.add(0);
		
		int cur_strucutre_region_len = 0;
		int cur_disorder_region_len = 0;
		
		start-=1;
		
		for (; start < end; start++) {
			Double val = arr.get(start);
			//log.trace(val);
			
			ave_disorder += val;

			if (val < min_val) {
				min_val = val;
			}

			if (val > max_val) {
				max_val = val;
			}

			if (val < 0.5) {
				ave_structure_reginon_score += val;
				cur_strucutre_region_len++;
			}

			if (val > 0.5) {
				ave_disorder_reginon_score += val;
				cur_disorder_region_len++;
			}

			if (val < 0.5 && start!=0 && arr.get(start - 1) > 0.5) {
				time_switch++;
				if(cur_disorder_region_len>0)
					disorder_regions_length.add(cur_disorder_region_len);

				cur_disorder_region_len = 0;
				cur_strucutre_region_len = 1;

			}

			if (val > 0.5 &&start!=0 && arr.get(start - 1) < 0.5) {
				time_switch++;
				if(cur_strucutre_region_len>0)
					structure_regions_length.add(cur_strucutre_region_len);
				
				cur_disorder_region_len = 0;
				cur_strucutre_region_len = 1;
			}
		}

		if (cur_disorder_region_len > 0)
			disorder_regions_length.add(cur_disorder_region_len);

		if (cur_strucutre_region_len > 0)
			structure_regions_length.add(cur_strucutre_region_len);

		ave_disorder_length = disorder_regions_length.size() > 0 ? ave_of_list_int(disorder_regions_length)
				: ave_disorder_length;
		min_disorder_length = disorder_regions_length.size() > 0 ? (double) Collections.min(disorder_regions_length)/region_len
				: min_disorder_length;
		max_disorder_length = disorder_regions_length.size() > 0 ? (double) Collections.max(disorder_regions_length)/region_len
				: max_disorder_length;

		ave_strucutre_length = structure_regions_length.size() > 0 ? ave_of_list_int(structure_regions_length)/region_len
				: ave_strucutre_length;
		min_strucutre_length = structure_regions_length.size() > 0 ? (double) Collections.min(structure_regions_length)/region_len
				: min_strucutre_length;
		max_strucutre_length = structure_regions_length.size() > 0 ? (double) Collections.max(structure_regions_length)/region_len
				: max_strucutre_length;

		ret.add(min_val);
		ret.add(max_val);
		
//		ret.add(disorder_regions_length.size()==0?ave_disorder_reginon_score:ave_disorder_reginon_score/sum_of_list_int(disorder_regions_length) );
//		ret.add(structure_regions_length.size()==0?ave_structure_reginon_score:ave_structure_reginon_score/sum_of_list_int(structure_regions_length) );
		
//		ret.add((double) time_switch);
		
//		ret.add(ave_disorder_length);
//		ret.add(min_disorder_length);
//		ret.add(max_disorder_length);
//		
//		ret.add(ave_strucutre_length);
//		ret.add(min_strucutre_length);
//		ret.add(max_strucutre_length);
		
		ret.add(ave_disorder/region_len);

		return ret;
	}

	public static ArrayList<Double> calculator_ss(ArrayList<Double> beta_sheet, ArrayList<Double> random_coil,
			ArrayList<Double> alpha_helix, int start, int end) {
		ArrayList<Double> ret = new ArrayList<Double>();

		ArrayList<Double> most_prob_strucutre = new ArrayList<Double>();
		ArrayList<Double> beta_sheet_cur = new ArrayList<Double>();
		ArrayList<Double> random_coil_cur = new ArrayList<Double>();
		ArrayList<Double> alpha_helix_cur = new ArrayList<Double>();
		
		start = Math.max(start-2,1);//cq加20200804
		end = Math.min(end+2, beta_sheet.size());//cq加20200804
		
		
		for (int i = start-1; i <end; ++i) {
			double tmp = Math.max(Math.max(beta_sheet.get(i), random_coil.get(i)), alpha_helix.get(i));
			
			//20200727cp增
			if(tmp==beta_sheet.get(i)) {
				beta_sheet_cur.add(tmp);
			}else if(tmp==random_coil.get(i)) {
				random_coil_cur.add(tmp);
			}else if(tmp==alpha_helix.get(i)) {
				alpha_helix_cur.add(tmp);
			}
			
			most_prob_strucutre.add(tmp);
		}
		
		double max_most_prob_strucutre = Collections.max(most_prob_strucutre);
		double min_most_prob_strucutre = Collections.min(most_prob_strucutre);
		double ave_most_prob_strucutre = ave_of_list(most_prob_strucutre);
		
		double protein_length = beta_sheet_cur.size()+random_coil_cur.size()+alpha_helix_cur.size();
		
		double ss_E = beta_sheet_cur.size()/protein_length;
		double ss_C = random_coil_cur.size()/protein_length;
		double ss_H = alpha_helix_cur.size()/protein_length;
		
		

		double max_beta_sheet = Collections.max(beta_sheet.subList(start-1, end));
		double min_beta_sheet = Collections.min(beta_sheet.subList(start-1, end));
		double ave_beta_sheet = ave_of_list(beta_sheet.subList(start-1, end));

		double max_random_coil = Collections.max(random_coil.subList(start-1, end));
		double min_random_coil = Collections.min(random_coil.subList(start-1, end));
		double ave_random_coil = ave_of_list(random_coil.subList(start-1, end));

		double max_alpha_helix = Collections.max(alpha_helix.subList(start-1, end));
		double min_alpha_helix = Collections.min(alpha_helix.subList(start-1, end));;
		double ave_alpha_helix = ave_of_list(alpha_helix.subList(start-1, end));

//		ret.add(max_most_prob_strucutre);
//		ret.add(min_most_prob_strucutre);
//		ret.add(ave_most_prob_strucutre);
		
		ret.add(ss_E);
		ret.add(ss_C);
		ret.add(ss_H);
		
		ret.add(max_beta_sheet);
		ret.add(min_beta_sheet);
		ret.add(ave_beta_sheet);
		ret.add(max_random_coil);
		ret.add(min_random_coil);
		ret.add(ave_random_coil);
		ret.add(max_alpha_helix);
		ret.add(min_alpha_helix);
		ret.add(ave_alpha_helix);

		return ret;
	}

	public static ArrayList<Double> calculator_asa(ArrayList<Double> arr, String aa, int start, int end) {
		ArrayList<Double> ret = new ArrayList<Double>();
		
		start = Math.max(start-2,1);//cq加20200804
		end = Math.min(end+2, arr.size());//cq加20200804
		
		for (int i = 0; i < arr.size(); ++i) {
			arr.set(i, asa_norm(arr.get(i), aa.charAt(i)));
		}
		//
		double ave_asa = ave_of_list(arr.subList(start-1, end) );
		double min_asa = Collections.min(arr.subList(start-1, end) );
		double max_asa = Collections.max(arr.subList(start-1, end) );
		ret.add(ave_asa);
		ret.add(min_asa);
		ret.add(max_asa);

		return ret;
	}

	public static ArrayList<Double> calculator_pfam(ArrayList<Pfam_domain> domains, int start, int end) {

		
		ArrayList<Double> ret = new ArrayList<Double>();
		if(domains.size()==0){
			ret.add(0.0);ret.add(0.0);
			return ret;
		}
		
		ArrayList<Double> precent_in_domain = new ArrayList<Double>();

		HashMap<Integer, Boolean> exon_region_collector = new HashMap<Integer, Boolean>();
		for (int i = start; i <= end; ++i) {
			exon_region_collector.put(i, true);
		}

		for (Pfam_domain iter_domain : domains) {
			int domain_start = iter_domain.getStart();
			int domain_end = iter_domain.getEnd();
			int domain_len = domain_end - domain_start + 1;

			int overlap_len = 0;
			for (int i = domain_start; i <= domain_end; ++i) {
				if (exon_region_collector.containsKey(i)) {
					exon_region_collector.put(i, false);
					overlap_len++;
				}
				precent_in_domain.add(overlap_len / (double) domain_len);

			}
		}
		double max_precent_in_domain = Collections.max(precent_in_domain);

		double percnet_int_exon = 0;
		for (Boolean ite_overlap : exon_region_collector.values()) {
			if (!ite_overlap.booleanValue()) {
				percnet_int_exon++;
			}
		}

		percnet_int_exon = percnet_int_exon / (double) (end - start + 1);

		ret.add(max_precent_in_domain);
		ret.add(percnet_int_exon);

		return ret;
	}

	public static ArrayList<Double> calculator_ptm(ArrayList<Ptm_site> ptm_sites, int start, int end) {
		ArrayList<Double> ret = new ArrayList<Double>();

		int number_of_ptms = 0;
		for (Ptm_site ite_ptm_site : ptm_sites) {
			int ptm_pos = ite_ptm_site.getPosition();
			if (ptm_pos >= start && ptm_pos <= end) {
				number_of_ptms++;
			}
		}

		double normalized_ptm = number_of_ptms / (double) (end - start + 1)*100;
		ret.add(normalized_ptm);
		
		return ret;
	}
	
}
