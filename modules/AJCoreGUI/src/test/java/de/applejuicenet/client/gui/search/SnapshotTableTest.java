package de.applejuicenet.client.gui.search;

import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.upload.table.UploadActiveTableModel;
import de.applejuicenet.client.gui.upload.table.UploadWaitingTableModel;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.*;

public class SnapshotTableTest {
    private Upload upload(int status, int speed) throws Exception {
        Class<?> type = Class.forName("de.applejuicenet.client.fassade.controller.xml.UploadDO");
        Constructor<?> constructor = type.getDeclaredConstructor(int.class);
        constructor.setAccessible(true);
        Upload upload = (Upload) constructor.newInstance(1);
        for (String name : new String[]{"setStatus", "setSpeed"}) {
            Method method = type.getDeclaredMethod(name, int.class);
            method.setAccessible(true);
            method.invoke(upload, name.equals("setStatus") ? status : speed);
        }
        return upload;
    }

    @Test
    public void tableReplacesSameIdSnapshotAndMovesStatus() throws Exception {
        LanguageSelector.getInstance(Paths.get("../../resources/language/deutsch.properties")
                .toAbsolutePath().normalize().toString());
        UploadActiveTableModel active = new UploadActiveTableModel();
        UploadWaitingTableModel waiting = new UploadWaitingTableModel();
        active.setUploads(Map.of(1, upload(Upload.AKTIVE_UEBERTRAGUNG, 100)));
        Upload next = upload(Upload.AKTIVE_UEBERTRAGUNG, 200);
        active.setUploads(Map.of(1, next));
        assertSame(next, active.getContent().get(0));
        Upload queued = upload(0, 0);
        active.setUploads(Map.of(1, queued));
        waiting.setUploads(Map.of(1, queued));
        assertEquals(0, active.getRowCount());
        assertSame(queued, waiting.getContent().get(0));
    }
}
