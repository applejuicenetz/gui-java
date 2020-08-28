/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalToolTipUI;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/MultiLineToolTip.java,v 1.14 2009/01/19 15:45:29 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class MultiLineToolTip extends JToolTip
{
   public MultiLineToolTip()
   {
      setUI(new MultiLineToolTipUI());
   }

   public MultiLineToolTip(MetalToolTipUI toolTipUI)
   {
      setUI(toolTipUI);
   }

   private class MultiLineToolTipUI extends MetalToolTipUI
   {
      private String[] strs;

      public void paint(Graphics g, JComponent c)
      {
         FontMetrics metrics = c.getFontMetrics(c.getFont());
         Dimension   size = c.getSize();

         g.setColor(c.getBackground());
         g.fillRect(0, 0, size.width, size.height);
         g.setColor(c.getForeground());
         if(strs != null)
         {
            int i = 0;

            for(String curS : strs)
            {
               g.drawString(curS, 3, (metrics.getHeight()) * (i + 1));
               i++;
            }
         }
      }

      public Dimension getPreferredSize(JComponent c)
      {
         FontMetrics metrics = c.getFontMetrics(c.getFont());
         String      tipText = ((JToolTip) c).getTipText();

         if(tipText == null)
         {
            tipText = "";
         }

         StringTokenizer st       = new StringTokenizer(tipText, "|");
         int             maxWidth = 0;
         List<String>    v        = new ArrayList<String>();
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
}
