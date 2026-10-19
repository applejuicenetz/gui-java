package de.applejuicenet.client.gui.options;

import org.junit.Test;

import de.applejuicenet.client.facade.shared.RefreshIntervals;

import javax.swing.*;

import static org.junit.Assert.*;

public class ODViewPanelTest {
    @Test
    public void refreshIntervalFieldsStartWithTheSavedValuesAndStayInRange() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ODViewPanel panel = new ODViewPanel();
            JSpinner poll = panel.corePollSpinner();
            JSpinner partlist = panel.partlistSpinner();
            SpinnerNumberModel pollModel = (SpinnerNumberModel) poll.getModel();
            SpinnerNumberModel partlistModel = (SpinnerNumberModel) partlist.getModel();
            assertEquals(RefreshIntervals.CORE_POLL_MIN_MS / 1000.0, ((Number) pollModel.getMinimum()).doubleValue(), 0.0001);
            assertEquals(RefreshIntervals.CORE_POLL_MAX_MS / 1000.0, ((Number) pollModel.getMaximum()).doubleValue(), 0.0001);
            assertEquals(RefreshIntervals.PARTLIST_REFRESH_MIN_MS / 1000.0, ((Number) partlistModel.getMinimum()).doubleValue(), 0.0001);
            assertEquals(RefreshIntervals.PARTLIST_REFRESH_MAX_MS / 1000.0, ((Number) partlistModel.getMaximum()).doubleValue(), 0.0001);
            assertFalse("opening the page changes nothing", panel.isDirty());
            poll.setValue(((Number) pollModel.getMaximum()).doubleValue());
            assertTrue(panel.isDirty());
        });
    }

    @Test
    public void panelHasContentAndTitle() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ODViewPanel panel = new ODViewPanel();
            assertTrue(panel.getComponentCount() > 0);
            assertNotNull(panel.getMenuText());
        });
    }
}
