package main;

import java.util.HashMap;

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

}
