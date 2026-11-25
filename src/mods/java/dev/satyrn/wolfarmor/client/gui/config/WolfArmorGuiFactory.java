package dev.satyrn.wolfarmor.client.gui.config;

import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WolfArmorGuiFactory implements IModGuiFactory {
   public WolfArmorGuiFactory() {
   }

   public void initialize(@Nonnull Minecraft minecraftInstance) {
   }

   public boolean hasConfigGui() {
      return true;
   }

   public GuiScreen createConfigGui(GuiScreen guiScreen) {
      return new WolfArmorGuiConfig(guiScreen);
   }

   @Nullable
   public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
      return null;
   }
}
