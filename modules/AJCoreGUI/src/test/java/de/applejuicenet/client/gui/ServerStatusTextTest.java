package de.applejuicenet.client.gui;

import de.applejuicenet.client.facade.entity.Information;
import de.applejuicenet.client.facade.entity.Server;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class ServerStatusTextTest {
    @Test
    public void loadedServerNameIsShownWithoutAddress() throws Exception {
        Information information = information(server("Mein Server"));
        assertEquals("Mein Server", AppleJuiceDialog.getServerDisplayName(information));
        assertEquals("server.example.org:2002", AppleJuiceDialog.getServerAddress(information));
    }

    @Test
    public void unknownServerNameFallsBackToAddress() throws Exception {
        for (String name : new String[] {null, "", "   "}) {
            Information information = information(server(name));
            assertEquals("server.example.org:2002", AppleJuiceDialog.getServerDisplayName(information));
        }
    }

    @Test
    public void nameAppearsWhenExistingServerDetailsAreLoadedLater() throws Exception {
        Server server = server(null);
        Information information = information(server);
        assertEquals("server.example.org:2002", AppleJuiceDialog.getServerDisplayName(information));
        Method setter = server.getClass().getDeclaredMethod("setName", String.class);
        setter.setAccessible(true);
        setter.invoke(server, "Mein Server");
        assertEquals("Mein Server", AppleJuiceDialog.getServerDisplayName(information));
        setter.invoke(server, "Neuer Name");
        assertEquals("Neuer Name", AppleJuiceDialog.getServerDisplayName(information));
    }

    @Test
    public void missingServerLeavesStatusTextEmpty() throws Exception {
        assertEquals("", AppleJuiceDialog.getServerDisplayName(information(null)));
    }

    private static Information information(Server server) throws Exception {
        Class<?> type = Class.forName("de.applejuicenet.client.facade.controller.xml.InformationDO");
        Constructor<?> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        Information information = (Information) constructor.newInstance();
        Method setter = type.getDeclaredMethod("setServer", Server.class);
        setter.setAccessible(true);
        setter.invoke(information, server);
        return information;
    }

    private static Server server(String name) throws Exception {
        Class<?> type = Class.forName("de.applejuicenet.client.facade.controller.xml.ServerDO");
        Constructor<?> constructor = type.getDeclaredConstructor(
                int.class, String.class, String.class, String.class, long.class, int.class);
        constructor.setAccessible(true);
        return (Server) constructor.newInstance(1, name, "server.example.org", "2002", 0L, 0);
    }
}
