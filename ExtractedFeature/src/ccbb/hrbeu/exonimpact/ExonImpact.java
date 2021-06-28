package ccbb.hrbeu.exonimpact;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import ccbb.hrbeu.exonimpact.genestructure.Bed_region_map;
import ccbb.hrbeu.exonimpact.proteinfeaturewrapper.Extractor_Pfam_feature;
import ccbb.hrbeu.exonimpact.proteinfeaturewrapper.Extractor_Ptm_feature;
import ccbb.hrbeu.exonimpact.proteinfeaturewrapper.Extractor_structure_feature;
import ccbb.hrbeu.exonimpact.sequencefeaturewrapper.Bed_region_extractor;
import ccbb.hrbeu.exonimpact.sequencefeaturewrapper.Extractor;
import ccbb.hrbeu.exonimpact.sequencefeaturewrapper.Extractor_phylop;
import ccbb.hrbeu.exonimpact.sequencefeaturewrapper.Extractor_sequence;

public class ExonImpact {

	Logger log = Logger.getLogger(ExonImpact.class);

	private static ExonImpact instance = null;
	
	LinkedList<StringBuilder> output_str=new LinkedList<StringBuilder>();
	
	Connection conn=null;
	
	public static ExonImpact get_instance(String init_path)
			throws ConfigurationException, ClassNotFoundException, SQLException, IOException {
		if (instance == null) {
			instance = new ExonImpact(init_path);
		}

		return instance;
	}

	public ExonImpact(String init_path) throws ConfigurationException, ClassNotFoundException, SQLException, IOException {
		init(init_path);
	}

	Configurations configs = new Configurations();
	Configuration config = null;

	ArrayList<Exon_feature> exon_features = new ArrayList<Exon_feature>();
	ArrayList<Extractor> Feature_extractors = new ArrayList<Extractor>();
	List<String> input_str_arr = new ArrayList<String>();

	public void init(String path_to_config) throws ConfigurationException, ClassNotFoundException, SQLException, IOException {
		//config = configs.properties(new File("configuration.txt"));
		config = configs.properties(new File(path_to_config));

		Extractor_sequence.get_instance().init(config.getString("fasta_path") );
		Bed_region_extractor.get_instance().build_index(config.getString("bed_path") );
		Bed_region_map.get_instance().init(config.getString("bed_path") );
		
		Extractor_phylop.get_instance().build_phylop_table(config.getString("phylop_path"));

		//init database
		String database_path = config.getString("database_path");
		
		Class.forName("org.sqlite.JDBC");
				
		conn = DriverManager.getConnection("jdbc:sqlite:"+database_path);
		
		//!TODO The below three lines will lead to error in watson
		Statement stat = conn.createStatement();
		log.info("the SQL statement is: restore from '"+database_path+"'");
		//stat.executeUpdate("restore from "+database_path);

		Extractor_Pfam_feature.get_instance().init(conn);
		Extractor_Ptm_feature.get_instance().init(conn);
		Extractor_structure_feature.get_instance().init(conn);
		
		
		log.trace("init database!");
		
	}
	
	public void batch_run() throws ClassNotFoundException, SQLException, IOException, InterruptedException {
		int  input_count=0;
		exon_features.clear();
		for (String iter_input : input_str_arr) {
				
			log.trace("process input: " + iter_input+" number_index is: "+input_count++);
			Exon_feature t_feature;
			t_feature = new Exon_feature(iter_input);
			
			exon_features.add(t_feature);

		}
		
	}
	
	public void batch_run(String output_file_name) throws ClassNotFoundException, SQLException, IOException, InterruptedException {
		StringBuilder line=new StringBuilder();
		
		for(int i=0;i<Exon_transcript_feature.feature_names2.size();++i){
			//FileUtils.writeStringToFile(new File(file_name), Exon_transcript_feature.feature_names2.get(i),true) ;
			line.append(Exon_transcript_feature.feature_names2.get(i) );
			
			if(i!=Exon_transcript_feature.feature_names2.size()-1)
				//FileUtils.writeStringToFile(new File(file_name), ",", true);
				line.append(",");
		}
		
		line.append("\n");
		
		FileUtils.write(new File(output_file_name), line.toString(), false);
		
		int  input_count=0;
		Exon_feature t_feature;

		for (String iter_input : input_str_arr) {
			LinkedList<StringBuilder> one_trans_output_str = new LinkedList<StringBuilder>();
			
			log.trace("process input: " + iter_input+" number_index is: "+input_count++);
			t_feature = new Exon_feature(iter_input);
			t_feature.output(one_trans_output_str);
			
			FileUtils.writeLines(new File(output_file_name), one_trans_output_str, true);
			
			//exon_features.add(t_feature);
		}
		
	}
	
	public void run_one(String input_event) throws ClassNotFoundException, SQLException, IOException, InterruptedException{
		
		Exon_feature t_feature;
		t_feature = new Exon_feature(input_event);
		exon_features.add(t_feature);
		
	}
	
	public void call_R(String file_name) throws IOException, InterruptedException{
		String command="Rscript.exe src\\R\\predict.r "+file_name;
		log.trace("R command is: "+command);
		Process p = new ProcessBuilder(
				"C:\\Program Files\\R\\R-3.2.1\\bin\\Rscript.exe",file_name).start();
		p.waitFor();
		
		//TODO!!!!!!!!!! need some smart person to modify this ugly code.
		int times=0;
		while (p.getInputStream().available() <= 0) {
			Thread.sleep(1000 * 1);
			if(times++>5) break;
		}
		
		BufferedInputStream error=new BufferedInputStream(p.getErrorStream());
		BufferedReader errorBr = new BufferedReader(new InputStreamReader(error));
		String lineStr;
		while((lineStr=errorBr.readLine())!=null){
			//System.out.println(lineStr);
			log.error(lineStr);
		}
		
		
	}

	public void build_xml(String file_name) throws ParserConfigurationException{
		
		for(Exon_feature ite_exon_fea:exon_features){
			ite_exon_fea.build_xml(file_name);
		}
		
	}
	
	public void build_xml(String file_name,String transcript_id) throws ParserConfigurationException{
		log.trace("xml file name is: "+file_name);
		
		for(Exon_feature ite_exon_fea:exon_features){
			ite_exon_fea.build_xml(file_name,transcript_id);
		}
		
	}
	
	public void read_from_file(String input_file_name) throws IOException {
		log.info("the input file path is: "+input_file_name);
		input_str_arr = FileUtils.readLines(new File(input_file_name));
	}

	public void output_to(String file_name) throws IOException {
		// empty the file
		//FileUtils.writeStringToFile(new File(file_name), "", false);
		StringBuilder line=new StringBuilder();
		
		for(int i=0;i<Exon_transcript_feature.feature_names2.size();++i){
			//FileUtils.writeStringToFile(new File(file_name), Exon_transcript_feature.feature_names2.get(i),true) ;
			line.append(Exon_transcript_feature.feature_names2.get(i) );
			
			if(i!=Exon_transcript_feature.feature_names2.size()-1)
				//FileUtils.writeStringToFile(new File(file_name), ",", true);
				line.append(",");
			
		}
		
		//FileUtils.writeStringToFile(new File(file_name), "\n", true);
		output_str.add(line);
		
		for (Exon_feature ite_exon_feature : exon_features) {
			ite_exon_feature.output(output_str);
		}
		FileUtils.writeLines(new File(file_name), output_str);
	}

	public ArrayList<Exon_feature> getExon_features() {
		return exon_features;
	}

	public void setExon_features(ArrayList<Exon_feature> exon_features) {
		this.exon_features = exon_features;
	}

	public ArrayList<Extractor> getFeature_extractors() {
		return Feature_extractors;
	}

	public void setFeature_extractors(ArrayList<Extractor> feature_extractors) {
		Feature_extractors = feature_extractors;
	}

}
