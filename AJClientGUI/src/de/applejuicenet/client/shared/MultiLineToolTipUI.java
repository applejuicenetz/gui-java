package de.applejuicenet.client.shared;

import java.util.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.metal.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/MultiLineToolTipUI.java,v 1.4 2003/06/30 20:35:50 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: MultiLineToolTipUI.java,v $
 * Revision 1.4  2003/06/30 20:35:50  maj0r
 * Code optimiert.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class MultiLineToolTipUI
    extends MetalToolTipUI {
  private String[] strs;
  private int maxWidth = 0;

  public void paint(Graphics g, JComponent c) {
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(g.getFont());
    Dimension size = c.getSize();
    g.setColor(c.getBackground());
    g.fillRect(0, 0, size.width, size.height);
    g.setColor(c.getForeground());
    if (strs != null) {
      int length = strs.length;
      for (int i = 0; i < length; i++) {
        g.drawString(strs[i], 3, (metrics.getHeight()) * (i + 1));
      }
    }
  }

  public Dimension getPreferredSize(JComponent c) {
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(c.getFont());
    String tipText = ( (JToolTip) c).getTipText();
    if (tipText == null) {
      tipText = "";
    }
    StringTokenizer st = new StringTokenizer(tipText, "|");
    int maxWidth = 0;
    Vector v = new Vector();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      int width = SwingUtilities.computeStringWidth(metrics, token);
      maxWidth = (maxWidth < width) ? width : maxWidth;
      v.addElement(token);
    }
    int lines = v.size();
    if (lines < 1) {
      strs = null;
      lines = 1;
    }
    else {
      strs = new String[lines];
      int i = 0;
      for (Enumeration e = v.elements(); e.hasMoreElements(); i++) {
        strs[i] = (String) e.nextElement();
      }
    }
    int height = metrics.getHeight() * lines;
    this.maxWidth = maxWidth;
    return new Dimension(maxWidth + 6, height + 4);
  }
}
