package boilerpipe;

import java.net.URL;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;
import de.l3s.boilerpipe.sax.HTMLHighlighter;

public class Boilerpipe {

	private HTMLHighlighter hh;
	private BoilerpipeExtractor extractor;

	public Boilerpipe() {
		extractor = CommonExtractors.ARTICLE_EXTRACTOR;
		hh = HTMLHighlighter.newHighlightingInstance();
	}

	public Boilerpipe(int i) throws Exception {
		hh = HTMLHighlighter.newHighlightingInstance();
		switch (i) {

		case 0:
			extractor = CommonExtractors.ARTICLE_EXTRACTOR;
			break;
		case 1:
			extractor = CommonExtractors.DEFAULT_EXTRACTOR;;
			break;
		case 2:
			extractor = CommonExtractors.CANOLA_EXTRACTOR;;
			break;
		case 3:
			extractor = CommonExtractors.LARGEST_CONTENT_EXTRACTOR;
			break;
		case 4:
			extractor = CommonExtractors.KEEP_EVERYTHING_EXTRACTOR;
			break;
		default:
			throw new Exception("intero compreso tra 0 e 4");
		}
	}

	public String[] getText(URL url) throws Exception {
		String[] TitoloTesto = new String[2];
		HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL("http://www.last.fm/event/3996308+Giraffage+at+Music+Hall+of+Williamsburg+on+31+January+2015"));
	    TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
		TitoloTesto[0] = doc.getTitle();
		TitoloTesto[1] = hh.process(url, extractor);
		return TitoloTesto;
	}

}
