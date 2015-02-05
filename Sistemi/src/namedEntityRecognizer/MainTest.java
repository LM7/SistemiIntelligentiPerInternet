package namedEntityRecognizer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import tagMe.tagMe;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import events.MsnSearchEngine;
import boilerpipe.Boilerpipe;

public class MainTest {
	public final static int numero_query = 1;

	public static void main(String[] args) throws Exception {
		String data = "5 November 2015";
		String evento_cantante = "Nickelback";
		String luogo = "";
		
		NamedEntityRecognizerTest ner = new NamedEntityRecognizerTest();
		
		/*String[] example = {"Good afternoon Rajat Raina, how are you today?",
		"I go to school at Stanford University, which California is located in California." };
		
		ArrayList<HashMap<String,Integer>> lista = ner.createListOfMapEntity(example);*/
		
		MsnSearchEngine se = new MsnSearchEngine();
		String[] urls = se.getUrls(data+" "+evento_cantante+" "+luogo, numero_query);
		
		
		
		/*parte database---> RICORDA SI SCRIVE "DABABASE"*/
		

		/*MongoClient mongo = null;
		try {
			mongo = new MongoClient("localhost", 27017);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		DB db = mongo.getDB("db");
		DBCollection collection = db.getCollection("collezione");		
		// svuota database
		BasicDBObject x = new BasicDBObject();
		collection.remove(x);*/

		for(String s: urls) {
			Boilerpipe b = new Boilerpipe();
			URL url = new URL(s);
			
			String title = b.getText(url)[0];
			String text = b.getText(url)[1];
			
			String[] site = {title, text};
			ArrayList<HashMap<String,Integer>> lista = ner.createListOfMapEntity(site); //restituisce la lista di mappe
			HashMap<String,Integer> locations = lista.get(0); // la mappa dei luoghi
			HashMap<String,Integer> people = lista.get(1); // la mappa delle persone-eventi
			
			String luogoTop = ner.entityTop(locations);
			String personaTop = ner.entityTop(people);
			
			
			System.out.println("IL LUOGO PROPOSTO E': "+ luogoTop);
			
			System.out.println("LA PERSONA PROPOSTA E': "+ personaTop);
			System.out.println("TITOLO "+title);
			
			/*tagme*/
			
			/*System.out.println("PARTE DI TAGME");
			
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
			System.out.println("PERSONA: "+datiPropostiText[0]);*/
			
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
