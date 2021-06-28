package ccbb.hrbeu.exonimpact.genestructure;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import ccbb.hrbeu.exonimpact.util.*;

public class Bed_region_map {
	
	private static Bed_region_map instance=null;
	
	
	public static Bed_region_map get_instance(){
		if(instance==null){
			instance=new Bed_region_map();
		}
		
		return instance;
	}
	
	public HashMap<String,Tris<String, Integer,Integer> > name_region_map=new HashMap<String,Tris<String, Integer,Integer> >();
	
	public void init(String bed_path) throws IOException{
		List<String> lines=FileUtils.readLines(new File(bed_path) );
		
		for(String ite_line:lines){
			String[] line_arr=ite_line.split("\\t");
			String name=line_arr[3];
			String chr=line_arr[0];
			int beg=Integer.parseInt(line_arr[1]);
			int end=Integer.parseInt(line_arr[2]);
			
			name_region_map.put(name, new Tris<String,Integer,Integer>(chr,beg,end) );
		}
		
	}
	
	public static boolean is_transcript_id(String input){
		boolean is_transcript_id=Pattern.matches("(NM|ENST).*", input);
		
		return is_transcript_id;	
	}
	
	
}
