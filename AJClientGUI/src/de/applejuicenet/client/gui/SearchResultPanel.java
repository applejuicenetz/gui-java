package de.applejuicenet.client.gui;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import javax.swing.*;
import javax.swing.table.TableColumnModel;

import de.applejuicenet.client.gui.tables.search.SearchResultTableModel;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.shared.Search;

import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/SearchResultPanel.java,v 1.1 2003/09/30 16:35:11 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SearchResultPanel.java,v $
 * Revision 1.1  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
 *
 */

public class SearchResultPanel extends JPanel{
    private Logger logger;
    private JTable searchResultTable = new JTable();
    private SearchResultTableModel tableModel;
    private Search search;

    public SearchResultPanel(Search aSearch) {
        search = aSearch;
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
        tableModel = new SearchResultTableModel(search);
        searchResultTable.setModel(tableModel);
        add(new JScrollPane(searchResultTable), BorderLayout.CENTER);
        searchResultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    public void updateSearchContent(){
        tableModel.updateTable();
        searchResultTable.repaint();
    }
}
