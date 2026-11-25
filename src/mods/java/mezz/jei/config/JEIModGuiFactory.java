package mezz.jei.config;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;

public class JEIModGuiFactory implements IModGuiFactory {
   public JEIModGuiFactory() {
   }

   public void initialize(Minecraft minecraftInstance) {
   }

   public boolean hasConfigGui() {
      return true;
   }

   public GuiScreen createConfigGui(GuiScreen parentScreen) {
      return new JEIModConfigGui(parentScreen);
   }

   @Nullable
   public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
      return null;
   }
}
