package main;

import java.io.PrintWriter;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class StampaTrainingSet {

	public static void main(String[] args) throws Exception {
		MongoClient mongo = null;
		try {
			mongo = new MongoClient("localhost", 27017);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		DB db = mongo.getDB("database");
		DBCollection collection = db.getCollection("training");	

		PrintWriter out = new PrintWriter("TrainingTest.txt", "UTF-8");

		DBCursor cursor = collection.find();
		int j = 1;
		while (cursor.hasNext()) {
			String s = cursor.next().toString();
			System.out.println(j+") "+s);
			s = s.substring(0,1) + s.substring(51);
			s = s.replaceAll("\"", "");
			s = s.replaceAll("[}]", "");
			s = s.replaceAll("[{]", "");
			out.println(s);
			j++;
		}

		out.close();
		System.out.println("Done");
	}
}
