/*
 * Created on 14.02.2005
 */
package test.de.applejuicenet.client.gui.powerdownload;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;

/**
 * @author loevenit
 */
public class DownloadDummy implements Download
{
	private Integer id;
	private String bezeichnung;
	private double prozentGeladen; 
	private int status;
	private int powerdwl;
	private long groesse;
	
	public DownloadDummy(Integer id, String bezeichnung, double prozentGeladen, int status, int powerdwl, long groesse)
	{
		super();
		this.id = id;
		this.bezeichnung = bezeichnung;
		this.prozentGeladen = prozentGeladen;
		this.status = status;
		this.powerdwl = powerdwl;
		this.groesse = groesse;
	}

	public DownloadDummy(Integer id, String bezeichnung, double prozentGeladen, int status, int powerdwl)
	{
		this(id, bezeichnung, prozentGeladen, status, powerdwl, 0);
	}

	public DownloadDummy(int id, String bezeichnung, double prozentGeladen, int status, int powerdwl)
	{
		this(new Integer(id), bezeichnung, prozentGeladen, status, powerdwl, 0);
	}

	public DownloadDummy(int id, double prozentGeladen, int status, int powerdwl)
	{
		this(id, "", prozentGeladen, status, powerdwl, 0);
	}

	public String getProzentGeladenAsString()
	{
		return Double.toString(prozentGeladen);
	}

	public double getProzentGeladen()
	{
		return prozentGeladen;
	}

	public DownloadSource getSourceById(int sourceId)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public DownloadSource[] getSources()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int getShareId()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public String getHash()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public long getGroesse()
	{
		return groesse;
	}

	public int getStatus()
	{
		return status;
	}

	public String getFilename()
	{
		return bezeichnung;
	}

	public String getTargetDirectory()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int getPowerDownload()
	{
		return powerdwl;
	}

	public int getId()
	{
		return id;
	}

	public int getTemporaryFileNumber()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public long getReady()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public long getRestZeit()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public String getRestZeitAsString()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public long getSpeedInBytes()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public long getBereitsGeladen()
	{
		return 0;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	public void setPowerdownload(Integer powerDownload)
	{	
		this.powerdwl = powerDownload.intValue();
	}
}