package de.applejuicenet.client.shared;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/MapSetStringKey.java,v 1.1 2003/07/01 14:50:45 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f?r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: MapSetStringKey.java,v $
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
        char[] ca = value.toCharArray();
        int size = ca.length;
        for (int x = 0; x < size; x++) {
          hashCode += ca[x];
        }
      }
      return (hashCode);
    }
}
