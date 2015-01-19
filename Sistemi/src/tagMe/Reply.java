package tagMe;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("org.jsonschema2pojo")
public class Reply {

	@Expose
	private String timestamp;
	@Expose
	private Integer time;
	@Expose
	private String api;
	@Expose
	private List<Annotation> annotations = new ArrayList<Annotation>();
	@Expose
	private String lang;

	/**
	 * 
	 * @return
	 * The timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * 
	 * @param timestamp
	 * The timestamp
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * 
	 * @return
	 * The time
	 */
	public Integer getTime() {
		return time;
	}

	/**
	 * 
	 * @param time
	 * The time
	 */
	public void setTime(Integer time) {
		this.time = time;
	}

	/**
	 * 
	 * @return
	 * The api
	 */
	public String getApi() {
		return api;
	}

	/**
	 * 
	 * @param api
	 * The api
	 */
	public void setApi(String api) {
		this.api = api;
	}

	/**
	 * 
	 * @return
	 * The annotations
	 */
	public List<Annotation> getAnnotations() {
		return annotations;
	}

	/**
	 * 
	 * @param annotations
	 * The annotations
	 */
	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}

	/**
	 * 
	 * @return
	 * The lang
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * 
	 * @param lang
	 * The lang
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(timestamp).append(time).append(api).append(annotations).append(lang).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Reply) == false) {
			return false;
		}
		Reply rhs = ((Reply) other);
		return new EqualsBuilder().append(timestamp, rhs.timestamp).append(time, rhs.time).append(api, rhs.api).append(annotations, rhs.annotations).append(lang, rhs.lang).isEquals();
	}

}
