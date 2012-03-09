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
	private List<String>	languages			= new ArrayList<String>();
	private List<URL>		languageImages		= new ArrayList<URL>();
	private String			format				= null;
	private Long			viewsTotal			= null;
	private Long			viewsCurrentMonth	= null;
	private Long			clicksTotal			= null;
	private Long			clicksCurrentMonth	= null;
	private String			fsk					= null;
	private Long			ratingVideoOutOf10	= null;
	private Long			ratingAudioOutOf10	= null;
	private URL				imageURL			= null;
	private URL				descriptionURL		= null;
	private String			trailer				= null;
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

	public List<String> getLanguages()
	{
		return languages;
	}

	public void setLanguages(List<String> languages)
	{
		this.languages = languages;
	}

	public List<URL> getLanguageImages()
	{
		return languageImages;
	}

	public void setLanguageImages(List<URL> languageImages)
	{
		this.languageImages = languageImages;
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

	public Long getClicksTotal()
	{
		return clicksTotal;
	}

	public void setClicksTotal(Long clicksTotal)
	{
		this.clicksTotal = clicksTotal;
	}

	public Long getClicksCurrentMonth()
	{
		return clicksCurrentMonth;
	}

	public void setClicksCurrentMonth(Long clicksCurrentMonth)
	{
		this.clicksCurrentMonth = clicksCurrentMonth;
	}

	public Long getRatingVideoOutOf10()
	{
		return ratingVideoOutOf10;
	}

	public void setRatingVideoOutOf10(Long ratingVideoOutOf10)
	{
		this.ratingVideoOutOf10 = ratingVideoOutOf10;
	}

	public Long getRatingAudioOutOf10()
	{
		return ratingAudioOutOf10;
	}

	public void setRatingAudioOutOf10(Long ratingAudioOutOf10)
	{
		this.ratingAudioOutOf10 = ratingAudioOutOf10;
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
			System.out.println(e.getMessage());
			e.printStackTrace();
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

	public String getFsk()
	{
		return fsk;
	}

	public void setFsk(String fsk)
	{
		this.fsk = fsk;
	}

	public void setTrailer(String trailer)
	{
		this.trailer = trailer;
	}

	public String getTrailer()
	{
		return trailer;
	}

	public void setQuality(String quality)
	{
		this.quality = quality;
	}

	public String getQuality()
	{
		return quality;
	}
}
