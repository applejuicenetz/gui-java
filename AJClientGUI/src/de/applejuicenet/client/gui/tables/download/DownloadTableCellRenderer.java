package de.applejuicenet.client.gui.tables.download;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadTableCellRenderer.java,v 1.20 2004/03/09 16:50:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class DownloadTableCellRenderer
    implements TableCellRenderer, DataUpdateListener {

    private Settings settings;

    public DownloadTableCellRenderer() {
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
        JLabel label1 = new JLabel();
        label1.setText( (String) value);
        label1.setFont(table.getFont());
        label1.setOpaque(true);
        if (isSelected) {
            label1.setBackground(table.getSelectionBackground());
            label1.setForeground(table.getSelectionForeground());
        }
        else {
            if (settings.isFarbenAktiv()) {
                label1.setBackground(settings.getQuelleHintergrundColor());
            }
            else {
                label1.setBackground(table.getBackground());
            }
            label1.setForeground(foreground);
        }
        return label1;
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