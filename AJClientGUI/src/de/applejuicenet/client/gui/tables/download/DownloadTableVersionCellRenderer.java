package de.applejuicenet.client.gui.tables.download;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;

public class DownloadTableVersionCellRenderer
    implements TableCellRenderer, DataUpdateListener {

    private Settings settings;

    public DownloadTableVersionCellRenderer() {
        super();
        settings = Settings.getSettings();
        PropertiesManager.getOptionsManager().addSettingsListener(this);
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
        JLabel label1 = new JLabel();
        label1.setOpaque(true);
        label1.setFont(table.getFont());
        label1.setText( (String) value);
        if (isSelected) {
            label1.setBackground(table.getSelectionBackground());
            label1.setForeground(table.getSelectionForeground());
        }
        else {
            if (downloadMainNode.getType() == DownloadMainNode.ROOT_NODE &&
                downloadDO.getStatus() == DownloadDO.FERTIG &&
                settings.isFarbenAktiv()) {
                label1.setBackground(settings.getDownloadFertigHintergrundColor());
            }
            else {
                label1.setBackground(table.getBackground());
            }
            label1.setForeground(table.getForeground());
        }
        return label1;
    }

    public Component getComponentForSource(DownloadSourceDO downloadSourceDO,
                                           JTable table,
                                           Object value,
                                           boolean isSelected,
                                           boolean hasFocus,
                                           int row,
                                           int column) {
        Color foreground = table.getForeground();
        JPanel returnPanel = new JPanel(new BorderLayout());
        JLabel image = new JLabel();

        JLabel versionText = new JLabel();

        if (isSelected) {
            returnPanel.setBackground(table.getSelectionBackground());
            returnPanel.setForeground(table.getSelectionForeground());
            image.setBackground(table.getSelectionBackground());
            versionText.setBackground(table.getSelectionBackground());
            image.setForeground(table.getSelectionForeground());
            versionText.setBackground(table.getSelectionForeground());
        }
        else {
            if (settings.isFarbenAktiv()) {
                returnPanel.setBackground(settings.getQuelleHintergrundColor());
                returnPanel.setForeground(foreground);
            }
            else {
                returnPanel.setBackground(table.getBackground());
                returnPanel.setForeground(table.getForeground());
            }
            image.setBackground(table.getBackground());
            versionText.setBackground(table.getBackground());
            image.setForeground(table.getForeground());
            versionText.setBackground(table.getForeground());
        }

        if (downloadSourceDO.getVersion() == null) {
            return returnPanel;
        }
        else {
            image.setIcon(downloadSourceDO.getVersion().getVersionIcon());
        }
        versionText.setText("  " + downloadSourceDO.getVersion().getVersion());
        versionText.setFont(table.getFont());
        returnPanel.add(image, BorderLayout.WEST);
        returnPanel.add(versionText, BorderLayout.CENTER);
        return returnPanel;
    }

    public Component getComponentForDirectory(DownloadDirectoryNode node,
                                              JTable table,
                                              Object value,
                                              boolean isSelected,
                                              boolean hasFocus,
                                              int row,
                                              int column) {
        JLabel label1 = new JLabel();
        label1.setText( (String) value);
        label1.setFont(table.getFont());
        label1.setOpaque(true);
        if (isSelected) {
            label1.setBackground(table.getSelectionBackground());
            label1.setForeground(table.getSelectionForeground());
        }
        else {
            label1.setBackground(table.getBackground());
            label1.setForeground(table.getForeground());
        }
        return label1;
    }

    public void fireContentChanged(int type, Object content) {
        if (type == DataUpdateListener.SETTINGS_CHANGED) {
            settings = (Settings) content;
        }
    }
}
