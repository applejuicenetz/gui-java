/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.controller;

import java.awt.Dimension;
import java.awt.Point;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/PositionManager.java,v 1.11 2009/01/26 13:31:36 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General  License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public interface PositionManager
{
   void save();

   void setMainXY(Point p);

   Point getMainXY();

   void setMainDimension(Dimension dimension);

   Dimension getMainDimension();

   void setDownloadWidths(int[] widths);

   void setDownloadSourcesWidths(int[] widths);

   boolean isLegal();

   int[] getDownloadWidths();

   int[] getDownloadSourcesWidths();

   int[] getUploadWidths();

   void setUploadWidths(int[] uploadWidths);

   int[] getUploadWaitingWidths();

   void setUploadWaitingWidths(int[] uploadWidths);

   int[] getServerWidths();

   void setServerWidths(int[] serverWidths);

   int[] getShareWidths();

   void setShareWidths(int[] shareWidths);

   void setDownloadColumnVisible(int column, boolean visible);

   void setDownloadSourcesColumnVisible(int column, boolean visible);

   boolean[] getDownloadColumnVisibilities();

   boolean[] getDownloadSourcesColumnVisibilities();

   void setUploadColumnVisible(int column, boolean visible);

   boolean[] getUploadColumnVisibilities();

   void setUploadWaitingColumnVisible(int column, boolean visible);

   boolean[] getUploadWaitingColumnVisibilities();

   void setDownloadColumnIndex(int column, int index);

   void setDownloadSourcesColumnIndex(int column, int index);

   int[] getDownloadColumnIndizes();

   int[] getDownloadSourcesColumnIndizes();

   void setUploadColumnIndex(int column, int index);

   int[] getUploadColumnIndizes();

   void setUploadWaitingColumnIndex(int column, int index);

   int[] getUploadWaitingColumnIndizes();
}
