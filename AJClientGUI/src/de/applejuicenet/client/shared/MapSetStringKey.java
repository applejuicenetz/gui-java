package de.applejuicenet.client.shared;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/MapSetStringKey.java,v 1.7 2003/12/29 16:04:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f?r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: MapSetStringKey.java,v $
 * Revision 1.7  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.6  2003/11/03 20:41:18  maj0r
 * Version 1.4 wieder hergestellt.
 *
 * Revision 1.5  2003/11/03 14:46:39  maj0r
 * Speicheroptimiert.
 *
 * Revision 1.4  2003/09/01 15:50:51  maj0r
 * Wo es moeglich war, DOs auf primitive Datentypen umgebaut.
 *
 * Revision 1.3  2003/08/25 07:22:47  maj0r
 * Gross-/Kleinschreibung ignorieren.
 *
 * Revision 1.2  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett ueberarbeitet.
 *
 * Revision 1.1  2003/07/01 14:50:45  maj0r
 * Inner-Class Key ausgelagert und umbenannt.
 *
 *
 */

public class MapSetStringKey {
    private String value;
    private int hashCode = -1;

    public MapSetStringKey(String value){
        this.value = value;
    }

    public MapSetStringKey(int value){
        this.value = Integer.toString(value);
    }

    public String getValue(){
      return value;
    }

    public boolean equals(Object object){
        if (object == null || !(object instanceof MapSetStringKey))
            return false;
        return value.compareToIgnoreCase(((MapSetStringKey)object).getValue())==0;
    }

    public int hashCode() {
      if (hashCode == -1) {
        char[] ca = value.toLowerCase().toCharArray();
        int size = ca.length;
        for (int x = 0; x < size; x++) {
          hashCode += ca[x];
        }
      }
      return (hashCode);
    }
}
