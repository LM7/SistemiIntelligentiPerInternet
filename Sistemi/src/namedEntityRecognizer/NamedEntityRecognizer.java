package namedEntityRecognizer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import boilerpipe.Boilerpipe;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;


public class NamedEntityRecognizer {

	public static final int IMPORTANZA_TITOLO = 10;

	public final static HashSet<String> STOP_LOCATION = new HashSet<String>(Arrays.asList("venues","venue","WordPress.com","big","big red","local","Gamespot","events","met","Europe","death","Songkick"));

	public static void main(String[] args) throws Exception {

		String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";
		//String serializedClassifier = "classifiers/english.conll.4class.distsim.crf.ser.gz";
		//String serializedClassifier = "classifiers/english.muc.7class.distsim.crf.ser.gz";
		//String serializedClassifier = "classifiers/english.nowiki.3class.distsim.crf.ser.gz";

		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);

		//Prova con Boilerpipe
		Boilerpipe b = new Boilerpipe();
		URL url = new URL("http://www.last.fm/event/3971840+Ryan+Adams+at+Hammersmith+Apollo+on+27+February+2015");
		String title = b.getText(url)[0];
		String text = b.getText(url)[1];
		String[] urls = new String[]{title,text}; 

		NamedEntityRecognizer ner = new NamedEntityRecognizer();

		ArrayList<HashMap<String, int[]>> listaMappe = ner.createListOfMapEntity(urls);
		
		ner.stampaListaMappe(listaMappe);


	}





	/* Restituisce una lista di mappe: Location -> 0; Person -> 1; Organization -> 2; chiave= stringa(parola); valore= occorrenza */
	public ArrayList<HashMap<String,int[]>> createListOfMapEntity(String[] urls) throws ClassCastException, ClassNotFoundException, IOException {
		String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";
		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);


		HashMap<String,int[]> str2occLoc = new HashMap<String,int[]>(); 
		HashMap<String,int[]> str2occPer = new HashMap<String,int[]>();
		HashMap<String,int[]> str2occOrg = new HashMap<String,int[]>();
		ArrayList<HashMap<String, int[]>> listEntity = new ArrayList<HashMap<String, int[]>>();
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
					if ( ! (STOP_LOCATION.contains(stringa)) ) {
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
										int[] appoggio = str2occLoc.get(key);
										appoggio[1] = appoggio[1] + 1;
										if (title == 2) {
											appoggio[0] = 1;
										}
										else {
											appoggio[0] = -1;
										}


									}
								}
								if (!trovato) {
									int[] appoggio = new int[2];
									if (title == 2) {
										appoggio[0] = 1;
										appoggio[1] = 1;
										str2occLoc.put(stringa, appoggio);
									}
									else {
										appoggio[0] = -1;
										appoggio[1] = 1;
										str2occLoc.put(stringa, appoggio);
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
										int[] appoggio = str2occPer.get(key);
										appoggio[1] = appoggio[1] + 1;
										if (title == 2) {
											appoggio[0] = 1;
										}
										else {
											appoggio[0] = -1;
										}
									}
								}
								if (!trovato) {
									int[] appoggio = new int[2];
									if (title == 2) {
										appoggio[0] = 1;
										appoggio[1] = 1;
										str2occPer.put(stringa, appoggio);
									} 
									else {
										appoggio[0] = -1;
										appoggio[1] = 1;
										str2occPer.put(stringa, appoggio);
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
										int[] appoggio = str2occOrg.get(key);
										appoggio[1] = appoggio[1] + 1;
									}
								}
								if (!trovato) {
									int[] appoggio = new int[2];
									appoggio[0] = 0;
									appoggio[1] = 1;
									str2occOrg.put(stringa, appoggio); 
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
		}

		return listEntity;

	}





	private void stampaListaMappe(ArrayList<HashMap<String, int[]>> listEntity) {
		System.out.println("STAMPO LA MIA MAPPA");

		int cont;
		HashMap<String,int[]> mapHelp = new HashMap<String,int[]>(); //mappa d'appoggio per stampare

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
				System.out.println("Key : " + key.toString() + " Value : "+ stampaFeatures(mapHelp.get(key)));
			}
		}

		System.out.println("Lunghezza della mia lista di mappe: "+ listEntity.size());
	}
	
	

	private static String stampaFeatures(int[] array) {
		int i;
		String s = "";
		for(i=0;i<array.length-1;i++)
			s += array[i]+":";
		s+= array[i];
		return s;
	}

}


