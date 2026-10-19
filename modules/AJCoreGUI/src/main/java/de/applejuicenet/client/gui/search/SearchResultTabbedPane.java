/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.search;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.entity.Search;
import de.applejuicenet.client.facade.exception.IllegalArgumentException;
import de.applejuicenet.client.shared.IconManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Copyright: General Public License
 */
public class SearchResultTabbedPane extends JTabbedPane implements MouseListener
{
   private static final Icon   icon   = IconManager.getInstance().getIcon("abbrechen");
   private static final Logger logger = LoggerFactory.getLogger(SearchResultTabbedPane.class);

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
               AppleJuiceClient.getAjFacade().cancelSearch(icon.getSearch());
            }
            catch(IllegalArgumentException e1)
            {
               logger.error(ApplejuiceFacade.ERROR_MESSAGE, e1);
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
