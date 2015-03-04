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
	
	public final static HashSet<String> STOP_SITE = new HashSet<String>(Arrays.asList("on StubHub!", 
			"- StubHub UK","- StubHub UK!","� Last.fm", "� Last.fm", "at Last.fm", "@ TicketHold","@ Ultimate-Guitar.Com",
			"at Last.fm","Stereoboard", "www.floramc.org", "rmalife.net", "Gumtree", "Seatwave.com",
			"� Songkick", "The sound of summer", "504ever.net", "Concertful", "StubHub UK!", "YouPict", 
			"- 5gig.com","5gig.co.uk", "mxdwn.com", "Thrillcall", "Kililive.com", "| Bandsintown", "MASS EDMC", 
			"Nerds Attack!", "Plannify", "BoxOffice Lazio", "ConcertWith.Me", "NaviHotels.com", 
			"Heyevent.com", "Friendfeed", "setlist.fm", "Getty Images", "TicketNetwork", "| Ticketfly",
			"| CheapTickets.com"));

	public final static int numero_query = 2;
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

						String titleTag = title.toLowerCase();
						SUTime_Titoli SUTT = new SUTime_Titoli();
						//DATA TAGGATA
						titleTag = SUTT.getTextTag(titleTag);
						
						//RIMUOVI SITI
						for(String sito: STOP_SITE){
							titleTag = titleTag.replace(sito.toLowerCase(), "");
						}
						
						//PERSONA TAGGATA
						titleTag = titleTag.replace(evento_cantante_giusto.toLowerCase(), "PPP");

						try {
							String[] sede_citta = luogo_giusto.split(",");
							//trim toglie spazi iniziali e finali
							String sede = sede_citta[0].trim();
							String citta = sede_citta[1].trim();
							//SEDE TAGGATA
							titleTag = titleTag.replace(sede.toLowerCase(), "SSS");
							//CITTA' TAGGATA
							titleTag = titleTag.replace(citta.toLowerCase(), "CCC");
						}catch(Exception e) {
							System.out.println("ERRORE SEDE_CITTA'");
							e.printStackTrace();
						}
						
						//ALTRI TAG
						titleTag = titleTag.replace("|", "SEPA");
						titleTag = titleTag.replaceAll(",", " SEPA");
						titleTag = titleTag.replace("�", "SEPA");
						titleTag = titleTag.replace("-", "SEPA");
						
						titleTag = titleTag.replace("SEPA twitter", "SOCIAL");
						titleTag = titleTag.replace("on twitter", "SOCIAL");
						titleTag = titleTag.replace("(@", "PREP");
						
						titleTag = titleTag.replace("@", "AAA");
						titleTag = titleTag.replace(" at ", " AAA ");
						
						titleTag = titleTag.replace("tickets & tour dates", "POSTP");
						
						titleTag = titleTag.replace(" concert tickets ", " MMM ");
						titleTag = titleTag.replace(" concert dates ", " MMM ");
						titleTag = titleTag.replace(" concerts ", " MMM ");
						titleTag = titleTag.replace(" concert ", " MMM ");
						titleTag = titleTag.replace(" tickets ", " MMM ");
						titleTag = titleTag.replace(" tickets ", " MMM ");
						titleTag = titleTag.replace(" ticket ", " MMM ");
						titleTag = titleTag.replace(" tour dates ", " MMM ");
						titleTag = titleTag.replace(" tour ", " MMM ");
						titleTag = titleTag.replace(" dates ", " MMM ");
						
						
						//toglie spazi finali e iniziali
						titleTag = titleTag.trim();
						
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