/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

/*
 * @(#)MemoryMonitor.java	1.32 03/01/23
 */

package de.applejuicenet.client.gui.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tools/Attic/MemoryMonitor.java,v 1.7 2004/10/06 12:29:14 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>              modifizierte, gekuerzte Sun-Source</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class MemoryMonitor
    extends JPanel
    implements Runnable {

    private static final long serialVersionUID = 5443875237558506269L;
	private Thread thread;
    private long sleepAmount = 1000;
    private int w, h;
    private BufferedImage bimg;
    private Graphics2D big;
    private Font font = new Font("Times New Roman", Font.PLAIN, 11);
    private Runtime runtime = Runtime.getRuntime();
    private int columnInc;
    private int pts[];
    private int ptNum;
    private int ascent, descent;
    private Rectangle graphOutlineRect = new Rectangle();
    private Rectangle2D mfRect = new Rectangle2D.Float();
    private Rectangle2D muRect = new Rectangle2D.Float();
    private Line2D graphLine = new Line2D.Float();
    private Color graphColor = new Color(46, 139, 87);
    private Color mfColor = new Color(0, 100, 0);
    private String usedStr;
    private String maxMem;

    public void startMemoryMonitor() {
        start();
    }

    public void stopMemoryMonitor() {
        stop();
    }

    public MemoryMonitor() {
        setBackground(Color.black);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (thread == null) {
                    start();
                }
                else {
                    stop();
                }
            }
        });
        maxMem = String.valueOf( (int) (runtime.maxMemory() / 1024));
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    public Dimension getPreferredSize() {
        return new Dimension(135, 80);
    }

    public void paint(Graphics g) {

        if (big == null) {
            return;
        }

        big.setBackground(getBackground());
        big.clearRect(0, 0, w, h);

        float freeMemory = (float) runtime.freeMemory();
        float totalMemory = (float) runtime.totalMemory();

        // .. Draw allocated and used strings ..
        big.setColor(Color.green);
        big.drawString(String.valueOf( (int) totalMemory / 1024) + "K / " +
                       maxMem + "K allocated", 4.0f, (float) ascent + 0.5f);
        usedStr = String.valueOf( ( (int) (totalMemory - freeMemory)) / 1024)
            + "K used";
        big.drawString(usedStr, 4, h - descent);

        // Calculate remaining size
        float ssH = ascent + descent;
        float remainingHeight = (h - (ssH * 2) - 0.5f);
        float blockHeight = remainingHeight / 10;
        float blockWidth = 20.0f;

        // .. Memory Free ..
        big.setColor(mfColor);
        int MemUsage = (int) ( (freeMemory / totalMemory) * 10);
        int i = 0;
        for (; i < MemUsage; i++) {
            mfRect.setRect(5, ssH + i * blockHeight,
                           blockWidth, blockHeight - 1);
            big.fill(mfRect);
        }

        // .. Memory Used ..
        big.setColor(Color.green);
        for (; i < 10; i++) {
            muRect.setRect(5, ssH + i * blockHeight,
                           blockWidth, blockHeight - 1);
            big.fill(muRect);
        }

        // .. Draw History Graph ..
        big.setColor(graphColor);
        int graphX = 30;
        int graphY = (int) ssH;
        int graphW = w - graphX - 5;
        int graphH = (int) remainingHeight;
        graphOutlineRect.setRect(graphX, graphY, graphW, graphH);
        big.draw(graphOutlineRect);

        int graphRow = graphH / 10;

        // .. Draw row ..
        for (int j = graphY; j <= graphH + graphY; j += graphRow) {
            graphLine.setLine(graphX, j, graphX + graphW, j);
            big.draw(graphLine);
        }

        // .. Draw animated column movement ..
        int graphColumn = graphW / 15;

        if (columnInc == 0) {
            columnInc = graphColumn;
        }

        for (int j = graphX + columnInc; j < graphW + graphX; j += graphColumn) {
            graphLine.setLine(j, graphY, j, graphY + graphH);
            big.draw(graphLine);
        }

        --columnInc;

        if (pts == null) {
            pts = new int[graphW];
            ptNum = 0;
        }
        else if (pts.length != graphW) {
            int tmp[] = null;
            if (ptNum < graphW) {
                tmp = new int[ptNum];
                System.arraycopy(pts, 0, tmp, 0, tmp.length);
            }
            else {
                tmp = new int[graphW];
                System.arraycopy(pts, pts.length - tmp.length, tmp, 0,
                                 tmp.length);
                ptNum = tmp.length - 2;
            }
            pts = new int[graphW];
            System.arraycopy(tmp, 0, pts, 0, tmp.length);
        }
        else {
            big.setColor(Color.yellow);
            pts[ptNum] = (int) (graphY + graphH * (freeMemory / totalMemory));
            for (int j = graphX + graphW - ptNum, k = 0; k < ptNum; k++, j++) {
                if (k != 0) {
                    if (pts[k] != pts[k - 1]) {
                        big.drawLine(j - 1, pts[k - 1], j, pts[k]);
                    }
                    else {
                        big.fillRect(j, pts[k], 1, 1);
                    }
                }
            }
            if (ptNum + 2 == pts.length) {
                // throw out oldest point
                for (int j = 1; j < ptNum; j++) {
                    pts[j - 1] = pts[j];
                }
                --ptNum;
            }
            else {
                ptNum++;
            }
        }
        g.drawImage(bimg, 0, 0, this);
    }

    private void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setName("MemoryMonitor");
        thread.start();
    }

    private synchronized void stop() {
        thread = null;
        notify();
    }

    public void run() {

        Thread me = Thread.currentThread();

        while (thread == me && !isShowing() || getSize().width == 0) {
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                return;
            }
        }

        while (thread == me && isShowing()) {
            Dimension d = getSize();
            if (d.width != w || d.height != h) {
                w = d.width;
                h = d.height;
                bimg = (BufferedImage) createImage(w, h);
                big = bimg.createGraphics();
                big.setFont(font);
                FontMetrics fm = big.getFontMetrics(font);
                ascent = fm.getAscent();
                descent = fm.getDescent();
            }
            repaint();
            try {
                Thread.sleep(sleepAmount);
            }
            catch (InterruptedException e) {
                break;
            }
        }
        thread = null;
    }
}
