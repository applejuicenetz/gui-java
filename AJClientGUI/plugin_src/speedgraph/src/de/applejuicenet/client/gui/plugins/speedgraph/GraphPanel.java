package de.applejuicenet.client.gui.plugins.speedgraph;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/speedgraph/src/de/applejuicenet/client/gui/plugins/speedgraph/GraphPanel.java,v 1.5 2004/10/14 08:57:55 maj0r Exp $
 *
 * <p>Titel: AppleJuice Core-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class GraphPanel extends JPanel{
    private String uploadSpeedKey = "uploadspeed";
    private String downloadSpeedKey = "downloadspeed";

    private int x = 33;
    private int lastUp = 0;
    private int lastDown = 0;
    private int width;
    private int imageHeight;
    private int bottomHeight = 30;
    private int minImageHeight = bottomHeight + 280;
    private long time;
    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
    private Font timeFont;

    private Image image = null;

    public void paintComponent(Graphics g) {
        if (image != null)
        {
            if (getHeight()!=imageHeight){
                if (getHeight()>=minImageHeight){
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
                int oldWidth = width;
                width = getWidth();
                Image image2 = new BufferedImage(width, imageHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics g2 = image2.getGraphics();
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0 , width, imageHeight);
                g2.drawImage(image, 0, 0, null);
                g2.setColor(Color.WHITE);
                g2.drawLine(oldWidth, imageHeight - bottomHeight, width, imageHeight - bottomHeight);
                g2.drawLine(oldWidth, imageHeight - bottomHeight - 21, width, imageHeight - bottomHeight - 21);
                g2.drawLine(oldWidth, imageHeight - bottomHeight - 41, width, imageHeight - bottomHeight - 41);
                g2.drawLine(oldWidth, imageHeight - bottomHeight - 61, width, imageHeight - bottomHeight - 61);
                g2.drawLine(oldWidth, imageHeight - bottomHeight - 81, width, imageHeight - bottomHeight - 81);
                g2.drawLine(oldWidth, imageHeight - bottomHeight - 101, width, imageHeight - bottomHeight - 101);
                g2.drawLine(oldWidth, imageHeight - bottomHeight - 121, width, imageHeight - bottomHeight - 121);
                g2.drawLine(oldWidth, imageHeight - bottomHeight - 141, width, imageHeight - bottomHeight - 141);
                g2.drawLine(oldWidth, imageHeight - bottomHeight - 161, width, imageHeight - bottomHeight - 161);
                g2.drawLine(oldWidth, imageHeight - bottomHeight - 181, width, imageHeight - bottomHeight - 181);
                g2.drawLine(oldWidth, imageHeight - bottomHeight - 201, width, imageHeight - bottomHeight - 201);
                g2.drawLine(oldWidth, imageHeight - bottomHeight - 221, width, imageHeight - bottomHeight - 221);
                g2.drawLine(oldWidth, imageHeight - bottomHeight - 241, width, imageHeight - bottomHeight - 241);
                image = image2;
                setPreferredSize(new Dimension(width, imageHeight));
            }
            g.drawImage(image, 0, 0, null);
        }
        else{
            super.paintComponent(g);
        }
    }

    public void update(HashMap speeds){
        if (image==null){
            time = System.currentTimeMillis();
            width = getWidth();
            if (getHeight()<minImageHeight){
                imageHeight = minImageHeight;
            }
            else{
                imageHeight = getHeight();
            }
            if (width==0 || imageHeight==0){
                /*
                 Da kamen schneller Daten rein, als das Plugin angezeigt werden konnte.
                 Wir nehmen folglich erst den naechsten Durchgang.
                  */
                return;
            }
            image = new BufferedImage(width, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.getGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0 , width, imageHeight);
            g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            int strWidth = fm.stringWidth ( "10 -" );
            int strHeight = fm.getHeight();
            g.drawString(" 10 ", 30-strWidth, imageHeight - bottomHeight - 18);
            g.drawString(" 20 ", 30-strWidth, imageHeight - bottomHeight - 38);
            g.drawString(" 30 ", 30-strWidth, imageHeight - bottomHeight - 58);
            g.drawString(" 40 ", 30-strWidth, imageHeight - bottomHeight - 78);
            g.drawString(" 50 ", 30-strWidth, imageHeight - bottomHeight - 98);
            g.drawString(" 60 ", 30-strWidth, imageHeight - bottomHeight - 118);
            g.drawString(" 70 ", 30-strWidth, imageHeight - bottomHeight - 138);
            g.drawString(" 80 ", 30-strWidth, imageHeight - bottomHeight - 158);
            g.drawString(" 90 ", 30-strWidth, imageHeight - bottomHeight - 178);
            strWidth = fm.stringWidth ( "100 -" );
            g.drawString("100 ", 33-strWidth, imageHeight - bottomHeight - 198);
            g.drawString("110 ", 33-strWidth, imageHeight - bottomHeight - 218);
            g.drawString("120 ", 33-strWidth, imageHeight - bottomHeight - 238);
            g.drawLine(32, imageHeight - bottomHeight, 32, imageHeight - bottomHeight - 250);
            g.drawLine(32, imageHeight - bottomHeight, width, imageHeight - bottomHeight);
            strWidth = fm.stringWidth ( "kb/s" );
            g.drawString("kb/s", 21, imageHeight - bottomHeight - 261);
            g.setColor(Color.GREEN);
            g.drawLine(70, imageHeight - bottomHeight- 265, 85, imageHeight - bottomHeight - 265);
            g.setColor(Color.WHITE);
            g.drawString("Download", 92, imageHeight - bottomHeight - 261);
            g.setColor(Color.YELLOW);
            g.drawLine(170, imageHeight - bottomHeight - 265, 185, imageHeight - bottomHeight - 265);
            g.setColor(Color.WHITE);
            g.drawString("Upload", 192, imageHeight - bottomHeight - 261);
            Font gFont = g.getFont();
            timeFont = new Font(gFont.getName(), gFont.getStyle(), 9);
            g.setFont(timeFont);
            fm = g.getFontMetrics();
            String actualTime = formatter.format(new Date(time));
            strWidth = fm.stringWidth (actualTime);
            g.drawString(actualTime, x - strWidth/2, imageHeight - 10);
            g.drawLine(x, imageHeight - bottomHeight - 1, x, imageHeight - bottomHeight + 4);
            g.drawLine(29, imageHeight - bottomHeight - 21, width, imageHeight - bottomHeight - 21);
            g.drawLine(29, imageHeight - bottomHeight - 41, width, imageHeight - bottomHeight - 41);
            g.drawLine(29, imageHeight - bottomHeight - 61, width, imageHeight - bottomHeight - 61);
            g.drawLine(29, imageHeight - bottomHeight - 81, width, imageHeight - bottomHeight - 81);
            g.drawLine(29, imageHeight - bottomHeight - 101, width, imageHeight - bottomHeight - 101);
            g.drawLine(29, imageHeight - bottomHeight - 121, width, imageHeight - bottomHeight - 121);
            g.drawLine(29, imageHeight - bottomHeight - 141, width, imageHeight - bottomHeight - 141);
            g.drawLine(29, imageHeight - bottomHeight - 161, width, imageHeight - bottomHeight - 161);
            g.drawLine(29, imageHeight - bottomHeight - 181, width, imageHeight - bottomHeight - 181);
            g.drawLine(29, imageHeight - bottomHeight - 201, width, imageHeight - bottomHeight - 201);
            g.drawLine(29, imageHeight - bottomHeight - 221, width, imageHeight - bottomHeight - 221);
            g.drawLine(29, imageHeight - bottomHeight - 241, width, imageHeight - bottomHeight - 241);
            setPreferredSize(new Dimension(width, imageHeight));
        }
        if (x>=width){
            width += width;
            Image image2 = new BufferedImage(width, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics g = image2.getGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(width/2, 0 , width, imageHeight);
            g.drawImage(image, 0, 0, null);
            g.setColor(Color.WHITE);
            g.drawLine(width/2, imageHeight - bottomHeight, width, imageHeight - bottomHeight);
            image = image2;
            setPreferredSize(new Dimension(width, imageHeight));
        }
        Long downSpeed = (Long)speeds.get(downloadSpeedKey);
        Graphics g = image.getGraphics();
        boolean increaseX = false;
        if (downSpeed!=null){
            int down = downSpeed.intValue() / 1024;
            g.setColor(Color.GREEN);
            g.drawLine(x, imageHeight - lastDown - bottomHeight - 1, x+1, imageHeight - down*2 - bottomHeight -1);
            lastDown = down*2;
            increaseX = true;
        }
        Long upSpeed = (Long)speeds.get(uploadSpeedKey);
        if (upSpeed!=null){
            int up = upSpeed.intValue() / 1024;
            g.setColor(Color.YELLOW);
            g.drawLine(x, imageHeight - lastUp - bottomHeight -1, x+1, imageHeight - up*2 - bottomHeight -1);
            lastUp = up*2;
            increaseX = true;
        }
        if (increaseX){
            long newTime = System.currentTimeMillis();
            if (newTime > time + 300000){
                time = newTime;
                g.setFont(timeFont);
                FontMetrics fm = g.getFontMetrics();
                String actualTime = formatter.format(new Date(time));
                int strWidth = fm.stringWidth (actualTime);
                g.setColor(Color.WHITE);
                g.drawString(actualTime, x - strWidth/2, imageHeight - 10);
                g.drawLine(x, imageHeight - bottomHeight - 1, x, imageHeight - bottomHeight + 4);
            }
            x++;
        }
        repaint();
    }
}
