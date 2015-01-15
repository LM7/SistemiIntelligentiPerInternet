package database;
import java.net.UnknownHostException;
import com.mongodb.*;

public class MainTesting {
	public static void main(String[] args) {
		try {
			MongoClient mongo = new MongoClient("localhost", 27017);
			DB db = mongo.getDB("cose");
			DBCollection collection = db.getCollection("libriCollection");

			// inserisco un libro
			BasicDBObject document = new BasicDBObject();
			
			document.put("isbn", 1935182870);
			document.put("titolo", "MongoDB in Action");
			document.put("autore", "Kyle Banker");
			
			document.put("gigio", "nomeGigio");
			collection.insert(document);

			// ora lo vado a ricercare
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("isbn", 1935182870);
			DBCursor cursor = collection.find(searchQuery);
			while (cursor.hasNext()) {
				System.out.println(cursor.next());
			}
			System.out.println("Done");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}
}