package lastFM;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.umass.lastfm.Event;
import de.umass.lastfm.Geo;
import de.umass.lastfm.PaginatedResult;

public class geoMethods {
	// key per accedere alle API
	public final static String key = "fdc2251ce7ea658e631af3c7709d4d84";
	public final static int NUMEROEVENTI = 10;

	public static ArrayList<String[]> eventsPusher(String citta) {

		// Preparo l'output
		ArrayList<String[]> output = new ArrayList<String[]>();

		// 'topOfTheTops' e' una pagina con i 10 eventi piu' recenti nella citta'
		PaginatedResult<Event> topOfTheTops = Geo.getEvents(citta, "", 1, NUMEROEVENTI, key);

		// Per ogni evento di 'topOfTheTops' popolo 'totale'
		for (Event evento : topOfTheTops) {
			String artista = evento.getHeadliner();
			String luogo = evento.getVenue().getName()+", "+evento.getVenue().getCity();

			Locale.setDefault(Locale.ENGLISH);
			SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMMMMMMMM dd yyyy");
			String data = DATE_FORMAT.format(evento.getStartDate());

			String[] tupla = {artista,luogo,data};
			output.add(tupla);
		}
		return output;
	}

}
