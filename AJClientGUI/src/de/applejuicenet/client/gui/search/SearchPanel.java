/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.search;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.RegisterI;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.TklPanel;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.IconManager;
import de.tklsoft.gui.controls.TKLButton;
import de.tklsoft.gui.controls.TKLLabel;
import de.tklsoft.gui.controls.TKLTextField;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/search/SearchPanel.java,v 1.15 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */
public class SearchPanel extends TklPanel implements LanguageListener, RegisterI
{
   private SearchResultTabbedPane resultPanel        = new SearchResultTabbedPane();
   private TKLButton              btnStartStopSearch = new TKLButton();
   private TKLTextField           suchbegriff        = new TKLTextField();
   private TKLLabel               suchen             = new TKLLabel();
   private TKLLabel               bearbeitung        = new TKLLabel();
   private JMenuItem              einfuegen;
   private JPopupMenu             menu;
   private Logger                 logger;

   public SearchPanel(GuiController guiController)
   {
      super(guiController);
      logger = Logger.getLogger(getClass());
      try
      {
         init();
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   public TKLButton getStartStopBtn()
   {
      return btnStartStopSearch;
   }

   public TKLTextField getSuchbegriffTxt()
   {
      return suchbegriff;
   }

   public TKLLabel getBearbeitungLbl()
   {
      return bearbeitung;
   }

   public SearchResultTabbedPane getSearchResultTabbedPane()
   {
      return resultPanel;
   }

   public JMenuItem getMnuEinfuegen()
   {
      return einfuegen;
   }

   private void init() throws Exception
   {
      setLayout(new BorderLayout());
      LanguageSelector.getInstance().addLanguageListener(this);
      JPanel panel3    = new JPanel();
      JPanel leftPanel = new JPanel();

      panel3.setLayout(new GridBagLayout());
      GridBagConstraints constraints = new GridBagConstraints();

      constraints.anchor     = GridBagConstraints.NORTH;
      constraints.fill       = GridBagConstraints.BOTH;
      constraints.gridx      = 0;
      constraints.gridy      = 0;
      constraints.gridwidth  = 1;
      constraints.gridheight = 1;
      panel3.add(suchen, constraints);
      constraints.gridx = 1;
      panel3.add(suchbegriff, constraints);
      constraints.gridy = 1;
      panel3.add(btnStartStopSearch, constraints);
      JPanel panel2 = new JPanel();

      panel2.setLayout(new FlowLayout());
      panel2.add(bearbeitung);
      constraints.gridx     = 0;
      constraints.gridy     = 2;
      constraints.gridwidth = 2;
      panel3.add(panel2, constraints);
      leftPanel.setLayout(new BorderLayout());
      leftPanel.add(panel3, BorderLayout.NORTH);
      add(leftPanel, BorderLayout.WEST);

      add(resultPanel, BorderLayout.CENTER);

      suchbegriff.addKeyListener(new KeyAdapter()
         {
            public void keyPressed(KeyEvent ke)
            {
               if(ke.getKeyCode() == KeyEvent.VK_ENTER)
               {
                  btnStartStopSearch.doClick();
               }
            }
         });
      menu = new JPopupMenu();
      IconManager im = IconManager.getInstance();

      einfuegen = new JMenuItem();
      einfuegen.setIcon(im.getIcon("clipboard"));
      einfuegen.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               linkMenuActionPerformed(e);
            }
         });
      menu.add(einfuegen);
      suchbegriff.addMouseListener(new MouseAdapter()
         {
            public void mouseClicked(MouseEvent e)
            {
               if(e.getButton() == MouseEvent.BUTTON3)
               {
                  showLinkMenu(e.getX(), e.getY());
               }
            }
         });
      suchbegriff.disableDirtyComponent(true);
   }

   private void showLinkMenu(int x, int y)
   {
      menu.show(suchbegriff, x, y);
   }

   private void linkMenuActionPerformed(ActionEvent e)
   {
      Clipboard    cb           = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable transferable = cb.getContents(this);

      if(transferable != null)
      {
         String data = null;

         try
         {
            data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
            suchbegriff.setText(data);
         }
         catch(Exception ex)
         {
            suchbegriff.setText("Error");
         }
      }
   }

   public void fireLanguageChanged()
   {
      try
      {
         LanguageSelector languageSelector = LanguageSelector.getInstance();

         suchen.setText(languageSelector.getFirstAttrbuteByTagName("mainform.searchlbl.caption") + ": ");
         btnStartStopSearch.setText(languageSelector.getFirstAttrbuteByTagName("mainform.searchbtn.searchcaption"));

         String[] resultTexte = new String[9];

         resultTexte[0] = (languageSelector.getFirstAttrbuteByTagName("javagui.searchform.offenesuchen"));
         resultTexte[1] = (languageSelector.getFirstAttrbuteByTagName("javagui.searchform.gefundenedateien"));
         resultTexte[2] = (languageSelector.getFirstAttrbuteByTagName("javagui.searchform.durchsuchteclients"));
         resultTexte[3] = (languageSelector.getFirstAttrbuteByTagName("mainform.Getlink3.caption"));
         resultTexte[4] = (languageSelector.getFirstAttrbuteByTagName("mainform.cancelsearch.caption"));
         resultTexte[5] = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.bereitsgeladen");
         resultTexte[6] = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.falscherlink");
         resultTexte[7] = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.sonstigerlinkfehlerlang");
         resultTexte[8] = languageSelector.getFirstAttrbuteByTagName("mainform.caption");

         String[] columns = new String[3];

         columns[0] = languageSelector.getFirstAttrbuteByTagName("mainform.searchs.col0caption");
         columns[1] = languageSelector.getFirstAttrbuteByTagName("mainform.searchs.col1caption");
         columns[2] = languageSelector.getFirstAttrbuteByTagName("mainform.searchs.col2caption");

         SearchResultPanel.setTexte(resultTexte, columns);

         for(int i = 0; i < resultPanel.getComponentCount(); i++)
         {
            ((SearchResultPanel) resultPanel.getComponentAt(i)).aendereSprache();
         }
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }
}
