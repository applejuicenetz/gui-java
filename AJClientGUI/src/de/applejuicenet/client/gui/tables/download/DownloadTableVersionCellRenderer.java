package de.applejuicenet.client.gui.tables.download;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class DownloadTableVersionCellRenderer
    implements TableCellRenderer, DataUpdateListener {

    private Settings settings;

    public DownloadTableVersionCellRenderer() {
        super();
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
        if (node.getClass() == DownloadSourceDO.class) {
            return getComponentForSource( (DownloadSourceDO) node, table, value,
                                         isSelected, hasFocus, row, column);
        }
        else if (node.getClass() == DownloadMainNode.class) {
            return getComponentForDownload( (DownloadMainNode) node, table,
                                           value, isSelected, hasFocus, row,
                                           column);
        }
        else if (node.getClass() == DownloadDirectoryNode.class) {
            return getComponentForDirectory( (DownloadDirectoryNode) node,
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
        DownloadDO downloadDO = downloadMainNode.getDownloadDO();
        Component c = downloadMainNode.getVersionComponent(table, value);
        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
            c.setForeground(table.getSelectionForeground());
        }
        else {
            if (downloadMainNode.getType() == DownloadMainNode.ROOT_NODE &&
                downloadDO.getStatus() == DownloadDO.FERTIG &&
                settings.isFarbenAktiv()) {
                c.setBackground(settings.getDownloadFertigHintergrundColor());
            }
            else {
                c.setBackground(table.getBackground());
            }
            c.setForeground(table.getForeground());
        }
        return c;
    }

    public Component getComponentForSource(DownloadSourceDO downloadSourceDO,
                                           JTable table,
                                           Object value,
                                           boolean isSelected,
                                           boolean hasFocus,
                                           int row,
                                           int column) {
        Component c = downloadSourceDO.getVersionComponent(table, value);
        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
            c.setForeground(table.getSelectionForeground());
        }
        else {
            c.setForeground(table.getForeground());
            if (settings.isFarbenAktiv()) {
                c.setBackground(settings.getQuelleHintergrundColor());
            }
            else {
                c.setBackground(table.getBackground());
            }
        }
        return c;
    }

    public Component getComponentForDirectory(DownloadDirectoryNode downloadDirectoryNode,
                                              JTable table,
                                              Object value,
                                              boolean isSelected,
                                              boolean hasFocus,
                                              int row,
                                              int column) {
        Component c = downloadDirectoryNode.getVersionComponent(table, value);
        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
            c.setForeground(table.getSelectionForeground());
        }
        else {
            c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
        }
        return c;
    }

    public void fireContentChanged(int type, Object content) {
        if (type == DataUpdateListener.SETTINGS_CHANGED) {
            settings = (Settings) content;
        }
    }
}
