package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.tools.datastructures.Color;

public class GUIDarkenedBackground extends GUIGradient {
   public GUIDarkenedBackground(GUIScreen screen) {
      super(screen, 0.0D, 0.0D, 1.0D, 1.0D, Color.BLACK.copy().setAF(0.85F));
   }
}
