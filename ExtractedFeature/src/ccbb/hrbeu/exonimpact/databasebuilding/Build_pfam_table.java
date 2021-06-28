package ccbb.hrbeu.exonimpact.databasebuilding;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class Build_pfam_table {

	Logger log = Logger.getLogger(Build_pfam_table.class);

	private Connection c;
	private Statement stmt;

	private static Build_pfam_table instance = null;

	public Build_pfam_table() {

	}

	public static Build_pfam_table get_instance() throws ClassNotFoundException, SQLException {
		if (instance == null) {
			instance = new Build_pfam_table();
		}

		return instance;
	}

	public void init(String database_path) throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + database_path);
		// refseq.db
	}

	public void load_pfam_into_table(String path) throws SQLException, IOException {
		List<String> pfam_lines = FileUtils.readLines(new File(path));

		c.setAutoCommit(false);
		stmt = c.createStatement();

		for (String ite_line : pfam_lines) {
			String[] line_arr = ite_line.split("\\s+");

			String sql = "INSERT INTO PFAM (TRANSCRIPT_ID,START,END,FAMILY,NAME,CLAN) " + "VALUES (" + "'" + line_arr[0]
					+ "', " + line_arr[1] + ", " + line_arr[2] + ", " + "'" + line_arr[5] + "', " + "'" + line_arr[6]
					+ "', " + "'" + line_arr[14] + "' " + ")";

			log.trace(sql);
			stmt.executeUpdate(sql);

		}

		stmt.close();
		c.commit();
		c.setAutoCommit(true);

	}

	public void create_table_pfam() throws SQLException {

		stmt = c.createStatement();
		String sql = "CREATE TABLE PFAM " + "(TRANSCRIPT_ID CHAR(20) NOT NULL," + " START INT NOT NULL,"
				+ "END INT NOT NULL," + " FAMILY CHAR(20)," + "NAME CHAR(20)," + "CLAN CHAR(20)" + " )";

		stmt.executeUpdate(sql);
		stmt.executeUpdate("CREATE INDEX TRANSCRIPT_ID_INDEX_PFAM ON PFAM (TRANSCRIPT_ID)");

		stmt.close();

	}

	public void drop_table_pfam() throws SQLException {

		stmt = c.createStatement();
		String sql = "DROP TABLE PFAM";

		stmt.executeUpdate(sql);
		stmt.close();
	}

	public static void main(String[] args) {
		try {
			Configurations configs = new Configurations();
			
			PropertiesConfiguration config = configs.properties(new File("configuration.txt"));
			
			String database_path = config.getString("database_path");
			Build_pfam_table.get_instance().init(database_path);

			//Build_pfam_table.get_instance().drop_table_pfam();
			Build_pfam_table.get_instance().create_table_pfam();
			Build_pfam_table.get_instance()
					.load_pfam_into_table("/Users/mengli/Documents/splicingSNP/exon_impact_new/db/ens_hg19.protein.pfam.data");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
