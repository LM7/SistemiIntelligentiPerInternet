package main;

import java.io.PrintWriter;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class StampaQuery2 {
	
	public static void main(String[] args) throws Exception {
		MongoClient mongo = null;
		try {
			mongo = new MongoClient("localhost", 27017);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		DB db = mongo.getDB("db2");
		DBCollection collection = db.getCollection("collezione2");
		
		PrintWriter out = new PrintWriter("AAA_dati_buoni.txt", "UTF-8");
		
		DBCursor cursor = collection.find();
		int j = 1;
		while (cursor.hasNext()) {
			String s = cursor.next().toString();
			System.out.println(j+") "+s);
			s = s.substring(0,1) + s.substring(51);
			s = s.replaceAll("\"url\"", "URL");
			s = s.replaceAll("\"data\"", "DATA");
			s = s.replaceAll("\"evento_cantante\"", "CANTANTE");
			s = s.replaceAll("\"luogo\"", "LUOGO GIUSTO");
			s = s.replaceAll("\"luoghi\"", "LUOGHI");
			s = s.replaceAll("\"xk male\"", "xk male");
			s = s.replaceAll("\"buono\"", "buono");
			out.println(s);
			j++;
		}

		out.close();
		System.out.println("Done");
	}

}
