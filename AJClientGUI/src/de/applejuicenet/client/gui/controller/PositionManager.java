package de.applejuicenet.client.gui.controller;

import java.awt.Dimension;
import java.awt.Point;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/PositionManager.java,v 1.7 2004/02/05 23:11:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: PositionManager.java,v $
 * Revision 1.7  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.6  2004/01/20 12:45:13  maj0r
 * Spaltenindizes werden jetzt gespeichert.
 *
 * Revision 1.5  2004/01/12 07:26:10  maj0r
 * Tabellenspalte nun ueber Headerkontextmenue ein/ausblendbar.
 *
 * Revision 1.4  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.3  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
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

    public int[] getShareWidths();

    public void setShareWidths(int[] shareWidths);

    public void setDownloadColumnVisible(int column, boolean visible);

    public boolean[] getDownloadColumnVisibilities();

    public void setUploadColumnVisible(int column, boolean visible);

    public boolean[] getUploadColumnVisibilities();

    public void setDownloadColumnIndex(int column, int index);

    public int[] getDownloadColumnIndizes();

    public void setUploadColumnIndex(int column, int index);

    public int[] getUploadColumnIndizes();
}