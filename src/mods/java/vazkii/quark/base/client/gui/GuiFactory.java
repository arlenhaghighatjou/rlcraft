package vazkii.quark.base.client.gui;

import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;
import vazkii.quark.base.client.gui.config.GuiConfigRoot;

public class GuiFactory implements IModGuiFactory {
   public GuiFactory() {
   }

   public void initialize(Minecraft minecraftInstance) {
   }

   public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
      return null;
   }

   public boolean hasConfigGui() {
      return true;
   }

   public GuiScreen createConfigGui(GuiScreen parentScreen) {
      return new GuiConfigRoot(parentScreen);
   }
}
