package de.applejuicenet.client.gui.tables.download;

import java.awt.Color;
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

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadTableCellRenderer.java,v 1.19 2004/03/09 16:25:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: DownloadTableCellRenderer.java,v $
 * Revision 1.19  2004/03/09 16:25:17  maj0r
 * PropertiesManager besser gekapselt.
 *
 * Revision 1.18  2004/01/09 15:33:35  maj0r
 * Spalten der Downloadtabelle koennen nun ordentlich verschoben werden.
 *
 * Revision 1.17  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.16  2003/10/16 12:06:51  maj0r
 * Diverse Schoenheitskorrekturen.
 *
 * Revision 1.15  2003/10/15 09:06:10  maj0r
 * Prozentanzeige nur bei aktiven Uebertragungen anzeigen.
 *
 * Revision 1.14  2003/10/02 11:15:13  maj0r
 * Kleinen Anzeigefehler korrigiert.
 *
 * Revision 1.13  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.12  2003/09/02 16:06:26  maj0r
 * Downloadbaum komplett umgebaut.
 *
 * Revision 1.11  2003/08/16 17:50:42  maj0r
 * Diverse Farben können nun manuell eingestellt bzw. deaktiviert werden.
 * DownloaduebersichtTabelle kann deaktiviert werden.
 *
 * Revision 1.10  2003/08/11 19:42:51  maj0r
 * Fertig-Status-Farbe korrigiert.
 *
 * Revision 1.9  2003/08/11 14:42:13  maj0r
 * Versions-Icon-Beschaffung in die Klasse Version verschoben.
 *
 * Revision 1.8  2003/08/10 21:08:18  maj0r
 * Diverse Änderungen.
 *
 * Revision 1.7  2003/08/09 10:56:38  maj0r
 * DownloadTabelle weitergeführt.
 *
 * Revision 1.6  2003/07/06 20:00:19  maj0r
 * DownloadTable bearbeitet.
 *
 * Revision 1.5  2003/07/04 06:43:51  maj0r
 * Diverse Änderungen am DownloadTableModel.
 *
 * Revision 1.4  2003/07/03 19:11:16  maj0r
 * DownloadTable überarbeitet.
 *
 * Revision 1.3  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 * Revision 1.2  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.1  2003/07/01 18:34:28  maj0r
 * Struktur verändert.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
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