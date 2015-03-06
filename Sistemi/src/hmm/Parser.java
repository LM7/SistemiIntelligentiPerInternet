package hmm;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class Parser {
	
	private void parserForTitle(String title) throws FileNotFoundException, UnsupportedEncodingException {
		String[] splits = title.split(" ");
		PrintWriter out = new PrintWriter("trainHMM.txt", "UTF-8");
		out.println("<s> <s>");
		for (String s: splits) {
			s = s.replaceAll("#", " ");
			out.println(s);
		}
		out.close();
		
	}

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		String title = "CCC#Forza DDD#Roma UUU#Balotelli AAA#Daje";
		Parser parser = new Parser();
		parser.parserForTitle(title);
	}

}
