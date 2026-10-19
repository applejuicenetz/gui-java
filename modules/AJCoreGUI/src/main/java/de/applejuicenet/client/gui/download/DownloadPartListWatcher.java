/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download;

import de.applejuicenet.client.facade.entity.Download;
import de.applejuicenet.client.facade.entity.DownloadSource;

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

      if(download.getStatus() != Download.SEARCHING_LOADING && download.getStatus() != Download.PAUSED)
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

      if(downloadSource.getStatus() == DownloadSource.IN_QUEUE && downloadSource.getQueuePosition() > 20)
      {
         ((DownloadPanel) downloadController.getComponent()).getDownloadOverviewPanel().setDownload(null);
      }
      else
      {
         ((DownloadPanel) downloadController.getComponent()).getDownloadOverviewPanel().setDownloadSource(downloadSource);
      }
   }
}
