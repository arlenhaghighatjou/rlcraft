package invtweaks.forge;

import invtweaks.InvTweaksGuiSettings;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModGuiFactory implements IModGuiFactory {
   public ModGuiFactory() {
   }

   public void initialize(Minecraft minecraftInstance) {
   }

   public boolean hasConfigGui() {
      return true;
   }

   @NotNull
   public GuiScreen createConfigGui(GuiScreen parentScreen) {
      return new InvTweaksGuiSettings(parentScreen);
   }

   @Nullable
   public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
      return null;
   }
}
