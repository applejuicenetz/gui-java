package de.applejuicenet.client.shared;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/NumberInputVerifier.java,v 1.9 2004/10/06 12:29:14 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 */

public class NumberInputVerifier
    extends PlainDocument {

    private static final long serialVersionUID = -5119488077963020043L;
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