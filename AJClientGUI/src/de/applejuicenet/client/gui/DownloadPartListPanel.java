package de.applejuicenet.client.gui;

import de.applejuicenet.client.shared.dac.PartListDO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class DownloadPartListPanel extends JPanel {
    private PartListDO partListDO;
    private Logger logger;

    public DownloadPartListPanel() {
        logger = Logger.getLogger(getClass());
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                repaint();
            }
        });
    }

    public void setPartList(PartListDO partListDO) {
        this.partListDO = partListDO;
        repaint();
    }

    public void repaint() {
        if (partListDO == null) {
            super.repaint();
        }
        else {
            try {
                Graphics graphics = getGraphics();
                int anzahlRows = getHeight() / 16 - 1;
                int anzahlZeile = getWidth() / 2;
                int anzahl = anzahlRows * anzahlZeile;
                int groesseProPart;
                int anzahlParts;
                if (partListDO.getGroesse() > anzahl) {
                    groesseProPart = (int) partListDO.getGroesse() / anzahl;
                    anzahlParts = anzahl;
                }
                else {
                    groesseProPart = 1;
                    anzahlParts = (int) partListDO.getGroesse();
                }
                int bisher = 0;
                int x = 1;
                int y = 0;
                long position = 0;
                int partPos = 0;
                PartListDO.Part[] parts = partListDO.getParts();
                long mbStart;
                long mbEnde;
                int kleiner;
                int groesstes;
                boolean ueberprueft;
                while (bisher < anzahlParts) {
                    bisher++;
                    if (x >= anzahlZeile * 2 - 2) {
                        y++;
                        x = 1;
                    }
                    position += groesseProPart;
                    while (parts[partPos].getFromPosition() < position && partPos < parts.length - 1) {
                        partPos++;
                    }
                    partPos--;
                    if (parts[partPos].getType() == -1) {
                        mbStart = position / 1048576 * 1048576;
                        mbEnde = mbStart + 1048576;
                        kleiner = partPos;
                        groesstes = partPos;
                        while (parts[kleiner].getFromPosition() > mbStart && kleiner > 0) {
                            kleiner--;
                        }
                        while (parts[groesstes].getFromPosition() < mbEnde && groesstes < parts.length - 1) {
                            groesstes++;
                        }
                        groesstes--;
                        ueberprueft = true;
                        for (int l = kleiner; l <= groesstes; l++) {
                            if (parts[l].getType() != -1) {
                                ueberprueft = false;
                                break;
                            }
                        }
                        if (ueberprueft) {
                            graphics.setColor(PartListDO.COLOR_TYPE_UEBERPRUEFT);
                        }
                        else {
                            graphics.setColor(PartListDO.COLOR_TYPE_OK);
                        }
                    }
                    else {
                        graphics.setColor(getColorByType(parts[partPos].getType()));
                    }
                    graphics.fillRect(x, y * 16, x + 1, (y + 1) * 16);
                    x += 2;
                }
            }
            catch (Exception e) {
                if (logger.isEnabledFor(Level.ERROR))
                    logger.error("Unbehandelte Exception", e);
            }
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
                return PartListDO.COLOR_TYPE_0;
        }
    }
}
