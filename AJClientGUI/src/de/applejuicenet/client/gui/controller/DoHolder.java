package de.applejuicenet.client.gui.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.applejuicenet.client.shared.dac.PropertyChangeAdapter;

public class DoHolder extends HashMap {
    public static final String VALUE_ADDED = "VALUE_ADDED";
    public static final String VALUE_REMOVED = "VALUE_REMOVED";
    
    private Set listener = new HashSet();
    private Informer informer = new Informer();

    public DoHolder() {
    }
    
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener){
        listener.add(propertyChangeListener);
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener){
        listener.remove(propertyChangeListener);
    }
    
    private void notifyListener(PropertyChangeEvent propertyChangeEvent){
        Iterator it = listener.iterator();
        while (it.hasNext()){
            ((PropertyChangeListener)it.next()).propertyChange(propertyChangeEvent);
        }
    }
    
    public Object put(Object key,Object value){
        Object obj = super.put(key, value);
        notifyListener(new PropertyChangeEvent(this, VALUE_ADDED, null, value));
        if (value instanceof PropertyChangeAdapter){
            ((PropertyChangeAdapter)value).addPropertyChangeListener(informer);
        }
        return obj;
    }

    public Object remove(Object key) {
        Object obj = super.remove(key);
        Object value = get(key);
        notifyListener(new PropertyChangeEvent(this, VALUE_REMOVED, key, value));
        if (value instanceof PropertyChangeAdapter){
            ((PropertyChangeAdapter)value).removePropertyChangeListener(informer);
        }
        return obj;
    }
    
    private class Informer implements PropertyChangeListener{
        public void propertyChange(PropertyChangeEvent evt) {
            notifyListener(evt);
        }
    }
}
