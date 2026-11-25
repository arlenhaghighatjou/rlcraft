package com.bloodnbonesgaming.loadingscreens.client.gui;

import net.minecraft.client.Minecraft;

public abstract class GuiElementBase {
   protected final EnumGuiLocation location;
   protected double relXOffset = 0.0D;
   protected double relYOffset = 0.0D;
   protected int absXOffset = 0;
   protected int absYOffset = 0;

   public GuiElementBase(EnumGuiLocation location) {
      this.location = location;
   }

   public void setRelXOffset(double offset) {
      this.relXOffset = offset;
   }

   public void setRelYOffset(double offset) {
      this.relYOffset = offset;
   }

   public void setAbsXOffset(int offset) {
      this.absXOffset = offset;
   }

   public void setAbsYOffset(int offset) {
      this.absYOffset = offset;
   }

   public abstract void render(Minecraft var1, int var2, int var3);
}
