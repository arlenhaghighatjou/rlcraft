package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.view.GUIPanZoomView;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class GUIFadingText extends GUIText {
   protected int startFadeTicks;
   protected int endFadeTicks;
   protected int age;

   public GUIFadingText(GUIScreen screen, String text, int startFadeTicks, int endFadeTicks) {
      this(screen, text, startFadeTicks, endFadeTicks, 1.0D);
   }

   public GUIFadingText(GUIScreen screen, String text, int startFadeTicks, int endFadeTicks, double scale) {
      this(screen, text, startFadeTicks, endFadeTicks, Color.WHITE, scale);
   }

   public GUIFadingText(GUIScreen screen, String text, int startFadeTicks, int endFadeTicks, Color color) {
      this(screen, text, startFadeTicks, endFadeTicks, color, 1.0D);
   }

   public GUIFadingText(GUIScreen screen, String text, int startFadeTicks, int endFadeTicks, Color color, double scale) {
      this(screen, text, startFadeTicks, endFadeTicks, color, color, color, scale);
   }

   public GUIFadingText(GUIScreen screen, String text, int startFadeTicks, int endFadeTicks, Color color, Color hoverColor, Color activeColor) {
      this(screen, text, startFadeTicks, endFadeTicks, color, hoverColor, activeColor, 1.0D);
   }

   public GUIFadingText(GUIScreen screen, String text, int startFadeTicks, int endFadeTicks, Color color, Color hoverColor, Color activeColor, double scale) {
      super(screen, text, color, hoverColor, activeColor, scale);
      this.age = 0;
      this.startFadeTicks = startFadeTicks;
      this.endFadeTicks = endFadeTicks;
   }

   public GUIFadingText(GUIScreen screen, double x, double y, String text, int startFadeTicks, int endFadeTicks) {
      this(screen, x, y, text, startFadeTicks, endFadeTicks, 1.0D);
   }

   public GUIFadingText(GUIScreen screen, double x, double y, String text, int startFadeTicks, int endFadeTicks, double scale) {
      this(screen, x, y, text, startFadeTicks, endFadeTicks, Color.WHITE, scale);
   }

   public GUIFadingText(GUIScreen screen, double x, double y, String text, int startFadeTicks, int endFadeTicks, Color color) {
      this(screen, x, y, text, startFadeTicks, endFadeTicks, color, 1.0D);
   }

   public GUIFadingText(GUIScreen screen, double x, double y, String text, int startFadeTicks, int endFadeTicks, Color color, double scale) {
      this(screen, x, y, text, startFadeTicks, endFadeTicks, color, color, color, scale);
   }

   public GUIFadingText(GUIScreen screen, double x, double y, String text, int startFadeTicks, int endFadeTicks, Color color, Color hoverColor, Color activeColor) {
      this(screen, x, y, text, startFadeTicks, endFadeTicks, color, hoverColor, activeColor, 1.0D);
   }

   public GUIFadingText(GUIScreen screen, double x, double y, String text, int startFadeTicks, int endFadeTicks, Color color, Color hoverColor, Color activeColor, double scale) {
      super(screen, x, y, text, color, hoverColor, activeColor, scale);
      this.age = 0;
      this.startFadeTicks = startFadeTicks;
      this.endFadeTicks = endFadeTicks;
   }

   public void draw() {
      Color c = (this.active ? this.activeColor : (this.isMouseWithin() ? this.hoverColor : this.color)).copy();
      c.setAF(c.af() * Tools.min(Tools.max(1.0F - (float)(this.age - this.startFadeTicks) / (float)(this.endFadeTicks - this.startFadeTicks), 0.0F), 1.0F));
      int mcColor = c.color() >>> 8 | c.a() << 24;
      if ((mcColor & -67108864) != 0) {
         GlStateManager.enableTexture2D();
         GlStateManager.pushMatrix();
         double adjustedScale = this.scale * (double)(new ScaledResolution(Minecraft.getMinecraft())).getScaleFactor();
         if (this.parent instanceof GUIPanZoomView) {
            adjustedScale *= ((GUIPanZoomView)this.parent).getZoom();
         }

         GlStateManager.scale(adjustedScale / (double)this.absolutePxWidth(), adjustedScale / (double)this.absolutePxHeight(), 1.0D);
         int yy = 0;

         for(String line : this.lines) {
            GUIScreen.FONT_RENDERER.drawString(line, 0.0F, (float)yy, mcColor, false);
            yy += GUIScreen.FONT_RENDERER.FONT_HEIGHT;
         }

         GlStateManager.popMatrix();
         this.drawChildren();
         if (++this.age >= this.endFadeTicks) {
            this.parent.remove(this);
         }

      }
   }
}
