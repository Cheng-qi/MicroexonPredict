package ccbb.hrbeu.exonimpact.test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;

import ccbb.hrbeu.exonimpact.ExonImpact;

public class Run_sMem {
	static Logger log=Logger.getLogger(Run.class);
	
	public static void test(String input,String path_to_config) {
		ExonImpact exon_impact=null;
		String output_name="";
		
		try {
			log.trace("Input path: "+input);
			log.trace("configuration path: "+path_to_config);
			
			exon_impact = ExonImpact.get_instance(path_to_config);

			
			exon_impact.read_from_file(input);
			//exon_impact.batch_run();
			
			output_name=new File(input).getName();
			exon_impact.batch_run("usr_input/"+output_name+"_features.csv");
			
			
		} catch (ClassNotFoundException | ConfigurationException | SQLException | IOException   e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
			log.error(e.getMessage());
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		}finally{
			
		}
		
	}
	
	public static void main(String[] args) {
		Run_sMem.test(args[0],args[1]);
		//Run_sMem.test("/Users/mengli/Documents/splicingSNP_new/data/build_db/miso_test/se", "configuration.txt");
		
		//Run.test("E:\\limeng\\splicingSNP\\exon_impact_new\\test_1.txt");
	}
}
