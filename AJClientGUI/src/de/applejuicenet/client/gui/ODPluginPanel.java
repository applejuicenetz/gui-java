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
    String text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"einstform", "Label11", "caption"})) + ":";
    label1.setText(text);

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
      String beschreibung;
      beschreibung = "Name:\r\n" + plugin.getTitle() + "\r\n\r\nAutor:\r\n" + plugin.getAutor()
          +"\r\n\r\nVersion:\r\n" + plugin.getVersion()
           + "\r\n\r\nBeschreibung:\r\n" + plugin.getBeschreibung();
      return beschreibung;
    }
  }

  void pluginList_valueChanged(ListSelectionEvent e) {
    PluginContainer selected = (PluginContainer)((JList)e.getSource()).getSelectedValue();
    beschreibung.setText(selected.getBeschreibung());
  }
}