package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Testing {
	
	public void testingTitle() throws Exception {
		/*File in cui verranno stampati tutti i risultati*/
		PrintWriter testingResults = new PrintWriter ("TestingResults.txt", "UTF-8");
		
		PrevisionTitle previsionTitle = new PrevisionTitle();
		
		/*CreateTitleForTesting createTitleTest = new CreateTitleForTesting(); NON SERVE PIU'
		HashMap<String, ArrayList<String[]>> mapForTest = createTitleTest.createTitle();*/
		
		HashMap<String, ArrayList<String[]>> mapForTest = FileMapForTesting.fromTextToMap("Map2Text.txt");
		
		String[] eventoPrevision = new String[4];
		String title, cantante, data, luogo;
		
		int risTot = 0;
		int titoliTot = 0;
		int ris;
		int[] risArray = new int[4];
		int treTot = 0;
		int dueTot = 0;
		int unoTot = 0;
		int zeroTot = 0;
		int cantanteTot = 0;
		int luogoTot = 0;
		int dataTot = 0;
		double percentualePunti, percentualeTre, percentualeDue, percentualeUno, percentualeZero, percentualeCantante, percentualeLuogo, percentualeData;
		int treUrl;
		int dueUrl;
		int unoUrl;
		int zeroUrl;
		int titoliTotUrl;
		double urlPercentualeTre, urlPercentualeDue, urlPercentualeUno, urlPercentualeZero;
		
		
		for ( String url: mapForTest.keySet() ) {
			ArrayList<String[]> listaArrayStringhe = mapForTest.get(url);
			System.out.println("---------------------STAAAART------------------");
			System.out.println("URL: "+url);
			treUrl = 0;
			dueUrl = 0;
			unoUrl = 0;
			zeroUrl = 0;
			titoliTotUrl = 0;
			for (String[] quadrupla: listaArrayStringhe) {
				title = quadrupla[0];
				if (title != null && !(title.equals("")) ) {
					titoliTot = titoliTot +1;
					titoliTotUrl = titoliTotUrl +1;
				}
				cantante = quadrupla[1];
				data = quadrupla[2];
				luogo = quadrupla[3];
				eventoPrevision = previsionTitle.tagPrevisionTitle(title);
				risArray = eventComparePrevision(cantante, data, luogo, eventoPrevision);
				ris = risArray[0];
				risTot = risTot + ris;
				if (ris == 3) { // tutto giusto :)
					treTot = treTot +1;
					cantanteTot = cantanteTot +1;
					luogoTot = luogoTot +1;
					dataTot = dataTot +1;
					treUrl = treUrl +1;
					System.out.println("TUTTI GIUSTI");
				}
				if (ris == 2) { // giusti due su tre
					dueTot = dueTot +1;
					dueUrl = dueUrl +1;
					if (risArray[1] == 1 ) { //ha sbagliato il cantante
						luogoTot = luogoTot +1;
						dataTot = dataTot +1;
						System.out.println("CANTANTE SBAGLIATO");
					}
					if (risArray[2] == 1 ) { // ha sbagliato il luogo
						cantanteTot = cantanteTot +1;
						dataTot = dataTot +1;
						System.out.println("LUOGO SBAGLIATO");
					}
					if (risArray[3] == 1 ) { // ha sbagliato la data
						cantanteTot = cantanteTot +1;
						luogoTot = luogoTot +1;
						System.out.println("DATA SBAGLIATA");
					}
				}
				if (ris == 1) { // uno su tre
					unoTot = unoTot +1;
					unoUrl = unoUrl +1;
					if (risArray[1] == 1 && risArray[2] == 1) {  // ha sbagliato il cantante e il luogo
						dataTot = dataTot +1;
						System.out.println("CANTANTE e LUOGO SBAGLIATI");
					}
					if (risArray[1] == 1 && risArray[3] == 1) { // ha sbagliato il cantante e la data
						luogoTot = luogoTot +1;
						System.out.println("CANTANTE e DATA SBAGLIATI");
					}
					if (risArray[2] == 1 && risArray[3] == 1) { // ha sbagliato il luogo e la data
						cantanteTot = cantanteTot +1;
						System.out.println("LUOGO e DATA SBAGLIATI");
					}
					
				}
				if (ris == 0) { // tutto sbagliato :(
					zeroTot = zeroTot +1;
					zeroUrl = zeroUrl +1;
					System.out.println("TUTTO SBAGLIATO");
				}
				System.out.println("TITOLO: "+ title+"; PUNTI PRESI: "+ris);
				System.out.println("CANTANTE GIUSTO:"+ cantante+"; DATA GIUSTA:"+data+"; LUOGO GIUSTO:"+luogo);
				System.out.println("EVENTO PREVISION: CANTANTE:"+eventoPrevision[0]+"; CITTA:"+eventoPrevision[1]+";SEDE:"+eventoPrevision[2]+"; DATA:"+eventoPrevision[3]+";");
			}
			//prima di entrare nel primo for e cambiare url (poi se lo vogliamo spostare si sposta facile)
			urlPercentualeTre = (treUrl * 100) / (titoliTotUrl);
			urlPercentualeDue = (dueUrl * 100) / (titoliTotUrl);
			urlPercentualeUno = (unoUrl * 100) / (titoliTotUrl);
			urlPercentualeZero = (zeroUrl * 100) / (titoliTotUrl);
			System.out.println("ECCO LA PERCENTUALE DEL DOMINIO: "+url);
			System.out.println("PERCENTUALE TRE: "+urlPercentualeTre+"%");
			System.out.println("PERCENTUALE DUE: "+urlPercentualeDue+"%");
			System.out.println("PERCENTUALE UNO: "+urlPercentualeUno+"%");
			System.out.println("PERCENTUALE ZERO: "+urlPercentualeZero+"%");
			
			//per file
		    testingResults.println("ECCO LA PERCENTUALE DEL DOMINIO: "+url);
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

	private int[] eventComparePrevision(String cantante, String data, String luogo, String[] eventoPrevision) {
		String cantantePrevision = eventoPrevision[0];
		String cittaPrevision = eventoPrevision[1];
		String sedePrevision = eventoPrevision[2];
		String dataPrevision = eventoPrevision[3];
		String luogoPrevision = cittaPrevision + " " + sedePrevision;
		int[] risultato = new int[]{0,0,0,0}; // risultato, cantante, luogo, data--> 1 se colpa sua, 0 no colpa sua
		
		if ( (cantante != null) && (cantantePrevision != null) && !(cantante.equals("")) && !(cantantePrevision.equals("")) ) {
			if ( cantante.equalsIgnoreCase(cantantePrevision) || cantante.contains(cantantePrevision) || cantantePrevision.contains(cantante) ) { //equals +1
				risultato[0] = risultato[0] +1;
				System.out.println("CANTANTE UGUALEEEEEEEEEEEEEEEEEEE");
			}
			else {
				risultato[1] = 1;
				System.out.println("CANTANTE NOOOOON UGUALEEEEEEEEEEEEEEEEEEE");
			}
		}
		
		// ROBA DATE....
		DateFormat format = new SimpleDateFormat("MMMMMMMMMM dd yyyy", Locale.ENGLISH);
		DateFormat format2 = new SimpleDateFormat("dd MMMMMMMMMM yyyy", Locale.ENGLISH); //si dovrebbero aggiungere piu' formati possibili
		boolean dataOK = false;
		try {
			Date dataVera = format.parse(data);
			Date dataPrevisionVera = format.parse(dataPrevision);
			System.out.println("DATA VERA: "+dataVera+"; DATA PREVISION VERA:"+dataPrevisionVera);
			if (dataVera.equals(dataPrevisionVera)) {
				dataOK = true;
				System.out.println("DAJEEEEEEEEEEEEEEEEEEEE DENTRO IL PRIMO TRYYYYYYYY");
			}
		}
		catch (ParseException e) {
			//e.printStackTrace();
			try {
				Date dataVera = format.parse(data);
				Date dataPrevisionVera2 = format2.parse(dataPrevision);
				if (dataVera.equals(dataPrevisionVera2)) {
					dataOK = true;
					System.out.println("DAJEEEEEEEEEEEEEEEEEEEE DENTRO IL SECONDO TRYYYYYYYY");
				}
			}
			catch (ParseException ee) {
				//e.printStackTrace();
				try {
					Date dataPrevisionVera = format.parse(dataPrevision);
					Date dataVera2 = format2.parse(data);
					if (dataPrevisionVera.equals(dataVera2)) {
						dataOK = true;
						System.out.println("DAJEEEEEEEEEEEEEEEEEEEE DENTRO IL TERZO TRYYYYYYYY");
					}
				}
				catch (ParseException eee) {
					try {
						Date dataPrevisionVera2 = format2.parse(dataPrevision);
						Date dataVera2 = format2.parse(data);
						if (dataPrevisionVera2.equals(dataVera2)) {
							dataOK = true;
							System.out.println("DAJEEEEEEEEEEEEEEEEEEEE DENTRO IL QUARTO TRYYYYYYYY");
						}
					}
					catch (ParseException eeee) {
					}
				}
			}
		}
		//fino qui parte new delle date
		
		
		if ( (data != null) && (dataPrevision != null) && !(data.equals("")) && !(dataPrevision.equals("")) ) {
			if ( data.equalsIgnoreCase(dataPrevision) || data.contains(dataPrevision) || dataPrevision.contains(data) || dataOK ) {
				risultato[0] = risultato[0] +1;
				System.out.println("DATAAAAA UGUALEEEEEEEEEEEEEEEEEEE");
			}
			else {
				risultato[3] = 1;
				System.out.println("DATA NOOOOON UGUALEEEEEEEEEEEEEEEEEEE");
			}
		}
		
		if ( (luogo != null) && (luogoPrevision != null) && !(luogo.equals("")) && !(luogoPrevision.equals("")) ) {
			if ( luogo.equalsIgnoreCase(luogoPrevision) || luogo.contains(luogoPrevision) || luogoPrevision.contains(luogo) 
					|| luogo.contains(cittaPrevision) || luogo.contains(sedePrevision)) {
				risultato[0] = risultato[0] +1;
				System.out.println("LUOGO UGUALEEEEEEEEEEEEEEEEEEE");
			}
			else {
				risultato[2] = 1;
				System.out.println("LUOGO NOOOOON UGUALEEEEEEEEEEEEEEEEEEE");
			}
		}
		
		return risultato;
		
	}

	public static void main(String[] args) throws Exception {
		Testing testing = new Testing();
		testing.testingTitle();

	}

}
