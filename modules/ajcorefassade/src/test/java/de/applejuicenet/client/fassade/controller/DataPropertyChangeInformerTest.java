package de.applejuicenet.client.fassade.controller;

import de.applejuicenet.client.fassade.listener.DataPropertyChangeListener;
import org.junit.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.assertEquals;

public class DataPropertyChangeInformerTest {
    @Test
    public void listenersMayRemoveOtherListenersDuringDispatch() {
        DataPropertyChangeInformer informer = new DataPropertyChangeInformer();
        AtomicInteger calls = new AtomicInteger();
        DataPropertyChangeListener[] listeners = new DataPropertyChangeListener[3];
        for (int i = 0; i < listeners.length; i++) {
            listeners[i] = event -> {
                calls.incrementAndGet();
                for (DataPropertyChangeListener listener : listeners) {
                    informer.removeDataPropertyChangeListener(listener);
                }
            };
        }
        for (DataPropertyChangeListener listener : listeners) {
            informer.addDataPropertyChangeListener(listener);
            informer.addDataPropertyChangeListener(listener);
        }
        informer.propertyChanged(null);
        assertEquals(3, calls.get());
        informer.propertyChanged(null);
        assertEquals(3, calls.get());
    }
}
