package de.applejuicenet.client.gui.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.applejuicenet.client.gui.listener.DataUpdateListener;

public abstract class DataUpdateInformer {
    private final int listenerType;
    private Set listener = new HashSet();

    protected DataUpdateInformer(int dataUpdateListenerType){
        listenerType = dataUpdateListenerType;
    }

    public int getDataUpdateListenerType(){
        return listenerType;
    }

    public void addDataUpdateListener(DataUpdateListener dataUpdateListener){
        listener.add(dataUpdateListener);
    }

    public void removeDataUpdateListener(DataUpdateListener dataUpdateListener){
        listener.remove(dataUpdateListener);
    }

    public void informDataUpdateListener(){
        Object content = getContentObject();
        Iterator it = listener.iterator();
        while (it.hasNext()) {
            ( (DataUpdateListener) it.next()).fireContentChanged(
                listenerType, content);
        }

    }

    protected abstract Object getContentObject();
}