package de.applejuicenet.client.fassade.controller.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.applejuicenet.client.fassade.controller.xml.SearchDO.SearchEntryDO.FileNameDO;
import de.applejuicenet.client.fassade.entity.FileName;
import de.applejuicenet.client.fassade.entity.Search;
import de.applejuicenet.client.fassade.entity.SearchEntry;
import de.applejuicenet.client.fassade.shared.FileTypeHelper;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/shared/SearchDO.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
 * 
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 * 
 * @author: Maj0r [aj@tkl-soft.de]
 * 
 */

class SearchDO extends Search {
	private int id;
	private String suchText;
	private int offeneSuchen;
	private int gefundenDateien;
	private int durchsuchteClients;
	private Map<String, SearchEntryDO> mapping = new HashMap<String, SearchEntryDO>();
	private List<SearchEntryDO> entries = new ArrayList<SearchEntryDO>();
	private boolean changed = true;
	private long creationTime;
	private boolean running;
	private Set<String> filter = new HashSet<String>();

	public SearchDO(int id) {
		this.id = id;
		creationTime = System.currentTimeMillis();
	}

	public long getCreationTime() {
		return creationTime;
	}

	public boolean isChanged() {
		return changed;
	}

	public synchronized void setChanged(boolean changed) {
		this.changed = changed;
	}

	public String getSuchText() {
		return suchText;
	}

	public void setSuchText(String suchText) {
		this.suchText = suchText;
	}

	public int getOffeneSuchen() {
		return offeneSuchen;
	}

	public void setOffeneSuchen(int offeneSuchen) {
		this.offeneSuchen = offeneSuchen;
	}

	public int getGefundenDateien() {
		return gefundenDateien;
	}

	public void setGefundenDateien(int gefundenDateien) {
		this.gefundenDateien = gefundenDateien;
	}

	public int getDurchsuchteClients() {
		return durchsuchteClients;
	}

	public void setDurchsuchteClients(int durchsuchteClients) {
		this.durchsuchteClients = durchsuchteClients;
	}

	public int getId() {
		return id;
	}

	public void addFilter(String newFilter) {
		filter.add(newFilter);
		if (filter.size() == FileTypeHelper.getAllTypes().length) {
			clearFilter();
		}
	}

	public void removeFilter(String newFilter) {
		if (filter.size() == 0) {
			for (String curType : FileTypeHelper.getAllTypes()) {
				filter.add(curType);
			}
		}
		filter.remove(newFilter);
	}

	public void clearFilter() {
		filter.clear();
	}

	public void addSearchEntry(SearchEntryDO searchEntry) {
		String key = Integer.toString(searchEntry.getId());
		if (!mapping.containsKey(key)) {
			mapping.put(key, searchEntry);
			entries.add(searchEntry);
			setChanged(true);
		} else {
			SearchEntryDO oldSearchEntry = mapping.get(key);
			for (FileName curFileName : searchEntry.getFileNames()) {
				oldSearchEntry.addFileName((FileNameDO)curFileName);
			}
		}
	}

	public SearchEntry[] getAllSearchEntries() {
		return (SearchEntry[]) entries.toArray(new SearchEntry[entries.size()]);
	}

	public SearchEntry getSearchEntryById(int id) {
		for (SearchEntryDO curSearchEntry : entries) {
			if (curSearchEntry.getId() == id) {
				return (SearchEntry)curSearchEntry;
			}
		}
		return null;
	}

	public SearchEntry[] getSearchEntries() {
		if (filter.size() == 0) {
			return (SearchEntry[]) entries.toArray(new SearchEntry[entries
					.size()]);
		}
		ArrayList<SearchEntryDO> neededEntries = new ArrayList<SearchEntryDO>();
		for (SearchEntryDO curSearchEntry : entries) {
			if (!filter.contains(curSearchEntry.getFileType())) {
				neededEntries.add(curSearchEntry);
			}
		}
		return (SearchEntry[]) neededEntries
				.toArray(new SearchEntry[neededEntries.size()]);
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isRunning() {
		return running;
	}

	class SearchEntryDO implements SearchEntry{
		private int id;
		private int searchId;
		private String checksumme;
		private long groesse;
		private String groesseAsString = null;
		private List<FileNameDO> fileNames = new ArrayList<FileNameDO>();
		private Set<String> keys = new HashSet<String>();
		private String type = FileTypeHelper.TYPE_UNKNOWN;

		public SearchEntryDO(int id, int searchId, String checksumme, long groesse) {
			this.id = id;
			this.searchId = searchId;
			this.checksumme = checksumme;
			this.groesse = groesse;
		}

		public int getId() {
			return id;
		}

		public String getFileType() {
			return type;
		}

		public int getSearchId() {
			return searchId;
		}

		public String getChecksumme() {
			return checksumme;
		}

		public long getGroesse() {
			return groesse;
		}

		private void recalculatePossibleFileType() {
			FileName[] fileNames = getFileNames();
			int pdf = 0;
			int image = 0;
			int movie = 0;
			int iso = 0;
			int text = 0;
			int sound = 0;
			int archive = 0;
			int currentMax = 0;
			for (FileName curFileName : getFileNames()) {
				String fileNameType = curFileName.getFileType();
				if (fileNameType.equals(FileTypeHelper.TYPE_UNKNOWN)) {
					continue;
				} else if (fileNameType.equals(FileTypeHelper.TYPE_PDF)) {
					pdf++;
					if (pdf > currentMax) {
						currentMax = pdf;
						type = FileTypeHelper.TYPE_PDF;
					}
				} else if (fileNameType.equals(FileTypeHelper.TYPE_IMAGE)) {
					image++;
					if (image > currentMax) {
						currentMax = image;
						type = FileTypeHelper.TYPE_IMAGE;
					}
				} else if (fileNameType.equals(FileTypeHelper.TYPE_MOVIE)) {
					movie++;
					if (movie > currentMax) {
						currentMax = movie;
						type = FileTypeHelper.TYPE_MOVIE;
					}
				} else if (fileNameType.equals(FileTypeHelper.TYPE_ISO)) {
					iso++;
					if (iso > currentMax) {
						currentMax = iso;
						type = FileTypeHelper.TYPE_ISO;
					}
				} else if (fileNameType.equals(FileTypeHelper.TYPE_TEXT)) {
					text++;
					if (text > currentMax) {
						currentMax = text;
						type = FileTypeHelper.TYPE_TEXT;
					}
				} else if (fileNameType.equals(FileTypeHelper.TYPE_SOUND)) {
					sound++;
					if (sound > currentMax) {
						currentMax = sound;
						type = FileTypeHelper.TYPE_SOUND;
					}
				} else if (fileNameType.equals(FileTypeHelper.TYPE_ARCHIVE)) {
					archive++;
					if (archive > currentMax) {
						currentMax = archive;
						type = FileTypeHelper.TYPE_ARCHIVE;
					}
				}
			}
			if (pdf == image && movie == iso && image == movie && movie == text
					&& text == sound && text == archive) {
				type = FileTypeHelper.TYPE_UNKNOWN;
			}
		}

		public void addFileName(FileNameDO fileName) {
			String key = fileName.getDateiName();
			if (!keys.contains(key)) {
				keys.add(key);
				fileNames.add(fileName);
				recalculatePossibleFileType();
				setChanged(true);
			} else {
				for (FileNameDO curFileName : fileNames) {
					if (curFileName.getDateiName().compareToIgnoreCase(
							fileName.getDateiName()) == 0) {
						curFileName.setHaeufigkeit(fileName.getHaeufigkeit());
					}
				}
			}
		}

		public FileName[] getFileNames() {
			return (FileName[]) fileNames
					.toArray(new FileName[fileNames.size()]);
		}

		class FileNameDO implements FileName{
			private String dateiName;
			private int haeufigkeit;
			private String fileType = FileTypeHelper.TYPE_UNKNOWN;

			public FileNameDO(String dateiName, int haeufigkeit) {
				this.dateiName = dateiName;
				this.haeufigkeit = haeufigkeit;
				fileType = FileTypeHelper.calculatePossibleFileType(dateiName);
			}

			public String getDateiName() {
				return dateiName;
			}

			public int getHaeufigkeit() {
				return haeufigkeit;
			}

			public void setHaeufigkeit(int haeufigkeit) {
				this.haeufigkeit = haeufigkeit;
			}

			public String getFileType() {
				return fileType;
			}
		}
	}
}
