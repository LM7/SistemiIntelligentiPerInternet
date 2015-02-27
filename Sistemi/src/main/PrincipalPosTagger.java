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

import posTagger.PosTagger;
import lastFM.geoMethods;
import nertagme.NerTagme2;
import suTime.SUTimeNuova;
import boilerpipe.Boilerpipe;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import events.MsnSearchEngine;

public class PrincipalPosTagger {
	public final static HashSet<String> STOP_LOCATION = new HashSet<String>(Arrays.asList("venues","venue","WordPress.com","big","big red","local","Gamespot","events","met","Europe","death"));

	public final static int numero_query = 1;
	//public final static String[] CITTA = {"Roma","Londra","New York","Los Angeles","Stoccolma","Parigi","Helsinki","Canberra","Chicago","Austin"};
	public final static String[] CITTA = {"Los Angeles"};

	public static void main(String[] args) {
		int i;
		int j = 0;
		int aaa= 0;
		int bbb= 0;
		int ccc = 0;

		//Database
		MongoClient mongo = null;
		try {
			mongo = new MongoClient("localhost", 27017);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		DB db = mongo.getDB("databasePOS");
		DBCollection collection = db.getCollection("trainingPOS");	

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


						//lavoriamo solo sul titolo
						//SUTime
						SUTimeNuova suT_titolo = new SUTimeNuova();
						HashMap<Date, int[]> date_titolo = suT_titolo.getTime(title,"");						

						//NERTAGME2

						site = new String[] {title,"cla e' una cacca"};
						List<HashMap<String,Integer>> result_titolo = NerTagme2.ritorna(site); 			
						Set<String> persone_titolo = result_titolo.get(0).keySet();
						Set<String> luoghi_titolo = result_titolo.get(1).keySet();

						Set<String> dateStringa_titolo = new HashSet<String>();
						Locale.setDefault(Locale.ENGLISH);
						SimpleDateFormat DATE_FORMAT_titolo = new SimpleDateFormat("MMMMMMMMMM dd yyyy");
						for(Date data_titolo: date_titolo.keySet()) {
							String dataStringa_titolo = DATE_FORMAT_titolo.format(data_titolo);
							dateStringa_titolo.add(dataStringa_titolo);
						}

						BasicDBObject document_titolo = new BasicDBObject();
						document_titolo.put("data", data_giusta);
						document_titolo.put("evento_cantante", evento_cantante_giusto);
						document_titolo.put("luogo", luogo_giusto);
						document_titolo.put("Titolo", title);

						document_titolo.put("date", dateStringa_titolo);
						document_titolo.put("persone", persone_titolo);
						document_titolo.put("luoghi", luoghi_titolo);

						document_titolo.put("url", url.toString());

						collection.insert(document_titolo);




						//lavoriamo solo sul testo
						text = text.replaceAll("\\<.*?>", "");
						text = text.substring(168);

						String[] frasi = text.split("\\.");
						System.out.println("# frasi = " + frasi.length);

						int kkk = 1;
						for(String frase: frasi) {
							frase = frase.replaceAll ("\r\n|\r|\n", " ");
							frase = frase.replaceAll(" {2,}", " ");
							frase = frase.replaceAll("\\<.*?>", "");
							System.out.println("frase "+kkk);
							kkk++;


							/*
						String title = BoilerpipeJson.getTitle(url);
						String text = BoilerpipeJson.getContent(url);
						String[] site = new String[]{title,text};
							 */

							//SUTime
							SUTimeNuova suT = new SUTimeNuova();
							HashMap<Date, int[]> date = suT.getTime("",frase);						

							//NERTAGME2

							site = new String[] {"cla e' una cacca",frase};
							List<HashMap<String,Integer>> result = NerTagme2.ritorna(site); 
							Set<String> persone = new HashSet<>();
							Set<String> luoghi = new HashSet<>();
							if(result != null && !result.isEmpty()) {
								persone = result.get(0).keySet();
								luoghi = result.get(1).keySet();
							}


							aaa += date.size();
							bbb += persone.size();
							ccc += luoghi.size();

							System.out.println("date"+date.size());
							System.out.println("PERSONE"+persone.size());
							System.out.println("LUOGHI"+luoghi.size());


							//Aggiungo nel db se e solo se ha trovato almeno una dei tre dati
							if(date.size()!=0 || persone.size()!=0 || luoghi.size()!=0) {
								Set<String> dateStringa = new HashSet<String>();
								Locale.setDefault(Locale.ENGLISH);
								SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMMMMMMMM dd yyyy");
								for(Date data: date.keySet()) {
									String dataStringa = DATE_FORMAT.format(data);
									dateStringa.add(dataStringa);
								}

								BasicDBObject document = new BasicDBObject();
								document.put("data", data_giusta);
								document.put("evento_cantante", evento_cantante_giusto);
								document.put("luogo", luogo_giusto);
								//document.put("Titolo", title);

								String fraseTagger = PosTagger.textTagged(frase);
								document.put("Frase", frase);
								document.put("FrasePOS", fraseTagger);

								document.put("date", dateStringa);
								document.put("persone", persone);
								document.put("luoghi", luoghi);

								document.put("url", url.toString());

								//String featuresData = stampaFeatures(date.get(data));
								//document.put("featuresData", featuresData);


								collection.insert(document);

							}
						}


					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		System.out.println("Date "+aaa+" Persone "+bbb+" Luoghi "+ccc);

		System.out.println("Done"+j);


		int k = 1;
		DBCursor cursor = collection.find();
		while (cursor.hasNext()) {
			System.out.println(k+") "+cursor.next());
			k++;
		}
	}

}