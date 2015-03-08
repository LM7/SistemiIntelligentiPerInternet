package suTime;

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
		for (CoreMap cm : timexAnnsAll) {
			List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
			
			String textPreData = text.substring(0, tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
			String textData = text.substring(tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class), tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
			String textPostData = text.substring(tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
			
			String[] dataComposta = cm.toString().split(" ");
			for(i=0;i<dataComposta.length;i++) {					
				if(!dataComposta[i].equals("") && !dataComposta[i].equals(" ")) {
					textData = textData.replaceAll(dataComposta[i], "DDD#"+dataComposta[i]);
				}
			}
			text = textPreData + textData + textPostData;
		}
		return text;
	}
	
	
	public static void main(String[] args) {
		SUTime_Titoli sutt = new SUTime_Titoli();
		/*
		String s = sutt.getTextTag(" Kumkum Bhagya Episode 231 February 27 2015 Preview Zeetv » | India Video News - VideoSamachar.com ");
		System.out.println(s);
		String ss = sutt.getTextTag("Baxter, International Inc. (BAX) Ex-Dividend Date Scheduled for March 09, 2015 - NASDAQ.com");
		System.out.println(ss);
		*/
		String sss = sutt.getTextTag("8 (Sunday,  10 April 2018) Connie, Stefny, Steven Letigre Brooklyn concert tickets, Output Club Brooklyn (Sunday,  8 March 2015) - 5gig.com");
		System.out.println("8 (Sunday,  10 April 2018) Connie, Stefny, Steven Letigre Brooklyn concert tickets, Output Club Brooklyn (Sunday,  8 March 2015) - 5gig.com");
		System.out.println(sss);
		
		String ssss = sutt.getTextTag("The Darkness at Whelan's|08 March 2015 - music listings for Dublin, Ireland - entertainment.ie");
		System.out.println("The Darkness at Whelan's|08 March 2015 - music listings for Dublin, Ireland - entertainment.ie");
		System.out.println(ssss);
	}

}