package de.applejuicenet.client.gui.plugins.speedgraph;

import de.applejuicenet.client.shared.MapSetStringKey;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/speedgraph/src/de/applejuicenet/client/gui/plugins/speedgraph/GraphPanel.java,v 1.1 2003/09/13 11:33:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Core-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: GraphPanel.java,v $
 * Revision 1.1  2003/09/13 11:33:17  maj0r
 * Neues Plugin SpeedGraph.
 *
 *
 */

public class GraphPanel extends JPanel{
    private MapSetStringKey uploadSpeedKey = new MapSetStringKey("uploadspeed");
    private MapSetStringKey downloadSpeedKey = new MapSetStringKey("downloadspeed");
    private MapSetStringKey creditsKey = new MapSetStringKey("credits");
    private MapSetStringKey sessionUploadKey = new MapSetStringKey("sessionupload");
    private MapSetStringKey sessionDownloadKey = new MapSetStringKey("sessiondownload");

    private int x = 33;
    private int lastUp = 0;
    private int lastDown = 0;
    private int width;
    private int imageHeight;

    private Image image = null;

    public void paintComponent(Graphics g) {
        if (image != null)
        {
            if (getHeight()!=imageHeight){
                if (getHeight()>=250){
                    int newImageHeight = getHeight();
                    Image image2 = new BufferedImage(width, newImageHeight, BufferedImage.TYPE_INT_ARGB);
                    Graphics g2 = image2.getGraphics();
                    g2.setColor(Color.BLACK);
                    g2.fillRect(0, 0 , width, newImageHeight);
                    g2.drawImage(image, 0, newImageHeight-imageHeight, null);
                    imageHeight = newImageHeight;
                    image = image2;
                    setPreferredSize(new Dimension(width, imageHeight));
                }
            }
            if (getWidth()>width){
                width = getWidth();
                Image image2 = new BufferedImage(width, imageHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics g2 = image2.getGraphics();
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0 , width, imageHeight);
                g2.drawImage(image, 0, 0, null);
                image = image2;
                setPreferredSize(new Dimension(width, imageHeight));
            }
            g.drawImage(image, 0, 0, null);
        }
        else
            super.paintComponent(g);
    }

    public void update(HashMap speeds){
        if (image==null){
            width = getWidth();
            if (getHeight()<250)
                imageHeight=250;
            else
                imageHeight=getHeight();
            image = new BufferedImage(width, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.getGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0 , width, imageHeight);
            g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            int strWidth = fm.stringWidth ( "10 -" );
            g.drawString(" 10 -", 30-strWidth, imageHeight - 21);
            g.drawString(" 20 -", 30-strWidth, imageHeight - 41);
            g.drawString(" 30 -", 30-strWidth, imageHeight - 61);
            g.drawString(" 40 -", 30-strWidth, imageHeight - 81);
            g.drawString(" 50 -", 30-strWidth, imageHeight - 101);
            g.drawString(" 60 -", 30-strWidth, imageHeight - 121);
            g.drawString(" 70 -", 30-strWidth, imageHeight - 141);
            g.drawString(" 80 -", 30-strWidth, imageHeight - 161);
            g.drawString(" 90 -", 30-strWidth, imageHeight - 181);
            strWidth = fm.stringWidth ( "100 -" );
            g.drawString("100 -", 33-strWidth, imageHeight - 201);
            g.drawString("110 -", 33-strWidth, imageHeight - 221);
            g.drawString("120 -", 33-strWidth, imageHeight - 241);
            g.drawLine(32, imageHeight, 32, imageHeight - 250);
            strWidth = fm.stringWidth ( "kb/s" );
            g.drawString("kb/s", 21, imageHeight - 261);
            g.setColor(Color.GREEN);
            g.drawLine(70, imageHeight - 265, 85, imageHeight - 265);
            g.setColor(Color.WHITE);
            g.drawString("Download", 92, imageHeight - 261);
            g.setColor(Color.YELLOW);
            g.drawLine(170, imageHeight - 265, 185, imageHeight - 265);
            g.setColor(Color.WHITE);
            g.drawString("Upload", 192, imageHeight - 261);

            setPreferredSize(new Dimension(width, imageHeight));
        }
        if (x>=width){
            width += width;
            Image image2 = new BufferedImage(width, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics g = image2.getGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(width/2, 0 , width, imageHeight);
            g.drawImage(image, 0, 0, null);
            image = image2;
            setPreferredSize(new Dimension(width, imageHeight));
        }
        int down = ((Long)speeds.get(downloadSpeedKey)).intValue() / 1024;
        int up = ((Long)speeds.get(uploadSpeedKey)).intValue() / 1024;
        Graphics g = image.getGraphics();
        g.setColor(Color.GREEN);
        g.drawLine(x, imageHeight - lastDown -1, x+1, imageHeight - down*2 -1);
        lastDown = down*2;
        g.setColor(Color.YELLOW);
        g.drawLine(x, imageHeight - lastUp -1, x+1, imageHeight - up*2 -1);
        lastUp = up*2;
        x++;
        repaint();
    }
}
