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


/*
 * Crea il file training.brown che e' un file di titolo taggati che puo' essere
 * utilizzato come modello di training. Inoltre questi titoli vengono salvati nel DB.
 * 
 * Numero di titoli cercati = numero_query * CITTA.length * NUMEROEVENTI(contenuto nella classe 
 * geoMethods nel package lastFM)
 * Numero di titolo inseriti nel training = tutti i titoli cercati che contengono
 * sia il cantante corretto sia (la citta' o la sede) corretta sia una data
 */
public class PosTitle {

	public final static int numero_query = 5;
	public final static String[] CITTA = {"Roma","London","New York","Los Angeles","Stoccolma","Paris","Helsinki","Canberra","Chicago","Austin"};
	//public final static String[] CITTA = {"Amsterdam","Liverpool","Boston","Detroit","Dublino"};

	public static void main(String[] args) {
		int i;
		int j = 0;
		int k = 0;
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
						titleTag = titleTag.replaceAll("\\s+", " ");
						titleTag = titleTag.trim();


						//RIMUOVI SITI
						HashSet<String> STOP_SITE = CleanTitle.STOP_SITE;
						for(String sito: STOP_SITE){
							titleTag = titleTag.replace(sito, "");
						}
						//trim toglie spazi iniziali e finali
						titleTag = titleTag.trim();

						String dominio = urlString.split("/")[2];
						CleanTitle ct = new CleanTitle(titleTag);
						dominio = dominio.replace("www.", "");
						titleTag = ct.removeSiteName(titleTag,dominio);
						titleTag = ct.replaceLastToken(titleTag);

						titleTag = titleTag.replaceAll("\\s+", " ");
						titleTag = titleTag.trim();


						SUTime_Titoli SUTT = new SUTime_Titoli();
						//DATA TAGGATA
						titleTag = SUTT.getTextTag(titleTag);

						titleTag = separaPunteggiatura(titleTag,new String[]{",",":",";","?","!","|","\"","(",")"});

						titleTag = titleTag.replaceAll("\\s+", " ");
						titleTag = titleTag.trim();

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

						//TAG PUNTEGGIATURA
						titleTag = insertTag(titleTag,"\"","\"");
						titleTag = insertTag(titleTag,"?","?");
						titleTag = insertTag(titleTag,"!","!");
						titleTag = insertTag(titleTag,"|","|");
						titleTag = insertTag(titleTag,",",",");
						titleTag = insertTag(titleTag,"-","-");
						titleTag = insertTag(titleTag,"–","–");
						titleTag = insertTag(titleTag,":",":");
						titleTag = insertTag(titleTag,";",";");
						titleTag = insertTag(titleTag,"(","(");
						titleTag = insertTag(titleTag,")",")");


						//ALTRI TAG
						titleTag = insertTag(titleTag,"|#| twitter", "SOCIAL");
						titleTag = insertTag(titleTag,"on twitter", "SOCIAL");

						ArrayList<String> listaAAA = new ArrayList<String>(Arrays.asList("@","at","in"));
						titleTag = insertTag(titleTag,listaAAA,"AAA");

						titleTag = insertTag(titleTag,"tickets & tour dates", "POSTP");

						titleTag = insertTag(titleTag,"on", "PRED");

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

						//toglie spazi finali e iniziali
						titleTag = titleTag.trim();
						titleTag = titleTag.replaceAll("\\s+", " ");

						titleTag = taggaAltro(titleTag,"ALTRO");

						titleTag = titleTag.trim();

						/*
						 * CREA FILE DI TRAINING
						 * solo se contiene sia PPP sia DDD sia (CCC oppure SSS)
						 */

						if(contieneDati(titleTag)) {
							train++;
							System.out.println("Training numero "+train);
							Parser.parserForBrownTitle(titleTag);

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
			if(parole[i].equals(" "))
				parole[i] = "";
			else if(!parole[i].contains("#") && containsCaseInsensitive(parole[i],lista))
				parole[i] = tag+"#"+parole[i];
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
