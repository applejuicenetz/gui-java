package de.applejuicenet.client.shared.dac;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/DirectoryDO.java,v 1.3 2003/12/29 16:04:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DirectoryDO.java,v $
 * Revision 1.3  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.2  2003/08/17 16:13:11  maj0r
 * Erstellen des DirectoryNode-Baumes korrigiert.
 *
 * Revision 1.1  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 *
 */

public class DirectoryDO {
    public static final int TYPE_RECHNER = 1;
    public static final int TYPE_LAUFWERK = 2;
    public static final int TYPE_DISKETTE = 3;
    public static final int TYPE_ORDNER = 4;
    public static final int TYPE_DESKTOP = 5;

    private static String separator;

    private String name;
    private int type;
    private boolean fileSystem;
    private String path;

    public DirectoryDO(String name, int type, boolean fileSystem, String path) {
        this.name = name;
        this.type = type;
        this.fileSystem = fileSystem;
        this.path = path;
    }

    public static String getSeparator() {
        return separator;
    }

    public static void setSeparator(String separator) {
        DirectoryDO.separator = separator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(boolean fileSystem) {
        this.fileSystem = fileSystem;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
