package de.applejuicenet.client.gui;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/DownloadDOOverviewPanel.java,v 1.16 2003/09/09 12:28:14 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadDOOverviewPanel.java,v $
 * Revision 1.16  2003/09/09 12:28:14  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.15  2003/09/04 10:13:49  maj0r
 * Logger eingebaut.
 *
 * Revision 1.14  2003/09/04 06:26:49  maj0r
 * Partlist korrigiert. Wird momentan beim Resize nicht neugezeichnet.
 *
 * Revision 1.13  2003/09/03 10:26:07  maj0r
 * NullPointer behoben und Logging eingefuehrt.
 *
 * Revision 1.12  2003/09/02 19:29:26  maj0r
 * Einige Stellen synchronisiert und Nullpointer behoben.
 * Version 0.21 beta.
 *
 * Revision 1.11  2003/09/01 18:00:15  maj0r
 * Wo es ging, DO auf primitiven Datentyp umgebaut.
 * Status "geprueft" eingefuehrt.
 *
 * Revision 1.10  2003/09/01 06:27:35  maj0r
 * Ueberarbeitet.
 *
 * Revision 1.9  2003/08/24 14:59:59  maj0r
 * Version 0.14
 * Diverse Aenderungen.
 *
 * Revision 1.8  2003/08/22 12:40:19  maj0r
 * Zeitaufwendiges Partliste holen in Thread ausgelagert.
 *
 * Revision 1.7  2003/08/16 17:49:55  maj0r
 * Diverse Farben können nun manuell eingestellt bzw. deaktiviert werden.
 * DownloaduebersichtTabelle kann deaktiviert werden.
 *
 * Revision 1.6  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.5  2003/08/12 16:23:36  maj0r
 * Kleine Layoutaenderung.
 *
 * Revision 1.4  2003/08/12 11:01:58  maj0r
 * Anzeige korrigiert.
 *
 * Revision 1.3  2003/08/11 18:19:43  maj0r
 * Korrektur: GridBagLayout kann nur 512 Components pro Zeile.
 *
 * Revision 1.2  2003/08/11 15:34:45  maj0r
 * Diverse Änderungen.
 *
 * Revision 1.1  2003/08/11 14:10:27  maj0r
 * DownloadPartList eingefügt.
 * Diverse Änderungen.
 *
 *
 */

import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.PartListDO;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.SwingWorker;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.listener.DataUpdateListener;

import javax.swing.*;
import java.awt.*;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public class DownloadDOOverviewPanel extends JPanel implements LanguageListener, DataUpdateListener{
    private DownloadDO downloadDO = null;
    private DownloadPartListPanel actualDlOverviewTable = new DownloadPartListPanel();
    private JLabel actualDLDateiName = new JLabel();
    private JLabel label4 = new JLabel("Vorhanden");
    private JLabel label3 = new JLabel("Nicht vorhanden");
    private JLabel label2 = new JLabel("In Ordnung");
    private JLabel label1 = new JLabel("Überprüft");
    private Settings settings;
    private Logger logger;

    public DownloadDOOverviewPanel() {
        logger = Logger.getLogger(getClass());
        try{
            init();
            settings = Settings.getSettings();
            LanguageSelector.getInstance().addLanguageListener(this);
            PropertiesManager.getOptionsManager().addSettingsListener(this);
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private void init() {
        setLayout(new BorderLayout());
        JPanel tempPanel1 = new JPanel();
        tempPanel1.setLayout(new FlowLayout());
        JLabel blau = new JLabel("     ");
        blau.setOpaque(true);
        blau.setBackground(Color.blue);
        tempPanel1.add(blau);
        tempPanel1.add(label4);

        JLabel red = new JLabel("     ");
        red.setOpaque(true);
        red.setBackground(Color.red);
        tempPanel1.add(red);
        tempPanel1.add(label3);

        JLabel black = new JLabel("     ");
        black.setOpaque(true);
        black.setBackground(Color.black);
        tempPanel1.add(black);
        tempPanel1.add(label2);

        JLabel green = new JLabel("     ");
        green.setOpaque(true);
        green.setBackground(Color.green);
        tempPanel1.add(green);
        tempPanel1.add(label1);

        add(tempPanel1, BorderLayout.NORTH);
        actualDLDateiName.setPreferredSize(new Dimension(actualDLDateiName.getPreferredSize().width, 17));
        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.add(actualDLDateiName, BorderLayout.NORTH);
        panel1.add(actualDlOverviewTable, BorderLayout.CENTER);
        add(panel1, BorderLayout.CENTER);
    }

    public void setDownloadDO(DownloadDO downloadDO) {
        try{
            if (!settings.isDownloadUebersicht() || downloadDO==null){
                actualDLDateiName.setText("");
                actualDlOverviewTable.setPartList(null);
            }
            else if (this.downloadDO != downloadDO && downloadDO.getStatus()!=DownloadDO.FERTIGSTELLEN &&
                    downloadDO.getStatus()!=DownloadDO.FERTIG)
            {
                this.downloadDO = downloadDO;
                final DownloadDO tempDO = downloadDO;
                final SwingWorker worker2 = new SwingWorker() {
                            public Object construct() {
                                return ApplejuiceFassade.getInstance().getDownloadPartList(tempDO);
                            }
                            public void finished(){
                                actualDLDateiName.setText(tempDO.getFilename() + " (" + tempDO.getTemporaryFileNumber() + ".data)");
                                actualDlOverviewTable.setPartList((PartListDO) getValue());
                            }
                        };
                worker2.start();
            }
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void fireLanguageChanged() {
        try{
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            label4.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                    getFirstAttrbuteByTagName(new String[]{"mainform", "Label4", "caption"})));
            label3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                    getFirstAttrbuteByTagName(new String[]{"mainform", "Label3", "caption"})));
            label2.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                    getFirstAttrbuteByTagName(new String[]{"mainform", "Label2", "caption"})));
            label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                    getFirstAttrbuteByTagName(new String[]{"mainform", "Label1", "caption"})));
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void fireContentChanged(int type, Object content) {
        if (type == DataUpdateListener.SETTINGS_CHANGED){
            settings = (Settings) content;
        }
    }
}
