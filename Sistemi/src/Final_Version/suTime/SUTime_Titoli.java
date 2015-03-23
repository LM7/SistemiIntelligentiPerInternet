package Final_Version.suTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;

public class SUTime_Titoli {

	private AnnotationPipeline pipeline;

	public SUTime_Titoli() {
		Properties props = new Properties();
		pipeline = new AnnotationPipeline();
		pipeline.addAnnotator(new TokenizerAnnotator(false));
		pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
		pipeline.addAnnotator(new POSTaggerAnnotator(false));
		pipeline.addAnnotator(new TimeAnnotator("sutime", props));
	}

	public String getTextTag(String text) {
		int i;
		Annotation annotation = new Annotation(text);
		annotation.set(CoreAnnotations.DocDateAnnotation.class, "2013-07-14");
		pipeline.annotate(annotation);
		List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
		int caratteriAggiunti = 0;
		for (CoreMap cm : timexAnnsAll) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String miaData = cm.get(TimeExpression.Annotation.class).getTemporal().getTimexValue();
			GregorianCalendar c = new GregorianCalendar();
			try{
				c.setTime(sdf.parse(miaData));

				List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
				String textPreData = text.substring(0, tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)+caratteriAggiunti);
				String textData = text.substring(tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)+caratteriAggiunti, tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class)+caratteriAggiunti);
				String textPostData = text.substring(tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class)+caratteriAggiunti);

				String[] dataComposta = cm.toString().split(" ");
				for(i=0;i<dataComposta.length;i++) {					
					if(!dataComposta[i].equals("") && !dataComposta[i].equals(" ") && !dataComposta[i].equals(",") && !dataComposta[i].contains(":") && 
							!dataComposta[i].equalsIgnoreCase("am") && !dataComposta[i].equalsIgnoreCase("pm")) {
						textData = textData.replaceAll(dataComposta[i], "DDD#"+dataComposta[i]);
						caratteriAggiunti += 4;
					}
				}
				text = textPreData + textData + textPostData;
			}catch(Exception  e){
			}
		}
		return text;
	}

	public boolean containsData(String testo) {
		Annotation annotation = new Annotation(testo);
		annotation.set(CoreAnnotations.DocDateAnnotation.class, "2013-07-14");
		pipeline.annotate(annotation);
		List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
		for(CoreMap cm : timexAnnsAll) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String miaData = cm.get(TimeExpression.Annotation.class).getTemporal().getTimexValue();
			GregorianCalendar c = new GregorianCalendar();
			try{
				c.setTime(sdf.parse(miaData));
				return true;
			}catch(Exception e) {
			}
		}
		return false;
	}

	public Date fromDataStringToDataDate (String data) {
		Annotation annotation = new Annotation(data);
		annotation.set(CoreAnnotations.DocDateAnnotation.class, "2013-07-14");
		pipeline.annotate(annotation);
		List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
		if(!timexAnnsAll.isEmpty()) {
			CoreMap cm = timexAnnsAll.get(0);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String miaData = cm.get(TimeExpression.Annotation.class).getTemporal().getTimexValue();
				try{
					return sdf.parse(miaData);
				}catch (Exception e){
				}
		}
		return null;
	}

	public static void main(String[] args) {
		SUTime_Titoli sutt = new SUTime_Titoli();
		/*
		String s = sutt.getTextTag(" Kumkum Bhagya Episode 231 February 27 2015 Preview Zeetv ?? | India Video News - VideoSamachar.com ");
		System.out.println(s);
		String ss = sutt.getTextTag("Baxter, International Inc. (BAX) Ex-Dividend Date Scheduled for March 09, 2015 - NASDAQ.com");
		System.out.println(ss);
		 */
		String sss = sutt.getTextTag("Hollywood Undead at Bottom Lounge, Chicago on March 17, 2015 05:30 pm | CheapTickets.com");
		System.out.println("8 (Sunday,  10 April 2018) Connie, Stefny, Steven Letigre Brooklyn concert tickets, Output Club Brooklyn March 10, 2015 - 5gig.com");
		System.out.println(sss);
	}

}