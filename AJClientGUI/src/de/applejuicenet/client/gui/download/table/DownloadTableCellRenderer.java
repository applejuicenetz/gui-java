package de.applejuicenet.client.gui.download.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import de.applejuicenet.client.fassade.controller.dac.DownloadDO;
import de.applejuicenet.client.fassade.controller.dac.DownloadSourceDO;
import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.gui.components.treetable.TreeTableModelAdapter;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.shared.Settings;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/download/table/Attic/DownloadTableCellRenderer.java,v 1.4 2005/01/18 17:35:25 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class DownloadTableCellRenderer
    extends JLabel
    implements TableCellRenderer, DataUpdateListener {

	private Settings settings;
    private static Font font;
    private static Color background;
    private static Color foreground;
    private static Color selectionBackground;
    private static Color selectionForeground;

    static {
        JTable tmpTable = new JTable();
        font = tmpTable.getFont();
        foreground = tmpTable.getForeground();
        background = tmpTable.getBackground();
        selectionBackground = tmpTable.getSelectionBackground();
        selectionForeground = tmpTable.getSelectionForeground();
    }

    public DownloadTableCellRenderer() {
        super();
        setFont(font);
        setOpaque(true);
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
            if (isSelected) {
                setBackground(selectionBackground);
                setForeground(selectionForeground);
            }
            else {
                setBackground(background);
                setForeground(foreground);
            }
            return this;
        }
    }

    public Component getComponentForDownload(DownloadMainNode downloadMainNode,
                                             JTable table,
                                             Object value,
                                             boolean isSelected,
                                             boolean hasFocus,
                                             int row,
                                             int column) {
        setText( (String) value);
        if (isSelected) {
            setBackground(selectionBackground);
            setForeground(selectionForeground);
        }
        else {
            DownloadDO downloadDO = downloadMainNode.getDownloadDO();
            if (downloadMainNode.getType() == DownloadMainNode.ROOT_NODE &&
                downloadDO.getStatus() == DownloadDO.FERTIG &&
                settings.isFarbenAktiv()) {
                setBackground(settings.getDownloadFertigHintergrundColor());
            }
            else {
                setBackground(background);
            }
            setForeground(foreground);
        }
        return this;
    }

    public Component getComponentForSource(DownloadSourceDO downloadSourceDO,
                                           JTable table,
                                           Object value,
                                           boolean isSelected,
                                           boolean hasFocus,
                                           int row,
                                           int column) {
        setText( (String) value);
        if (isSelected) {
            setBackground(selectionBackground);
            setForeground(selectionForeground);
        }
        else {
            if (settings.isFarbenAktiv()) {
                setBackground(settings.getQuelleHintergrundColor());
            }
            else {
                setBackground(background);
            }
            setForeground(foreground);
        }
        return this;
    }

    public Component getComponentForDirectory(DownloadDirectoryNode node,
                                              JTable table,
                                              Object value,
                                              boolean isSelected,
                                              boolean hasFocus,
                                              int row,
                                              int column) {
        setText( (String) value);
        if (isSelected) {
            setBackground(selectionBackground);
            setForeground(selectionForeground);
        }
        else {
            setBackground(background);
            setForeground(foreground);
        }
        return this;
    }

    public void fireContentChanged(int type, Object content) {
        if (type == DataUpdateListener.SETTINGS_CHANGED) {
            settings = (Settings) content;
        }
    }
}