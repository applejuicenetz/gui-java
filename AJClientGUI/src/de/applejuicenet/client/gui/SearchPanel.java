package de.applejuicenet.client.gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.shared.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/SearchPanel.java,v 1.12 2003/09/30 16:35:11 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SearchPanel.java,v $
 * Revision 1.12  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
 * Revision 1.11  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.10  2003/09/07 09:29:55  maj0r
 * Position des Hauptfensters und Breite der Tabellenspalten werden gespeichert.
 *
 * Revision 1.9  2003/08/24 14:59:59  maj0r
 * Version 0.14
 * Diverse Aenderungen.
 *
 * Revision 1.8  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.7  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class SearchPanel
        extends JPanel
        implements LanguageListener, RegisterI, DataUpdateListener {

    public static SearchPanel _this;
    private JTabbedPane resultPanel = new JTabbedPane();
    private JButton btnStartStopSearch = new JButton("Suche starten");
    private JTextField suchbegriff = new JTextField();
    int anzahlSuchanfragen = 0;
    private JLabel label1 = new JLabel("Suchbegriff: ");
    private JLabel label2 = new JLabel("0 Suchanfragen in Bearbeitung");
    private Logger logger;
    private HashMap searchIds = new HashMap();

    public SearchPanel() {
        _this = this;
        logger = Logger.getLogger(getClass());
        try {
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private void init() throws Exception {
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

        add(resultPanel, BorderLayout.CENTER);
        btnStartStopSearch.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                String suchText = suchbegriff.getText();
                if (suchText.length()!=0){
                    ApplejuiceFassade.getInstance().startSearch(suchText);
                    suchbegriff.setText("");
                }
            }
        });
        ApplejuiceFassade.getInstance().addDataUpdateListener(this, DataUpdateListener.SEARCH_CHANGED);
    }

    public void registerSelected() {
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                    getFirstAttrbuteByTagName(new String[]{"mainform", "searchlbl",
                                                           "caption"})) + ": ");
            btnStartStopSearch.setText(ZeichenErsetzer.korrigiereUmlaute(
                    languageSelector.
                    getFirstAttrbuteByTagName(new String[]{"mainform", "searchbtn",
                                                           "searchcaption"})));

            String temp = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                    getFirstAttrbuteByTagName(new String[]{"mainform", "opensearches",
                                                           "caption"}));
            label2.setText(temp.replaceAll("%d", Integer.toString(anzahlSuchanfragen)));

            String[] columns = new String[3];
            columns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                    getFirstAttrbuteByTagName(new String[]{"mainform", "searchs",
                                                           "col0caption"}));
            columns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                    getFirstAttrbuteByTagName(new String[]{"mainform", "searchs",
                                                           "col1caption"}));
            columns[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                    getFirstAttrbuteByTagName(new String[]{"mainform", "searchs",
                                                           "col2caption"}));

/*            TableColumnModel tcm = searchResultTable.getColumnModel();
            for (int i = 0; i < tcm.getColumnCount(); i++) {
                tcm.getColumn(i).setHeaderValue(columns[i]);
            }*/
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void fireContentChanged(int type, Object content) {
        if (type==DataUpdateListener.SEARCH_CHANGED){
            synchronized(content){
                Iterator it = ((HashMap)content).keySet().iterator();
                Object key;
                Search aSearch;
                SearchResultPanel searchResultPanel;
                while (it.hasNext()){
                    key = it.next();
                    if (!searchIds.containsKey(key)){
                        aSearch = (Search)((HashMap)content).get(key);
                        searchResultPanel = new SearchResultPanel(aSearch);
                        resultPanel.add(aSearch.getSuchText(), searchResultPanel);
                        resultPanel.setSelectedComponent(searchResultPanel);
                        searchIds.put(key, searchResultPanel);
                    }
                    else{
                        searchResultPanel = (SearchResultPanel) searchIds.get(key);
                        searchResultPanel.updateSearchContent();
                    }
                }
            }
        }
    }
}