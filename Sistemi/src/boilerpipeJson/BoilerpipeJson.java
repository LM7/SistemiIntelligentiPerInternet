package boilerpipeJson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class BoilerpipeJson {

	public static String getContent(URL url) throws Exception {
		//String urlString = "http://consequenceofsound.net/2015/02/bunbury-festival-announces-2015-lineup/";

		String urlString = url.toString();
		URL urlBoilerpipe = new URL("http://boilerpipe-web.appspot.com/extract?url="+urlString+"&extractor=ArticleExtractor&output=json");
		HttpURLConnection connection = (HttpURLConnection) urlBoilerpipe.openConnection();
		BufferedReader read = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		JSONObject json = new JSONObject(read.readLine());
		JSONObject response = json.getJSONObject("response");
		return (response.getString("content"));
	}
	
	public static String getTitle(URL url) throws Exception {
		//String urlString = "http://consequenceofsound.net/2015/02/bunbury-festival-announces-2015-lineup/";

		String urlString = url.toString();
		URL urlBoilerpipe = new URL("http://boilerpipe-web.appspot.com/extract?url="+urlString+"&extractor=ArticleExtractor&output=json");
		HttpURLConnection connection = (HttpURLConnection) urlBoilerpipe.openConnection();
		BufferedReader read = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		JSONObject json = new JSONObject(read.readLine());
		JSONObject response = json.getJSONObject("response");
		return (response.getString("title"));
	}
	
	public static void main(String[] args) throws Exception {
		URL url = new URL("http://consequenceofsound.net/2015/02/bunbury-festival-announces-2015-lineup/");
		String title = getTitle(url);
		String s = getContent(url);
		System.out.println(title);
		System.out.println("----------");
		System.out.println(s);
	}

}
