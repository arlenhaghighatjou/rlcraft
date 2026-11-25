package org.orecruncher.lib.gui;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.Localization;

@SideOnly(Side.CLIENT)
public class GuiTooltip {
   protected final GuiTooltip.ITooltipRenderer renderer;
   protected final HoverChecker checker;
   protected final List<String> tooltip;

   public GuiTooltip(@Nonnull GuiTooltip.ITooltipRenderer renderer, @Nonnull GuiButton button, @Nonnull String tipText) {
      this(renderer, button, tipText, 200);
   }

   public GuiTooltip(@Nonnull GuiTooltip.ITooltipRenderer renderer, @Nonnull GuiButton button, @Nonnull String tipText, int width) {
      FontRenderer font = Minecraft.getMinecraft().fontRenderer;
      this.renderer = renderer;
      this.checker = new HoverChecker(button, 800);
      this.tooltip = this.generateTooltip(font, tipText, width);
   }

   private List<String> generateTooltip(@Nonnull FontRenderer font, @Nonnull String langKey, int width) {
      String t = Localization.format(langKey);
      return font.listFormattedStringToWidth(t, width);
   }

   public boolean handle(int mouseX, int mouseY) {
      if (this.checker.checkHover(mouseX, mouseY)) {
         this.renderer.drawTooltip(mouseX, mouseY, this.tooltip);
         return true;
      } else {
         return false;
      }
   }

   public interface ITooltipRenderer {
      void drawTooltip(int var1, int var2, @Nonnull List<String> var3);
   }
}
