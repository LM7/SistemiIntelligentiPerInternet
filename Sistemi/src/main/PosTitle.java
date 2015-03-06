package main;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

public class PosTitle {

	public final static HashSet<String> STOP_SITE = new HashSet<String>(Arrays.asList("on StubHub!", 
			"- StubHub UK","- StubHub UK!","– Last.fm", "— Last.fm", "at Last.fm", "@ TicketHold","@ Ultimate-Guitar.Com",
			"at Last.fm","Stereoboard", "www.floramc.org", "rmalife.net", "Gumtree", "Seatwave.com",
			"– Songkick", "The sound of summer", "504ever.net", "Concertful", "StubHub UK!", "YouPict", 
			"- 5gig.com","5gig.co.uk", "mxdwn.com", "Thrillcall", "Kililive.com", "| Bandsintown", "MASS EDMC", 
			"Nerds Attack!", "Plannify", "BoxOffice Lazio", "ConcertWith.Me", "NaviHotels.com", 
			"Heyevent.com", "Friendfeed", "setlist.fm", "Getty Images", "TicketNetwork", "| Ticketfly",
			"| CheapTickets.com"));

	public final static int numero_query = 1;
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
						SUTime_Titoli SUTT = new SUTime_Titoli();
						//DATA TAGGATA
						titleTag = SUTT.getTextTag(titleTag);

						//RIMUOVI SITI
						for(String sito: STOP_SITE){
							titleTag = titleTag.replace(sito, "");
						}
						//trim toglie spazi iniziali e finali
						titleTag.trim();



						//PERSONA TAGGATA
						titleTag = insertTag(titleTag,evento_cantante_giusto, "PPP");

						try {
							String[] sede_citta = luogo_giusto.split(",");
							//trim toglie spazi iniziali e finali
							String sede = sede_citta[0].trim();
							String citta = sede_citta[1].trim();
							//SEDE TAGGATA
							titleTag = insertTag(titleTag,sede, "SSS");
							//CITTA' TAGGATA
							titleTag = insertTag(titleTag,citta, "CCC");
						}catch(Exception e) {
							System.out.println("ERRORE SEDE_CITTA'");
							e.printStackTrace();
						}

						//ALTRI TAG
						titleTag = titleTag.replace(",", " ,");
						titleTag = titleTag.replace("  ", " ");
						ArrayList<String> listaSepa = new ArrayList<String>(Arrays.asList("|",",","–","-"));
						titleTag = insertTag(titleTag,listaSepa,"SEPA");

						titleTag = insertTag(titleTag,"SEPA#| twitter", "SOCIAL");
						titleTag = insertTag(titleTag,"on twitter", "SOCIAL");

						titleTag = insertTag(titleTag,"(@", "PREP");

						ArrayList<String> listaAAA = new ArrayList<String>(Arrays.asList("@","at"));
						titleTag = insertTag(titleTag,listaAAA,"AAA");

						titleTag = insertTag(titleTag,"tickets & tour dates", "POSTP");

						/*
						titleTag = insertTag(titleTag,"concert tickets", "MMM");
						titleTag = insertTag(titleTag,"concert dates", "MMM");
						titleTag = insertTag(titleTag,"tour dates", "MMM");
						 */
						ArrayList<String> listaMMM = new ArrayList<String>(Arrays.asList("concerts","concert","tickets","ticket","tour","dates"));
						titleTag = insertTag(titleTag,listaMMM,"MMM");

						//toglie spazi finali e iniziali
						titleTag = titleTag.trim();

						titleTag = taggaAltro(titleTag,"ALTRO");

						titleTag = titleTag.trim();



						String dominio = urlString.split("/")[2];

						BasicDBObject document = new BasicDBObject();
						document.put("data", data_giusta);
						document.put("evento_cantante", evento_cantante_giusto);
						document.put("luogo", luogo_giusto);

						document.put("Titolo", title);
						document.put("TitoloTag", titleTag);

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




	private static String taggaAltro(String testo, String tag) {
		String[] parole = testo.split(" ");
		int i;
		for(i=0;i<parole.length;i++) {
			if(!parole[i].contains("#"))
				parole[i] = tag+"#"+parole[i];
		}
		return toStringa(parole);
	}




	private static String insertTag(String testo, String stringaDaTaggare, String tag) {
		String[] parole = testo.split(" ");
		String[] appoggio = parole.clone();
		String[] cantante_luogo = stringaDaTaggare.split(" ");
		int i,j;
		for(i=0;i<parole.length;i++) {
			Boolean sbagliato = false;
			if(parole[i].equalsIgnoreCase(cantante_luogo[0])){
				appoggio[i] = tag+"#"+parole[i];
				for(j=1;j<cantante_luogo.length&&!sbagliato;j++) {
					if((i+j)<parole.length) {
						if(parole[i+j].equalsIgnoreCase(cantante_luogo[j])){
							appoggio[i+j] = tag+"#"+parole[i+j];
						}
						else
							sbagliato = true;
					}
					else
						sbagliato = true;
				}
				if (sbagliato) {
					appoggio = parole.clone();
				}
				else if(j == cantante_luogo.length) {
					testo = toStringa(appoggio);
				}
			}
		}
		return testo;
	}

	private static String insertTag(String testo, ArrayList<String> lista, String tag) {
		int i;
		String[] parole = testo.split(" ");
		for(i=0;i<parole.length;i++) {
			if(containsCaseInsensitive(parole[i],lista)) {
				parole[i] = tag+"#"+parole[i];
			}
		}
		return toStringa(parole);
	}


	private static String toStringa(String[] parole) {
		String text = "";
		int i;
		for(i=0;i<parole.length-1;i++)
			text += parole[i]+" ";
		text += parole[parole.length-1];
		return text;
	}

	private static boolean containsCaseInsensitive(String strToCompare, ArrayList<String> list)
	{
		for(String str:list)
		{
			if(str.equalsIgnoreCase(strToCompare))
			{
				return true;
			}
		}
		return false;
	}
}
