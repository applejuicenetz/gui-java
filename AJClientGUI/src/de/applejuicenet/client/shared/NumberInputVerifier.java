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

  private boolean limits;
  private int max = 0;
  private int min = 0;

  public NumberInputVerifier() {
    limits = false;
  }

  public NumberInputVerifier(int min, int max) {
    limits = true;
    this.min = min;
    this.max = max;
  }

  public void insertString(int offset, String str, AttributeSet attSet) throws
      BadLocationException {
    if (str == null) {
      return;
    }
    String old = getText(0, getLength());

    try {
      int i = Integer.parseInt(str);
      if (limits) {
        int gesamt = Integer.parseInt(old.substring(0, offset) + str +
                                      old.substring(offset));
        if (gesamt < min || gesamt > max) {
          return;
        }
      }
    }
    catch (NumberFormatException nfE) {
      return;
    }
    super.insertString(offset, str, attSet);
    old = getText(0, getLength());
  }
}