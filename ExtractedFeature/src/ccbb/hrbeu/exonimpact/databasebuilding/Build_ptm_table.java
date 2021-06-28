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

public class Build_ptm_table {

	Logger log = Logger.getLogger(Build_ptm_table.class);

	private Connection c;
	private Statement stmt;

	private static Build_ptm_table instance = null;

	private Build_ptm_table() throws ClassNotFoundException, SQLException {
	}

	public static Build_ptm_table get_instance() throws ClassNotFoundException, SQLException {
		if (instance == null) {
			instance = new Build_ptm_table();
		}

		return instance;
	}

	public void init(String database_path) throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + database_path);
		// refseq.db
	}

	public void load_ptm_into_table(String path) throws SQLException, IOException {
		List<String> ptm_lines = FileUtils.readLines(new File(path));

		c.setAutoCommit(false);
		stmt = c.createStatement();

		for (String ite_line : ptm_lines) {
			String[] line_arr = ite_line.split("\\t");

			String sql = "INSERT INTO PTM (TRANSCRIPT_ID,UNIPROT_ID,POSITION,MODIFICATION) " + "VALUES (" + "'"
					+ line_arr[0] + "', " + "'" + line_arr[1] + "', " + line_arr[2] + " ,'" + line_arr[3] + "'" + ")";
			// log.trace(sql);
			stmt.executeUpdate(sql);
		}

		stmt.close();
		c.commit();
		c.setAutoCommit(true);

	}

	public void create_table_ptm() throws SQLException {

		stmt = c.createStatement();
		String sql = "CREATE TABLE PTM " + "(TRANSCRIPT_ID CHAR(20)  NOT NULL," + " UNIPROT_ID CHAR(20) NOT NULL, "
				+ " POSITION INT NOT NULL, " + " MODIFICATION CHAR(20) )";

		stmt.executeUpdate(sql);

		stmt.executeUpdate("CREATE INDEX TRANSCRIPT_ID_INDEX_PTM ON PTM (TRANSCRIPT_ID)");

		stmt.close();

	}

	public void drop_table_ptm() throws SQLException {

		stmt = c.createStatement();
		String sql = "DROP TABLE PTM";

		stmt.executeUpdate(sql);

		stmt.close();

	}

	public static void main(String[] args) {
		String path_ptm_for_sqllite="/Users/mengli/Documents/splicingSNP_new/data/build_db/ens_ptm_for_sqllite.tsv";
		build(args[0]);
		
		//build(path_ptm_for_sqllite);
	}

	public static void build(String path_ptm_for_sqllite){
		try {
			Configurations configs = new Configurations();
			
			PropertiesConfiguration config = configs.properties(new File("configuration.txt"));
			
			String database_path = config.getString("database_path");
			Build_ptm_table.get_instance().init(database_path);

			Build_ptm_table.get_instance().drop_table_ptm();
			Build_ptm_table.get_instance().create_table_ptm();
			Build_ptm_table.get_instance()
					.load_ptm_into_table(path_ptm_for_sqllite);
			
			//57875
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
