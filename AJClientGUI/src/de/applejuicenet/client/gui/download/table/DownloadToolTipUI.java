package de.applejuicenet.client.gui.download.table;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalToolTipUI;

public class DownloadToolTipUI extends MetalToolTipUI {
	
    private String[] strs;

    public void paint(Graphics g, JComponent c) {
        if (strs != null) {
            Font normalFont = c.getFont();
            FontMetrics normalMetrics = c.getFontMetrics(normalFont);
            Font titleFont = new Font(c.getFont().getName(), Font.BOLD,
                                 c.getFont().getSize());
            FontMetrics titleMetrics = c.getFontMetrics(titleFont);
            Dimension size = c.getSize();
            g.setColor(c.getBackground());
            g.fillRect(0, 0, size.width, size.height);
            g.setColor(c.getForeground());
            int length = strs.length;
            int maxWidth = titleMetrics.stringWidth(strs[0]);
            int tmp;
            for (int a=1; a<strs.length; a++){
                tmp = normalMetrics.stringWidth(strs[a]);
                if (tmp > maxWidth){
                    maxWidth = tmp;
                }
            }
            g.setFont(titleFont);
            int y = normalMetrics.getHeight();
            g.drawString(strs[0], 3, y);
            g.drawLine(3, y + y/2, maxWidth - 3, y + y/2);
            g.setFont(normalFont);
            for (int i = 1; i < length; i++) {
                g.drawString(strs[i], 3, y * (i + 1));
            }
        }
    }

    public Dimension getPreferredSize(JComponent c) {
        Font font = new Font(c.getFont().getName(), Font.BOLD,
                             c.getFont().getSize());
        FontMetrics metrics = c.getFontMetrics(font);
        String tipText = ( (JToolTip) c).getTipText();
        if (tipText == null) {
            tipText = "";
        }
        StringTokenizer st = new StringTokenizer(tipText, "|");
        int maxWidth = 0;
        Vector<String> v = new Vector<String>();
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
        return new Dimension(maxWidth + 6, height + 4);
    }
}
