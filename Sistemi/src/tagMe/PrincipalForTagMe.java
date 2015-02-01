package tagMe;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;




import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.util.CoreMap;
import events.MsnSearchEngine;
import boilerpipe.Boilerpipe;

public class PrincipalForTagMe {

	public final static int numero_query = 10;

	public static void main(String[] args) throws IOException {
		String data = "7 March 2015";
		String evento_cantante = "Placebo";
		String luogo = "";
		
		String title = "";
		
		MsnSearchEngine se = new MsnSearchEngine();
		String[] urls = se.getUrls(data+" "+evento_cantante+" "+luogo, numero_query);
		for(String s: urls) {
			//Boilerpipe
			Boilerpipe b = new Boilerpipe();
			URL url = null;
			try {
				url = new URL(s);
				String[] site = b.getText(url);
				title = site[0];
				//String text = site[1];				
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			/***
			//Rimozione delle espressioni temporali
			//la presenza delle espressioni temporali condiziona consistentemente il tagging effettuato da TagMe
			//
			Properties props = new Properties();
			AnnotationPipeline pipeline = new AnnotationPipeline();
			pipeline.addAnnotator(new TokenizerAnnotator(false));
			pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
			pipeline.addAnnotator(new POSTaggerAnnotator(false));
			pipeline.addAnnotator(new TimeAnnotator("sutime", props));
			
			Annotation annotation = new Annotation(title);
			annotation.set(CoreAnnotations.DocDateAnnotation.class, "2015-01-31");
			pipeline.annotate(annotation);
			System.out.println(annotation.get(CoreAnnotations.TextAnnotation.class));
			System.out.println("--");
			List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
			for (CoreMap cm : timexAnnsAll) {
				//System.out.println("Espressione temporale: "+cm);
				String temporalExpression = cm.toString();
				//System.out.println("Espressione temporale.toString: "+temporalExpression);
				title= title.replace(temporalExpression, "");  
				//System.out.println("Testo pulito: "+text);
			}
			***/
			
			/*Richiesta verso TagMe*/
			URL site= new URL("http://tagme.di.unipi.it/tag");
			HttpURLConnection con=(HttpURLConnection) site.openConnection(); 

			//add request header
			con.setRequestMethod("POST");
			//con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "key=41480047b3428dcfe6a5c1bba1f0a93e&text="+title+"&include_categories=true";

			//send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			//wr.writeBytes(urlParameters);
			wr.write(urlParameters.getBytes("UTF-8"));
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("--------------------------------------------------------");
			System.out.println("Sending 'POST' request to URL : " + site);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);
			System.out.println("--------------------------------------------------------");

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			System.out.println(response.toString());
			System.out.println("--------------------------------------------------------");
			
			System.out.println("TITLE: "+title);
			System.out.println("--------------------------------------------------------");
			
			/* Elaborazione dei risultati*/
			Parser p = new Parser(response.toString());
			HashMap<String, List<String>> tagMeResult = p.processingReply();

			List<String> topWordPlaceCityList = new ArrayList<String>(); 
			topWordPlaceCityList.add("cities");
			topWordPlaceCityList.add("capitals");
			topWordPlaceCityList.add("city");
			topWordPlaceCityList.add("capital");
			topWordPlaceCityList.add("states");
			topWordPlaceCityList.add("populated place");
			topWordPlaceCityList.add("towns");
			
			List<String> topWordPlaceVenueList = new ArrayList<String>(); 
			topWordPlaceVenueList.add("venues");
			topWordPlaceVenueList.add("arenas");
			topWordPlaceVenueList.add("buildings and structures");
			topWordPlaceVenueList.add("hall");
			topWordPlaceVenueList.add("theatres");
			topWordPlaceVenueList.add("populated place");
			topWordPlaceVenueList.add("suburbs");
			
			List<String> topWordPersonList = new ArrayList<String>(); 
			topWordPersonList.add("singer-songwriters");
			topWordPersonList.add("singer");
			topWordPersonList.add("musical groups established in");
			topWordPersonList.add("births");
			topWordPersonList.add("artists");

			HashMap<String,Integer> wordTaggedPlaceCity = p.filterCategories(tagMeResult, topWordPlaceCityList);
			System.out.println("\n(LUOGHI: Citta' dell'evento) - Parole Taggate Rilevanti");
			printMap(wordTaggedPlaceCity);
			
			HashMap<String,Integer> wordTaggedPlaceVenue = p.filterCategories(tagMeResult, topWordPlaceVenueList);
			System.out.println("\n(LUOGHI: Sede dell'evento) - Parole Taggate Rilevanti");
			printMap(wordTaggedPlaceVenue);

			HashMap<String,Integer> wordTaggedPerson = p.filterCategories(tagMeResult, topWordPersonList);
			System.out.println("\n(PERSONE) - Parole Taggate Rilevanti");
			printMap(wordTaggedPerson);
			
			System.out.println();
			
			String[] selections = p.choiceDataProposals(wordTaggedPlaceCity,wordTaggedPlaceVenue,wordTaggedPerson);
			
			String[] resultChoice = new String[2];
			resultChoice[1] = selections[1]+", "+selections[2];
			resultChoice[0] = selections[0];
			
			System.out.println("\n=== Dati proposti ===");
			System.out.println("LUOGO: "+resultChoice[1]);
			System.out.println("PERSONA: "+resultChoice[0]);
		}
	}

		@SuppressWarnings("rawtypes")
		public static void printMap(HashMap<String, Integer> wordTaggedPlace) {
			Iterator iterator = wordTaggedPlace.keySet().iterator();	  
			while (iterator.hasNext()) {
				String key = iterator.next().toString();
				String value = wordTaggedPlace.get(key).toString();	  
				System.out.println("La Parola \""+key+"\" contiene "+value+" categorie dal contenuto Top");
			}
		}
}