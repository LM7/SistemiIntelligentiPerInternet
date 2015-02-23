package main;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import lastFM.geoMethods;
import nertagme.NerTagme2;
import suTime.SUTimeNuova;
import boilerpipe.Boilerpipe;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import events.MsnSearchEngine;

public class CreateTrainingTest {

	//"SII_events"
	//"siicdll"
	
	public final static HashSet<String> STOP_LOCATION = new HashSet<String>(Arrays.asList("venues","venue","WordPress.com","big","big red","local","Gamespot","events","met","Europe","death"));
	
	public final static int numero_query = 1;
	//public final static String[] CITTA = {"Roma","Londra","New York","Los Angeles","Stoccolma","Parigi","Helsinki","Canberra","Chicago","Austin"};
	public final static String[] CITTA = {"Stoccolma"};

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
		DB db = mongo.getDB("database");
		DBCollection collection = db.getCollection("training");	
		
		// svuota database
		BasicDBObject remove = new BasicDBObject();
		collection.remove(remove);


		for(i=0;i<CITTA.length;i++) {

			// 'totale' e' una lista di eventi [artista, luogo, data]
			ArrayList<String[]> totale = geoMethods.eventsPusher(CITTA[i]);

			//int prendiSolo10Eventi;
			//for(prendiSolo10Eventi=10;prendiSolo10Eventi<totale.size();prendiSolo10Eventi++){
			for(String[] trio: totale) {
				//String[] trio = totale.get(prendiSolo10Eventi);
				String evento_cantante_giusto = trio[0];
				String luogo_giusto = trio[1];
				String data_giusta = trio[2];


				MsnSearchEngine se = new MsnSearchEngine();
				String[] urls = se.getUrls(data_giusta+" "+evento_cantante_giusto, numero_query);
				for(String s: urls) {
					//Boilerpipe
					Boilerpipe bolierpipe = new Boilerpipe();
					URL url = null;
					try {
						j++;
						System.out.println("Stiamo a "+j+"  La citta' e' "+CITTA[i]);
						url = new URL(s);	
						String[] site = bolierpipe.getText(url);
						String title = site[0];
						String text = site[1];				

						
						//SUTime
						SUTimeNuova suT = new SUTimeNuova();
						HashMap<Date, int[]> date = suT.getTime(title,text);						
						
						//NERTAGME2
						
						List<HashMap<String,Integer>> result = NerTagme2.ritorna(site); 			
						Set<String> persone = result.get(0).keySet();
						Set<String> luoghi = result.get(1).keySet();
						
						Boolean ha_rimosso = luoghi.removeAll(STOP_LOCATION);
						System.out.println("HA TOLTO LUOGHI BRUTTI " + ha_rimosso);
						
						System.out.println("date"+date.size());
						System.out.println("PERSONE"+persone.size());
						System.out.println("LUOGHI"+luoghi.size());

						//variabili d'appoggio usate solo per contare
						int x,y,z;
						x=0;
						for(Date data: date.keySet()) {
							y=0;
							Locale.setDefault(Locale.ENGLISH);
							SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMMMMMMMM dd yyyy");
							String dataStringa = DATE_FORMAT.format(data);
							for(String persona: persone) {
								z=0;
								String buono = "";
								//se la data oppure la persona e' sbagliata il record non e' buono
								if(!dataStringa.equals(data_giusta) || !persona.equals(evento_cantante_giusto))
									buono = "-1";
								for(String luogo: luoghi) {
									
									BasicDBObject document = new BasicDBObject();
									document.put("BUONO", buono);
									document.put("data", data_giusta);
									document.put("evento_cantante", evento_cantante_giusto);
									document.put("luogo", luogo_giusto);
									
									String d= "data"+x;
									String p= "persona"+y;
									String l = "luogo"+z;
									
									document.put(d, dataStringa);
									document.put(p, persona);
									document.put(l, luogo);
									
									document.put("url", url.toString());
									
									String featuresData = stampaFeatures(date.get(data));
									document.put("featuresData", featuresData);
									
									
									collection.insert(document);
									
									z++;
								}
								y++;
							}
							x++;
						}
					
						
					} catch (Exception e) {
						//System.out.println(e.getMessage());
					}

				}
			}
		}
		/*
		int k = 1;
		DBCursor cursor = collection.find();
		while (cursor.hasNext()) {
			System.out.println(k+") "+cursor.next());
			k++;
		}*/

		System.out.println("Done"+j);
	}

	private static String stampaFeatures(int[] array) {
		int i;
		String s = "";
		for(i=0;i<array.length-1;i++)
			s += array[i]+":";
		s+= array[i];
		return s;
	}
	
	
}
