package de.applejuicenet.client.shared.dac;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class PropertyChangeAdapter {
    private Set listener = new HashSet();
    
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener){
        listener.add(propertyChangeListener);
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener){
        listener.remove(propertyChangeListener);
    }
    
    protected void notifyPropertyChangeListener(PropertyChangeEvent propertyChangeEvent){
        Iterator it = listener.iterator();
        while (it.hasNext()){
            ((PropertyChangeListener)it.next()).propertyChange(propertyChangeEvent);
        }
    }
}
