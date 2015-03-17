package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Scanner;

public class FileMapForTesting {

	public static void fromMapToText(HashMap<String, ArrayList<String[]>> test, String text){
		try
		{
			// Mi prendo di prepotenza il txt 
			File logFile = new File(text);
			BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));

			// Mi prendo tutte le combinazioni
			for(Entry<String, ArrayList<String[]>> entry : test.entrySet()) {
				String key = entry.getKey();
				ArrayList<String[]> value = entry.getValue();

				// Per ogni dominio stampo su ogni riga la lista degli eventi
				for(String[] info : value)
				{
					writer.write(key + " : ");
					writer.write(info[0] + " : ");
					writer.write(info[1] + " : ");
					writer.write(info[2] + " : ");
					writer.write(info[3] + "\n");
				}

			}

			// Chiudere il writer, mica stamo ar colosseo
			writer.close();

		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static HashMap<String, ArrayList<String[]>> fromTextToMap(String text){
		Scanner scanner = null;

		try {
			scanner = new Scanner(new FileReader(text));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HashMap<String, ArrayList<String[]>> output = new HashMap<String, ArrayList<String[]>>();

		// Finchè ho righe da leggere
		while (scanner.hasNextLine()) {
			String[] columns = scanner.nextLine().split(" : ");

			ArrayList<String[]> alInfo = output.get(columns[0]);
			if(alInfo == null)
				alInfo = new ArrayList<String[]>();
				String[] info = {columns[1],columns[2],columns[3],columns[4]};
				alInfo.add(info);
				output.put(columns[0], alInfo);
		}

		scanner.close();

		System.out.println(output.toString());

		return output;
	}

	/*public static void main(String[] args) {
		// Inizializzo la mappa che userò come test..
				HashMap<String, ArrayList<String[]>> test;

				// ..la popolo con varie query..
				test = createTitle();

				// ..la converto in testo (dentro 'Map2Text.txt')..
				fromMapToText(test,"Map2Text.txt");

				// ..la ri-converto in mappa..
				test = fromTextToMap("Map2Text.txt");
				// ..e la "stampo" (dentro 'Text2Map.txt').
				fromMapToText(test, "Text2Map.txt");
	}*/

}
