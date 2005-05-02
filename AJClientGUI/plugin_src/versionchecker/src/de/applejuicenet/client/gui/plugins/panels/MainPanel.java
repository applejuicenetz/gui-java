package de.applejuicenet.client.gui.plugins.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.fassade.entity.Version;
import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/versionchecker/src/de/applejuicenet/client/gui/plugins/panels/Attic/MainPanel.java,v 1.7 2005/05/02 14:45:42 maj0r Exp $
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
    private HashMap<String, VersionHolder> versions = new HashMap();
    private HashSet<String> ids = new HashSet();
    private VersionTableModel versionTableModel = new VersionTableModel();
    private JTable versionTable;
	private DecimalFormat formatter = new DecimalFormat("###,##0.00");

    public MainPanel() {
        logger = Logger.getLogger(getClass());
        try {
            init();
        }
        catch (Exception e) {
            logger.error("Unbehandelte Exception", e);
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

    public void updateByDownload(HashMap<String, Download> downloads) {
        String versionsNr;
        String key;
        String key2;
        VersionHolder versionHolder;
        boolean updateView = false;
        try {
            synchronized (downloads) {
                for (Download curDownload : downloads.values()) {
                    if (curDownload == null) {
                        continue;
                    }
                    for (DownloadSource curSource : curDownload.getSources()) {
                        if (curSource == null || curSource.getVersion() == null) {
                            continue;
                        }
                        key = Integer.toString(curSource.getId());
                        if (!ids.contains(key)) {
                            updateView = true;
                            ids.add(key);
                            versionsNr = curSource.getVersion().getVersion();
                            key2 = versionsNr;
                            if (versions.containsKey(key2)) {
                                versionHolder = versions.get(
                                    key2);
                            }
                            else {
                                versionHolder = new VersionHolder(versionsNr);
                                versions.put(key2, versionHolder);
                            }
                            versionHolder.addUser(curSource.getVersion().
                                                  getBetriebsSystem());
                        }
                    }
                }
            }
            if (updateView) {
                versionTableModel.setTable(versions);
                updateTableHeader();
            }
        }
        catch (Exception e) {
            logger.error("Unbehandelte Exception", e);
        }
    }

	private void updateTableHeader() {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				versionTable.getTableHeader().updateUI();			
			}
		});
	}

	public void updateByUploads(HashMap<String, Upload> uploads) {
        String versionsNr;
        String key;
        String key2;
        VersionHolder versionHolder;
        boolean updateView = false;
        try {
            synchronized (uploads) {
                for (Upload curUpload : uploads.values()) {
                    if (curUpload == null || curUpload.getVersion() == null) {
                        continue;
                    }
                    key = Integer.toString(curUpload.getId());
                    if (!ids.contains(key)) {
                        updateView = true;
                        ids.add(key);
                        versionsNr = curUpload.getVersion().getVersion();
                        key2 = versionsNr;
                        if (versions.containsKey(key2)) {
                            versionHolder = versions.get(key2);
                        }
                        else {
                            versionHolder = new VersionHolder(versionsNr);
                            versions.put(key2, versionHolder);
                        }
                        versionHolder.addUser(curUpload.getVersion().
                                              getBetriebsSystem());
                    }
                }
            }
            if (updateView) {
                versionTableModel.setTable(versions);
                updateTableHeader();
            }
        }
        catch (Exception e) {
            logger.error("Unbehandelte Exception", e);
        }
    }

    class TableHeaderCellRenderer
        extends JLabel
        implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
            switch (column) {
                case 1: {
                    setIcon(getVersionIcon(Version.WIN32));
                    setText(value.toString() + getPercent(Version.WIN32));
                    break;
                }
                case 2: {
                    setIcon(getVersionIcon(Version.LINUX));
                    setText(value.toString() + getPercent(Version.LINUX));
                    break;
                }
                case 3: {
                    setIcon(getVersionIcon(Version.MACINTOSH));
                    setText(value.toString() + getPercent(Version.MACINTOSH));
                    break;
                }
                case 4: {
                    setIcon(getVersionIcon(Version.SOLARIS));
                    setText(value.toString() + getPercent(Version.SOLARIS));
                    break;
                }
                case 5: {
                    setIcon(getVersionIcon(Version.OS2));
                    setText(value.toString() + getPercent(Version.OS2));
                    break;
                }
                case 6: {
                    setIcon(getVersionIcon(Version.FREEBSD));
                    setText(value.toString() + getPercent(Version.FREEBSD));
                    break;
                }
                case 7: {
                    setIcon(getVersionIcon(Version.NETWARE));
                    setText(value.toString() + getPercent(Version.NETWARE));
                    break;
                }
                case 8: {
                    setIcon(getVersionIcon(-11 /*unbekannt*/));
                    setText(value.toString() + getPercent(-11));
                    break;
                }
                default: {
                    //setText(value.toString());
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
    
    private String getPercent(int os){
    	if (VersionHolder.countAll == 0){
    		return "";
    	}
    	int gesamt = 0;
    	for (VersionHolder curHolder : versions.values()) {
    		gesamt += curHolder.getUser(os);
    	}
    	if (gesamt == 0){
    		return "";
    	}
    	double percent = (double) gesamt / VersionHolder.countAll * 100;
    	return "  ( " + formatter.format(percent) +  "% )";
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
