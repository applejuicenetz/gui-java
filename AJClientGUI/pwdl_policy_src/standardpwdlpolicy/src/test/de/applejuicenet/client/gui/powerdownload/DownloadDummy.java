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
	private double bereitsGeladen; 
	private int status;
	private int powerdwl;
	private long groesse;
	
	public DownloadDummy(Integer id, String bezeichnung, double bereitsGeladen, int status, int powerdwl)
	{
		super();
		this.id = id;
		this.bezeichnung = bezeichnung;
		this.bereitsGeladen = bereitsGeladen;
		this.status = status;
		this.powerdwl = powerdwl;
		this.groesse = 0;
	}

	public DownloadDummy(Integer id, String bezeichnung, double bereitsGeladen, int status, int powerdwl, long groesse)
	{
		super();
		this.id = id;
		this.bezeichnung = bezeichnung;
		this.bereitsGeladen = bereitsGeladen;
		this.status = status;
		this.powerdwl = powerdwl;
		this.groesse = groesse;
	}

	public String getProzentGeladenAsString()
	{
		return Double.toString(bereitsGeladen);
	}

	public double getProzentGeladen()
	{
		return bereitsGeladen;
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
		return (long)bereitsGeladen;
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