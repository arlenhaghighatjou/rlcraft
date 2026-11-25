package lumien.chunkanimator.config;

import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;

public class ChunkAnimatorGuiFactory implements IModGuiFactory {
   public ChunkAnimatorGuiFactory() {
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
      return new ChunkAnimatorConfigGui(parentScreen);
   }
}
