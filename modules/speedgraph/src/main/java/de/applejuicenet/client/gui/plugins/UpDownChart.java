/*
 * Created on 29.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.applejuicenet.client.gui.plugins;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/*TODO UL/DL marken für ISDN, DSL,...

/**
 * @author Zab0815
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class UpDownChart extends JPanel implements MouseListener, MouseMotionListener, PropertyChangeListener {
    /*** <code>BG_NO_GRADIENT</code> draws simple background with <code>BGStart</code> as color */
    public final static int BG_NO_GRADIENT = -1;
    /*** <code>BG_LEFT_TO_RIGHT</code> draws the gradient from left with <code>BGStart</code> to right with <code>BGEnd</code> */
    public final static int BG_LEFT_TO_RIGHT = 0;
    /*** <code>BG_RIGHT_TO_LEFT</code> draws the gradient from right with <code>BGStart</code> to left with <code>BGEnd</code> */
    public final static int BG_RIGHT_TO_LEFT = 1;
    /*** <code>BG_DOWN_TO_UP</code> draws the gradient from up with <code>BGStart</code> to down with <code>BGEnd</code> */
    public final static int BG_UP_TO_DOWN = 2;
    /*** <code>BG_DOWN_TO_UP</code> draws the gradient from down with <code>BGStart</code> to up with <code>BGEnd</code> */
    public final static int BG_DOWN_TO_UP = 3;
    /*** <code>BG_UPLEFT_TO_DOWNRIGHT</code> draws the gradient from up-left with <code>BGStart</code> to down-right with <code>BGEnd</code> */
    public final static int BG_UPLEFT_TO_DOWNRIGHT = 4;
    /*** <code>BG_DOWNRIGHT_TO_UPLEFT</code> draws the gradient from down-right with <code>BGStart</code> to up-left with <code>BGEnd</code> */
    public final static int BG_DOWNRIGHT_TO_UPLEFT = 5;
    /*** <code>BG_UPRIGHT_TO_DOWNLEFT</code> draws the gradient from up-right with <code>BGStart</code> to down-left with <code>BGEnd</code> */
    public final static int BG_UPRIGHT_TO_DOWNLEFT = 6;
    /*** <code>BG_DOWNLEFT_TO_UPRIGHT</code> draws the gradient from down-left with <code>BGStart</code> to up-right with <code>BGEnd</code> */
    public final static int BG_DOWNLEFT_TO_UPRIGHT = 7;

    private final int minChartHeight = 100;

    private static Logger logger;
    private static Timer timerThread;
    private TimerTask chartTimer = null;

    private long initTime = 0;

    private static int gradientDirection = BG_DOWN_TO_UP;
    private static Color BGEnd = new Color(20, 200, 20);
    private static Color BGStart = new Color(0, 0, 0);
    private Color downCol = new Color(20, 20, 200);
    private Color upCol = new Color(200, 20, 20);
    private Color lineCol = new Color(255, 255, 255);

    private UpDownData ud = null;
    private SpeedGraphPlugin parent = null;

    private int maxWidth = 0;
    private int legendX = 250;
    private int legendY = 10;
    private int legendWidth = 200;
    private int legendHeight = 20;
    private int legendAlpha = 128;
    private int mouseX = 0;
    private int mouseY = 0;

    private int topY = 0;
    private int bottomY = 0;
    private int bottomHeight = 30;
    private int maxSpeed = 10;
    private int aktX = 0;
    private int oldHeight = 0;
    private int oldWidth = 0;

    private Image image = null;
    private BufferedImage backImage = null;
    private BufferedImage legendImage = null;

    private boolean dragging = false;
    private String uploadSpeedKey = "uploadspeed";
    private String downloadSpeedKey = "downloadspeed";
    private long updatePeriod;

    /**
     * @return Returns the updatePeriod.
     */
    public long getUpdatePeriod() {
        return updatePeriod;
    }

    /**
     * @param updatePeriod The updatePeriod to set.
     */
    public void setUpdatePeriod(long updatePeriod) {
        this.updatePeriod = updatePeriod;
    }

    /**
     * @param parent The properties to set.
     */
    public void setParent(SpeedGraphPlugin parent) {
        this.parent = parent;
        readProperies();
    }

    private class chartTimerTask extends TimerTask {

        public void run() {
            aktX++;
            repaint();
            //System.out.println("Timer:" + (System.currentTimeMillis() - initTime));
        }
    }

    public UpDownChart() {
        logger = LoggerFactory.getLogger(getClass());
        addMouseListener(this);
        initTime = System.currentTimeMillis();
        try {
            try {
                timerThread = new Timer();
                chartTimer = new chartTimerTask();
            } catch (Exception e1) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e1);
            }

            ud = new UpDownData();
        } catch (NullPointerException e) {
            logger.error("Nullpointer-exception: data-storage not initialized!", e);
        }
    }


    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && dragging) {
            if (legendX + e.getX() - mouseX > 0)
                legendX += e.getX() - mouseX;
            else legendX = 0;

            if (legendY + e.getY() - mouseY > topY)
                legendY += e.getY() - mouseY;
            else legendY = topY;

            dragging = false;
            legendAlpha = 80;
            repaint();
        }
    }

    ;

    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && !dragging) {
            if (e.getX() > legendX && e.getX() < legendX + 200) {
                if (e.getY() > legendY && e.getY() < legendY + 20) {
                    dragging = true;
                    mouseX = e.getX();
                    mouseY = e.getY();
                    legendAlpha = 200;
                    repaint();
                }
            }
        }
    }

    ;

    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent e) {
        if (legendX + e.getX() - mouseX > 0)
            legendX += e.getX() - mouseX;
        else legendX = 0;

        if (legendY + e.getY() - mouseY > topY)
            legendY += e.getY() - mouseY;
        else legendY = topY;
        legendAlpha = 80;
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent e) {
    }

    ;

    public void mouseExited(MouseEvent e) {
        dragging = false;
    }

    ;

    public void mouseEntered(MouseEvent e) {
    }

    ;

    public void mouseClicked(MouseEvent e) {
    }

    ;


    public void update(HashMap info) {

        if (ud == null) {
            ud = new UpDownData();
        }

        //add data to the array
        try {
            //Prüfen, ob mehr Daten gespeichert wurden, als angezeigt werden können.
            if (ud.size() > maxWidth)
                //erstes element aus dem Array löschen.
                ud.remove(0);

            ud.add((Long) info.get(uploadSpeedKey), (Long) info.get(downloadSpeedKey), System.currentTimeMillis());
            if (ud.size() == 1)
                timerThread.schedule(chartTimer, 1000, updatePeriod);

        } catch (NullPointerException e) {
            //No Data, or no Time then exit
        }

//		if (getWidth()==0 || getHeight()==0){
//			/*
//			 Da kamen schneller Daten rein, als das Plugin angezeigt werden konnte.
//			 Wir nehmen folglich erst den naechsten Durchgang.
//			 */
//			return;
//		}

        //drawGraph();
        //repaint();
    }


    public void paintComponent(Graphics g) {
//		initializeImages();
        if (image != null) {
            boolean UpdateFlag = false;

            if (getHeight() != oldHeight || getWidth() != oldWidth) {
                UpdateFlag = true;
            }

            if (Boolean.parseBoolean(parent.getProperties().getProperty("changed"))) {
                readProperies();

                UpdateFlag = true;
            }

            if (UpdateFlag == true) {
                oldHeight = getHeight();
                bottomY = getHeight() - bottomHeight;
                oldWidth = getWidth();
                maxWidth = getWidth();

                image = null;
                //System.out.println("discard image");
                backImage = null;
                //System.out.println("discard backImage");
                initializeImages();
            }

            try {
                Graphics2D g2 = (Graphics2D) image.getGraphics();

                //check if we're running out of visible area
                if (ud.size() >= maxWidth) {
                    ///remove the first element of data
                    ud.remove(0);
                }

                g2.clearRect(0, 0, image.getWidth(null), image.getHeight(null));

                //copy backImage to components Image
                g2.drawImage(backImage, 0, 0, image.getWidth(null), image.getHeight(null)
                        , 0, 0, backImage.getWidth(), backImage.getHeight(), Color.BLACK, null);

                //g.drawImage(backImage, 0, 0, image.getWidth(null), image.getHeight(null), new Color(0,0,0), null);

                //copy legendImage to components Image

                drawLabel();
                drawLines();
                drawGraph();

                g.drawImage(image, 0, 0, null);
                g.drawImage(legendImage, legendX, legendY, new Color(0, 0, 0, legendAlpha), null);
            } catch (Exception e) {
                logger.error("Error drawing graphics :" + e.getMessage() + "\r\n" + e.getCause());
            }
        } else {
            initializeImages();
            super.paintComponent(g);
        }
    }

    /**
     *
     */
    private void drawGraph() {
        Graphics2D g = (Graphics2D) image.getGraphics();
        //get first time-stamp
        int lastDown = 0;
        int lastUp = 0;
        long lastTimeLabel = ud.get(0).getTime();
        double lAverageUp = 0.0;
        double lAverageDown = 0.0;

        maxSpeed = 0;

        do {
            maxSpeed += 10;
        } while (ud.getMaxUp() > maxSpeed * 1024);

        do {
            maxSpeed += 10;
        } while (ud.getMaxDown() > maxSpeed * 1024);

        //draw upload rate-graph
        for (int i = 0; i < ud.size(); i++) {
            int up = (int) (ud.get(i).getUp() * bottomY / maxSpeed / 1024.0);
            int down = (int) (ud.get(i).getDown() * bottomY / maxSpeed / 1024.0);
            int xPos = i;

            lAverageUp += ud.get(i).getUp();
            lAverageDown += ud.get(i).getDown();

            //System.out.print("upspeed = " + upSpeed.doubleValue() + " up = " + up + "\r\n");
//			if ((ud.get(i).getUp() > maxSpeed*1024) || (ud.get(i).getDown() > maxSpeed*1024)) {
//				maxSpeed += 10;
//				//forceResize =true;
//				repaint();
//				return;
//			}
            g.setColor(upCol);
            g.drawLine(31 + xPos, bottomY - lastUp, xPos + 32, bottomY - up);
            g.setColor(downCol);
            g.drawLine(31 + xPos, bottomY - lastDown, xPos + 32, bottomY - down);

            if (i % 50 == 0) {
                lastTimeLabel = ud.get(i).getTime();
                drawTime(xPos + 31, ud.get(i).getTime());
            }

            lastUp = up;
            lastDown = down;
        }

        Stroke s = new BasicStroke(1, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 0, new float[]{6, 6, 6, 6, 6, 6, 0, 0}, 0);

        NumberFormat nf = DecimalFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        drawMark(g, lAverageUp / ud.size(), nf, upCol, s);
        drawMark(g, lAverageDown / ud.size(), nf, downCol, s);

        s = new BasicStroke(1, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 0, new float[]{6, 6, 0, 0, 6, 6, 0, 0}, 0);


        //draw line for maximum Up/Download regarding the settings
        drawMark(g, AppleJuiceClient.getAjFassade().getCurrentAJSettings().getMaxUploadInKB() * 1024, nf, Color.GRAY, s);
        drawMark(g, AppleJuiceClient.getAjFassade().getCurrentAJSettings().getMaxDownloadInKB() * 1024, nf, Color.GRAY, s);

        double Average = lAverageUp / ud.size();
        drawMark(g, Average, nf, upCol.brighter(), s);

        Average = lAverageDown / ud.size();
        drawMark(g, Average, nf, downCol.brighter(), s);


        g.dispose();
    }

    /**
     * Draw horizontal Line as mark
     *
     * @param g        graphics to draw in
     * @param speedVal speed in Byte/s
     * @param nf       NumberFormat for text
     * @param col      Color to draw text and Line
     * @param stroke   Stroke to draw line with
     */
    private void drawMark(Graphics2D g, double speedVal, NumberFormat nf, Color col, Stroke stroke) {
        Double value = speedVal / 1024.0;

        g.setStroke(stroke);
        g.setColor(col.brighter());

        speedVal = bottomY - (speedVal * bottomY / maxSpeed / 1024.0);
        g.drawLine(31, (int) speedVal, image.getWidth(null), (int) speedVal);
        g.setColor(col.brighter());
        String outText = nf.format(value);

        g.drawString(outText, image.getWidth(null) - g.getFontMetrics().stringWidth(outText) - 10, (int) speedVal - 10);
    }

    /**
     *
     */
    private void drawLabel() {

//		try {
//			Graphics2D g = (Graphics2D)image.getGraphics();
//			FontMetrics fm = g.getFontMetrics();
//			
//			//setup format of outtext
//			NumberFormat nf = NumberFormat.getInstance();
//			nf.setMinimumIntegerDigits(3);
//			
//			//first clear old text - fill with gradient
//			//g.setPaint(getGradient(gradientDirection,image.getHeight(null),30));
//			//g.fillRect(0, 0, 30, image.getHeight(null));
//			
//			//setup font
//			g.setColor(Color.WHITE);
//			int strWidth = fm.stringWidth ( "XXX -" );
//			int strHeight = fm.getAscent();
//			g.setFont(new Font(g.getFont().getName(), Font.PLAIN, g.getFont().getSize()));
//			g.drawString("kb/s", 30-g.getFontMetrics().stringWidth("kb/s "), image.getHeight(null)-strHeight);
//			
//			
//			//draw text for speed
//			for (int i=0;i<(maxSpeed/10)+1;i++){
//				//calculate y-position
//				int yPos = (int)(bottomY-((bottomY - topY - strHeight*1.0)/( maxSpeed/10)) * i);
//				
//				//set color and draw text for speed
//				g.drawString(nf.format(i*10), 30-strWidth, yPos+strHeight/2);
//			}
//			g.setColor(lineCol);
//			g.drawLine(30,topY,30,bottomY);
//			g.dispose();
//		}
//		catch (Exception e) {
//			if (logger.isEnabled(Level.ERROR))
//				logger.error("Error drawing labels :" + e.getMessage() + "\r\n" + e.getCause());
//		}
    }

    /**
     *
     */
    public void drawLegend(int alpha) {
        try {
            Graphics2D g = (Graphics2D) legendImage.getGraphics();

            g.setFont(new Font(g.getFont().getName(), Font.BOLD, g.getFont().getSize()));
            int strHeight = g.getFontMetrics().getAscent();

            //fill transparent area
            g.setColor(new Color(0, 0, 0, 255));
            g.fillRect(0, 0, legendWidth, legendHeight);

            //draw rounded frame around Legend
            g.setColor(new Color(0, 0, 0, alpha));
            g.fillRoundRect(1, 1, legendWidth - 2, legendHeight - 2, 7, 7);
            g.setColor(lineCol);
            g.drawRoundRect(1, 1, legendWidth - 2, legendHeight - 2, 7, 7);

            //now draw text
            g.setColor(Color.WHITE);
            g.drawString("Download", 32, strHeight + 3);
            g.drawString("Upload", 132, strHeight + 3);

            //and the lines before the text
            g.setStroke(new BasicStroke((float) 2.0));
            g.setColor(downCol);
            g.drawLine(10, (int) (strHeight * 1), 25, (int) (strHeight * 1));

            g.setColor(upCol);
            g.drawLine(110, (int) (strHeight * 1), 125, (int) (strHeight * 1));
            g.dispose();
        } catch (Exception e) {
            logger.error("Error painting legend :" + e.getMessage() + "\r\n" + e.getCause());
        }
    }

    /**
     *
     */
    public void drawBackground() {
        try {
            Graphics2D g = (Graphics2D) backImage.getGraphics();
            if (gradientDirection != BG_NO_GRADIENT) {
                g.setPaint(getGradient(gradientDirection, backImage.getHeight(), backImage.getWidth()));
            } else
                g.setColor(getBGStart());

            g.fillRect(0, 0, backImage.getWidth(), backImage.getHeight());
            g.dispose();
        } catch (Exception e) {
            logger.error("Error painting background :" + e.getMessage() + "\r\n" + e.getCause());
        }
    }

    /**
     *
     */
    private void drawLines() {
        try {
            int divider = 5;
            //setup Scaling for horizontal speedmarks (10 / 5 kBit/s)
            //everything above 50 kBit is 10kBit/s or 5kBit/s if below 50kBit.
            if ((ud.getMaxDown() / 1024 > 50) || (ud.getMaxUp() / 1024 > 50)) {
                divider = 10;
            }
            int yStep = maxSpeed / divider;

            Graphics2D g = (Graphics2D) image.getGraphics();

            FontMetrics fm = g.getFontMetrics();

            //setup format of outtext
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMinimumIntegerDigits(3);

            //setup font
            int strWidth = fm.stringWidth("XXX -");
            int strHeight = fm.getAscent();
            g.setFont(new Font(g.getFont().getName(), Font.PLAIN, g.getFont().getSize()));
            g.drawString("kb/s", 30 - g.getFontMetrics().stringWidth("kb/s "), image.getHeight(null) - strHeight);

            for (int i = 0; i < (yStep) + 1; i++) {
                //calculate y-position
                int yPos = bottomY - ((bottomY - topY) / (yStep)) * i;

                g.setColor(lineCol);
                //set Linestyle to dashed if line is between numbers of 10th
                if ((i * divider % 10) != 0) {
                    float[] dash = {(float) 5.0, (float) 2.0};
                    g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND, 0, new float[]{0, 0, 0, 6, 0, 0, 0, 6}, 0));
                } else
                    g.setStroke(new BasicStroke());


                //draw horizontal line
                g.drawLine(29, yPos, getWidth() - 1, yPos);

                //set color and draw text for speed
                g.drawString(nf.format(i * divider), 30 - strWidth, yPos + strHeight / 2);
            }
            //draw vertical Line
            g.setColor(lineCol);
            g.setStroke(new BasicStroke());
            g.drawLine(30, topY, 30, bottomY);

            g.dispose();
        } catch (Exception e) {
            logger.error("Error drawing lines :" + e.getMessage() + "\r\n" + e.getCause());
        }

    }

    /**
     *
     */
    private void drawTime(int aktXPos, long time) {
        try {
            Graphics2D g = (Graphics2D) image.getGraphics();

            g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), 10));
            int strHeight = g.getFontMetrics().getAscent();

            String actualTime = new SimpleDateFormat("HH:mm").format(new Date(time));
            int strWidth = g.getFontMetrics().stringWidth(actualTime);
            g.drawString(actualTime, aktXPos, bottomY + strHeight + 3);

            g.setColor(Color.RED);
            float[] dash = {(float) 5.0, (float) 2.0};
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND, 0, new float[]{0, 0, 0, 6, 0, 0, 0, 6}, 0));
            g.drawLine(aktXPos, topY - 2, aktXPos, bottomY - 2);
            g.dispose();
        } catch (Exception e) {
            logger.error("Error drawing time :" + e.getMessage() + "\r\n" + e.getCause());
        }

    }

    private void readProperies() {
        try {
            upCol = parent.getPropertyColor("UploadColor", Color.red);
            downCol = parent.getPropertyColor("DownloadColor", Color.blue);

            BGStart = parent.getPropertyColor("GradientStart", Color.white);
            BGEnd = parent.getPropertyColor("GradientEnd", Color.darkGray);

            if (!Boolean.parseBoolean(parent.getProperties().getProperty("UseGradient")))
                gradientDirection = BG_NO_GRADIENT;
            else
                gradientDirection = Integer.parseInt(parent.getProperties().getProperty("GradientDirection"));

            updatePeriod = Integer.parseInt(parent.getProperties().getProperty("UpdateTime", "2000"));
            try {
                timerThread.schedule(chartTimer, 500, updatePeriod);
            } catch (IllegalStateException | IllegalMonitorStateException e) {
                System.err.println("Not owner - no wait();");
            } finally {
                //timerThread.schedule(chartTimer, 500, updatePeriod);
            }

            parent.getProperties().put("changed", "false");
            parent.saveProperties();
        } catch (Exception e) {
            logger.error("Error reading Properties :" + e.getMessage() + "\r\n" + e.getCause());
        }
    }

    /**
     *
     */
    private void initializeImages() {
        try {
            if (image == null) {
                try {
                    if (getWidth() == 0) {
                        return;
                    }
                    if (getHeight() < minChartHeight) {
                        return;
                    }

                    bottomY = getHeight() - bottomHeight;
                    maxWidth = getWidth() - 31;

                    image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                    //System.out.print("new image("+ image.getWidth(null)+ "," + image.getHeight(null) +")\r\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (legendImage == null) {
                try {
                    legendImage = new BufferedImage(legendWidth, legendHeight, BufferedImage.TYPE_INT_ARGB);
                    drawLegend(legendAlpha);
                    //System.out.print("new legendImage("+ legendImage.getWidth(null)+ "," + legendImage.getHeight(null) +")\r\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (backImage == null) {
                try {
                    switch (gradientDirection) {
                        case UpDownChart.BG_DOWN_TO_UP:
                        case UpDownChart.BG_UP_TO_DOWN:
                            backImage = new BufferedImage(10, getHeight() - topY, BufferedImage.TYPE_INT_ARGB);
                            break;
                        case UpDownChart.BG_LEFT_TO_RIGHT:
                        case UpDownChart.BG_RIGHT_TO_LEFT:
                            backImage = new BufferedImage(getWidth() - 31, 10, BufferedImage.TYPE_INT_ARGB);
                            break;
                        default:
                            backImage = new BufferedImage(10, getHeight() - topY, BufferedImage.TYPE_INT_ARGB);
                            break;
                    }
                    //System.out.print("new backImage("+ backImage.getWidth(null)+ "," + backImage.getHeight(null) +")\r\n");
                    drawBackground();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            logger.error("Error creating Image :" + e.getMessage() + "\r\n" + e.getCause());
        }
    }

    /**
     * Generates a gradient in the selected direction of the given <code>BGStart</code> and <code>BGEnd</code> colors.
     * The direction may either be one of the following:<br>
     * <code>BG_LEFT_TO_RIGHT</code><br>
     * <code>BG_RIGHT_TO_LEFT</code><br>
     * <code>BG_UP_TO_DOWN</code><br>
     * <code>BG_DOWN_TO_UP</code><br>
     * <code>BG_UPLEFT_TO_DOWNRIGHT</code><br>
     * <code>BG_DOWNRIGHT_TO_UPLEFT</code><br>
     * <code>BG_UPRIGHT_TO_DOWNLEFT</code><br>
     * <code>BG_DOWNLEFT_TO_UPRIGHT</code><br>
     *
     * @param direction: see above
     * @param height:    height of the gradient
     * @param width:     width of the gradient
     * @return the <code>GradientPaint</code> of the selected direction and size
     */
    private static GradientPaint getGradient(int direction, int height, int width) {
        int x1 = 0;
        int x2 = 0;
        int y1 = 0;
        int y2 = 0;
        switch (direction) {
            case BG_UP_TO_DOWN:
                x1 = 1;
                x2 = 1;
                y1 = height;
                y2 = 0;
                break;
            case BG_DOWN_TO_UP:
                x1 = 1;
                x2 = 1;
                y1 = 0;
                y2 = height;
                break;
            case BG_LEFT_TO_RIGHT:
                x1 = 0;
                x2 = width;
                y1 = 1;
                y2 = 1;
                break;
            case BG_RIGHT_TO_LEFT:
                x1 = width;
                x2 = 0;
                y1 = 1;
                y2 = 1;
                break;
            case BG_UPLEFT_TO_DOWNRIGHT:
                x1 = 0;
                x2 = width;
                y1 = height;
                y2 = 0;
                break;
            case BG_UPRIGHT_TO_DOWNLEFT:
                x1 = width;
                x2 = 0;
                y1 = height;
                y2 = 0;
                break;
            case BG_DOWNLEFT_TO_UPRIGHT:
                x1 = 0;
                x2 = width;
                y1 = 0;
                y2 = height;
                break;
            case BG_DOWNRIGHT_TO_UPLEFT:
                x1 = width;
                x2 = 0;
                y1 = 0;
                y2 = height;
                break;
            case BG_NO_GRADIENT:
                x1 = 0;
                x2 = 0;
                y1 = 0;
                y2 = 0;
                return null;
        }
        return new GradientPaint(x1, y1, BGStart, x2, y2, BGEnd);
    }


    /**
     * @return Returns the bGEnd.
     */
    public Color getBGEnd() {
        return BGEnd;
    }

    /**
     * @param end The bGEnd to set.
     */
    public void setBGEnd(Color end) {
        BGEnd = end;
    }

    /**
     * @return Returns the bGStart.
     */
    public Color getBGStart() {
        return BGStart;
    }

    /**
     * @param start The bGStart to set.
     */
    public void setBGStart(Color start) {
        BGStart = start;
    }

    /**
     * @return Returns the downCol.
     */
    public Color getDownCol() {
        return downCol;
    }

    /**
     * @param downCol The downCol to set.
     */
    public void setDownCol(Color downCol) {
        this.downCol = downCol;
    }

    /**
     * @return Returns the lineCol.
     */
    public Color getLineCol() {
        return lineCol;
    }

    /**
     * @param lineCol The lineCol to set.
     */
    public void setLineCol(Color lineCol) {
        this.lineCol = lineCol;
    }

    /**
     * @return Returns the maxSpeed.
     */
    public int getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * @param maxSpeed The maxSpeed to set.
     */
    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /**
     * @return Returns the image.
     */
    public Image getImage() {
        return image;
    }

    /**
     * @return Returns the maxWidth.
     */
    public int getMaxWidth() {
        return maxWidth;
    }

    /**
     * @param maxWidth The maxWidth to set.
     */
    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    /**
     * @return Returns the upCol.
     */
    public Color getUpCol() {
        return upCol;
    }

    /**
     * @param upCol The upCol to set.
     */
    public void setUpCol(Color upCol) {
        this.upCol = upCol;
    }

    /**
     * @return Returns the gradientDirection.
     */
    public int getGradientDirection() {
        return gradientDirection;
    }

    /**
     * @param gradientDirection The gradientDirection to set.
     */
    public void setGradientDirection(int gradientDirection) {
        UpDownChart.gradientDirection = gradientDirection;
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println(evt.getPropertyName() + ":" + evt.getOldValue() + "->" + evt.getNewValue());
    }


}
