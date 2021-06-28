package ccbb.hrbeu.exonimpact.sequencefeaturewrapper;

import java.util.regex.Pattern;

import ccbb.hrbeu.exonimpact.genestructure.Exon;
import ccbb.hrbeu.exonimpact.genestructure.Transcript;

public class Bed_decoder {

	Transcript trans=new Transcript();
	
	private static Bed_decoder instance=null;
	
	public static Bed_decoder get_instance(){
		if(instance==null){
			instance=new Bed_decoder();
			instance.trans=new Transcript();
			
		}
		return instance;
		
	}
	
	static String bed_pattern="chr\\w+\\t(\\d+)\\t(\\d+)";
	
	static String exon_region_pattern="chr\\w+:(\\d+):(\\d+)";
	
	public static boolean is_bed(String input){
		boolean is_bed = Pattern.matches(bed_pattern, input);
		
		return is_bed;
	}
	
	public static boolean is_exon_region(String input){
		boolean is_exon_region = Pattern.matches(exon_region_pattern, input);
		
		return is_exon_region;
	}
	
	public Transcript get_transcript(String input) {
		String[] line_arr=input.split("\\t");
		
		String chr=line_arr[0];
		int beg=Integer.parseInt(line_arr[1])+1;
		int end=Integer.parseInt(line_arr[2]);
		
		trans.addExon(new Exon(chr,beg,end) );
		trans.setTarget_start(beg);
		trans.setTarget_end(end);
		trans.setChr(chr);
		
		trans.setTx_start(beg);
		trans.setTx_end(end);
		
		return trans;
	}
	
}
