/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.controller;

import java.awt.Dimension;
import java.awt.Point;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/PositionManagerImpl.java,v 1.3 2009/01/21 14:45:12 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
**/
public class PositionManagerImpl implements PositionManager
{
   private static PositionManager instance          = null;
   private PropertiesManager      propertiesManager;

   private PositionManagerImpl()
   {
      propertiesManager = PropertiesManager.getInstance();
   }

   public static synchronized PositionManager getInstance()
   {
      if(instance == null)
      {
         instance = new PositionManagerImpl();
      }

      return instance;
   }

   public void save()
   {
      propertiesManager.save();
   }

   public void setMainXY(Point p)
   {
      propertiesManager.setMainXY(p);
   }

   public Point getMainXY()
   {
      return propertiesManager.getMainXY();
   }

   public void setMainDimension(Dimension dimension)
   {
      propertiesManager.setMainDimension(dimension);
   }

   public Dimension getMainDimension()
   {
      return propertiesManager.getMainDimension();
   }

   public void setDownloadWidths(int[] widths)
   {
      propertiesManager.setDownloadWidths(widths);
   }

   public boolean isLegal()
   {
      return propertiesManager.isLegal();
   }

   public int[] getDownloadWidths()
   {
      return propertiesManager.getDownloadWidths();
   }

   public int[] getUploadWidths()
   {
      return propertiesManager.getUploadWidths();
   }

   public void setUploadWidths(int[] uploadWidths)
   {
      propertiesManager.setUploadWidths(uploadWidths);
   }

   public int[] getServerWidths()
   {
      return propertiesManager.getServerWidths();
   }

   public void setServerWidths(int[] serverWidths)
   {
      propertiesManager.setServerWidths(serverWidths);
   }

   public int[] getShareWidths()
   {
      return propertiesManager.getShareWidths();
   }

   public void setShareWidths(int[] shareWidths)
   {
      propertiesManager.setShareWidths(shareWidths);
   }

   public void setDownloadColumnVisible(int column, boolean visible)
   {
      propertiesManager.setDownloadColumnVisible(column, visible);
   }

   public boolean[] getDownloadColumnVisibilities()
   {
      return propertiesManager.getDownloadColumnVisibilities();
   }

   public void setUploadColumnVisible(int column, boolean visible)
   {
      propertiesManager.setUploadColumnVisible(column, visible);
   }

   public boolean[] getUploadColumnVisibilities()
   {
      return propertiesManager.getUploadColumnVisibilities();
   }

   public void setDownloadColumnIndex(int column, int index)
   {
      propertiesManager.setDownloadColumnIndex(column, index);
   }

   public int[] getDownloadColumnIndizes()
   {
      return propertiesManager.getDownloadColumnIndizes();
   }

   public void setUploadColumnIndex(int column, int index)
   {
      propertiesManager.setUploadColumnIndex(column, index);
   }

   public int[] getUploadColumnIndizes()
   {
      return propertiesManager.getUploadColumnIndizes();
   }

   public int[] getUploadWaitingWidths()
   {
      return propertiesManager.getUploadWaitingWidths();
   }

   public void setUploadWaitingWidths(int[] uploadWaitingWidths)
   {
      propertiesManager.setUploadWaitingWidths(uploadWaitingWidths);

   }

   public int[] getUploadWaitingColumnIndizes()
   {
      return propertiesManager.getUploadWaitingColumnIndizes();
   }

   public boolean[] getUploadWaitingColumnVisibilities()
   {
      return propertiesManager.getUploadWaitingColumnVisibilities();
   }

   public void setUploadWaitingColumnIndex(int column, int index)
   {
      propertiesManager.setUploadWaitingColumnIndex(column, index);
   }

   public void setUploadWaitingColumnVisible(int column, boolean visible)
   {
      propertiesManager.setUploadWaitingColumnVisible(column, visible);
   }
}
