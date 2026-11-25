package me.ichun.mods.ichunutil.common.core.config;

import java.util.Set;
import me.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;

public class GenericModGuiFactory implements IModGuiFactory {
   public GenericModGuiFactory() {
   }

   public void initialize(Minecraft mc) {
   }

   public boolean hasConfigGui() {
      return true;
   }

   public GuiScreen createConfigGui(GuiScreen parentScreen) {
      return new GuiConfigs(parentScreen);
   }

   public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
      return null;
   }
}
