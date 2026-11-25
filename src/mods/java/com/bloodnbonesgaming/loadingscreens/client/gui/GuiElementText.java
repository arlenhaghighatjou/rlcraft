package com.bloodnbonesgaming.loadingscreens.client.gui;

import net.minecraft.client.Minecraft;

public class GuiElementText extends GuiElementBase {
   private final String text;
   private int color;

   public GuiElementText(EnumGuiLocation location, String text, int color) {
      super(location);
      this.text = text;
      this.color = color;
   }

   public GuiElementText(EnumGuiLocation location, String text) {
      super(location);
      this.text = text;
      this.color = 16777215;
   }

   public void setColor(int color) {
      this.color = color;
   }

   public void render(Minecraft minecraft, int guiWidth, int guiHeight) {
      int width = minecraft.fontRenderer.getStringWidth(this.text);
      int height = minecraft.fontRenderer.FONT_HEIGHT;
      minecraft.fontRenderer.drawStringWithShadow(this.text, (float)((double)this.location.getX(guiWidth, width) + this.relXOffset * (double)guiWidth + (double)this.absXOffset), (float)((double)this.location.getY(guiHeight, height) + this.relYOffset * (double)guiWidth + (double)this.absYOffset), this.color);
   }
}
