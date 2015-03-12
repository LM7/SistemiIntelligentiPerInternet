package main;

public class mainRemoveSiteName {

	public static void main(String[] args) {
		String title = "PFX - The Pink Floyd Experience Tickets, April 01, 2015 in Chico - Chico tickets for sale - newsreview.com";
		String domain = "chico.newsreview.com";
		title = title.replaceAll("\\s+", " ");
		title = title.trim();
		System.out.println("DOMAIN: "+domain);
		System.out.println("Title PRIMA: "+title);
		CleanTitle ct = new CleanTitle(title);
		title = ct.removeSiteName(title,domain);
		title = title.replaceAll("\\s+", " ");
		title = title.trim();
		System.out.println("Title DOPO: "+title+"\n");
		
		
	}
}
