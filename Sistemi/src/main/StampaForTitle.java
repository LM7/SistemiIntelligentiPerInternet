package main;

import java.io.PrintWriter;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class StampaForTitle {

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
			s = s.substring(0,1) + s.substring(51);
			s = s.replaceAll("\"", "");
			s = s.replaceAll("[}]", "");
			s = s.replaceAll("[{]", "");
			
			System.out.println(j+") "+s);
			s = s.replaceAll("data :", "data§");
			s = s.replaceAll(", evento_cantante :", "§cantante§");
			s = s.replaceAll(", luogo :", "§luogo§");
			s = s.replaceAll(", Titolo :", "§Titolo§");
			s = s.replaceAll(", TitoloTag :", "§TitoloTag§");
			s = s.replaceAll(", dominio :", "§dominio§");
			s = s.replaceAll(", url :", "§url§");
			
			out.println(s);
			j++;
		}

		out.close();
		System.out.println("Done");
	}
}
