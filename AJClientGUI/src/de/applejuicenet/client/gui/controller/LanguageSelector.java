package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.gui.listener.LanguageListener;
import java.util.HashSet;
import de.applejuicenet.client.shared.XMLDecoder;
import java.util.Iterator;
import de.applejuicenet.client.shared.exception.LanguageSelectorNotInstanciatedException;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class LanguageSelector extends XMLDecoder{
  private HashSet languageListener = new HashSet();
  private static LanguageSelector instance = null;

  private LanguageSelector(String path){
    super(path);
  }

  public static LanguageSelector getInstance() throws LanguageSelectorNotInstanciatedException{
    if (instance==null)
      throw new LanguageSelectorNotInstanciatedException();
    return instance;
  }

  public static LanguageSelector getInstance(String path){
    if (instance==null)
      instance = new LanguageSelector(path);
    else{
      instance.reload(path);
      instance.informLanguageListener();
    }
    return instance;
  }

  public void addLanguageListener(LanguageListener listener){
    if (!(languageListener.contains(listener)))
      languageListener.add(listener);
  }

  private void informLanguageListener(){
    Iterator it = languageListener.iterator();
    while (it.hasNext()){
      ((LanguageListener)it.next()).fireLanguageChanged();
    }
  }
}