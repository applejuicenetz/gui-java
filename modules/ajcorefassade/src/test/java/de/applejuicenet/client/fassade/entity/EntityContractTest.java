package de.applejuicenet.client.fassade.entity;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class EntityContractTest {
    private static class TestServer extends Server {
        private final int id;
        private final long lastSeen;

        TestServer(int id, long lastSeen) {
            this.id = id;
            this.lastSeen = lastSeen;
        }

        public String getName() { return "n"; }
        public String getHost() { return "h"; }
        public String getPort() { return "1"; }
        public long getTimeLastSeen() { return lastSeen; }
        public int getVersuche() { return 0; }
        public int getId() { return id; }
        public boolean isConnected() { return false; }
        public boolean isTryConnect() { return false; }
    }

    @Test
    public void serverEqualsHandlesNullAndOtherTypes() {
        Server server = new TestServer(1, 0);
        assertFalse(server.equals(null));
        assertFalse(server.equals("1"));
        assertTrue(server.equals(server));
    }

    @Test
    public void serverEqualsAndHashCodeUseId() {
        Set<Server> set = new HashSet<>();
        set.add(new TestServer(5, 0));
        assertTrue(set.contains(new TestServer(5, 99)));
        assertFalse(set.contains(new TestServer(6, 0)));
        assertEquals(new TestServer(5, 0).hashCode(), new TestServer(5, 1).hashCode());
    }

    @Test
    public void lastSeenFormatMatchesPreviousPatternAndSystemZone() {
        long millis = 1_700_000_000_123L;
        String expected = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(millis));
        assertEquals(expected, new TestServer(1, millis).getTimeLastSeenAsString());
        assertEquals("", new TestServer(1, 0).getTimeLastSeenAsString());
    }

    @Test(timeout = 20000)
    public void lastSeenFormatIsThreadSafe() throws Exception {
        long millis = 1_700_000_000_000L;
        String expected = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(millis));
        TestServer server = new TestServer(1, millis);
        ExecutorService pool = Executors.newFixedThreadPool(8);
        try {
            List<Future<Boolean>> results = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                results.add(pool.submit(() -> {
                    for (int n = 0; n < 5000; n++) {
                        if (!expected.equals(server.getTimeLastSeenAsString())) {
                            return false;
                        }
                    }
                    return true;
                }));
            }
            for (Future<Boolean> result : results) {
                assertTrue(result.get(15, TimeUnit.SECONDS));
            }
        } finally {
            pool.shutdownNow();
        }
    }
}
