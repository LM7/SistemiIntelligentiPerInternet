package posTagger;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class PosTaggerTest {


	public static void main(String[] args) {
		MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");

		String sample = "Good afternoon Rajat Raina, how are you today?"+
		"I go to school at Stanford University, which is located in California.";

		String tagged = tagger.tagString(sample);
		System.out.println(tagged);
	}

}
