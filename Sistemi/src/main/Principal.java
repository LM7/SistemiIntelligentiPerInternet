package main;

import java.io.PrintWriter;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import suTime.SUTime;
import boilerpipe.Boilerpipe;

public class Principal {
	
	public static void main(String[] args) throws Exception {
		Boilerpipe b = new Boilerpipe();
		URL url = new URL("http://www.last.fm/event/3987451+Mini+Mansions+at+The+Lexington+on+19+January+2015"
				//"http://www.aloud.com/towns/london/camden%20underworld.xml"
				//"http://www.aloud.com/tickets/within-the-ruins"
				//"http://lambgoat.com/news/23481/Within-The-Ruins-I-Declare-War-tour-Europe"				
				);
		String text = b.getText(url);
		
		PrintWriter out = new PrintWriter("contenutoTesto.html", "UTF-8");
		out.println("<meta http-equiv=\"Content-Type\" content=\"html; charset=utf-8\" />");
		out.println(text);
		out.close();
		
		SUTime suT = new SUTime();
		
		suT.getTimeProva(text);
		
		HashMap<Date, Integer> date = suT.getTime(text);
		int i = 1;
		int numDate = 0;
		for(Date d: date.keySet()){
			System.out.println(i+")"+d+" :"+date.get(d));
			i++;
			numDate += date.get(d);
		}
		System.out.println("DATE TOT= "+numDate);
		System.out.println("data evento proposto="+suT.dataEvento(date));
	}
}
