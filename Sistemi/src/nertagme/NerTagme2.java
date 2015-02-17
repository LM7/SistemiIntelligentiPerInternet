package nertagme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import namedEntityRecognizer.NamedEntityRecognizerTest;
import tagMe.Parser;
import tagMe.Tagger;

public class NerTagme2 {


	public static List<HashMap<String,Integer>> ritorna (String[] site) throws Exception {
		String title = site[0];
		String text = site[1];
		
		ArrayList<HashMap<String,Integer>> resultList = new ArrayList<HashMap<String,Integer>>();

		NamedEntityRecognizerTest ner = new NamedEntityRecognizerTest();
		NerTagme2 nertagme2 = new NerTagme2();
		ArrayList<HashMap<String,Integer>> lista = ner.createListOfMapEntity(site); //restituisce la lista di mappe
		HashMap<String,Integer> locations = lista.get(0); // la mappa dei luoghi
		HashMap<String,Integer> people = lista.get(1); // la mappa delle persone-eventi


		Tagger tagMe = new Tagger();
		Parser parser = new Parser();



		List<HashMap<String,Integer>> listaMappeTitleRaw = tagMe.getTagMePartialProposedData(title);
		if ( listaMappeTitleRaw != null  ) {


			HashMap<String,Integer> mapPTitleRaw = listaMappeTitleRaw.get(0);
			HashMap<String,Integer> mapCTitleRaw = listaMappeTitleRaw.get(1);
			HashMap<String,Integer> mapVTitleRaw = listaMappeTitleRaw.get(2);

			HashMap<String,Integer> mapPTitle = parser.addValueTitle(mapPTitleRaw);
			HashMap<String,Integer> mapCTitle = parser.addValueTitle(mapCTitleRaw);
			HashMap<String,Integer> mapVTitle = parser.addValueTitle(mapVTitleRaw);

			String[] datiPropostiTitle = parser.choiceDataProposals(mapPTitle, mapCTitle, mapVTitle);

			List<HashMap<String,Integer>> listaMappeText = tagMe.getTagMePartialProposedData(text);
			if ( listaMappeText != null  ) {
				HashMap<String,Integer> mapPText = listaMappeText.get(0);
				HashMap<String,Integer> mapCText = listaMappeText.get(1);
				HashMap<String,Integer> mapVText = listaMappeText.get(2);

				

				
				HashMap<String,Integer> mapPFinal = parser.mergeMap(mapPTitle,mapPText);
				HashMap<String,Integer> mapCFinal = parser.mergeMap(mapCTitle,mapCText);
				HashMap<String,Integer> mapVFinal = parser.mergeMap(mapVTitle,mapVText);


				
				HashMap<String,Integer> persone = nertagme2.unisciPersone(people, mapPFinal);
				HashMap<String,Integer> luoghi = nertagme2.unisciLuoghi(locations, mapCFinal, mapVFinal);
				
				resultList.add(0, persone);
				resultList.add(1, luoghi);
			}
		}
		
		
	
		return resultList;

	}

	private HashMap<String, Integer> unisciLuoghi( HashMap<String, Integer> locations, HashMap<String, Integer> mapCFinal, HashMap<String, Integer> mapVFinal) {
		HashMap<String,Integer> mapFinale = new HashMap<String,Integer>();
		mapFinale.putAll(mapVFinal);
		mapFinale.putAll(mapCFinal);
		mapFinale.putAll(locations);
		return mapFinale;
	}

	private HashMap<String, Integer> unisciPersone(HashMap<String, Integer> people, HashMap<String, Integer> mapPFinal) {
		HashMap<String,Integer> mapFinale = new HashMap<String,Integer>();
		mapFinale.putAll(mapPFinal);
		mapFinale.putAll(people);
		return mapFinale;
	}


}
