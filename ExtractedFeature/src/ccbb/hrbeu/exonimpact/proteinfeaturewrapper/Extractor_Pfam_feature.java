package ccbb.hrbeu.exonimpact.proteinfeaturewrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.sqlite.SQLiteConfig;

import ccbb.hrbeu.exonimpact.genestructure.Pfam_domain;

public class Extractor_Pfam_feature {

	private Connection c;
	private Statement stmt;

	private ResultSet rs;
	private static Extractor_Pfam_feature instance = null;

	public static Extractor_Pfam_feature get_instance() throws ClassNotFoundException, SQLException {
		if (instance == null) {
			instance = new Extractor_Pfam_feature();
		}

		return instance;
	}

	public Extractor_Pfam_feature() throws ClassNotFoundException, SQLException {

	}

	public void init(Connection conn) throws SQLException, ClassNotFoundException {
		c=conn;
	}

	public ArrayList<Pfam_domain> get_pfam_domains(String transcript_id) throws SQLException {
		ArrayList<Pfam_domain> pfam_domains = new ArrayList<Pfam_domain>();

		String sql = "SELECT * from PFAM WHERE TRANSCRIPT_ID='" + transcript_id + "'";
		stmt = c.createStatement();
		rs = stmt.executeQuery(sql);

		while (rs.next()) {
			int start = rs.getInt("START");
			int end = rs.getInt("END");
			String family = rs.getString("FAMILY");
			String name = rs.getString("NAME");
			String clan = rs.getString("CLAN");

			pfam_domains.add(new Pfam_domain(transcript_id, start, end, family, name, clan));

		}

		return pfam_domains;
	}
	
	public static void main(String[] args) {
		try {
			
			//Extractor_Pfam_feature.get_instance().init("E:\\limeng\\splicingSNP\\exon_impact_new\\db\\refseq.db");
			ArrayList<Pfam_domain> pfam_domains = Extractor_Pfam_feature.get_instance().get_pfam_domains("NM_020420");

			for (Pfam_domain ite_domain : pfam_domains) {
				System.out.println(ite_domain.getName()+" "+ite_domain.getStart()+" "+ite_domain.getEnd()+" ");

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
