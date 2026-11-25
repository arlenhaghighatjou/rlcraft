package org.orecruncher.lib.gui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.Color;

@SideOnly(Side.CLIENT)
public class StandardPanel extends Panel<StandardPanel> {
   public StandardPanel() {
   }

   public StandardPanel(int width, int height) {
      super(width, height);
   }

   public StandardPanel(int width, int height, Color fore, Color back, Color border) {
      super(width, height, fore, back, border);
   }
}
