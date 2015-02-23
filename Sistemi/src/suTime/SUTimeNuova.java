package suTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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

public class SUTimeNuova {

	private AnnotationPipeline pipeline;
	public static final int IMPORTANZA_TITOLO = 1;

	public SUTimeNuova() {
		Properties props = new Properties();
		pipeline = new AnnotationPipeline();
		pipeline.addAnnotator(new TokenizerAnnotator(false));
		pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
		pipeline.addAnnotator(new POSTaggerAnnotator(false));
		pipeline.addAnnotator(new TimeAnnotator("sutime", props));
	}

	/*
	 * FEATURE -> [0] = si trova nel titolo (1,-1); [1] = # occorrenze
	 */
	public HashMap<Date, int[]> getTime(String title, String text) {
		HashMap<Date, int[]> date = new HashMap<Date, int[]>();

		date = createMap(title, date, 1);
		date = createMap(text, date, -1);
		return date;
	}

	private HashMap<Date, int[]> createMap(String title_text, HashMap<Date, int[]> date, int isTitolo) {
		
		GregorianCalendar ieri = new GregorianCalendar();
		ieri.add(Calendar.HOUR_OF_DAY, -24);
		Annotation annotation = new Annotation(title_text);
		annotation.set(CoreAnnotations.DocDateAnnotation.class, "2013-07-14");
		pipeline.annotate(annotation);
		List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
		for(CoreMap cm : timexAnnsAll) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String miaData = cm.get(TimeExpression.Annotation.class).getTemporal().getTimexValue();
			GregorianCalendar c = new GregorianCalendar();
			try{
				c.setTime(sdf.parse(miaData));
				if(!c.before(ieri)) {
					Date d = c.getTime();
					int[] appoggio = new int[2];
					if(date.containsKey(d)) {
						appoggio = date.get(d);
						appoggio[1] += 1;
						//date.put(d, date.get(d)[1] + IMPORTANZA_TITOLO);
					}
					else {
						appoggio[0] = isTitolo;
						appoggio[1] = 1;
						date.put(d, appoggio);
					}
				}
			} 
			catch (Exception e){
				//nulla
			}		

		}
		return date;
	}

}


