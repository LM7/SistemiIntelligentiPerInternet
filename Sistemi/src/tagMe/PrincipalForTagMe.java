package tagMe;


import java.net.URL;
import java.util.HashMap;
import java.util.List;

import events.MsnSearchEngine;
import boilerpipe.Boilerpipe;

public class PrincipalForTagMe {

	public final static int numero_query = 1;

	public static void main(String[] args) throws Exception {
		String data = "23 June 2015";
		String evento_cantante = "Lady Gaga";
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
				
				title = title.replaceAll("\\&.*?\\;", "");
				text = text.replaceAll("\n", " ");
				text = text.replaceAll("\\<.*?\\>|\\{.*?\\}", "");
				text = text.replaceAll("\\&.*?\\;", "");

				Tagger tagMe = new Tagger();
				Parser parser = new Parser();
				
				List<HashMap<String,Integer>> listaMappeTitleRaw = tagMe.getTagMePartialProposedData(title);
				HashMap<String,Integer> mapPTitleRaw = listaMappeTitleRaw.get(0);
				HashMap<String,Integer> mapCTitleRaw = listaMappeTitleRaw.get(1);
				HashMap<String,Integer> mapVTitleRaw = listaMappeTitleRaw.get(2);
				/*
				System.out.println("\nPERSONA: MAPPA TITLE RAW: "+mapPTitleRaw);
				System.out.println("CITTA': MAPPA TITLE RAW: "+mapCTitleRaw);
				System.out.println("SEDE: MAPPA TITLE RAW: "+mapVTitleRaw);
				*/
				
				HashMap<String,Integer> mapPTitle = parser.addValueTitle(mapPTitleRaw);
				HashMap<String,Integer> mapCTitle = parser.addValueTitle(mapCTitleRaw);
				HashMap<String,Integer> mapVTitle = parser.addValueTitle(mapVTitleRaw);
				/*
				System.out.println("PERSONA: MAPPA TITLE (+10points): "+mapPTitle);
				System.out.println("CITTA': MAPPA TITLE (+10points): "+mapCTitle);
				System.out.println("SEDE: MAPPA TITLE (+10points): "+mapVTitle);
				*/

				String[] datiPropostiTitle = parser.choiceDataProposals(mapPTitle, mapCTitle, mapVTitle);
				/*
				System.out.println("\n=== Dati proposti TITOLO ===");
				System.out.println("PERSONA: "+datiPropostiTitle[0]);
				System.out.println("CITTA': "+datiPropostiTitle[1]);
				System.out.println("SEDE: "+datiPropostiTitle[2]);
				*/
				
				List<HashMap<String,Integer>> listaMappeText = tagMe.getTagMePartialProposedData(text);
				HashMap<String,Integer> mapPText = listaMappeText.get(0);
				HashMap<String,Integer> mapCText = listaMappeText.get(1);
				HashMap<String,Integer> mapVText = listaMappeText.get(2);
				/*
				System.out.println("PERSONA: MAPPA TEXT: "+mapPText);
				System.out.println("CITTA': MAPPA TEXT: "+mapCText);
				System.out.println("SEDE: MAPPA TEXT: "+mapVText);
				*/
				
				String[] datiPropostiText = parser.choiceDataProposals(mapPText, mapCText, mapVText);
				/*
				System.out.println("\n=== Dati proposti TESTO ===");
				System.out.println("PERSONA: "+datiPropostiText[0]);
				System.out.println("CITTA': "+datiPropostiText[1]);
				System.out.println("SEDE: "+datiPropostiText[2]);
				*/
				
				String[] datiPropostiFinali = tagMe.getTagMeFinalProposedData(datiPropostiTitle,datiPropostiText,mapPTitle,mapCTitle,mapVTitle,mapPText,mapCText,mapVText);
				System.out.println("\n=== Dati proposti FINALI ===");
				System.out.println("PERSONA: "+datiPropostiFinali[0]);
				System.out.println("CITTA': "+datiPropostiFinali[1]);
				System.out.println("SEDE: "+datiPropostiFinali[2]);
				
				HashMap<String,Integer> mapPFinal = parser.mergeMap(mapPTitle,mapPText);
				HashMap<String,Integer> mapCFinal = parser.mergeMap(mapCTitle,mapCText);
				HashMap<String,Integer> mapVFinal = parser.mergeMap(mapVTitle,mapVText);
				/*
				System.out.println("PERSONA: MAPPA FINAL: "+mapPFinal);
				System.out.println("CITTA': MAPPA FINAL: "+mapCFinal);
				System.out.println("SEDE: MAPPA FINAL: "+mapVFinal);
				*/
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

}