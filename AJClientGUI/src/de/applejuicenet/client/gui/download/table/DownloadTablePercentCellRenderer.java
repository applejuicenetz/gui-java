package de.applejuicenet.client.gui.download.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.gui.components.treetable.TreeTableModelAdapter;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.download.table.DownloadMainNode.MainNodeType;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.util.DownloadSourceCalculator;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/download/table/Attic/DownloadTablePercentCellRenderer.java,v 1.8 2006/10/26 13:34:06 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class DownloadTablePercentCellRenderer
    implements TableCellRenderer, DataUpdateListener {
    private Settings settings;
    private static Color background;
    private static Color selectionBackground;

    static {
        JTable tmpTable = new JTable();
        background = tmpTable.getBackground();
        selectionBackground = tmpTable.getSelectionBackground();
    }

    public DownloadTablePercentCellRenderer() {
        settings = Settings.getSettings();
        OptionsManagerImpl.getInstance().addSettingsListener(this);
    }

    public Component getTableCellRendererComponent(JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column) {

        Object node = ( (TreeTableModelAdapter) table.getModel()).nodeForRow(
            row);
        if (node instanceof DownloadSource) {
            return getComponentForSource( (DownloadSource) node, table, value,
                                         isSelected, hasFocus, row, column);
        }
        else if (node.getClass() == DownloadMainNode.class) {
            return getComponentForDownload( (DownloadMainNode) node, table,
                                           value, isSelected, hasFocus, row,
                                           column);
        }
        else if (node.getClass() == DownloadNode.class) {
            return getComponentForDirectory( (DownloadNode) node,
                                            table, value, isSelected, hasFocus,
                                            row, column);
        }
        else {
            return new JLabel("");
        }
    }

    public Component getComponentForDownload(DownloadMainNode downloadMainNode,
                                             JTable table,
                                             Object value,
                                             boolean isSelected,
                                             boolean hasFocus,
                                             int row,
                                             int column) {
        Component c = downloadMainNode.getProgressbarComponent(table, value);
        Download download = downloadMainNode.getDownload();
        if (isSelected) {
            c.setBackground(selectionBackground);
        }
        else {
            if (downloadMainNode.getType() == MainNodeType.ROOT_NODE &&
                download.getStatus() == Download.FERTIG &&
                settings.isFarbenAktiv()) {
                c.setBackground(settings.
                                     getDownloadFertigHintergrundColor());
            }
            else {
                c.setBackground(background);
            }
        }
        return c;
    }

    public Component getComponentForSource(DownloadSource downloadSource,
                                           JTable table,
                                           Object value,
                                           boolean isSelected,
                                           boolean hasFocus,
                                           int row,
                                           int column) {
        Component c = DownloadSourceCalculator.getProgressbarComponent(downloadSource);
        if (isSelected) {
            c.setBackground(selectionBackground);
        }
        else {
            if (settings.isFarbenAktiv()) {
                c.setBackground(settings.getQuelleHintergrundColor());
            }
            else {
                c.setBackground(background);
            }
        }
        return c;
    }

    public Component getComponentForDirectory(DownloadNode downloadDirectoryNode,
                                              JTable table,
                                              Object value,
                                              boolean isSelected,
                                              boolean hasFocus,
                                              int row,
                                              int column) {
        JLabel label = new JLabel();
        label.setOpaque(true);
        if (isSelected) {
            label.setBackground(selectionBackground);
        }
        else {
            label.setBackground(background);
        }
        return label;
    }

    public void fireContentChanged(DATALISTENER_TYPE type, Object content) {
        if (type == DATALISTENER_TYPE.SETTINGS_CHANGED) {
            settings = (Settings) content;
        }
    }
}
