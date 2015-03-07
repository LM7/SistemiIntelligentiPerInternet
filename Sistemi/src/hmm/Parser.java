package hmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class Parser {
	
	public static void parserForTitle(String title) throws FileNotFoundException, UnsupportedEncodingException {
		String[] splits = title.split(" ");
		PrintWriter out = new PrintWriter("data/trainHMM.txt", "UTF-8");
		out.println("<s> <s>");
		for (String s: splits) {
			s = s.replaceAll("#", " ");
			out.println(s);
		}
		out.close();
		
	}
	
	private void parserForFileTitle(File titoli) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader("contenutoDB.txt")); //titoli appena ottenuti
		String line = reader.readLine();
		PrintWriter outForHMM = new PrintWriter("trainForHMM.txt", "UTF-8"); //file che servira' per HMM
		while (line!=null) {
			outForHMM.println("<s> <s>");
			String[] splits = line.split(" ");
			for (String s: splits) {
				s = s.replaceAll("#", " ");
				outForHMM.println(s);
			}
		    line = reader.readLine();
		}
		outForHMM.close();
	}

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		String title = "CCC#Forza DDD#Roma BBB#Balotelli AAA#Daje";
		Parser.parserForTitle(title);
	}

}
