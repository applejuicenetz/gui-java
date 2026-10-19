package de.applejuicenet.client.facade;

import de.applejuicenet.client.facade.listener.CoreStatusListener;
import org.junit.Test;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;

public class CoreStatusDispatchTest {
    @Test public void lifecycleChangesAndFailuresCannotKillStartupNotification() throws Exception {
        var dispatch = ApplejuiceFacade.class.getDeclaredMethod("informCoreStatusListener", CoreStatusListener.STATUS.class);
        dispatch.setAccessible(true);
        var settings = new de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder("127.0.0.1", 1, "", true);
        var facade = new ApplejuiceFacade(settings);
        var registered = new ArrayList<CoreStatusListener>();
        AtomicInteger delivered = new AtomicInteger();
        CoreStatusListener bad = status -> { throw new IllegalStateException("plugin startup failure"); };
        registered.add(bad);
        for (int i = 0; i < 10; i++) {
            CoreStatusListener callback = status -> {
                ApplejuiceFacade.removeCoreStatusListener(bad);
                delivered.incrementAndGet();
            };
            registered.add(callback);
        }
        try {
            registered.forEach(ApplejuiceFacade::addCoreStatusListener);
            dispatch.invoke(facade, CoreStatusListener.STATUS.STARTED);
            assertEquals(10, delivered.get());
        } finally { registered.forEach(ApplejuiceFacade::removeCoreStatusListener); }
    }
}
