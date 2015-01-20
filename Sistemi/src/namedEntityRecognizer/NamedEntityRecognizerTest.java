package namedEntityRecognizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;

public class NamedEntityRecognizerTest {

	public static void main(String[] args) throws Exception {

		String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";
		//String serializedClassifier = "classifiers/english.conll.4class.distsim.crf.ser.gz";
		//String serializedClassifier = "classifiers/english.muc.7class.distsim.crf.ser.gz";
		//String serializedClassifier = "classifiers/english.nowiki.3class.distsim.crf.ser.gz";

		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);


		String[] example = {"Good afternoon Rajat Raina, how are you today?",
		"I go to school at Stanford University, which California is located in California." }; //modifica
		
		System.out.println("STAMPATA NUMERO 1"); //frase con parola/categoria
		
		for (String str : example) {
			System.out.println(classifier.classifyToString(str));
		}
		System.out.println("---");
		
		System.out.println("STAMPATA NUMERO 2"); //uguale alla 1 praticamente...

		for (String str : example) {
			// This one puts in spaces and newlines between tokens, so just print not println.
			System.out.print(classifier.classifyToString(str, "slashTags", false));
		}
		System.out.println("---");
		
		System.out.println("STAMPATA NUMERO 3"); //frase con categorie...

		for (String str : example) {
			// This one is best for dealing with the output as a TSV (tab-separated column) file.
			// The first column gives entities, the second their classes, and the third the remaining text in a document
			System.out.print(classifier.classifyToString(str, "tabbedEntities", false));
		}
		System.out.println("---");
		
		System.out.println("STAMPATA NUMERO 4"); //dove iniziano e finiscono le categorie

		for (String str : example) {
			System.out.println(classifier.classifyWithInlineXML(str));
		}
		System.out.println("---");
		
		System.out.println("STAMPATA NUMERO 5"); //ogni parola analizzata--> categoria= entity

		for (String str : example) {
			System.out.println(classifier.classifyToString(str, "xml", true));
		}
		System.out.println("---");
		
		System.out.println("STAMPATA NUMERO 6"); // tutte le parole in colonna

		for (String str : example) {
			System.out.print(classifier.classifyToString(str, "tsv", false));
		}
		System.out.println("---");
		System.out.println("---");

		// This gets out entities with character offsets
		
		System.out.println("STAMPATA NUMERO 7"); //dove iniziano e finiscono le categorie---> solo le categorie
		int j = 0;
		for (String str : example) {
			j++;
			List<Triple<String,Integer,Integer>> triples = classifier.classifyToCharacterOffsets(str);
			for (Triple<String,Integer,Integer> trip : triples) {
				System.out.printf("%s over character offsets [%d, %d) in sentence %d.%n",
						trip.first(), trip.second(), trip.third, j);
			}
		}
		System.out.println("---");

		// This prints out all the details of what is stored for each token
		
		System.out.println("STAMPATA NUMERO 8"); // tutte le parole analizzate in maniera approfondita
		int i=0;
		for (String str : example) {
			for (List<CoreLabel> lcl : classifier.classify(str)) {
				for (CoreLabel cl : lcl) {
					System.out.print(i++ + ": ");
					System.out.println(cl.toShorterString());
					System.out.println(cl.originalText()); //la stringa
					System.out.println(cl.get(CoreAnnotations.AnswerAnnotation.class)); //la categoria
					
				}
			}
		}

		System.out.println("---");
		
		
		
		
		HashMap<String,Integer> str2occ = new HashMap<String,Integer>(); //chiave= stringa(parola); valore= occorrenza
		
		
		for (String str : example) {
			for (List<CoreLabel> lcl : classifier.classify(str)) {
				for (CoreLabel cl : lcl) {
					String stringa = cl.originalText();
					String entity = cl.get(CoreAnnotations.AnswerAnnotation.class);
					if (entity.equals("LOCATION") || entity.equals("PERSON") || entity.equals("ORGANIZATION") ) {
						boolean trovato = false;
						for (String key: str2occ.keySet() ) { 
							if (key.equals(stringa)) {
								trovato = true;
								str2occ.put(key, str2occ.get(key) +1 );
							}
						}
						if (!trovato) {
							str2occ.put(stringa, 1); 
						}
						
					}
				}
			}
		}
			
		
		System.out.println("STAMPO LA MIA MAPPA");
		
		for (String key: str2occ.keySet() ) {
			System.out.println("Key : " + key.toString() + " Value : "+ str2occ.get(key));
			
			
		}
		

	}
}
