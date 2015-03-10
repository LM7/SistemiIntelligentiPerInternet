package eu.danieldk.nlp.jitar.cli;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import eu.danieldk.nlp.jitar.cli.*;

public class main {

	public static void main(String[] args) throws IOException {
		
		String[] datiForTraining = new String[3];
		datiForTraining[0] = "brown";
		datiForTraining[1] = "training.brown";
		datiForTraining[2] = "corpus.model";
	
		CrossValidation.doCrossValidation(datiForTraining);
		//Evaluate.doEvaluation(datiForTraining);
		Train.doTraining(datiForTraining);
		
		List<String> titoliDaTaggare = new ArrayList<String>();
		//List<String> titoliDaTaggare = lettura del file test con i titoli da testare
		titoliDaTaggare.add("The Frontier Ruckus Mercury Lounge January 30 , 2015");
		
		Tag.doTagging(datiForTraining[2], titoliDaTaggare);
		
	}
   
}
