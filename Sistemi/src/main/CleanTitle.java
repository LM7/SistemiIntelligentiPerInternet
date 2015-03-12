package main;

public class CleanTitle {
	String title;

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
				if(tempTokenPrec.equals("|") || tempTokenPrec.equals(",") || tempTokenPrec.equals("–") || tempTokenPrec.equals("-") || tempTokenPrec.equals("/") ||	tempTokenPrec.equals(":") 
				|| tempTokenPrec.equals("at") ||tempTokenPrec.equals("in") ||tempTokenPrec.equals("from") ||tempTokenPrec.equals("with")) {
					title = replaceLast(title, tokenPrec, " ");
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
					title = replaceLast(title,titleSplit[i], "");
					//System.out.println(tokenPrec.toLowerCase());
				}
				/***
				//System.out.println(temp+ " è contenuto nel dominio");
				if(check==true) {
					i=i+1;
					System.out.println(titleSplit[i]);
					title = replaceLast(title,titleSplit[i], "");
					//System.out.println("nel check");
					check=false;
				}
				if(domain.contains(tokenSucc.toLowerCase())) {
					if(!tokenSucc.equals("")) {
						System.out.println("qua con: "+tokenSucc+i);
						title = title.replace(titleSplit[i]+" "+tokenSucc, "");
						i=i+1;
						check = true;
						if(tokenPrec.equals("|") || tokenPrec.equals(",") || tokenPrec.equals("–") || tokenPrec.equals("-") ||
								tokenPrec.equals(":") || tokenPrec.equals("at") ||tokenPrec.equals("in") ||tokenPrec.equals("from")) {
							title = replaceLast(title, tokenPrec, "");
						}
					}
				}
				 ***/
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
		if(tempTokenPrec.equals("|") || tempTokenPrec.equals(",") || tempTokenPrec.equals("–") || tempTokenPrec.equals("-") || tempTokenPrec.equals("/") ||	tempTokenPrec.equals(":") 
		|| tempTokenPrec.equals("at") ||tempTokenPrec.equals("in") ||tempTokenPrec.equals("from") ||tempTokenPrec.equals("with")) {
			title = replaceLast(title, tokenPrec, "");
		}
		return title;
	}
	/*
	 * metodo di supporto per la rimozione dell'ultima occorrenza del contenuto da una stringa
	 */
	public static String replaceLast(String string, String toReplace, String replacement) {
		int pos = string.lastIndexOf(toReplace);
		if (pos > -1) {
			return string.substring(0, pos)
					+ replacement
					+ string.substring(pos + toReplace.length(), string.length());
		} else {
			return string;
		}
	}
}
