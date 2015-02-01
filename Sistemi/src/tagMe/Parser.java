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

	public Parser(String result) {
		this.result=result;
		this.gson=new Gson();
		this.reply=new Reply();
	}

	public String getReplyTagMe() {
		return result;
	}

	public void setReplyTagMe(String result) {
		this.result = result;
	}

	public HashMap<String, List<String>> processingReply() {
		HashMap<String, List<String>> tagMeResult = new HashMap<String, List<String>>();
		reply = gson.fromJson(result,Reply.class);
		List<Annotation> annotations = reply.getAnnotations();
		int zeroCategorie=0;
		System.out.println("ANNOTATIONS: \nsize: "+annotations.size()+"\n");
		for (Annotation a : annotations) {	
			System.out.println("Spot: "+a.getSpot()+"\n(Wikipedia) Title: "+a.getTitle()+"\nCategories: "+a.getDbpediaCategories());
			System.out.println("#categories: "+a.getDbpediaCategories().size());
			System.out.println();

			if(a.getDbpediaCategories().size()==0) {
				zeroCategorie++;
			}
			else{
				tagMeResult.put(a.getSpot(), a.getDbpediaCategories());
			}
		}
		System.out.println("Parole taggate escluse dall'analisi: "+zeroCategorie);
		return tagMeResult;
	}

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
		return wordResult;
	}

	public String[] choiceDataProposals(HashMap<String, Integer> wordTaggedPlaceCity,HashMap<String, Integer> wordTaggedPlaceVenue,HashMap<String, Integer> wordTaggedPerson) {
		String[] selections = new String[3];
		
		String topPerson = wordMaximumValue(wordTaggedPerson); //persona proposta
		//System.out.println("Proposta per Persona: "+selections[0]);
		if(wordTaggedPlaceCity.containsKey(topPerson)) {
			wordTaggedPlaceCity.remove(topPerson);
		}
		if(wordTaggedPlaceVenue.containsKey(topPerson)) {
			wordTaggedPlaceVenue.remove(topPerson);
		}
		selections[0]=topPerson;
		
		
		int placeCitySize = wordTaggedPlaceCity.size();
		int placeVenueSize = wordTaggedPlaceVenue.size();
		boolean check = false;

		for (int i=0; i<(placeCitySize+placeVenueSize)/2; i++) {
			if (placeCitySize==1 &&placeVenueSize==1&&check==false) {
				String wordPlaceCity = getFirstElementKeySet(wordTaggedPlaceCity);
				if(wordTaggedPlaceVenue.containsKey(wordPlaceCity) && wordTaggedPlaceCity.containsKey(wordPlaceCity)) {
					//System.out.println("VALORE CITY: "+wordTaggedPlaceCity.get(wordPlaceCity));
					//System.out.println("VALORE VENUE: "+wordTaggedPlaceVenue.get(wordPlaceCity));
					if(wordTaggedPlaceCity.get(wordPlaceCity)>=wordTaggedPlaceVenue.get(wordPlaceCity)) {
						wordTaggedPlaceVenue.remove(wordPlaceCity);
					}
					else {
						wordTaggedPlaceCity.remove(wordPlaceCity);
					}
				}
				else {
					selections[1] = wordMaximumValue(wordTaggedPlaceCity); 
					selections[2] = wordMaximumValue(wordTaggedPlaceVenue);
					return selections;
				}
			}

			else if(placeCitySize==1 &&check==false) {
				String wordPlaceCity = getFirstElementKeySet(wordTaggedPlaceCity);
				selections[1] = wordPlaceCity;
				if(wordTaggedPlaceVenue.containsKey(wordPlaceCity)); 
				wordTaggedPlaceVenue.remove(wordPlaceCity);
				check=true;
			}
			else if (placeVenueSize==1&&check==false) {
				String wordPlaceVenue = getFirstElementKeySet(wordTaggedPlaceVenue);
				selections[2] = wordPlaceVenue;
				if(wordTaggedPlaceCity.containsKey(wordPlaceVenue)); 
				wordTaggedPlaceCity.remove(wordPlaceVenue);
				check = true;
			}
			else {
				String topPlaceCity = selections[1];
				if(wordTaggedPlaceVenue.containsKey(topPlaceCity) && wordTaggedPlaceCity.containsKey(topPlaceCity)) {
					//System.out.println("VALORE CITY: "+wordTaggedPlaceCity.get(topPlaceCity));
					//System.out.println("VALORE VENUE: "+wordTaggedPlaceVenue.get(topPlaceCity));
					if(wordTaggedPlaceCity.get(topPlaceCity)>=wordTaggedPlaceVenue.get(topPlaceCity)) {
						wordTaggedPlaceVenue.remove(topPlaceCity);
					}
					else {
						wordTaggedPlaceCity.remove(topPlaceCity);
					}
				}
			}
			selections[1] = wordMaximumValue(wordTaggedPlaceCity); //città proposta
			//System.out.println("Proposta per Citta': "+selections[1]);
			selections[2] = wordMaximumValue(wordTaggedPlaceVenue); //sede proposta
			//System.out.println("Proposta per Sede: "+selections[2]+"\n");
		}
		return selections;
	}
	
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
			wordMaxValue=null;
		return wordMaxValue;
	}

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
}
