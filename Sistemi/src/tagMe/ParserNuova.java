package tagMe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;

public class ParserNuova {
	String result;
	String content;
	Reply reply;
	Gson gson; 
	HashMap<String, Integer> occurrencesCount;

	public ParserNuova(String result, String content) {
		this.result=result;
		this.content=content;
		this.gson=new Gson();
		this.reply=new Reply();
		this.occurrencesCount  = new HashMap<String, Integer>();
	}

	public ParserNuova() {
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


	@SuppressWarnings("rawtypes")
	public HashMap<String,Integer> filterCategories(HashMap<String,List<String>> wordCategories, List<String> topWordList) {
		List<String> list = new ArrayList<String>();
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
						list.add(title);

					}
				}
			}
		}
		wordResult=countsOccurrences(list);
		return wordResult;
	}


	public HashMap<String,Integer> countsOccurrences(List<String> words) {
		HashMap<String,Integer> wordResult = new HashMap<String,Integer>();
		for (String wordMatched : words) {
			int occ = StringUtils.countMatches(content, wordMatched);
			wordResult.put(wordMatched, occ);
		}
		return wordResult;
	}

}
