package de.applejuicenet.client.gui.controller;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import static org.junit.Assert.*;

public class PropertiesResetTest {
    @Rule public TemporaryFolder files = new TemporaryFolder();

    private PropertiesManager load(Path file, BooleanSupplier confirm) throws Exception {
        Constructor<PropertiesManager> constructor = PropertiesManager.class
                .getDeclaredConstructor(String.class, BooleanSupplier.class);
        constructor.setAccessible(true);
        return constructor.newInstance(file.toString(), confirm);
    }

    @Test
    public void decliningResetPreservesBrokenFile() throws Exception {
        Path file = files.newFile().toPath();
        String broken = "invalid=\\uZZZZ\n";
        Files.writeString(file, broken);
        AtomicInteger prompts = new AtomicInteger();
        try {
            load(file, () -> { prompts.incrementAndGet(); return false; });
            fail("Declined reset must abort initialization");
        } catch (InvocationTargetException ex) {
            assertTrue(ex.getCause() instanceof IllegalStateException);
        }
        assertEquals(1, prompts.get());
        assertEquals(broken, Files.readString(file));
        assertFalse(Files.exists(file.resolveSibling(file.getFileName() + ".bak")));
    }

    @Test
    public void resetKeepsExistingBackup() throws Exception {
        Path file = files.newFile().toPath();
        Files.writeString(file, "invalid=\\uZZZZ\n");
        Path backup = file.resolveSibling(file.getFileName() + ".bak");
        Files.writeString(backup, "previous recovery copy");
        load(file, () -> true);
        assertEquals("previous recovery copy", Files.readString(backup));
        assertEquals("invalid=\\uZZZZ\n", Files.readString(file.resolveSibling(file.getFileName() + ".bak.1")));
    }

    @Test
    public void missingFileNeedsNoResetConfirmation() throws Exception {
        Path file = files.getRoot().toPath().resolve("missing.properties");
        load(file, () -> { throw new AssertionError("First start must not ask for reset"); });
        assertFalse(Files.exists(file));
    }

    @Test
    public void acceptingResetBacksUpBrokenFileAndLoadsDefaults() throws Exception {
        Path file = files.newFile().toPath();
        String broken = "invalid=\\uZZZZ\n";
        Files.writeString(file, broken);
        AtomicInteger prompts = new AtomicInteger();
        PropertiesManager manager = load(file, () -> { prompts.incrementAndGet(); return true; });
        assertEquals(1, prompts.get());
        assertEquals("english", manager.getSprache());
        assertEquals(broken, Files.readString(file.resolveSibling(file.getFileName() + ".bak")));
        assertFalse(Files.readString(file).contains("ZZZZ"));
    }
}
