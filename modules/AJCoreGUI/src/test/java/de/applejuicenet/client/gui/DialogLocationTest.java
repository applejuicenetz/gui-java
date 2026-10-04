package de.applejuicenet.client.gui;

import org.junit.Test;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Point;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class DialogLocationTest {
    @Test
    public void alignsRightInsideOwnerAndFollowsItsMovement() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JFrame owner = new JFrame();
            JDialog dialog = new JDialog(owner);
            try {
                owner.setBounds(40, 60, 600, 400);
                owner.setVisible(true);
                dialog.setSize(200, 100);
                DialogLocation.alignRight(dialog);
                assertEquals(new Point(440, 210), dialog.getLocation());
                owner.setLocation(100, 120);
                DialogLocation.alignRight(dialog);
                assertEquals(new Point(500, 270), dialog.getLocation());
            } finally {
                dialog.dispose();
                owner.dispose();
            }
        });
    }

    @Test
    public void centersOverOffCenterWindowAndFollowsItsMovement() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JFrame owner = new JFrame();
            JDialog dialog = new JDialog(owner);
            try {
                owner.setBounds(40, 60, 600, 400);
                owner.setVisible(true);
                dialog.setSize(200, 100);
                DialogLocation.center(dialog);
                assertEquals(new Point(240, 210), dialog.getLocation());
                owner.setLocation(100, 120);
                DialogLocation.center(dialog);
                assertEquals(new Point(300, 270), dialog.getLocation());
            } finally {
                dialog.dispose();
                owner.dispose();
            }
        });
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
