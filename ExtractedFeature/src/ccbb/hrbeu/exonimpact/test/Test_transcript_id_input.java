package ccbb.hrbeu.exonimpact.test;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.configuration2.ex.ConfigurationException;

import ccbb.hrbeu.exonimpact.ExonImpact;

public class Test_transcript_id_input {
	
	public static void test_one() {
		try {
			
			ExonImpact exon_impact = ExonImpact.get_instance("configuration.txt");
			exon_impact.read_from_file("/Users/mengli/Documents/splicingSNP_new/data/build_db/test_for_correctness/test3.txt");
			exon_impact.batch_run();
			exon_impact.output_to("/Users/mengli/Documents/splicingSNP_new/data/build_db/test_for_correctness/test_for_transcript_id_output.csv");
			//exon_impact.build_xml("E:\\limeng\\splicingSNP\\exon_impact_new\\test_1");
			
		} catch (ClassNotFoundException | ConfigurationException | SQLException | IOException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args){
		test_one();
	}
	
}
