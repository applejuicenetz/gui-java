/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.controller;

import java.awt.*;

/**
 * Copyright: General  License
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

   void setDownloadSort(int column, boolean ascent);

   int[] getDownloadSort();

   void setDownloadSourcesSort(int column, boolean ascent);

   int[] getDownloadSourcesSort();
   
   void setUploadSort(int column, boolean ascent);

   int[] getUploadSort();

   void setUploadWaitingSort(int column, boolean ascent);

   int[] getUploadWaitingSort();

   void setServerSort(int column, boolean ascent);

   int[] getServerSort();
   
   void setSearchSort(int column, boolean ascent);

   int[] getSearchSort();
   
   boolean[] getDownloadColumnVisibilities();

   boolean[] getDownloadSourcesColumnVisibilities();

   void setUploadColumnVisible(int column, boolean visible);

   boolean[] getUploadColumnVisibilities();

   void setUploadWaitingColumnVisible(int column, boolean visible);

   boolean[] getUploadWaitingColumnVisibilities();

   void setDownloadColumnIndex(int column, int index);

   void setDownloadSourcesColumnIndex(int column, int index);

   int[] getDownloadColumnIndices();

   int[] getDownloadSourcesColumnIndices();

   void setUploadColumnIndex(int column, int index);

   int[] getUploadColumnIndices();

   void setUploadWaitingColumnIndex(int column, int index);

   int[] getUploadWaitingColumnIndices();
}
