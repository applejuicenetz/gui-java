/*
 * Created on 14.02.2005
 */
package test.de.applejuicenet.client.gui.powerdownload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import junit.framework.Assert;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;

/**
 * @author loevenit
 */
public class ApplejuiceFassadeDummy extends ApplejuiceFassade
{
	private Map<String, Download> snapshot = null;
	private List<DownloadDummy> expected = new Vector();

	public ApplejuiceFassadeDummy(Map<String, Download> downloads) throws IllegalArgumentException
	{
		super("", new Integer(Integer.MIN_VALUE), "", true);
		snapshot = downloads;
	}	

	public ApplejuiceFassadeDummy() throws IllegalArgumentException
	{
		super("", new Integer(Integer.MIN_VALUE), "", true);
		snapshot = new HashMap();
	}	

	public Map<String, Download> getDownloadsSnapshot()
	{
		return snapshot;
	}
	
	public void pauseDownload(List<Download> downloads)
		throws IllegalArgumentException
	{
		for (Download cur : downloads) {
			((DownloadDummy)cur).setStatus(Download.PAUSIERT);
			snapshot.put(Integer.toString(cur.getId()), cur);
		}
	}

	public void resumeDownload(List<Download> downloads)
		throws IllegalArgumentException
	{
		for (Download cur : downloads) {
			((DownloadDummy)cur).setStatus(Download.SUCHEN_LADEN);
			snapshot.put(Integer.toString(cur.getId()), cur);
		}
	}
	public void setPowerDownload(List<Download> downloads, Integer powerDownload)
		throws IllegalArgumentException
	{
		for (Download cur : downloads) {
			((DownloadDummy)cur).setPowerdownload(powerDownload);
			snapshot.put(Integer.toString(cur.getId()), cur);
		}
	}

	public void addDownload(Download download)
	{
		this.snapshot.put(Integer.toString(download.getId()), download);
	}
	
	public void addExpectedDownload(DownloadDummy dummy)
	{
		this.expected.add(dummy);
	}
	
	public void clearExpectedDownload()
	{
		this.expected.clear();
	}
	
	public void verify() {
		Assert.assertEquals("expected.size() != snapshot.size()", expected.size(), snapshot.size());
		for (DownloadDummy cur : expected) {
			Download download = snapshot.get(Integer.toString(cur.getId()));
			Assert.assertNotNull(download);
			Assert.assertEquals("Filename nicht gleich", cur.getFilename(), download.getFilename());
			Assert.assertEquals("Status nicht gleich", cur.getStatus(), download.getStatus());
			Assert.assertEquals("BereitsGeladen nicht gleich", cur.getProzentGeladen(), download.getProzentGeladen(), 0.00);
			Assert.assertEquals("Powerdownload nicht gleich", cur.getPowerDownload(), download.getPowerDownload());
			Assert.assertEquals("Groesse nicht gleich", cur.getGroesse(), download.getGroesse());
			Assert.assertEquals("quellen nicht gleich", cur.getSources(), download.getSources());
		}
	}
}
