package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder;
import org.junit.Test;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import static org.junit.Assert.*;

public class ModifiedAtomicityTest {
    @Test
    public void statusGettersDoNotExposeMutableParserState() throws Exception {
        ModifiedXMLHolder holder = new ModifiedXMLHolder(
                new CoreConnectionSettingsHolder("localhost", 9851, "", true), null);
        InformationDO internal = new InformationDO();
        internal.setCredits(42);
        ServerDO server = new ServerDO(1);
        server.setName("original");
        internal.setServer(server);
        Field info = ModifiedXMLHolder.class.getDeclaredField("information");
        info.setAccessible(true);
        info.set(holder, internal);
        var network = new de.applejuicenet.client.facade.shared.NetworkInfo();
        network.setAjTotalUsers(7);
        Field net = ModifiedXMLHolder.class.getDeclaredField("netInfo");
        net.setAccessible(true);
        net.set(holder, network);
        InformationDO delivered = (InformationDO) holder.getInformation();
        delivered.setCredits(99);
        ((ServerDO) delivered.getServer()).setName("changed");
        holder.getNetworkInfo().setAjTotalUsers(99);
        assertEquals(42, holder.getInformation().getCredits());
        assertEquals("original", holder.getInformation().getServer().getName());
        assertEquals(7, holder.getNetworkInfo().getAJTotalUsers());
        internal.setCredits(123);
        network.setAjTotalUsers(123);
        assertEquals(99, delivered.getCredits());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void badDiffDoesNotPublishEarlierChangesAndNextDiffWorks() throws Exception {
        ModifiedXMLHolder holder = new ModifiedXMLHolder(
                new CoreConnectionSettingsHolder("localhost", 9851, "", true), null);
        Field info = ModifiedXMLHolder.class.getDeclaredField("information");
        info.setAccessible(true);
        info.set(holder, new InformationDO());
        Field network = ModifiedXMLHolder.class.getDeclaredField("netInfo");
        network.setAccessible(true);
        network.set(holder, new de.applejuicenet.client.facade.shared.NetworkInfo());
        Field field = ModifiedXMLHolder.class.getDeclaredField("serverMap");
        field.setAccessible(true);
        ServerDO server = new ServerDO(1);
        server.setName("before");
        ((Map<Integer, Object>) field.get(holder)).put(1, server);
        Method apply = ModifiedXMLHolder.class.getDeclaredMethod("applyDiff", String.class);
        apply.setAccessible(true);
        String valid = "<server id='1' name='after' host='localhost' lastseen='1' port='9851' connectiontry='0'/>";
        for (String bad : new String[]{"<applejuice>" + valid + "<server id='bad'/></applejuice>",
                "<applejuice>" + valid + "<broken>"}) {
            try {
                apply.invoke(holder, bad);
                fail("Broken diff must fail");
            } catch (InvocationTargetException expected) {
                assertEquals("before", holder.getServer().get(1).getName());
                assertEquals("before", server.getName());
            }
        }
        apply.invoke(holder, "<applejuice>" + valid + "</applejuice>");
        assertEquals("after", holder.getServer().get(1).getName());
    }
}
