package de.applejuicenet.client.fassade.controller.json;

import java.util.Date;
import java.util.List;

public class ReleaseinfoVO
{
	private boolean			fsk;
	private Date			created;
	private String			qualiInfo;
	private long			hits;
	private String			language;
	private String			link;
	private String			title;
	private Date			updated;
	private String			format;
	private String			category_link;
	private String			img;
	private String			languageImg;
	private long			hitsInMonth;
	private String			category;
	private List<String>	genre;

	public boolean isFsk()
	{
		return fsk;
	}

	public void setFsk(boolean fsk)
	{
		this.fsk = fsk;
	}

	public Date getCreated()
	{
		return created;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public String getQualiInfo()
	{
		return qualiInfo;
	}

	public void setQualiInfo(String qualiInfo)
	{
		this.qualiInfo = qualiInfo;
	}

	public long getHits()
	{
		return hits;
	}

	public void setHits(long hits)
	{
		this.hits = hits;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public String getLink()
	{
		return link;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public Date getUpdated()
	{
		return updated;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}

	public String getFormat()
	{
		return format;
	}

	public void setFormat(String format)
	{
		this.format = format;
	}

	public String getCategory_link()
	{
		return category_link;
	}

	public void setCategory_link(String category_link)
	{
		this.category_link = category_link;
	}

	public String getLanguageImg()
	{
		return languageImg;
	}

	public void setLanguageImg(String languageImg)
	{
		this.languageImg = languageImg;
	}

	public long getHitsInMonth()
	{
		return hitsInMonth;
	}

	public void setHitsInMonth(long hitsInMonth)
	{
		this.hitsInMonth = hitsInMonth;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public String getImg()
	{
		return img;
	}

	public void setImg(String img)
	{
		this.img = img;
	}

	public List<String> getGenre()
	{
		return genre;
	}

	public void setGenre(List<String> genre)
	{
		this.genre = genre;
	}
}
