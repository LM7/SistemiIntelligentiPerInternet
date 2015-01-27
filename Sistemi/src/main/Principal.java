package main;

import java.io.PrintWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import namedEntityRecognizer.NamedEntityRecognizerTest;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import events.MsnSearchEngine;
import suTime.SUTime;
import boilerpipe.Boilerpipe;

public class Principal {
	
	public final static int numero_query = 5;

	public static void main(String[] args) {
		String data = "31 January 2015";
		String evento_cantante = "Giraffage";
		String luogo = "";

		//Database
		MongoClient mongo = null;
		try {
			mongo = new MongoClient("localhost", 27017);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		DB db = mongo.getDB("db");
		DBCollection collection = db.getCollection("collezione");		
		// svuota database
		BasicDBObject x = new BasicDBObject();
		collection.remove(x);

		MsnSearchEngine se = new MsnSearchEngine();
		String[] urls = se.getUrls(data+" "+evento_cantante+" "+luogo, numero_query);
		for(String s: urls) {
			//Boilerpipe
			Boilerpipe b = new Boilerpipe();
			URL url = null;
			try {
				url = new URL(s);
				String title = b.getText(url)[0];
				String text = b.getText(url)[1];				
				
				//SUTime
				SUTime suT = new SUTime();
				HashMap<Date, Integer> date = suT.getTime(title,text);
				Date dataProposta = suT.dataEvento(date);
				
				
				
				//NER
				String[] site = {title, text};
				
				//Luogo
				NamedEntityRecognizerTest ner = new NamedEntityRecognizerTest();
				ArrayList<HashMap<String,Integer>> lista = ner.createListOfMapEntity(site); //restituisce la lista di mappe
				Set<String> luoghi = lista.get(0).keySet();    //la lista di luoghi/locations
				String luogoTop = ner.locationTop(lista, title); //il luogo proposto
				
				//Evento/Persona
				Set<String> persone = lista.get(1).keySet(); //la lista di persone
				String personaTop = ner.personTop(lista, title); // la persona proposta
				

				HashMap<String, Integer> dateString = new HashMap<String, Integer>();	
				for (Date d : date.keySet()){
					dateString.put(d.toString(), date.get(d));
				}
				BasicDBObject document = new BasicDBObject();
				document.put("data", data);
				document.put("evento_cantante", evento_cantante);
				document.put("luogo", luogo);
				document.put("url", url.toString());
				document.put("data proposta", dataProposta.toString());
				document.put("date", dateString);
				document.put("luogo proposto", luogoTop );
				document.put("luoghi", luoghi);
				document.put("persona proposta", personaTop );
				document.put("persone", persone);
				collection.insert(document);
				
				System.out.println("Documento aggiunto");

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		}
		DBCursor cursor = collection.find();
		while (cursor.hasNext()) {
			System.out.println(cursor.next());
		}

		System.out.println("Done");
	}



	public void prova() throws Exception {
		MsnSearchEngine se = new MsnSearchEngine();
		String[] urls = se.getUrls("Roma", 5);

		//Queste 2 righe verranno tolte
		for(String s: urls)
			System.out.println(s);


		Boilerpipe b = new Boilerpipe();

		/* questo verra' scommentato
		for(String s: urls) {
			URL url = new URL(s);
		 */

		//Queste 5 righe verranno tolte
		URL url = new URL("http://www.last.fm/event/3996308+Giraffage+at+Music+Hall+of+Williamsburg+on+31+January+2015"
				//"http://www.aloud.com/towns/london/camden%20underworld.xml"
				//"http://www.aloud.com/tickets/within-the-ruins"
				//"http://lambgoat.com/news/23481/Within-The-Ruins-I-Declare-War-tour-Europe"				
				);

		String title = b.getText(url)[0];
		String text = b.getText(url)[1];

		//Queste 4 righe verranno tolte
		PrintWriter out = new PrintWriter("contenutoTesto.html", "UTF-8");
		out.println("<meta http-equiv=\"Content-Type\" content=\"html; charset=utf-8\" />");
		out.println(text);
		out.close();

		SUTime suT = new SUTime();

		//Questa riga verra' tolta
		suT.getTimeProva(text);

		HashMap<Date, Integer> date = suT.getTime(title,text);

		//Queste 8 righe verranno tolte
		int i = 1;
		int numDate = 0;
		for(Date d: date.keySet()){
			System.out.println(i+")"+d+" :"+date.get(d));
			i++;
			numDate += date.get(d);
		}
		System.out.println("DATE TOT= "+numDate);
		System.out.println("data evento proposto="+suT.dataEvento(date));		
	}

}
