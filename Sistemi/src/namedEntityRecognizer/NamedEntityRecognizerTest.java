package namedEntityRecognizer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import boilerpipe.Boilerpipe;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;

public class NamedEntityRecognizerTest {
	
	public static final int IMPORTANZA_TITOLO = 10;

	public static void main(String[] args) throws Exception {

		String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";
		//String serializedClassifier = "classifiers/english.conll.4class.distsim.crf.ser.gz";
		//String serializedClassifier = "classifiers/english.muc.7class.distsim.crf.ser.gz";
		//String serializedClassifier = "classifiers/english.nowiki.3class.distsim.crf.ser.gz";

		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);
		
		//Prova con Boilerpipe
		/*Boilerpipe b = new Boilerpipe();
		URL url = new URL("http://www.last.fm/event/3917258+Ed+Sheeran+at+Palalottomatica+on+26+January+2015");
		String title = b.getText(url)[0];
		String text = b.getText(url)[1];*/
		

		String[] example = {"Good afternoon Rajat Raina, how are you today?",
		"I go to school at Stanford University, which California is located in California, New York" }; //modifica
		
		//String[] example2 = {title, text};
		
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
		String categoria = "";
		String prev = "";
		String concat = "";
		String entityNow = "";
		for (String str : example) {
			for (List<CoreLabel> lcl : classifier.classify(str)) {
				for (CoreLabel cl : lcl) {
					System.out.print(i++ + ": ");
					System.out.println(cl.toShorterString());
					System.out.println(cl.originalText()); //la stringa
					System.out.println(cl.get(CoreAnnotations.AnswerAnnotation.class));//la categoria
					entityNow = cl.get(CoreAnnotations.AnswerAnnotation.class);
					// Provo a unire due o più categorie consecutive
					if (entityNow.equals(categoria) && ( entityNow.equals("LOCATION") || entityNow.equals("PERSON") || entityNow.equals("ORGANIZATION") )  ) {
						concat = prev + " "+ cl.originalText();
						cl.setOriginalText(concat);
						System.out.println(cl.originalText());
						System.out.println(cl.get(CoreAnnotations.AnswerAnnotation.class));//la categoria
						/*System.out.println("LA STRINGA PRECEDENTE: "+prev );
						System.out.println("LA STRINGA CONCATENATA: "+concat);*/
						prev = concat;
					}
					else {
						prev = cl.originalText();
						concat = "";
					}
					categoria = entityNow;
					
					
				}
			}
		}

		System.out.println("---");
		
		
	}
		
		
		
		
		
	/* Restituisce una lista di mappe: Location -> 0; Person -> 1; Organization -> 2; chiave= stringa(parola); valore= occorrenza */
	public ArrayList<HashMap<String,Integer>> createListOfMapEntity(String[] urls) throws ClassCastException, ClassNotFoundException, IOException {
		String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";
		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);
		
		
		HashMap<String,Integer> str2occLoc = new HashMap<String,Integer>(); 
		HashMap<String,Integer> str2occPer = new HashMap<String,Integer>();
		HashMap<String,Integer> str2occOrg = new HashMap<String,Integer>();
		ArrayList<HashMap<String, Integer>> listEntity = new ArrayList<HashMap<String, Integer>>();
		listEntity.add(0, str2occLoc);
		listEntity.add(1, str2occPer);
		listEntity.add(2, str2occOrg);
		
		//Variabili per unire stesse categorie consecutive
		String categoria = "";
		String prev = "";
		String concat = "";
		String delete = "";
		boolean del = false;

		
		int title = 0;

		for (String str : urls) {
			title = title + 2;
			for (List<CoreLabel> lcl : classifier.classify(str)) {
				for (CoreLabel cl : lcl) {
					String stringa = cl.originalText();
					String entity = cl.get(CoreAnnotations.AnswerAnnotation.class);
					

					if (entity.equals("LOCATION") || entity.equals("PERSON") || entity.equals("ORGANIZATION") ) {
						
						if (entity.equals(categoria) && ( entity.equals("LOCATION") || entity.equals("PERSON") )  ) {
							concat = prev + " "+ cl.originalText();
							stringa = concat;
							delete = prev;
							del = true;
							prev = concat;
						}
						else {
							prev = cl.originalText();
							concat = "";
						}

						if (entity.equals("LOCATION")) {
							if (del) {
								str2occLoc.remove(delete);
							}
							boolean trovato = false;
							for (String key: str2occLoc.keySet() ) { 
								if (key.equals(stringa)) {
									trovato = true;
									if (title == 2) {
										str2occLoc.put(key, str2occLoc.get(key) + IMPORTANZA_TITOLO );
									}
									else {
										str2occLoc.put(key, str2occLoc.get(key) +1 );
									}
									
									
								}
							}
							if (!trovato) {
								if (title == 2) {
									str2occLoc.put(stringa, IMPORTANZA_TITOLO);
								}
								else {
									str2occLoc.put(stringa, 1);
								}
								
								
							}

						}
						if (entity.equals("PERSON")) {
							if (del) {
								str2occPer.remove(delete);
							}
							boolean trovato = false;
							for (String key: str2occPer.keySet() ) { 
								if (key.equals(stringa)) {
									trovato = true;
									if (title == 2) {
										str2occPer.put(key, str2occPer.get(key) + IMPORTANZA_TITOLO );
									}
									else {
										str2occPer.put(key, str2occPer.get(key) +1 );
									}
								}
							}
							if (!trovato) {
								if (title == 2) {
									str2occPer.put(stringa, IMPORTANZA_TITOLO);
								} 
								else {
									str2occPer.put(stringa, 1);
								}
							}
						}
						if (entity.equals("ORGANIZATION")) {
							if (del) {
								str2occOrg.remove(delete);
							}
							boolean trovato = false;
							for (String key: str2occOrg.keySet() ) { 
								if (key.equals(stringa)) {
									trovato = true;
									str2occOrg.put(key, str2occOrg.get(key) +1 );
								}
							}
							if (!trovato) {
								str2occOrg.put(stringa, 1); 
							}
						}

					}
					

					if (entity.equals("LOCATION")) {
						listEntity.remove(0);
						listEntity.add(0, str2occLoc);
					}
					if (entity.equals("PERSON")) {
						listEntity.remove(1);
						listEntity.add(1, str2occPer);
					}
					if (entity.equals("ORGANIZATION")) {
						listEntity.remove(2);
						listEntity.add(2, str2occOrg);
					}
					
					categoria = entity;
					del = false;
					

				}
			}
		}


		/*System.out.println("STAMPO LA MIA MAPPA");

		int cont;
		HashMap<String,Integer> mapHelp = new HashMap<String,Integer>(); //mappa d'appoggio per stampare

		for (cont=0; cont<3; cont++) {
			mapHelp = listEntity.get(cont);
			if (cont == 0) {
				System.out.println("LOCATION");
			}
			if (cont == 1) {
				System.out.println("PERSON");
			}
			if (cont == 2) {
				System.out.println("ORGANIZATION");
			}
			for (String key: mapHelp.keySet() ) {
				System.out.println("Key : " + key.toString() + " Value : "+ mapHelp.get(key));
			}
		}

		System.out.println("Lunghezza della mia lista di mappe: "+ listEntity.size());	*/
		
		return listEntity;

	}
	
	
	
	public String entityTop(HashMap<String,Integer> entities ) {
		int max = 0;
		int i = 0;
		String entTop = "";
		for (String entita: entities.keySet()) {
			i = entities.get(entita);
			if (i>max) {
				max = i;
				entTop = entita;
			}
		}
		return entTop;	
	}

}


// SI POTRA' ELIMINARE
/*public String entityTop(HashMap<String,Integer> mappa, String titolo) throws ClassCastException, ClassNotFoundException, IOException {
	String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";
	AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);
	int max = 0;
	int i;
	String entityMoreOcc = "";
	Set<String> entities = mappa.keySet();
	String categoriaPrev = "";
	String stringaPrev = "";
	String categoriaNow = "";
	String concat = "";
	for (String entity: entities) {
		i = mappa.get(entity);
		for (List<CoreLabel> lcl : classifier.classify(titolo)) {
			for (CoreLabel cl : lcl) {
				categoriaNow = cl.get(CoreAnnotations.AnswerAnnotation.class);
				System.out.println("categoriaNow: "+categoriaNow);
				System.out.println("entity: "+entity);
				System.out.println("corelabel: "+cl.originalText());
				if ( (categoriaPrev.equals(categoriaNow)) && (categoriaNow.equals("LOCATION") || categoriaNow.equals("PERSON") || categoriaNow.equals("ORGANIZATION") ) ) {
					System.out.println("nella mia mappa: "+entity);
					concat = stringaPrev + " " + cl.originalText();
					stringaPrev = concat;
					System.out.println("il mio concat: "+concat);
					if (entity.equals(concat)) {
						mappa.put(entity, mappa.get(entity) + IMPORTANZA_TITOLO -1);
						i = mappa.get(entity);
					}
				}
				else {
					if (entity.equals(cl.originalText())) {
						mappa.put(entity, mappa.get(entity) + IMPORTANZA_TITOLO -1);
						i = mappa.get(entity);
					}
					stringaPrev = cl.originalText();
					concat = "";
				}
				categoriaPrev = cl.get(CoreAnnotations.AnswerAnnotation.class);
				System.out.println("categoriaPrev: "+categoriaPrev);
			}
		}
		if (i>max) {
			max = i;
			entityMoreOcc = entity;
		}
		
	}

	System.out.println("STAMPO LA MIA MAPPA DI LOCATION or PERSON");
	HashMap<String,Integer> mapHelp = new HashMap<String,Integer>(); //mappa d'appoggio per stampare
	mapHelp = mappa;
	System.out.println("LOCATION or PERSON");
	for (String key: mapHelp.keySet() ) {
		System.out.println("Key : " + key.toString() + " Value : "+ mapHelp.get(key));
	}


	return entityMoreOcc;
}*/




	
	

