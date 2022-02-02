/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.search;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Search;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/search/SearchResultTabbedPane.java,v 1.7 2009/01/28 09:44:09 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class SearchResultTabbedPane extends JTabbedPane implements MouseListener
{
   private static Icon   icon   = IconManager.getInstance().getIcon("abbrechen");
   private static Logger logger = Logger.getLogger(SearchResultTabbedPane.class);

   public SearchResultTabbedPane()
   {
      addMouseListener(this);
   }

   public void addTab(String title, Component component)
   {
      super.addTab(title, component);
   }

   public void enableIconAt(int index, Search aSearch)
   {
      super.setIconAt(index, new CloseIcon(aSearch));
   }

   public void mouseClicked(MouseEvent e)
   {
      int tabNumber = getUI().tabForCoordinate(this, e.getX(), e.getY());

      if(tabNumber < 0)
      {
         return;
      }

      CloseIcon icon = ((CloseIcon) getIconAt(tabNumber));

      if(icon != null)
      {
         Rectangle rect = icon.getBounds();

         if(rect.contains(e.getX(), e.getY()))
         {
            removeTabAt(tabNumber);
            try
            {
               AppleJuiceClient.getAjFassade().cancelSearch(icon.getSearch());
            }
            catch(IllegalArgumentException e1)
            {
               logger.error(ApplejuiceFassade.ERROR_MESSAGE, e1);
            }
         }
      }
   }

   public void mouseEntered(MouseEvent mouseevent)
   {
   }

   public void mouseExited(MouseEvent mouseevent)
   {
   }

   public void mousePressed(MouseEvent mouseevent)
   {
   }

   public void mouseReleased(MouseEvent mouseevent)
   {
   }

   private class CloseIcon implements Icon
   {
      private int    x_pos;
      private int    y_pos;
      private int    width;
      private int    height;
      private Search aSearch;

      public CloseIcon(Search aSearch)
      {
         width        = icon.getIconWidth();
         height       = icon.getIconHeight();
         this.aSearch = aSearch;
      }

      public Search getSearch()
      {
         return aSearch;
      }

      public void paintIcon(Component c, Graphics g, int x, int y)
      {
         x_pos = x;
         y_pos = y;
         if(icon != null)
         {
            icon.paintIcon(c, g, x_pos, y_pos);
         }
      }

      public int getIconWidth()
      {
         return width;
      }

      public int getIconHeight()
      {
         return height;
      }

      public Rectangle getBounds()
      {
         return new Rectangle(x_pos, y_pos, width, height);
      }
   }
}
