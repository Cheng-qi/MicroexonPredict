package ccbb.hrbeu.exonimpact.test;

import java.io.IOException;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration2.ex.ConfigurationException;

import ccbb.hrbeu.exonimpact.ExonImpact;

public class Asa_ss {

	//clinvar_intron_synonymous_spanr
	
	public static void main(String[] args) {
		try {
			ExonImpact exon_impact = ExonImpact.get_instance("configuration.txt");
			exon_impact.read_from_file("/Users/mengli/Documents/splicingSNP_new/data/asa_random_coil/asa_random_coil.txt");
			exon_impact.batch_run();
			exon_impact.output_to("/Users/mengli/Documents/splicingSNP_new/data/asa_random_coil/asa_random_coil_output.txt");
			exon_impact.build_xml("asa_ss_demo","ENST00000315285");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// exon_impact.build_xml("E:\\limeng\\splicingSNP\\exon_impact_new\\test_1");
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
