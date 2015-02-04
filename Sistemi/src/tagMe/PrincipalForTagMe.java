package tagMe;


import java.net.URL;

import events.MsnSearchEngine;
import boilerpipe.Boilerpipe;

public class PrincipalForTagMe {

	public final static int numero_query = 3;

	public static void main(String[] args) throws Exception {
		String data = "27 February 2015";
		String evento_cantante = "Fightstar";
		String luogo = "";

		String title = "";
		String text = "";

		MsnSearchEngine se = new MsnSearchEngine();
		String[] urls = se.getUrls(data+" "+evento_cantante+" "+luogo, numero_query);
		for(String s: urls) {
			//Boilerpipe
			Boilerpipe b = new Boilerpipe(3);
			URL url = null;
			try {
				url = new URL(s);
				System.out.println("Sito: "+url);
				String[] site = b.getText(url);
				title = site[0];
				text = site[1];
				
				text= text.replaceAll("\n", " ");
				text= text.replaceAll("\\<.*?\\>|\\{.*?\\}", "");
				text= text.replaceAll("\\&.*?\\;", "");
				//System.out.println("Testo: "+text);

				tagMe st = new tagMe();
				
				
				String[] datiPropostiTitle = st.getTagMeProposedData(title);
				System.out.println("\n=== Dati proposti TITOLO ===");
				System.out.println("LUOGO: "+datiPropostiTitle[1]);
				System.out.println("PERSONA: "+datiPropostiTitle[0]);
				
				String[] datiPropostiText = st.getTagMeProposedData(text);
				System.out.println("\n=== Dati proposti TESTO ===");
				System.out.println("LUOGO: "+datiPropostiText[1]);
				System.out.println("PERSONA: "+datiPropostiText[0]);
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}