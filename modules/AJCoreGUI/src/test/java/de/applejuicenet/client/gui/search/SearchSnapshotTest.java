package de.applejuicenet.client.gui.search;

import de.applejuicenet.client.fassade.entity.Search;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import org.junit.Test;

import javax.swing.SwingUtilities;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class SearchSnapshotTest {
    private Search search(String text) throws Exception {
        Class<?> type = Class.forName("de.applejuicenet.client.fassade.controller.xml.SearchDO");
        Constructor<?> constructor = type.getDeclaredConstructor(int.class);
        constructor.setAccessible(true);
        Search search = (Search) constructor.newInstance(1);
        Method setter = type.getDeclaredMethod("setSuchText", String.class);
        setter.setAccessible(true);
        setter.invoke(search, text);
        return search;
    }

    @Test
    public void existingPanelAcceptsNewSnapshot() throws Exception {
        LanguageSelector.getInstance(Paths.get("../../resources/language/deutsch.properties")
                .toAbsolutePath().normalize().toString());
        Search first = search("before");
        Search next = search("after");
        SearchResultPanel[] panel = new SearchResultPanel[1];
        SwingUtilities.invokeAndWait(() -> panel[0] = new SearchResultPanel(first));
        java.lang.reflect.Field buttons = SearchResultPanel.class.getDeclaredField("filterButtons");
        buttons.setAccessible(true);
        SwingUtilities.invokeAndWait(() -> {
            try {
                ((javax.swing.JToggleButton[]) buttons.get(panel[0]))[0].doClick(0);
            } catch (IllegalAccessException ex) {
                throw new AssertionError(ex);
            }
        });
        Method update = SearchResultPanel.class.getDeclaredMethod("setSearchSnapshot", Search.class);
        SwingUtilities.invokeAndWait(() -> {
            try {
                update.invoke(panel[0], next);
            } catch (ReflectiveOperationException ex) {
                throw new AssertionError(ex);
            }
        });
        assertSame(next, panel[0].getSearch());
    }
}
