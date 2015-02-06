package main;

import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import namedEntityRecognizer.NamedEntityRecognizerTest;
import nertagme.NerTagme;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import edu.stanford.nlp.wordseg.affDict;
import events.MsnSearchEngine;
import suTime.SUTime;
import boilerpipe.Boilerpipe;
import lastFM.*;

public class Principal {

	public final static int numero_query = 1;
	public final static String[] CITTA = {"Roma","Londra","New York","Los Angeles","Stoccolma","Parigi","Helsinki","Camberra","Chicago","Austin"};

	public static void main(String[] args) {
		int i;
		int j = 0;

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


		for(i=0;i<CITTA.length;i++) {

			// 'totale' e' una lista di eventi [artista, luogo, data]
			ArrayList<String[]> totale = geoMethods.eventsPusher(CITTA[i]);

			for(String[] trio: totale) {
				String evento_cantante = trio[0];
				String luogo = trio[1];
				String data = trio[2];



				// SCOMMENTARE PER VEDERE LE INFO RACCOLTE
				// Visualizzazione di 'totale'
				/*
		for (String[] a : totale){
			System.out.println("Artista: "+a[0]);
			System.out.println("Luogo: "+a[1]);
			System.out.println("Data: "+a[2]);
			System.out.println();
		}
				 */

				/*String data = "31 January 2015";
		String evento_cantante = "Giraffage";
		String luogo = "";*/

				/*String data = "28 January 2015";
		String evento_cantante = "Die Antwoord";
		String luogo = "";*/

				/*String data = "23 June 2015";
		String evento_cantante = "Lady Gaga";
		String luogo = "";*/

				/*String data = "2 June 2015";
		String evento_cantante = "Hozier";
		String luogo = "";*/

				/*String data = "5 November 2015";
		String evento_cantante = "Nickelback";
		String luogo = "";*/

				/*String data = "20 March 2015";
		String evento_cantante = "Francesco De Gregori";
		String luogo = "";*/

				/*String data = "10 April 2015";
		 String evento_cantante = "The Prodigy";
		 String luogo = ""; Heineken Music Hall, Amsterdam
				 */

				/*String data = "4 April 2015";
		 String evento_cantante = "A Day to Remember / The Offspring";
		 String luogo = ""; Tempe Beach Park, United States
				 */

				/*String data = "27 April 2015";
		 String evento_cantante = "One Republic at MTS Centre";
		 String luogo = ""; MTS Centre, Canada
				 */




				

				MsnSearchEngine se = new MsnSearchEngine();
				String[] urls = se.getUrls(data+" "+evento_cantante, numero_query);
				for(String s: urls) {
					//Boilerpipe
					Boilerpipe b = new Boilerpipe();
					URL url = null;
					try {
						j++;
						
						url = new URL(s);	
						String[] site = b.getText(url);
						String title = site[0];
						String text = site[1];				

						//SUTime
						SUTime suT = new SUTime();
						HashMap<Date, Integer> date = suT.getTime(title,text);
						Date dataProposta = suT.dataEvento(date);


						/**
				//NER
				//Luogo
				NamedEntityRecognizerTest ner = new NamedEntityRecognizerTest();
				ArrayList<HashMap<String,Integer>> lista = ner.createListOfMapEntity(site); //restituisce la lista di mappe
				HashMap<String,Integer> locations = lista.get(0); // la mappa dei luoghi
				HashMap<String,Integer> people = lista.get(1); // la mappa delle persone-eventi

				String luogoTop = ner.entityTop(locations);
				String personaTop = ner.entityTop(people);
						 **/
						//NERTAGME
						String[] datiProposti = NerTagme.ritorna(site);
						String personaProposta = datiProposti[0];
						String luogoProposto = datiProposti[1];

						/*HashMap<String, Integer> dateString = new HashMap<String, Integer>();	
				for (Date d : date.keySet()){
					dateString.put(d.toString(), date.get(d));
				}*/
						//luogo = "Williamsburg";
						//luogo = "Le Zenith, Paris";
						//luogo = "Radio City Music Hall, New York";
						//luogo = "Shepherds Bush Empire, London";
						//luogo = "Razzmatazz, Barcelona";
						//luogo = "Roma, Palalottomatica";
						BasicDBObject document = new BasicDBObject();
						document.put("data", data);
						document.put("evento_cantante", evento_cantante);
						document.put("luogo", luogo);
						document.put("url", url.toString());
						document.put("data proposta", dataProposta.toString());
						//document.put("date", dateString);
						document.put("luogo proposto", luogoProposto);
						//document.put("luoghi", locations);
						document.put("persona proposta", personaProposta );
						//document.put("persone", people);
						collection.insert(document);

						System.out.println("Documento aggiunto");

					} catch (Exception e) {
						System.out.println(e.getMessage());
					}

				}
			}
		}
		DBCursor cursor = collection.find();
		while (cursor.hasNext()) {
			System.out.println(cursor.next());
		}

		System.out.println("Done"+j);
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
