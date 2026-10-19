/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download;

import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.entity.Download;
import de.applejuicenet.client.facade.entity.DownloadSource;
import de.applejuicenet.client.facade.entity.Part;
import de.applejuicenet.client.facade.entity.PartList;
import de.applejuicenet.client.facade.shared.CharacterReplacer;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 *
 * Copyright: General Public License
 *
 */
public class DownloadPartListPanel extends JPanel implements MouseMotionListener, LanguageListener {
    private static DownloadPartListPanel instance = null;
    private PartList partList;
    private Logger logger;
    private BufferedImage image = null;
    private int width;
    private int height;
    private long finishedSince = -1;
    private boolean miniFile = false;
    private String checked;
    private String notPresent;
    private String present;
    private String sourcesText;
    private String transferText;
    private MouseEvent savedMouseEvent = null;
    private Integer id = null;
    private int rowHeight;
    private long pixelSize;
    private BufferedImage lineImage;

    private DownloadPartListPanel() {
        super(new BorderLayout());
        logger = LoggerFactory.getLogger(getClass());
        addMouseMotionListener(this);
        LanguageSelector.getInstance().addLanguageListener(this);
    }

    public static synchronized DownloadPartListPanel getInstance() {
        if (instance == null) {
            instance = new DownloadPartListPanel();
        }

        return instance;
    }

    public void paintComponent(Graphics g) {
        if (partList != null && image != null) {
            if (height != getSize().height || width != getSize().width) {
                setPartList(partList, id);
            }

            g.setColor(getBackground());
            g.fillRect(0, 0, width, height);
            if (image != null) {
                g.drawImage(image, 0, 0, null);
            }
        } else {
            super.paintComponent(g);
        }
    }

    private void updatePanel() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                repaint();
            }
        });
    }

    public synchronized void setPartList(PartList newPartList, Integer newId) {
        try {
            if (newPartList == null || newId == null) {
                partList = null;
                lineImage = null;
                image = null;
                savedMouseEvent = null;
                updatePanel();
                return;
            }

            boolean idChanged = false;

            if (id == null) {
                idChanged = true;
                id = newId;
            }

            if (id.longValue() != newId.longValue()) {
                idChanged = true;
            }

            boolean redraw = false;

            if (idChanged) {
                id = newId;
                partList = null;
            } else if (partList != null && newPartList != null) {
                if (height != getSize().height || width != getSize().width) {
                    redraw = true;
                }

                if (!redraw) {
                    Part[] parts = partList.getParts();
                    Part[] newParts = newPartList.getParts();

                    if (parts.length == newParts.length) {
                        boolean sameParts = true;

                        for (int i = 0; i < parts.length; i++) {
                            if (!parts[i].equals(newParts[i])) {
                                sameParts = false;
                                break;
                            }
                        }

                        if (sameParts) {
                            insertSources(partList, lineImage);
                            updatePanel();
                            return;
                        }
                    }
                }
            }

            partList = newPartList;
            height = getSize().height;
            width = getSize().width;
            if (partList != null && width > 0 && height >= 15 && partList.getSize() > 0 && partList.getParts().length > 0) {
                Part[] parts = partList.getParts();

                rowHeight = 15;
                int rows = height / rowHeight;

                miniFile = false;
                pixelSize = partList.getSize() / ((long) rows * width);
                if (pixelSize == 0) {
                    pixelSize = ((long) rows * width) / partList.getSize();
                    miniFile = true;
                }

                lineImage = new BufferedImage(width * rows, 15, BufferedImage.TYPE_INT_ARGB);
                Graphics graphics = lineImage.getGraphics();
                int topLeft = 0;
                int rectWidth = 0;

                finishedSince = -1;
                for (int i = 0; i < parts.length - 1; i++) {
                    drawPart(false, (partList.getPartListType() == PartList.MAIN_PARTLIST), graphics, pixelSize, parts[i].getType(),
                            rowHeight, parts[i].getFromPosition(), parts[i + 1].getFromPosition());
                }

                drawPart(true, (partList.getPartListType() == PartList.MAIN_PARTLIST), graphics, pixelSize,
                        parts[parts.length - 1].getType(), rowHeight, parts[parts.length - 1].getFromPosition(),
                        partList.getSize());
                BufferedImage imgWithSources = insertSources(partList, lineImage);

                if (savedMouseEvent != null) {
                    processMouseMotionEvent(savedMouseEvent);
                }
            } else {
                image = null;
                savedMouseEvent = null;
            }
        } catch (Exception e) {
            logger.debug(ApplejuiceFacade.ERROR_MESSAGE, e);
            partList = null;
            lineImage = null;
            image = null;
            savedMouseEvent = null;
        }

        updatePanel();
    }

    private BufferedImage insertSources(PartList partList, BufferedImage sourceImage) {
        BufferedImage imageWithSources = new BufferedImage(sourceImage.getWidth(), 15, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = imageWithSources.getGraphics();

        graphics.drawImage(sourceImage.getSubimage(0, 0, sourceImage.getWidth(), rowHeight), 0, 0, null);
        int topLeft;
        int rectWidth;

        if (partList.getPartListType() == PartList.MAIN_PARTLIST) {
            Download download = (Download) partList.getValueObject();

            if (download.getStatus() == Download.SEARCHING_LOADING) {
                for (DownloadSource curSource : download.getSources()) {
                    if (curSource.getStatus() == DownloadSource.TRANSFER) {
                        if (!miniFile) {
                            topLeft = (int) (curSource.getDownloadFrom() / pixelSize);
                            rectWidth = (int) (curSource.getDownloadTo() / pixelSize) - topLeft;
                        } else {
                            topLeft = (int) (curSource.getDownloadFrom() * pixelSize);
                            rectWidth = (int) (curSource.getDownloadTo() * pixelSize) - topLeft;
                        }

                        graphics.setColor(getColorByPercent(curSource.getReadyPercent()));
                        graphics.fillRect(topLeft, 0, rectWidth, rowHeight);
                    }
                }
            }
        } else {
            DownloadSource downloadSource = (DownloadSource) partList.getValueObject();

            if (downloadSource.getStatus() == DownloadSource.TRANSFER) {
                if (!miniFile) {
                    topLeft = (int) (downloadSource.getDownloadFrom() / pixelSize);
                    rectWidth = (int) (downloadSource.getDownloadTo() / pixelSize) - topLeft;
                } else {
                    topLeft = (int) (downloadSource.getDownloadFrom() * pixelSize);
                    rectWidth = (int) (downloadSource.getDownloadTo() * pixelSize) - topLeft;
                }

                graphics.setColor(getColorByPercent(downloadSource.getReadyPercent()));
                graphics.fillRect(topLeft, 0, rectWidth, rowHeight);
            }
        }

        image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = image.getGraphics();
        int x = 0;
        int rows = height / rowHeight;

        for (int i = 0; i < rows; i++) {
            g.drawImage(imageWithSources.getSubimage(x, 0, width, rowHeight), 0, i * rowHeight, null);
            x += width;
        }

        return imageWithSources;
    }

    private void drawPart(boolean forceDraw, boolean isMainList, Graphics graphics, long pixelSize, int partType, int rowHeight,
                          long currentFrom, long nextFrom) {
        int topLeft = 0;
        int rectWidth = 0;

        if (isMainList) {
            if (partType == -1 && !forceDraw) {
                if (finishedSince != -1) {
                    return;
                } else {
                    finishedSince = currentFrom;
                }
            } else {
                if (finishedSince != -1) {
                    topLeft = (int) (finishedSince / pixelSize);
                    long mbCount = (currentFrom - finishedSince) / 1048576;

                    rectWidth = (int) (mbCount * 1048576 / pixelSize);
                    graphics.setColor(PartList.COLOR_TYPE_CHECKED);
                    graphics.fillRect(topLeft, 0, rectWidth, rowHeight);
                    topLeft += rectWidth;
                    rectWidth = (int) (currentFrom / pixelSize) - topLeft;
                    if (partType == -1 || forceDraw) {
                        graphics.setColor(PartList.COLOR_TYPE_CHECKED);
                    } else {
                        graphics.setColor(PartList.COLOR_TYPE_OK);
                    }

                    graphics.fillRect(topLeft, 0, rectWidth, rowHeight);
                    topLeft = (int) (currentFrom / pixelSize);
                    rectWidth = (int) (nextFrom / pixelSize) - topLeft;
                    graphics.setColor(getColorByType(partType));
                    graphics.fillRect(topLeft, 0, rectWidth, rowHeight);
                    finishedSince = -1;
                } else {
                    if (!miniFile) {
                        topLeft = (int) (currentFrom / pixelSize);
                        rectWidth = (int) (nextFrom / pixelSize) - topLeft;
                    } else {
                        topLeft = (int) (currentFrom * pixelSize);
                        rectWidth = (int) (nextFrom * pixelSize) - topLeft;
                    }

                    graphics.setColor(getColorByType(partType));
                    graphics.fillRect(topLeft, 0, rectWidth, rowHeight);
                }
            }
        } else {
            if (!miniFile) {
                topLeft = (int) (currentFrom / pixelSize);
                rectWidth = (int) (nextFrom / pixelSize) - topLeft;
            } else {
                topLeft = (int) (currentFrom * pixelSize);
                rectWidth = (int) (nextFrom * pixelSize) - topLeft;
            }

            graphics.setColor(getColorByType(partType));
            graphics.fillRect(topLeft, 0, rectWidth, rowHeight);
        }
    }

    private Color getColorByType(int type) {
        switch (type) {

            case -1:
                return PartList.COLOR_TYPE_OK;

            case 0:
                return PartList.COLOR_TYPE_0;

            case 1:
                return PartList.COLOR_TYPE_1;

            case 2:
                return PartList.COLOR_TYPE_2;

            case 3:
                return PartList.COLOR_TYPE_3;

            case 4:
                return PartList.COLOR_TYPE_4;

            case 5:
                return PartList.COLOR_TYPE_5;

            case 6:
                return PartList.COLOR_TYPE_6;

            case 7:
                return PartList.COLOR_TYPE_7;

            case 8:
                return PartList.COLOR_TYPE_8;

            case 9:
                return PartList.COLOR_TYPE_9;

            case 10:
                return PartList.COLOR_TYPE_10;

            default:
                return PartList.COLOR_TYPE_10;
        }
    }

    private Color getColorByPercent(double percent) {
        if (percent < 10) {
            return PartList.COLOR_READY_10;
        } else if (percent < 30) {
            return PartList.COLOR_READY_30;
        } else if (percent < 50) {
            return PartList.COLOR_READY_50;
        } else if (percent < 70) {
            return PartList.COLOR_READY_70;
        } else if (percent < 90) {
            return PartList.COLOR_READY_90;
        } else {
            return PartList.COLOR_READY_100;
        }
    }

    public void mouseDragged(MouseEvent mouseEvent) {
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        if (image != null) {
            savedMouseEvent = mouseEvent;
            Point p = mouseEvent.getPoint();

            try {
                int rgb = image.getRGB((int) p.getX(), (int) p.getY());

                if (rgb == PartList.COLOR_TYPE_CHECKED.getRGB()) {
                    setToolTipText(checked);
                } else if (rgb == PartList.COLOR_TYPE_0.getRGB()) {
                    setToolTipText(notPresent);
                } else if (rgb == PartList.COLOR_TYPE_OK.getRGB()) {
                    setToolTipText(present);
                } else if (rgb == PartList.COLOR_TYPE_1.getRGB()) {
                    setToolTipText("1" + sourcesText);
                } else if (rgb == PartList.COLOR_TYPE_2.getRGB()) {
                    setToolTipText("2" + sourcesText);
                } else if (rgb == PartList.COLOR_TYPE_3.getRGB()) {
                    setToolTipText("3" + sourcesText);
                } else if (rgb == PartList.COLOR_TYPE_4.getRGB()) {
                    setToolTipText("4" + sourcesText);
                } else if (rgb == PartList.COLOR_TYPE_5.getRGB()) {
                    setToolTipText("5" + sourcesText);
                } else if (rgb == PartList.COLOR_TYPE_6.getRGB()) {
                    setToolTipText("6" + sourcesText);
                } else if (rgb == PartList.COLOR_TYPE_7.getRGB()) {
                    setToolTipText("7" + sourcesText);
                } else if (rgb == PartList.COLOR_TYPE_8.getRGB()) {
                    setToolTipText("8" + sourcesText);
                } else if (rgb == PartList.COLOR_TYPE_9.getRGB()) {
                    setToolTipText("9" + sourcesText);
                } else if (rgb == PartList.COLOR_TYPE_10.getRGB()) {
                    setToolTipText("10+" + sourcesText);
                } else if (rgb == PartList.COLOR_READY_10.getRGB()) {
                    setToolTipText("0-10" + transferText);
                } else if (rgb == PartList.COLOR_READY_30.getRGB()) {
                    setToolTipText("10-30" + transferText);
                } else if (rgb == PartList.COLOR_READY_50.getRGB()) {
                    setToolTipText("30-50" + transferText);
                } else if (rgb == PartList.COLOR_READY_70.getRGB()) {
                    setToolTipText("50-70" + transferText);
                } else if (rgb == PartList.COLOR_READY_90.getRGB()) {
                    setToolTipText("70-90" + transferText);
                } else if (rgb == PartList.COLOR_READY_100.getRGB()) {
                    setToolTipText("90-100" + transferText);
                } else {
                    setToolTipText(null);
                }
            } catch (ArrayIndexOutOfBoundsException aoobE) {
                setToolTipText(null);
            }
        } else {
            setToolTipText(null);
        }
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();

            present = CharacterReplacer.fixUmlauts(languageSelector.getFirstAttributeByTagName("mainform.Label4.caption"));
            notPresent = CharacterReplacer.fixUmlauts(languageSelector.getFirstAttributeByTagName("mainform.Label3.caption"));
            checked = CharacterReplacer.fixUmlauts(languageSelector.getFirstAttributeByTagName("mainform.Label1.caption"));
            sourcesText = CharacterReplacer.fixUmlauts(languageSelector.getFirstAttributeByTagName("javagui.downloadform.sources"));
            transferText = CharacterReplacer.fixUmlauts(languageSelector.getFirstAttributeByTagName("javagui.downloadform.transferred"));
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }
}
