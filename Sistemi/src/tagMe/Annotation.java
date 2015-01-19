package tagMe;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Annotation {

	@Expose
	private Integer id;
	@Expose
	private String title;
	@SerializedName("dbpedia_categories")
	@Expose
	private List<String> dbpediaCategories = new ArrayList<String>();
	@Expose
	private Integer start;
	@Expose
	private String rho;
	@Expose
	private Integer end;
	@Expose
	private String spot;

	/**
	 * 
	 * @return
	 * The id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 * The id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 * The title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 
	 * @param title
	 * The title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 
	 * @return
	 * The dbpediaCategories
	 */
	public List<String> getDbpediaCategories() {
		return dbpediaCategories;
	}

	/**
	 * 
	 * @param dbpediaCategories
	 * The dbpedia_categories
	 */
	public void setDbpediaCategories(List<String> dbpediaCategories) {
		this.dbpediaCategories = dbpediaCategories;
	}

	/**
	 * 
	 * @return
	 * The start
	 */
	public Integer getStart() {
		return start;
	}

	/**
	 * 
	 * @param start
	 * The start
	 */
	public void setStart(Integer start) {
		this.start = start;
	}

	/**
	 * 
	 * @return
	 * The rho
	 */
	public String getRho() {
		return rho;
	}

	/**
	 * 
	 * @param rho
	 * The rho
	 */
	public void setRho(String rho) {
		this.rho = rho;
	}

	/**
	 * 
	 * @return
	 * The end
	 */
	public Integer getEnd() {
		return end;
	}

	/**
	 * 
	 * @param end
	 * The end
	 */
	public void setEnd(Integer end) {
		this.end = end;
	}

	/**
	 * 
	 * @return
	 * The spot
	 */
	public String getSpot() {
		return spot;
	}

	/**
	 * 
	 * @param spot
	 * The spot
	 */
	public void setSpot(String spot) {
		this.spot = spot;
	}

}