package boilerpipe;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.SAXException;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;

public class PageInfo {

	public static void main(String[] args) throws MalformedURLException, IOException, BoilerpipeProcessingException, SAXException {
		// TODO Auto-generated method stub
		final HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL("http://www.last.fm/events"));
	    final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();

	    System.out.println("Page title: " + doc.getTitle());

	}

}
