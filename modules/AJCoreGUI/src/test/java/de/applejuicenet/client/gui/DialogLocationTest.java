package de.applejuicenet.client.gui;

import org.junit.Test;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Point;
import java.awt.Robot;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class DialogLocationTest {
    @Test
    public void alignsRightInsideOwnerAndFollowsItsMovement() throws Exception {
        checkPlacement(DialogLocation::alignRight, new Point(440, 210), new Point(500, 270));
    }

    @Test
    public void centersOverOffCenterWindowAndFollowsItsMovement() throws Exception {
        checkPlacement(DialogLocation::center, new Point(240, 210), new Point(300, 270));
    }

    private static void checkPlacement(Consumer<JDialog> position, Point initial, Point moved) throws Exception {
        JFrame[] owner = new JFrame[1];
        JDialog[] dialog = new JDialog[1];
        Robot robot = new Robot();
        SwingUtilities.invokeAndWait(() -> {
            owner[0] = new JFrame();
            dialog[0] = new JDialog(owner[0]);
            owner[0].setBounds(40, 60, 600, 400);
            owner[0].setVisible(true);
            dialog[0].setSize(200, 100);
        });
        try {
            robot.waitForIdle();
            SwingUtilities.invokeAndWait(() -> {
                assertEquals(new Point(40, 60), owner[0].getLocation());
                position.accept(dialog[0]);
                assertEquals(initial, dialog[0].getLocation());
                owner[0].setLocation(100, 120);
            });
            robot.waitForIdle();
            SwingUtilities.invokeAndWait(() -> {
                assertEquals(new Point(100, 120), owner[0].getLocation());
                position.accept(dialog[0]);
                assertEquals(moved, dialog[0].getLocation());
            });
        } finally {
            SwingUtilities.invokeAndWait(() -> {
                dialog[0].dispose();
                owner[0].dispose();
            });
        }
    }

    @Test
    public void nestedDialogUsesMainOwnerInsteadOfImmediateDialog() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JFrame owner = new JFrame();
            JDialog options = new JDialog(owner);
            JDialog dialog = new JDialog(options);
            try {
                owner.setBounds(40, 60, 600, 400);
                owner.setVisible(true);
                options.setBounds(10, 20, 300, 200);
                options.setVisible(true);
                dialog.setSize(200, 100);
                assertSame(owner, DialogLocation.getReference(options));
                DialogLocation.center(dialog);
                assertEquals(new Point(240, 210), dialog.getLocation());
            } finally {
                dialog.dispose();
                options.dispose();
                owner.dispose();
            }
        });
    }

    @Test
    public void componentReferenceUsesItsTopLevelWindow() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JFrame owner = new JFrame();
            JPanel panel = new JPanel();
            try {
                owner.add(panel);
                owner.setBounds(40, 60, 600, 400);
                owner.setVisible(true);
                assertSame(owner, DialogLocation.getReference(panel));
            } finally {
                owner.dispose();
            }
        });
    }

    @Test
    public void startupWithoutVisibleOwnerFallsBackToScreen() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JFrame owner = new JFrame();
            JDialog dialog = new JDialog(owner);
            JDialog expected = new JDialog(owner);
            try {
                dialog.setSize(200, 100);
                expected.setSize(200, 100);
                assertNull(DialogLocation.getReference(owner));
                assertNull(DialogLocation.getReference(null));
                assertNull(DialogLocation.getReference(new JPanel()));
                expected.setLocationRelativeTo(null);
                DialogLocation.center(dialog);
                assertEquals(expected.getLocation(), dialog.getLocation());
            } finally {
                dialog.dispose();
                expected.dispose();
                owner.dispose();
            }
        });
    }
}
