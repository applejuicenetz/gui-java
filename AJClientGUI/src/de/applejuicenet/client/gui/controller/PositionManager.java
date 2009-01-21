/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.controller;

import java.awt.Dimension;
import java.awt.Point;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/PositionManager.java,v 1.10 2009/01/21 14:45:12 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public interface PositionManager
{
   public void save();

   public void setMainXY(Point p);

   public Point getMainXY();

   public void setMainDimension(Dimension dimension);

   public Dimension getMainDimension();

   public void setDownloadWidths(int[] widths);

   public boolean isLegal();

   public int[] getDownloadWidths();

   public int[] getUploadWidths();

   public void setUploadWidths(int[] uploadWidths);

   public int[] getUploadWaitingWidths();

   public void setUploadWaitingWidths(int[] uploadWidths);

   public int[] getServerWidths();

   public void setServerWidths(int[] serverWidths);

   public int[] getShareWidths();

   public void setShareWidths(int[] shareWidths);

   public void setDownloadColumnVisible(int column, boolean visible);

   public boolean[] getDownloadColumnVisibilities();

   public void setUploadColumnVisible(int column, boolean visible);

   public boolean[] getUploadColumnVisibilities();

   public void setUploadWaitingColumnVisible(int column, boolean visible);

   public boolean[] getUploadWaitingColumnVisibilities();

   public void setDownloadColumnIndex(int column, int index);

   public int[] getDownloadColumnIndizes();

   public void setUploadColumnIndex(int column, int index);

   public int[] getUploadColumnIndizes();

   public void setUploadWaitingColumnIndex(int column, int index);

   public int[] getUploadWaitingColumnIndizes();
}
