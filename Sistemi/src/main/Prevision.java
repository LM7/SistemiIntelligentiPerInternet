package main;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

import boilerpipe.Boilerpipe;
import eu.danieldk.nlp.jitar.cli.CrossValidation;
import eu.danieldk.nlp.jitar.cli.Tag;
import eu.danieldk.nlp.jitar.cli.Train;

public class Prevision {

	public final static HashSet<String> STOP_SITE = new HashSet<String>(Arrays.asList("on StubHub!", 
			"- StubHub UK","- StubHub UK!","– Last.fm", "– Last.fm", "at Last.fm", "@ TicketHold","@ Ultimate-Guitar.Com",
			"at Last.fm","Stereoboard", "ConcertWith.Me", "NaviHotels.com", "Heyevent.com", "Friendfeed", "setlist.fm",
			"Getty Images", "TicketNetwork", "www.floramc.org", "rmalife.net", "Gumtree", "Seatwave.com",
			"– Songkick", "The sound of summer", "504ever.net", "| Concertful", "StubHub UK!", "YouPict", 
			"- 5gig.com","5gig.co.uk", "mxdwn.com", "Thrillcall", "Kililive.com", "| Bandsintown", "MASS EDMC", 
			"| Nerds Attack!", "Plannify", "BoxOffice Lazio", "| Ticketfly", "| CheapTickets.com",
			"| MASS EDMC", "| Kililive.com", "| setlist.fm", " - Stereoboard", "SoundCrashMusic", "| SoundCrashMusic",
			"TicketsInventory Mobile", "- backpage.com", "from Bandsintown", "| ConcertBank.com", "| clubZone", "- univision.com",
			"- Wikipedia, the free encyclopedia", "| Eventful","| SeatGeek","| Eventsfy","__ Last.fm"," Setlist ","__ Songkick"));
	
	public static String[] tagPrevision(URL url) throws Exception {
		String[] datiForTraining = new String[3];
		datiForTraining[0] = "brown";
		datiForTraining[1] = "dataBROWN/training.brown";
		datiForTraining[2] = "dataBROWN/corpus.model";
	
		CrossValidation.doCrossValidation(datiForTraining);
		//Evaluate.doEvaluation(datiForTraining);
		Train.doTraining(datiForTraining);
		
		Boilerpipe bolierpipe = new Boilerpipe();
		String[] site = bolierpipe.getText(url);
		String title = site[0];
		
		title = title.replaceAll("\\s+", " ");
		title = title.trim();
		
		System.out.println("Title PRIMA: "+title);
		for(String sito: STOP_SITE){
			title = title.replace(sito, "");
		}
		//trim toglie spazi iniziali e finali
		title.trim();
		
		CleanTitle ct = new CleanTitle(title);
		String domain = url.getHost().split("/")[0];
		domain = domain.replace("www.", "");
		System.out.println("DOMAIN: "+domain);
		title = ct.removeSiteName(title,domain);
		title = title.replaceAll("\\s+", " ");
		title = title.trim();
		System.out.println("Title DOPO: "+title+"\n");
		
		String tag = Tag.doTagging(datiForTraining[2], title);
		
		String[] parole = title.split(" ");
		String[] tags = tag.split(" ");
		
		System.out.println("("+parole.length +") "+ title);
		System.out.println("("+tags.length +") "+tag);
		
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
		PERSONA.trim();
		CITTA.trim();
		SEDE.trim();
		DATA.trim();
		String[] evento = new String[]{PERSONA,CITTA,SEDE,DATA};
		return evento;
	}
		 
	public static void main(String[] args) throws Exception {
		
		URL url = new URL("http://www.loadingdockslc.com/event/735297-title-fight-salt-lake-city/");
		String[] evento = tagPrevision(url);
		System.out.println("PERSONA: " + evento[0]);
		System.out.println("CITTA: " + evento[1]);
		System.out.println("SEDE: " + evento[2]);
		System.out.println("DATA: " + evento[3]);
	}

}
