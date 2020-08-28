/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.shared;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReleaseInfo
{
	private final String	host;
	private final String	md5;
	private String			title				= null;
	private Date			releaseDate			= null;
	private String			category			= null;
	private List<String>	genres				= new ArrayList<String>();
	private String			language			= null;
	private URL				languageImage		= null;
	private String			format				= null;
	private Long			viewsTotal			= null;
	private Long			viewsCurrentMonth	= null;
	private boolean			fsk18				= false;
	private URL				imageURL			= null;
	private URL				descriptionURL		= null;
	private String			quality;

	public ReleaseInfo(String host, String md5)
	{
		this.host = host;
		this.md5 = md5;
	}

	public String getMd5()
	{
		return md5;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public Date getReleaseDate()
	{
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate)
	{
		this.releaseDate = releaseDate;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public List<String> getGenres()
	{
		return genres;
	}

	public void setGenres(List<String> genres)
	{
		this.genres = genres;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public URL getLanguageImage()
	{
		return languageImage;
	}

	public void setLanguageImage(String languageImage)
	{
		try
		{
			this.languageImage = new URL(host + languageImage);
		}
		catch (MalformedURLException e)
		{
		}
	}

	public String getFormat()
	{
		return format;
	}

	public void setFormat(String format)
	{
		this.format = format;
	}

	public Long getViewsTotal()
	{
		return viewsTotal;
	}

	public void setViewsTotal(Long viewsTotal)
	{
		this.viewsTotal = viewsTotal;
	}

	public Long getViewsCurrentMonth()
	{
		return viewsCurrentMonth;
	}

	public void setViewsCurrentMonth(Long viewsCurrentMonth)
	{
		this.viewsCurrentMonth = viewsCurrentMonth;
	}

	public URL getImageURL()
	{
		return imageURL;
	}

	public void setImage(String imageLink)
	{
		try
		{
			imageURL = new URL(host + imageLink);
		}
		catch (MalformedURLException e)
		{
		}
	}

	public URL getDescriptionURL()
	{
		return descriptionURL;
	}

	public void setDescription(String descriptionLink)
	{
		try
		{
			descriptionURL = new URL(host + descriptionLink);
		}
		catch (MalformedURLException e)
		{
		}
	}

	public boolean isFsk18()
	{
		return fsk18;
	}

	public void setFsk18(boolean fsk18)
	{
		this.fsk18 = fsk18;
	}

	public String getQuality()
	{
		return quality;
	}

	public void setQuality(String quality)
	{
		this.quality = quality;
	}

}
