package com.creativemd.creativecore.common.gui.client.style;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import org.lwjgl.util.Color;

public class SidedColoredDisplayStyle extends DisplayStyle {
   public Color north;
   public Color east;
   public Color south;
   public Color west;

   public SidedColoredDisplayStyle(Color north, Color east, Color south, Color west) {
      this.north = north;
      this.east = east;
      this.south = south;
      this.west = west;
   }

   public void renderStyle(GuiRenderHelper helper, int width, int height) {
      int middleX = width / 2;
      int middleY = height / 2;
      GuiRenderHelper.renderColorTriangle(0, 0, width, 0, middleX, middleY, this.north);
      GuiRenderHelper.renderColorTriangle(middleX, middleY, width, 0, width, height, this.east);
      GuiRenderHelper.renderColorTriangle(0, height, middleX, middleY, width, height, this.south);
      GuiRenderHelper.renderColorTriangle(middleX, middleY, 0, height, 0, 0, this.west);
   }
}
