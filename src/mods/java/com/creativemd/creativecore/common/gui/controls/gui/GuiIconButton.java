package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.ColoredTextureDisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public abstract class GuiIconButton extends GuiButton {
   private int iconIndex;
   private ColoredTextureDisplayStyle icon;
   private ColoredTextureDisplayStyle iconBackground;

   public GuiIconButton(int x, int y, int iconIndex) {
      super("icon" + iconIndex, "", x, y, 12, 12);
      this.setIcon(iconIndex);
   }

   public GuiIconButton(int x, int y, int width, int iconIndex) {
      super("icon" + iconIndex, "", x, y, width, 12);
      this.setIcon(iconIndex);
   }

   public GuiIconButton(String name, int x, int y, int width, int iconIndex) {
      super(name, "", x, y, width, 12);
      this.setIcon(iconIndex);
   }

   public GuiIconButton(String name, int x, int y, int iconIndex) {
      super(name, "", x, y, 12, 12);
      this.setIcon(iconIndex);
   }

   public int getIconIndex() {
      return this.iconIndex;
   }

   public DisplayStyle getIcon() {
      return this.icon;
   }

   public void setIcon(int iconIndex) {
      this.iconIndex = iconIndex;
      this.iconBackground = new ColoredTextureDisplayStyle(ColorUtils.RGBAToInt(0, 0, 0, 100), guiUtilsImage, 240, iconIndex * 16);
      this.icon = new ColoredTextureDisplayStyle(-1, guiUtilsImage, 240, iconIndex * 16);
   }

   public boolean shouldDrawTitle() {
      return false;
   }

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
      GlStateManager.translate(-2.0F, -2.0F, 0.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translate(1.0F, 1.0F, 0.0F);
      this.iconBackground.renderStyle(helper, 16, 16);
      GlStateManager.popMatrix();
      this.icon.renderStyle(helper, 16, 16);
   }
}
