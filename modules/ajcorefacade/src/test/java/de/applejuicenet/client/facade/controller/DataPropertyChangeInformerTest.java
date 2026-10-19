package de.applejuicenet.client.facade.controller;

import de.applejuicenet.client.facade.listener.DataPropertyChangeListener;
import org.junit.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.assertEquals;

public class DataPropertyChangeInformerTest {
    @Test
    public void listenerFailureDoesNotPreventLaterDeliveryOrNextUpdate() {
        DataPropertyChangeInformer informer = new DataPropertyChangeInformer();
        AtomicInteger calls = new AtomicInteger();
        informer.addDataPropertyChangeListener(event -> { throw new IllegalStateException("broken listener"); });
        informer.addDataPropertyChangeListener(event -> calls.incrementAndGet());
        informer.propertyChanged(null);
        informer.propertyChanged(null);
        assertEquals(2, calls.get());
    }

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
