package main;

import hmm.Parser;

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
			"- StubHub UK","- StubHub UK!","– Last.fm", "– Last.fm", "at Last.fm", "@ TicketHold","@ Ultimate-Guitar.Com",
			"at Last.fm","Stereoboard", "ConcertWith.Me", "NaviHotels.com", "Heyevent.com", "Friendfeed", "setlist.fm",
			"Getty Images", "TicketNetwork", "www.floramc.org", "rmalife.net", "Gumtree", "Seatwave.com",
			"– Songkick", "The sound of summer", "504ever.net", "| Concertful", "StubHub UK!", "YouPict", 
			"- 5gig.com","5gig.co.uk", "mxdwn.com", "Thrillcall", "Kililive.com", "| Bandsintown", "MASS EDMC", 
			"| Nerds Attack!", "Plannify", "BoxOffice Lazio", "| Ticketfly", "| CheapTickets.com",
			"| MASS EDMC", "| Kililive.com", "| setlist.fm", " - Stereoboard", "SoundCrashMusic", "| SoundCrashMusic",
			"TicketsInventory Mobile", "- backpage.com", "from Bandsintown", "| ConcertBank.com", "| clubZone", "- univision.com",
			"- Wikipedia, the free encyclopedia", "| Eventful","| SeatGeek","| Eventsfy","__ Last.fm"," Setlist ","__ Songkick"));

	public final static int numero_query = 5;
	//public final static String[] CITTA = {"Roma","Londra","New York","Los Angeles","Stoccolma","Parigi","Helsinki","Canberra","Chicago","Austin"};
	public final static String[] CITTA = {"Amsterdam","Liverpool","Boston","Detroit","Dublino","Berlino","Oslo","Sydney","Philadelphia","Las Vegas"};

	public static void main(String[] args) {
		int i;
		int j = 0;
		int train = 0;

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
						titleTag = titleTag.replace("  ", " ");
						SUTime_Titoli SUTT = new SUTime_Titoli();
						//DATA TAGGATA
						titleTag = SUTT.getTextTag(titleTag);

						//RIMUOVI SITI
						for(String sito: STOP_SITE){
							titleTag = titleTag.replace(sito, "");
						}
						//trim toglie spazi iniziali e finali
						titleTag.trim();

						titleTag = separaPunteggiatura(titleTag,new String[]{",",":",";",".","?","!","|","\""});
						titleTag = titleTag.replace(",", " ,");
						titleTag = titleTag.replace("  ", " ");

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
						ArrayList<String> listaSepa = new ArrayList<String>(Arrays.asList("|",",","–","-",":"));
						titleTag = insertTag(titleTag,listaSepa,"SEPA");

						titleTag = insertTag(titleTag,"SEPA#| twitter", "SOCIAL");
						titleTag = insertTag(titleTag,"on twitter", "SOCIAL");

						titleTag = insertTag(titleTag,"(@", "PREP");

						ArrayList<String> listaAAA = new ArrayList<String>(Arrays.asList("@","at","in"));
						titleTag = insertTag(titleTag,listaAAA,"AAA");

						titleTag = insertTag(titleTag,"tickets & tour dates", "POSTP");

						titleTag = insertTag(titleTag,"on", "PRED");
						/*
						titleTag = insertTag(titleTag,"concert tickets", "MMM");
						titleTag = insertTag(titleTag,"concert dates", "MMM");
						titleTag = insertTag(titleTag,"tour dates", "MMM");
						 */

						String dominio = urlString.split("/")[2];

						//rimuove il nome del sito dal titolo
						titleTag = removeSiteName(titleTag,dominio);

						titleTag = insertTag(titleTag,"tickets for sale", "SELL");
						titleTag = insertTag(titleTag,"concert tickets", "SELL");
						ArrayList<String> listaSELL = new ArrayList<String>(Arrays.asList("tickets","ticket","sale","sales"));
						titleTag = insertTag(titleTag,listaSELL,"SELL");

						ArrayList<String> listaCONCERTO = new ArrayList<String>(Arrays.asList("concerts","concert","tour","dates","events","event","announced","show","live","calendar","schedule"));
						titleTag = insertTag(titleTag,listaCONCERTO,"CONCERT");

						ArrayList<String> listaMMM = new ArrayList<String>(Arrays.asList("lyrics","listing","music","dance","reviews","voices","opera","ballet","theatre"));
						titleTag = insertTag(titleTag,listaMMM,"MUSIC");

						ArrayList<String> listaET = new ArrayList<String>(Arrays.asList("&","and","feat"));
						titleTag = insertTag(titleTag,listaET,"ET");

						ArrayList<String> listaART = new ArrayList<String>(Arrays.asList("the","an","a"));
						titleTag = insertTag(titleTag,listaART,"ART");

						/*
						ArrayList<String> listaBAD = new ArrayList<String>(Arrays.asList("cancelled","homepage","forums","album","weather"));
						titleTag = insertTag(titleTag,listaBAD,"BAD");
						 */
						
						//toglie eventuali SEPA/AAA/from/PRED finali 
						String[] titleSplit = titleTag.split(" ");
						int titleSplitLength = titleSplit.length;
						String lastToken = titleSplit[titleSplitLength-1];
						//System.out.println("LastToken: "+lastToken+" del titolo: "+titleTag);
						if(lastToken.contains("SEPA") || lastToken.contains("AAA") || lastToken.contains("from") || lastToken.contains("PRED")) {
							titleTag = replaceLast(titleTag, lastToken, "");
						}

						//toglie spazi finali e iniziali
						titleTag = titleTag.trim();

						titleTag = taggaAltro(titleTag,"ALTRO");

						titleTag = titleTag.trim();

						/*
						 * CREA FILE DI TRAINING
						 * solo se contiene sia PPP sia DDD sia (CCC oppure SSS)
						 */
						if(contieneDati(titleTag)) {
							train++;
							System.out.println("Training numero "+train);
							Parser.parserForTitle(titleTag);
						}


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




	private static boolean contieneDati(String testo) {
		if(testo.contains("PPP#") && testo.contains("DDD#") && 
				(testo.contains("CCC#") || testo.contains("SSS#")) )
			return true;
		return false;
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

	/*
	 * Metodo di supporto per la rimozione del nome del sito all'interno del titolo
	 */
	private static String removeSiteName(String title, String domain) {
		domain = domain.replace("www.", "");
		String[] titleSplit = title.split(" ");
		String temp;
		String tokenPrec="";
		for (int i=0; i<titleSplit.length;i++) {
			temp = titleSplit[i].toLowerCase();
			if(temp.contains(".com") || temp.contains(".fm") || temp.contains(".org") || temp.contains(".net")) {
				//System.out.println("Nel titolo "+title+" esiste un .qualcosa da eliminare");
				title = title.replaceFirst(titleSplit[i], "");
				//System.out.println("Eliminazione di ["+titleSplit[i]+"]: "+title);
				if(tokenPrec.contains("SEPA") || tokenPrec.contains("AAA") || tokenPrec.contains("from")) {
					title = replaceLast(title, tokenPrec, "");
				}
			}
			else if(domain.contains(temp)) {
				title = title.replaceFirst(titleSplit[i], "");
				if(tokenPrec.contains("SEPA") || tokenPrec.contains("AAA") || tokenPrec.contains("from")) {
					title = replaceLast(title, tokenPrec, "");
				}
			}
			tokenPrec = titleSplit[i];
		}
		return title;
	}

	/*
	 * metodo di supporto per la rimozione dell'ultima occorrenza del contenuto da una stringa
	 */
	public static String replaceLast(String string, String toReplace, String replacement) {
		int pos = string.lastIndexOf(toReplace);
		if (pos > -1) {
			return string.substring(0, pos)
					+ replacement
					+ string.substring(pos + toReplace.length(), string.length());
		} else {
			return string;
		}
	}
}
