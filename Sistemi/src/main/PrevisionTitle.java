package main;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

import boilerpipe.Boilerpipe;
import eu.danieldk.nlp.jitar.cli.CrossValidation;
import eu.danieldk.nlp.jitar.cli.Tag;
import eu.danieldk.nlp.jitar.cli.Train;

public class PrevisionTitle {
	
	public final static HashSet<String> STOP_SITE = new HashSet<String>(Arrays.asList("on StubHub!", 
			"- StubHub UK","- StubHub UK!","� Last.fm", "� Last.fm", "at Last.fm", "@ TicketHold","@ Ultimate-Guitar.Com",
			"at Last.fm","Stereoboard", "ConcertWith.Me", "NaviHotels.com", "Heyevent.com", "Friendfeed", "setlist.fm",
			"Getty Images", "TicketNetwork", "www.floramc.org", "rmalife.net", "Gumtree", "Seatwave.com",
			"� Songkick", "The sound of summer", "504ever.net", "| Concertful", "StubHub UK!", "YouPict", 
			"- 5gig.com","5gig.co.uk", "mxdwn.com", "Thrillcall", "Kililive.com", "| Bandsintown", "MASS EDMC", 
			"| Nerds Attack!", "Plannify", "BoxOffice Lazio", "| Ticketfly", "| CheapTickets.com",
			"| MASS EDMC", "| Kililive.com", "| setlist.fm", " - Stereoboard", "SoundCrashMusic", "| SoundCrashMusic",
			"TicketsInventory Mobile", "- backpage.com", "from Bandsintown", "| ConcertBank.com", "| clubZone", "- univision.com",
			"- Wikipedia, the free encyclopedia", "| Eventful","| SeatGeek","| Eventsfy","__ Last.fm"," Setlist ","__ Songkick"));

	public String[] tagPrevisionTitle(String title) throws Exception {
		String[] datiForTraining = new String[3];
		datiForTraining[0] = "brown";
		datiForTraining[1] = "dataBROWN/trainingALLPunt.brown";
		datiForTraining[2] = "dataBROWN/corpus.model";
	
		CrossValidation.doCrossValidation(datiForTraining);
		//Evaluate.doEvaluation(datiForTraining);
		Train.doTraining(datiForTraining);
		
		Boilerpipe bolierpipe = new Boilerpipe();
		
		title = separaPunteggiatura(title,new String[]{",",":",";","?","!","|","\"","(",")"});
		
		title = title.replaceAll("\\s+", " ");
		title = title.trim();
		
		//System.out.println("Title PRIMA: "+title);
		for(String sito: STOP_SITE){
			title = title.replace(sito, "");
		}
		//trim toglie spazi iniziali e finali
		title.trim();
		
		CleanTitle ct = new CleanTitle(title);
		//String domain = url.getHost().split("/")[0];
		//domain = domain.replace("www.", "");
		//System.out.println("DOMAIN: "+domain);
		//title = ct.removeSiteName(title,domain);
		title = title.replaceAll("\\s+", " ");
		title = title.trim();
		//System.out.println("Title DOPO: "+title+"\n");
		
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
		PERSONA = PERSONA.trim();
		CITTA = CITTA.trim();
		SEDE = SEDE.trim();
		DATA = DATA.trim();
		String[] evento = new String[]{PERSONA,CITTA,SEDE,DATA};
		return evento;
	}
	
	private static String separaPunteggiatura(String text, String[] punteggiatura) {
		int i=0;
		int j=0;
		char[] caratteri = text.toCharArray();
		while(j<caratteri.length) {
			boolean modificato = false;
			caratteri = text.toCharArray();
			for(j=0;j<caratteri.length&&!modificato;j++) {
				for(i=0;i<punteggiatura.length;i++) {
					if(caratteri[j] == (punteggiatura[i].charAt(0))) {
						if(j!=caratteri.length-1 && caratteri[j+1]!=' ') {
							text = text.substring(0,j+1) + " " + text.substring(j+1);
							modificato = true;
						}
						if(j!=0 && caratteri[j-1]!=' ') {
							text = text.substring(0,j) + " " + text.substring(j);
							modificato = true;
						}
					}
				}
			}
		}
		return text;
	}


}
