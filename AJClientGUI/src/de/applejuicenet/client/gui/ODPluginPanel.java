package de.applejuicenet.client.gui;

import javax.swing.*;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import java.awt.BorderLayout;
import java.util.Vector;
import java.awt.Dimension;
import java.awt.Color;
import javax.swing.event.*;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.LanguageSelector;


/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class ODPluginPanel extends JPanel {
  private JList pluginList;
  private JEditorPane beschreibung = new JEditorPane();
  private JLabel label1 = new JLabel();
  private AppleJuiceDialog theApp;
  private String name;
  private String version;
  private String autor;
  private String erlaeuterung;

  public ODPluginPanel(JFrame parent) {
    theApp = (AppleJuiceDialog) parent;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    PluginConnector[] plugins = theApp.getPlugins();
    Vector v = new Vector();
    if (plugins.length!=0){
      for (int i = 0; i < plugins.length; i++) {
        v.add(new PluginContainer(plugins[i]));
      }
    }
    Dimension parentSize = theApp.getSize();
    beschreibung.setBackground(new Color(212, 208, 200));
    Dimension dim1 = beschreibung.getPreferredSize();
    beschreibung.setPreferredSize(new Dimension(parentSize.width/3, dim1.height));
    beschreibung.setEditable(false);
    pluginList = new JList(v);
    pluginList.setPreferredSize(new Dimension(190,parentSize.height/2));
    pluginList.addListSelectionListener(new ListSelectionListener(){
      public void valueChanged(ListSelectionEvent e){
        pluginList_valueChanged(e);
      }
    });
    setLayout(new BorderLayout());
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"einstform", "Label11", "caption"})) + ":");
    name = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"javagui", "options", "plugins", "name"}));
    version = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"javagui", "options", "plugins", "version"}));
    erlaeuterung = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"javagui", "options", "plugins", "beschreibung"}));
    autor = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"javagui", "options", "plugins", "autor"}));

    add(label1, BorderLayout.NORTH);
    add(pluginList, BorderLayout.WEST);
    add(beschreibung, BorderLayout.CENTER);
  }

  class PluginContainer{
    private PluginConnector plugin;

    public PluginContainer(PluginConnector plugin){
      this.plugin = plugin;
    }

    public String toString(){
      return plugin.getTitle();
    }

    public String getBeschreibung(){
      String text;
      text = name + ":\r\n" + plugin.getTitle() + "\r\n\r\n" + autor + ":\r\n" + plugin.getAutor()
          +"\r\n\r\n" + version  + ":\r\n" + plugin.getVersion()
           + "\r\n\r\n" + erlaeuterung + ":\r\n" + plugin.getBeschreibung();
      return text;
    }
  }

  void pluginList_valueChanged(ListSelectionEvent e) {
    PluginContainer selected = (PluginContainer)((JList)e.getSource()).getSelectedValue();
    beschreibung.setText(selected.getBeschreibung());
  }
}