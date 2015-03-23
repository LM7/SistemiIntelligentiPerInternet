/*
 * @(#)StringTokenizer.java	1.0 05/01/07
 *
 * Copyright 2007 Fabio Gasparetti. All rights reserved.
 */
package Final_Version.events;

import java.util.ArrayList;

/**
 * A simple string tokenizer.
 * 
 * See also the tokenizers in the Lucene library.
 * 
 * @author Fabio Gasparetti
 * @version 1.0, 05/01/07
 */
public class StringTokenizer {
	/**
	 * Returns the number of tokens in a given string.
	 * 
	 * Returns the number of tokens in a given string <code>text</code>, where each token
	 * is separated by one of the characters in the <code>separator</code> string.
	 * 
	 * @param text
	 * @param separator
	 * @return
	 */
	public static int numElements(String text, String separator) {
		return StringTokenizer.elements(text,separator).length;
	}
	
	/**
	 * Extracts the list of tokens in a given string.
	 * 
	 * Extracts the list of tokens in a given string <code>text</code>, where each token
	 * is separated by one of the characters in the <code>separator</code> string.
	 * 
	 * @param text
	 * @param separator
	 * @return
	 */
	public static String[] elements(String text, String separator) {
		ArrayList list = new ArrayList(); 
		char[] s = text.toCharArray();
		int i, start = 0, end = -1;
		boolean foundText = false;
		for (i = 0; i < s.length; i++) {
			char ch = text.charAt(i);
			if (separator.lastIndexOf(ch) >= 0) {
				// more than one separator in a row
				if (foundText == false) {
					start = i+1;
				} else {	
					end = i;
					list.add(text.substring(start, end));
					start = end+1;
					end = -1;
					foundText = false;
				}
				continue;
			}
			foundText = true;
		}
		// last sequence is the element
		if ((start != -1) && foundText && (i >= start+1)) {
			list.add(text.substring(start, i));
		}
		return (String[])list.toArray(new String[] {});
	}
	
	public static String elementAt(String text, int idx, String separator) {
		char[] s = text.toCharArray();
		int i, start = 0, end = -1, curridx = 0;
		boolean foundText = false;
		for (i = 0; i < s.length; i++) {
			char ch = text.charAt(i);
			if (separator.lastIndexOf(ch) >= 0) {
				// more than one separator in a row
				if (foundText == false) {
					start = i+1;
				} else {	
					end = i;
					if (curridx++ == idx) {
						return text.substring(start, end);
					} else {
						start = end+1;
						end = -1;
						foundText = false;
					}
				}
				continue;
			}
			foundText = true;
		}
		// last sequence is the element
		if ((start != -1) && foundText && (i >= start+1) && (curridx == idx)) {
			return text.substring(start, i);
		}
		return null;
	}
	
    // debug
    public static void main(String[] args) {
    	String s[] = StringTokenizer.elements("pippo pluto, p,paperino", ", x");
    	for (int i = 0; i < s.length; i++) 
    		System.out.println(s[i]);
    }
}
