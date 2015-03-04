package tagMe;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import boilerpipe.Boilerpipe;
import events.MsnSearchEngine;

public class MainTestForFeature {

	public final static int numero_query = 1;

	public static void main(String[] args) throws Exception {
		String data = "10 July 2015";
		String evento_cantante = "Aerosmith";
		String luogo = "";

		String title = "";
		String text = "";

		MsnSearchEngine se = new MsnSearchEngine();
		String[] urls = se.getUrls(data+" "+evento_cantante+" "+luogo, numero_query);
		for(String s: urls) {
			//Boilerpipe
			Boilerpipe b = new Boilerpipe(3);
			URL url = null;
			try {
				url = new URL(s);
				System.out.println("URL: "+s);
				String[] site = b.getText(url);
				title = site[0];
				text = site[1];

				TaggerNuova tagMe = new TaggerNuova();
				/* list è una lista di mappe composte come segue:
				 * list.get(0) è la mappa delle sedi, list.get(1) è la mappa delle citta', list.get(2) è la mappa delle persone
				 * ogni mappa è composta dalla parola chiave e un array di Integer composto come segue:
				 * int[0]: presenza nel titolo (-1,1); int[1]: numero di occorrenze
				 */ 
				List<HashMap<String, int[]>> list = tagMe.getTagMeData(title, text);
				for(HashMap<String, int[]> map : list) {
					printMap(map);
				}

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	/*
	 * Stampa una mappa
	 */
	@SuppressWarnings("rawtypes")
	public static void printMap(HashMap<String, int[]> map) {
		Iterator iterator = map.keySet().iterator();	  
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			int[] values = map.get(key);	  
			System.out.println("Parola: "+key+" | Titolo: "+values[0]+" | #:"+values[1]);
		}
	}
}
