
package events;

import java.util.*;
import java.io.*;



/**
 * Provides access to a term-weight vector.
 * 
 * Internally, the term vector is alphabetically ordered.
 * 
 * @author Fabio Gasparetti
 * @version 1.0, 01/01/05
 */
public class TermWeightVector implements Serializable, Cloneable, Comparable {
	protected String terms[] = null;

	protected double weights[] = null;

	public TermWeightVector() {
		terms = new String[0];
		weights = new double[0];
	}

	/**
	 * Returns the weight of a given term, 0 if the term is not present.
	 */
	public double weight(String term) {
		int i = Arrays.binarySearch(terms, term);
		return i >= 0 ? weights[i] : 0;
	}

	/**
	 * Returns an array of the terms in the vector. The term is alphabetically
	 * ordered.
	 */
	public String[] terms() {
		return terms;
	}

	/**
	 * Returns a new vector composed of nelem terms with highest weights.
	 */
	public TermWeightVector truncate(int nelem) {
		if (terms.length <= nelem) {
			try {
				return (TermWeightVector) this.clone();
			} catch (Exception ex) {
			}
		}
		// sort according to weights
		scoreSort();
		TermWeightVector vect = new TermWeightVector();
		vect.terms = new String[nelem];
		vect.weights = new double[nelem];
		System.arraycopy(terms, 0, vect.terms, 0, nelem);
		System.arraycopy(weights, 0, vect.weights, 0, nelem);
		// bring back the original sort
		termSort();
		vect.termSort();
		return vect;
	}

	/**
	 * Set all terms' weights to w.
	 * 
	 * @param w
	 */
	public void setTermWeights(double w) {
		for (int i = 0; i < weights.length; i++) {
			weights[i] = w;

		}
	}

	/**
	 * Returns a new vector composed of terms with weights > threshold.
	 */
	public TermWeightVector truncate(double threshold) {
		// sort according to weights
		scoreSort();
		int i;
		for (i = 0; i < weights.length; i++)
			if (weights[i] <= threshold)
				break;

		TermWeightVector vect = new TermWeightVector();
		vect.terms = new String[i];
		vect.weights = new double[i];
		System.arraycopy(terms, 0, vect.terms, 0, i);
		System.arraycopy(weights, 0, vect.weights, 0, i);
		// bring back the original sort
		termSort();
		vect.termSort();
		return vect;
	}

	protected void termSort() {
		// bubble sort
		boolean doMore = true;
		while (doMore) {
			doMore = false;
			for (int i = 0; i < weights.length - 1; i++) {
				if (terms[i].compareTo(terms[i + 1]) > 0) {
					// exchange elements
					double temp = weights[i];
					weights[i] = weights[i + 1];
					weights[i + 1] = temp;
					String s = terms[i];
					terms[i] = terms[i + 1];
					terms[i + 1] = s;
					doMore = true; // after an exchange, must look again
				}
			}
		}
	}

	protected void scoreSort() {
		// bubble sort
		boolean doMore = true;
		while (doMore) {
			doMore = false;
			for (int i = 0; i < weights.length - 1; i++) {
				if (weights[i] < weights[i + 1]) {
					// exchange elements
					double temp = weights[i];
					weights[i] = weights[i + 1];
					weights[i + 1] = temp;
					String s = terms[i];
					terms[i] = terms[i + 1];
					terms[i + 1] = s;
					doMore = true; // after an exchange, must look again
				}
			}
		}
	}

	/**
	 * Returns an array of the term wieghts in the vector.
	 */
	public double[] weights() {
		return weights;
	}

	/**
	 * weight <- frequency
	 */
	static public TermWeightVector build(String tokens, Analyzer analyzer)
			throws IOException {
		TermWeightVector vect = new TermWeightVector();
		if (tokens != null) {
			String[] ts = analyzer.tokenArray(new StringReader(tokens));
			vect = TermWeightVector.build(ts);
		}
		return vect;
	}

	/**
	 * weight <- defaultWeight
	 */
	static public TermWeightVector build(String tokens, Analyzer analyzer,
			double defaultWeight) throws IOException {
		TermWeightVector vect = new TermWeightVector();
		if (tokens != null) {
			String[] ts = analyzer.tokenArray(new StringReader(tokens));
			vect = TermWeightVector.build(ts, defaultWeight);
		}
		return vect;
	}

	/**
	 * weight <- defaultWeight
	 */
	public Object clone() {
		TermWeightVector vect = new TermWeightVector();
		vect.terms = new String[terms.length];
		System.arraycopy(terms, 0, vect.terms, 0, terms.length);
		vect.weights = new double[weights.length];
		System.arraycopy(weights, 0, vect.weights, 0, weights.length);
		return vect;
	}

	/**
	 * weight <- defaultWeight
	 */
	static public TermWeightVector build(String[] tokens, double defaultWeight) {
		TermWeightVector vect = new TermWeightVector();
		vect.terms = new String[tokens.length];
		System.arraycopy(tokens, 0, vect.terms, 0, tokens.length);
		vect.weights = new double[tokens.length];
		Arrays.fill(vect.weights, defaultWeight);
		vect.termSort();
		return vect;
	}

	/**
	 * 
	 * weight <- defaultWeight
	 * 
	 * Note: lengths of the two vectors must be the same; if tokens has
	 * duplicate words, the weight will be the sum of all the words.
	 */
	static public TermWeightVector build(String[] tokens, double[] weights) {
		if (tokens.length != weights.length)
			return null;
		TreeMap v = new TreeMap();
		for (int i = 0; i < tokens.length; i++) {
			Double d = (Double) v.get(tokens[i]);
			if (d == null)
				d = new Double(weights[i]);
			else
				d = new Double(d.doubleValue() + weights[i]);
			v.put(tokens[i], d);
		}
		TermWeightVector vect = new TermWeightVector();
		vect.internalBuild(v);
		return vect;
	}

	/**
	 * Returns a new TermWeightVector instance given an array of tokens. Weights
	 * corresponds to word frequencies.
	 */
	static public TermWeightVector build(String[] tokens) {
		TreeMap termWeights = new TreeMap();
		for (int i = 0; i < tokens.length; i++) {
			Double n = (Double) termWeights.get(tokens[i]);
			if (n != null) {
				termWeights.put(tokens[i], new Double(n.doubleValue() + 1d));
			} else {
				termWeights.put(tokens[i], new Double(1));
			}
		}
		TermWeightVector vect = new TermWeightVector();
		vect.internalBuild(termWeights);
		return vect;
	}

	/**
	 * Returns a new TermWeightVector instance given an array of tokens. The
	 * token frequency in the array corresponds to the token weight.
	 */
	static public TermWeightVector build(TreeMap termWeights) {
		TermWeightVector vect = new TermWeightVector();
		vect.internalBuild(termWeights);
		return vect;
	}

	/*
	 * Returns a new TermWeightVector instance given a term-weight map.
	 */
	protected void internalBuild(TreeMap termWeights) {
		terms = new String[termWeights.size()];
		weights = new double[termWeights.size()];
		System.arraycopy(termWeights.keySet().toArray(new String[] {}), 0,
				terms, 0, terms.length);
		int i = 0;
		Iterator iter = termWeights.values().iterator();
		while (iter.hasNext()) {
			double d = ((Double) iter.next()).doubleValue();
			weights[i++] = d;
		}
	}

	/**
	 * Merges the given TermWeightVector object with the called object.
	 * 
	 * The resulting array is ordered alphabetically. If a term compares in both
	 * arrays, the highest weight is kept. Returns the updated TermWeights
	 * object.
	 */
	public void merge(TermWeightVector w) {
		TreeMap m = new TreeMap();
		for (int i = 0; i < terms.length; i++) {
			m.put(terms[i], new Double(weights[i]));
		}
		for (int i = 0; i < w.terms.length; i++) {
			Double weight = (Double) m.get(w.terms[i]);
			if ((weight == null)
					|| ((weight != null) && (w.weights[i] > weight
							.doubleValue()))) {
				m.put(w.terms[i], new Double(w.weights[i]));
			}
		}
		internalBuild(m);
	}

	public double norm() {
		double tot = 0d;
		for (int i = 0; i < weights.length; i++) {
			tot += weights[i] * weights[i];
		}
		return Math.sqrt(tot);
	}

	public void normalize() {
		double norm = norm();
		if (norm == 0d)
			return;
		for (int i = 0; i < weights.length; i++) {
			weights[i] /= norm;
		}
	}

	public void timesScalar(double d) {
		for (int i = 0; i < weights.length; i++) {
			weights[i] *= d;
		}
	}

	public int size() {
		return terms.length;
	}

	/**
	 * The size of the string if the terms are concatenated with no spaces.
	 * Note: the calculation is online so it is time-consuming.
	 */
	public int numberCharacters() {
		int count = 0;
		for (int i = 0; i < terms.length; i++) {
			count += terms[i].length();
		}
		return count;
	}

	/**
	 * Adds the given TermWeightVector object with the called object.
	 * 
	 * The resulting array is ordered alphabetically. Returns the updated
	 * TermWeights object. 2 weights of the same term will be summed.
	 */
	public void add(TermWeightVector w) {
		TreeMap m = new TreeMap();
		for (int i = 0; i < terms.length; i++) {
			m.put(terms[i], new Double(weights[i]));
		}
		for (int i = 0; i < w.terms.length; i++) {
			Double weight = (Double) m.get(w.terms[i]);
			if (weight != null) {
				m.put(w.terms[i], new Double(weight.doubleValue()
						+ w.weights[i]));
			} else {
				m.put(w.terms[i], new Double(w.weights[i]));
			}
		}
		internalBuild(m);
	}

	/**
	 * Draws the cosine measure between two vectors.
	 */
	public double cosineRule(TermWeightVector v) {
		double d = 0d;
		double den1 = 0d;
		double den2 = 0d;

		int i = 0, j = 0;
		while ((i < v.terms.length) && (j < terms.length)) {
			int cmp = v.terms[i].compareTo(terms[j]);
			if (cmp > 0) {
				j++;
			} else if (cmp < 0) {
				i++;
			} else {
				d += weights[j] * v.weights[i];
				i++;
				j++;
			}
		}
		for (i = 0; i < terms.length; i++)
			den1 += weights[i] * weights[i];
		for (i = 0; i < v.terms.length; i++)
			den2 += v.weights[i] * v.weights[i];

		if ((den1 > 0d) && (den2 > 0d))
			return d / (Math.sqrt(den1) * Math.sqrt(den2));
		return 0d;
	}

	/**
	 * Draws the jaccard measure between two vectors.
	 */
	public double jaccard(TermWeightVector v) {
		double n = 0;
		int i = 0, j = 0;
		while ((i < v.terms.length) && (j < terms.length)) {
			int cmp = v.terms[i].compareTo(terms[j]);
			if (cmp > 0) {
				j++;
			} else if (cmp < 0) {
				i++;
			} else {
				n++;
				i++;
				j++;
			}
		}
		return n / (v.terms.length + terms.length - n);
	}

	/**
	 * Returns an array of tokens given an input text.
	 * <p>
	 * Each token is composed of letters or digits. Any character that is not
	 * letter or digit is considered a space.
	 * <p>
	 * Note: returned tokens are lower-case.
	 * 
	 * @see java.lang.Character#isLetterOrDigit(char ch)
	 */
	public static String[] tokenize(String text) {
		char s1[] = text.trim().toLowerCase().toCharArray();
		int j = 0;
		boolean space = true;
		for (int i = 0; i < s1.length; i++) {
			char ch = s1[i];
			if (Character.isLetterOrDigit(ch)) {
				s1[j++] = ch;
				space = false;
			} else if (space == false) {
				s1[j++] = ' ';
				space = true;
			}
		}
		// fill moved chs with spaces
		for (; j < s1.length; j++) {
			s1[j] = ' ';
		}

		String s = new String(s1);
		ArrayList tokens = new ArrayList();
		int i;
		while ((i = s.indexOf(" ")) != -1) {
			tokens.add(s.substring(0, i));
			s = s.substring(i + 1);
		}
		if (s.length() > 0)
			tokens.add(s.substring(0));
		return (String[]) tokens.toArray(new String[] {});
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Vector size:" + terms.length + " ");
		for (int i = 0; i < terms.length; i++) {
			buf.append(terms[i] + ":" + weights[i] + " ");
		}
		return "[" + buf.toString().trim() + "]";
	}

	/**
	 * Outputs the sequence of tokens as internally ordered with a single space
	 * between tokens. If parameter is true, term weights become term
	 * frequencies (i.e. occurrences) in the output string.
	 */
	public String toPlainString(boolean occurrences) {
		StringBuffer buf = new StringBuffer();
		if (occurrences) {
			for (int i = 0; i < terms.length; i++)
				for (int j = 0; j < weights[i]; j++)
					buf.append(terms[i] + " ");
		} else {
			for (int i = 0; i < terms.length; i++)
				buf.append(terms[i] + " ");
		}
		return buf.toString().trim();
	}

	/**
	 * Compares the two lists of terms alphabetically ordered from the two
	 * TermWeightVector objects. If a pair of terms differs, the results of
	 * compareTo comparison is returned. If a pair of terms corresponds but the
	 * corresponding weights differs, {-1,0,1} are returned according to the the
	 * sign of the difference. If one list ends before the other, the length
	 * difference is returned.
	 */
	public int compareTo(Object o) {
		if (this == o)
			return 0;
		TermWeightVector v = (TermWeightVector) o;
		int len1 = this.terms.length;
		int len2 = v.terms.length;
		int diff = 0;
		for (int i = 0; i < len1 && i < len2; i++) {
			diff = v.terms[i].compareTo(terms[i]);
			if (diff == 0)
				diff = (int) Math.signum(v.weights[i] - weights[i]);
			if (diff != 0)
				break;
		}
		if (diff == 0)
			diff = len2 - len1;
		return diff;
	}

	private void readObject(java.io.ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		terms = (String[]) stream.readObject();
		weights = (double[]) stream.readObject();
	}

	private void writeObject(java.io.ObjectOutputStream stream)
			throws IOException {
		stream.writeObject(terms);
		stream.writeObject(weights);
	}

	public static void main(String[] args) throws Exception {
		TermWeightVector vect1 = TermWeightVector.build(
				"ciao questa e e e una prova questa", new StandardAnalyzer());
		TermWeightVector vect10 = TermWeightVector.build(
				"ciao questa e una prova questa", new StandardAnalyzer());
		TermWeightVector vect3 = TermWeightVector.build(
				" ciao questa e una prova prova1", new StandardAnalyzer());
		TermWeightVector vect4 = TermWeightVector.build(
				"ciao questa e una prova questa", new StandardAnalyzer());
		TermWeightVector vect5 = TermWeightVector.build("ciao",
				new StandardAnalyzer());
		TermWeightVector vect9 = TermWeightVector.build("ciao",
				new StandardAnalyzer());
		TermWeightVector vect6 = TermWeightVector.build("cciao",
				new StandardAnalyzer());
		TermWeightVector vect7 = TermWeightVector.build("caiao",
				new StandardAnalyzer());
		TermWeightVector vect8 = TermWeightVector.build("cxiao",
				new StandardAnalyzer());
		System.out.println("compare1:" + vect1.compareTo(vect3));
		System.out.println("compare2:" + vect1.compareTo(vect4));
		System.out.println("compare3:" + vect1.compareTo(vect5));
		System.out.println("compare4:" + vect1.compareTo(vect6));
		System.out.println("compare5:" + vect1.compareTo(vect7));
		System.out.println("compare6:" + vect1.compareTo(vect8));
		System.out.println("compare7:" + vect1.compareTo(vect9));
		System.out.println("compare8:" + vect1.compareTo(vect10));

		System.out.println(vect1.toString());
		System.out.println(vect1.norm());
		vect1.normalize();
		System.out.println(vect1.toString());
		vect1.timesScalar(5);
		System.out.println(vect1.toString());
		vect1.normalize();
		System.out.println(vect1.toString());
		TermWeightVector vect2 = TermWeightVector.build(vect1.terms);
		System.out.println(vect2.toString());

		System.out.println(vect3.toString());

		System.out.println(vect1.cosineRule(vect2));
		System.out.println(vect1.jaccard(vect2));
		// System.out.println(vect1.add(vect3));
		vect1.merge(vect3);
		System.out.println(vect1);
		vect1 = TermWeightVector.build("ciao questa e una prova questa",
				new StandardAnalyzer());
		vect1.add(vect3);
		System.out.println(vect1);
		vect3 = vect1.truncate(2);
		System.out.println(vect3);
	}

}
