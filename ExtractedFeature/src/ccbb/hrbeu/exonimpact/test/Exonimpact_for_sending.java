package ccbb.hrbeu.exonimpact.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.jfree.util.Log;

public class Exonimpact_for_sending {
	static Logger log = Logger.getLogger(Exonimpact_for_sending.class);

	public static void send1(String data) throws IOException {
		// TODO code application logic here
		byte[] buffer = data.getBytes("UTF-8");
		int port = 8787;

		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("localhost");
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		sendData = data.getBytes();

		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
		clientSocket.send(sendPacket); // - See more at:
		// https://systembash.com/a-simple-java-udp-server-and-udp-client/#sthash.lHTgqLnk.dpuf
		
		
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		String modifiedSentence = new String(receivePacket.getData());
		modifiedSentence=modifiedSentence.trim();
		log.trace("the recieve data is: "+modifiedSentence);
		
		if(modifiedSentence.equals("OK")){
			log.info("OK");
		}
		
		
		clientSocket.close();
		// - See more at:
		// https://systembash.com/a-simple-java-udp-server-and-udp-client/#sthash.omsqKuLy.dpuf

		System.out.println(InetAddress.getLocalHost().getHostAddress() + ":" + sendPacket.getPort());

	}

	public static void main(String[] args) {
		try {
			send1("/Users/mengli/Documents/splicingSNP_new/data/build_db/test_for_correctness/test_1.txt");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
