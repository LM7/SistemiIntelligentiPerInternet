package main;

import java.util.HashMap;

import namedEntityRecognizer.NamedEntityRecognizerTest;

public class NerTagme {
	NamedEntityRecognizerTest ner = new NamedEntityRecognizerTest();
	//da cambiare
	
	
	public boolean nerCompareToTagme(HashMap<String,Integer> nerLuoghiMap, HashMap<String,Integer> nerPersoneMap, HashMap<String,Integer> tagmeMap) {
		String luogoTop = ner.entityTop(nerLuoghiMap);
		String personaTop = ner.entityTop(nerPersoneMap);
		
		for (String parola: tagmeMap.keySet()) {
			if (luogoTop.equals(parola)) {
				return true;
			}
			if (personaTop.equals(parola)) {
				return true;
			}
		}
		
		
		
		return false;
		
		
		
	}

}
