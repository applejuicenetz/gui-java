package de.applejuicenet.client.gui.plugins.panels;

import javax.swing.JPanel;
import org.apache.log4j.Logger;
import java.util.HashMap;
import org.apache.log4j.Level;
import java.util.ArrayList;
import java.util.Iterator;
import de.applejuicenet.client.shared.dac.UploadDO;
import java.util.HashSet;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import de.applejuicenet.client.shared.Version;
import javax.swing.JTable;
import java.awt.BorderLayout;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.Color;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/versionchecker/src/de/applejuicenet/client/gui/plugins/panels/Attic/MainPanel.java,v 1.3 2004/03/14 15:10:11 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: MainPanel.java,v $
 * Revision 1.3  2004/03/14 15:10:11  maj0r
 * ConcurrentModificationException behoben.
 *
 * Revision 1.2  2004/01/30 16:32:56  maj0r
 * MapSetStringKey ausgebaut.
 *
 * Revision 1.1  2004/01/27 15:45:45  maj0r
 * Erste Version des Plugins VersionChecker gebaut.
 *
 *
 */

public class MainPanel
    extends JPanel {
    private Logger logger;
    private HashMap versions = new HashMap();
    private HashSet ids = new HashSet();
    private VersionTableModel versionTableModel = new VersionTableModel();
    private JTable versionTable;

    public MainPanel() {
        logger = Logger.getLogger(getClass());
        try {
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void init() {
        setLayout(new BorderLayout());
        versionTableModel = new VersionTableModel();
        versionTable = new JTable();
        versionTable.setModel(versionTableModel);
        for (int i = 0; i < versionTable.getColumnCount(); i++) {
            versionTable.getTableHeader().getColumnModel().getColumn(i).
                setHeaderRenderer(new TableHeaderCellRenderer());
            if (i > 0) {
                versionTable.getColumnModel().getColumn(i).setCellRenderer(new
                    TableValueCellRenderer());
            }
        }
        add(new JScrollPane(versionTable), BorderLayout.CENTER);
    }

    public void updateByDownload(HashMap downloads) {
        DownloadDO downloadDO = null;
        String versionsNr;
        String key;
        String key2;
        VersionHolder versionHolder;
        DownloadSourceDO[] sources;
        boolean updateView = false;
        try {
            synchronized (downloads) {
                Iterator it = downloads.values().iterator();
                while (it.hasNext()) {
                    downloadDO = (DownloadDO) it.next();
                    if (downloadDO == null) {
                        continue;
                    }
                    sources = downloadDO.getSources();
                    for (int i = 0; i < sources.length; i++) {
                        if (sources[i] == null || sources[i].getVersion() == null) {
                            continue;
                        }
                        key = Integer.toString(sources[i].getId());
                        if (!ids.contains(key)) {
                            updateView = true;
                            ids.add(key);
                            versionsNr = sources[i].getVersion().getVersion();
                            key2 = versionsNr;
                            if (versions.containsKey(key2)) {
                                versionHolder = (VersionHolder) versions.get(
                                    key2);
                            }
                            else {
                                versionHolder = new VersionHolder(versionsNr);
                                versions.put(key2, versionHolder);
                            }
                            versionHolder.addUser(sources[i].getVersion().
                                                  getBetriebsSystem());
                        }
                    }
                }
            }
            if (updateView) {
                versionTableModel.setTable(versions);
                versionTable.updateUI();
                versionTable.repaint();
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void updateByUploads(HashMap uploads) {
        UploadDO uploadDO = null;
        String versionsNr;
        String key;
        String key2;
        VersionHolder versionHolder;
        boolean updateView = false;
        try {
            synchronized (uploads) {
                Iterator it = uploads.values().iterator();
                while (it.hasNext()) {
                    uploadDO = (UploadDO) it.next();
                    if (uploadDO == null || uploadDO.getVersion() == null) {
                        continue;
                    }
                    key = Integer.toString(uploadDO.getUploadID());
                    if (!ids.contains(key)) {
                        updateView = true;
                        ids.add(key);
                        versionsNr = uploadDO.getVersion().getVersion();
                        key2 = versionsNr;
                        if (versions.containsKey(key2)) {
                            versionHolder = (VersionHolder) versions.get(key2);
                        }
                        else {
                            versionHolder = new VersionHolder(versionsNr);
                            versions.put(key2, versionHolder);
                        }
                        versionHolder.addUser(uploadDO.getVersion().
                                              getBetriebsSystem());
                    }
                }
            }
            if (updateView) {
                versionTableModel.setTable(versions);
                versionTable.updateUI();
                versionTable.repaint();
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    class TableHeaderCellRenderer
        extends JLabel
        implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
            setText(value.toString());
            switch (column) {
                case 1: {
                    setIcon(new Version("", Version.WIN32).getVersionIcon());
                    break;
                }
                case 2: {
                    setIcon(new Version("", Version.LINUX).getVersionIcon());
                    break;
                }
                case 3: {
                    setIcon(new Version("", Version.MACINTOSH).getVersionIcon());
                    break;
                }
                case 4: {
                    setIcon(new Version("", Version.SOLARIS).getVersionIcon());
                    break;
                }
                case 5: {
                    setIcon(new Version("", Version.OS2).getVersionIcon());
                    break;
                }
                case 6: {
                    setIcon(new Version("", Version.FREEBSD).getVersionIcon());
                    break;
                }
                case 7: {
                    setIcon(new Version("", Version.NETWARE).getVersionIcon());
                    break;
                }
                case 8: {
                    setIcon(new Version("", Version.UNBEKANNT).getVersionIcon());
                    break;
                }
                default: {
                    break;
                }
            }
            setBackground(table.getBackground());
            setForeground(table.getForeground());
            setEnabled(table.isEnabled());
            setFont(table.getFont());
            setOpaque(true);
            return this;
        }
    }

    class TableValueCellRenderer
        extends JLabel
        implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
            setText(value.toString());
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            }
            else {
                setBackground(table.getBackground());
                try {
                    if (Integer.parseInt(value.toString()) > 0) {
                        setForeground(Color.BLUE);
                    }
                    else {
                        setForeground(table.getForeground());
                    }
                }
                catch (Exception e) {
                    setForeground(table.getForeground());
                }
            }
            setEnabled(table.isEnabled());
            setFont(table.getFont());
            setOpaque(true);
            return this;
        }
    }
}
