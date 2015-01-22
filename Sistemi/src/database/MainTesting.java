package database;

import java.io.PrintWriter;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;

import suTime.SUTime;
import boilerpipe.Boilerpipe;

import com.mongodb.*;

public class MainTesting {
	public static void main(String[] args) {
		try {
			MongoClient mongo = new MongoClient("localhost", 27017);
			DB db = mongo.getDB("cose");
			DBCollection collection = db.getCollection("libriCollection");
			
			BasicDBObject x = new BasicDBObject();
			collection.remove(x);
			
			Boilerpipe b = new Boilerpipe();
			
			URL url = new URL("http://www.last.fm/event/3996308+Giraffage+at+Music+Hall+of+Williamsburg+on+31+January+2015");
			
			String title = b.getText(url)[0];
			String text = b.getText(url)[1];
		
			
			SUTime suT = new SUTime();			
			HashMap<Date, Integer> date = suT.getTime(title,text);
			
			HashMap<String, Integer> gigio = new HashMap<String, Integer>();
			
			for (Date d : date.keySet()){
				gigio.put(d.toString(), date.get(d));
			}

			
			// inserisco un libro
			BasicDBObject document = new BasicDBObject();
			
			System.out.println(date.size());
			System.out.println(gigio.size());
			document.put("date", gigio);
			document.put("data proposta", suT.dataEvento(date).toString());
			collection.insert(document);
			
			BasicDBObject document2 = new BasicDBObject();
			document2.append("data proposta", "ventinovequattronovantuno");
			document2.append("dita", 45);
			collection.insert(document2);
			
			
			
			
			
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("data proposta", suT.dataEvento(date).toString());
			
			DBCursor cursor = collection.find();
			while (cursor.hasNext()) {
				System.out.println(cursor.next());
			}
			
			System.out.println("Done");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}