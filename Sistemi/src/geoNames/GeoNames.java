package geoNames;

import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

public class GeoNames {

	public static void main(String[] args) throws Exception {
		
		System.out.println(isCity("roma"));
		System.out.println(isCity("Rihanna"));
	}
	
	
	/*
	 * Restituisce 1 se riconosce la citta', -1 se non la riconosce, 
	 * 0 se finisce il numero di richieste che puo' fare
	 */
	public static int isCity(String citta) {
		try {
			WebService.setUserName("siicdll");
			
			ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
			searchCriteria.setQ(citta);
			ToponymSearchResult searchResult = WebService.search(searchCriteria);
			if(searchResult.getTotalResultsCount() > 0)
				return 1;
			else 
				return -1;
		} catch (Exception e) {
			return 0;
		}
		
	}
}
