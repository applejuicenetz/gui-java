package de.applejuicenet.client.shared;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.metal.*;

public class MultiLineToolTipUI extends MetalToolTipUI {
  private String[] strs;
  private int maxWidth = 0;

  public void paint(Graphics g, JComponent c) {
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(g.getFont());
    Dimension size = c.getSize();
    g.setColor(c.getBackground());
    g.fillRect(0, 0, size.width, size.height);
    g.setColor(c.getForeground());
    if (strs != null) {
      for (int i=0;i<strs.length;i++) {
	g.drawString(strs[i], 3, (metrics.getHeight()) * (i+1));
      }
    }
  }

  public Dimension getPreferredSize(JComponent c) {
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(c.getFont());
    String tipText = ((JToolTip)c).getTipText();
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
    } else {
      strs = new String[lines];
      int i=0;
      for (Enumeration e = v.elements(); e.hasMoreElements() ;i++) {
        strs[i] = (String)e.nextElement();
      }
    }
    int height = metrics.getHeight() * lines;
    this.maxWidth = maxWidth;
    return new Dimension(maxWidth + 6, height + 4);
  }
}

