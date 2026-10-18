package de.applejuicenet.client.gui;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Kleine vektorgezeichnete Icons fuer die Statusleiste. Ohne Farbe wird die Vordergrundfarbe des Labels genutzt.
 */
final class StatusIcons {
    private static final int SIZE = 12;
    private static final Color DOWN = new Color(0x3B82F6);
    private static final Color UP = new Color(0xF59E0B);

    private interface Painter {
        void paint(Graphics2D g);
    }

    private StatusIcons() {
    }

    /** Aktuelle Download-Geschwindigkeit. */
    static Icon speedDown() {
        return icon(DOWN, g -> {
            g.draw(new Line2D.Float(6, 1, 6, 10));
            g.draw(head(2.5f, 6.5f, 6, 10, 9.5f, 6.5f));
        });
    }

    /** Aktuelle Upload-Geschwindigkeit. */
    static Icon speedUp() {
        return icon(UP, g -> {
            g.draw(new Line2D.Float(6, 11, 6, 2));
            g.draw(head(2.5f, 5.5f, 6, 2, 9.5f, 5.5f));
        });
    }

    /** Gesamter Download: Pfeil auf eine Ablage. */
    static Icon totalDown() {
        return icon(DOWN, g -> {
            g.draw(new Line2D.Float(6, 0.5f, 6, 7.5f));
            g.draw(head(2.5f, 4.5f, 6, 8, 9.5f, 4.5f));
            g.draw(new Line2D.Float(2, 11, 10, 11));
        });
    }

    /** Gesamter Upload: Pfeil von einer Ablage weg. */
    static Icon totalUp() {
        return icon(UP, g -> {
            g.draw(new Line2D.Float(2, 1, 10, 1));
            g.draw(new Line2D.Float(6, 11.5f, 6, 4.5f));
            g.draw(head(2.5f, 7.5f, 6, 4, 9.5f, 7.5f));
        });
    }

    /** Externe IP. */
    static Icon globe() {
        return icon(null, g -> {
            g.draw(new Ellipse2D.Float(1, 1, 10, 10));
            g.draw(new Line2D.Float(1, 6, 11, 6));
            g.draw(new Ellipse2D.Float(4, 1, 4, 10));
        });
    }

    /** Credits: Diamant. */
    static Icon coin() {
        return icon(null, g -> {
            Path2D gem = new Path2D.Float();
            gem.moveTo(3.5f, 2);
            gem.lineTo(8.5f, 2);
            gem.lineTo(11, 5);
            gem.lineTo(6, 10.5f);
            gem.lineTo(1, 5);
            gem.closePath();
            g.draw(gem);
            g.draw(new Line2D.Float(1, 5, 11, 5));
            g.draw(new Line2D.Float(3.5f, 2, 4.5f, 5));
            g.draw(new Line2D.Float(8.5f, 2, 7.5f, 5));
            g.draw(new Line2D.Float(4.5f, 5, 6, 10.5f));
            g.draw(new Line2D.Float(7.5f, 5, 6, 10.5f));
        });
    }

    /** Offene Verbindungen. */
    static Icon connections() {
        return icon(null, g -> {
            g.draw(new Line2D.Float(6, 3, 2.5f, 9));
            g.draw(new Line2D.Float(6, 3, 9.5f, 9));
            g.draw(new Line2D.Float(2.5f, 9, 9.5f, 9));
            for (float[] p : new float[][] {{6, 2.5f}, {2.5f, 9}, {9.5f, 9}}) {
                g.fill(new Ellipse2D.Float(p[0] - 1.8f, p[1] - 1.8f, 3.6f, 3.6f));
            }
        });
    }

    /** Server. */
    static Icon server() {
        return icon(null, g -> {
            g.draw(new RoundRectangle2D.Float(1.5f, 1.5f, 9, 4, 1.5f, 1.5f));
            g.draw(new RoundRectangle2D.Float(1.5f, 6.5f, 9, 4, 1.5f, 1.5f));
            g.fill(new Ellipse2D.Float(3, 2.7f, 1.6f, 1.6f));
            g.fill(new Ellipse2D.Float(3, 7.7f, 1.6f, 1.6f));
        });
    }

    private static Path2D head(float x1, float y1, float x2, float y2, float x3, float y3) {
        Path2D path = new Path2D.Float();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        return path;
    }

    private static Icon icon(Color color, Painter painter) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics graphics, int x, int y) {
                Graphics2D g = (Graphics2D) graphics.create();
                try {
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                    g.setColor(color != null ? color : c.getForeground());
                    g.translate(x, y);
                    g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    painter.paint(g);
                } finally {
                    g.dispose();
                }
            }

            @Override
            public int getIconWidth() {
                return SIZE;
            }

            @Override
            public int getIconHeight() {
                return SIZE;
            }
        };
    }
}
