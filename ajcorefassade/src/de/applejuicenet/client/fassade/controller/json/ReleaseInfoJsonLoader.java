/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.controller.json;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.applejuicenet.client.fassade.shared.ProxySettings;
import de.applejuicenet.client.fassade.shared.ReleaseInfo;
import de.applejuicenet.client.fassade.shared.WebsiteContentLoader;

public class ReleaseInfoJsonLoader
{
	private static final Map<String, ReleaseCacheObject>	releasesCache	= new HashMap<String, ReleaseCacheObject>();
	private static final long								CACHE_TIMEOUT	= 300000;
	private final ReleaseInfo								releaseInfo;
	private final ProxySettings								proxy;
	private final String									hash;
	private static String									releaseInfoHost	= null;
	private static Integer									releaseInfoPort;

	public ReleaseInfoJsonLoader(String hash, ProxySettings proxy)
	{
		if (null == releaseInfoHost)
		{
			Properties props = new Properties();
			File aFile = new File(System.getProperty("user.dir") + File.separator + "ajcore.properties");
			if (aFile.exists())
			{
				try
				{
					FileInputStream fiS = new FileInputStream(aFile);
					props.load(fiS);
					fiS.close();
					releaseInfoHost = props.getProperty("releaseinfo.host");
					String tmp = props.getProperty("releaseinfo.port", "80");
					releaseInfoPort = Integer.parseInt(tmp);
				}
				catch (Exception e)
				{
					// nix zu tun
				}
			}
			if (null == releaseInfoHost)
			{
				releaseInfoHost = "http://applefiles.cc";
			}
			if (null == releaseInfoPort)
			{
				releaseInfoPort = 80;
			}
		}
		this.hash = hash;
		releaseInfo = new ReleaseInfo(releaseInfoHost, hash);
		this.proxy = proxy;
	}

	public ReleaseInfo getReleaseInfo() throws Exception
	{
		ReleaseCacheObject releaseCacheObject = releasesCache.get(hash);

		if (null != releaseCacheObject)
		{
			if (releaseCacheObject.getTimestamp() + CACHE_TIMEOUT > System.currentTimeMillis())
			{
				return releaseCacheObject.getReleaseInfo();
			}
			else
			{
				releasesCache.remove(hash);
			}
		}

		String releaseDataTmp =
				WebsiteContentLoader.getWebsiteContent(proxy, releaseInfoHost, releaseInfoPort, "/api/get_link_info/?hash=" + hash);

		if ("no result".equals(releaseDataTmp))
		{
			releaseCacheObject = new ReleaseCacheObject(System.currentTimeMillis(), null);
			releasesCache.put(hash, releaseCacheObject);
			return null;
		}

		GsonBuilder gsonb = new GsonBuilder();
		gsonb.registerTypeAdapter(Date.class, new DateDeserializer());
		Gson gson = gsonb.create();

		ReleaseinfoVO data = gson.fromJson(releaseDataTmp, ReleaseinfoVO.class);

		releaseInfo.setTitle(data.getTitle());
		releaseInfo.setCategory(data.getCategory());
		releaseInfo.setViewsCurrentMonth(data.getHitsInMonth());
		releaseInfo.setViewsTotal(data.getHits());
		releaseInfo.setFormat(data.getFormat());
		releaseInfo.setDescription(data.getLink());
		releaseInfo.setFsk18(data.isFsk());
		releaseInfo.setGenres(data.getGenre());

		releaseInfo.setImage(data.getImg());

		if (data.getLanguage() != null)
		{
			releaseInfo.setLanguage(data.getLanguage());
			releaseInfo.setLanguageImage(data.getLanguageImg());
		}
		releaseInfo.setQuality(data.getQualiInfo());
		releaseInfo.setReleaseDate(data.getCreated());

		releaseCacheObject = new ReleaseCacheObject(System.currentTimeMillis(), releaseInfo);
		releasesCache.put(hash, releaseCacheObject);

		return releaseInfo;

	}

	private class ReleaseCacheObject
	{
		private long		timestamp;
		private ReleaseInfo	releaseInfo;

		public ReleaseCacheObject(long timestamp, ReleaseInfo releaseInfo)
		{
			super();
			this.timestamp = timestamp;
			this.releaseInfo = releaseInfo;
		}

		public ReleaseInfo getReleaseInfo()
		{
			return releaseInfo;
		}

		public long getTimestamp()
		{
			return timestamp;
		}
	}
}
