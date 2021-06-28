package ccbb.hrbeu.exonimpact.proteinfeaturewrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sqlite.SQLiteConfig;

import ccbb.hrbeu.exonimpact.Feature_calculator;
import ccbb.hrbeu.exonimpact.genestructure.Protein_structure;
import ccbb.hrbeu.exonimpact.util.Convert_data_type;

public class Extractor_structure_feature {

	Logger log = Logger.getLogger(Extractor_structure_feature.class);

	private Connection c;
	private Statement stmt;
	private ResultSet rs;

	private static Extractor_structure_feature instance = null;

	public static Extractor_structure_feature get_instance() {
		if (instance == null) {
			instance = new Extractor_structure_feature();
		}

		return instance;
	}

	public void init(Connection conn) throws SQLException, ClassNotFoundException {
		c=conn;
	}

	public Protein_structure get_structure_features(String transcript_id) throws SQLException {

		log.trace("current search transcript is: " + transcript_id);

		String sql = "SELECT * from STRUCTURE WHERE TRANSCRIPT_ID='" + transcript_id + "'";
		stmt = c.createStatement();
		rs = stmt.executeQuery(sql);

		while (rs.next()) {

			String[] aa = rs.getString("AA").split(",");
			String amino_acid = StringUtils.join(aa, "");

			ArrayList<Double> beta_sheet = Convert_data_type.convert_string_to_arrayList(rs.getString("BETA_SHEET"));
			ArrayList<Double> random_coil = Convert_data_type.convert_string_to_arrayList(rs.getString("RANDOM_COIL"));
			ArrayList<Double> alpha_helix = Convert_data_type.convert_string_to_arrayList(rs.getString("ALPHA_HELIX"));
			ArrayList<Double> asa = Convert_data_type.convert_string_to_arrayList(rs.getString("ASA"));
			ArrayList<Double> disorder = Convert_data_type.convert_string_to_arrayList(rs.getString("DISORDER"));

			return new Protein_structure(transcript_id, amino_acid, beta_sheet, random_coil, alpha_helix, asa,
					disorder);
		}
		
		log.trace("The transcript id don't have have record for protein structure. : " + transcript_id);

		return new Protein_structure();
	}

	public static void main(String[] args) {
		try {
			//Extractor_structure_feature.get_instance().init("/Users/mengli/Documents/splicingSNP/exon_impact_new/db/refseq.db");
			Protein_structure strucutre_feature = Extractor_structure_feature.get_instance()
					.get_structure_features("NM_020420");
			
			System.out.println(strucutre_feature.getAa().substring(82, 100) );
			
			System.out.println(StringUtils.join(strucutre_feature.getRandom_coil().subList(82-1, 99), ","));
			System.out.println(StringUtils.join(strucutre_feature.getAlpha_helix().subList(82-1, 99), ","));
			System.out.println(StringUtils.join(strucutre_feature.getBeta_sheet().subList(82-1, 99), ","));
			for(int i=0;i<strucutre_feature.getAsa().size();++i){
				strucutre_feature.getAsa().set(i, Feature_calculator.asa_norm(strucutre_feature.getAsa().get(i), strucutre_feature.getAa().charAt(i) ) );
			}
				
			System.out.println(StringUtils.join(strucutre_feature.getAsa(), ","));
			System.out.println(StringUtils.join(strucutre_feature.getDisorder().subList(82-1, 99), ","));
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
