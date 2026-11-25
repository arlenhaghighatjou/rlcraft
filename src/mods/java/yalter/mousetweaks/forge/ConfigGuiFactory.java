package yalter.mousetweaks.forge;

import java.util.Set;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;

public class ConfigGuiFactory implements IModGuiFactory {
   public ConfigGuiFactory() {
   }

   public void initialize(bib minecraftInstance) {
   }

   public boolean hasConfigGui() {
      return true;
   }

   public blk createConfigGui(blk guiScreen) {
      return new ConfigGui(guiScreen);
   }

   public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
      return null;
   }
}
