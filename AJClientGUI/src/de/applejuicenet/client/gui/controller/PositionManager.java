package de.applejuicenet.client.gui.controller;

import java.io.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.xerces.impl.dv.util.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.*;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/PositionManager.java,v 1.1 2003/09/07 09:29:55 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: PositionManager.java,v $
 * Revision 1.1  2003/09/07 09:29:55  maj0r
 * Position des Hauptfensters und Breite der Tabellenspalten werden gespeichert.
 *
 * Revision 1.22  2003/08/22 10:54:25  maj0r
 * Klassen umbenannt.
 * ConnectionSettings ueberarbeitet.
 *
 * Revision 1.21  2003/08/19 12:38:47  maj0r
 * Passworteingabe und md5 korrigiert.
 *
 * Revision 1.20  2003/08/16 18:40:25  maj0r
 * Passworteingabe korrigiert.
 *
 * Revision 1.19  2003/08/16 17:50:06  maj0r
 * Diverse Farben können nun manuell eingestellt bzw. deaktiviert werden.
 * DownloaduebersichtTabelle kann deaktiviert werden.
 *
 * Revision 1.18  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.17  2003/08/11 14:10:28  maj0r
 * DownloadPartList eingefügt.
 * Diverse Änderungen.
 *
 * Revision 1.16  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.15  2003/07/01 14:55:06  maj0r
 * Unnütze Abfrage entfernt.
 *
 * Revision 1.14  2003/06/24 14:32:27  maj0r
 * Klassen zum Sortieren von Tabellen eingefügt.
 * Servertabelle kann nun spaltenweise sortiert werden.
 *
 * Revision 1.13  2003/06/24 12:06:49  maj0r
 * log4j eingefügt (inkl. Bedienung über Einstellungsdialog).
 *
 * Revision 1.12  2003/06/22 19:01:22  maj0r
 * Hostverwendung korrigiert.
 *
 * Revision 1.11  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class PositionManager
        extends XMLDecoder {
    private static PositionManager instance = null;
    private Logger logger;
    private Point mainXY;
    private Dimension mainDimension;

    private int[] downloadWidths;
    private int[] uploadWidths;
    private int[] serverWidths;
    private int[] searchWidths;
    private int[] shareWidths;

    private boolean legal = false;

    private PositionManager(String path) {
        super(path);
        logger = Logger.getLogger(getClass());
    }

    public static PositionManager getInstance() {
        if (instance == null) {
            String path = System.getProperty("user.dir") + File.separator +
                    "properties.xml";
            instance = new PositionManager(path);
            instance.init();
        }
        return instance;
    }

    protected void init(){
        try{
            String temp = getFirstAttrbuteByTagName(new String[]{"options", "location", "main", "x"});
            if (temp.length()!=0){
                legal = true;
                int mainX = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "main", "x"}));
                int mainY = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "main", "y"}));
                mainXY = new Point(mainX, mainY);
                int mainWidth = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "main", "width"}));
                int mainHeight = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "main", "height"}));
                mainDimension = new Dimension(mainWidth, mainHeight);

                downloadWidths = new int[10];
                downloadWidths[0] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column0"}));
                downloadWidths[1] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column1"}));
                downloadWidths[2] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column2"}));
                downloadWidths[3] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column3"}));
                downloadWidths[4] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column4"}));
                downloadWidths[5] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column5"}));
                downloadWidths[6] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column6"}));
                downloadWidths[7] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column7"}));
                downloadWidths[8] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column8"}));
                downloadWidths[9] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column9"}));

                uploadWidths = new int[7];
                uploadWidths[0] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "upload", "column0"}));
                uploadWidths[1] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "upload", "column1"}));
                uploadWidths[2] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "upload", "column2"}));
                uploadWidths[3] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "upload", "column3"}));
                uploadWidths[4] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "upload", "column4"}));
                uploadWidths[5] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "upload", "column5"}));
                uploadWidths[6] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "upload", "column6"}));

                serverWidths = new int[4];
                serverWidths[0] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "server", "column0"}));
                serverWidths[1] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "server", "column1"}));
                serverWidths[2] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "server", "column2"}));
                serverWidths[3] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "server", "column3"}));

                searchWidths = new int[6];
                searchWidths[0] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "search", "column0"}));
                searchWidths[1] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "search", "column1"}));
                searchWidths[2] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "search", "column2"}));
                searchWidths[3] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "search", "column3"}));
                searchWidths[4] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "search", "column4"}));
                searchWidths[5] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "search", "column5"}));

                shareWidths = new int[3];
                shareWidths[0] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "share", "column0"}));
                shareWidths[1] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "share", "column1"}));
                shareWidths[2] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "share", "column2"}));
            }
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }


    public void save(){
        try{
            setAttributeByTagName(new String[]{"options", "location", "main", "x"}, mainXY.x);
            setAttributeByTagName(new String[]{"options", "location", "main", "y"}, mainXY.y);
            setAttributeByTagName(new String[]{"options", "location", "main", "width"}, mainDimension.width);
            setAttributeByTagName(new String[]{"options", "location", "main", "height"}, mainDimension.height);

            setAttributeByTagName(new String[]{"options", "location", "download", "column0"}, downloadWidths[0]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column1"}, downloadWidths[1]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column2"}, downloadWidths[2]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column3"}, downloadWidths[3]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column4"}, downloadWidths[4]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column5"}, downloadWidths[5]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column6"}, downloadWidths[6]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column7"}, downloadWidths[7]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column8"}, downloadWidths[8]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column9"}, downloadWidths[9]);

            setAttributeByTagName(new String[]{"options", "location", "upload", "column0"}, uploadWidths[0]);
            setAttributeByTagName(new String[]{"options", "location", "upload", "column1"}, uploadWidths[1]);
            setAttributeByTagName(new String[]{"options", "location", "upload", "column2"}, uploadWidths[2]);
            setAttributeByTagName(new String[]{"options", "location", "upload", "column3"}, uploadWidths[3]);
            setAttributeByTagName(new String[]{"options", "location", "upload", "column4"}, uploadWidths[4]);
            setAttributeByTagName(new String[]{"options", "location", "upload", "column5"}, uploadWidths[5]);
            setAttributeByTagName(new String[]{"options", "location", "upload", "column6"}, uploadWidths[6]);

            setAttributeByTagName(new String[]{"options", "location", "server", "column0"}, serverWidths[0]);
            setAttributeByTagName(new String[]{"options", "location", "server", "column1"}, serverWidths[1]);
            setAttributeByTagName(new String[]{"options", "location", "server", "column2"}, serverWidths[2]);
            setAttributeByTagName(new String[]{"options", "location", "server", "column3"}, serverWidths[3]);

            setAttributeByTagName(new String[]{"options", "location", "search", "column0"}, searchWidths[0]);
            setAttributeByTagName(new String[]{"options", "location", "search", "column1"}, searchWidths[1]);
            setAttributeByTagName(new String[]{"options", "location", "search", "column2"}, searchWidths[2]);
            setAttributeByTagName(new String[]{"options", "location", "search", "column3"}, searchWidths[3]);
            setAttributeByTagName(new String[]{"options", "location", "search", "column4"}, searchWidths[4]);
            setAttributeByTagName(new String[]{"options", "location", "search", "column5"}, searchWidths[5]);

            setAttributeByTagName(new String[]{"options", "location", "share", "column0"}, shareWidths[0]);
            setAttributeByTagName(new String[]{"options", "location", "share", "column1"}, shareWidths[1]);
            setAttributeByTagName(new String[]{"options", "location", "share", "column2"}, shareWidths[2]);
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void setMainXY(Point p) {
        mainXY = p;
    }

    public Point getMainXY(){
        return mainXY;
    }

    public void setMainDimension(Dimension dimension) {
        mainDimension = dimension;
    }

    public Dimension getMainDimension(){
        return mainDimension;
    }

    public void setDownloadWidths(int[] widths) {
        downloadWidths = widths;
    }

    public boolean isLegal() {
        return legal;
    }

    public int[] getDownloadWidths() {
        return downloadWidths;
    }

    public int[] getUploadWidths() {
        return uploadWidths;
    }

    public void setUploadWidths(int[] uploadWidths) {
        this.uploadWidths = uploadWidths;
    }

    public int[] getServerWidths() {
        return serverWidths;
    }

    public void setServerWidths(int[] serverWidths) {
        this.serverWidths = serverWidths;
    }

    public int[] getSearchWidths() {
        return searchWidths;
    }

    public void setSearchWidths(int[] searchWidths) {
        this.searchWidths = searchWidths;
    }

    public int[] getShareWidths() {
        return shareWidths;
    }

    public void setShareWidths(int[] shareWidths) {
        this.shareWidths = shareWidths;
    }
}