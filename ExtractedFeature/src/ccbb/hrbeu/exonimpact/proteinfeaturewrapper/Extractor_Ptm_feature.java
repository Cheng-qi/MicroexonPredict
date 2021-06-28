package ccbb.hrbeu.exonimpact.proteinfeaturewrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.sqlite.SQLiteConfig;

import ccbb.hrbeu.exonimpact.genestructure.Ptm_site;

public class Extractor_Ptm_feature {

	private Connection c;
	private Statement stmt;
	private ResultSet rs;

	private static Extractor_Ptm_feature instance = null;

	public static Extractor_Ptm_feature get_instance() {
		if (instance == null) {
			instance = new Extractor_Ptm_feature();
		}

		return instance;
	}

	public void init(Connection conn) throws SQLException, ClassNotFoundException {
		c=conn;
	}

	public ArrayList<Ptm_site> get_ptm_sites(String transcript_id) throws SQLException {
		ArrayList<Ptm_site> ptm_sites = new ArrayList<Ptm_site>();

		String sql = "SELECT * from PTM WHERE TRANSCRIPT_ID='" + transcript_id + "'";
		stmt = c.createStatement();
		rs = stmt.executeQuery(sql);

		while (rs.next()) {
			String uniprot_id = rs.getString("UNIPROT_ID");
			int position = rs.getInt("POSITION");
			String modification = rs.getString("MODIFICATION");
			ptm_sites.add(new Ptm_site(transcript_id, uniprot_id, position, modification));

		}

		return ptm_sites;
	}

	public static void main(String[] args) {
		try {
			//Extractor_Ptm_feature.get_instance().init("E:\\limeng\\splicingSNP\\exon_impact_new\\db\\refseq.db");
			ArrayList<Ptm_site> ptm_sites = Extractor_Ptm_feature.get_instance().get_ptm_sites("NM_020420");

			for (Ptm_site ite_site : ptm_sites) {
				System.out.println(ite_site.getPosition());
				
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
