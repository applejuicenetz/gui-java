package de.tklsoft.gui.controls;

import de.tklsoft.gui.controls.DelegationObject;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class TKLTable extends JTable {

   public TKLTable() {
      this.init();
   }

   public TKLTable(TableModel tableModel) {
      super(tableModel);
      this.init();
   }

   private void init() {
      this.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent keyEvent) {
            super.keyReleased(keyEvent);
            DelegationObject.notifyPapas(TKLTable.this.getParent(), keyEvent);
         }
      });
   }
}
