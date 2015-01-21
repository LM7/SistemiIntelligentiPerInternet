package main;

import java.io.PrintWriter;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import events.MsnSearchEngine;
import suTime.SUTime;
import boilerpipe.Boilerpipe;

public class Principal {
	
	public static void main(String[] args) throws Exception {
		MsnSearchEngine se = new MsnSearchEngine();
		String[] urls = se.getUrls("Roma", 5);
		
		//Queste 2 righe verranno tolte
		for(String s: urls)
			System.out.println(s);
		
		
		Boilerpipe b = new Boilerpipe();
		
		/* questo verra' scommentato
		for(String s: urls) {
			URL url = new URL(s);
		*/
		
		//Queste 5 righe verranno tolte
		URL url = new URL("http://www.last.fm/event/3996308+Giraffage+at+Music+Hall+of+Williamsburg+on+31+January+2015"
				//"http://www.aloud.com/towns/london/camden%20underworld.xml"
				//"http://www.aloud.com/tickets/within-the-ruins"
				//"http://lambgoat.com/news/23481/Within-The-Ruins-I-Declare-War-tour-Europe"				
				);
		
		String title = b.getText(url)[0];
		String text = b.getText(url)[1];
		
		//Queste 4 righe verranno tolte
		PrintWriter out = new PrintWriter("contenutoTesto.html", "UTF-8");
		out.println("<meta http-equiv=\"Content-Type\" content=\"html; charset=utf-8\" />");
		out.println(text);
		out.close();
		
		SUTime suT = new SUTime();
		
		//Questa riga verra' tolta
		suT.getTimeProva(text);
		
		HashMap<Date, Integer> date = suT.getTime(title,text);
		
		//Queste 8 righe verranno tolte
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
