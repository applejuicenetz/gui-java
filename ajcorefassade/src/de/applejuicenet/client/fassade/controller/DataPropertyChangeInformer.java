package de.applejuicenet.client.fassade.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.applejuicenet.client.fassade.event.DataPropertyChangeEvent;
import de.applejuicenet.client.fassade.listener.DataPropertyChangeListener;

public final class DataPropertyChangeInformer {
    private Set listener = new HashSet();

    public void addDataPropertyChangeListener(DataPropertyChangeListener dataPropertyChangeListener){
        listener.add(dataPropertyChangeListener);
    }

    public void removeDataPropertyChangeListener(DataPropertyChangeListener dataPropertyChangeListener){
        listener.remove(dataPropertyChangeListener);
    }

    public void propertyChanged(DataPropertyChangeEvent dataPropertyChangeEvent){
        Iterator it = listener.iterator();
        while (it.hasNext()) {
            ( (DataPropertyChangeListener) it.next()).propertyChanged(dataPropertyChangeEvent);
        }
    }
}