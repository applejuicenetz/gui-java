/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.search;

import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.gui.RegisterI;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.TklPanel;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.IconManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;

/**
 * Copyright: General Public License
 */
public class SearchPanel extends TklPanel implements LanguageListener, RegisterI
{
   private SearchResultTabbedPane resultPanel        = new SearchResultTabbedPane();
   private JButton              btnStartStopSearch = new JButton();
   private JTextField           searchTerm        = new JTextField();
   private JLabel               searchLabel             = new JLabel();
   private JLabel               processing        = new JLabel();
   private JMenuItem              paste;
   private JPopupMenu             menu;
   private final Logger logger;

   public SearchPanel(GuiController guiController)
   {
      super(guiController);
      logger = LoggerFactory.getLogger(getClass());
      try
      {
         init();
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
      }
   }

   public JButton getStartStopBtn()
   {
      return btnStartStopSearch;
   }

   public JTextField getSearchTermTxt()
   {
      return searchTerm;
   }

   public JLabel getProcessingLbl()
   {
      return processing;
   }

   public SearchResultTabbedPane getSearchResultTabbedPane()
   {
      return resultPanel;
   }

   public JMenuItem getMnuPaste()
   {
      return paste;
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
      panel3.add(searchLabel, constraints);
      constraints.gridx = 1;
      panel3.add(searchTerm, constraints);
      constraints.gridy = 1;
      panel3.add(btnStartStopSearch, constraints);
      JPanel panel2 = new JPanel();

      panel2.setLayout(new FlowLayout());
      panel2.add(processing);
      constraints.gridx     = 0;
      constraints.gridy     = 2;
      constraints.gridwidth = 2;
      panel3.add(panel2, constraints);
      leftPanel.setLayout(new BorderLayout());
      leftPanel.add(panel3, BorderLayout.NORTH);
      add(leftPanel, BorderLayout.WEST);

      add(resultPanel, BorderLayout.CENTER);

      searchTerm.addKeyListener(new KeyAdapter()
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

      paste = new JMenuItem();
      paste.setIcon(im.getIcon("clipboard"));
      paste.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               linkMenuActionPerformed(e);
            }
         });
      menu.add(paste);
      searchTerm.addMouseListener(new MouseAdapter()
         {
            public void mouseClicked(MouseEvent e)
            {
               if(e.getButton() == MouseEvent.BUTTON3)
               {
                  showLinkMenu(e.getX(), e.getY());
               }
            }
         });
   }

   private void showLinkMenu(int x, int y)
   {
      menu.show(searchTerm, x, y);
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
            searchTerm.setText(data);
         }
         catch(Exception ex)
         {
            searchTerm.setText("Error");
         }
      }
   }

   public void fireLanguageChanged()
   {
      try
      {
         LanguageSelector languageSelector = LanguageSelector.getInstance();

         searchLabel.setText(languageSelector.getFirstAttributeByTagName("mainform.searchlbl.caption") + ": ");
         btnStartStopSearch.setText(languageSelector.getFirstAttributeByTagName("mainform.searchbtn.searchcaption"));

         String[] resultTexts = new String[10];

         resultTexts[0] = languageSelector.getFirstAttributeByTagName("javagui.searchform.opensearches");
         resultTexts[1] = languageSelector.getFirstAttributeByTagName("javagui.searchform.foundfiles");
         resultTexts[2] = languageSelector.getFirstAttributeByTagName("javagui.searchform.searchedclients");
         resultTexts[3] = languageSelector.getFirstAttributeByTagName("mainform.Getlink3.caption");
         resultTexts[4] = languageSelector.getFirstAttributeByTagName("mainform.cancelsearch.caption");
         resultTexts[5] = languageSelector.getFirstAttributeByTagName("javagui.downloadform.alreadyloaded");
         resultTexts[6] = languageSelector.getFirstAttributeByTagName("javagui.downloadform.invalidlink");
         resultTexts[7] = languageSelector.getFirstAttributeByTagName("javagui.downloadform.otherlinkerrorlong");
         resultTexts[8] = languageSelector.getFirstAttributeByTagName("mainform.caption");
         resultTexts[9] = languageSelector.getFirstAttributeByTagName("releaseinfo.menu");

         String[] columns = new String[3];

         columns[0] = languageSelector.getFirstAttributeByTagName("mainform.searchs.col0caption");
         columns[1] = languageSelector.getFirstAttributeByTagName("mainform.searchs.col1caption");
         columns[2] = languageSelector.getFirstAttributeByTagName("mainform.searchs.col2caption");

         SearchResultPanel.setTexts(resultTexts, columns);

         for(int i = 0; i < resultPanel.getComponentCount(); i++)
         {
            ((SearchResultPanel) resultPanel.getComponentAt(i)).changeLanguage();
         }
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
      }
   }
}
