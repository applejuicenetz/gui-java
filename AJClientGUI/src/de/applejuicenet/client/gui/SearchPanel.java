package de.applejuicenet.client.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.shared.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/SearchPanel.java,v 1.7 2003/06/10 12:31:03 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SearchPanel.java,v $
 * Revision 1.7  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class SearchPanel
    extends JPanel
    implements LanguageListener, RegisterI {
  JTable searchResultTable = new JTable();
  JButton btnStartStopSearch = new JButton("Suche starten");
  JTextField suchbegriff = new JTextField();
  int anzahlSuchanfragen = 0;
  private JLabel label1 = new JLabel("Suchbegriff: ");
  private JLabel label2 = new JLabel("0 Suchanfragen in Bearbeitung");

  public SearchPanel() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    setLayout(new BorderLayout());
    LanguageSelector.getInstance().addLanguageListener(this);
    JPanel panel3 = new JPanel();
    JPanel leftPanel = new JPanel();
    panel3.setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    panel3.add(label1, constraints);
    constraints.gridx = 1;
    panel3.add(suchbegriff, constraints);
    constraints.gridy = 1;
    panel3.add(btnStartStopSearch, constraints);
    JPanel panel2 = new JPanel();
    panel2.setLayout(new FlowLayout());
    panel2.add(label2);
    constraints.gridx = 0;
    constraints.gridy = 2;
    constraints.gridwidth = 2;
    panel3.add(panel2, constraints);
    leftPanel.setLayout(new BorderLayout());
    leftPanel.add(panel3, BorderLayout.NORTH);
    add(leftPanel, BorderLayout.WEST);

    searchResultTable.setModel(new SearchResultTableModel());
    JScrollPane aScrollPane = new JScrollPane();
    aScrollPane.getViewport().add(searchResultTable);
    add(aScrollPane, BorderLayout.CENTER);
  }

  public void registerSelected() {
  }

  public void fireLanguageChanged() {
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "searchlbl",
                                  "caption"})) + ": ");
    btnStartStopSearch.setText(ZeichenErsetzer.korrigiereUmlaute(
        languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "searchbtn",
                                  "searchcaption"})));

    String temp = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "opensearches",
                                  "caption"}));
    label2.setText(temp.replaceAll("%d", Integer.toString(anzahlSuchanfragen)));

    String[] columns = new String[6];
    columns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "searchs",
                                  "col0caption"}));
    columns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "searchs",
                                  "col1caption"}));
    columns[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "searchs",
                                  "col4caption"}));
    columns[3] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "searchs",
                                  "col5caption"}));
    columns[4] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "searchs",
                                  "col4caption"}));
    columns[5] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "searchs",
                                  "col5caption"}));

    TableColumnModel tcm = searchResultTable.getColumnModel();
    for (int i = 0; i < tcm.getColumnCount(); i++) {
      tcm.getColumn(i).setHeaderValue(columns[i]);
    }
  }
}