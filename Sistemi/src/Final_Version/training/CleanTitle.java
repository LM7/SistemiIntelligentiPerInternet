package Final_Version.training;

import java.util.Arrays;
import java.util.HashSet;

public class CleanTitle {
	String title;
	public final static HashSet<String> STOP_SITE = new HashSet<String>(Arrays.asList("on StubHub!", 
			"- StubHub UK","- StubHub UK!","??? Last.fm", "at Last.fm", "@ TicketHold","@ Ultimate-Guitar.Com",
			"Stereoboard", "ConcertWith.Me", "NaviHotels.com", "Heyevent.com", "Friendfeed", "setlist.fm",
			"Getty Images", "TicketNetwork", "www.floramc.org", "rmalife.net", "Gumtree", "Seatwave.com",
			"??? Songkick", "The sound of summer", "504ever.net", "| Concertful", "StubHub UK!", "YouPict", 
			"- 5gig.com","5gig.co.uk", "mxdwn.com", "Thrillcall", "Kililive.com", "| Bandsintown", "MASS EDMC", 
			"| Nerds Attack!", "Plannify", "BoxOffice Lazio", "| Ticketfly", "| CheapTickets.com",
			"| MASS EDMC", "| Kililive.com", "| setlist.fm", " - Stereoboard", "SoundCrashMusic", "| SoundCrashMusic",
			"TicketsInventory Mobile", "- backpage.com", "from Bandsintown", "| ConcertBank.com", "| clubZone", "- univision.com",
			"- Wikipedia, the free encyclopedia", "| Eventful","| SeatGeek","| Eventsfy","__ Last.fm"," Setlist ","__ Songkick"));
	
	public CleanTitle(String title) {
		super();
		this.title = title;
	}
	
	/*
	 * Metodo di supporto per la rimozione del nome del sito all'interno del titolo
	 */
	public String removeSiteName(String title, String domain) {

		int indexOfLastPoint = domain.lastIndexOf('.');
		domain = domain.substring(0, indexOfLastPoint);
		boolean check = false;
		String[] titleSplit = title.split(" ");
		String temp;
		String tokenSucc = "",tokenPrec="";
		int j=0;
		for (int i=0; i<titleSplit.length;i++) {
			
			temp = titleSplit[i].toLowerCase();
			//System.out.println("=== "+temp+" ===");

			j=i+1;
			if(j<titleSplit.length) {
				tokenSucc = titleSplit[j];
				//System.out.println("tokenSucc: "+tokenSucc);
			}
			else 
				tokenSucc="";

			if(temp.contains(".www") || temp.contains(".com") || temp.contains(".fm") || temp.contains(".org") || temp.contains(".net") || temp.equals(domain)) {
				//System.out.println("token contiene .qualcosa");
				title = title.replaceFirst(titleSplit[i], "");
				String tempTokenPrec = tokenPrec.toLowerCase();
				if(tempTokenPrec.equals("|") || tempTokenPrec.equals(",") || tempTokenPrec.equals("???") || tempTokenPrec.equals("-") || tempTokenPrec.equals("/") ||	tempTokenPrec.equals(":") 
				|| tempTokenPrec.equals("at") ||tempTokenPrec.equals("in") ||tempTokenPrec.equals("from") ||tempTokenPrec.equals("with")) {
					title = replaceLastOccurrence(title, tokenPrec, " ");
				}
			}

			else if(domain.contains(temp)) {
				//System.out.println("A: "+tokenSucc);
				//System.out.println("B: "+tokenPrec);
				if(domain.contains(tokenSucc.toLowerCase()) && !tokenSucc.equals("")) {
					title = doThis(domain, tokenSucc, tokenPrec, titleSplit[i], i);
					check=true;
				}
				else if(domain.contains(tokenPrec.toLowerCase())) {
					title = replaceLastOccurrence(title,titleSplit[i], "");
					//System.out.println(tokenPrec.toLowerCase());
				}
			}
			tokenPrec = titleSplit[i];
			if(check) {
				i = i+1;
				check=false;
			}
			//System.out.println(i);
		}


		return title;
	}

	public String doThis(String domain, String tokenSucc, String tokenPrec, String token, int i) {
		//System.out.println("qua con: "+tokenSucc+i);
		title = title.replace(token+" "+tokenSucc, "");
		String tempTokenPrec = tokenPrec.toLowerCase();
		if(tempTokenPrec.equals("|") || tempTokenPrec.equals(",") || tempTokenPrec.equals("???") || tempTokenPrec.equals("-") || tempTokenPrec.equals("/") ||	tempTokenPrec.equals(":") 
		|| tempTokenPrec.equals("at") ||tempTokenPrec.equals("in") ||tempTokenPrec.equals("from") ||tempTokenPrec.equals("with")) {
			title = replaceLastOccurrence(title, tokenPrec, "");
		}
		return title;
	}
	/*
	 * metodo di supporto per la rimozione dell'ultima occorrenza del contenuto da una stringa
	 */
	public static String replaceLastOccurrence(String string, String toReplace, String replacement) {
		int pos = string.lastIndexOf(toReplace);
		if (pos > -1) {
			return string.substring(0, pos)
					+ replacement
					+ string.substring(pos + toReplace.length(), string.length());
		} else {
			return string;
		}
	}
	
	public String replaceLastToken(String title) {
		String[] titleSplit = title.split(" ");
		int titleLength = titleSplit.length;
		String lastToken = titleSplit[titleLength-1];
		if(lastToken.equals("|") || lastToken.equals(",") || lastToken.equals("???") || lastToken.equals("-") || lastToken.equals("/") ||	lastToken.equals(":") 
				|| lastToken.equals("at") ||lastToken.equals("in") ||lastToken.equals("from") ||lastToken.equals("with")) {
		title = replaceLastOccurrence(title, lastToken, "");
		}
		return title;
	}
}
