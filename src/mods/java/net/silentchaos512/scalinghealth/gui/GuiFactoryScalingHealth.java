package net.silentchaos512.scalinghealth.gui;

import net.minecraft.client.gui.GuiScreen;
import net.silentchaos512.lib.gui.config.GuiFactorySL;

public class GuiFactoryScalingHealth extends GuiFactorySL {
   public GuiFactoryScalingHealth() {
   }

   public Class<? extends GuiScreen> mainConfigGuiClass() {
      return GuiConfigScalingHealth.class;
   }

   public GuiScreen createMainConfigGui(GuiScreen parentScreen) {
      return new GuiConfigScalingHealth(parentScreen);
   }
}
