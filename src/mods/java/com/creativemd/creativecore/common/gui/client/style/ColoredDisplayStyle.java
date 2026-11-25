package com.creativemd.creativecore.common.gui.client.style;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.util.Color;

public class ColoredDisplayStyle extends DisplayStyle {
   public Color color;

   public ColoredDisplayStyle(Color color) {
      this.color = color;
   }

   public ColoredDisplayStyle(int r, int g, int b) {
      this(new Color(r, g, b));
   }

   public ColoredDisplayStyle(int r, int g, int b, int a) {
      this(new Color(r, g, b, a));
   }

   public ColoredDisplayStyle(byte r, byte g, byte b) {
      this(new Color(r, g, b));
   }

   public ColoredDisplayStyle(byte r, byte g, byte b, byte a) {
      this(new Color(r, g, b, a));
   }

   public ColoredDisplayStyle(int color) {
      this(ColorUtils.IntToRGB(color));
   }

   public ColoredDisplayStyle(Vec3d color) {
      this(new Color((byte)((int)color.x), (byte)((int)color.y), (byte)((int)color.z)));
   }

   public ColoredDisplayStyle(Vec3i color) {
      this(new Color(color.getX(), color.getY(), color.getZ()));
   }

   public void renderStyle(GuiRenderHelper helper, int width, int height) {
      helper.renderColorPlate(this.color, width, height);
   }
}
