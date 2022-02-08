package de.applejuicenet.client.fassade.entity;

import de.applejuicenet.client.fassade.shared.FileType;

import java.util.List;

public abstract class Search implements IdOwner
{
   public static int currentSearchCount = 0;

   public abstract long getCreationTime();

   public abstract boolean isChanged();

   public abstract String getSuchText();

   public abstract int getOffeneSuchen();

   public abstract int getGefundenDateien();

   public abstract int getDurchsuchteClients();

   public abstract int getId();

   public abstract void addFilter(FileType newFilter);

   public abstract void removeFilter(FileType newFilter);

   public abstract void clearFilter();

   public abstract List<SearchEntry> getAllSearchEntries();

   public abstract SearchEntry getSearchEntryById(int id);

   public abstract List<SearchEntry> getSearchEntries();

   public abstract boolean isRunning();

   public abstract void setChanged(boolean changed);

   public abstract long getEntryCount();
}
