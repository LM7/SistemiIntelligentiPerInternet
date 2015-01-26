package namedEntityRecognizer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import events.MsnSearchEngine;
import boilerpipe.Boilerpipe;

public class MainTest {
	public final static int numero_query = 5;

	public static void main(String[] args) throws Exception {
		String data = "31 January 2015";
		String evento_canatante = "Giraffage";
		String luogo = "";
		
		NamedEntityRecognizerTest ner = new NamedEntityRecognizerTest();
		
		/*String[] example = {"Good afternoon Rajat Raina, how are you today?",
		"I go to school at Stanford University, which California is located in California." };
		
		ArrayList<HashMap<String,Integer>> lista = ner.createListOfMapEntity(example);*/
		
		MsnSearchEngine se = new MsnSearchEngine();
		String[] urls = se.getUrls(data+" "+evento_canatante+" "+luogo, numero_query);
		
		for(String s: urls) {
			Boilerpipe b = new Boilerpipe();
			URL url = new URL(s);
			
			String title = b.getText(url)[0];
			String text = b.getText(url)[1];
			
			String[] site = {title, text};
			ArrayList<HashMap<String,Integer>> lista = ner.createListOfMapEntity(site);
			
		}
		
		
		
		
		
		

	}

}
