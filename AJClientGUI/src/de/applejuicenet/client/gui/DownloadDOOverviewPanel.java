package de.applejuicenet.client.gui;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/DownloadDOOverviewPanel.java,v 1.11 2003/09/01 18:00:15 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadDOOverviewPanel.java,v $
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
import de.applejuicenet.client.gui.controller.OptionsManager;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.listener.DataUpdateListener;

import javax.swing.*;
import java.awt.*;

public class DownloadDOOverviewPanel extends JPanel implements LanguageListener, DataUpdateListener{
    private DownloadDO downloadDO = null;
    private JPanel actualDlOverviewTable = new JPanel();
    private JLabel actualDLDateiName = new JLabel();
    private JLabel label4 = new JLabel("Vorhanden");
    private JLabel label3 = new JLabel("Nicht vorhanden");
    private JLabel label2 = new JLabel("In Ordnung");
    private JLabel label1 = new JLabel("Überprüft");
    private Settings settings;

    public DownloadDOOverviewPanel() {
        init();
        settings = Settings.getSettings();
        LanguageSelector.getInstance().addLanguageListener(this);
        OptionsManager.getInstance().addSettingsListener(this);
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
        add(actualDlOverviewTable, BorderLayout.CENTER);
    }

    private void setPartListDO(PartListDO partListDO){
        final PartListDO tempPartList = partListDO;
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                actualDLDateiName.setText(downloadDO.getFilename() + " (" + downloadDO.getTemporaryFileNumber() + ".data)");
                remove(actualDlOverviewTable);
                actualDlOverviewTable = new JPanel(new GridBagLayout());
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.NORTHWEST;
                constraints.fill = GridBagConstraints.BOTH;
                constraints.gridx = 0;
                constraints.gridy = 0;
                constraints.weightx = 1;
                JLabel label1 = null;
                PartListDO.Part[] parts = tempPartList.getParts();
                int anzahl = 5632;
                int anzahlZeile = 512;
                int anzahlZeilen = 11;
                if (tempPartList.getGroesse()<513){
                    anzahl = (int) tempPartList.getGroesse();
                    anzahlZeile = anzahl;
                    anzahlZeilen = 1;
                }
                else if (tempPartList.getGroesse()<5633){
                    anzahl = (int)tempPartList.getGroesse() / anzahlZeilen * anzahlZeilen;
                    anzahlZeile = anzahl/anzahlZeilen;
                }
                constraints.gridwidth = anzahlZeile;
                actualDlOverviewTable.add(actualDLDateiName, constraints);
                constraints.weighty = 1;
                constraints.gridwidth = 1;
                int groesseProPart = (int) tempPartList.getGroesse() / anzahl;
                long position = 0;
                int partPos = 0;
                long mbStart;
                long mbEnde;
                int kleiner;
                int groesstes;
                boolean ueberprueft;
                for (int i=0; i<anzahlZeilen; i++){
                    constraints.gridy = i+1;
                    for (int x=0; x<anzahlZeile; x++){
                        constraints.gridx = x;
                        position += groesseProPart;
                        while (parts[partPos].getFromPosition()<position && partPos<parts.length-1){
                            partPos++;
                        }
                        label1 = new JLabel();
                        label1.setOpaque(true);
                        if (parts[partPos].getType()==-1){
                            mbStart = position / 1048576 * 1048576;
                            mbEnde = mbStart + 1048576;
                            kleiner = partPos;
                            groesstes = partPos;
                            while (parts[kleiner].getFromPosition()>mbStart){
                                kleiner--;
                            }
                            while (parts[groesstes].getFromPosition()<mbEnde){
                                groesstes++;
                            }
                            groesstes--;
                            ueberprueft = true;
                            for (int l = kleiner; l<=groesstes; l++){
                                if (parts[l].getType()!=-1){
                                    ueberprueft = false;
                                    break;
                                }
                            }
                            if (ueberprueft){
                                label1.setBackground(PartListDO.COLOR_TYPE_UEBERPRUEFT);
                            }
                            else{
                                label1.setBackground(PartListDO.COLOR_TYPE_OK);
                            }
                        }
                        else{
                            label1.setBackground(getColorByType(parts[partPos].getType()));
                        }
                        actualDlOverviewTable.add(label1, constraints);
                    }
                }
                add(actualDlOverviewTable, BorderLayout.CENTER);
            }
        });
    }

    public void setDownloadDO(DownloadDO downloadDO) {
        if (!settings.isDownloadUebersicht()){
            remove(actualDlOverviewTable);
        }
        else if (this.downloadDO != downloadDO && downloadDO.getStatus()!=DownloadDO.FERTIGSTELLEN &&
                downloadDO.getStatus()!=DownloadDO.FERTIG)
        {
            this.downloadDO = downloadDO;
            final DownloadDO tempDO = downloadDO;
            final SwingWorker worker2 = new SwingWorker() {
                        public Object construct() {
                            PartListDO partListDO = ApplejuiceFassade.getInstance().getDownloadPartList(tempDO);
                            return partListDO;
                        }
                        public void finished(){
                            setPartListDO((PartListDO)getValue());
                        }
                    };
            worker2.start();
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

    public void fireLanguageChanged() {
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

    public void fireContentChanged(int type, Object content) {
        if (type == DataUpdateListener.SETTINGS_CHANGED){
            settings = (Settings) content;
        }
    }
}
