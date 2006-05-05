package de.applejuicenet.client.gui.plugins.jabber.control;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

public class LinkEditorKit extends StyledEditorKit
{
   public static final Object LINK = new StringBuffer("LINK");
   private static LinkHandler linkHandler = new LinkHandler();

   public LinkEditorKit()
   {
   }

   public void install(JEditorPane p)
   {
      super.install(p);
      p.addMouseListener(linkHandler);
      p.addMouseMotionListener(linkHandler);
   }

   public void deinstall(JEditorPane p)
   {
      p.removeMouseListener(linkHandler);
      p.removeMouseMotionListener(linkHandler);
      super.deinstall(p);
   }

   private static class LinkHandler extends MouseAdapter implements MouseMotionListener
   {
      private Element activeElement;

      private Element characterElementAt(MouseEvent e)
      {
         JEditorPane     p = (JEditorPane) e.getComponent();
         Position.Bias[] bias = new Position.Bias[1];
         int             position = p.getUI().viewToModel(p, e.getPoint(), bias);

         if(bias[0] == Position.Bias.Backward && position != 0)
         {
            --position;
         }

         Element c = ((StyledDocument) p.getDocument()).getCharacterElement(position);

         return c;
      }

      public void mouseMoved(MouseEvent e)
      {
         JEditorPane p = (JEditorPane) e.getComponent();

         if(!p.isEditable())
         {
            Element c = characterElementAt(e);
            Object  target = c.getAttributes().getAttribute(LINK);

            if(!(target instanceof URL))
            {
               target = null;
            }

            if(target != null)
            {
               p.fireHyperlinkUpdate(new HyperlinkEvent(p, HyperlinkEvent.EventType.ENTERED, (URL) target, null, c));
            }
            else
            {
               p.fireHyperlinkUpdate(new HyperlinkEvent(p, HyperlinkEvent.EventType.EXITED, (URL) target, null, c));
            }
         }
      }

      public void mouseDragged(MouseEvent e)
      {
      }

      public void mouseEntered(MouseEvent e)
      {
      }

      public void mousePressed(MouseEvent e)
      {
         if(!SwingUtilities.isLeftMouseButton(e))
         {
            return;
         }

         JEditorPane p = (JEditorPane) e.getComponent();

         if(p.isEditable())
         {
            return;
         }

         Element c = characterElementAt(e);

         if(c != null && c.getAttributes().getAttribute(LINK) != null)
         {
            activeElement = c;
         }
      }

      public void mouseReleased(MouseEvent e)
      {
         if(!SwingUtilities.isLeftMouseButton(e) || activeElement == null)
         {
            return;
         }

         JEditorPane p = (JEditorPane) e.getComponent();
         Element     c = characterElementAt(e);

         if(!p.isEditable() && c == activeElement)
         {
            activeElement = null;
            Object target = c.getAttributes().getAttribute(LINK);

            if(!(target instanceof URL))
            {
               target = null;
            }

            p.fireHyperlinkUpdate(new HyperlinkEvent(p, HyperlinkEvent.EventType.ACTIVATED, (URL) target, null, c));
         }
      }
   }
}
