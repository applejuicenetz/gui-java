package de.applejuicenet.client.gui.search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Search;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.RegisterI;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.SoundPlayer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/search/SearchPanel.java,v 1.8 2005/01/19 16:22:19 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class SearchPanel
    extends JPanel
    implements LanguageListener, RegisterI, DataUpdateListener {

	private static SearchPanel instance;
    private SearchResultTabbedPane resultPanel = new SearchResultTabbedPane();
    private JButton btnStartStopSearch = new JButton();
    private JTextField suchbegriff = new JTextField();
    private JLabel label1 = new JLabel();
    private String bearbeitung;
    private JLabel label2 = new JLabel();
    private Logger logger;
    private Map searchIds = null;
    private boolean panelSelected = false;

    public static synchronized SearchPanel getInstance() {
        if (instance == null) {
            instance = new SearchPanel();
        }
        return instance;
    }

    private SearchPanel() {
        logger = Logger.getLogger(getClass());
        try {
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    private void init() throws Exception {
        setLayout(new BorderLayout());
        LanguageSelector.getInstance().addLanguageListener(this);
        JPanel panel3 = new JPanel();
        JPanel leftPanel = new JPanel();
        panel3.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        panel3.add(label1, constraints);
        constraints.gridx = 1;
        panel3.add(suchbegriff, constraints);
        constraints.gridy = 1;
        panel3.add(btnStartStopSearch, constraints);
        JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout());
        panel2.add(label2);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        panel3.add(panel2, constraints);
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(panel3, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);

        add(resultPanel, BorderLayout.CENTER);

        suchbegriff.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnStartStopSearch.doClick();
                }
            }
        });

        btnStartStopSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String suchText = suchbegriff.getText();
                if (suchText.length() != 0) {
                	try {
						AppleJuiceClient.getAjFassade().startSearch(suchText);
						suchbegriff.setSelectionStart(0);
						suchbegriff.setSelectionEnd(suchText.length());
						SoundPlayer.getInstance().playSound(SoundPlayer.SUCHEN);
					} catch (IllegalArgumentException e) {
						logger.error(e);
					}
                }
            }
        });

        AppleJuiceClient.getAjFassade().addDataUpdateListener(this,
        		DATALISTENER_TYPE.SEARCH_CHANGED);
    }

    public void registerSelected() {
        panelSelected = true;
        SearchResultPanel searchResultPanel = (SearchResultPanel) resultPanel.
            getSelectedComponent();
        if (searchResultPanel != null) {
            searchResultPanel.updateSearchContent();
        }
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.searchlbl.caption")) + ": ");
            btnStartStopSearch.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.searchbtn.searchcaption")));

            bearbeitung = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.opensearches.caption"));
            label2.setText(bearbeitung.replaceAll("%d", Integer.toString(Search.currentSearchCount)));
            String[] resultTexte = new String[9];
            resultTexte[0] = (ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.searchform.offenesuchen")));
            resultTexte[1] = (ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.searchform.gefundenedateien")));
            resultTexte[2] = (ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.searchform.durchsuchteclients")));
            resultTexte[3] = (ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.Getlink3.caption")));
            resultTexte[4] = (ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.cancelsearch.caption")));            
            resultTexte[5] = ZeichenErsetzer.korrigiereUmlaute(languageSelector
    				.getFirstAttrbuteByTagName(".root.javagui.downloadform.bereitsgeladen"));
            resultTexte[6] = ZeichenErsetzer.korrigiereUmlaute(languageSelector
    				.getFirstAttrbuteByTagName(".root.javagui.downloadform.falscherlink"));
            resultTexte[7] = ZeichenErsetzer.korrigiereUmlaute(languageSelector
    				.getFirstAttrbuteByTagName(".root.javagui.downloadform.sonstigerlinkfehlerlang"));
            resultTexte[8] = ZeichenErsetzer.korrigiereUmlaute(languageSelector
    				.getFirstAttrbuteByTagName(".root.mainform.caption"));

            
            String[] columns = new String[3];
            columns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.searchs.col0caption"));
            columns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.searchs.col1caption"));
            columns[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.searchs.col2caption"));

            SearchResultPanel.setTexte(resultTexte, columns);

            for (int i = 0; i < resultPanel.getComponentCount(); i++) {
                ( (SearchResultPanel) resultPanel.getComponentAt(i)).
                    aendereSprache();
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public void fireContentChanged(DATALISTENER_TYPE type, final Object content) {
        if (type == DATALISTENER_TYPE.SEARCH_CHANGED) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
			            synchronized (content) {
			            	if (searchIds == null){
			            		searchIds = new HashMap();
			            	}
			                Iterator it = ( (HashMap) content).keySet().iterator();
			                Object key;
			                Search aSearch;
			                SearchResultPanel searchResultPanel;
			                while (it.hasNext()) {
			                    key = it.next();
			                    if (!searchIds.containsKey(key)) {
			                        aSearch = (Search) ( (HashMap) content).get(key);
			                        searchResultPanel = new SearchResultPanel(aSearch);
			                        resultPanel.addTab(aSearch.getSuchText(),
			                                           searchResultPanel);
			                        searchResultPanel.aendereSprache();
			                        resultPanel.setSelectedComponent(searchResultPanel);
			                        searchIds.put(key, searchResultPanel);
			                    }
			                    else {
			                        searchResultPanel = (SearchResultPanel) searchIds.
			                            get(key);
			                        aSearch = (Search) ( (HashMap) content).get(key);
			                        if (panelSelected) {
			                            searchResultPanel.updateSearchContent();
			                        }
			                    }
			                }
			                Object[] searchPanels = resultPanel.getComponents();
			                int id;
			                String searchKey;
			                for (int i = 0; i < searchPanels.length; i++) {
			                    id = ( (SearchResultPanel) searchPanels[i]).getSearch().
			                        getId();
			                    searchKey = Integer.toString(id);
			                    if (! ( (HashMap) content).containsKey(searchKey)) {
			                        int index = resultPanel.indexOfComponent((Component)searchPanels[i]);
			                        searchIds.remove(searchKey);
			                        resultPanel.enableIconAt(index, ((SearchResultPanel) searchPanels[i]).getSearch());
			                    }
			                    else if (!((SearchResultPanel) searchPanels[i]).getSearch().isRunning()){
			                    	int index = resultPanel.indexOfComponent((Component)searchPanels[i]);
			                    	resultPanel.enableIconAt(index, ((SearchResultPanel) searchPanels[i]).getSearch());
			                    }
			                }
			                label2.setText(bearbeitung.replaceAll("%d",
			                    Integer.toString(Search.currentSearchCount)));
			            }
			        }
			        catch (Exception e) {
			            if (logger.isEnabledFor(Level.ERROR)) {
			                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			            }
			        }
				}});
        }
    }

    public void lostSelection() {
        panelSelected = false;
    }
}
