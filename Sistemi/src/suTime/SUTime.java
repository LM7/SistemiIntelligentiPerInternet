package suTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class SUTime {

	private AnnotationPipeline pipeline;
	public static final int IMPORTANZA_TITOLO = 10;

	public SUTime() {
		Properties props = new Properties();
		pipeline = new AnnotationPipeline();
		pipeline.addAnnotator(new TokenizerAnnotator(false));
		pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
		pipeline.addAnnotator(new POSTaggerAnnotator(false));
		pipeline.addAnnotator(new TimeAnnotator("sutime", props));
	}

	public void getTimeProva(String text) {
		Annotation annotation = new Annotation(text);
		annotation.set(CoreAnnotations.DocDateAnnotation.class, "2013-07-14");
		pipeline.annotate(annotation);
		System.out.println("--");
		List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
		System.out.println(timexAnnsAll.size());
		int i = 1;
		for (CoreMap cm : timexAnnsAll) {
			System.out.print(i+") ");
			System.out.print(cm);
			System.out.print("-->");
			System.out.print(cm.get(TimeExpression.Annotation.class).getTemporal().getTime());
			System.out.print("-->");
			System.out.println(cm.get(TimeExpression.Annotation.class).getTemporal().getTimexValue());
			i++;

		}
		System.out.println("--");
		int j = 0;
		int k = 0;
		int l = 0;
		List<GregorianCalendar> date = new ArrayList<GregorianCalendar>();
		for(CoreMap cm : timexAnnsAll) {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			String miaData = cm.get(TimeExpression.Annotation.class).getTemporal().getTimexValue();
			GregorianCalendar c = new GregorianCalendar();
			GregorianCalendar ieri = new GregorianCalendar();
			ieri.add(Calendar.HOUR_OF_DAY, -24);
			try{
				c.setTime(sdf.parse(miaData));
				date.add(c);
				//System.out.println(c.getTime());
				j++;
				if(!c.before(ieri))
					l++;
			} 
			catch (Exception e){
				try {
					c.setTime(sdf1.parse(miaData));
					date.add(c);
					//System.out.println(c.getTime());
					j++;
					if(!c.before(ieri))
						l++;
				} catch (Exception e2) {
					try {
						c.setTime(sdf2.parse(miaData));
						date.add(c);
						//System.out.println(c.getTime());
						j++;
						if(!c.before(ieri))
							l++;
					} catch (Exception e3) {
						System.out.println(e.getMessage());
						k++;
					}
				}
			}		

		}
		System.out.println("date parsate: "+j);
		System.out.println("date non parsate: "+k);
		System.out.println("date maggiori a ieri: "+l);
	}
	
	public HashMap<Date, Integer> getTime(String title, String text) {
		HashMap<Date, Integer> date = new HashMap<Date, Integer>();
		GregorianCalendar ieri = new GregorianCalendar();
		ieri.add(Calendar.HOUR_OF_DAY, -24);
		
		Annotation annotation = new Annotation(title);
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
					if(date.containsKey(d))
						date.put(d, date.get(d) + IMPORTANZA_TITOLO);
					else
						date.put(d, IMPORTANZA_TITOLO);
				}
			} 
			catch (Exception e){
				//nulla
			}		

		}
		
		Annotation annotation2 = new Annotation(text);
		annotation2.set(CoreAnnotations.DocDateAnnotation.class, "2013-07-14");
		pipeline.annotate(annotation2);
		List<CoreMap> timexAnnsAll2 = annotation2.get(TimeAnnotations.TimexAnnotations.class);
		for(CoreMap cm : timexAnnsAll2) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String miaData = cm.get(TimeExpression.Annotation.class).getTemporal().getTimexValue();
			GregorianCalendar c = new GregorianCalendar();
			try{
				c.setTime(sdf.parse(miaData));
				if(!c.before(ieri)) {
					Date d = c.getTime();
					if(date.containsKey(d))
						date.put(d, date.get(d) + 1);
					else
						date.put(d, 1);
				}
			} 
			catch (Exception e){
				//nulla
			}		

		}
		return date;
	}
	
	/* prende l'evento con piu' occorrenze in caso di pari 
	 * occorrenze prende la data piu' piccola
	 */
	public Date dataEvento(HashMap<Date, Integer> date) {
		Date evento = null;
		int num = 0;
		for(Date d: date.keySet()) {
			if(date.get(d)> num) {
				num = date.get(d);
				evento = d;
			}
			else if (date.get(d)==num)
				if(d.after(evento))
					evento = d;
		}
		return evento;
	}

}
