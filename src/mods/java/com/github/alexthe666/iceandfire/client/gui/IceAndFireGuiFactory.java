package com.github.alexthe666.iceandfire.client.gui;

import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;

public class IceAndFireGuiFactory implements IModGuiFactory {
   public IceAndFireGuiFactory() {
   }

   public void initialize(Minecraft minecraftInstance) {
   }

   public boolean hasConfigGui() {
      return true;
   }

   public GuiScreen createConfigGui(GuiScreen parentScreen) {
      return new GuiIceAndFireConfig(parentScreen);
   }

   public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
      return null;
   }
}
