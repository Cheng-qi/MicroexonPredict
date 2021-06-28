package ccbb.hrbeu.exonimpact.test;

import java.io.IOException;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;

import ccbb.hrbeu.exonimpact.ExonImpact;

public class Gen_xml {
	
	static Logger log=Logger.getLogger(Gen_xml.class);
	
	public static void test(String event,String transcript_id,String file_name) {
		try {
			log.trace("event is: "+event+"\t"+" transcript_id is: "+transcript_id);
			
			ExonImpact exon_impact = ExonImpact.get_instance("configuration.txt");
			exon_impact.run_one(event);
			exon_impact.build_xml(file_name,transcript_id);
			//exon_impact.build_xml("E:\\limeng\\splicingSNP\\exon_impact_new\\test_1");
			
		} catch (ClassNotFoundException | ConfigurationException | SQLException | IOException | ParserConfigurationException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		Gen_xml.test(args[0],args[1],args[2]);
		
	}
}
