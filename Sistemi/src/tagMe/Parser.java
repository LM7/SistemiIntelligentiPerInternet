package tagMe;

import java.util.List;

import com.google.gson.Gson;

public class Parser {
	String result;
	Reply reply;
	Gson gson; 
	
	public Parser(String result) {
		this.result=result;
		this.gson=new Gson();
		this.reply=new Reply();
	}

	public String getReplyTagMe() {
		return result;
	}

	public void setReplyTagMe(String result) {
		this.result = result;
	}
	
	public void processingReply() {
		reply = gson.fromJson(result,Reply.class);
		List<Annotation> annotations = reply.getAnnotations();
		System.out.println("Annotations:");
		for (Annotation a : annotations) {
			System.out.println("Spot: "+a.getSpot()+"\n(Wikipedia) Title: "+a.getTitle()+"\nCategories: "+a.getDbpediaCategories());
			System.out.println();
		}
	}
		
}
