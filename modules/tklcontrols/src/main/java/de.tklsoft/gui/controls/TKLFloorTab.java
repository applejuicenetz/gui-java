package de.tklsoft.gui.controls;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TKLFloorTab extends JPanel {

   private final JPanel northPanel = new JPanel(new GridBagLayout());
   private final JPanel southPanel = new JPanel(new GridBagLayout());
   private final CardLayout cardLayout = new CardLayout();
   private final JPanel centerPanel;
   private final ArrayList<TKLFloorMainMenu> mainMenuButtons;
   private final HashMap<String, Integer> yIndizes;
   private final GridBagConstraints constraints;
   private int maxY;


   public TKLFloorTab() {
      this.centerPanel = new JPanel(this.cardLayout);
      this.mainMenuButtons = new ArrayList<>();
      this.yIndizes = new HashMap<>();
      this.constraints = new GridBagConstraints();
      this.maxY = -1;
      this.setLayout(new BorderLayout());
      this.add(this.northPanel, "North");
      this.add(this.southPanel, "South");
      this.add(this.centerPanel, "Center");
      this.constraints.anchor = 11;
      this.constraints.fill = 1;
      this.constraints.insets = new Insets(0, 0, 0, 0);
      this.constraints.weightx = 1.0D;
      this.constraints.weighty = 0.0D;
      this.constraints.gridx = 0;
   }

   public void showMenu(TKLFloorMainMenu tKLFloorMainMenu) {
      String name = tKLFloorMainMenu.getName();
      if(name != null && name.length() != 0) {
         Integer indexTmp = this.yIndizes.get(name.toLowerCase());
         if(indexTmp != null) {
            int index = indexTmp;

            for(int i = 0; i < this.yIndizes.size(); ++i) {
               JButton northButton = (this.mainMenuButtons.get(i)).getNorthButton();
               northButton.setSelected(false);
               JButton southButton = (this.mainMenuButtons.get(i)).getSouthButton();
               southButton.setSelected(false);
               if(i <= index) {
                  northButton.setVisible(true);
                  if(i != index) {
                     northButton.setBackground(Color.LIGHT_GRAY);
                  } else {
                     northButton.setBackground(Color.WHITE);
                  }

                  southButton.setVisible(false);
               } else {
                  northButton.setVisible(false);
                  southButton.setVisible(true);
               }
            }

            this.cardLayout.show(this.centerPanel, name.toLowerCase());
         }

      }
   }

   public void addMenu(TKLFloorMainMenu tKLFloorMainMenu) {
      String name = tKLFloorMainMenu.getName();
      Iterator<TKLFloorMainMenu> northButton = this.mainMenuButtons.iterator();

      TKLFloorMainMenu southButton;
      do {
         if(!northButton.hasNext()) {
            JButton northButton1 = tKLFloorMainMenu.getNorthButton();
            JButton southButton1 = tKLFloorMainMenu.getSouthButton();
            ++this.maxY;
            this.constraints.gridy = this.maxY;
            northButton1.setVisible(false);
            this.northPanel.add(northButton1, this.constraints);
            this.southPanel.add(southButton1, this.constraints);
            northButton1.setVisible(false);
            this.mainMenuButtons.add(tKLFloorMainMenu);
            this.yIndizes.put(name.toLowerCase(), this.maxY);
            this.centerPanel.add(name.toLowerCase(), tKLFloorMainMenu.getMenuPanel());
            return;
         }

         southButton = northButton.next();
      } while(!southButton.getName().equalsIgnoreCase(name));

      throw new RuntimeException("Menüname '" + name + "' bereits definiert!");
   }
}
