package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.ColoredDisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.client.style.TextureStretchDisplayStyle;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.util.Color;

public class GuiColorPlate extends GuiControl {
   private Color color;
   private DisplayStyle colorPlate;
   private DisplayStyle backgroundPlate;

   public GuiColorPlate(String name, int x, int y, int width, int height, Color color) {
      super(name, x, y, width, height);
      this.backgroundPlate = new TextureStretchDisplayStyle(guiUtilsImage, 224, 240, 16, 16);
      this.marginWidth = 0;
      this.setColor(color);
   }

   public GuiColorPlate(String name, int x, int y, int width, int height, Vec3i color) {
      super(name, x, y, width, height);
      this.marginWidth = 0;
      this.backgroundPlate = new TextureStretchDisplayStyle(guiUtilsImage, 224, 240, 16, 16);
      this.setColor(new Color(color.getX(), color.getY(), color.getZ()));
   }

   public void setColor(Vec3i color) {
      this.setColor(new Color((byte)color.getX(), (byte)color.getY(), (byte)color.getZ()));
   }

   public void setColor(Color color) {
      this.color = color;
      this.colorPlate = new ColoredDisplayStyle(color);
   }

   public Color getColor() {
      return this.color;
   }

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
      this.backgroundPlate.renderStyle(helper, width, height);
      this.colorPlate.renderStyle(helper, width, height);
   }

   public boolean hasBackground() {
      return false;
   }
}
