package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.shared.dac.PartListDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/PartListHolder.java,v 1.1 2003/10/04 15:29:12 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: PartListHolder.java,v $
 * Revision 1.1  2003/10/04 15:29:12  maj0r
 * Userpartliste hinzugefuegt.
 *
 *
 */

public interface PartListHolder {
    public PartListDO getPartList();
}
