package de.applejuicenet.client.gui;

import de.applejuicenet.client.shared.dac.PartListDO;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/DownloadPartListPanel.java,v 1.2 2003/09/04 09:27:25 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadPartListPanel.java,v $
 * Revision 1.2  2003/09/04 09:27:25  maj0r
 * DownloadPartListe fertiggestellt.
 *
 *
 */

public class DownloadPartListPanel extends JPanel {
    private PartListDO partListDO;
    private Logger logger;
    private BufferedImage I = null;
    int width;
    int height;

    public DownloadPartListPanel() {
        super(new BorderLayout());
        logger = Logger.getLogger(getClass());
    }

    public void paintComponent(Graphics g) {
        if (partListDO != null)
        {
            if (height != getHeight() || width != getWidth()){
                setPartList(partListDO);
            }
            g.drawImage(I, 0, 0, null);
        }
        else
            super.paintComponent(g);
    }

    public void setPartList(PartListDO partListDO) {
        if (this.partListDO == partListDO)
            return;
        this.partListDO = partListDO;
        if (partListDO != null)
        {
            try
            {
                I = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_ARGB);
                Graphics graphics = I.getGraphics();
                height = getHeight();
                width = getWidth();
                int anzahlRows = getHeight() / 16;
                int anzahlZeile = getWidth() / 2;
                int anzahl = anzahlRows * anzahlZeile;
                int groesseProPart;
                int anzahlParts;
                if (partListDO.getGroesse() > anzahl)
                {
                    groesseProPart = (int) partListDO.getGroesse() / anzahl;
                    anzahlParts = anzahl;
                }
                else
                {
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
                while (bisher < anzahlParts)
                {
                    bisher++;
                    if (x >= anzahlZeile * 2 - 2)
                    {
                        y++;
                        x = 1;
                    }
                    position += groesseProPart;
                    while (parts[partPos].getFromPosition() < position && partPos < parts.length - 1)
                    {
                        partPos++;
                    }
                    partPos--;
                    if (parts[partPos].getType() == -1)
                    {
                        mbStart = position / 1048576 * 1048576;
                        mbEnde = mbStart + 1048576;
                        kleiner = partPos;
                        groesstes = partPos;
                        while (parts[kleiner].getFromPosition() > mbStart && kleiner > 0)
                        {
                            kleiner--;
                        }
                        while (parts[groesstes].getFromPosition() < mbEnde && groesstes < parts.length - 1)
                        {
                            groesstes++;
                        }
                        groesstes--;
                        ueberprueft = true;
                        for (int l = kleiner; l <= groesstes; l++)
                        {
                            if (parts[l].getType() != -1)
                            {
                                ueberprueft = false;
                                break;
                            }
                        }
                        if (ueberprueft)
                        {
                            graphics.setColor(PartListDO.COLOR_TYPE_UEBERPRUEFT);
                        }
                        else
                        {
                            graphics.setColor(PartListDO.COLOR_TYPE_OK);
                        }
                    }
                    else
                    {
                        graphics.setColor(getColorByType(parts[partPos].getType()));
                    }
                    graphics.fillRect(x, y * 16 +1 , x + 1, (y + 1) * 16);
                    x += 2;
                }
                getGraphics().drawImage(I, 0, 0, null);
            }
            catch (Exception e)
            {
                if (logger.isEnabledFor(Level.ERROR))
                    logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private Color getColorByType(int type) {
        switch (type)
        {
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
