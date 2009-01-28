/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.search;

import java.awt.Component;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.Search;
import de.applejuicenet.client.fassade.entity.Share;
import de.applejuicenet.client.fassade.event.DataPropertyChangeEvent;
import de.applejuicenet.client.fassade.event.DownloadDataPropertyChangeEvent;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.GuiControllerActionListener;
import de.applejuicenet.client.gui.components.util.Value;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.download.DownloadPropertyChangeListener;
import de.applejuicenet.client.gui.search.table.SearchEntryIconRenderer;
import de.applejuicenet.client.shared.SoundPlayer;

import de.tklsoft.gui.controls.TKLTextField;

public class SearchController extends GuiController
{
   private static SearchController         instance                       = null;
   private final int                       DOWNLOAD_PROPERTY_CHANGE_EVENT = 1;
   private final int                       START_STOP                     = 2;
   private Map<Integer, SearchResultPanel> searchIds                      = null;
   private SearchPanel                     searchPanel;
   private boolean                         panelSelected                  = false;
   private boolean                         firstSearch                    = true;
   private String                          bearbeitung;

   private SearchController()
   {
      super();
      searchPanel = new SearchPanel(this);
      init();
      LanguageSelector.getInstance().addLanguageListener(this);
   }

   private void init()
   {
      AppleJuiceClient.getAjFassade().addDataUpdateListener(this, DATALISTENER_TYPE.SEARCH_CHANGED);

      searchPanel.getStartStopBtn().addActionListener(new GuiControllerActionListener(this, START_STOP));
      AppleJuiceClient.getAjFassade().getDownloadPropertyChangeInformer()
      .addDataPropertyChangeListener(new DownloadPropertyChangeListener(this, DOWNLOAD_PROPERTY_CHANGE_EVENT));
   }

   public static synchronized SearchController getInstance()
   {
      if(null == instance)
      {
         instance = new SearchController();
      }

      return instance;
   }

   public JComponent getComponent()
   {
      return searchPanel;
   }

   public void fireAction(int actionId, Object source)
   {
      switch(actionId)
      {

         case START_STOP:
         {
            startStop();
            break;
         }

         case DOWNLOAD_PROPERTY_CHANGE_EVENT:
         {
            downloadPropertyChanged((DataPropertyChangeEvent) source);
            break;
         }

         default:
            logger.error("Unregistrierte EventId " + actionId);
      }
   }

   private void startStop()
   {
      TKLTextField suchbegriff = searchPanel.getSuchbegriffTxt();
      String       suchText = suchbegriff.getText();

      if(suchText.length() != 0)
      {
         try
         {
            startFirstSearch();
            AppleJuiceClient.getAjFassade().startSearch(suchText);
            suchbegriff.setSelectionStart(0);
            suchbegriff.setSelectionEnd(suchText.length());
            SoundPlayer.getInstance().playSound(SoundPlayer.SUCHEN);
         }
         catch(IllegalArgumentException e)
         {
            logger.error(e);
         }
      }
   }

   public void componentSelected()
   {
      panelSelected = true;
      SearchResultPanel searchResultPanel = (SearchResultPanel) searchPanel.getSearchResultTabbedPane().getSelectedComponent();

      if(searchResultPanel != null)
      {
         searchResultPanel.updateSearchContent();
      }
   }

   public void componentLostSelection()
   {
      panelSelected = false;
   }

   public Value[] getCustomizedValues()
   {
      return null;
   }

   protected void languageChanged()
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      bearbeitung = languageSelector.getFirstAttrbuteByTagName("mainform.opensearches.caption");
      searchPanel.getBearbeitungLbl().setText(bearbeitung.replaceAll("%d", Integer.toString(Search.currentSearchCount)));
      searchPanel.getMnuEinfuegen().setText(languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.einfuegen"));
   }

   protected void startFirstSearch()
   {
      if(!firstSearch)
      {
         return;
      }

      firstSearch = false;
      AppleJuiceClient.getAjFassade().getDownloadPropertyChangeInformer()
      .addDataPropertyChangeListener(new DownloadPropertyChangeListener(this, DOWNLOAD_PROPERTY_CHANGE_EVENT));
      Map<Integer, Share> shares = AppleJuiceClient.getAjFassade().getShare(false);

      for(Share curShare : shares.values())
      {
         SearchEntryIconRenderer.addMd5Sum(curShare.getCheckSum());
      }

      Map<Integer, Download> downloads = AppleJuiceClient.getAjFassade().getDownloadsSnapshot();

      for(Download curDownload : downloads.values())
      {
         SearchEntryIconRenderer.addMd5Sum(curDownload.getHash());
      }
   }

   protected void contentChanged(DATALISTENER_TYPE type, final Object content)
   {
      if(type == DATALISTENER_TYPE.SEARCH_CHANGED)
      {
         startFirstSearch();
         SwingUtilities.invokeLater(new Runnable()
            {
               @SuppressWarnings("unchecked")
               public void run()
               {
                  try
                  {
                     if(searchIds == null)
                     {
                        searchIds = new HashMap<Integer, SearchResultPanel>();
                     }

                     Map<Integer, Search>   theContent  = (Map<Integer, Search>) content;
                     Search                 aSearch;
                     SearchResultTabbedPane resultPanel = searchPanel.getSearchResultTabbedPane();

                     synchronized(content)
                     {
                        for(Integer curKey : theContent.keySet())
                        {
                           if(!searchIds.containsKey(curKey))
                           {
                              aSearch = theContent.get(curKey);
                              SearchResultPanel searchResultPanel = new SearchResultPanel(aSearch);

                              resultPanel.addTab(aSearch.getSuchText(), searchResultPanel);
                              searchResultPanel.aendereSprache();
                              resultPanel.setSelectedComponent(searchResultPanel);
                              searchIds.put(curKey, searchResultPanel);
                           }
                           else
                           {
                              aSearch = theContent.get(curKey);
                              if(aSearch.isChanged() && panelSelected)
                              {
                                 SearchResultPanel curSelectedsearchResultPanel = (SearchResultPanel) searchPanel.getSearchResultTabbedPane()
                                                                                  .getSelectedComponent();

                                 SearchResultPanel searchResultPanel = searchIds.get(curKey);

                                 if(curSelectedsearchResultPanel != null && curSelectedsearchResultPanel == searchResultPanel)
                                 {
                                    searchResultPanel.updateSearchContent();
                                 }
                                 else
                                 {
                                    searchResultPanel.updateZahlen();
                                 }
                              }
                           }
                        }

                        int id;

                        for(Object curObj : resultPanel.getComponents())
                        {
                           aSearch = ((SearchResultPanel) curObj).getSearch();
                           id = aSearch.getId();
                           int index = resultPanel.indexOfComponent((Component) curObj);

                           resultPanel.setTitleAt(index, aSearch.getSuchText() + " (" + aSearch.getEntryCount() + ")");
                           if(!theContent.containsKey(id))
                           {
                              searchIds.remove(id);
                              resultPanel.enableIconAt(index, aSearch);
                           }
                           else if(!aSearch.isRunning())
                           {
                              resultPanel.enableIconAt(index, aSearch);
                           }
                        }

                        searchPanel.getBearbeitungLbl()
                        .setText(bearbeitung.replaceAll("%d", Integer.toString(Search.currentSearchCount)));
                     }
                  }
                  catch(Exception e)
                  {
                     logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
                  }
               }
            });
      }
   }

   private synchronized void downloadPropertyChanged(DataPropertyChangeEvent evt)
   {
      if(evt.isEventContainer())
      {
         for(DataPropertyChangeEvent curEvent : evt.getNestedEvents())
         {
            handleDownloadDataPropertyChangeEvent((DownloadDataPropertyChangeEvent) curEvent);
         }
      }
      else
      {
         handleDownloadDataPropertyChangeEvent((DownloadDataPropertyChangeEvent) evt);
      }
   }

   private void handleDownloadDataPropertyChangeEvent(DownloadDataPropertyChangeEvent event)
   {
      if(event.getName() == null)
      {
         return;
      }
      else if(event.getName().equals(DownloadDataPropertyChangeEvent.DOWNLOAD_ADDED))
      {
         SearchEntryIconRenderer.addMd5Sum(((Download) event.getNewValue()).getHash());
      }
      else if(event.getName().equals(DownloadDataPropertyChangeEvent.DOWNLOAD_REMOVED))
      {
         if((Download) event.getOldValue() != null)
         {
            SearchEntryIconRenderer.removeMd5Sum(((Download) event.getOldValue()).getHash());
         }
      }
   }
}
