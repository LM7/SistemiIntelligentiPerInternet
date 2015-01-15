/*
 * @(#)StopList.java	1.0 05/01/07
 *
 * Copyright 2007 Fabio Gasparetti. All rights reserved.
 */
package events;

import java.io.IOException;
import org.apache.lucene.analysis.*;

/**
 * 
 * 
 * @author  Fabio Gasparetti
 * @version 1.0, 05/01/07
 */
public final class NumberFilter extends org.apache.lucene.analysis.TokenFilter {

  public NumberFilter(org.apache.lucene.analysis.TokenStream in) {
    super(in);
  }

  public final Token next() throws IOException {
    for (Token token = input.next(); token != null; token = input.next()) {
      String s = token.termText();
      if (!s.matches("[0-9,\\.]+"))
        return token;
    }
    return null;
  }  
}
