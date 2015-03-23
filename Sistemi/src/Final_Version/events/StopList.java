/*
 * @(#)StopList.java	1.0 05/01/07
 *
 * Copyright 2007 Fabio Gasparetti. All rights reserved.
 */
package Final_Version.events;

import java.util.HashSet;
import java.io.*;

/**
 * A stop list implementation.
 * 
 * @author Fabio Gasparetti
 * @version 1.0, 05/01/07
 */
public class StopList {
	protected static HashSet stopSet = new HashSet();

	public static final String[] DEFAULT_EN_STOPLIST = {
			// traditional en stop words
			"a", "an", "and", "are", "as", "at", "be", "but", "by", "for",
			"if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "s",
			"such", "t", "that", "the", "their", "then", "there", "these",
			"they", "this", "to", "was", "will", "with" };

	public StopList(String[] list) {
		// build stop word set
		for (int i = 0; i < list.length; i++) {
			stopSet.add(list[i]);
		}
	}

	public StopList(BufferedReader reader) throws IOException {
		load(reader);
	}

	/** Case insensitive matching */
	public static boolean isContained(String word) {
		return stopSet.contains(word.toLowerCase());
	}

	public String[] stoplist() {
		return (String[]) stopSet.toArray(new String[] {});
	}

	public static String[] defaultStoplist() {
		return DEFAULT_EN_STOPLIST;
	}

	public void load(BufferedReader reader) throws IOException {
		String line = null;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (!line.startsWith("#")) { // skip comments
				stopSet.add(line);
			}
		}
	}
}
