package dev.satyrn.wolfarmor.client.gui.config;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WolfArmorGuiConfig extends GuiConfig {
   public WolfArmorGuiConfig(@Nonnull GuiScreen parent) {
      super(parent, getConfigurationElements(), "wolfarmor", false, false, I18n.format("gui.wolfarmor.config", new Object[0]), I18n.format("gui.wolfarmor.config.subtitle", new Object[0]));
   }

   @Nonnull
   private static List<IConfigElement> getConfigurationElements() {
      return new ArrayList();
   }
}
