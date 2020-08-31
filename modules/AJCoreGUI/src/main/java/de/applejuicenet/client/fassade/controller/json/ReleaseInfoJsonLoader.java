/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.controller.json;

import java.io.*;
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
	private static final String  defaultHost = "https://xrel.knastbruder.applejuicenet.de";
	private static final Integer defaultPort = 443;
	private static final String defaultPath = "/api/ajfsp/%s_%s.json";

	private static final Map<String, ReleaseCacheObject>	releasesCache	= new HashMap<>();
	private static final long								CACHE_TIMEOUT	= 300000;
	private final ReleaseInfo								releaseInfo;
	private final ProxySettings								proxy;
	private final String									hash;
	private final Long									    size;
	private static String									releaseInfoHost	= null;
	private static String									releaseInfoPath	= null;
	private static Integer									releaseInfoPort;

	public ReleaseInfoJsonLoader(String hash, Long size, ProxySettings proxy)
	{
		if (null == releaseInfoHost)
		{
			Properties props = new Properties();
            String path = System.getProperty("user.home") + File.separator + "appleJuice" + File.separator + "gui" + File.separator + "xrel.properties";

            File aFile = new File(path);
			if (aFile.exists())
			{
				try
				{
					FileInputStream fiS = new FileInputStream(aFile);
					props.load(fiS);
					fiS.close();

					releaseInfoHost = props.getProperty("host", defaultHost);
					releaseInfoPath = props.getProperty("path", defaultPath);
					releaseInfoPort = Integer.parseInt(props.getProperty("port", String.valueOf(defaultPort)));
				}
				catch (Exception e)
				{
					// nix zu tun
				}
			}else{
				props.setProperty("host", defaultHost);
				props.setProperty("port", String.valueOf(defaultPort));
				props.setProperty("path", defaultPath);
				try(OutputStream outputStream = new FileOutputStream(aFile)){
					props.store(outputStream, null);
				} catch (IOException e) {
					// nix zu tun
				}
			}

			releaseInfoHost = (releaseInfoHost == null) ? defaultHost : releaseInfoHost;
			releaseInfoPath = (releaseInfoPath == null) ? defaultPath : releaseInfoPath;
			releaseInfoPort = (releaseInfoPort == null) ? defaultPort : releaseInfoPort;
		}

		this.hash = hash;
		this.size = size;
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

		String releaseDataTmp = WebsiteContentLoader.getWebsiteContent(proxy, releaseInfoHost, releaseInfoPort, String.format(releaseInfoPath, hash, size));

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
