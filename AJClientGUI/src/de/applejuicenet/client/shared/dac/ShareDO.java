package de.applejuicenet.client.shared.dac;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/ShareDO.java,v 1.9 2004/02/18 18:43:04 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ShareDO.java,v $
 * Revision 1.9  2004/02/18 18:43:04  maj0r
 * Von DOM auf SAX umgebaut.
 *
 * Revision 1.8  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.7  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.6  2003/09/01 15:50:51  maj0r
 * Wo es moeglich war, DOs auf primitive Datentypen umgebaut.
 *
 * Revision 1.5  2003/08/04 14:28:55  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.4  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class ShareDO {
    private final int id;
    private String filename;
    private String shortfilename;
    private long size;
    private String checksum;
    private int prioritaet;

    public ShareDO(int id) {
        this.id = id;
    }

    public ShareDO(int id, String filename, String shortfilename, long size,
                   String checksum, int prioritaet) {
        this.id = id;
        this.filename = filename;
        this.shortfilename = shortfilename;
        this.size = size;
        this.checksum = checksum;
        this.prioritaet = prioritaet;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public int getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public long getSize() {
        return size;
    }

    public String getCheckSum() {
        return checksum;
    }

    public String getShortfilename() {
        return shortfilename;
    }

    public void setShortfilename(String shortfilename) {
        this.shortfilename = shortfilename;
    }

    public int getPrioritaet() {
        return prioritaet;
    }

    public void setPrioritaet(int prioritaet) {
        this.prioritaet = prioritaet;
    }
}
