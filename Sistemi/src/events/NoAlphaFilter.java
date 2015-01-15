/*
 * @(#)StopList.java	1.0 05/01/07
 *
 * Copyright 2007 Fabio Gasparetti. All rights reserved.
 */
package events;

import java.io.IOException;
import org.apache.lucene.analysis.*;

/**
 * Math any sequence that contains any character but Letter (alphabetic character extended with unicodes)
 * UPPERCASE_LETTER (Lu Unicode category)
 * LOWERCASE_LETTER (Ll Unicode category)
 * TITLECASE_LETTER (Lt Unicode category)
 * MODIFIER_LETTER (Lm Unicode category)
 * OTHER_LETTER (Lo Unicode category)
 * 
 * @author  Fabio Gasparetti
 * @version 1.0, 05/01/07
 */
public final class NoAlphaFilter extends org.apache.lucene.analysis.TokenFilter {

  public NoAlphaFilter(org.apache.lucene.analysis.TokenStream in) {
    super(in);
  }

  public final Token next() throws IOException {
    for (Token token = input.next(); token != null; token = input.next()) {
      String s = token.termText();
      for (int i = 0; i < s.length(); i++) 
    	  if (Character.isLetter(s.charAt(i)))
    		  return token;
    }
    return null;
  }  
}
