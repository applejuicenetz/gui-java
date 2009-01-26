/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;

public class DownloadPartListWatcher
{
   private DownloadController downloadController;

   public DownloadPartListWatcher(DownloadController downloadController)
   {
      this.downloadController = downloadController;
   }

   public void setDownloadNode(Download download)
   {
      if(download == null)
      {
         ((DownloadPanel) downloadController.getComponent()).getDownloadOverviewPanel().setDownload(null);
         return;
      }

      if(download.getStatus() != Download.SUCHEN_LADEN && download.getStatus() != Download.PAUSIERT)
      {
         download = null;
      }

      ((DownloadPanel) downloadController.getComponent()).getDownloadOverviewPanel().setDownload(download);
   }

   public void setDownloadNode(DownloadSource downloadSource)
   {
      if(downloadSource == null)
      {
         ((DownloadPanel) downloadController.getComponent()).getDownloadOverviewPanel().setDownload(null);
         return;
      }

      if(downloadSource.getStatus() == DownloadSource.IN_WARTESCHLANGE && downloadSource.getQueuePosition() > 20)
      {
         ((DownloadPanel) downloadController.getComponent()).getDownloadOverviewPanel().setDownload(null);
      }
      else
      {
         ((DownloadPanel) downloadController.getComponent()).getDownloadOverviewPanel().setDownloadSource(downloadSource);
      }
   }
}
