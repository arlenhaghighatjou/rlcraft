package gigaherz.toolbelt.client.config;

import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;

public class ConfigGuiFactory implements IModGuiFactory {
   public ConfigGuiFactory() {
   }

   public void initialize(Minecraft minecraftInstance) {
   }

   public boolean hasConfigGui() {
      return true;
   }

   public GuiScreen createConfigGui(GuiScreen parentScreen) {
      return new ConfigGui(parentScreen);
   }

   /** @deprecated */
   @Deprecated
   public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
      return null;
   }
}
