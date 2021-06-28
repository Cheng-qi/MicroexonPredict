package ccbb.hrbeu.exonimpact.test;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;

import ccbb.hrbeu.exonimpact.ExonImpact;

public class Exonimpact_for_server {

	static Logger log = Logger.getLogger(Exonimpact_for_server.class);

	private int port = 8889;
	ExonImpact exon_impact;

	private static Exonimpact_for_server instance = null;

	public static Exonimpact_for_server get_instance() {
		if (instance == null) {
			instance = new Exonimpact_for_server();
		}

		return instance;
	}

	public void init(String path_to_config)
			throws ClassNotFoundException, ConfigurationException, SQLException, IOException {
		exon_impact = ExonImpact.get_instance(path_to_config);
		
		Configurations configs = new Configurations();
		Configuration config = null;
		config = configs.properties(new File(path_to_config));

		port=config.getInt("port");
		serverSocket = new DatagramSocket(port);
	}
	
	DatagramSocket serverSocket = null;

	public void listen() throws SocketException   {

		//System.out.printf("Listening on udp:%s:%d%n", InetAddress.getLocalHost().getHostAddress(), port);
		log.info("Listening on udp:");
		
		while (true) {
			byte[] sendData = new byte[1024];

			byte[] receiveData = new byte[1024];

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				serverSocket.receive(receivePacket);

			String sentence = new String(receivePacket.getData()); //
			// the sentence should be path_to_input_file
			log.info("the recieve message is:" + sentence);
			if(!sentence.startsWith("chr")){
				build_predict(sentence);
				//After finish the program, return a success message.
				//System.out.println("RECEIVED: " + sentence);
	            InetAddress IPAddress = receivePacket.getAddress();
	            
	            int r_port = receivePacket.getPort();
	            String capitalizedSentence = "OK";
	            sendData = capitalizedSentence.getBytes();
	            
	            DatagramPacket sendPacket =new DatagramPacket(sendData, sendData.length, IPAddress, r_port);
	            log.trace("send packet to: "+IPAddress.getHostAddress()+":"+(r_port));

	            serverSocket.send(sendPacket);
	            
			}else{
				build_xml(sentence);
				//After finish the program, return a success message.
				//System.out.println("RECEIVED: " + sentence);
	            InetAddress IPAddress = receivePacket.getAddress();
	            
	            int r_port = receivePacket.getPort();
	            String capitalizedSentence = "XML-OK";
	            sendData = capitalizedSentence.getBytes();
	            log.trace("send packet to: "+IPAddress.getHostAddress()+":"+(r_port) );
	            DatagramPacket sendPacket =new DatagramPacket(sendData, sendData.length, IPAddress,r_port	);
	            
	            serverSocket.send(sendPacket);
			}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error(e.getMessage());
			}
			catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error(e.getMessage());

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error(e.getMessage());

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error(e.getMessage());

			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error(e.getMessage());
				
			// TODO never stop the server, change the log4j configuration file, append log error.
			} catch(Exception e){
				e.printStackTrace();
				log.error(e.getMessage());
			}

		}
	}

	private void build_xml(String sentence) throws ClassNotFoundException, SQLException, IOException, InterruptedException, ParserConfigurationException {
		// TODO Auto-generated method stub
		sentence=sentence.trim();
		String[] recieve_str=sentence.split("\\$");
		
		exon_impact.run_one(recieve_str[0]);
		exon_impact.build_xml("./usr_xml/"+recieve_str[2],recieve_str[1]);
		//exon_impact.build_xml("E:\\limeng\\splicingSNP\\exon_impact_new\\test_1");
		
	}

	private void build_predict(String sentence) throws IOException, ClassNotFoundException, SQLException, InterruptedException {
		// TODO Auto-generated method stub
		System.out.println("the recieve message is:" + sentence);
		sentence=sentence.trim();
		exon_impact.read_from_file(sentence);
		// exon_impact.batch_run();

		String output_name = new File(sentence).getName();
		exon_impact.batch_run("usr_input/" + output_name + "_features.csv");

	
	}

	public static void main(String[] args) {
		try {
			// Exonimpact_for_server.get_instance().init(args[0]);
			Exonimpact_for_server.get_instance().init("configuration.txt");
			Exonimpact_for_server.get_instance().listen();

		} catch (ClassNotFoundException | ConfigurationException | SQLException | IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}

		finally{
			Exonimpact_for_server.get_instance().serverSocket.close();
		}
	}

}
