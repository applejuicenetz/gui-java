package de.applejuicenet.client.fassade.entity;

public abstract class Search {
	public static int currentSearchCount = 0;

	public abstract long getCreationTime();

	public abstract boolean isChanged();

	public abstract String getSuchText();

	public abstract int getOffeneSuchen();

	public abstract int getGefundenDateien();

	public abstract int getDurchsuchteClients();

	public abstract int getId();

	public abstract void addFilter(String newFilter);

	public abstract void removeFilter(String newFilter);

	public abstract void clearFilter();

	public abstract SearchEntry[] getAllSearchEntries();

	public abstract SearchEntry getSearchEntryById(int id);

	public abstract SearchEntry[] getSearchEntries();
	
	public abstract boolean isRunning();	
	
	public abstract void setChanged(boolean changed);
    
    public abstract long getEntryCount();
}
