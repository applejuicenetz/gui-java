package de.applejuicenet.client.gui.download;

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
				.getDownloadDOOverviewPanel()
					.setDownloadDO(null);
			return;
		}
		if (nodeObject.getClass() == DownloadMainNode.class
				&& ((DownloadMainNode) nodeObject)
						.getType() == DownloadMainNode.ROOT_NODE) {
			((DownloadPanel)downloadController.getComponent())
				.getDownloadDOOverviewPanel()
					.setDownloadDO(((DownloadMainNode) nodeObject)
							.getDownload());
		} else if (nodeObject instanceof DownloadSource) {
			if (((DownloadSource) nodeObject).getStatus() == DownloadSource.IN_WARTESCHLANGE
					&& ((DownloadSource) nodeObject)
							.getQueuePosition() > 20) {
				((DownloadPanel)downloadController.getComponent())
				.getDownloadDOOverviewPanel()
					.setDownloadDO(null);
			}
			else{
				((DownloadPanel)downloadController.getComponent())
					.getDownloadDOOverviewPanel()
						.setDownloadSourceDO((DownloadSource) nodeObject);
			}
		}
	}
}
