package tagMe;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;

public class TaggerNuova {
	static File stopWordFile = new File("stopWord.txt");
	public final static HashSet<String> STOP_LOCATION = new HashSet<String>(Arrays.asList("venues","venue","WordPress.com","big","big red","local","Gamespot","events","met","Europe","death","Songkick"));
	public final static HashSet<String> STOP_PERSON = new HashSet<String>(Arrays.asList("originals","heads","harder","best","singer songwriter"));


	public List<HashMap<String, int[]>> getTagMeData(String title, String text) throws IOException {
		ArrayList <HashMap<String, int[]>> tagMeData = new ArrayList<HashMap<String, int[]>>();

		//System.out.println("============LAVORO SUL TITOLO============");
		ArrayList<HashMap<String,Integer>> titleMap = getListMaps(title);
		//System.out.println("============LAVORO SUL TESTO============");
		ArrayList<HashMap<String,Integer>> textMap = getListMaps(text);

		tagMeData.add(mergeMap(titleMap.get(2),textMap.get(2)));
		tagMeData.add(mergeMap(titleMap.get(1),textMap.get(1)));
		tagMeData.add(mergeMap(titleMap.get(0),textMap.get(0)));

		return tagMeData;
	}


	/*
	 * Metodo di supporto per fondere le mappe di titolo e testo insieme
	 */
	@SuppressWarnings("rawtypes")
	private HashMap<String, int[]> mergeMap(HashMap<String, Integer> titleMap,HashMap<String, Integer> textMap) {
		//printMap(titleMap);
		//printMap(textMap);
		HashMap<String, int[]> merge = new HashMap<String, int[]>();
		if (titleMap.isEmpty()) {
			Iterator iteratorText = textMap.keySet().iterator();	  
			while (iteratorText.hasNext()) {
				String word = iteratorText.next().toString();
				int[] values = new int[2];
				values[0]=-1;
				values[1]=textMap.get(word);
				merge.put(word,values);
			}
		}
		else {
			Iterator iteratorTitle = titleMap.keySet().iterator();	  
			while (iteratorTitle.hasNext()) {
				String word = iteratorTitle.next().toString();
				int[] values = new int[2];
				values[0]=1;
				if(!textMap.containsKey(word)) {	
					values[1]=titleMap.get(word);
				}
				else {
					values[1]=titleMap.get(word)+textMap.get(word);
					textMap.remove(word);
				}
				merge.put(word,values);
			}
			if (!textMap.isEmpty()) {
				Iterator iteratorText = textMap.keySet().iterator();	  
				while (iteratorText.hasNext()) {
					String word = iteratorText.next().toString();
					int[] values = new int[2];
					values[0]=-1;
					values[1]=textMap.get(word);
					merge.put(word,values);
				}
			}
		}
		return merge;
	}



	/* 
	 * Restituisce i valori parziali proposti (titolo e testo) 
	 */
	public ArrayList<HashMap<String,Integer>> getListMaps(String content) throws IOException {
		String textOriginal = content;
		//System.out.println("°°°°°°°°°°°°°°°°°°°°°ORIGINAL°°°°°°°°°°°°°°°°°°°°°\n"+textOriginal);
		
		content = content.replaceAll("\n", " ");
		content = content.replaceAll("\\<.*?\\>|\\{.*?\\}", " ");
		content = content.replaceAll("\\&.*?\\;", "");
		content = content.replaceAll(".x-boilerpipe-mark1", "");

		/*
		 * NON PUOI MODIFICARE IL TESTO ORIGINALE IN QUESTO CASO 
		 * ESEMPIO DI QUELLO CHE SUCCEDE:
		 * Lorenzo trova il luogo Roma tu trovi il luogo roma -> 2 luoghi diversi
		 * text = text.toLowerCase();
		 */

		content = content.replaceAll("’", "'");
		content = removeStopWord(content);
		content = content.replaceAll("[^a-zA-Z ]", " ");
		content = removeStopWord(content);
		//System.out.println("°°°°°°°°°°°°°°°°°°°°°CONTENT°°°°°°°°°°°°°°°°°°°°°\n"+content);


		ArrayList<HashMap<String,Integer>> result = new ArrayList<HashMap<String,Integer>>();

		/*Rimozione delle espressioni temporali
		 * la presenza delle espressioni temporali condiziona consistentemente il tagging effettuato da TagMe
		 */
		Properties props = new Properties();
		AnnotationPipeline pipeline = new AnnotationPipeline();
		pipeline.addAnnotator(new TokenizerAnnotator(false));
		pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
		pipeline.addAnnotator(new POSTaggerAnnotator(false));
		pipeline.addAnnotator(new TimeAnnotator("sutime", props));

		Annotation annotation = new Annotation(content);
		annotation.set(CoreAnnotations.DocDateAnnotation.class, "2015-02-04");
		pipeline.annotate(annotation);
		//System.out.println(annotation.get(CoreAnnotations.TextAnnotation.class));
		List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);

		for (CoreMap cm : timexAnnsAll) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String miaData = cm.get(TimeExpression.Annotation.class).getTemporal().getTimexValue();
			GregorianCalendar c = new GregorianCalendar();
			try {
				c.setTime(sdf.parse(miaData));
				//se la parsa la deve toglie
				String temporalExpression = cm.toString();
				//System.out.println("Espressione temporale.toString: "+temporalExpression);
				content= content.replace(temporalExpression, "");
				//System.out.println("Testo pulito: "+text);
			} catch (Exception e) {
				//nulla
			}

		}

		/*Richiesta verso TagMe*/
		URL url= new URL("http://tagme.di.unipi.it/tag");
		HttpURLConnection con=(HttpURLConnection) url.openConnection(); 

		//add request header
		con.setRequestMethod("POST");
		//con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "key=41480047b3428dcfe6a5c1bba1f0a93e&text="+content+"&include_categories=true";

		//send post request
		try {
			con.setDoOutput(true);
		}catch(Exception e) {}
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.write(urlParameters.getBytes("UTF-8"));
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		/*System.out.println("--------------------------------------------------------");
		System.out.println("Sending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);
		System.out.println("--------------------------------------------------------");*/

		if(responseCode!=200){
			urlParameters = "key=41480047b3428dcfe6a5c1bba1f0a93e&text="+textOriginal+"&include_categories=true";

			//send post request
			try{
				con.setDoOutput(true);
			}catch(Exception e) {
				return result;
			}
			wr = new DataOutputStream(con.getOutputStream());
			wr.write(urlParameters.getBytes("UTF-8"));
			wr.flush();
			wr.close();

			responseCode = con.getResponseCode();
			/*System.out.println("--------------------------------------------------------");
			System.out.println("Sending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);
			System.out.println("--------------------------------------------------------");*/

			if(responseCode!=200){
				return null;
			}
		}


		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		//System.out.println(response.toString());
		//System.out.println("--------------------------------------------------------");

		/* Elaborazione dei risultati*/
		ParserNuova p = new ParserNuova(response.toString(),content);
		HashMap<String, List<String>> tagMeResult = p.processingReply();

		List<String> topWordPlaceCityList = new ArrayList<String>(); 
		topWordPlaceCityList.add("cities in");
		topWordPlaceCityList.add("capitals");
		//topWordPlaceCityList.add("city");
		topWordPlaceCityList.add("capital");
		//topWordPlaceCityList.add("states");
		//topWordPlaceCityList.add("populated places in");
		topWordPlaceCityList.add("boroughs");

		List<String> topWordPlaceVenueList = new ArrayList<String>(); 
		topWordPlaceVenueList.add("music venues");
		topWordPlaceVenueList.add("event venues");
		topWordPlaceVenueList.add("arenas");
		topWordPlaceVenueList.add("buildings and structures");
		topWordPlaceVenueList.add("concert halls");
		topWordPlaceVenueList.add("theatres");
		//topWordPlaceVenueList.add("populated places in");

		List<String> topWordPersonList = new ArrayList<String>(); 
		topWordPersonList.add("singer-songwriters");
		topWordPersonList.add("singer");
		topWordPersonList.add("musical groups established in");

		HashMap<String,Integer> wordTaggedPlaceCity = p.filterCategories(tagMeResult, topWordPlaceCityList);
		//System.out.println("\n(LUOGHI: Citta' dell'evento) - Parole Taggate Rilevanti");
		//printMap(wordTaggedPlaceCity);

		HashMap<String,Integer> wordTaggedPlaceVenue = p.filterCategories(tagMeResult, topWordPlaceVenueList);
		//System.out.println("\n(LUOGHI: Sede dell'evento) - Parole Taggate Rilevanti");
		//printMap(wordTaggedPlaceVenue);

		HashMap<String,Integer> wordTaggedPerson = p.filterCategories(tagMeResult, topWordPersonList);
		//System.out.println("\n(PERSONE) - Parole Taggate Rilevanti");
		//printMap(wordTaggedPerson);

		wordTaggedPlaceCity.keySet().removeAll(STOP_LOCATION);
		wordTaggedPlaceVenue.keySet().removeAll(STOP_LOCATION);
		wordTaggedPerson.keySet().removeAll(STOP_PERSON);
		
		result.add(wordTaggedPerson);
		result.add(wordTaggedPlaceCity);
		result.add(wordTaggedPlaceVenue);

		return result;
	}

	/*
	 * Rimozione delle stopWord
	 */
	public static String removeStopWord(String text) throws IOException    {
		StringTokenizer tokens = new StringTokenizer(text, " ");
		String newText = "";
		while (tokens.hasMoreTokens()) {
			String temp = tokens.nextToken();
			if (!checkStopWord(temp,stopWordFile)) {
				newText += temp + " ";
			}
		}
		text = "";
		text = newText;
		return text;
	}


	/*
	 * Metodo di supporto per la verifica delle stop word
	 */
	@SuppressWarnings("resource")
	public static boolean checkStopWord(String word, File stopWordFile) throws IOException {
		BufferedReader stopWordReader = new BufferedReader(new FileReader(stopWordFile));
		String text2;
		while ((text2 = stopWordReader.readLine()) != null) {
			/*
			 * SUPPONENDO CHE QUI FAI IL CONFRONTO TRA PAROLA E STOPWORD
			 * MODIFICHIAMO equals() CON equalsIgnoreCase()
			 */
			if(word.equalsIgnoreCase(text2))
				return true;
		}
		stopWordReader.close();
		return false;
	}
	
	/*
	 * Stampa una mappa
	 */
	@SuppressWarnings("rawtypes")
	public static void printMap(HashMap<String, Integer> map) {
		System.out.println("MAPPA");
		Iterator iterator = map.keySet().iterator();	  
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			int value = map.get(key);	  
			System.out.println("Parola: "+key+" | Valore: "+value);
		}
	}
}
