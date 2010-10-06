package de.applejuicenet.client.fassade.entity;

import java.io.Serializable;
import java.text.DecimalFormat;

@SuppressWarnings("serial")
public abstract class Information implements Serializable
{

	public static final int				VERBUNDEN				= 0;
	public static final int				NICHT_VERBUNDEN			= 1;
	public static final int				VERSUCHE_ZU_VERBINDEN	= 2;

	private static final DecimalFormat	percentFormatter		= new DecimalFormat("###,##0.00");

	public abstract long getSessionUpload();

	public abstract long getSessionDownload();

	public abstract long getCredits();

	public abstract long getUploadSpeed();

	public abstract long getDownloadSpeed();

	public abstract long getOpenConnections();

	public abstract long getMaxUploadPositions();

	public abstract int getVerbindungsStatus();

	public abstract String getServerName();

	public abstract String getExterneIP();

	public abstract Server getServer();

	public final String getCreditsAsString()
	{
		StringBuffer result = new StringBuffer(" Credits: ");
		result.append(bytesUmrechnen(getCredits()));
		return result.toString();
	}

	public final String getUpDownSessionAsString()
	{
		StringBuffer result = new StringBuffer(" in: ");
		result.append(bytesUmrechnen(getSessionDownload()));
		result.append(" out: ");
		result.append(bytesUmrechnen(getSessionUpload()));
		return result.toString();
	}

	public final String getUpDownAsString()
	{
		StringBuffer result = new StringBuffer(" in: ");
		result.append(getBytesSpeed(getDownloadSpeed()));
		result.append(" out: ");
		result.append(getBytesSpeed(getUploadSpeed()));
		return result.toString();
	}

	public final StringBuffer getUpAsString()
	{
		return getBytesSpeed(getUploadSpeed());
	}

	public final StringBuffer getDownAsString()
	{
		return getBytesSpeed(getDownloadSpeed());
	}

	private final StringBuffer getBytesSpeed(long bytes)
	{
		if (bytes == 0)
		{
			return new StringBuffer("0 KB/s");
		}
		StringBuffer result = bytesUmrechnen(bytes);
		result.append("/s");
		return result;
	}

	private final StringBuffer bytesUmrechnen(long bytes)
	{
		boolean minus = false;
		if (bytes < 0)
		{
			minus = true;
			bytes *= -1;
		}
		if (bytes == 0)
		{
			return new StringBuffer("0 MB");
		}
		long faktor = 1;
		if (bytes < 1024l)
		{
			faktor = 1;
		}
		else if (bytes / 1024l < 1024l)
		{
			faktor = 1024l;
		}
		else if (bytes / 1048576l < 1024l)
		{
			faktor = 1048576l;
		}
		else if (bytes / 1073741824l < 1024l)
		{
			faktor = 1073741824l;
		}
		else
		{
			faktor = 1099511627776l;
		}
		if (minus)
		{
			bytes *= -1;
		}
		double umgerechnet = (double) bytes / faktor;
		StringBuffer result = new StringBuffer(percentFormatter.format(umgerechnet));
		if (faktor == 1)
		{
			result.append(" Bytes");
		}
		else if (faktor == 1024l)
		{
			result.append(" kb");
		}
		else if (faktor == 1048576l)
		{
			result.append(" MB");
		}
		else if (faktor == 1073741824l)
		{
			result.append(" GB");
		}
		else
		{
			result.append(" TB");
		}
		return result;
	}
}
