package de.applejuicenet.client.gui;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/DownloadDOOverviewPanel.java,v 1.36 2004/10/11 18:18:51 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.dac.PartListDO;
import de.applejuicenet.client.shared.exception.WebSiteNotFoundException;

public class DownloadDOOverviewPanel
    extends JPanel
    implements LanguageListener{
    private static final long serialVersionUID = 6888218356284268994L;
	private DownloadPartListPanel actualDlOverviewTable;
    private JLabel actualDLDateiName = new JLabel();
    private JLabel label5 = new JLabel("aktive Übertragung");
    private JLabel label4 = new JLabel("Vorhanden");
    private JLabel label3 = new JLabel("Nicht vorhanden");
    private JLabel label2 = new JLabel("In Ordnung");
    private JLabel label1 = new JLabel("Überprüft");
    private Logger logger;
    private JButton holeListe = new JButton("Hole Partliste");
    private Thread partListWorkerThread = null;
    private DownloadPanel downloadPanel;

    public DownloadDOOverviewPanel(DownloadPanel parent) {
        logger = Logger.getLogger(getClass());
        try {
            downloadPanel = parent;
            actualDlOverviewTable = DownloadPartListPanel.getInstance();
            init();
            LanguageSelector.getInstance().addLanguageListener(this);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public void enableHoleListButton(boolean enable) {
        holeListe.setEnabled(enable);
    }

    private void init() {
        holeListe.setEnabled(false);
        setLayout(new BorderLayout());
        JPanel tempPanel1 = new JPanel();
        tempPanel1.setLayout(new FlowLayout());

        JLabel gelb = new JLabel("     ");
        gelb.setOpaque(true);
        gelb.setBackground(Color.YELLOW);
        tempPanel1.add(gelb);
        tempPanel1.add(label5);

        JLabel blau = new JLabel("     ");
        blau.setOpaque(true);
        blau.setBackground(Color.BLUE);
        tempPanel1.add(blau);
        tempPanel1.add(label4);

        JLabel red = new JLabel("     ");
        red.setOpaque(true);
        red.setBackground(Color.RED);
        tempPanel1.add(red);
        tempPanel1.add(label3);

        JLabel black = new JLabel("     ");
        black.setOpaque(true);
        black.setBackground(Color.BLACK);
        tempPanel1.add(black);
        tempPanel1.add(label2);

        JLabel green = new JLabel("     ");
        green.setOpaque(true);
        green.setBackground(Color.GREEN);
        tempPanel1.add(green);
        tempPanel1.add(label1);

        JPanel panel3 = new JPanel(new BorderLayout());
        panel3.add(holeListe, BorderLayout.WEST);
        panel3.add(tempPanel1, BorderLayout.CENTER);

        add(panel3, BorderLayout.NORTH);
        actualDLDateiName.setPreferredSize(new Dimension(actualDLDateiName.
            getPreferredSize().width, 17));
        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.add(actualDLDateiName, BorderLayout.NORTH);
        panel1.add(actualDlOverviewTable, BorderLayout.CENTER);
        add(panel1, BorderLayout.CENTER);
        holeListe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                holeListe.setEnabled(false);
                downloadPanel.tryGetPartList();
            }
        });
    }

    public void setDownloadDO(DownloadDO downloadDO) {
        try {
            if (downloadDO == null) {
                if (partListWorkerThread != null){
                    partListWorkerThread.interrupt();
                    partListWorkerThread = null;
                }
                actualDLDateiName.setText("");
                actualDlOverviewTable.setPartList(null);
            }
            else if (downloadDO.getStatus() != DownloadDO.FERTIGSTELLEN &&
                     downloadDO.getStatus() != DownloadDO.FERTIG) {
                final DownloadDO tempDO = downloadDO;
                if (partListWorkerThread != null){
                    partListWorkerThread.interrupt();
                }
                partListWorkerThread = new Thread() {
                    public void run() {
                        actualDLDateiName.setText(" " + tempDO.getFilename() +
                                                  " (" +
                                                  tempDO.getTemporaryFileNumber() +
                                                  ".data)");
                        actualDlOverviewTable.setPartList(null);
                        PartListDO partList = null;
                        while (!isInterrupted()) {
                            try{
                                partList = ApplejuiceFassade.getInstance().
                                    getPartList(tempDO);
                            }
                            catch(WebSiteNotFoundException wsnfE){
                                // Core ist wahrscheinlich zurzeit ueberlastet
                                partList = null;
                            }
                            if (isInterrupted()){
                                break;
                            }
                            if (partList == null) {
                                interrupt();
                                actualDLDateiName.setText("");
                                actualDlOverviewTable.setPartList(null);
                            }
                            else {
                                actualDlOverviewTable.setPartList(partList);
                                try {
                                    sleep(2000);
                                }
                                catch (InterruptedException iE) {
                                    interrupt();
                                }
                            }
                        }
                    }
                };
                partListWorkerThread.start();
            }
        }
        catch (Exception e) {
            if (partListWorkerThread != null){
                partListWorkerThread.interrupt();
                partListWorkerThread = null;
            }
            actualDLDateiName.setText("");
            actualDlOverviewTable.setPartList(null);
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public void setDownloadSourceDO(DownloadSourceDO downloadSourceDO) {
        try {
            if (downloadSourceDO == null) {
                if (partListWorkerThread != null){
                    partListWorkerThread.interrupt();
                    partListWorkerThread = null;
                }
                actualDLDateiName.setText("");
                actualDlOverviewTable.setPartList(null);
            }
            else {
                final DownloadSourceDO tempDO = downloadSourceDO;
                if (partListWorkerThread != null){
                    partListWorkerThread.interrupt();
                }
                partListWorkerThread = new Thread() {
                    public void run() {
                        actualDLDateiName.setText(tempDO.getFilename() + " (" +
                                                  tempDO.getNickname() + ")");
                        actualDlOverviewTable.setPartList(null);
                        PartListDO partList;
                        try {
                            partList = ApplejuiceFassade.getInstance().
                                getPartList(tempDO);
                        }
                        catch (WebSiteNotFoundException ex) {
                            // Core ist wahrscheinlich zurzeit ueberlastet
                            partList = null;
                        }
                        if (partList == null) {
                            interrupt();
                            actualDLDateiName.setText("");
                            actualDlOverviewTable.setPartList(null);
                        }
                        else {
                            actualDlOverviewTable.setPartList(partList);
                        }
                    }
                };
                partListWorkerThread.start();
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            label5.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.downloadform.aktiveuebertragung")));
            label4.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.Label4.caption")));
            label3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.Label3.caption")));
            label2.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.Label2.caption")));
            label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.Label1.caption")));
            holeListe.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.downloadform.partlisteanzeigen")));
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }
}
