package main;

import eu.danieldk.nlp.jitar.cli.CrossValidation;
import eu.danieldk.nlp.jitar.cli.Tag;
import eu.danieldk.nlp.jitar.cli.Train;

public class PrevisionTitle {
	
	public String[] tagPrevisionTitle(String title) throws Exception {
		String[] datiForTraining = new String[3];
		datiForTraining[0] = "brown";
		datiForTraining[1] = "dataBROWN/trainingTaggaPuntSel.brown";
		datiForTraining[2] = "dataBROWN/corpus.model";
	
		CrossValidation.doCrossValidation(datiForTraining);
		//Evaluate.doEvaluation(datiForTraining);
		Train.doTraining(datiForTraining);
		
		String tag = Tag.doTagging(datiForTraining[2], title);
		
		String[] parole = title.split(" ");
		String[] tags = tag.split(" ");
		
		System.out.println("("+parole.length +") "+ title);
		System.out.println("("+tags.length +") "+tag);
		
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
		PERSONA = PERSONA.trim();
		CITTA = CITTA.trim();
		SEDE = SEDE.trim();
		DATA = DATA.trim();
		String[] evento = new String[]{PERSONA,CITTA,SEDE,DATA};
		return evento;
	}

}
