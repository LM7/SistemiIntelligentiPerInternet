package main;

import java.io.PrintWriter;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class StampaQuery {

	public static void main(String[] args) throws Exception {
		MongoClient mongo = null;
		try {
			mongo = new MongoClient("localhost", 27017);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		DB db = mongo.getDB("db");
		DBCollection collection = db.getCollection("collezione");
		
		PrintWriter out = new PrintWriter("contenutoDB.txt", "UTF-8");
		
		DBCursor cursor = collection.find();
		int j = 1;
		while (cursor.hasNext()) {
			String s = cursor.next().toString();
			System.out.println(j+") "+s);
			s = j+") "+s.substring(0,1) + s.substring(51);
			s = s.replaceAll("\"data\"", "DATA");
			s = s.replaceAll("\"data proposta\"", "DATA PROPOSTA");
			s = s.replaceAll("\"evento_cantante\"", "\tPERSONA");
			s = s.replaceAll("\"persona proposta\"", "PERSONA PROPOSTA");
			s = s.replaceAll("\"luogo\"", "\tLUOGO");
			s = s.replaceAll("\"luogo proposto\"", "LUOGO PROPOSTO");
			s = s.replaceAll("\"url\"", "\t\tURL");
			out.println(s);
			j++;
		}

		out.close();
		System.out.println("Done");
	}
}
