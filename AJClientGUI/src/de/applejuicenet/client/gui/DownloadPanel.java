package de.applejuicenet.client.gui;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.shared.SortButtonRenderer;
import de.applejuicenet.client.gui.tables.JTreeTable;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.gui.tables.download.DownloadDirectoryNode;
import de.applejuicenet.client.gui.tables.download.DownloadMainNode;
import de.applejuicenet.client.gui.tables.download.DownloadModel;
import de.applejuicenet.client.gui.tables.download.DownloadRootNode;
import de.applejuicenet.client.gui.tables.download.DownloadTableCellRenderer;
import de.applejuicenet.client.shared.MapSetStringKey;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.SoundPlayer;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/DownloadPanel.java,v 1.76 2004/01/05 14:34:59 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: DownloadPanel.java,v $
 * Revision 1.76  2004/01/05 14:34:59  maj0r
 * Bug #42 umgesetzt (Danke an dsp2004)
 * Partlisten werden nun durch eine Option wahlweise bei Mausklick auf den Download / Quelle oder ueber den Button "Partliste anzeigen" geholt.
 *
 * Revision 1.75  2004/01/04 12:37:27  maj0r
 * Bug #40 umgesetzt (Danke an hirsch.marcel)
 * Incoming-Verzeichnis kann nun für mehrere Downloads gleichzeitig geaendert werden.
 *
 * Revision 1.74  2004/01/01 18:38:14  maj0r
     * Partlisten von einigen wenigen DownloadSourcen wurden bei Bedarf nicht geholt.
 *
 * Revision 1.73  2003/12/30 20:52:19  maj0r
     * Umbenennen von Downloads und Aendern von Zielverzeichnissen vervollstaendigt.
 *
 * Revision 1.72  2003/12/30 14:52:11  maj0r
 * Das Zielverzeichnis fuer einen Download kann nun geaendert werden.
 *
 * Revision 1.71  2003/12/30 14:31:23  maj0r
 * Downloads koennen nun umbenannt werden.
 *
 * Revision 1.70  2003/12/30 10:35:00  maj0r
 * Bug #8 umgesetzt (Danke an finn)
 * Downloadlinks kann man nun auch direkt in der Downloadtabelle per Kontexmenue erzeugen.
 *
 * Revision 1.69  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.68  2003/12/29 15:29:53  maj0r
 * Downloadlinks werden jetzt in ISO-8859-1 an den Core uebertragen..
 *
 * Revision 1.67  2003/12/19 14:26:58  maj0r
 * Dau-Button zum Anzeigen der Partliste eingebaut.
 *
 * Revision 1.66  2003/12/17 17:03:37  maj0r
 * In der Downloadtabelle nun ein Warteicon angezeigt, bis erstmalig Daten geholt wurden.
 *
 * Revision 1.65  2003/12/17 14:54:06  maj0r
 * Reaktion des Popupmenues durch Threads beschleunigt.
 *
 * Revision 1.64  2003/12/17 11:24:28  maj0r
 * Frischen NullPointer behoben.
 *
 * Revision 1.63  2003/12/17 11:08:30  maj0r
 * Partliste wird nun nur noch über das PopupMenü geholt.
 * Wenn der Downloadtab verlassen wird, wird das Aktualisieren der aktuellen Partliste beendet.
 *
 * Revision 1.62  2003/12/16 09:06:40  maj0r
 * Partliste wird nun erst nach 2 Sekunden Wartezeit geholt, um ein erneutes Klicken behandeln zu können.
 *
 * Revision 1.61  2003/12/05 11:18:02  maj0r
 * Workaround fürs Setzen der Hintergrundfarben der Scrollbereiche ausgebaut.
 *
 * Revision 1.60  2003/11/30 17:01:33  maj0r
 * Hintergrundfarbe aller Scrollbereiche an ihre Tabellen angepasst.
 *
 * Revision 1.59  2003/11/17 14:44:10  maj0r
 * Erste funktionierende Version des automatischen Powerdownloads eingebaut.
 *
 * Revision 1.58  2003/11/03 20:57:03  maj0r
 * Sortieren nach Status eingebaut.
 *
 * Revision 1.57  2003/10/31 16:24:58  maj0r
 * Soundeffekte fuer diverse Ereignisse eingefuegt.
 *
 * Revision 1.56  2003/10/31 11:31:45  maj0r
 * Soundeffekte fuer diverse Ereignisse eingefuegt. Kommen noch mehr.
 *
 * Revision 1.55  2003/10/21 14:50:11  maj0r
 * Fixe Sprachverwendung entfernt.
 *
 * Revision 1.54  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.53  2003/10/21 11:36:32  maj0r
 * Infos werden nun ueber einen Listener geholt.
 *
 * Revision 1.52  2003/10/12 15:57:55  maj0r
 * Kleinere Bugs behoben.
 * Sortiert wird nun nur noch bei Klick auf den Spaltenkopf um CPU-Zeit zu sparen.
 *
 * Revision 1.51  2003/10/10 15:12:26  maj0r
 * Sortieren im Downloadbereich eingefuegt.
 *
 * Revision 1.50  2003/10/05 11:48:36  maj0r
 * Server koennen nun direkt durch Laden einer Homepage hinzugefuegt werden.
 * Userpartlisten werden angezeigt.
 * Downloadpartlisten werden alle 15 Sek. aktualisiert.
 *
 * Revision 1.49  2003/10/04 15:30:54  maj0r
 * Userpartliste hinzugefuegt.
 * Erste Version des Versteckens.
 *
 * Revision 1.48  2003/10/02 15:01:00  maj0r
 * Erste Version den Versteckens eingebaut.
 *
 * Revision 1.47  2003/10/01 14:45:40  maj0r
 * Suche fortgesetzt.
 *
 * Revision 1.46  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
 * Revision 1.45  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.44  2003/09/07 09:29:55  maj0r
     * Position des Hauptfensters und Breite der Tabellenspalten werden gespeichert.
 *
 * Revision 1.43  2003/09/04 10:14:08  maj0r
 * Logger eingebaut.
 *
 * Revision 1.42  2003/09/04 09:27:25  maj0r
 * DownloadPartListe fertiggestellt.
 *
 * Revision 1.41  2003/09/04 06:26:49  maj0r
 * Partlist korrigiert. Wird momentan beim Resize nicht neugezeichnet.
 *
 * Revision 1.40  2003/09/02 16:08:10  maj0r
 * Downloadbaum komplett umgebaut.
 *
 * Revision 1.39  2003/08/27 11:18:34  maj0r
 * Kleinen Fehler korrigiert.
 *
 * Revision 1.38  2003/08/22 14:16:00  maj0r
 * Threadverwendung korrigiert.
 *
 * Revision 1.37  2003/08/22 12:39:46  maj0r
 * Bug ID 798
 *
 * Revision 1.36  2003/08/22 10:03:11  maj0r
 * Threadverwendung korrigiert.
 *
 * Revision 1.35  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.34  2003/08/11 14:10:27  maj0r
 * DownloadPartList eingefügt.
 * Diverse Änderungen.
 *
 * Revision 1.33  2003/08/10 21:08:18  maj0r
 * Diverse Änderungen.
 *
 * Revision 1.32  2003/08/09 10:56:25  maj0r
 * DownloadTabelle weitergeführt.
 *
 * Revision 1.31  2003/08/05 20:47:06  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.30  2003/08/05 05:11:59  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.29  2003/07/06 20:00:19  maj0r
 * DownloadTable bearbeitet.
 *
 * Revision 1.28  2003/07/04 15:25:38  maj0r
 * Version erhöht.
 * DownloadModel erweitert.
 *
 * Revision 1.27  2003/07/04 06:43:51  maj0r
 * Diverse Änderungen am DownloadTableModel.
 *
 * Revision 1.26  2003/07/03 19:11:16  maj0r
 * DownloadTable überarbeitet.
 *
 * Revision 1.25  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 * Revision 1.24  2003/07/01 18:49:03  maj0r
 * Struktur verändert.
 *
 * Revision 1.23  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.22  2003/07/01 18:34:28  maj0r
 * Struktur verändert.
 *
 * Revision 1.21  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class DownloadPanel
    extends JPanel
    implements LanguageListener, RegisterI, DataUpdateListener {

    public static DownloadPanel _this;

    private DownloadDOOverviewPanel downloadDOOverviewPanel;
    private JTextField downloadLink = new JTextField();
    private JButton btnStartDownload = new JButton("Download");
    private PowerDownloadPanel powerDownloadPanel;
    private JTreeTable downloadTable;
    private JLabel linkLabel = new JLabel("ajfsp-Link hinzufügen");
    private DownloadModel downloadModel;
    private JPopupMenu popup = new JPopupMenu();
    private boolean initialized = false;
    private JScrollPane aScrollPane;
    private JMenuItem item1;
    private JMenuItem item2;
    private JMenuItem item4;
    private JMenuItem item5;
    private JMenuItem item6;
    private JMenuItem itemCopyToClipboard = new JMenuItem();
    private String downloadAbbrechen;
    private String dialogTitel;
    private JMenuItem item7;
    private Logger logger;
    private boolean isDownloadUebersicht;
    private String neuerDateiname;
    private String neuesVerzeichnis;

    private DownloadPartListWatcher downloadPartListWatcher = new
        DownloadPartListWatcher();

    public DownloadPanel() {
        _this = this;
        logger = Logger.getLogger(getClass());
        try {
            downloadDOOverviewPanel = new DownloadDOOverviewPanel(this);
            powerDownloadPanel = new PowerDownloadPanel(this);
            isDownloadUebersicht = Settings.getSettings().isDownloadUebersicht();
            init();
            LanguageSelector.getInstance().addLanguageListener(this);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void tryGetPartList() {
        item7.doClick();
    }

    private void init() throws Exception {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        item1 = new JMenuItem("Abbrechen");
        item2 = new JMenuItem("Pause/Fortsetzen");
        item4 = new JMenuItem("Umbenennen");
        item5 = new JMenuItem("Zielordner ändern");
        item6 = new JMenuItem("Fertige Übertragungen entfernen");
        item7 = new JMenuItem("Partliste anzeigen");
        popup.add(item1);
        popup.add(item2);
        popup.add(item4);
        popup.add(item5);
        popup.add(item6);
        popup.add(itemCopyToClipboard);
        popup.add(item7);
        item7.setVisible(false);

        itemCopyToClipboard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Object[] selectedItems = getSelectedDownloadItems();
                if (selectedItems != null && selectedItems.length == 1) {
                    Clipboard cb = Toolkit.getDefaultToolkit().
                        getSystemClipboard();
                    StringBuffer toCopy = new StringBuffer();
                    toCopy.append("ajfsp://file|");
                    if (selectedItems[0].getClass() == DownloadMainNode.class
                        &&
                        ( (DownloadMainNode) selectedItems[0]).getType() ==
                        DownloadMainNode.ROOT_NODE) {
                        DownloadDO downloadDO = ( (DownloadMainNode)
                                                 selectedItems[0]).
                            getDownloadDO();
                        toCopy.append(downloadDO.getFilename() + "|" +
                                      downloadDO.getHash() + "|" +
                                      downloadDO.getGroesse() + "/");
                        StringSelection contents = new StringSelection(toCopy.
                            toString());
                        cb.setContents(contents, null);
                    }
                    else if (selectedItems[0].getClass() == DownloadSourceDO.class) {
                        DownloadSourceDO downloadSourceDO = (DownloadSourceDO)
                            selectedItems[0];
                        HashMap downloads = ApplejuiceFassade.getInstance().
                            getDownloadsSnapshot();
                        MapSetStringKey key = new MapSetStringKey(
                            downloadSourceDO.getDownloadId());
                        DownloadDO downloadDO = (DownloadDO) downloads.get(key);
                        if (downloadDO != null) {
                            toCopy.append(downloadSourceDO.getFilename() + "|" +
                                          downloadDO.getHash() + "|" +
                                          downloadDO.getGroesse() + "/");
                            StringSelection contents = new StringSelection(
                                toCopy.toString());
                            cb.setContents(contents, null);
                        }
                    }
                }
            }
        });
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Object[] selectedItems = getSelectedDownloadItems();
                if (selectedItems != null && selectedItems.length != 0) {
                    int result = JOptionPane.showConfirmDialog(AppleJuiceDialog.
                        getApp(),
                        downloadAbbrechen, dialogTitel,
                        JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        ArrayList indizesAbbrechen = new ArrayList();
                        for (int i = 0; i < selectedItems.length; i++) {
                            if (selectedItems[i].getClass() == DownloadMainNode.class) {
                                DownloadDO downloadDO = ( (DownloadMainNode)
                                    selectedItems[i]).getDownloadDO();
                                indizesAbbrechen.add(new Integer(downloadDO.
                                    getId()));
                            }
                        }
                        int size = indizesAbbrechen.size();
                        if (size > 0) {
                            final int[] abbrechen = new int[size];
                            for (int i = 0; i < size; i++) {
                                abbrechen[i] = ( (Integer) indizesAbbrechen.get(
                                    i)).intValue();
                            }
                            new Thread() {
                                public void run() {
                                    ApplejuiceFassade.getInstance().
                                        cancelDownload(
                                        abbrechen);
                                    SoundPlayer.getInstance().playSound(
                                        SoundPlayer.
                                        ABGEBROCHEN);
                                }
                            }

                            .start();
                        }
                    }
                }
            }
        });

        item2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Object[] selectedItems = getSelectedDownloadItems();
                if (selectedItems != null && selectedItems.length != 0 &&
                    !powerDownloadPanel.isAutomaticPwdlActive()) {
                    ArrayList indizesPausieren = new ArrayList();
                    ArrayList indizesFortsetzen = new ArrayList();
                    for (int i = 0; i < selectedItems.length; i++) {
                        if (selectedItems[i].getClass() == DownloadMainNode.class) {
                            DownloadDO downloadDO = ( (DownloadMainNode)
                                selectedItems[i]).getDownloadDO();
                            if (downloadDO.getStatus() == DownloadDO.PAUSIERT) {
                                indizesFortsetzen.add(new Integer(downloadDO.
                                    getId()));
                            }
                            else {
                                indizesPausieren.add(new Integer(downloadDO.
                                    getId()));
                            }
                        }
                    }
                    int size = indizesPausieren.size();
                    if (size > 0) {
                        final int[] pausieren = new int[size];
                        for (int i = 0; i < size; i++) {
                            pausieren[i] = ( (Integer) indizesPausieren.get(i)).
                                intValue();
                        }
                        new Thread() {
                            public void run() {
                                ApplejuiceFassade.getInstance().pauseDownload(
                                    pausieren);
                            }
                        }

                        .start();
                    }
                    size = indizesFortsetzen.size();
                    if (size > 0) {
                        final int[] fortsetzen = new int[size];
                        for (int i = 0; i < size; i++) {
                            fortsetzen[i] = ( (Integer) indizesFortsetzen.get(i)).
                                intValue();
                        }
                        new Thread() {
                            public void run() {
                                ApplejuiceFassade.getInstance().resumeDownload(
                                    fortsetzen);
                            }
                        }

                        .start();
                    }
                }
            }
        });

        item4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Object[] selectedItems = getSelectedDownloadItems();
                if (selectedItems != null && selectedItems.length == 1) {
                    if (selectedItems[0].getClass() == DownloadMainNode.class
                        &&
                        ( (DownloadMainNode) selectedItems[0]).getType() ==
                        DownloadMainNode.ROOT_NODE) {
                        DownloadDO downloadDO = ( (DownloadMainNode)
                                                 selectedItems[0]).
                            getDownloadDO();
                        String neuerName = JOptionPane.showInputDialog(
                            AppleJuiceDialog.getApp(), neuerDateiname + ":",
                            downloadDO.getFilename());
                        if (neuerName == null) {
                            return;
                        }
                        neuerName = neuerName.trim();
                        if (neuerName.length() != 0) {
                            if (downloadDO.getFilename().compareTo(neuerName) !=
                                0) {
                                ApplejuiceFassade.getInstance().renameDownload(
                                    downloadDO.getId(), neuerName);
                            }
                        }
                    }
                }
            }
        });

        item5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Object[] selectedItems = getSelectedDownloadItems();
                if (selectedItems == null ||
                    selectedItems.length == 0){
                    return;
                }
                String[] dirs = ApplejuiceFassade.getInstance().
                    getCurrentIncomingDirs();
                IncomingDirSelectionDialog incomingDirSelectionDialog =
                    new IncomingDirSelectionDialog(AppleJuiceDialog.getApp(),
                    dirs);
                incomingDirSelectionDialog.show();
                String neuerName = incomingDirSelectionDialog.
                    getSelectedIncomingDir();

                if (neuerName == null) {
                    return;
                }
                else {
                    neuerName = neuerName.trim();
                    if (neuerName.indexOf(File.separator) == 0 ||
                        neuerName.indexOf(ApplejuiceFassade.separator) == 0) {
                        neuerName = neuerName.substring(1);
                    }
                }
                DownloadDO downloadDO;
                for (int i = 0; i < selectedItems.length; i++) {
                    if (selectedItems[i].getClass() == DownloadMainNode.class
                        &&
                        ( (DownloadMainNode) selectedItems[i]).getType() ==
                        DownloadMainNode.ROOT_NODE) {
                        downloadDO = ( (DownloadMainNode) selectedItems[i]).
                            getDownloadDO();
                        if (downloadDO.getTargetDirectory().compareTo(
                            neuerName) != 0) {
                            ApplejuiceFassade.getInstance().
                                setTargetDir(
                                downloadDO.getId(), neuerName);
                        }
                    }
                }
            }
        });

        item6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ApplejuiceFassade.getInstance().cleanDownloadList();
            }
        });

        item7.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Object[] selectedItems = getSelectedDownloadItems();
                if (selectedItems != null && selectedItems.length == 1) {
                    if (selectedItems[0].getClass() == DownloadMainNode.class
                        &&
                        ( (DownloadMainNode) selectedItems[0]).getType() ==
                        DownloadMainNode.ROOT_NODE) {
                        downloadPartListWatcher.setDownloadNode(selectedItems[0]);
                    }
                    else if (selectedItems[0].getClass() == DownloadSourceDO.class) {
                        downloadPartListWatcher.setDownloadNode(selectedItems[0]);
                    }
                }
            }
        });

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        JPanel tempPanel = new JPanel();
        tempPanel.setLayout(new BorderLayout());
        tempPanel.add(linkLabel, BorderLayout.WEST);
        tempPanel.add(downloadLink, BorderLayout.CENTER);
        tempPanel.add(btnStartDownload, BorderLayout.EAST);
        topPanel.add(tempPanel, constraints);
        constraints.gridwidth = 3;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weighty = 1;
        constraints.weightx = 1;

        downloadLink.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnStartDownload.doClick();
                }
            }
        });

        downloadModel = new DownloadModel();
        downloadTable = new JTreeTable(downloadModel);

        DownloadTableCellRenderer renderer = new DownloadTableCellRenderer();
        for (int i = 1; i < downloadTable.getColumnModel().getColumnCount(); i++) {
            downloadTable.getColumnModel().getColumn(i).setCellRenderer(
                renderer);
        }
        btnStartDownload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                startDownload();
            }
        });
        aScrollPane = new JScrollPane(downloadTable);
        aScrollPane.setBackground(downloadTable.getBackground());
        downloadTable.getTableHeader().setBackground(downloadTable.
            getBackground());
        aScrollPane.getViewport().setOpaque(false);
        downloadTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (!DownloadRootNode.isInitialized()) {
                    return;
                }
                Point p = e.getPoint();
                int selectedRow = downloadTable.rowAtPoint(p);
                Object node = ( (TreeTableModelAdapter) downloadTable.getModel()).
                    nodeForRow(selectedRow);
                if (downloadTable.columnAtPoint(p) != 0) {
                    if (e.getClickCount() == 2) {
                        ( (TreeTableModelAdapter) downloadTable.getModel()).
                            expandOrCollapseRow(selectedRow);
                    }
                }
                if (node.getClass() == DownloadMainNode.class
                    &&
                    ( (DownloadMainNode) node).getType() ==
                    DownloadMainNode.ROOT_NODE) {
                    if (!powerDownloadPanel.isAutomaticPwdlActive()) {
                        powerDownloadPanel.btnPdl.setEnabled(true);
                    }
                    if (Settings.getSettings().isDownloadUebersicht()){
                        downloadDOOverviewPanel.enableHoleListButton(false);
                        tryGetPartList();
                    }
                    else{
                        downloadDOOverviewPanel.enableHoleListButton(true);
                    }
                }
                else if (node.getClass() == DownloadSourceDO.class) {
                    if (!powerDownloadPanel.isAutomaticPwdlActive()) {
                        powerDownloadPanel.btnPdl.setEnabled(true);
                    }
                    if (Settings.getSettings().isDownloadUebersicht()){
                        downloadDOOverviewPanel.enableHoleListButton(false);
                        tryGetPartList();
                    }
                    else{
                        downloadDOOverviewPanel.enableHoleListButton(true);
                    }
                }
                else {
                    powerDownloadPanel.btnPdl.setEnabled(false);
                    downloadDOOverviewPanel.enableHoleListButton(false);
                }
            }

            public void mousePressed(MouseEvent me) {
                super.mousePressed(me);
                maybeShowPopup(me);
            }

            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (!DownloadRootNode.isInitialized()) {
                    return;
                }
                if (e.isPopupTrigger()) {
                    Point p = e.getPoint();
                    int selectedRow = downloadTable.rowAtPoint(p);
                    int[] currentSelectedRows = downloadTable.getSelectedRows();
                    for (int i = 0; i < currentSelectedRows.length; i++) {
                        if (currentSelectedRows[i] == selectedRow) {
                            selectedRow = -1;
                            break;
                        }
                    }
                    if (selectedRow != -1) {
                        downloadTable.setRowSelectionInterval(selectedRow,
                            selectedRow);
                    }
                    Object[] selectedItems = getSelectedDownloadItems();
                    item4.setVisible(false);
                    item5.setVisible(false);
                    item7.setVisible(false);
                    if (selectedItems != null) {
                        if (selectedItems.length == 1) {
                            if ( (selectedItems[0].getClass() ==
                                  DownloadMainNode.class
                                  &&
                                  ( (DownloadMainNode) selectedItems[0]).
                                  getType() ==
                                  DownloadMainNode.ROOT_NODE)
                                ||
                                (selectedItems[0].getClass() ==
                                 DownloadSourceDO.class)) {
                                item7.setVisible(true);
                                if (selectedItems[0].getClass() !=
                                    DownloadSourceDO.class) {
                                    item4.setVisible(true);
                                    item5.setVisible(true);
                                }
                            }
                        }
                        else {
                            for (int i = 0; i < selectedItems.length; i++) {
                                if ( (selectedItems[i].getClass() ==
                                      DownloadMainNode.class
                                      &&
                                      ( (DownloadMainNode) selectedItems[i]).
                                      getType() ==
                                      DownloadMainNode.ROOT_NODE)) {
                                    item5.setVisible(true);
                                }
                            }
                        }
                    }
                    popup.show(downloadTable, e.getX(), e.getY());
                }
            }
        });

        topPanel.add(aScrollPane, constraints);

        bottomPanel.add(powerDownloadPanel, BorderLayout.WEST);

        bottomPanel.add(downloadDOOverviewPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        SortButtonRenderer renderer2 = new SortButtonRenderer();
        TableColumnModel model = downloadTable.getColumnModel();
        int n = model.getColumnCount();
        for (int i = 0; i < n; i++) {
            model.getColumn(i).setHeaderRenderer(renderer2);
        }
        JTableHeader header = downloadTable.getTableHeader();
        header.addMouseListener(new SortMouseAdapter(header, renderer2));
        downloadTable.setSelectionMode(ListSelectionModel.
                                       MULTIPLE_INTERVAL_SELECTION);

        ApplejuiceFassade.getInstance().addDataUpdateListener(this,
            DataUpdateListener.DOWNLOAD_CHANGED);
    }

    private void startDownload() {
        final String link = downloadLink.getText();
        if (link.length() != 0) {
            new Thread() {
                public void run() {
                    String encodedLink = link;
                    try {
                        StringBuffer tempLink = new StringBuffer(link);
                        for (int i = 0; i < tempLink.length(); i++) {
                            if (tempLink.charAt(i) == ' ') {
                                tempLink.setCharAt(i, '.');
                            }
                        }
                        encodedLink = URLEncoder.encode(tempLink.toString(),
                            "ISO-8859-1");
                    }
                    catch (UnsupportedEncodingException ex) {
                        //gibbet, also nix zu behandeln...
                    }
                    ApplejuiceFassade.getInstance().processLink(encodedLink);
                    SoundPlayer.getInstance().playSound(SoundPlayer.LADEN);
                }
            }

            .start();
            downloadLink.setText("");
        }
    }

    public Object[] getSelectedDownloadItems() {
        try {
            int count = downloadTable.getSelectedRowCount();
            Object[] result = null;
            if (count == 1) {
                result = new Object[count];
                result[0] = ( (TreeTableModelAdapter) downloadTable.getModel()).
                    nodeForRow(downloadTable.getSelectedRow());
            }
            else if (count > 1) {
                result = new Object[count];
                int[] indizes = downloadTable.getSelectedRows();
                for (int i = 0; i < indizes.length; i++) {
                    result[i] = ( (TreeTableModelAdapter) downloadTable.
                                 getModel()).nodeForRow(indizes[i]);
                }
            }
            return result;
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
            return null;
        }
    }

    public void registerSelected() {
        downloadDOOverviewPanel.enableHoleListButton(false);
        if (!initialized) {
            try {
                initialized = true;
                int width = aScrollPane.getWidth() - 18;
                TableColumnModel headerModel = downloadTable.getTableHeader().
                    getColumnModel();
                int columnCount = headerModel.getColumnCount();
                PositionManager pm = PropertiesManager.getPositionManager();
                if (pm.isLegal()) {
                    int[] widths = pm.getDownloadWidths();
                    for (int i = 0; i < columnCount; i++) {
                        headerModel.getColumn(i).setPreferredWidth(widths[i]);
                    }
                }
                else {
                    for (int i = 0; i < columnCount; i++) {
                        headerModel.getColumn(i).setPreferredWidth(width /
                            columnCount);
                    }
                }
                downloadTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            }
            catch (Exception e) {
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.error("Unbehandelte Exception", e);
                }
            }
        }
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            String text = languageSelector.getFirstAttrbuteByTagName(new String[] {
                "mainform", "Label14", "caption"});
            dialogTitel = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "caption"}));
            downloadAbbrechen = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "msgdlgtext5"}));
            linkLabel.setText(ZeichenErsetzer.korrigiereUmlaute(text));
            btnStartDownload.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "downlajfsp",
                                          "caption"})));
            btnStartDownload.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[] {
                "mainform",
                "downlajfsp", "hint"})));
            String[] tableColumns = new String[10];
            tableColumns[0] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                          "col0caption"}));
            tableColumns[1] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                          "col1caption"}));
            tableColumns[2] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                          "col2caption"}));
            tableColumns[3] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                          "col3caption"}));
            tableColumns[4] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                          "col4caption"}));
            tableColumns[5] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                          "col5caption"}));
            tableColumns[6] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                          "col6caption"}));
            tableColumns[7] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                          "col7caption"}));
            tableColumns[8] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                          "col8caption"}));
            tableColumns[9] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                          "col9caption"}));

            TableColumnModel tcm = downloadTable.getColumnModel();
            for (int i = 0; i < tcm.getColumnCount(); i++) {
                tcm.getColumn(i).setHeaderValue(tableColumns[i]);
            }

            item1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "canceldown",
                                          "caption"})));
            String temp = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "pausedown",
                                          "caption"}));
            temp += "/" +
                ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                  getFirstAttrbuteByTagName(new
                String[] {
                "mainform", "resumedown", "caption"}));
            item2.setText(temp);
            item4.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "renamefile",
                                          "caption"})));
            item5.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "changetarget",
                                          "caption"})));
            item6.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "Clearfinishedentries1", "caption"})));
            item7.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui",
                                          "downloadform", "partlisteanzeigen"})));
            itemCopyToClipboard.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[] {
                "mainform",
                "getlink1", "caption"})));
            neuerDateiname = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui",
                                          "downloadform", "neuerdateiname"}));
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public int[] getColumnWidths() {
        TableColumnModel tcm = downloadTable.getColumnModel();
        int[] widths = new int[tcm.getColumnCount()];
        for (int i = 0; i < tcm.getColumnCount(); i++) {
            widths[i] = tcm.getColumn(i).getWidth();
        }
        return widths;
    }

    public void fireContentChanged(int type, Object content) {
        try {
            if (type == DataUpdateListener.DOWNLOAD_CHANGED) {
                HashMap downloads = (HashMap) content;
                ( (DownloadRootNode) downloadModel.getRoot()).setDownloadMap(
                    downloads);
                DownloadDirectoryNode.setDownloads(downloads);
                downloadTable.updateUI();
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void lostSelection() {
        downloadPartListWatcher.setDownloadNode(null);
        downloadDOOverviewPanel.setDownloadDO(null);
    }

    class SortMouseAdapter
        extends MouseAdapter {
        private JTableHeader header;
        private SortButtonRenderer renderer;

        public SortMouseAdapter(JTableHeader header,
                                SortButtonRenderer renderer) {
            this.header = header;
            this.renderer = renderer;
            header.setReorderingAllowed(false);
        }

        public void mousePressed(MouseEvent e) {
            int col = header.columnAtPoint(e.getPoint());
            if (col == 9) {
                return;
            }
            renderer.setPressedColumn(col);
            renderer.setSelectedColumn(col);
            header.repaint();

            if (header.getTable().isEditing()) {
                header.getTable().getCellEditor().stopCellEditing();
            }

            boolean isAscent;
            if (SortButtonRenderer.UP == renderer.getState(col)) {
                isAscent = true;
            }
            else {
                isAscent = false;
            }
            DownloadRootNode rootNode = ( (DownloadRootNode) downloadModel.
                                         getRoot());
            switch (col) {
                case 0: {
                    rootNode.setSortCriteria(DownloadRootNode.SORT_DOWNLOADNAME,
                                             isAscent);
                    break;
                }
                case 1: {
                    rootNode.setSortCriteria(DownloadRootNode.SORT_STATUS,
                                             isAscent);
                    break;
                }
                case 2: {
                    rootNode.setSortCriteria(DownloadRootNode.SORT_GROESSE,
                                             isAscent);
                    break;
                }
                case 3: {
                    rootNode.setSortCriteria(DownloadRootNode.
                                             SORT_BEREITS_GELADEN, isAscent);
                    break;
                }
                case 4: {
                    rootNode.setSortCriteria(DownloadRootNode.
                                             SORT_GESCHWINDIGKEIT, isAscent);
                    break;
                }
                case 5: {
                    rootNode.setSortCriteria(DownloadRootNode.SORT_RESTZEIT,
                                             isAscent);
                    break;
                }
                case 6: {
                    rootNode.setSortCriteria(DownloadRootNode.SORT_PROZENT,
                                             isAscent);
                    break;
                }
                case 7: {
                    rootNode.setSortCriteria(DownloadRootNode.
                                             SORT_REST_ZU_LADEN, isAscent);
                    break;
                }
                case 8: {
                    rootNode.setSortCriteria(DownloadRootNode.SORT_PWDL,
                                             isAscent);
                    break;
                }
                default:
                    break;
            }
            downloadTable.updateUI();
        }

        public void mouseReleased(MouseEvent e) {
            renderer.setPressedColumn( -1);
            header.repaint();
        }
    }

    private class DownloadPartListWatcher {
        private Thread worker = null;
        private Object nodeObject = null;

        public void setDownloadNode(Object node) {
            if (node == null) {
                nodeObject = null;
                if (worker != null) {
                    worker.interrupt();
                    worker = null;
                }
                return;
            }
            if (worker != null) {
                worker.interrupt();
                worker = null;
            }
            nodeObject = node;
            worker = new Thread() {
                public void run() {
                    try {
                        sleep(2000);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                if (nodeObject.getClass() ==
                                    DownloadMainNode.class
                                    &&
                                    ( (DownloadMainNode) nodeObject).
                                    getType() ==
                                    DownloadMainNode.ROOT_NODE) {
                                    powerDownloadPanel.btnPdl.
                                        setEnabled(false);
                                    downloadDOOverviewPanel.setDownloadDO( ( (
                                        DownloadMainNode) nodeObject).
                                        getDownloadDO());
                                }
                                else if (nodeObject.getClass() ==
                                         DownloadSourceDO.class) {
                                    powerDownloadPanel.btnPdl.
                                        setEnabled(false);
                                    if ( ( (DownloadSourceDO) nodeObject).
                                        getStatus() ==
                                        DownloadSourceDO.IN_WARTESCHLANGE &&
                                        ( (DownloadSourceDO) nodeObject).
                                        getQueuePosition() > 20) {
                                        return;
                                    }
                                    downloadDOOverviewPanel.setDownloadSourceDO( (
                                        DownloadSourceDO) nodeObject);
                                }
                            }
                        });
                    }
                    catch (InterruptedException ex) {
                        interrupt();
                    }
                }
            };
            worker.start();
        }
    }
}