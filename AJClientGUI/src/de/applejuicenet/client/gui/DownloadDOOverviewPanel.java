package de.applejuicenet.client.gui;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/DownloadDOOverviewPanel.java,v 1.2 2003/08/11 15:34:45 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadDOOverviewPanel.java,v $
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
import de.applejuicenet.client.gui.controller.DataManager;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;

import javax.swing.*;
import java.awt.*;

public class DownloadDOOverviewPanel extends JPanel implements LanguageListener{
    private DownloadDO downloadDO = null;
    private JPanel actualDlOverviewTable = new JPanel();
    private JLabel actualDLDateiName = new JLabel();
    private JLabel label4 = new JLabel("Vorhanden");
    private JLabel label3 = new JLabel("Nicht vorhanden");
    private JLabel label2 = new JLabel("In Ordnung");
    private JLabel label1 = new JLabel("Überprüft");

    public DownloadDOOverviewPanel() {
        init();
        LanguageSelector.getInstance().addLanguageListener(this);
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

        JPanel tempPanel2 = new JPanel(new BorderLayout());
        tempPanel2.add(tempPanel1, BorderLayout.NORTH);
        tempPanel2.add(actualDLDateiName, BorderLayout.SOUTH);
        add(tempPanel2, BorderLayout.NORTH);

        add(actualDlOverviewTable, BorderLayout.CENTER);
    }

    public void setDownloadDO(DownloadDO downloadDO) {
        if (this.downloadDO != downloadDO)
        {
            this.downloadDO = downloadDO;
            PartListDO partListDO = DataManager.getInstance().getDownloadPartList(downloadDO);
            actualDLDateiName.setText(downloadDO.getFilename() + " (" + downloadDO.getTemporaryFileNumber() + ".data)");
            remove(actualDlOverviewTable);
            actualDlOverviewTable = new JPanel(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.WEST;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.weightx = 1;
            constraints.weighty = 1;
            JLabel label1 = null;
            PartListDO.Part[] parts = partListDO.getParts();
            int anzahlParts = parts.length;
            int anzahlProZeile = anzahlParts / 10;
            int count = 0;
            for (int i = 0; i < 10; i++)
            {
                constraints.gridy = i;
                for (int x = 0; x < anzahlProZeile; x++)
                {
                    if (count<anzahlParts){
                        constraints.gridx = x;
                        label1 = new JLabel();
                        label1.setOpaque(true);
                        label1.setBackground(getColorByType(parts[count].getType()));
                        actualDlOverviewTable.add(label1, constraints);
                        count++;
                    }
                }
            }
            if (count < anzahlParts)
            {
                constraints.gridy = 10;
                count = 0;
                for (int i = anzahlProZeile * 10; i < anzahlParts; i++)
                {
                    constraints.gridx = count;
                    label1 = new JLabel();
                    label1.setOpaque(true);
                    label1.setBackground(getColorByType(parts[i].getType()));
                    actualDlOverviewTable.add(label1, constraints);
                    count++;
                }
            }
            add(actualDlOverviewTable, BorderLayout.CENTER);
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
}
