package main;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import suTime.SUTime_Titoli;
import lastFM.geoMethods;
import boilerpipe.Boilerpipe;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import events.MsnSearchEngine;

public class PrincipalForTitle {
	
	public final static HashSet<String> STOP_SITE = new HashSet<String>(Arrays.asList("Last.fm","Songkick"));

	public final static int numero_query = 10;
	public final static String[] CITTA = {"Roma","Londra","New York","Los Angeles","Stoccolma","Parigi","Helsinki","Canberra","Chicago","Austin"};
	//public final static String[] CITTA = {"Londra","New York"};

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
				for(String urlString: urls) {
					//Boilerpipe
					Boilerpipe bolierpipe = new Boilerpipe();
					URL url = null;
					try {
						j++;
						System.out.println("Stiamo a "+j+"  La citta' e' "+CITTA[i]);
						url = new URL(urlString);	
						String[] site = bolierpipe.getText(url);
						String title = site[0];
						//String text = site[1];				
						
						String titleTag = title;
						//RIMUOVI SITI
						for(String sito: STOP_SITE){
							titleTag = titleTag.replaceAll(sito, "");
						}

						SUTime_Titoli SUTT = new SUTime_Titoli();
						//DATA TAGGATA
						titleTag = SUTT.getTextTag(title);
						//PERSONA TAGGATA
						titleTag = titleTag.replaceAll(evento_cantante_giusto, "PPP");

						try {
							String[] sede_citta = luogo_giusto.split(",");
							//trim toglie spazi iniziali e finali
							String sede = sede_citta[0].trim();
							String citta = sede_citta[1].trim();
							//SEDE TAGGATA
							titleTag = titleTag.replaceAll(sede, "SSS");
							//CITTA' TAGGATA
							titleTag = titleTag.replaceAll(citta, "CCC");
						}catch(Exception e) {
							System.out.println("ERRORE SEDE_CITTA'");
							e.printStackTrace();
						}
						
						
						String dominio = urlString.split("/")[2];
						
						//eventualmente si puo' togliere
						//SUTime
						/*
						SUTimeNuova suT = new SUTimeNuova();
						HashMap<Date, int[]> date = suT.getTime(title,"");						

						//NERTAGME2
						site[1] = " ";
						List<HashMap<String,Integer>> result = NerTagme2.ritorna(site); 			
						Set<String> persone = result.get(0).keySet();
						Set<String> luoghi = result.get(1).keySet();
						 */

						BasicDBObject document = new BasicDBObject();
						document.put("data", data_giusta);
						document.put("evento_cantante", evento_cantante_giusto);
						document.put("luogo", luogo_giusto);

						document.put("Titolo", title);
						document.put("TitoloTag", titleTag);
/*
						document.put("dateTrovate", date);
						document.put("persone", persone);
						document.put("luoghi", luoghi);
*/
						document.put("dominio", dominio);
						document.put("url", url.toString());
						
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

}