package de.applejuicenet.client.facade.entity;

import java.io.Serializable;
import java.text.DecimalFormat;

@SuppressWarnings("serial")
public abstract class Information implements Serializable
{

	public static final int				CONNECTED				= 0;
	public static final int				NOT_CONNECTED			= 1;
	public static final int				TRYING_TO_CONNECT	= 2;

	private static final DecimalFormat	percentFormatter		= new DecimalFormat("###,##0.00");

	public abstract long getSessionUpload();

	public abstract long getSessionDownload();

	public abstract long getCredits();

	public abstract long getUploadSpeed();

	public abstract long getDownloadSpeed();

	public abstract long getOpenConnections();

	public abstract long getMaxUploadPositions();

	public abstract int getConnectionStatus();

	public abstract String getServerName();

	public abstract String getExternalIP();

	public abstract Server getServer();

	public final String getCreditsAsString()
	{
		StringBuilder result = new StringBuilder(" Credits: ");
		result.append(convertBytes(getCredits()));
		return result.toString();
	}

	public final String getUpDownSessionAsString()
	{
		StringBuilder result = new StringBuilder(" in: ");
		result.append(convertBytes(getSessionDownload()));
		result.append(" out: ");
		result.append(convertBytes(getSessionUpload()));
		return result.toString();
	}

	public final String getUpDownAsString()
	{
		StringBuilder result = new StringBuilder(" in: ");
		result.append(getBytesSpeed(getDownloadSpeed()));
		result.append(" out: ");
		result.append(getBytesSpeed(getUploadSpeed()));
		return result.toString();
	}

	public final StringBuilder getSessionDownAsString()
	{
		return convertBytes(getSessionDownload());
	}

	public final StringBuilder getSessionUpAsString()
	{
		return convertBytes(getSessionUpload());
	}

	public final StringBuilder getUpAsString()
	{
		return getBytesSpeed(getUploadSpeed());
	}

	public final StringBuilder getDownAsString()
	{
		return getBytesSpeed(getDownloadSpeed());
	}

	private final StringBuilder getBytesSpeed(long bytes)
	{
		if (bytes == 0)
		{
			return new StringBuilder("0 KB/s");
		}
		StringBuilder result = convertBytes(bytes);
		result.append("/s");
		return result;
	}

	private final StringBuilder convertBytes(long bytes)
	{
		boolean minus = false;
		if (bytes < 0)
		{
			minus = true;
			bytes *= -1;
		}
		if (bytes == 0)
		{
			return new StringBuilder("0 MB");
		}
		long factor = 1;
		if (bytes < 1024l)
		{
			factor = 1;
		}
		else if (bytes / 1024l < 1024l)
		{
			factor = 1024l;
		}
		else if (bytes / 1048576l < 1024l)
		{
			factor = 1048576l;
		}
		else if (bytes / 1073741824l < 1024l)
		{
			factor = 1073741824l;
		}
		else
		{
			factor = 1099511627776l;
		}
		if (minus)
		{
			bytes *= -1;
		}
		double converted = (double) bytes / factor;
		StringBuilder result = new StringBuilder(percentFormatter.format(converted));
		if (factor == 1)
		{
			result.append(" Bytes");
		}
		else if (factor == 1024l)
		{
			result.append(" kb");
		}
		else if (factor == 1048576l)
		{
			result.append(" MB");
		}
		else if (factor == 1073741824l)
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
