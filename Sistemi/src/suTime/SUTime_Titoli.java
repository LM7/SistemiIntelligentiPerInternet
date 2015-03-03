package suTime;

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
		Annotation annotation = new Annotation(text);
		annotation.set(CoreAnnotations.DocDateAnnotation.class, "2013-07-14");
		pipeline.annotate(annotation);
		List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
		for (CoreMap cm : timexAnnsAll) {
			text = text.replaceAll(cm.toString(), "DDD");
		}
		return text;
	}
	
	
	public static void main(String[] args) {
		SUTime_Titoli sutt = new SUTime_Titoli();
		String s = sutt.getTextTag(" Kumkum Bhagya Episode 231 February 27 2015 Preview Zeetv » | India Video News - VideoSamachar.com ");
		System.out.println(s);
	}

}