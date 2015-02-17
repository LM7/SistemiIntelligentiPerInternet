package namedEntityRecognizer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lastFM.geoMethods;
import nertagme.NerTagme;
import tagMe.Parser;
import tagMe.Tagger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import events.MsnSearchEngine;
import boilerpipe.Boilerpipe;

public class MainTest {
	public final static int numero_query = 1;
	public final static String[] CITTA = {"Austin"};


	public static void main(String[] args) throws Exception {
		/*String data = "5 November";
		String evento_cantante = "Nickelback";
		String luogo = "";*/
		int i;

		for(i=0;i<CITTA.length;i++) {

			// 'totale' e' una lista di eventi [artista, luogo, data]
			ArrayList<String[]> totale = geoMethods.eventsPusher(CITTA[i]);

			int prendiSolo10Eventi;
			for(prendiSolo10Eventi=11;prendiSolo10Eventi<totale.size();prendiSolo10Eventi++){
				//for(String[] trio: totale) {
				String[] trio = totale.get(prendiSolo10Eventi);
				String evento_cantante = trio[0];
				String luogo = trio[1];
				String data = trio[2];



				NamedEntityRecognizerTest ner = new NamedEntityRecognizerTest();
				NerTagme nertagme = new NerTagme();


				MsnSearchEngine se = new MsnSearchEngine();
				String[] urls = se.getUrls(data+" "+evento_cantante+" "+luogo, numero_query);





				for(String s: urls) {
					Boilerpipe b = new Boilerpipe();
					URL url = new URL(s);
					System.out.println("UUUUURL: "+url);

					
					try {
						String title = b.getText(url)[0];

						String text = b.getText(url)[1];

						String[] site = {title, text};
						//System.out.println("TITOLO "+title);
						//System.out.println("TESTO "+text);

						ArrayList<HashMap<String,Integer>> lista = ner.createListOfMapEntity(site); //restituisce la lista di mappe
						HashMap<String,Integer> locations = lista.get(0); // la mappa dei luoghi
						HashMap<String,Integer> people = lista.get(1); // la mappa delle persone-eventi

						System.out.println("LISTA DI LUOGHI"+ locations.keySet());
						System.out.println("LISTA DI PERSONE"+ people.keySet());
						
					}
					catch(Exception e) {
						
					}


					
						
					

					

					/*String luogoTop = ner.entityTop(locations);
			String personaTop = ner.entityTop(people);

			String[] result = {personaTop, luogoTop};


			System.out.println("LMLMLMLMLM IL LUOGO PROPOSTO E': "+ luogoTop);

			System.out.println("LMLMLMLMLM LA PERSONA PROPOSTA E': "+ personaTop);*/




					/*tagme*/

					/*System.out.println("PARTE DI TAGME");

			Tagger tagMe = new Tagger();
			Parser parser = new Parser();



			List<HashMap<String,Integer>> listaMappeTitleRaw = tagMe.getTagMePartialProposedData(title);
			if ( listaMappeTitleRaw != null  ) {


				HashMap<String,Integer> mapPTitleRaw = listaMappeTitleRaw.get(0);
				HashMap<String,Integer> mapCTitleRaw = listaMappeTitleRaw.get(1);
				HashMap<String,Integer> mapVTitleRaw = listaMappeTitleRaw.get(2);

				HashMap<String,Integer> mapPTitle = parser.addValueTitle(mapPTitleRaw);
				HashMap<String,Integer> mapCTitle = parser.addValueTitle(mapCTitleRaw);
				HashMap<String,Integer> mapVTitle = parser.addValueTitle(mapVTitleRaw);

				String[] datiPropostiTitle = parser.choiceDataProposals(mapPTitle, mapCTitle, mapVTitle);

				List<HashMap<String,Integer>> listaMappeText = tagMe.getTagMePartialProposedData(text);
				if ( listaMappeText != null  ) {
					HashMap<String,Integer> mapPText = listaMappeText.get(0);
					HashMap<String,Integer> mapCText = listaMappeText.get(1);
					HashMap<String,Integer> mapVText = listaMappeText.get(2);

					String[] datiPropostiText = parser.choiceDataProposals(mapPText, mapCText, mapVText);

					String[] datiPropostiFinali = tagMe.getTagMeFinalProposedData(datiPropostiTitle,datiPropostiText,mapPTitle,mapCTitle,mapVTitle,mapPText,mapCText,mapVText);
					System.out.println("\n=== Dati proposti FINALI ===");
					System.out.println("CRCRCRCR PERSONA: "+datiPropostiFinali[0]);
					System.out.println("CRCRCRCR CITTA': "+datiPropostiFinali[1]);
					System.out.println("CRCRCRCR SEDE: "+datiPropostiFinali[2]);

					HashMap<String,Integer> mapPFinal = parser.mergeMap(mapPTitle,mapPText);
					HashMap<String,Integer> mapCFinal = parser.mergeMap(mapCTitle,mapCText);
					HashMap<String,Integer> mapVFinal = parser.mergeMap(mapVTitle,mapVText);


					result = nertagme.nerCompareToTagme(locations, people, mapPFinal, mapCFinal, mapVFinal, datiPropostiFinali);
				}
			}

			System.out.println("ECCO LA PERSONA TOP: "+result[0]+" ED ECCO IL LUOGO TOP: "+result[1]);*/





					/*BasicDBObject document = new BasicDBObject();
			document.put("data", data);
			document.put("evento_cantante", evento_cantante);
			document.put("luogo", luogo);
			document.put("url", url.toString());
			document.put("luogo proposto", luogoTop );
			document.put("luoghi", luoghi);
			collection.insert(document);*/
				}
			}




		}

	}

	public static void printMap(HashMap<String, Integer> wordTaggedPlace) {
		Iterator iterator = wordTaggedPlace.keySet().iterator();	  
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			String value = wordTaggedPlace.get(key).toString();	  
			System.out.println("La Parola proposta \""+key+"\" ha valore "+value);
		}
	}
	



}
