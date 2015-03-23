package Final_Version.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class Parser {
	
	public static void parserForTitle(String title) throws FileNotFoundException, UnsupportedEncodingException {
		String[] splits = title.split(" ");
		PrintWriter out = new PrintWriter ("data/trainHMM.txt", "UTF-8");
		out.println("<s> <s>");
		for (String s: splits) {
			s = s.replaceAll("#", " ");
			out.println(s);
		}
		out.close();
		
	}
	
	public void parserForFileTitle(File titoli) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(titoli)); //titoli appena ottenuti
		String line = reader.readLine();
		PrintWriter outForHMM = new PrintWriter("trainForHMM.pos", "UTF-8"); //file che servira' per HMM
		while (line!=null) {
			outForHMM.println("<s> <s>");
			String[] splits = line.split(" ");
			for (String s: splits) {
				s = s.replaceAll("#", " ");
				outForHMM.println(s);

			}
			outForHMM.println(". .");
			line = reader.readLine();
		}
		reader.close();
		outForHMM.close();
	}
	
	public static void parserForBrownTitle(String title) throws FileNotFoundException {
		String[] parole = title.split(" ");
		PrintWriter out = new PrintWriter(new FileOutputStream(new File("FinalDataBROWN/training.brown"),true));
		for (String parola: parole) {
			String tag[] = parola.split("#");
			out.print(tag[1]+"/"+tag[0]+" ");
		}
		out.println();
		out.close();
	}
	
	public static void parserForBrownFileTitle(File titoli) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(titoli));
		String line = reader.readLine();
		PrintWriter outBrown = new PrintWriter("dataBROWN/trainingSepa.brown", "UTF-8");
		int lengSplits, canc,i;
		String tag, parola, parolaFin;
		while (line!=null) {
			i = 0;
			String[] splits = line.split(" ");
			lengSplits = splits.length;
			for (String s:splits) {
				i = i+1;
				canc = s.lastIndexOf("#"); 
				tag = s.substring(0, canc);
				parola = s.substring(canc+1);
				parolaFin = parola + "/" + tag;
				outBrown.print(parolaFin);
				if (i != lengSplits) {
					outBrown.print(" ");
				}
			}
			line = reader.readLine();
			if (line != null) {
				outBrown.println("");
				outBrown.println("");
			}
		}
		reader.close();
		outBrown.close();
		System.out.println("Done");
	}

	public static void main(String[] args) throws Exception {
		/*String title = "CCC#Forza DDD#Roma BBB#Balotelli AAA#Daje";
		 * CCC#Daje DDD#Ciao
		 * AAA#Roma BBB#Bau
		 * Parser.parserForTitle(title);
		*/
		File f = new File("titoli_tutti.txt");
		Parser.parserForBrownFileTitle(f);
		
		
		/*
		String title = "PPP#Fifth PPP#Harmony AAA#at SSS#Royal SSS#Oak SSS#Music SSS#Theatre SEPA#, CCC#Royal CCC#Oak PRED#on DDD#March DDD#16 SEPA#, DDD#2015 DDD#07 SEPA#: ALTRO#00pm";
		String title2 = "PPP#The PPP#Darkness SEPA#- ALTRO#Dublin SELL#Concert SELL#Tickets SEPA#- PPP#The PPP#Darkness ALTRO#Whelans SELL#Tickets SEPA#- DDD#March DDD#08 SEPA#, DDD#2015";
		
		Parser.parserForBrownTitle(title);
		Parser.parserForBrownTitle(title2);
		*/
	}

}
