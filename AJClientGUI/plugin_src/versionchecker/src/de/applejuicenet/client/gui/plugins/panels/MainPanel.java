package de.applejuicenet.client.gui.plugins.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.fassade.entity.Version;
import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/versionchecker/src/de/applejuicenet/client/gui/plugins/panels/Attic/MainPanel.java,v 1.5 2005/01/21 16:28:09 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
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
        Download download = null;
        String versionsNr;
        String key;
        String key2;
        VersionHolder versionHolder;
        DownloadSource[] sources;
        boolean updateView = false;
        try {
            synchronized (downloads) {
                Iterator it = downloads.values().iterator();
                while (it.hasNext()) {
                    download = (Download) it.next();
                    if (download == null) {
                        continue;
                    }
                    sources = download.getSources();
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
        Upload upload = null;
        String versionsNr;
        String key;
        String key2;
        VersionHolder versionHolder;
        boolean updateView = false;
        try {
            synchronized (uploads) {
                Iterator it = uploads.values().iterator();
                while (it.hasNext()) {
                    upload = (Upload) it.next();
                    if (upload == null || upload.getVersion() == null) {
                        continue;
                    }
                    key = Integer.toString(upload.getUploadID());
                    if (!ids.contains(key)) {
                        updateView = true;
                        ids.add(key);
                        versionsNr = upload.getVersion().getVersion();
                        key2 = versionsNr;
                        if (versions.containsKey(key2)) {
                            versionHolder = (VersionHolder) versions.get(key2);
                        }
                        else {
                            versionHolder = new VersionHolder(versionsNr);
                            versions.put(key2, versionHolder);
                        }
                        versionHolder.addUser(upload.getVersion().
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
                    setIcon(getVersionIcon(Version.WIN32));
                    break;
                }
                case 2: {
                    setIcon(getVersionIcon(Version.LINUX));
                    break;
                }
                case 3: {
                    setIcon(getVersionIcon(Version.MACINTOSH));
                    break;
                }
                case 4: {
                    setIcon(getVersionIcon(Version.SOLARIS));
                    break;
                }
                case 5: {
                    setIcon(getVersionIcon(Version.OS2));
                    break;
                }
                case 6: {
                    setIcon(getVersionIcon(Version.FREEBSD));
                    break;
                }
                case 7: {
                    setIcon(getVersionIcon(Version.NETWARE));
                    break;
                }
                case 8: {
                    setIcon(getVersionIcon(-11 /*unbekannt*/));
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

	private Icon getVersionIcon(int version) {
        switch (version) {
	        case Version.WIN32: {
	            return IconManager.getInstance().getIcon("winsymbol");
	        }
	        case Version.LINUX: {
	            return IconManager.getInstance().getIcon("linuxsymbol");
	        }
	        case Version.FREEBSD: {
	            return IconManager.getInstance().getIcon("freebsdsymbol");
	        }
	        case Version.MACINTOSH: {
	            return IconManager.getInstance().getIcon("macsymbol");
	        }
	        case Version.SOLARIS: {
	            return IconManager.getInstance().getIcon("sunossymbol");
	        }
	        case Version.NETWARE: {
	            return IconManager.getInstance().getIcon("netwaresymbol");
	        }
	        case Version.OS2: {
	            return IconManager.getInstance().getIcon("os2symbol");
	        }
	        default: {
	            return IconManager.getInstance().getIcon("unbekanntsymbol");
	        }
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
