package de.applejuicenet.client.shared;

import javax.swing.text.*;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class NumberInputVerifier
    extends PlainDocument {
  public void insertString(int offset, String str, AttributeSet attSet) throws
      BadLocationException {
    if (str == null)
      return;
    String old = getText(0, getLength());
    String newStr = old.substring(0, offset) + str + old.substring(offset);

    try {
      int i = Integer.parseInt(str);
    }
    catch (NumberFormatException nfE) {
      return;
    }
    super.insertString(offset, str, attSet);
  }
}