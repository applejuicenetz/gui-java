package de.applejuicenet.client.gui;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.gui.plugins.*;
import de.applejuicenet.client.shared.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/AppleJuiceDialog.java,v 1.25 2003/07/04 15:25:38 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: AppleJuiceDialog.java,v $
 * Revision 1.25  2003/07/04 15:25:38  maj0r
 * Version erhöht.
 * DownloadModel erweitert.
 *
 * Revision 1.24  2003/06/24 12:06:49  maj0r
 * log4j eingefügt (inkl. Bedienung über Einstellungsdialog).
 *
 * Revision 1.23  2003/06/22 19:54:45  maj0r
 * Behandlung von fehlenden Verzeichnissen und fehlenden xml-Dateien hinzugefügt.
 *
 * Revision 1.22  2003/06/13 15:07:30  maj0r
 * Versionsanzeige hinzugefügt.
 * Da der Controllerteil refactort werden kann, haben Controller und GUI separate Versionsnummern.
 *
 * Revision 1.21  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class AppleJuiceDialog
    extends JFrame
    implements LanguageListener {

  public static final String GUI_VERSION = "0.04 Alpha";

  RegisterPanel registerPane;
  JLabel[] statusbar = new JLabel[5];
  private JMenu sprachMenu;
  private JMenu optionenMenu;
  private HashSet plugins;
  private JMenuItem menuItem;
  private JFrame _this;
  private JButton pause;
  private boolean paused = false;
  private Logger logger;

  private static AppleJuiceDialog theApp;

  public AppleJuiceDialog() {
    super();
    logger = Logger.getLogger(getClass());
    try {
      jbInit();
      pack();
      _this = this;
      theApp = this;
      LanguageSelector.getInstance().addLanguageListener(this);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void addPluginToHashSet(PluginConnector plugin) {
    plugins.add(plugin);
  }

  public PluginConnector[] getPlugins() {
    return (PluginConnector[]) plugins.toArray(new PluginConnector[plugins.size()]);
  }

  public static AppleJuiceDialog getApp() {
    return theApp;
  }

  private void jbInit() throws Exception {
    setTitle("AppleJuice Client");
    plugins = new HashSet();
    Image img = IconManager.getInstance().getIcon("applejuice").getImage();
    setIconImage(img);
    setJMenuBar(createMenuBar());
    String path = System.getProperty("user.dir") + File.separator + "language" +
        File.separator;
    path += OptionsManager.getInstance().getSprache() + ".xml";
    registerPane = new RegisterPanel(this);
    LanguageSelector ls = LanguageSelector.getInstance(path);
    addWindowListener(
        new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        closeDialog(evt);
      }
    });
    getContentPane().setLayout(new BorderLayout());
    registerPane.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        RegisterI register = (RegisterI) registerPane.getSelectedComponent();
        register.registerSelected();
      }
    });
    getContentPane().add(registerPane, BorderLayout.CENTER);

    JPanel panel = new JPanel(new GridBagLayout());

    for (int i = 0; i < statusbar.length; i++) {
      statusbar[i] = new JLabel();
      statusbar[i].setHorizontalAlignment(JLabel.RIGHT);
      statusbar[i].setBorder(new BevelBorder(BevelBorder.LOWERED));
      statusbar[i].setFont(new java.awt.Font("SansSerif", 0, 11));
    }
    pause = new JButton("Pause");
    pause.setFont(new java.awt.Font("SansSerif", 0, 11));
    DataManager.getInstance().addStatusbarForListen(statusbar);
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;
    panel.add(statusbar[0], constraints);
    constraints.gridx = 1;
    constraints.weightx = 1;
    panel.add(statusbar[1], constraints);
    constraints.weightx = 0;
    constraints.gridx = 2;
    panel.add(statusbar[2], constraints);
    constraints.gridx = 3;
    panel.add(statusbar[3], constraints);
    constraints.gridx = 4;
    panel.add(statusbar[4], constraints);
    constraints.gridx = 5;
    panel.add(pause, constraints);
    getContentPane().add(panel, BorderLayout.SOUTH);

    //Tooltipps einstellen
    ToolTipManager.sharedInstance().setInitialDelay(1);
    ToolTipManager.sharedInstance().setDismissDelay(50000);
    fireLanguageChanged();
    DataManager.getInstance().startXMLCheck();
  }

  public Dimension getPreferredSize() {
    Dimension systemDimension = getToolkit().getScreenSize();
    return new Dimension( (int) systemDimension.getWidth() / 4 * 3,
                         (int) systemDimension.getHeight() / 4 * 3);
  }

  private static void einstellungenSpeichern() {
    String sprachText = LanguageSelector.getInstance().
        getFirstAttrbuteByTagName(new String[] {"Languageinfo", "name"});
    OptionsManager.getInstance().setSprache(sprachText);
  }

  private void closeDialog(WindowEvent evt) {
    setVisible(false);
    String nachricht = "appleJuice-Core-GUI wird beendet...";
    if (logger.isEnabledFor(Level.INFO))
      logger.info(nachricht);
    System.out.println(nachricht);
    einstellungenSpeichern();
    System.exit(0);
  }

  public static void closeWithErrormessage(String error, boolean speichereEinstellungen) {
    JOptionPane.showMessageDialog(theApp, error, "Fehler!",
                                  JOptionPane.OK_OPTION);
    String nachricht = "appleJuice-Core-GUI wird beendet...";
    Logger aLogger = Logger.getLogger(AppleJuiceDialog.class.getName());
    if (aLogger.isEnabledFor(Level.INFO))
      aLogger.info(nachricht);
    System.out.println(nachricht);
    if (speichereEinstellungen)
      einstellungenSpeichern();
    System.out.println("Fehler: " + error);
    System.exit( -1);
  }

  protected JMenuBar createMenuBar() {
    String path = System.getProperty("user.dir") + File.separator + "language" +
        File.separator;
    File languagePath = new File(path);
    if (!languagePath.isDirectory()) {
      closeWithErrormessage("Der Ordner 'language' für die Sprachauswahl xml-Dateien ist nicht vorhanden.\r\nappleJuice wird beendet.", false);
    }
    String[] tempListe = languagePath.list();
    HashSet sprachDateien = new HashSet();
    for (int i = 0; i < tempListe.length; i++) {
      if (tempListe[i].indexOf(".xml") != -1) {
        sprachDateien.add(tempListe[i]);
      }
    }
    if (sprachDateien.size() == 0) {
      closeWithErrormessage("Es sind keine xml-Dateien für die Sprachauswahl im Ordner 'language' vorhanden.\r\nappleJuice wird beendet.", false);
    }
    JMenuBar menuBar = new JMenuBar();
    optionenMenu = new JMenu("Extras");
    menuItem = new JMenuItem("Optionen");
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        OptionsDialog od = new OptionsDialog(_this);
        Dimension appDimension = od.getSize();
        Dimension screenSize = _this.getSize();
        od.setLocation( (screenSize.width - appDimension.width) / 4,
                       (screenSize.height - appDimension.height) / 4);
        od.show();
      }
    });
    optionenMenu.add(menuItem);
    menuBar.add(optionenMenu);

    sprachMenu = new JMenu("Sprache");
    menuBar.add(sprachMenu);
    ButtonGroup lafGroup = new ButtonGroup();

    Iterator it = sprachDateien.iterator();
    while (it.hasNext()) {
      String sprachText = LanguageSelector.getInstance(path + (String) it.next()).
          getFirstAttrbuteByTagName(new String[] {"Languageinfo", "name"});
      JCheckBoxMenuItem rb = new JCheckBoxMenuItem(sprachText);
      if (OptionsManager.getInstance().getSprache().equalsIgnoreCase(sprachText)) {
        rb.setSelected(true);
      }
      sprachMenu.add(rb);
      rb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent ae) {
          JCheckBoxMenuItem rb2 = (JCheckBoxMenuItem) ae.
              getSource();
          if (rb2.isSelected()) {
            String path = System.getProperty("user.dir") + File.separator +
                "language" + File.separator;
            String dateiName = path + rb2.getText() + ".xml";
            LanguageSelector.getInstance(dateiName);
          }
        }
      });
      lafGroup.add(rb);
    }
    return menuBar;
  }

  public void fireLanguageChanged() {
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    String versionsNr = DataManager.getInstance().getCoreVersion().getVersion();
    setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                               getFirstAttrbuteByTagName(new
        String[] {"mainform", "caption"})) + " (Core " + versionsNr + ")");
    sprachMenu.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "languagesheet",
                                  "caption"})));
    menuItem.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "caption"})));
    optionenMenu.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "menu", "extras"})));
    if (paused) {
      pause.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
          getFirstAttrbuteByTagName(new String[] {"javagui", "mainform",
                                    "fortsetzen"})));
    }
    else {
      pause.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
          getFirstAttrbuteByTagName(new String[] {"javagui", "mainform",
                                    "pause"})));
    }
  }
}
