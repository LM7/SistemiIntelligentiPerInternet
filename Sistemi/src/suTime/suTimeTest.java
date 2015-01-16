package suTime;

import java.util.Properties;
import java.util.List;

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

public class suTimeTest {

	public static void main(String[] args) {
		Properties props = new Properties();
		AnnotationPipeline pipeline = new AnnotationPipeline();
		pipeline.addAnnotator(new TokenizerAnnotator(false));
		pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
		pipeline.addAnnotator(new POSTaggerAnnotator(false));
		pipeline.addAnnotator(new TimeAnnotator("sutime", props));


		String[] testo = {"J Mascis at Scala Attendance Images Similar Reviews"
				+ "J Mascis Scala Thursday 8 January 2015 Upload poster With Luluc Today at 7:30pm "
				+ "Add to a calendar JAN8 Scala 275 Pentonville Road London N1 9NL"
				+ "United Kingdom Show on Map Show on Map Tel: +442078332022 "
				+ "Web: Scala-London.co.UK/scala/events.php?v"};
		for (String text : testo) {
			Annotation annotation = new Annotation(text);
			annotation.set(CoreAnnotations.DocDateAnnotation.class, "2013-07-14");
			pipeline.annotate(annotation);
			System.out.println(annotation.get(CoreAnnotations.TextAnnotation.class));
			List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
			for (CoreMap cm : timexAnnsAll) {
				List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
				System.out.println(cm + " [from char offset " +
						tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) +
						" to " + tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class) + ']' +
						" --> " + cm.get(TimeExpression.Annotation.class).getTemporal());
			}
			System.out.println("--");
		}
	}

}
