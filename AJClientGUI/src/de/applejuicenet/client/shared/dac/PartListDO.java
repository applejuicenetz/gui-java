package de.applejuicenet.client.shared.dac;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/PartListDO.java,v 1.3 2003/09/01 06:27:35 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: PartListDO.java,v $
 * Revision 1.3  2003/09/01 06:27:35  maj0r
 * Ueberarbeitet.
 *
 * Revision 1.2  2003/08/11 19:41:09  maj0r
 * HashSet ggn ArrayList getauscht, um die Reihenfolge der Parts zu behalten.
 *
 * Revision 1.1  2003/08/11 16:52:22  maj0r
 * DownloadPartList eingefügt.
 *
 *
 */

import java.awt.*;
import java.util.HashSet;
import java.util.ArrayList;

public class PartListDO {
    public static final Color COLOR_TYPE_OK = Color.BLACK;
    public static final Color COLOR_TYPE_0 = Color.RED;
    public static final Color COLOR_TYPE_1 = new Color(25, 25, 250);
    public static final Color COLOR_TYPE_2 = new Color(50, 50, 250);
    public static final Color COLOR_TYPE_3 = new Color(75, 75, 250);
    public static final Color COLOR_TYPE_4 = new Color(100, 100, 250);
    public static final Color COLOR_TYPE_5 = new Color(125, 125, 250);
    public static final Color COLOR_TYPE_6 = new Color(150, 150, 250);
    public static final Color COLOR_TYPE_7 = new Color(175, 175, 250);
    public static final Color COLOR_TYPE_8 = new Color(200, 200, 250);
    public static final Color COLOR_TYPE_9 = new Color(225, 225, 250);
    public static final Color COLOR_TYPE_10 = Color.BLUE;

    private DownloadDO downloadDO;
    private long groesse;
    private ArrayList parts = new ArrayList();

    public PartListDO(DownloadDO downloadDO){
        this.downloadDO = downloadDO;
    }

    public long getGroesse() {
        return groesse;
    }

    public void addPart(Part aPart){
        parts.add(aPart);
    }

    public Part[] getParts(){
        return (Part[])parts.toArray(new Part[parts.size()]);
    }

    public void setGroesse(long groesse) {
        this.groesse = groesse;
    }

    public DownloadDO getDownloadDO() {
        return downloadDO;
    }

    public class Part{
        private Long fromPosition;
        private int type;

        public Part(Long fromPosition, int type) {
            this.fromPosition = fromPosition;
            this.type = type;
        }

        public Long getFromPosition() {
            return fromPosition;
        }

        public int getType() {
            return type;
        }
   }
}
