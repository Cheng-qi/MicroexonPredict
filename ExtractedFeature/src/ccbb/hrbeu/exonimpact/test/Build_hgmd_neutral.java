package ccbb.hrbeu.exonimpact.test;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.configuration2.ex.ConfigurationException;

import ccbb.hrbeu.exonimpact.ExonImpact;

public class Build_hgmd_neutral {
	
	public static void extract_hgmd_features() throws IOException, ClassNotFoundException, SQLException, InterruptedException, ConfigurationException{
		
		ExonImpact exon_impact = ExonImpact.get_instance("configuration.txt");
		exon_impact.read_from_file("/Users/mengli/Documents/splicingSNP_new/data/hgmd_raw_data/hgmd_transcript_exon_id");
		exon_impact.batch_run();
		exon_impact.output_to("/Users/mengli/Documents/splicingSNP_new/data/features/hgmd_features.csv");
		
	}
	
	public static void extract_neutral_features() throws ClassNotFoundException, ConfigurationException, SQLException, IOException, InterruptedException{
		
		ExonImpact exon_impact = ExonImpact.get_instance("configuration.txt");
		exon_impact.read_from_file("/Users/mengli/Documents/splicingSNP_new/data/1000_genome/1000_genome_transcript_exon_id");
		exon_impact.batch_run();
		exon_impact.output_to("/Users/mengli/Documents/splicingSNP_new/data/features/neutral_indel_features.csv");
		
	}
	
	public static void main(String[] args){
		try {
			
			extract_hgmd_features();
			
			extract_neutral_features();
		} catch (ClassNotFoundException | ConfigurationException | SQLException | IOException
				| InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
