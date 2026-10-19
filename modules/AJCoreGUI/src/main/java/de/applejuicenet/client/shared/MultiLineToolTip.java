/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared;

import javax.swing.JToolTip;

/** Theme-rendered tooltip supporting the language files' pipe-separated lines. */
public class MultiLineToolTip extends JToolTip {
    @Override
    public void setTipText(String text) {
        if (text == null) {
            super.setTipText(null);
            return;
        }
        String escaped = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        super.setTipText("<html>" + escaped.replace("|", "<br>") + "</html>");
    }
}
