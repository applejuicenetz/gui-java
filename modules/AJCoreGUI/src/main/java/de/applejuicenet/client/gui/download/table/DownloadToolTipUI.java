/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download.table;

import javax.swing.*;
import javax.swing.plaf.metal.MetalToolTipUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DownloadToolTipUI extends MetalToolTipUI
{
   private String[] strs;

   public void paint(Graphics g, JComponent c)
   {
      if(strs != null)
      {
         Font        normalFont    = c.getFont();
         FontMetrics normalMetrics = c.getFontMetrics(normalFont);
         Font        titleFont     = new Font(c.getFont().getName(), Font.BOLD, c.getFont().getSize());
         FontMetrics titleMetrics  = c.getFontMetrics(titleFont);
         Dimension   size          = c.getSize();

         g.setColor(c.getBackground());
         g.fillRect(0, 0, size.width, size.height);
         g.setColor(c.getForeground());
         int length   = strs.length;
         int maxWidth = titleMetrics.stringWidth(strs[0]);
         int tmp;

         for(String curS : strs)
         {
            tmp = normalMetrics.stringWidth(curS);
            if(tmp > maxWidth)
            {
               maxWidth = tmp;
            }
         }

         g.setFont(titleFont);
         int y = normalMetrics.getHeight();

         g.drawString(strs[0], 3, y);
         g.drawLine(3, y + y / 2, maxWidth - 3, y + y / 2);
         g.setFont(normalFont);
         for(int i = 1; i < length; i++)
         {
            g.drawString(strs[i], 3, y * (i + 1));
         }
      }
   }

   public Dimension getPreferredSize(JComponent c)
   {
      Font        font    = new Font(c.getFont().getName(), Font.BOLD, c.getFont().getSize());
      FontMetrics metrics = c.getFontMetrics(font);
      String      tipText = ((JToolTip) c).getTipText();

      if(tipText == null)
      {
         tipText = "";
      }

      int             maxWidth = 0;
      StringTokenizer st = new StringTokenizer(tipText, "|");
      List<String>    v  = new ArrayList<String>();

      String          token;
      int             width;

      while(st.hasMoreTokens())
      {
         token = st.nextToken();
         width = SwingUtilities.computeStringWidth(metrics, token);

         maxWidth = (maxWidth < width) ? width : maxWidth;
         v.add(token);
      }

      int lines = v.size();

      if(lines < 1)
      {
         strs  = null;
         lines = 1;
      }
      else
      {
         strs = (String[]) v.toArray(new String[v.size()]);
      }

      int height = metrics.getHeight() * lines;

      return new Dimension(maxWidth + 6, height + 4);
   }
}
