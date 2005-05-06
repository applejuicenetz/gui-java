package de.applejuicenet.client.gui.download;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.gui.download.table.DownloadMainNode;

public class DownloadPartListWatcher {
	private Object nodeObject = null;
	private DownloadController downloadController;
	
	public DownloadPartListWatcher(DownloadController downloadController){
		this.downloadController = downloadController;
	}

	public void setDownloadNode(Object node) {
		nodeObject = node;
		if (nodeObject == null) {
			((DownloadPanel)downloadController.getComponent())
				.getDownloadOverviewPanel()
					.setDownload(null);
			return;
		}
		if (nodeObject.getClass() == DownloadMainNode.class
				&& ((DownloadMainNode) nodeObject)
						.getType() == DownloadMainNode.ROOT_NODE) {
            Download download = ((DownloadMainNode) nodeObject).getDownload();
            if (download.getStatus() != Download.SUCHEN_LADEN
                    && download.getStatus() != Download.PAUSIERT){
                download = null;
            }
			((DownloadPanel)downloadController.getComponent())
				.getDownloadOverviewPanel().setDownload(download);
		} else if (nodeObject instanceof DownloadSource) {
			if (((DownloadSource) nodeObject).getStatus() == DownloadSource.IN_WARTESCHLANGE
					&& ((DownloadSource) nodeObject)
							.getQueuePosition() > 20) {
				((DownloadPanel)downloadController.getComponent())
				.getDownloadOverviewPanel()
					.setDownload(null);
			}
			else{
				((DownloadPanel)downloadController.getComponent())
					.getDownloadOverviewPanel()
						.setDownloadSource((DownloadSource) nodeObject);
			}
		}
	}
}
