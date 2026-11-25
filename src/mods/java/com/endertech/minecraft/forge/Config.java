package com.endertech.minecraft.forge;

import com.endertech.common.IntBounds;
import com.endertech.minecraft.forge.configs.ModConfig;
import com.endertech.minecraft.forge.configs.ModConfigGui;
import com.endertech.minecraft.forge.regs.ForgeRegistrator;
import com.endertech.minecraft.forge.world.ForgeWorld;
import com.endertech.minecraft.forge.world.Wind;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config extends ModConfig {
   public Config(String modId, FMLPreInitializationEvent event) {
      super(modId, event);
   }

   public void init() {
      String category = ForgeWorld.SmokeContainers.class.getSimpleName();
      ForgeWorld.SmokeContainers.maxVentPipeLength = this.getInt(category, "maxVentPipeLength", 32, IntBounds.from(Integer.valueOf(1), Integer.valueOf(64)), "Maximum length of the vent pipe for smoke or gas transmition.");
      ForgeWorld.SmokeContainers.ventInhaleDistance = this.getInt(category, "ventInhaleDistance", 4, IntBounds.from(Integer.valueOf(1), Integer.valueOf(16)), "Maximum distance from vent to the gas block to inhale.");
      ForgeWorld.SmokeContainers.maxBlocksInMultiblock = this.getInt(category, "maxBlocksInMultiblock", 81, IntBounds.from(Integer.valueOf(1), Integer.valueOf(512)), "Maximum amount of blocks that will be checked when searching for multiblock boundaries.");
      Wind.defaultWind = new Wind(this, 0, 0.1F, 0.01F, 0.0015F);
      category = ForgeRegistrator.class.getSimpleName();
      ForgeRegistrator.registerDisabledUnits = this.getBool(category, "registerDisabledUnits", true, "If set to False, all disabled units will be completely removed from the game.");
      ForgeMain.debug = this.getBool("Debug", "enabled", false, "Enable/Disable debug mode.");
   }

   public static class Factory extends ModConfigGui.Factory {
      public Factory() {
      }

      public GuiScreen createConfigGui(GuiScreen parent) {
         return new ModConfigGui(ForgeMain.instance, parent);
      }
   }
}
