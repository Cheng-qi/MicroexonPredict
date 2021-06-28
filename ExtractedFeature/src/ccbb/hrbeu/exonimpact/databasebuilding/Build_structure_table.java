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

public class Build_structure_table {

	Logger log = Logger.getLogger(Build_structure_table.class);
	
	private Connection c;
	private Statement stmt;

	private static Build_structure_table instance = null;

	private Build_structure_table() throws ClassNotFoundException, SQLException {
		
	}

	public static Build_structure_table get_instance() throws ClassNotFoundException, SQLException {
		if (instance == null) {
			instance = new Build_structure_table();
		}

		return instance;
	}

	public void init(String database_path) throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + database_path);
		// refseq.db
	}

	public void load_strucutre_into_table(String path_to_spinedx) throws IOException, SQLException {
		List<String> spinedx_files = FileUtils.readLines(new File(path_to_spinedx));
		
		c.setAutoCommit(false);
		stmt = c.createStatement();

		for (String ite_line : spinedx_files) {

			String[] line_arr = ite_line.split("\\t");

			String transcript_id = line_arr[0];
			String aa = line_arr[1];
			String beta_sheet = line_arr[2];
			String random_coil = line_arr[3];
			String alpha_helix = line_arr[4];
			String asa = line_arr[5];
			String disorder = line_arr[6];
			// String beta_sheet=line_arr[1];

			String sql = "INSERT INTO STRUCTURE (TRANSCRIPT_ID,AA,BETA_SHEET,RANDOM_COIL,ALPHA_HELIX,ASA,DISORDER) "
					+ "VALUES (" + "'" + transcript_id + "'," + "'" + aa + "'," + "'" + beta_sheet + "'," + "'"
					+ random_coil + "'," + "'" + alpha_helix + "'," + "'" + asa + "'," + "'" + disorder + "'" + ")";
			
			log.trace(sql);
			try {
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error("The problem transcript is: "+transcript_id);
			}
		}

		stmt.close();
		c.commit();
		c.setAutoCommit(true);

	}

	public void create_table_structure() throws SQLException {

		stmt = c.createStatement();
		String sql = "CREATE TABLE STRUCTURE (TRANSCRIPT_ID CHAR(20) NOT NULL,AA TEXT,"
				+ "BETA_SHEET TEXT,RANDOM_COIL TEXT, ALPHA_HELIX TEXT,ASA TEXT,DISORDER TEXT )";

		stmt.executeUpdate(sql);
		stmt.executeUpdate("CREATE INDEX TRANSCRIPT_ID_INDEX_STRUCTURE ON STRUCTURE (TRANSCRIPT_ID )");

		stmt.close();

	}

	public void drop_table_strucutre() throws SQLException {

		stmt = c.createStatement();
		String sql = "DROP TABLE STRUCTURE";

		stmt.executeUpdate(sql);
		stmt.close();
	}
	
	public static void run(String configure_path,String input_path){
		try {
			Configurations configs = new Configurations();
			
			PropertiesConfiguration config = configs.properties(new File(configure_path));
			
			String database_path = config.getString("database_path");
			
			Build_structure_table.get_instance().init(database_path);
			
			Build_structure_table.get_instance().drop_table_strucutre();
			Build_structure_table.get_instance().create_table_structure();
			Build_structure_table.get_instance()
					.load_strucutre_into_table(input_path);
			
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
	

	public static void main(String[] args) {
		//run(args[0],args[1]);
		run("configuration.txt","/Users/mengli/Documents/splicingSNP/exon_impact_new/db/ens_spine_x_d_combine.tsv");
		
	}
	
}
