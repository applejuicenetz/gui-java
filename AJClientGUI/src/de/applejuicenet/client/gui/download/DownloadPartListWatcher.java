package de.applejuicenet.client.gui.download;

import javax.swing.SwingUtilities;

import de.applejuicenet.client.gui.download.table.DownloadMainNode;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;

public class DownloadPartListWatcher {
	private Thread worker = null;
	private Object nodeObject = null;
	private DownloadController downloadController;
	
	public DownloadPartListWatcher(DownloadController downloadController){
		this.downloadController = downloadController;
	}

	public void setDownloadNode(Object node) {
		if (node == null) {
			nodeObject = null;
			if (worker != null) {
				worker.interrupt();
				worker = null;
			}
			return;
		}
		if (worker != null) {
			worker.interrupt();
			worker = null;
		}
		nodeObject = node;
		worker = new Thread() {
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (nodeObject.getClass() == DownloadMainNode.class
								&& ((DownloadMainNode) nodeObject)
										.getType() == DownloadMainNode.ROOT_NODE) {
							((DownloadPanel)downloadController.getComponent())
								.getDownloadDOOverviewPanel()
									.setDownloadDO(((DownloadMainNode) nodeObject)
											.getDownloadDO());
						} else if (nodeObject.getClass() == DownloadSourceDO.class) {
							if (((DownloadSourceDO) nodeObject).getStatus() == DownloadSourceDO.IN_WARTESCHLANGE
									&& ((DownloadSourceDO) nodeObject)
											.getQueuePosition() > 20) {
								return;
							}
							((DownloadPanel)downloadController.getComponent())
								.getDownloadDOOverviewPanel()
									.setDownloadSourceDO((DownloadSourceDO) nodeObject);
						}
					}
				});
			}
		};
		worker.start();
	}
}
