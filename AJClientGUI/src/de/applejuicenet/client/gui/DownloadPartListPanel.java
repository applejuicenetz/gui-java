package de.applejuicenet.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.dac.PartListDO;
import de.applejuicenet.client.shared.dac.PartListDO.Part;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/DownloadPartListPanel.java,v 1.15 2004/02/12 16:36:58 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: DownloadPartListPanel.java,v $
 * Revision 1.15  2004/02/12 16:36:58  maj0r
 * Anzeige der Teile, die zurzeit uebertragen werden (weiss bis dunkelgelb).
 *
 * Revision 1.14  2004/02/12 10:54:03  maj0r
 * Darstellungsfehler in der Partliste behoben.
 *
 * Revision 1.13  2004/02/09 17:46:11  maj0r
 * Partliste ueberarbeitet.
 *
 * Revision 1.12  2004/02/05 23:11:26  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.11  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.10  2003/12/19 14:26:34  maj0r
 * Neuzeichnen korrigiert.
 *
 * Revision 1.9  2003/12/19 13:35:40  maj0r
 * Bug in der Partliste behoben.
 *
 * Revision 1.8  2003/12/16 09:28:04  maj0r
 * NullPointer behoben.
 *
 * Revision 1.7  2003/10/15 09:12:34  maj0r
 * Beim Deaktivieren der Partliste wird diese nun auch zurueck gesetzt,
 *
 * Revision 1.6  2003/10/04 15:30:26  maj0r
 * Userpartliste hinzugefuegt.
 *
 * Revision 1.5  2003/09/07 12:11:59  maj0r
 * Anzeige korrigiert, da in der aktuellen Core auch Verfuegbarkeitswerte > 10 vorkommen koennen.
 *
 * Revision 1.4  2003/09/04 10:14:44  maj0r
 * NullPointer behoben.
 *
 * Revision 1.3  2003/09/04 09:29:18  maj0r
 * Anpassung an die Namenskonvention.
 *
 * Revision 1.2  2003/09/04 09:27:25  maj0r
 * DownloadPartListe fertiggestellt.
 *
 *
 */

public class DownloadPartListPanel
    extends JPanel {
    private PartListDO partListDO;
    private Logger logger;
    private BufferedImage image = null;
    private int width;
    private int height;
    private long fertigSeit = -1;

    public DownloadPartListPanel() {
        super(new BorderLayout());
        logger = Logger.getLogger(getClass());
    }

    public void paintComponent(Graphics g) {
        if (partListDO != null && image != null) {
            if (height != (int) getSize().getHeight() ||
                width != (int) getSize().getWidth()) {
                setPartList(partListDO);
            }
            g.setColor(getBackground());
            g.fillRect(0, 0, width, height);
            g.drawImage(image, 0, 0, null);
        }
        else {
            super.paintComponent(g);
        }
    }

    public void setPartList(PartListDO partListDO) {
        try {
            this.partListDO = partListDO;
            height = (int) getSize().getHeight();
            width = (int) getSize().getWidth();
            if (partListDO != null) {
                int zeilenHoehe = 15;
                int zeilen = height / zeilenHoehe;
                int pixelSize = (int) (partListDO.getGroesse() / (zeilen * width) );
                BufferedImage tempImage = new BufferedImage(width * zeilen, 15,
                                          BufferedImage.TYPE_INT_ARGB);
                Graphics graphics = tempImage.getGraphics();
                int obenLinks = 0;
                int breite = 0;
                Part[] parts = partListDO.getParts();
                for (int i=0; i<parts.length-1; i++){
                    drawPart((partListDO.getPartListType()==PartListDO.MAIN_PARTLIST), graphics, pixelSize, parts[i].getType(), zeilenHoehe,
                                         parts[i].getFromPosition(), parts[i+1].getFromPosition());
                }
                drawPart( (partListDO.getPartListType() == PartListDO.MAIN_PARTLIST), graphics, pixelSize,
                         parts[parts.length - 1].getType(), zeilenHoehe, parts[parts.length - 1].getFromPosition(),
                         partListDO.getGroesse());
                if (partListDO.getPartListType()==PartListDO.MAIN_PARTLIST){
                    DownloadDO downloadDO = (DownloadDO)partListDO.getValueDO();
                    if (downloadDO.getStatus() == DownloadDO.SUCHEN_LADEN) {
                        DownloadSourceDO[] sources = downloadDO.getSources();
                        for (int i = 0; i < sources.length; i++) {
                            if (sources[i].getStatus() ==
                                DownloadSourceDO.UEBERTRAGUNG) {
                                obenLinks = sources[i].getDownloadFrom() /
                                    pixelSize;
                                breite = (sources[i].getDownloadTo() /
                                    pixelSize) - obenLinks;
                                graphics.setColor(getColorByProcent(sources[i].getReadyPercent()));
                                graphics.fillRect(obenLinks, 0,
                                                  breite, zeilenHoehe);
                            }
                        }
                    }
                }
                else{
                    DownloadSourceDO downloadSourceDO = (DownloadSourceDO)partListDO.getValueDO();
                    if (downloadSourceDO.getStatus()==downloadSourceDO.UEBERTRAGUNG){
                        obenLinks = downloadSourceDO.getDownloadFrom() /
                            pixelSize;
                        breite = (downloadSourceDO.getDownloadTo() /
                            pixelSize) - obenLinks;
                        graphics.setColor(getColorByProcent(downloadSourceDO.getReadyPercent()));
                        graphics.fillRect(obenLinks, 0,
                                          breite, zeilenHoehe);
                    }
                }
                image = new BufferedImage(width, height,
                                          BufferedImage.TYPE_INT_ARGB);
                Graphics g = image.getGraphics();
                int x = 0;
                for (int i=0; i<zeilen; i++){
                    g.drawImage(tempImage.getSubimage(x, 0, width, zeilenHoehe), 0, i*zeilenHoehe, null);
                    x += width;
                }
            }
            updateUI();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void drawPart(boolean isMainList, Graphics graphics, int pixelSize, int partType, int zeilenHoehe, long currentFrom, long nextFrom){
        int obenLinks = 0;
        int breite = 0;
        if (isMainList){
            if (partType == -1) {
                if (fertigSeit != -1) {
                    return;
                }
                else {
                    fertigSeit = currentFrom;
                }
            }
            else {
                if (fertigSeit != -1) {
                    obenLinks = (int) (fertigSeit / pixelSize);
                    int mbCount = (int) (currentFrom - fertigSeit) /
                        1048576;
                    breite = mbCount * 1048576 / pixelSize;
                    graphics.setColor(PartListDO.COLOR_TYPE_UEBERPRUEFT);
                    graphics.fillRect(obenLinks, 0, breite, zeilenHoehe);
                    obenLinks += breite;
                    breite = (int) (currentFrom / pixelSize) -
                        obenLinks;
                    graphics.setColor(PartListDO.COLOR_TYPE_OK);
                    graphics.fillRect(obenLinks, 0, breite, zeilenHoehe);
                    obenLinks = (int) currentFrom / pixelSize;
                    breite = (int) (nextFrom / pixelSize) -
                        obenLinks;
                    graphics.setColor(getColorByType(partType));
                    graphics.fillRect(obenLinks, 0, breite, zeilenHoehe);
                    fertigSeit = -1;
                }
                else {
                    obenLinks = (int) (currentFrom / pixelSize);
                    breite = (int) (nextFrom / pixelSize) -
                        obenLinks;
                    graphics.setColor(getColorByType(partType));
                    graphics.fillRect(obenLinks, 0, breite, zeilenHoehe);
                }
            }
        }
        else{
            obenLinks = (int)(currentFrom / pixelSize);
            breite = (int)(nextFrom / pixelSize) - obenLinks;
            graphics.setColor(getColorByType(partType));
            graphics.fillRect(obenLinks, 0, breite, zeilenHoehe);
        }
    }

    private Color getColorByType(int type) {
        switch (type) {
            case -1:
                return PartListDO.COLOR_TYPE_OK;
            case 0:
                return PartListDO.COLOR_TYPE_0;
            case 1:
                return PartListDO.COLOR_TYPE_1;
            case 2:
                return PartListDO.COLOR_TYPE_2;
            case 3:
                return PartListDO.COLOR_TYPE_3;
            case 4:
                return PartListDO.COLOR_TYPE_4;
            case 5:
                return PartListDO.COLOR_TYPE_5;
            case 6:
                return PartListDO.COLOR_TYPE_6;
            case 7:
                return PartListDO.COLOR_TYPE_7;
            case 8:
                return PartListDO.COLOR_TYPE_8;
            case 9:
                return PartListDO.COLOR_TYPE_9;
            case 10:
                return PartListDO.COLOR_TYPE_10;
            default:
                return PartListDO.COLOR_TYPE_10;
        }
    }

    private Color getColorByProcent(double percent){
        if (percent<10){
            return PartListDO.COLOR_READY_10;
        }
        else if (percent<30){
            return PartListDO.COLOR_READY_30;
        }
        else if (percent<50){
            return PartListDO.COLOR_READY_50;
        }
        else if (percent<70){
            return PartListDO.COLOR_READY_70;
        }
        else if (percent<90){
            return PartListDO.COLOR_READY_90;
        }
        else{
            return PartListDO.COLOR_READY_100;
        }
    }
}
