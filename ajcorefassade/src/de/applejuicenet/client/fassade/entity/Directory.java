package de.applejuicenet.client.fassade.entity;

public abstract class Directory {

	public static final int TYPE_RECHNER = 1;
	public static final int TYPE_LAUFWERK = 2;
	public static final int TYPE_DISKETTE = 3;
	public static final int TYPE_ORDNER = 4;
	public static final int TYPE_DESKTOP = 5;
	
	private static String separator;

	public static String getSeparator() {
		return separator;
	}

	public static void setSeparator(String separator) {
		Directory.separator = separator;
	}

	public abstract String getName();

	public abstract int getType();

	public abstract boolean isFileSystem();

	public abstract String getPath();

	public abstract Directory[] getChildren();	
}
