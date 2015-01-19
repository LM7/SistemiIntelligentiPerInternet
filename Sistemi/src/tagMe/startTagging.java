package tagMe;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class startTagging {
	
	public static void main(String[] args) throws IOException {

		URL url= new URL("http://tagme.di.unipi.it/tag");
		HttpURLConnection con=(HttpURLConnection) url.openConnection(); 

		//add request header
		con.setRequestMethod("POST");
		//con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String text = "Grammy nominated singer and songwriter Ed Sheeran will be performing at The O2 on 12, 13, 14 and 15 October 2014. The 22-year olds debut + entered the UK Albums Chart at number one with the highest opening week figures for a debut male solo artist, and has now gone six times platinum. Ed Sheeran is also a two-time BRIT Award winner (Best Male Artist and Best Breakthrough Artist) and recipient of an Ivor Novello, helping him become a genuine global phenomenon. Sheeran said:  \"I can't wait to tour this new record, it's been a long time coming. I am looking forward to touring the UK once again.";
		String urlParameters = "key=41480047b3428dcfe6a5c1bba1f0a93e&text="+text+"&include_categories=true";

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("--------------------------------------------------------");
		System.out.println("Sending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);
		System.out.println("--------------------------------------------------------");

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		System.out.println("--------------------------------------------------------");
		
		// PARSING
		Parser p = new Parser(response.toString());
		p.processingReply();


	}
}
