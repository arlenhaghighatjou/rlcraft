package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.filter.TextFilter;
import com.fantasticsource.mctools.gui.element.view.GUIPanZoomView;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class GUIMultilineTextInput extends GUITextInput {
   public GUIMultilineTextInput(GUIScreen screen, String text, TextFilter filter) {
      super(screen, text, filter);
   }

   public GUIMultilineTextInput(GUIScreen screen, String text, TextFilter filter, double scale) {
      super(screen, text, filter, scale);
   }

   public GUIMultilineTextInput(GUIScreen screen, String text, TextFilter filter, Color activeColor) {
      super(screen, text, filter, activeColor);
   }

   public GUIMultilineTextInput(GUIScreen screen, String text, TextFilter filter, Color activeColor, double scale) {
      super(screen, text, filter, activeColor, scale);
   }

   public GUIMultilineTextInput(GUIScreen screen, double x, double y, String text, TextFilter filter) {
      super(screen, x, y, text, filter);
   }

   public GUIMultilineTextInput(GUIScreen screen, double x, double y, String text, TextFilter filter, double scale) {
      super(screen, x, y, text, filter, scale);
   }

   public GUIMultilineTextInput(GUIScreen screen, double x, double y, String text, TextFilter filter, Color activeColor) {
      super(screen, x, y, text, filter, activeColor);
   }

   public GUIMultilineTextInput(GUIScreen screen, double x, double y, String text, TextFilter filter, Color activeColor, double scale) {
      super(screen, x, y, text, filter, activeColor, scale);
   }

   public void draw() {
      GlStateManager.disableTexture2D();
      double adjustedScale = this.scale * (double)(new ScaledResolution(Minecraft.getMinecraft())).getScaleFactor();
      if (this.parent instanceof GUIPanZoomView) {
         adjustedScale *= ((GUIPanZoomView)this.parent).getZoom();
      }

      if (!this.filter.acceptable(this.text)) {
         GlStateManager.color(T_RED.rf(), T_RED.gf(), T_RED.bf(), T_RED.af());
         GlStateManager.glBegin(7);
         GlStateManager.glVertex3f(0.0F, 0.0F, 0.0F);
         GlStateManager.glVertex3f(0.0F, 1.0F, 0.0F);
         GlStateManager.glVertex3f(1.0F, 1.0F, 0.0F);
         GlStateManager.glVertex3f(1.0F, 0.0F, 0.0F);
         GlStateManager.glEnd();
      } else if (this.active) {
         GlStateManager.color(Color.GRAY.rf(), Color.GRAY.gf(), Color.GRAY.bf(), 0.2F);
         GlStateManager.glBegin(7);
         GlStateManager.glVertex3f(0.0F, 0.0F, 0.0F);
         GlStateManager.glVertex3f(0.0F, 1.0F, 0.0F);
         GlStateManager.glVertex3f(1.0F, 1.0F, 0.0F);
         GlStateManager.glVertex3f(1.0F, 0.0F, 0.0F);
         GlStateManager.glEnd();
      }

      if (this.text.length() > 0) {
         Color c = this.active ? this.activeColor : (this.isMouseWithin() ? this.hoverColor : this.color);
         int mcColor = c.color() >>> 8 | c.a() << 24;
         if ((mcColor & -67108864) != 0) {
            GlStateManager.enableTexture2D();
            GlStateManager.pushMatrix();
            GlStateManager.scale(adjustedScale / (double)this.absolutePxWidth(), adjustedScale / (double)this.absolutePxHeight(), 1.0D);
            int yy = 0;

            for(String line : this.lines) {
               GUIScreen.FONT_RENDERER.drawString(line, 1.0F, (float)yy, mcColor, false);
               yy += GUIScreen.FONT_RENDERER.FONT_HEIGHT;
            }

            GlStateManager.popMatrix();
            GlStateManager.disableTexture2D();
         }
      }

      if (this.active) {
         float wUnit = (float)(adjustedScale / (double)this.absolutePxWidth());
         float lineHeight = 1.0F / (float)Tools.max(1, this.fullLines.size());
         float cursorX = 0.0F;
         int cursorLine = 0;
         int lineStart = 0;

         for(String fullLine : this.fullLines) {
            if (this.cursorPosition <= lineStart + fullLine.length()) {
               cursorX = (float)GUIScreen.FONT_RENDERER.getStringWidth(fullLine.substring(0, this.cursorPosition - lineStart).replace("\n", "")) + 0.5F;
               break;
            }

            ++cursorLine;
            lineStart += fullLine.length();
         }

         cursorX = Tools.max(cursorX, 0.5F);
         cursorX = cursorX * wUnit;
         if (this.selectorPosition != -1 && this.selectorPosition != this.cursorPosition) {
            float selectorX = 0.0F;
            int selectorLine = 0;
            int lineStart = 0;

            for(String fullLine : this.fullLines) {
               if (this.selectorPosition <= lineStart + fullLine.length()) {
                  selectorX = (float)GUIScreen.FONT_RENDERER.getStringWidth(fullLine.substring(0, this.selectorPosition - lineStart).replace("\n", "")) + 0.5F;
                  break;
               }

               ++selectorLine;
               lineStart += fullLine.length();
            }

            selectorX = selectorX * wUnit;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.3F);
            if (selectorLine == cursorLine) {
               float min = Tools.min(cursorX, selectorX);
               float max = Tools.max(cursorX, selectorX);
               GlStateManager.glBegin(7);
               GlStateManager.glVertex3f(min, (float)cursorLine * lineHeight, 0.0F);
               GlStateManager.glVertex3f(min, (float)(cursorLine + 1) * lineHeight, 0.0F);
               GlStateManager.glVertex3f(max, (float)(cursorLine + 1) * lineHeight, 0.0F);
               GlStateManager.glVertex3f(max, (float)cursorLine * lineHeight, 0.0F);
               GlStateManager.glEnd();
            } else if (this.selectorPosition < this.cursorPosition) {
               GlStateManager.glBegin(7);
               GlStateManager.glVertex3f(selectorX, (float)selectorLine * lineHeight, 0.0F);
               GlStateManager.glVertex3f(selectorX, (float)(selectorLine + 1) * lineHeight, 0.0F);
               GlStateManager.glVertex3f(1.0F, (float)(selectorLine + 1) * lineHeight, 0.0F);
               GlStateManager.glVertex3f(1.0F, (float)selectorLine * lineHeight, 0.0F);

               for(int middleLine = selectorLine + 1; middleLine < cursorLine; ++middleLine) {
                  GlStateManager.glVertex3f(0.0F, (float)middleLine * lineHeight, 0.0F);
                  GlStateManager.glVertex3f(0.0F, (float)(middleLine + 1) * lineHeight, 0.0F);
                  GlStateManager.glVertex3f(1.0F, (float)(middleLine + 1) * lineHeight, 0.0F);
                  GlStateManager.glVertex3f(1.0F, (float)middleLine * lineHeight, 0.0F);
               }

               GlStateManager.glVertex3f(0.0F, (float)cursorLine * lineHeight, 0.0F);
               GlStateManager.glVertex3f(0.0F, (float)(cursorLine + 1) * lineHeight, 0.0F);
               GlStateManager.glVertex3f(cursorX, (float)(cursorLine + 1) * lineHeight, 0.0F);
               GlStateManager.glVertex3f(cursorX, (float)cursorLine * lineHeight, 0.0F);
               GlStateManager.glEnd();
            } else {
               GlStateManager.glBegin(7);
               GlStateManager.glVertex3f(cursorX, (float)cursorLine * lineHeight, 0.0F);
               GlStateManager.glVertex3f(cursorX, (float)(cursorLine + 1) * lineHeight, 0.0F);
               GlStateManager.glVertex3f(1.0F, (float)(cursorLine + 1) * lineHeight, 0.0F);
               GlStateManager.glVertex3f(1.0F, (float)cursorLine * lineHeight, 0.0F);

               for(int middleLine = cursorLine + 1; middleLine < selectorLine; ++middleLine) {
                  GlStateManager.glVertex3f(0.0F, (float)middleLine * lineHeight, 0.0F);
                  GlStateManager.glVertex3f(0.0F, (float)(middleLine + 1) * lineHeight, 0.0F);
                  GlStateManager.glVertex3f(1.0F, (float)(middleLine + 1) * lineHeight, 0.0F);
                  GlStateManager.glVertex3f(1.0F, (float)middleLine * lineHeight, 0.0F);
               }

               GlStateManager.glVertex3f(0.0F, (float)selectorLine * lineHeight, 0.0F);
               GlStateManager.glVertex3f(0.0F, (float)(selectorLine + 1) * lineHeight, 0.0F);
               GlStateManager.glVertex3f(selectorX, (float)(selectorLine + 1) * lineHeight, 0.0F);
               GlStateManager.glVertex3f(selectorX, (float)selectorLine * lineHeight, 0.0F);
               GlStateManager.glEnd();
            }
         }

         if ((System.currentTimeMillis() - this.cursorTime) % 1000L < 500L) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.glBegin(1);
            GlStateManager.glVertex3f(cursorX, (float)cursorLine * lineHeight, 0.0F);
            GlStateManager.glVertex3f(cursorX, (float)(cursorLine + 1) * lineHeight, 0.0F);
            GlStateManager.glEnd();
         }
      }

      this.drawChildren();
   }

   public int fullLineCount() {
      return Tools.max(1, this.fullLines.size());
   }

   public int cursorLine() {
      int pos = this.cursorPosition;
      int line = 0;

      for(String fullLine : this.fullLines) {
         pos -= fullLine.length();
         if (pos <= 0) {
            return line;
         }

         ++line;
      }

      return line;
   }
}
