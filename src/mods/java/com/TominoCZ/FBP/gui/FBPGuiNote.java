package com.TominoCZ.FBP.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class FBPGuiNote extends Gui {
   Minecraft mc = Minecraft.getMinecraft();

   public FBPGuiNote() {
      ScaledResolution scaledResolutionIn = new ScaledResolution(this.mc);
      int width = scaledResolutionIn.getScaledWidth();
      this.drawCenteredString(this.mc.fontRenderer, "§LFreeze Effect", width / 2, 5, Integer.parseInt("FFAA00", 16));
   }
}
