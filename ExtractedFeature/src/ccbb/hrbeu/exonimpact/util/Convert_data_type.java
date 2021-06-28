package ccbb.hrbeu.exonimpact.util;

import java.util.ArrayList;

public class Convert_data_type {

	public static ArrayList<Double> convert_string_to_arrayList(String input) {
		String[] input_arr = input.split(",");
		ArrayList<Double> ret = new ArrayList<Double>();

		for (String ite_str : input_arr) {
			ret.add(Double.parseDouble(ite_str));
		}

		return ret;
	}

	
}
