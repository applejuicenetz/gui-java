/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.controller.xml.SearchDO.SearchEntryDO.FileNameDO;
import de.applejuicenet.client.facade.entity.FileName;
import de.applejuicenet.client.facade.entity.Search;
import de.applejuicenet.client.facade.entity.SearchEntry;
import de.applejuicenet.client.facade.shared.FileType;

import java.util.*;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/shared/SearchDO.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>
 * Title: AppleJuice Client-GUI
 * </p>
 * <p>
 * Description: Official GUI for the appleJuice core developed by muhviehstarr
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 *
 */
class SearchDO extends Search
{
   private int                        id;
   private String                     searchText;
   private int                        openSearches;
   private int                        foundFiles;
   private int                        searchedClients;
   private Map<String, SearchEntryDO> mapping         = new HashMap<String, SearchEntryDO>();
   private List<SearchEntry>          entries         = new ArrayList<SearchEntry>();
   private boolean                    changed         = false;
   private long                       creationTime;
   private boolean                    running;
   private Set<FileType>              filter          = new HashSet<FileType>();
   private boolean                    doFilter        = true;
   private List<SearchEntry>          filteredEntries = null;

   public SearchDO(int id)
   {
      this.id      = id;
      creationTime = System.currentTimeMillis();
   }

   SearchDO snapshot()
   {
      SearchDO copy = new SearchDO(id);
      copy.searchText = searchText;
      copy.openSearches = openSearches;
      copy.foundFiles = foundFiles;
      copy.searchedClients = searchedClients;
      copy.creationTime = creationTime;
      copy.running = running;
      copy.filter = new HashSet<>(filter);
      for(SearchEntry entry : entries)
      {
         SearchEntryDO original = (SearchEntryDO) entry;
         SearchEntryDO copiedEntry = copy.new SearchEntryDO(original.id, original.searchId,
                                                           original.checksum, original.size);
         for(FileNameDO name : original.fileNames)
         {
            copiedEntry.addFileName(copiedEntry.new FileNameDO(name.fileName, name.frequency));
         }
         copy.addSearchEntry(copiedEntry);
      }
      copy.changed = changed;
      return copy;
   }

   public long getCreationTime()
   {
      return creationTime;
   }

   public boolean isChanged()
   {
      return changed;
   }

   public synchronized void setChanged(boolean changed)
   {
      this.changed = changed;
   }

   public String getSearchText()
   {
      return searchText;
   }

   public void setSearchText(String searchText)
   {
      this.searchText = searchText;
   }

   public int getOpenSearches()
   {
      return openSearches;
   }

   public void setOpenSearches(int openSearches)
   {
      this.openSearches = openSearches;
   }

   public int getFoundFiles()
   {
      return foundFiles;
   }

   public void setFoundFiles(int foundFiles)
   {
      this.foundFiles = foundFiles;
   }

   public int getSearchedClients()
   {
      return searchedClients;
   }

   public void setSearchedClients(int searchedClients)
   {
      this.searchedClients = searchedClients;
   }

   public int getId()
   {
      return id;
   }

   public void addFilter(FileType newFilter)
   {
      filter.add(newFilter);
      if(filter.size() == FileType.values().length)
      {
         clearFilter();
      }

      doFilter = true;
   }

   public void removeFilter(FileType newFilter)
   {
      if(filter.size() == 0)
      {
         for(FileType curType : FileType.values())
         {
            filter.add(curType);
         }
      }

      filter.remove(newFilter);
      doFilter = true;
   }

   public void clearFilter()
   {
      filter.clear();
      doFilter = true;
   }

   public void addSearchEntry(SearchEntryDO searchEntry)
   {
      String key = Integer.toString(searchEntry.getId());

      if(!mapping.containsKey(key))
      {
         mapping.put(key, searchEntry);
         entries.add(searchEntry);
         setChanged(true);
         doFilter = true;
      }
      else
      {
         SearchEntryDO oldSearchEntry = mapping.get(key);

         for(FileName curFileName : searchEntry.getFileNames())
         {
            oldSearchEntry.addFileName((FileNameDO) curFileName);
         }
      }
   }

   public List<SearchEntry> getAllSearchEntries()
   {
      return entries;
   }

   public SearchEntry getSearchEntryById(int id)
   {
      for(SearchEntry curSearchEntry : entries)
      {
         if(curSearchEntry.getId() == id)
         {
            return (SearchEntry) curSearchEntry;
         }
      }

      return null;
   }

   public List<SearchEntry> getSearchEntries()
   {
      if(filter.size() == 0)
      {
         return entries;
      }

      if(doFilter || null == filteredEntries)
      {
         doFilter        = false;
         filteredEntries = new ArrayList<SearchEntry>();

         for(SearchEntry curSearchEntry : entries)
         {
            if(!filter.contains(curSearchEntry.getFileType()))
            {
               filteredEntries.add(curSearchEntry);
            }
         }
      }

      return filteredEntries;
   }

   public void setRunning(boolean running)
   {
      this.running = running;
   }

   public boolean isRunning()
   {
      return running;
   }

   public long getEntryCount()
   {
      return entries.size();
   }

   class SearchEntryDO implements de.applejuicenet.client.facade.entity.SearchEntry
   {
      private int              id;
      private int              searchId;
      private String           checksum;
      private long             size;
      private String           sizeAsString = null;
      private List<FileNameDO> fileNames       = new ArrayList<FileNameDO>();
      private Set<String>      keys            = new HashSet<String>();
      private FileType         type            = FileType.UNKNOWN;

      public SearchEntryDO(int id, int searchId, String checksum, long size)
      {
         this.id         = id;
         this.searchId   = searchId;
         this.checksum = checksum;
         this.size    = size;
      }

      public int getId()
      {
         return id;
      }

      public FileType getFileType()
      {
         return type;
      }

      public int getSearchId()
      {
         return searchId;
      }

      public String getChecksum()
      {
         return checksum;
      }

      public long getSize()
      {
         return size;
      }

      private void recalculatePossibleFileType()
      {
         FileName[] fileNames  = getFileNames();
         int        pdf        = 0;
         int        image      = 0;
         int        movie      = 0;
         int        iso        = 0;
         int        text       = 0;
         int        sound      = 0;
         int        archive    = 0;
         int        currentMax = 0;

         for(FileName curFileName : getFileNames())
         {
            FileType fileNameType = curFileName.getFileType();

            if(fileNameType == FileType.UNKNOWN)
            {
               continue;
            }
            else if(fileNameType == FileType.PDF)
            {
               pdf++;
               if(pdf > currentMax)
               {
                  currentMax = pdf;
                  type       = FileType.PDF;
               }
            }
            else if(fileNameType == FileType.IMAGE)
            {
               image++;
               if(image > currentMax)
               {
                  currentMax = image;
                  type       = FileType.IMAGE;
               }
            }
            else if(fileNameType == FileType.MOVIE)
            {
               movie++;
               if(movie > currentMax)
               {
                  currentMax = movie;
                  type       = FileType.MOVIE;
               }
            }
            else if(fileNameType == FileType.ISO)
            {
               iso++;
               if(iso > currentMax)
               {
                  currentMax = iso;
                  type       = FileType.ISO;
               }
            }
            else if(fileNameType == FileType.TEXT)
            {
               text++;
               if(text > currentMax)
               {
                  currentMax = text;
                  type       = FileType.TEXT;
               }
            }
            else if(fileNameType == FileType.SOUND)
            {
               sound++;
               if(sound > currentMax)
               {
                  currentMax = sound;
                  type       = FileType.SOUND;
               }
            }
            else if(fileNameType == FileType.ARCHIVE)
            {
               archive++;
               if(archive > currentMax)
               {
                  currentMax = archive;
                  type       = FileType.ARCHIVE;
               }
            }
         }

         if(pdf == image && movie == iso && image == movie && movie == text && text == sound && text == archive)
         {
            type = FileType.UNKNOWN;
         }
      }

      public void addFileName(FileNameDO fileName)
      {
         String key = fileName.getFileName();

         if(!keys.contains(key))
         {
            keys.add(key);
            fileNames.add(fileName);
            recalculatePossibleFileType();
            setChanged(true);
         }
         else
         {
            for(FileNameDO curFileName : fileNames)
            {
               if(curFileName.getFileName().compareToIgnoreCase(fileName.getFileName()) == 0)
               {
                  curFileName.setFrequency(fileName.getFrequency());
               }
            }
         }
      }

      public FileName[] getFileNames()
      {
         return (FileName[]) fileNames.toArray(new FileName[fileNames.size()]);
      }

      class FileNameDO implements FileName
      {
         private String   fileName;
         private int      frequency;
         private FileType fileType = FileType.UNKNOWN;

         public FileNameDO(String fileName, int frequency)
         {
            this.fileName   = fileName;
            this.frequency = frequency;
            fileType         = FileType.calculatePossibleFileType(fileName);
         }

         public String getFileName()
         {
            return fileName;
         }

         public int getFrequency()
         {
            return frequency;
         }

         public void setFrequency(int frequency)
         {
            this.frequency = frequency;
         }

         public FileType getFileType()
         {
            return fileType;
         }
      }
   }
}
