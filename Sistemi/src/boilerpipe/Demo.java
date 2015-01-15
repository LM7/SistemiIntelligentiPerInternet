package boilerpipe;

import java.io.PrintWriter;
import java.net.URL;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.HTMLHighlighter;

/**
 * Demonstrates how to use Boilerpipe to get the main content, highlighted as HTML.
 * 
 * @author Christian Vieri
 * @see Oneliner if you only need the plain text.
 */
public class Demo {
	public static void main(String[] args) throws Exception {
		URL url = new URL(
				"http://www.last.fm/events/+place/United+Kingdom/London"
//				"http://boilerpipe-web.appspot.com/"
		        );
		
		// choose from a set of useful BoilerpipeExtractors...
		final BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;
//		final BoilerpipeExtractor extractor = CommonExtractors.DEFAULT_EXTRACTOR;
//		final BoilerpipeExtractor extractor = CommonExtractors.CANOLA_EXTRACTOR;
//		final BoilerpipeExtractor extractor = CommonExtractors.LARGEST_CONTENT_EXTRACTOR;
//		final BoilerpipeExtractor extractor = CommonExtractors.KEEP_EVERYTHING_EXTRACTOR;

		// choose the operation mode (i.e., highlighting or extraction)
		final HTMLHighlighter hh = HTMLHighlighter.newHighlightingInstance();
//		final HTMLHighlighter hh = HTMLHighlighter.newExtractingInstance();
		
		PrintWriter out = new PrintWriter("contenutoTesto.html", "UTF-8");
				//("contenutoTesto.html", "UTF-8");
		//out.println("<base href=\"" + url + "\" >");
		out.println("<meta http-equiv=\"Content-Type\" content=\"json; charset=utf-8\" />");
		out.println(hh.process(url, extractor));
		out.close();
		
		System.out.println("Now open file:///tmp/highlighted.html in your web browser");
	}
}

