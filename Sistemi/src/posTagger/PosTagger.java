package posTagger;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class PosTagger {
	
	public static String textTagged(String testo) {
		MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");
		String tagged = tagger.tagString(testo);
		return tagged;
	}

}
