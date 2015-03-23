package Final_Version.events;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;


public class TermFreq implements Comparable, Serializable {
	String term;
	int freq;

	public TermFreq(String t, int f) {
		term = t;
		freq = f;
	}
	public int getFreq() {
		return freq;
	}

	public String getTerm() {
		return term;
	}
	
	/**
	 * Returns the frequency of one term given a TermFreq array.
	 * 
	 * Note: the array must be lexically ordered. 
	 */
	public static int getFreqByArray(TermFreq[] tfs, String term) {
		TermFreq tf = new TermFreq(term, 0);
		int idx = Arrays.binarySearch(tfs, tf);
		return idx >= 0 ? tfs[idx].freq : 0; 
	}

	/**
	 * Returns the index of the given term in a TremFreq array, < 0 if not found.
	 * 
	 * Note: the array must be lexically ordered.
	 */
	public static int getTermIdx(TermFreq[] tfs, String term) {
		TermFreq tf = new TermFreq(term, 0);
		int idx = Arrays.binarySearch(tfs, tf);
		return idx; 
	}

	public int compareTo(Object o) {
		if (this == o) 
			return 0;
		TermFreq tf = (TermFreq) o;
		int c = term.compareTo(tf.term);
		return c;
	}
	
	public String toString() {
		return "('" + term + "':" + freq + ")"; 
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(term);
		out.write(freq);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		term = (String)in.readObject();
		freq = in.readInt();
	}
	
	
}