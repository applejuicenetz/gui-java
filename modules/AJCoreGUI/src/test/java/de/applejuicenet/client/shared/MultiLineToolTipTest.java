package de.applejuicenet.client.shared;

import com.formdev.flatlaf.FlatLightLaf;
import org.junit.Test;
import javax.swing.*;
import static org.junit.Assert.*;

public class MultiLineToolTipTest {
    @Test
    public void usesThemeRendererAndPreservesEscapedLines() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LookAndFeel previous = UIManager.getLookAndFeel();
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
                MultiLineToolTip tip = new MultiLineToolTip();
                assertEquals(new JToolTip().getUI().getClass(), tip.getUI().getClass());
                tip.setTipText("First <value>|Second & last");
                assertEquals("<html>First &lt;value&gt;<br>Second &amp; last</html>", tip.getTipText());
                tip.setTipText(null);
                assertNull(tip.getTipText());
            } catch (Exception e) { throw new AssertionError(e); }
            finally {
                try { UIManager.setLookAndFeel(previous); }
                catch (Exception e) { throw new AssertionError(e); }
            }
        });
    }
}
