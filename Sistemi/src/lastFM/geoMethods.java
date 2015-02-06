package lastFM;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.umass.lastfm.Event;
import de.umass.lastfm.Geo;
import de.umass.lastfm.PaginatedResult;

public class geoMethods {
	// key per accedere alle API
	public final static String key = "fdc2251ce7ea658e631af3c7709d4d84";
	public final static int NUMEROEVENTI = 15;

	public static ArrayList<String[]> eventsPusher(String citta) {

		// Preparo l'output
		ArrayList<String[]> output = new ArrayList<String[]>();

		// 'topOfTheTops' e' una pagina con i 10 eventi piu' recenti nella citta'
		PaginatedResult<Event> topOfTheTops = Geo.getEvents(citta, "", 1, NUMEROEVENTI, key);

		// Per ogni evento di 'topOfTheTops' popolo 'totale'
		for (Event evento : topOfTheTops) {
			String artista = evento.getHeadliner();
			String luogo = evento.getVenue().getName()+", "+evento.getVenue().getCity();

			SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM dd yyyy");
			String data = DATE_FORMAT.format(evento.getStartDate());
			String month = data.substring(0, 2);
			switch (month) {
				case "01":  month = "January";       break;
				case "02":  month = "February";      break;
				case "03":  month = "March";         break;
				case "04":  month = "April";         break;
				case "05":  month = "May";           break;
				case "06":  month = "June";          break;
				case "07":  month = "July";          break;
				case "08":  month = "August";        break;
				case "09":  month = "September";     break;
				case "10": month = "October";       break;
				case "11": month = "November";      break;
				case "12": month = "December";      break;
				default: month = "Invalid month"; break;
			}
			data = month+data.substring(2);

			String[] tupla = {artista,luogo,data};
			output.add(tupla);
		}
		return output;
	}
}
