package tagMe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;

public class Parser {
	String result;
	Reply reply;
	Gson gson; 
	HashMap<String, Integer> occurrencesCount;

	public Parser(String result) {
		this.result=result;
		this.gson=new Gson();
		this.reply=new Reply();
		this.occurrencesCount  = new HashMap<String, Integer>();
	}

	public Parser() {
		// TODO Auto-generated constructor stub
	}

	public String getReplyTagMe() {
		return result;
	}

	public void setReplyTagMe(String result) {
		this.result = result;
	}

	/* Processamento della risposta di tagMe 
	 * restituisce una mappa composta da String (chiave) e lista di Stringhe (valore) 
	 * (che rappresentano ciascuna parola con le rispettive categorie)
	 *  e aggiorna una mappa composta da String (chiave) e Integer (valore) 
	 *  (che rappresentano ciascuna parola con le rispettive occorrenze nel testo)
	 */
	public HashMap<String, List<String>> processingReply() {
		HashMap<String, List<String>> tagMeResult = new HashMap<String, List<String>>();
		//HashMap<String, Integer> occurrencesCount  = new HashMap<String, Integer>();
		reply = gson.fromJson(result,Reply.class);
		List<Annotation> annotations = reply.getAnnotations();
		int zeroCategorie=0;
		//System.out.println("ANNOTATIONS: \nsize: "+annotations.size()+"\n");
		String word="";
		for (Annotation a : annotations) {	
			//System.out.println("Spot: "+a.getSpot()+"\n(Wikipedia) Title: "+a.getTitle()+"\nCategories: "+a.getDbpediaCategories());
			//System.out.println("#categories: "+a.getDbpediaCategories().size());
			//System.out.println();
			if(a.getDbpediaCategories().size()==0) {
				zeroCategorie++;
			}
			else{
				word=a.getSpot();
				if(tagMeResult.containsKey(word)) {
					int oldOccorence = occurrencesCount.get(word);
					occurrencesCount.put(word, oldOccorence+1);
				}
				else {
					tagMeResult.put(word, a.getDbpediaCategories());
					occurrencesCount.put(word, 1);
				}

			}
		}
		//System.out.println("Parole taggate escluse dall'analisi: "+zeroCategorie);
		//System.out.println("\nOCCORRENZE: "+occurrencesCount);
		return tagMeResult;
	}

	
	/*Data una lista di topWord e una mappa composta da String (chiave) e lista di Stringhe (valore) 
	 * (che rappresentano ciascuna parola con le rispettive categorie)
	 *  restituisce una mappa che associa a ciascuna parola, il numero di categorie associate che contengono topWord
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<String,Integer> filterCategories(HashMap<String,List<String>> wordCategories, List<String> topWordList) {
		HashMap<String,Integer> wordResult = new HashMap<String,Integer>();
		Iterator iterator = wordCategories.keySet().iterator();	  
		while (iterator.hasNext()) {
			String title = iterator.next().toString();
			//System.out.println("\n\n========= ANALISI CATEGORIE PER LA PAROLA TAGGATA: "+title+" =========");
			List<String> categories = wordCategories.get(title);	
			for (String category: categories) {
				category = category.toLowerCase();
				//System.out.println("\n *** Considero la categoria: "+category+" ***");
				for (String word : topWordList) {
					//System.out.println("Verifico se la topWord: \""+word+"\" e' contenuta nella categoria: \""+category+"\"");
					if (category.indexOf(word) != -1) {
						if(wordResult.containsKey(title)) {
							wordResult.put(title, wordResult.get(title)+1);
						}
						else {
							wordResult.put(title, 1);
						}
					}
				}
			}
		}
		HashMap<String,Integer> finalValue = addValuesOccurrences(wordResult);
		return finalValue;
	}

	
	/* Data una mappa composta da String (chiave) e Integer (valore)
	 * arricchisce il valore della presenza delle topCategories con il numero di occorrenze delle parole nel testo
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<String,Integer> addValuesOccurrences(HashMap<String,Integer> wordResult) {
		Iterator iterator = wordResult.keySet().iterator();	  
		while (iterator.hasNext()) {
			String word = iterator.next().toString();
			int topCategories = wordResult.get(word);
			if(occurrencesCount.containsKey(word)) {
				int newValue = topCategories*occurrencesCount.get(word);
				//System.out.println();
				//System.out.println("PAROLA: "+word+" Categorie: "+topCategories+" e occorrenze: "+occurrencesCount.get(word));
				wordResult.put(word, newValue);
			}
		}
		//System.out.println("\nVALORI NUOVI: "+wordResult);
		return wordResult;
	}

	
	/* Restituisce un array composto dai valori proposti 
	 * per i campi Persona, Citta' e Sede (Citta' e Sede diventeranno successivamente un unico campo)
	 */
	public String[] choiceDataProposals(HashMap<String, Integer> mapP, HashMap<String, Integer> mapC, HashMap<String, Integer> mapV) {
		String[] words = new String[3];
		int citySize = mapC.size(); int venueSize = mapV.size(); int personSize = mapP.size();
		String cityTop; String venueTop; String personTop;
		
		if(personSize==0) {
			personTop = "";
		}
		else {
			personTop = wordMaximumValue(mapP);
			if(mapC.containsKey(personTop))
				mapC.remove(personTop);
			else if(mapV.containsKey(personTop))
				mapV.remove(personTop);
		}
		words[0]=personTop;

		if(citySize==0) {
			words[1]="";
			if(venueSize==1) {
				words[2]=getFirstElementKeySet(mapV);
				
			}
			else if(venueSize>1) {
				words[2]=wordMaximumValue(mapV);	
			}
		}
		if(venueSize==0) {
			words[2]="";
			if(citySize==1) {
				words[1]=getFirstElementKeySet(mapC);
			}
			else if(citySize>1) {
				words[1]=wordMaximumValue(mapC);
			}
		}
		else if(citySize==1 && venueSize==1) {
			String word=getFirstElementKeySet(mapC);
			if(mapC.containsKey(word) && mapV.containsKey(word)) {
				if(mapC.get(word)>=mapV.get(word)) {
					words[1]=word;
					words[2]="";
				}
				else {
					words[1]="";
					words[2]=word;
				}
			}
			else {
				words[1]=word;
				
				words[2]=getFirstElementKeySet(mapV);
			}
		}
		else {
			cityTop=wordMaximumValue(mapC);
			venueTop=wordMaximumValue(mapV);
			if(cityTop.equals(venueTop)) {
				if(mapC.get(cityTop)>=mapV.get(venueTop)) {
					words[1]=cityTop;
					mapV.remove(venueTop);
					venueTop=wordMaximumValue(mapV);
					words[2]=venueTop;
				}
				else {
					words[2]=venueTop;
					
					mapC.remove(cityTop);
					cityTop=wordMaximumValue(mapC);
					words[1]=cityTop;
				}
			}
			else {
				words[1]=cityTop;
				words[2]=venueTop;
			}
		}
		return words;
	}
	
	/* Data una mappa composta da String (chiave) e Integer (valore)
	 * restituisce la chiave associata al valore piu' alto
	 */
	@SuppressWarnings({"rawtypes" })
	private String wordMaximumValue(HashMap<String, Integer> wordTaggedPerson) {
		String wordMaxValue=getFirstElementKeySet(wordTaggedPerson);
		if(!wordMaxValue.equals("")) {
			int maxValue = wordTaggedPerson.get(wordMaxValue);
			Iterator iterator = wordTaggedPerson.keySet().iterator();	  
			while (iterator.hasNext()) {
				String key = iterator.next().toString();
				int value = wordTaggedPerson.get(key);
				if(value>=maxValue) {
					maxValue=value;
					wordMaxValue=key;
				}
			}
		}
		else
			wordMaxValue="";
		return wordMaxValue;
	}
	
	/*
	 * Aggiunge un valore (10) alle parole matchate nel titolo
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<String, Integer> addValueTitle(HashMap<String, Integer> map) {
		HashMap<String, Integer> newMap = new HashMap<String, Integer>();
		Iterator iteratorMapP = map.keySet().iterator();	  
		while (iteratorMapP.hasNext()) {
			String key = iteratorMapP.next().toString();
			int value = map.get(key)+10; 
			newMap.put(key,value);
		}
		return newMap;
	}
	
	
	/* Data una mappa composta da String (chiave) e Integer (valore)
	 * restituisce la prima chiave della mappa
	 */
	private String getFirstElementKeySet(HashMap<String, Integer> map) {
		String value="";
		if (!map.isEmpty()) {
			Set<String> keys = map.keySet();
			for(String k: keys) {
				value = k;
				break;
			}
		}
		return value;
	}


	/* Metodo per fondere gli elementi di due mappe
	 * 
	 */
	public HashMap<String, Integer> mergeMap(HashMap<String, Integer> mapA,HashMap<String, Integer> mapB) {
		HashMap<String, Integer> mapC = new HashMap<String, Integer>();
		mapC.putAll(mapA);
		mapC.putAll(mapB);
	return mapC;
	}

}

