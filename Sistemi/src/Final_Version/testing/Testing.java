package Final_Version.testing;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import Final_eu.danieldk.nlp.jitar.cli.CrossValidation;
import Final_eu.danieldk.nlp.jitar.cli.Tag;
import Final_eu.danieldk.nlp.jitar.cli.Train;
import Final_Version.parser.FileMapForTesting;
import Final_Version.suTime.SUTime_Titoli;

public class Testing {
	
	public static void testingTitle() throws Exception {
		/*File in cui verranno stampati tutti i risultati*/
		PrintWriter testingResults = new PrintWriter ("FinalDataBROWN/TestingResults.txt", "UTF-8");
		
		
		/*CreateTitleForTesting createTitleTest = new CreateTitleForTesting(); NON SERVE PIU'
		HashMap<String, ArrayList<String[]>> mapForTest = createTitleTest.createTitle();*/
		
		HashMap<String, ArrayList<String[]>> mapForTest = FileMapForTesting.fromTextToMap("FinalDataBROWN/Map2Text.txt");
		
		String[] eventoPrevision = new String[4];
		String title, cantante, data, luogo;
		
		double risTot = 0;
		double titoliTot = 0;
		int ris;
		int[] risArray = new int[4];
		double treTot = 0;
		double dueTot = 0;
		double unoTot = 0;
		double zeroTot = 0;
		double cantanteTot = 0;
		double luogoTot = 0;
		double dataTot = 0;
		double percentualePunti, percentualeTre, percentualeDue, percentualeUno, percentualeZero, percentualeCantante, percentualeLuogo, percentualeData;
		double treUrl;
		double dueUrl;
		double unoUrl;
		double zeroUrl;
		double titoliTotUrl;
		double urlPercentualeTre, urlPercentualeDue, urlPercentualeUno, urlPercentualeZero;
		
		System.out.println("---------------------STAAAART------------------");
		int k = 0;
		for ( String dominio: mapForTest.keySet() ) {
			ArrayList<String[]> listaArrayStringhe = mapForTest.get(dominio);
			treUrl = 0;
			dueUrl = 0;
			unoUrl = 0;
			zeroUrl = 0;
			titoliTotUrl = 0;
			k = k+1;
			System.out.println("Stiamo al dominio numero: "+k+" su "+mapForTest.size());
			for (String[] quadrupla: listaArrayStringhe) {
				title = quadrupla[0];
				if (title != null && !(title.equals("")) ) {
					titoliTot = titoliTot +1;
					titoliTotUrl = titoliTotUrl +1;
				}
				cantante = quadrupla[1];
				data = quadrupla[2];
				luogo = quadrupla[3];
				eventoPrevision = Testing.tagPrevisionTitle(title);
				risArray = Testing.eventComparePrevision(cantante, data, luogo, eventoPrevision);
				ris = risArray[0];
				risTot = risTot + ris;
				if (ris == 3) { // tutto giusto :)
					treTot = treTot +1;
					cantanteTot = cantanteTot +1;
					luogoTot = luogoTot +1;
					dataTot = dataTot +1;
					treUrl = treUrl +1;
				}
				if (ris == 2) { // giusti due su tre
					dueTot = dueTot +1;
					dueUrl = dueUrl +1;
					if (risArray[1] == 1 ) { //ha sbagliato il cantante
						luogoTot = luogoTot +1;
						dataTot = dataTot +1;
					}
					if (risArray[2] == 1 ) { // ha sbagliato il luogo
						cantanteTot = cantanteTot +1;
						dataTot = dataTot +1;
					}
					if (risArray[3] == 1 ) { // ha sbagliato la data
						cantanteTot = cantanteTot +1;
						luogoTot = luogoTot +1;
					}
				}
				if (ris == 1) { // uno su tre
					unoTot = unoTot +1;
					unoUrl = unoUrl +1;
					if (risArray[1] == 1 && risArray[2] == 1) {  // ha sbagliato il cantante e il luogo
						dataTot = dataTot +1;
					}
					if (risArray[1] == 1 && risArray[3] == 1) { // ha sbagliato il cantante e la data
						luogoTot = luogoTot +1;
					}
					if (risArray[2] == 1 && risArray[3] == 1) { // ha sbagliato il luogo e la data
						cantanteTot = cantanteTot +1;
					}
					
				}
				if (ris == 0) { // tutto sbagliato :(
					zeroTot = zeroTot +1;
					zeroUrl = zeroUrl +1;
				}
			}
			
			urlPercentualeTre = (treUrl * 100) / (titoliTotUrl);
			urlPercentualeDue = (dueUrl * 100) / (titoliTotUrl);
			urlPercentualeUno = (unoUrl * 100) / (titoliTotUrl);
			urlPercentualeZero = (zeroUrl * 100) / (titoliTotUrl);
			
			urlPercentualeTre = Testing.only2Decimals(urlPercentualeTre);
			urlPercentualeDue = Testing.only2Decimals(urlPercentualeDue);
			urlPercentualeUno = Testing.only2Decimals(urlPercentualeUno);
			urlPercentualeZero = Testing.only2Decimals(urlPercentualeZero);
			
			
			//per file
		    testingResults.println("ECCO LA PERCENTUALE DEL DOMINIO: "+dominio);
		    testingResults.println("TITOLI TROVATI: "+titoliTotUrl);
		    testingResults.println("PERCENTUALE TRE: "+urlPercentualeTre+"%");
		    testingResults.println("PERCENTUALE DUE: "+urlPercentualeDue+"%");
		    testingResults.println("PERCENTUALE UNO: "+urlPercentualeUno+"%");
		    testingResults.println("PERCENTUALE ZERO: "+urlPercentualeZero+"%");
		    testingResults.println("");
		    
		}
		
		percentualePunti = (risTot * 100) / (titoliTot*3);
		percentualeTre = (treTot * 100) / titoliTot;
		percentualeDue = (dueTot * 100) / titoliTot;
		percentualeUno = (unoTot * 100) / titoliTot;
		percentualeZero = (zeroTot * 100) / titoliTot;
		
		percentualeCantante = (cantanteTot * 100) / (titoliTot);
		percentualeLuogo = (luogoTot * 100) / titoliTot;
		percentualeData = (dataTot * 100) / titoliTot;
		
		percentualePunti = Testing.only2Decimals(percentualePunti);
		percentualeTre = Testing.only2Decimals(percentualeTre);
		percentualeDue = Testing.only2Decimals(percentualeDue);
		percentualeUno = Testing.only2Decimals(percentualeUno);
		percentualeZero = Testing.only2Decimals(percentualeZero);
		
		percentualeCantante = Testing.only2Decimals(percentualeCantante);
		percentualeLuogo = Testing.only2Decimals(percentualeLuogo);
		percentualeData = Testing.only2Decimals(percentualeData);
		
		System.out.println("TUTTE LE PERCENTUALI:");

		System.out.println("PERCENTUALE PUNTI: "+ percentualePunti+"%");
		System.out.println("PERCENTUALE TRE: "+ percentualeTre+"%");
		System.out.println("PERCENTUALE DUE: "+ percentualeDue+"%");
		System.out.println("PERCENTUALE UNO: "+ percentualeUno+"%");
		System.out.println("PERCENTUALE ZERO: "+ percentualeZero+"%");
		System.out.println("PERCENTUALE CANTANTE: "+ percentualeCantante+"%");
		System.out.println("PERCENTUALE LUOGO: "+ percentualeLuogo+"%");
		System.out.println("PERCENTUALE DATA: "+ percentualeData+"%");
		
		//per file
		
		testingResults.println("TITOLI TOTALI TROVATI: "+titoliTot);
		testingResults.println("TUTTE LE PERCENTUALI:");

		testingResults.println("PERCENTUALE PUNTI: "+ percentualePunti+"%");
		testingResults.println("PERCENTUALE TRE: "+ percentualeTre+"%");
		testingResults.println("PERCENTUALE DUE: "+ percentualeDue+"%");
		testingResults.println("PERCENTUALE UNO: "+ percentualeUno+"%");
		testingResults.println("PERCENTUALE ZERO: "+ percentualeZero+"%");
		testingResults.println("PERCENTUALE CANTANTE: "+ percentualeCantante+"%");
		testingResults.println("PERCENTUALE LUOGO: "+ percentualeLuogo+"%");
		testingResults.println("PERCENTUALE DATA: "+ percentualeData+"%");

		System.out.println("---------------------------FINE------------------------");
		
		testingResults.close();
				
	}

	private  static int[] eventComparePrevision(String cantante, String data, String luogo, String[] eventoPrevision) {
		String cantantePrevision = eventoPrevision[0];
		String cittaPrevision = eventoPrevision[1];
		String sedePrevision = eventoPrevision[2];
		String dataPrevision = eventoPrevision[3];
		String luogoPrevision = cittaPrevision + " " + sedePrevision;
		int[] risultato = new int[]{0,0,0,0}; // risultato, cantante, luogo, data--> 1 se colpa sua, 0 no colpa sua
		
		if ( (cantante != null) && (cantantePrevision != null) && !(cantante.equals("")) && !(cantantePrevision.equals("")) ) {
			if ( cantante.equalsIgnoreCase(cantantePrevision) || cantante.contains(cantantePrevision) || cantantePrevision.contains(cantante) ) { //equals +1
				risultato[0] = risultato[0] +1;
			}
			else {
				risultato[1] = 1;
			}
		}
		
		// ROBA DATE....
		DateFormat format = new SimpleDateFormat("MMMMMMMMMM dd yyyy", Locale.ENGLISH);
		boolean dataOK = false;
		try {
			Date dataVera = format.parse(data);
			SUTime_Titoli suTime = new SUTime_Titoli();
			Date dataPrevisionVera = suTime.fromDataStringToDataDate(dataPrevision);
			if ( (dataPrevisionVera != null) && (dataVera != null) && (dataVera.equals(dataPrevisionVera)) ) {
				dataOK = true;
			}
		}
		catch (ParseException e) {
			//e.printStackTrace();
		}
		
		
		if ( (data != null) && (dataPrevision != null) && !(data.equals("")) && !(dataPrevision.equals("")) ) {
			if ( data.equalsIgnoreCase(dataPrevision) || data.contains(dataPrevision) || dataPrevision.contains(data) || dataOK ) {
				risultato[0] = risultato[0] +1;
			}
			else {
				risultato[3] = 1;
			}
		}
		
		if ( (luogo != null) && (luogoPrevision != null) && !(luogo.equals("")) && !(luogoPrevision.equals("")) ) {
			if ( luogo.equalsIgnoreCase(luogoPrevision) || luogo.contains(luogoPrevision) || luogoPrevision.contains(luogo) 
					|| luogo.contains(cittaPrevision) || luogo.contains(sedePrevision)) {
				risultato[0] = risultato[0] +1;
			}
			else {
				risultato[2] = 1;
			}
		}
		
		return risultato;
		
	}
	
	private static double only2Decimals(double numero) {
		BigDecimal i = new BigDecimal(numero, new MathContext(3));
		numero = i.doubleValue();
		return numero;
	}
	
	public static String[] tagPrevisionTitle(String title) throws Exception {
		String[] datiForTraining = new String[3];
		datiForTraining[0] = "brown";
		datiForTraining[1] = "FinalDataBROWN/training.brown";
		datiForTraining[2] = "FinalDataBROWN/corpus.model";
	
		CrossValidation.doCrossValidation(datiForTraining);
		//Evaluate.doEvaluation(datiForTraining);
		Train.doTraining(datiForTraining);
		
		String tag = Tag.doTagging(datiForTraining[2], title);
		
		String[] parole = title.split(" ");
		String[] tags = tag.split(" ");
		
		
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

	public static void main(String[] args) throws Exception {
		Testing.testingTitle();

	}

}
