package ccbb.hrbeu.exonimpact.test;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.configuration2.ex.ConfigurationException;

import ccbb.hrbeu.exonimpact.ExonImpact;

public class Clinvar_intron_synonymous_dpsi20 {
	public static void main(String[] args) {
		try {
			ExonImpact exon_impact = ExonImpact.get_instance("configuration.txt");
			exon_impact.read_from_file("/Users/mengli/Documents/splicingSNP_new/data/clinvar/clinvar_benign_snp_data_vcf_dpsi_join_dpsi20_exon_id");
			exon_impact.batch_run();
			//exon_impact.output_to("/Users/mengli/Documents/splicingSNP_new/data/clinvar/clinvar_benign_snp_data_vcf_dpsi_join_dpsi20_exon_id_output.txt");
			//exon_impact.build_xml("asa_ss_demo","ENST00000315285");
			exon_impact.output_to("/Users/mengli/Documents/splicingSNP_new/data/clinvar/clinvar_benign_snp_data_vcf_dpsi_join_dpsi20_exon_id_output.csv");
			//exon_impact.output_to("clinvar_benign_snp_data_vcf_dpsi_join_dpsi20_exon_id_output.txt");
			
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
		} 

	}
}
