package de.applejuicenet.client.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import java.awt.*;
import javax.swing.table.TableColumn;
import de.applejuicenet.client.gui.tablerenderer.JTreeTable;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import java.util.HashSet;
import de.applejuicenet.client.shared.Version;
import de.applejuicenet.client.gui.tablerenderer.DownloadModel;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.exception.*;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import de.applejuicenet.client.gui.tablerenderer.DownloadTableCellRenderer;
import javax.swing.SwingUtilities;
import de.applejuicenet.client.gui.tablerenderer.TreeTableModelAdapter;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class DownloadPanel extends JPanel implements LanguageListener, RegisterI{
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
    tc.setCellRenderer(new DownloadTableCellRenderer());


    downloadTable.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e){
        Point p = e.getPoint();
        if (e.getClickCount()==2 && downloadTable.columnAtPoint(p)!=0){
          TreeTableModelAdapter model = (TreeTableModelAdapter)downloadTable.getModel();
          int selectedRow = downloadTable.getSelectedRow();
          ((TreeTableModelAdapter)downloadTable.getModel()).expandOrCollapseRow(selectedRow);
        }
      }

      public void mousePressed(MouseEvent me){
        Point p = me.getPoint();
        int iRow = downloadTable.rowAtPoint(p);
        int iCol = downloadTable.columnAtPoint(p);
        downloadTable.setRowSelectionInterval(iRow, iRow);
        downloadTable.setColumnSelectionInterval(iCol, iCol);
      }

      public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
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
  }

  public void registerSelected(){

  }

  public void fireLanguageChanged(){
      LanguageSelector languageSelector = LanguageSelector.getInstance();
      String text = languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "Label14", "caption"});
      linkLabel.setText(ZeichenErsetzer.korrigiereUmlaute(text));
      btnStartDownload.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "downlajfsp", "caption"})));
      btnStartDownload.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "downlajfsp", "hint"})));
      String[] tableColumns = new String[10];
      tableColumns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col0caption"}));
      tableColumns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col1caption"}));
      tableColumns[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col2caption"}));
      tableColumns[3] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col3caption"}));
      tableColumns[4] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col4caption"}));
      tableColumns[5] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col5caption"}));
      tableColumns[6] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col6caption"}));
      tableColumns[7] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col7caption"}));
      tableColumns[8] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col8caption"}));
      tableColumns[9] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col9caption"}));

      TableColumnModel tcm = downloadTable.getColumnModel();
      for (int i=0; i<tcm.getColumnCount(); i++){
        tcm.getColumn(i).setHeaderValue(tableColumns[i]);
      }

      label4.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "Label4", "caption"})));
      label3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "Label3", "caption"})));
      label2.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "Label2", "caption"})));
      label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "Label1", "caption"})));

      item1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "canceldown" ,"caption"})));
      String temp = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "pausedown" ,"caption"}));
      temp+="/" + ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "resumedown" ,"caption"}));
      item2.setText(temp);
      item3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "powerdownload" ,"caption"})));
      item4.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "renamefile" ,"caption"})));
      item5.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "changetarget" ,"caption"})));
      item6.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "Clearfinishedentries1" ,"caption"})));
  }
}