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
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.Point;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.listener.LanguageListener;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/DownloadPartListPanel.java,v 1.27 2004/03/01 21:20:59 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f\uFFFDr den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: DownloadPartListPanel.java,v $
 * Revision 1.27  2004/03/01 21:20:59  maj0r
 * Natuerlich nur synchronisieren, wenn nicht NULL ...
 *
 * Revision 1.26  2004/03/01 15:46:25  maj0r
 * Bug #254 gefixt
 * Moeglichen NullPointer durch MultiThreading behoben.
 *
 * Revision 1.25  2004/02/26 13:59:41  maj0r
 * Logischen Fehler behoben, wenn es nur ums Neuzeichnen einer vorhandenen Partliste ging.
 *
 * Revision 1.24  2004/02/26 10:38:26  maj0r
 * Partlistverwendung auf Singleton geaendert.
 *
 * Revision 1.23  2004/02/25 11:08:08  maj0r
 * Partliste zeigt nun per MausOver-Effekt den Tooltipp zum ausgewaehlten Partstueck an.
 *
 * Revision 1.22  2004/02/24 08:11:39  maj0r
 * Bug #239 gefixt (Danke an dsp2004)
 * ArrayIndexOutOfBoundsException behoben.
 *
 * Revision 1.21  2004/02/20 14:55:02  maj0r
 * Speicheroptimierungen.
 *
 * Revision 1.20  2004/02/19 13:34:35  maj0r
 * Bild wird freigegeben, wenn die Partliste entfernt wird.
 *
 * Revision 1.19  2004/02/17 14:42:57  maj0r
 * Bug #220 gefixt (Danke an dsp2004)
 * OutOfMemoryError behoben.
 *
 * Revision 1.16  2004/02/12 18:18:32  maj0r
 * Zeichnen des letzten Parts korrigiert.
 *
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
    extends JPanel implements MouseMotionListener, LanguageListener{
    private PartListDO partListDO;
    private Logger logger;
    private BufferedImage image = null;
    private int width;
    private int height;
    private long fertigSeit = -1;
    private boolean miniFile = false;

    private String ueberprueft = "";
    private String nichtVorhanden = "";
    private String vorhanden = "";
    private String quellen = "";
    private String uebertragen = "";

    private MouseEvent savedMouseEvent = null;

    private static DownloadPartListPanel instance = null;

    private DownloadPartListPanel() {
        super(new BorderLayout());
        logger = Logger.getLogger(getClass());
        addMouseMotionListener(this);
        LanguageSelector.getInstance().addLanguageListener(this);
    }

    public static synchronized DownloadPartListPanel getInstance(){
        if (instance == null){
            instance = new DownloadPartListPanel();
        }
        return instance;
    }

    public void paintComponent(Graphics g) {
        if (partListDO != null && image != null) {
            if (height != (int) getSize().getHeight() ||
                width != (int) getSize().getWidth()) {
                synchronized (partListDO){
                    setPartList(partListDO);
                }
            }
            g.setColor(getBackground());
            g.fillRect(0, 0, width, height);
            if (image!=null){
                g.drawImage(image, 0, 0, null);
            }
        }
        else {
            super.paintComponent(g);
        }
    }

    public void setPartList(PartListDO newPartListDO) {
        try {
            if (partListDO != null && partListDO != newPartListDO) {
                partListDO.removeAllParts();
            }
            partListDO = newPartListDO;
            height = (int) getSize().getHeight();
            width = (int) getSize().getWidth();
            if (partListDO != null && partListDO.getParts().length > 0) {
                synchronized (partListDO) {
                    Part[] parts = partListDO.getParts();
                    int zeilenHoehe = 15;
                    int zeilen = height / zeilenHoehe;
                    miniFile = false;
                    int pixelSize = (int) (partListDO.getGroesse() /
                                           (zeilen * width));
                    if (pixelSize == 0) {
                        pixelSize = (int) ( (zeilen * width) /
                                           partListDO.getGroesse());
                        miniFile = true;
                    }
                    BufferedImage tempImage = new BufferedImage(width * zeilen,
                        15,
                        BufferedImage.TYPE_INT_ARGB);
                    Graphics graphics = tempImage.getGraphics();
                    int obenLinks = 0;
                    int breite = 0;
                    fertigSeit = -1;
                    for (int i = 0; i < parts.length - 1; i++) {
                        drawPart(false,
                                 (partListDO.getPartListType() ==
                                  PartListDO.MAIN_PARTLIST),
                                 graphics, pixelSize, parts[i].getType(),
                                 zeilenHoehe,
                                 parts[i].getFromPosition(),
                                 parts[i + 1].getFromPosition());
                    }
                    drawPart(true,
                             (partListDO.getPartListType() ==
                              PartListDO.MAIN_PARTLIST),
                             graphics, pixelSize,
                             parts[parts.length - 1].getType(), zeilenHoehe,
                             parts[parts.length - 1].getFromPosition(),
                             partListDO.getGroesse());
                    if (partListDO.getPartListType() ==
                        PartListDO.MAIN_PARTLIST) {
                        DownloadDO downloadDO = (DownloadDO) partListDO.
                            getValueDO();
                        if (downloadDO.getStatus() == DownloadDO.SUCHEN_LADEN) {
                            DownloadSourceDO[] sources = downloadDO.getSources();
                            for (int i = 0; i < sources.length; i++) {
                                if (sources[i].getStatus() ==
                                    DownloadSourceDO.UEBERTRAGUNG) {
                                    if (!miniFile) {
                                        obenLinks = sources[i].getDownloadFrom() /
                                            pixelSize;
                                        breite = (sources[i].getDownloadTo() /
                                                  pixelSize) - obenLinks;
                                    }
                                    else {
                                        obenLinks = sources[i].getDownloadFrom() *
                                            pixelSize;
                                        breite = (sources[i].getDownloadTo() *
                                                  pixelSize) - obenLinks;
                                    }
                                    graphics.setColor(getColorByPercent(sources[
                                        i].getReadyPercent()));
                                    graphics.fillRect(obenLinks, 0,
                                        breite, zeilenHoehe);
                                }
                            }
                        }
                    }
                    else {
                        DownloadSourceDO downloadSourceDO = (DownloadSourceDO)
                            partListDO.getValueDO();
                        if (downloadSourceDO.getStatus() ==
                            downloadSourceDO.UEBERTRAGUNG) {
                            if (!miniFile) {
                                obenLinks = downloadSourceDO.getDownloadFrom() /
                                    pixelSize;
                                breite = (downloadSourceDO.getDownloadTo() /
                                          pixelSize) - obenLinks;
                            }
                            else {
                                obenLinks = downloadSourceDO.getDownloadFrom() *
                                    pixelSize;
                                breite = (downloadSourceDO.getDownloadTo() *
                                          pixelSize) - obenLinks;
                            }
                            graphics.setColor(getColorByPercent(
                                downloadSourceDO.getReadyPercent()));
                            graphics.fillRect(obenLinks, 0,
                                              breite, zeilenHoehe);
                        }
                    }
                    image = new BufferedImage(width, height,
                                              BufferedImage.TYPE_INT_ARGB);
                    Graphics g = image.getGraphics();
                    int x = 0;
                    for (int i = 0; i < zeilen; i++) {
                        g.drawImage(tempImage.getSubimage(x, 0, width,
                            zeilenHoehe), 0, i * zeilenHoehe, null);
                        x += width;
                    }
                    if (savedMouseEvent != null) {
                        processMouseMotionEvent(savedMouseEvent);
                    }
                }
            }
            else {
                image = null;
                savedMouseEvent = null;
            }
            updateUI();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void drawPart(boolean forceDraw, boolean isMainList, Graphics graphics, int pixelSize, int partType,
                          int zeilenHoehe, long currentFrom, long nextFrom){
        int obenLinks = 0;
        int breite = 0;
        if (isMainList){
            if (partType == -1 && !forceDraw) {
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
                    if (partType == -1 || forceDraw){
                        graphics.setColor(PartListDO.COLOR_TYPE_UEBERPRUEFT);
                    }
                    else{
                        graphics.setColor(PartListDO.COLOR_TYPE_OK);
                    }
                    graphics.fillRect(obenLinks, 0, breite, zeilenHoehe);
                    obenLinks = (int) currentFrom / pixelSize;
                    breite = (int) (nextFrom / pixelSize) -
                        obenLinks;
                    graphics.setColor(getColorByType(partType));
                    graphics.fillRect(obenLinks, 0, breite, zeilenHoehe);
                    fertigSeit = -1;
                }
                else {
                    if (!miniFile){
                        obenLinks = (int) (currentFrom / pixelSize);
                        breite = (int) (nextFrom / pixelSize) -
                            obenLinks;
                    }
                    else{
                        obenLinks = (int) (currentFrom * pixelSize);
                        breite = (int) (nextFrom * pixelSize) -
                            obenLinks;
                    }
                    graphics.setColor(getColorByType(partType));
                    graphics.fillRect(obenLinks, 0, breite, zeilenHoehe);
                }
            }
        }
        else{
            if (!miniFile){
                obenLinks = (int) (currentFrom / pixelSize);
                breite = (int) (nextFrom / pixelSize) - obenLinks;
            }
            else{
                obenLinks = (int) (currentFrom * pixelSize);
                breite = (int) (nextFrom * pixelSize) - obenLinks;
            }
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

    private Color getColorByPercent(double percent){
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

    public void mouseDragged(MouseEvent mouseEvent) {
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        if (image!=null){
            savedMouseEvent = mouseEvent;
            Point p = mouseEvent.getPoint();
            int rgb = image.getRGB((int)p.getX(), (int)p.getY());
            if (rgb == PartListDO.COLOR_TYPE_UEBERPRUEFT.getRGB()) {
                setToolTipText(ueberprueft);
            }
            else if (rgb == PartListDO.COLOR_TYPE_0.getRGB()) {
                setToolTipText(nichtVorhanden);
            }
            else if (rgb == PartListDO.COLOR_TYPE_OK.getRGB()) {
                setToolTipText(vorhanden);
            }
            else if (rgb == PartListDO.COLOR_TYPE_1.getRGB()) {
                setToolTipText("1" + quellen);
            }
            else if (rgb == PartListDO.COLOR_TYPE_2.getRGB()) {
                setToolTipText("2" + quellen);
            }
            else if (rgb == PartListDO.COLOR_TYPE_3.getRGB()) {
                setToolTipText("3" + quellen);
            }
            else if (rgb == PartListDO.COLOR_TYPE_4.getRGB()) {
                setToolTipText("4" + quellen);
            }
            else if (rgb == PartListDO.COLOR_TYPE_5.getRGB()) {
                setToolTipText("5" + quellen);
            }
            else if (rgb == PartListDO.COLOR_TYPE_6.getRGB()) {
                setToolTipText("6" + quellen);
            }
            else if (rgb == PartListDO.COLOR_TYPE_7.getRGB()) {
                setToolTipText("7" + quellen);
            }
            else if (rgb == PartListDO.COLOR_TYPE_8.getRGB()) {
                setToolTipText("8" + quellen);
            }
            else if (rgb == PartListDO.COLOR_TYPE_9.getRGB()) {
                setToolTipText("9" + quellen);
            }
            else if (rgb == PartListDO.COLOR_TYPE_10.getRGB()) {
                setToolTipText("10+" + quellen);
            }
            else if (rgb == PartListDO.COLOR_READY_10.getRGB()) {
                setToolTipText("0-10" + uebertragen);
            }
            else if (rgb == PartListDO.COLOR_READY_30.getRGB()) {
                setToolTipText("10-30" + uebertragen);
            }
            else if (rgb == PartListDO.COLOR_READY_50.getRGB()) {
                setToolTipText("30-50" + uebertragen);
            }
            else if (rgb == PartListDO.COLOR_READY_70.getRGB()) {
                setToolTipText("50-70" + uebertragen);
            }
            else if (rgb == PartListDO.COLOR_READY_90.getRGB()) {
                setToolTipText("70-90" + uebertragen);
            }
            else if (rgb == PartListDO.COLOR_READY_100.getRGB()) {
                setToolTipText("90-100" + uebertragen);
            }
            else {
                setToolTipText(null);
            }
        }
        else{
            setToolTipText(null);
        }
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            vorhanden = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.Label4.caption"));
            nichtVorhanden = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.Label3.caption"));
            ueberprueft = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.Label1.caption"));
            quellen = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.downloadform.quellen"));
            uebertragen = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.downloadform.uebertragen"));
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }
}
