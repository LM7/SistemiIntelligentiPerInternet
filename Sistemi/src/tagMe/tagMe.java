package tagMe;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
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
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;

public class tagMe {

	public HashMap<String,Integer> getTagMeProposedData(String text) throws IOException {
		/*Elaborazione del testo da taggare*/
		//text ="Die Antwoord at Le Zenith  (Paris) on 28 Jan 2015";
		//text = "Giraffage ?  Tickets ? Music Hall of Williamsburg ? Brooklyn, NY ? January 31st, 2015";
		//text = "Lady Gaga at Radio City Music Hall  (New York) on 23 Jun 2015";
		//text = "Hozier tour (Concert) 31st January 2015-2nd June 2015";		
		//text = "Nickelback at Razzmatazz  (Barcelona) on 5 Nov 2015";
		//text = "Francesco De Gregori Rome Tickets - Francesco De Gregori on Friday, March 20, 2015 at Palalottomatica | TicketNetwork";
		//text = "The Prodigy at Heineken Music Hall  (Amsterdam) on 10 Apr 2015";
		//text = "A Day to Remember / The Offspring at Tempe Beach Park  (Tempe) on 4 Apr 2015";
		//text = "One Republic at MTS Centre  (Winnipeg) on 27 Apr 2015";

		/*Rimozione delle espressioni temporali
		 * la presenza delle espressioni temporali condiziona consistentemente il tagging effettuato da TagMe
		 */
		Properties props = new Properties();
		AnnotationPipeline pipeline = new AnnotationPipeline();
		pipeline.addAnnotator(new TokenizerAnnotator(false));
		pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
		pipeline.addAnnotator(new POSTaggerAnnotator(false));
		pipeline.addAnnotator(new TimeAnnotator("sutime", props));

		Annotation annotation = new Annotation(text);
		annotation.set(CoreAnnotations.DocDateAnnotation.class, "2015-02-04");
		pipeline.annotate(annotation);
		System.out.println(annotation.get(CoreAnnotations.TextAnnotation.class));
		List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);

		for (CoreMap cm : timexAnnsAll) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String miaData = cm.get(TimeExpression.Annotation.class).getTemporal().getTimexValue();
			GregorianCalendar c = new GregorianCalendar();
			/*
			try {
				c.setTime(sdf.parse(miaData));
				//se la parsa la deve toglie
				String temporalExpression = cm.toString();
				//System.out.println("Espressione temporale.toString: "+temporalExpression);
				text= text.replace(temporalExpression, "");
				System.out.println("Testo pulito: "+text);
			} catch (ParseException e) {
				//nulla
				e.printStackTrace();
			}
			*/
		}

		/*Richiesta verso TagMe*/
		URL url= new URL("http://tagme.di.unipi.it/tag");
		HttpURLConnection con=(HttpURLConnection) url.openConnection(); 

		//add request header
		con.setRequestMethod("POST");
		//con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "key=41480047b3428dcfe6a5c1bba1f0a93e&text="+text+"&include_categories=true";

		//send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.write(urlParameters.getBytes("UTF-8"));
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("--------------------------------------------------------");
		System.out.println("Sending 'POST' request to URL : " + url);
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
		topWordPlaceCityList.add("boroughs");

		List<String> topWordPlaceVenueList = new ArrayList<String>(); 
		topWordPlaceVenueList.add("venues");
		topWordPlaceVenueList.add("arenas");
		topWordPlaceVenueList.add("buildings and structures");
		topWordPlaceVenueList.add("hall");
		topWordPlaceVenueList.add("theatres");
		topWordPlaceVenueList.add("populated place");

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

		System.out.println();

		HashMap<String,Integer> selections = p.choiceDataProposals(wordTaggedPlaceCity,wordTaggedPlaceVenue,wordTaggedPerson);
		
		return selections;
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
