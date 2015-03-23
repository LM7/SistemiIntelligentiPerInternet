package Final_Version.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class FileMapForTesting {

	public static void fromMapToText(HashMap<String, ArrayList<String[]>> test, String text){
		try
		{
			// Mi prendo di prepotenza il txt 
			File logFile = new File(text);
			Writer writer = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(logFile), "UTF-8"));

			// Mi prendo tutte le combinazioni
			for(Entry<String, ArrayList<String[]>> entry : test.entrySet()) {
				String key = entry.getKey();
				ArrayList<String[]> value = entry.getValue();

				// Per ogni dominio stampo su ogni riga la lista degli eventi
				for(String[] info : value)
				{
					writer.write(key + " § ");
					writer.write(info[0] + " § ");
					writer.write(info[1] + " § ");
					writer.write(info[2] + " § ");
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

	@SuppressWarnings("resource")
	public static HashMap<String, ArrayList<String[]>> fromTextToMap(String text){
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(text), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		HashMap<String, ArrayList<String[]>> output = new HashMap<String, ArrayList<String[]>>();

		// Finchï¿½ï¿½ï¿½ï¿½ ho righe da leggere
		String str;
		try {
			while ((str = in.readLine()) != null) {
				String[] columns = str.split(" § ");

				ArrayList<String[]> alInfo = output.get(columns[0]);
				if(alInfo == null)
					alInfo = new ArrayList<String[]>();
					String[] info = {columns[1],columns[2],columns[3],columns[4]};
					alInfo.add(info);
					output.put(columns[0], alInfo);
			}
		} catch (IOException e) {
			System.out.println("Error file");
			e.printStackTrace();
		}

		System.out.println(output.toString());

		return output;
	}

	/*public static void main(String[] args) {
		// Inizializzo la mappa che userï¿½ï¿½ï¿½ï¿½ come test..
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
