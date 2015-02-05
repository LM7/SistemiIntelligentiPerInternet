package tagMe;


import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import events.MsnSearchEngine;
import boilerpipe.Boilerpipe;

public class PrincipalForTagMe {

	public final static int numero_query = 1;

	@SuppressWarnings("rawtypes")
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
				
				
				HashMap<String,Integer> datiPropostiTitleRaw = st.getTagMeProposedData(title);
				HashMap<String,Integer> datiPropostiTitle = new HashMap<String,Integer>();
				Iterator iterator = datiPropostiTitleRaw.keySet().iterator();	  
				while (iterator.hasNext()) {
					String key = iterator.next().toString();
					int value = datiPropostiTitleRaw.get(key);
					datiPropostiTitle.put(key, value+10);
				}
				
				System.out.println("\n=== Dati proposti TITOLO ===");
				printMap(datiPropostiTitle);
				
				
				HashMap<String,Integer> datiPropostiText = st.getTagMeProposedData(text);
				System.out.println("\n=== Dati proposti TESTO ===");
				printMap(datiPropostiText);
				
				HashMap<String,Integer> datiDefinitiviProposti = new HashMap<String,Integer>();
				Iterator iteratorTitle = datiPropostiTitle.keySet().iterator();	  
				Iterator iteratorText = datiPropostiText.keySet().iterator();
				while (iteratorTitle.hasNext() && iteratorText.hasNext()) {
					String wordTitle = iteratorTitle.next().toString();
					String wordText = iteratorText.next().toString();
					int valueTitle = datiPropostiTitle.get(wordTitle);
					int valueText = datiPropostiText.get(wordText);
					if(valueTitle>=valueText) {
						datiDefinitiviProposti.put(wordTitle, valueTitle);
					}
					else {
						datiDefinitiviProposti.put(wordText, valueText);
					}
				}
				
				System.out.println("\n=== Dati proposti FINALI ===");
				printMap(datiDefinitiviProposti);
				
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void printMap(HashMap<String, Integer> wordTaggedPlace) {
		Iterator iterator = wordTaggedPlace.keySet().iterator();	  
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			String value = wordTaggedPlace.get(key).toString();	  
			System.out.println("La Parola proposta \""+key+"\" ha valore "+value);
		}
	}
}