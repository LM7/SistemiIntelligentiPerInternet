package hmm;

import java.io.FileNotFoundException;
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

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		String title = "CCC#Forza DDD#Roma BBB#Balotelli AAA#Daje";
		Parser.parserForTitle(title);
	}

}
