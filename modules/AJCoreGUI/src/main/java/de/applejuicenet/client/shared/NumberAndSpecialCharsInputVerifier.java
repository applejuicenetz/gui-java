package de.applejuicenet.client.shared;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class NumberAndSpecialCharsInputVerifier
    extends PlainDocument {

  private String match;
	private String regex;

  public NumberAndSpecialCharsInputVerifier(String chars) {
    match = "^[0-9" + chars + "]+$";
  }

  public NumberAndSpecialCharsInputVerifier(String chars, int count) {
    match = "^[0-9" + chars + "]{0," + count + "}$";
  }
  
  public void setSpecialPattern(String regex) {
  	this.regex = regex;
  }

  public void insertString(int offset, String str, AttributeSet attSet) throws
      BadLocationException {
    if (str == null) {
      return;
    }
    String old = getText(0, getLength());
    if (!(old + str).matches(match)) {
    	return;
    }
    if (!(old + str).matches(regex)) {
    	return;
    }    
    super.insertString(offset, str, attSet);
    old = getText(0, getLength());
  }
}