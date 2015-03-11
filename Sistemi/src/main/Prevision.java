package main;

import java.net.URL;

import boilerpipe.Boilerpipe;
import eu.danieldk.nlp.jitar.cli.CrossValidation;
import eu.danieldk.nlp.jitar.cli.Tag;
import eu.danieldk.nlp.jitar.cli.Train;

public class Prevision {

	public static String[] tagPrevision(URL url) throws Exception {
		String[] datiForTraining = new String[3];
		datiForTraining[0] = "brown";
		datiForTraining[1] = "dataBROWN/training.brown";
		datiForTraining[2] = "dataBROWN/corpus.model";
	
		CrossValidation.doCrossValidation(datiForTraining);
		//Evaluate.doEvaluation(datiForTraining);
		Train.doTraining(datiForTraining);
		
		Boilerpipe bolierpipe = new Boilerpipe();
		String[] site = bolierpipe.getText(url);
		String title = site[0];
		/*bisogna scartare la fine del titolo (Es. – Last.fm)
		 * come su PosTitle
		 */
		title = title.replace("  ", " ");
		title = title.trim();
		
		
		String tag = Tag.doTagging(datiForTraining[2], title);
		
		String[] parole = title.split(" ");
		String[] tags = tag.split(" ");
		
		System.out.println(parole.length +" "+ title);
		System.out.println(tags.length +" "+tag);
		
		String PERSONA = "";
		String CITTA = "";
		String SEDE = "";
		String DATA = "";
		
		int i;
		for(i=0;i<tags.length;i++) {
			if(tags[i].equals("PPP")) {
				PERSONA += parole[i]+" ";
			}
			if(tags[i].equals("CCC")) {
				CITTA += parole[i]+" ";
			}
			if(tags[i].equals("SSS")) {
				SEDE += parole[i]+" ";
			}
			if(tags[i].equals("DDD")) {
				DATA += parole[i]+" ";
			}
		}
		PERSONA.trim();
		CITTA.trim();
		SEDE.trim();
		DATA.trim();
		String[] evento = new String[]{PERSONA,CITTA,SEDE,DATA};
		return evento;
	}
		
	public static void main(String[] args) throws Exception {
		
		URL url = new URL("http://www.last.fm/event/4066436+Brandon+Flowers+at+Webster+Hall+on+24+March+2015");
		String[] evento = tagPrevision(url);
		System.out.println("PERSONA: " + evento[0]);
		System.out.println("CITTA: " + evento[1]);
		System.out.println("SEDE: " + evento[2]);
		System.out.println("DATA: " + evento[3]);
	}
}
