package net.silentchaos512.lib.gui.config;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;

/** @deprecated */
@Deprecated
public abstract class GuiFactorySL implements IModGuiFactory {
   public GuiFactorySL() {
   }

   public abstract Class<? extends GuiScreen> mainConfigGuiClass();

   public void initialize(Minecraft minecraftInstance) {
   }

   public boolean hasConfigGui() {
      return true;
   }

   public GuiScreen createConfigGui(GuiScreen parentScreen) {
      try {
         return (GuiScreen)this.mainConfigGuiClass().getConstructor(GuiScreen.class).newInstance(parentScreen);
      } catch (Exception var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
      return new HashSet();
   }
}
