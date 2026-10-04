package de.applejuicenet.client.fassade.controller;

import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import org.junit.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;

public class DataUpdateInformerTest {
    @Test public void listenerFailureCannotStopOtherViews() {
        AtomicInteger delivered = new AtomicInteger();
        DataUpdateInformer informer = informer();
        informer.addDataUpdateListener((type, content) -> { throw new IllegalStateException("broken plugin"); });
        informer.addDataUpdateListener((type, content) -> delivered.incrementAndGet());
        informer.informDataUpdateListener();
        assertEquals(1, delivered.get());
    }

    @Test public void listenerCanDetachDuringNotification() {
        DataUpdateInformer informer = informer();
        AtomicInteger delivered = new AtomicInteger();
        DataUpdateListener[] removing = new DataUpdateListener[1];
        removing[0] = (type, content) -> informer.removeDataUpdateListener(removing[0]);
        informer.addDataUpdateListener(removing[0]);
        for (int i = 0; i < 10; i++) informer.addDataUpdateListener((type, content) -> delivered.incrementAndGet());
        informer.informDataUpdateListener();
        assertEquals(10, delivered.get());
    }

    private DataUpdateInformer informer() {
        return new DataUpdateInformer(DataUpdateListener.DATALISTENER_TYPE.DOWNLOAD_CHANGED) {
            protected Object getContentObject() { return new Object(); }
        };
    }
}
