package de.applejuicenet.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.gui.tables.download.DownloadModel;
import de.applejuicenet.client.gui.tables.download.DownloadTableCellRenderer;
import de.applejuicenet.client.gui.tables.download.DownloadNode;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.gui.tables.JTreeTable;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/DownloadPanel.java,v 1.27 2003/07/04 06:43:51 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadPanel.java,v $
 * Revision 1.27  2003/07/04 06:43:51  maj0r
 * Diverse Änderungen am DownloadTableModel.
 *
 * Revision 1.26  2003/07/03 19:11:16  maj0r
 * DownloadTable überarbeitet.
 *
 * Revision 1.25  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 * Revision 1.24  2003/07/01 18:49:03  maj0r
 * Struktur verändert.
 *
 * Revision 1.23  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.22  2003/07/01 18:34:28  maj0r
 * Struktur verändert.
 *
 * Revision 1.21  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class DownloadPanel
    extends JPanel
    implements LanguageListener, RegisterI, DataUpdateListener {
  private JTextField downloadLink = new JTextField();
  private JButton btnStartDownload = new JButton("Download");
  private PowerDownloadPanel powerDownloadPanel = new PowerDownloadPanel();
  private JTreeTable downloadTable;
  private JTable actualDlOverviewTable = new JTable();
  private JLabel linkLabel = new JLabel("ajfsp-Link hinzufügen");
  private DownloadModel downloadModel;
  private JLabel label4 = new JLabel("Vorhanden");
  private JLabel label3 = new JLabel("Nicht vorhanden");
  private JLabel label2 = new JLabel("In Ordnung");
  private JLabel label1 = new JLabel("Überprüft");
  private JPopupMenu popup = new JPopupMenu();
  JMenuItem item1;
  JMenuItem item2;
  JMenuItem item3;
  JMenuItem item4;
  JMenuItem item5;
  JMenuItem item6;

  public DownloadPanel() {
    try {
      jbInit();
      LanguageSelector.getInstance().addLanguageListener(this);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    setLayout(new BorderLayout());
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new GridBagLayout());
    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new BorderLayout());

    item1 = new JMenuItem("Abbrechen");
    item2 = new JMenuItem("Pause/Fortsetzen");
    item3 = new JMenuItem("Powerdownload");
    item4 = new JMenuItem("Umbenennen");
    item5 = new JMenuItem("Zielordner ändern");
    item6 = new JMenuItem("Fertige Übertragungen entfernen");
    popup.add(item1);
    popup.add(item2);
    popup.add(item3);
    popup.add(item4);
    popup.add(item5);
    popup.add(item6);

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 3;
    constraints.gridheight = 1;
    JPanel tempPanel = new JPanel();
    tempPanel.setLayout(new BorderLayout());
    tempPanel.add(linkLabel, BorderLayout.WEST);
    tempPanel.add(downloadLink, BorderLayout.CENTER);
    tempPanel.add(btnStartDownload, BorderLayout.EAST);
    topPanel.add(tempPanel, constraints);
    constraints.gridwidth = 3;
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.weighty = 1;
    constraints.weightx = 1;

    downloadModel = new DownloadModel();
    downloadTable = new JTreeTable(downloadModel);

    TableColumn tc = downloadTable.getColumnModel().getColumn(9);
    TableColumn tc2 = downloadTable.getColumnModel().getColumn(6);
    DownloadTableCellRenderer renderer = new DownloadTableCellRenderer();
    tc.setCellRenderer(renderer);
    tc2.setCellRenderer(renderer);

    downloadTable.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        if (e.getClickCount() == 2 && downloadTable.columnAtPoint(p) != 0) {
          TreeTableModelAdapter model = (TreeTableModelAdapter) downloadTable.
              getModel();
          int selectedRow = downloadTable.getSelectedRow();
          ( (TreeTableModelAdapter) downloadTable.getModel()).expandOrCollapseRow(selectedRow);
        }
      }

      public void mousePressed(MouseEvent me) {
        Point p = me.getPoint();
        int iRow = downloadTable.rowAtPoint(p);
        int iCol = downloadTable.columnAtPoint(p);
        downloadTable.setRowSelectionInterval(iRow, iRow);
        downloadTable.setColumnSelectionInterval(iCol, iCol);
        maybeShowPopup(me);
      }

      public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        maybeShowPopup(e);
      }

      private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
          popup.show(downloadTable, e.getX(), e.getY());
        }
      }
    });

    JScrollPane aScrollPane = new JScrollPane();
    aScrollPane.getViewport().add(downloadTable);
    topPanel.add(aScrollPane, constraints);

    bottomPanel.add(powerDownloadPanel, BorderLayout.WEST);
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

    JPanel tempPanel2 = new JPanel();
    tempPanel2.setLayout(new BorderLayout());
    tempPanel2.add(tempPanel1, BorderLayout.NORTH);
    tempPanel2.add(actualDlOverviewTable, BorderLayout.CENTER);
    bottomPanel.add(tempPanel2, BorderLayout.CENTER);

    add(topPanel, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);
    DataManager.getInstance().addDataUpdateListener(this, DataUpdateListener.DOWNLOAD_CHANGED);
  }

  public void registerSelected() {
    //    nix zu tun
      //test
      Version version = new Version("0.01", Version.LINUX);
      DownloadDO download = new DownloadDO("12", "24", "kjhh", "387636455", DownloadDO.SUCHEN_LADEN, "test1.rar", "filme", 1);
      DownloadSourceDO source = new DownloadSourceDO("13", DownloadSourceDO.IN_WARTESCHLANGE, DownloadSourceDO.INDIREKTE_VERBINDUNG,
              new Integer(100), new Integer(300), new Integer(230), new Integer(243), version, 4, 1, "test1.rar", "nickname");
      download.addOrAlterSource(source);
      DownloadNode node = new DownloadNode(download);
      DownloadDO download2 = new DownloadDO("16", "14", "kjhh", "387635", DownloadDO.SUCHEN_LADEN, "test2.rar", "", 1);
      DownloadSourceDO source2 = new DownloadSourceDO("18", DownloadSourceDO.IN_WARTESCHLANGE, DownloadSourceDO.DIREKTE_VERBINDUNG,
              new Integer(100), new Integer(300), new Integer(230), new Integer(243), version, 4, 1, "test2.rar", "maj0r");
      download2.addOrAlterSource(source2);
      DownloadNode node2 = new DownloadNode(download2);
      downloadTable.updateUI();
  }

  public void fireLanguageChanged() {
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    String text = languageSelector.getFirstAttrbuteByTagName(new String[] {
        "mainform", "Label14", "caption"});
    linkLabel.setText(ZeichenErsetzer.korrigiereUmlaute(text));
    btnStartDownload.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "downlajfsp",
                                  "caption"})));
    btnStartDownload.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
        languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform",
        "downlajfsp", "hint"})));
    String[] tableColumns = new String[10];
    tableColumns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col0caption"}));
    tableColumns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col1caption"}));
    tableColumns[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col2caption"}));
    tableColumns[3] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col3caption"}));
    tableColumns[4] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col4caption"}));
    tableColumns[5] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col5caption"}));
    tableColumns[6] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col6caption"}));
    tableColumns[7] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col7caption"}));
    tableColumns[8] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col8caption"}));
    tableColumns[9] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col9caption"}));

    TableColumnModel tcm = downloadTable.getColumnModel();
    for (int i = 0; i < tcm.getColumnCount(); i++) {
      tcm.getColumn(i).setHeaderValue(tableColumns[i]);
    }

    label4.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "Label4", "caption"})));
    label3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "Label3", "caption"})));
    label2.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "Label2", "caption"})));
    label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "Label1", "caption"})));

    item1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "canceldown",
                                  "caption"})));
    String temp = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "pausedown",
                                  "caption"}));
    temp += "/" +
        ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                          getFirstAttrbuteByTagName(new String[] {
        "mainform", "resumedown", "caption"}));
    item2.setText(temp);
    item3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "powerdownload",
                                  "caption"})));
    item4.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "renamefile",
                                  "caption"})));
    item5.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "changetarget",
                                  "caption"})));
    item6.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform",
                                  "Clearfinishedentries1", "caption"})));
  }

    public void fireContentChanged(int type, Object content) {
        downloadTable.updateUI();
    }
}