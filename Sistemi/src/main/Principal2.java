package main;

import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import nertagme.NerTagme;
import nertagme.NerTagme2;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import events.MsnSearchEngine;
import suTime.SUTime;
import boilerpipe.Boilerpipe;
import lastFM.*;

public class Principal2 {

	public final static int numero_query = 1;
	public final static String[] CITTA = {"Roma","Londra","New York","Los Angeles","Stoccolma","Parigi","Helsinki","Canberra","Chicago","Austin"};

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
		DB db = mongo.getDB("db2");
		DBCollection collection = db.getCollection("collezione2");	
		
		// svuota database
		BasicDBObject x = new BasicDBObject();
		collection.remove(x);


		for(i=0;i<CITTA.length;i++) {

			// 'totale' e' una lista di eventi [artista, luogo, data]
			ArrayList<String[]> totale = geoMethods.eventsPusher(CITTA[i]);

			int prendiSolo10Eventi;
			for(prendiSolo10Eventi=19;prendiSolo10Eventi<totale.size();prendiSolo10Eventi++){
			//for(String[] trio: totale) {
				String[] trio = totale.get(prendiSolo10Eventi);
				String evento_cantante = trio[0];
				String luogo = trio[1];
				String data = trio[2];


				MsnSearchEngine se = new MsnSearchEngine();
				String[] urls = se.getUrls(data+" "+evento_cantante, numero_query);
				for(String s: urls) {
					//Boilerpipe
					Boilerpipe b = new Boilerpipe();
					URL url = null;
					try {
						j++;
						System.out.println("Stiamo a "+j+"  La citta' e' "+CITTA[i]);
						url = new URL(s);	
						String[] site = b.getText(url);
						String title = site[0];
						String text = site[1];				

						Set<String> luoghi = null; 
						
						//SUTime
						SUTime suT = new SUTime();
						HashMap<Date, Integer> date = suT.getTime(title,text);
						boolean continua = contieneDate(date.keySet(),data);
						
						

						
						//NERTAGME2
						
						List<HashMap<String,Integer>> result = NerTagme2.ritorna(site); 
						
						
						String cosaMale = "";
						if(continua) {
							//lista di persone presa da NERTAGME hashMap.keySet
							Set<String> persone = result.get(0).keySet();					
							continua = persone.contains(evento_cantante);
							
							if(continua){
								//lista di luoghi presa da NERTAGME hashMap.keySet
								luoghi = result.get(1).keySet();
							}
							else
								cosaMale = "No persona";
						}
						else
							cosaMale = "No data";

						
						BasicDBObject document = new BasicDBObject();
						document.put("url", url.toString());
						document.put("data", data);
						document.put("evento_cantante", evento_cantante);
						document.put("luogo", luogo);
						
						if(luoghi != null) {
							document.put("luoghi", luoghi);
							document.put("buono", "");
						}
						else {
							document.put("xk male", cosaMale);
							document.put("buono", false);
						}

						collection.insert(document);
						
					} catch (Exception e) {
						//System.out.println(e.getMessage());
					}

				}
			}
		}
		int k = 1;
		DBCursor cursor = collection.find();
		while (cursor.hasNext()) {
			System.out.println(k+") "+cursor.next());
			k++;
		}

		System.out.println("Done"+j);
	}



	private static boolean contieneDate(Set<Date> keySet, String data) {	
		Locale.setDefault(Locale.ENGLISH);
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMMMMMMMM dd yyyy");
		//stampaLista(keySet,data);
		for(Date d: keySet) {
			String dataString = DATE_FORMAT.format(d);
			if(dataString.equals(data))
				return true;
		}
		return false;
	}


	private static void stampaLista(Set<Date> keySet, String data) {
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMMMMMMMM dd yyyy");
		System.out.print(data+" -> { ");
		for(Date d: keySet) {
			String dataString = DATE_FORMAT.format(d);
			System.out.println(dataString + " ; ");
		}
		System.out.print("}");
	}

}
