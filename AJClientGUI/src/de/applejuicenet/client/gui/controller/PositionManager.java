package de.applejuicenet.client.gui.controller;

import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/PositionManager.java,v 1.2 2003/09/09 12:28:15 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: PositionManager.java,v $
 * Revision 1.2  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 *
 */

public interface PositionManager {

    public void save();

    public void setMainXY(Point p);

    public Point getMainXY();

    public void setMainDimension(Dimension dimension);

    public Dimension getMainDimension();

    public void setDownloadWidths(int[] widths);

    public boolean isLegal();

    public int[] getDownloadWidths();

    public int[] getUploadWidths();

    public void setUploadWidths(int[] uploadWidths);

    public int[] getServerWidths();

    public void setServerWidths(int[] serverWidths);

    public int[] getSearchWidths();

    public void setSearchWidths(int[] searchWidths);

    public int[] getShareWidths();

    public void setShareWidths(int[] shareWidths);
}