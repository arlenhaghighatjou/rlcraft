package net.ilexiconn.llibrary.client.gui;

import java.util.Set;
import net.ilexiconn.llibrary.client.gui.config.LLibraryConfigGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LLibraryGUIFactory implements IModGuiFactory {
   public LLibraryGUIFactory() {
   }

   public void initialize(Minecraft mc) {
   }

   public boolean hasConfigGui() {
      return true;
   }

   public GuiScreen createConfigGui(GuiScreen parentScreen) {
      return new LLibraryConfigGUI(parentScreen);
   }

   public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
      return null;
   }
}
