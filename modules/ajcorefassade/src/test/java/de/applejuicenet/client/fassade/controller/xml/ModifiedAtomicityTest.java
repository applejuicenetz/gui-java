package de.applejuicenet.client.fassade.controller.xml;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import org.junit.Test;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import static org.junit.Assert.*;

public class ModifiedAtomicityTest {
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
        network.set(holder, new de.applejuicenet.client.fassade.shared.NetworkInfo());
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
