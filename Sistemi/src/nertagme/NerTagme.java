package nertagme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tagMe.Parser;
import tagMe.Tagger;
import namedEntityRecognizer.NamedEntityRecognizerTest;

public class NerTagme {
	NamedEntityRecognizerTest ner = new NamedEntityRecognizerTest();


	public String[] nerCompareToTagme(HashMap<String,Integer> nerLuoghiMap, HashMap<String,Integer> nerPersoneMap, HashMap<String,Integer> tagmePersoneMap,
			HashMap<String,Integer> tagmeLuoghiMap, HashMap<String, Integer> tagmeSediMap, String[] datiTopCR) {

		String[] result = new String[2];

		String luogoTopLM = ner.entityTop(nerLuoghiMap);
		String personaTopLM = ner.entityTop(nerPersoneMap);

		String cittaTopCR = datiTopCR[1];
		String personaTopCR = datiTopCR[0];
		String sedeTopCR = datiTopCR[2];

		String luogoTopCR = cittaTopCR + " " + sedeTopCR;



		if (  (personaTopLM == null || personaTopLM.equals("")) && (personaTopCR != null && !personaTopCR.equals(""))  ) {
			result[0] = personaTopCR;
		}
		else if ( (personaTopCR == null || personaTopCR.equals("")) && (personaTopLM != null && !personaTopLM.equals("")) ) {
			result[0] = personaTopLM;
		}
		else if ( (personaTopCR == null || personaTopCR.equals("")) && (personaTopLM == null && personaTopLM.equals("")) ) {
			result[0] = "";
		}
		else {
			if (personaTopLM.equals(personaTopCR)) {
				result[0] = personaTopLM;
			}
			else {
				result[0] = valutaPersonaTop(personaTopLM, personaTopCR, nerPersoneMap, tagmePersoneMap);
			}
		}



		if (  (luogoTopLM == null || luogoTopLM.equals("")) && (luogoTopCR != null && !luogoTopCR.equals(""))  ) {
			result[1] = luogoTopCR;
		}
		else if ( (luogoTopCR == null || luogoTopCR.equals("")) && (luogoTopLM != null && !luogoTopLM.equals("")) ) {
			result[1] = luogoTopLM;
		}
		else if ( (luogoTopCR == null || luogoTopCR.equals("")) && (luogoTopLM == null && luogoTopLM.equals("")) ) {
			result[1] = "";
		}

		else {
			if ( luogoTopCR.contains(luogoTopLM) ) {
				result[1] = luogoTopCR;
			}
			else {
				result[1] = valutaLuogoTop(luogoTopLM, cittaTopCR, sedeTopCR, nerLuoghiMap, tagmeLuoghiMap, tagmeSediMap);
			}
		}


		return result;


	}


	private String valutaLuogoTop(String luogoTopLM, String cittaTopCR, String sedeTopCR, HashMap<String, Integer> nerLuoghiMap, 
			HashMap<String, Integer> tagmeLuoghiMap, HashMap<String, Integer> tagmeSediMap) {
		String locationTop = "";

		int percentualeLM = stimaPercentuale(luogoTopLM, nerLuoghiMap);
		int percentualeCittaCR = stimaPercentuale(cittaTopCR, tagmeLuoghiMap);
		int percentualeSedeCR = stimaPercentuale(sedeTopCR, tagmeSediMap);

		if ( luogoTopLM.equals(cittaTopCR) ) {
			if (percentualeLM >= percentualeCittaCR) {
				locationTop = luogoTopLM + " "+ sedeTopCR;
			}
			else {
				locationTop = cittaTopCR + " " + sedeTopCR;
			}

		}

		else if ( luogoTopLM.equals(sedeTopCR) ) {
			if (percentualeLM >= percentualeSedeCR) {
				locationTop = cittaTopCR + " "+ luogoTopLM;
			}
			else {
				locationTop = cittaTopCR + " " + sedeTopCR;
			}

		}

		else {
			if (percentualeLM > percentualeCittaCR) {
				if (percentualeLM > percentualeSedeCR) {
					locationTop = luogoTopLM;
				}
				else {
					locationTop = cittaTopCR + " " + sedeTopCR;
				}
			}
			else {
				locationTop = cittaTopCR + " " + sedeTopCR;
			}
		}
		return locationTop;
	}


	private String valutaPersonaTop(String personaTopLM, String personaTopCR, HashMap<String, Integer> nerPersoneMap, HashMap<String, Integer> tagmePersoneMap) {
		String personTop = "";

		int percentualeLM = stimaPercentuale(personaTopLM, nerPersoneMap);
		int percentualeCR = stimaPercentuale(personaTopCR, tagmePersoneMap);
		if (percentualeLM >= percentualeCR) {
			personTop = personaTopLM;
		}
		else {
			personTop = personaTopCR;
		}

		return personTop;
	}


	private int stimaPercentuale(String entityTop, HashMap<String, Integer> entitiesMap) {
		int x = 0;
		int somma = 0;
		for (int valore: entitiesMap.values()) {
			somma = somma + valore;
		}
		if ( entityTop != null && !entityTop.equals("") ) {
			int valorePersonaTop = entitiesMap.get(entityTop);
			x = (100*valorePersonaTop) / somma;
		}
		return x;
	}


	public static String[] ritorna (String[] site) throws Exception {
		String title = site[0];
		String text = site[1];

		NamedEntityRecognizerTest ner = new NamedEntityRecognizerTest();
		NerTagme nertagme = new NerTagme();
		ArrayList<HashMap<String,Integer>> lista = ner.createListOfMapEntity(site); //restituisce la lista di mappe
		HashMap<String,Integer> locations = lista.get(0); // la mappa dei luoghi
		HashMap<String,Integer> people = lista.get(1); // la mappa delle persone-eventi

		String luogoTop = ner.entityTop(locations);
		String personaTop = ner.entityTop(people);

		String[] result = {personaTop, luogoTop};

		/*
		System.out.println("LMLMLMLMLM IL LUOGO PROPOSTO E': "+ luogoTop);

		System.out.println("LMLMLMLMLM LA PERSONA PROPOSTA E': "+ personaTop);
		System.out.println("TITOLO "+title);

		//tagme

		System.out.println("PARTE DI TAGME");
		*/

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

				String[] datiPropostiText = parser.choiceDataProposals(mapPText, mapCText, mapVText);

				String[] datiPropostiFinali = tagMe.getTagMeFinalProposedData(datiPropostiTitle,datiPropostiText,mapPTitle,mapCTitle,mapVTitle,mapPText,mapCText,mapVText);
				/*
				System.out.println("\n=== Dati proposti FINALI ===");
				System.out.println("CRCRCRCR PERSONA: "+datiPropostiFinali[0]);
				System.out.println("CRCRCRCR CITTA': "+datiPropostiFinali[1]);
				System.out.println("CRCRCRCR SEDE: "+datiPropostiFinali[2]);
				*/
				HashMap<String,Integer> mapPFinal = parser.mergeMap(mapPTitle,mapPText);
				HashMap<String,Integer> mapCFinal = parser.mergeMap(mapCTitle,mapCText);
				HashMap<String,Integer> mapVFinal = parser.mergeMap(mapVTitle,mapVText);


				result = nertagme.nerCompareToTagme(locations, people, mapPFinal, mapCFinal, mapVFinal, datiPropostiFinali);
			}
		}

		return result;

	}

}
