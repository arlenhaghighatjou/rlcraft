package net.silentchaos512.scalinghealth.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.silentchaos512.scalinghealth.config.Config;

public class GuiConfigScalingHealth extends GuiConfig {
   public GuiConfigScalingHealth(GuiScreen parent) {
      super(parent, Config.INSTANCE.getConfigElements(), "scalinghealth", false, false, "Scaling Health Config");
   }

   public void initGui() {
      super.initGui();
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   protected void actionPerformed(GuiButton button) {
      super.actionPerformed(button);
   }
}
