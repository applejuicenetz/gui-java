package de.applejuicenet.client.shared;

import javax.swing.text.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/NumberInputVerifier.java,v 1.7 2003/12/29 16:04:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: NumberInputVerifier.java,v $
 * Revision 1.7  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.6  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.5  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
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
      Integer.parseInt(str);
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