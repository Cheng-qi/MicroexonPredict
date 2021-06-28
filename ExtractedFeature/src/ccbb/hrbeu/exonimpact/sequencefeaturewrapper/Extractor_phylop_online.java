package ccbb.hrbeu.exonimpact.sequencefeaturewrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Extractor_phylop_online {
	
	public static String getResultOnce(String qstr) throws IOException, InterruptedException {
		String urlStr = qstr;
		String result = "";

		URL url;

		url = new URL(urlStr);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(1000000);
		// conn.
		int i = 0;
		while (conn.getResponseCode() != 200) {

			Thread.sleep(1000);

			conn.disconnect();
			conn = (HttpURLConnection) url.openConnection();

			if (i > 5)
				break;
			i++;
		}

		if (conn.getResponseCode() != 200) {
			throw new IOException(conn.getResponseMessage());
		}
		
		// Buffer the result into a string
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line + "");
		}
		rd.close();

		conn.disconnect();
		result = sb.toString();

		return result;
	}

	public static ArrayList<Double> extract(String input) throws IOException, InterruptedException{
		String serverString = "http://watson.compbio.iupui.edu/ExonImpact2/get_phylop.php?";
		
		String qstr = serverString + "input="+input;
		
		String re = getResultOnce(qstr);
		//System.out.println(re);
		
		String [] re_arr=re.split(",");
		
		ArrayList<Double> ret=new ArrayList<Double>();
		
		for(int i=0;i<re_arr.length;i++){
			ret.add(Double.parseDouble(re_arr[i]) );
		}
		
		return ret;
	}
	
	public static ArrayList<Double> extract(String chr,int beg,int end) throws IOException, InterruptedException{
		if(end-beg>100000){
			return new ArrayList<Double>();
		}
		
		return extract(chr+":"+beg+"-"+end);
		
	}
	
	public static void main(String[] args){
		 ArrayList<Double> re;
		try {
			
			re = extract("chrY:26951604:26951655");
			
			System.out.println(re);
			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String query_str=""
		
		
	}
	
}
