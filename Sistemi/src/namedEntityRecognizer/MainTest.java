package namedEntityRecognizer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
			
			System.out.println("PARTE DI TAGME");
			/**
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
				
				HashMap<String,Integer> mapPTitle = parser.addValueTitle(mapPTitleRaw);
				HashMap<String,Integer> mapCTitle = parser.addValueTitle(mapCTitleRaw);
				HashMap<String,Integer> mapVTitle = parser.addValueTitle(mapVTitleRaw);
		
				String[] datiPropostiTitle = parser.choiceDataProposals(mapPTitle, mapCTitle, mapVTitle);
				
				List<HashMap<String,Integer>> listaMappeText = tagMe.getTagMePartialProposedData(text);
				HashMap<String,Integer> mapPText = listaMappeText.get(0);
				HashMap<String,Integer> mapCText = listaMappeText.get(1);
				HashMap<String,Integer> mapVText = listaMappeText.get(2);
				
				String[] datiPropostiText = parser.choiceDataProposals(mapPText, mapCText, mapVText);
				
				String[] datiPropostiFinali = tagMe.getTagMeFinalProposedData(datiPropostiTitle,datiPropostiText,mapPTitle,mapCTitle,mapVTitle,mapPText,mapCText,mapVText);
				System.out.println("\n=== Dati proposti FINALI ===");
				System.out.println("PERSONA: "+datiPropostiFinali[0]);
				System.out.println("CITTA': "+datiPropostiFinali[1]);
				System.out.println("SEDE: "+datiPropostiFinali[2]);
				
				HashMap<String,Integer> mapPFinal = parser.mergeMap(mapPTitle,mapPText);
				HashMap<String,Integer> mapCFinal = parser.mergeMap(mapCTitle,mapCText);
				HashMap<String,Integer> mapVFinal = parser.mergeMap(mapVTitle,mapVText);
			**/
			
			
			
		
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
	
	public static void printMap(HashMap<String, Integer> wordTaggedPlace) {
		Iterator iterator = wordTaggedPlace.keySet().iterator();	  
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			String value = wordTaggedPlace.get(key).toString();	  
			System.out.println("La Parola proposta \""+key+"\" ha valore "+value);
		}
	}


}
