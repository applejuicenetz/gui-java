package de.applejuicenet.client.shared;

import java.awt.*;
import javax.swing.*;


public class MultiLineToolTip extends JToolTip {
  public MultiLineToolTip() {
    setUI(new MultiLineToolTipUI());
  }
}

