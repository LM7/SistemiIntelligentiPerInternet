package main;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import lastFM.geoMethods;
import suTime.SUTime_Titoli;
import boilerpipe.Boilerpipe;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import events.MsnSearchEngine;

public class CreateTitleForTesting {

	public final static int numero_query = 10;
	public final static String[] CITTA = {"Roma","Londra","New York","Los Angeles","Stoccolma","Parigi","Helsinki","Canberra","Chicago","Austin", "Amsterdam",
											"Liverpool", "Boston", "Detroit", "Dublino", "Houston", "Phoenix", "Dallas", "Denver", "Manchester" };
	//public final static String[] CITTA = {"Roma","Londra","New York","Los Angeles","Stoccolma"};

	public static void main(String[] args) {
		createTitle();
	}
	
	public static HashMap<String, ArrayList<String[]>> createTitle() {
		int i;
		int j = 0;
		int train = 0;
		int k = 0;
		
		HashMap<String, ArrayList<String[]>> dominio_dati = new HashMap<String, ArrayList<String[]>>();
		//Database
		MongoClient mongo = null;
		try {
			mongo = new MongoClient("localhost", 27017);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		DB db = mongo.getDB("dbfortesting");
		DBCollection collection = db.getCollection("testing");
		

		// svuota database
		BasicDBObject remove = new BasicDBObject();
		collection.remove(remove);

		for(i=0;i<CITTA.length;i++) {

			// 'totale' e' una lista di eventi [artista, luogo, data]
			ArrayList<String[]> totale = geoMethods.eventsPusher(CITTA[i]);

			for(String[] trio: totale) {
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
						titleTag = titleTag.replaceAll("\\s+", " ");
						titleTag = titleTag.trim();


						//RIMUOVI SITI
						HashSet<String> STOP_SITE = CleanTitle.STOP_SITE;
						for(String sito: STOP_SITE){
							titleTag = titleTag.replace(sito, "");
						}
						//trim toglie spazi iniziali e finali
						titleTag.trim();

						String dominio = urlString.split("/")[2];
						CleanTitle ct = new CleanTitle(titleTag);
						dominio = dominio.replace("www.", "");
						titleTag = ct.removeSiteName(titleTag,dominio);
						titleTag = ct.replaceLastToken(titleTag);

						titleTag = titleTag.replaceAll("\\s+", " ");
						titleTag = titleTag.trim();


						titleTag = separaPunteggiatura(titleTag,new String[]{",",":",";","?","!","|","\"","(",")"});
						titleTag = titleTag.replaceAll("\\s+", " ");
						titleTag = titleTag.trim();

						//PERSONA TAGGATA
						boolean personaTagg = containsIgnoreCase(titleTag,evento_cantante_giusto);
						//DATA TAGGATA
						boolean dataTagg = new SUTime_Titoli().containsData(titleTag);
						boolean sedeTagg = false;
						boolean cittaTagg = false;
						try {
							String[] sede_citta = luogo_giusto.split(",");
							//trim toglie spazi iniziali e finali
							String sede = sede_citta[0].trim();
							String citta = sede_citta[1].trim();
							//SEDE TAGGATA
							sedeTagg = containsIgnoreCase(titleTag,sede);
							//CITTA' TAGGATA
							cittaTagg = containsIgnoreCase(titleTag,citta);
						}catch(Exception e) {
							System.out.println("ERRORE SEDE_CITTA'");
							e.printStackTrace();
						}

						titleTag = titleTag.trim();

						/*
						 * CREA FILE DI TRAINING
						 * solo se contiene sia PPP sia DDD sia (CCC oppure SSS)
						 */
						
						if(personaTagg && dataTagg && (cittaTagg || sedeTagg)) {
							String[] quadrupla = new String[]{titleTag, evento_cantante_giusto, data_giusta, luogo_giusto};
							if(dominio_dati.containsKey(dominio)) {
								ArrayList<String[]> dati = dominio_dati.get(dominio);
								dati.add(quadrupla);								
							}
							else {
								ArrayList<String[]> dati = new ArrayList<String[]>();
								dati.add(quadrupla);
								dominio_dati.put(dominio, dati);
							}
							
							train++;
							System.out.println("Training numero "+train);

							BasicDBObject document = new BasicDBObject();
							document.put("data", data_giusta);
							document.put("evento_cantante", evento_cantante_giusto);
							document.put("luogo", luogo_giusto);

							document.put("Titolo", title);
							document.put("TitoloTag", titleTag);

							document.put("dominio", dominio);
							document.put("url", url.toString());

							collection.insert(document);
						}
						else {
							k++;
						}
					} catch (Exception e) {
						//System.out.println(e.getMessage());
					}

				}
			}
		}
		System.out.println("buttate: "+k);
		k = 1;
		DBCursor cursor = collection.find();
		while (cursor.hasNext()) {
			System.out.println(k+") "+cursor.next());
			k++;
		}

		System.out.println("Done"+j);
		
		Stampa(dominio_dati);
		
		//Stampa la mappa in un file
		FileMapForTesting.fromMapToText(dominio_dati, "Map2Text.txt");
		
		return dominio_dati;
	}



	private static String Stampa(HashMap<String, ArrayList<String[]>> dominio_dati) {
		String mappa = "";
		for(String key: dominio_dati.keySet()) {
			mappa += key + " --> ";
			int i = 1;
			for(String[] dati: dominio_dati.get(key)) {
				mappa += i+") "+dati[0] + " "+dati[1] + " "+dati[2] + " "+dati[3] + " ";
				i++;
			}
			System.out.println(mappa);
			mappa = "";
		}
		return mappa;
	}

	private static String separaPunteggiatura(String text, String[] punteggiatura) {
		int i=0;
		int j=0;
		char[] caratteri = text.toCharArray();
		while(j<caratteri.length) {
			boolean modificato = false;
			caratteri = text.toCharArray();
			for(j=0;j<caratteri.length&&!modificato;j++) {
				for(i=0;i<punteggiatura.length;i++) {
					if(caratteri[j] == (punteggiatura[i].charAt(0))) {
						if(j!=caratteri.length-1 && caratteri[j+1]!=' ') {
							text = text.substring(0,j+1) + " " + text.substring(j+1);
							modificato = true;
						}
						if(j!=0 && caratteri[j-1]!=' ') {
							text = text.substring(0,j) + " " + text.substring(j);
							modificato = true;
						}
					}
				}
			}
		}
		return text;
	}



	private static boolean containsIgnoreCase(String testo, String testo_da_cercare) {
		String[] parole = testo.split(" ");
		String[] parole_da_cercare = testo_da_cercare.split(" ");

		int i,j;
		for(i=0;i<parole.length;i++) {
			Boolean sbagliato = false;
			if(parole[i].equalsIgnoreCase(parole_da_cercare[0])){
				for(j=1;j<parole_da_cercare.length&&!sbagliato;j++) {
					if((i+j)<parole.length) {
						if(!parole[i+j].equalsIgnoreCase(parole_da_cercare[j])){
							sbagliato = true;
						}
					}
					else
						sbagliato = true;
				}
				if(!sbagliato && j == parole_da_cercare.length) {
					return true;
				}
			}
		}
		return false;
	}
}