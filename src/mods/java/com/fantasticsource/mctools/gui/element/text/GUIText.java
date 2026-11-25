package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.MonoASCIIFontRenderer;
import com.fantasticsource.mctools.Render;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.view.GUIPanZoomView;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class GUIText extends GUIElement {
   public final double scale;
   public String oldText;
   protected String text;
   protected ArrayList<String> lines;
   protected ArrayList<String> fullLines;
   protected Color color;
   protected Color hoverColor;
   protected Color activeColor;

   public GUIText(GUIScreen screen, String text) {
      this(screen, text, Color.WHITE, 1.0D);
   }

   public GUIText(GUIScreen screen, String text, double scale) {
      this(screen, text, Color.WHITE, scale);
   }

   public GUIText(GUIScreen screen, String text, Color color) {
      this(screen, text, color, color, color, 1.0D);
   }

   public GUIText(GUIScreen screen, String text, Color color, double scale) {
      this(screen, text, color, color, color, scale);
   }

   public GUIText(GUIScreen screen, String text, Color color, Color hoverColor, Color activeColor) {
      this(screen, text, color, hoverColor, activeColor, 1.0D);
   }

   public GUIText(GUIScreen screen, String text, Color color, Color hoverColor, Color activeColor, double scale) {
      super(screen, 0.0D, 0.0D);
      this.lines = new ArrayList();
      this.fullLines = new ArrayList();
      this.scale = scale * screen.textScale;
      this.text = text;
      this.oldText = text;
      this.color = color;
      this.hoverColor = hoverColor;
      this.activeColor = activeColor;
      this.recalc(0);
   }

   public GUIText(GUIScreen screen, double x, double y, String text) {
      this(screen, x, y, text, Color.WHITE, 1.0D);
   }

   public GUIText(GUIScreen screen, double x, double y, String text, double scale) {
      this(screen, x, y, text, Color.WHITE, scale);
   }

   public GUIText(GUIScreen screen, double x, double y, String text, Color color) {
      this(screen, x, y, text, color, color, color, 1.0D);
   }

   public GUIText(GUIScreen screen, double x, double y, String text, Color color, double scale) {
      this(screen, x, y, text, color, color, color, scale);
   }

   public GUIText(GUIScreen screen, double x, double y, String text, Color color, Color hoverColor, Color activeColor) {
      this(screen, x, y, text, color, hoverColor, activeColor, 1.0D);
   }

   public GUIText(GUIScreen screen, double x, double y, String text, Color color, Color hoverColor, Color activeColor, double scale) {
      super(screen, x, y, 0.0D, 0.0D);
      this.lines = new ArrayList();
      this.fullLines = new ArrayList();
      this.scale = scale * screen.textScale;
      this.text = text;
      this.oldText = text;
      this.color = color;
      this.hoverColor = hoverColor;
      this.activeColor = activeColor;
      this.recalc(0);
   }

   public GUIText setColor(Color color) {
      return this.setColor(color, color, color);
   }

   public GUIText setColor(Color color, Color hoverColor, Color activeColor) {
      this.color = color;
      this.hoverColor = hoverColor;
      this.activeColor = activeColor;
      return this;
   }

   public String getText() {
      return this.text;
   }

   public GUIText setText(String text) {
      if (text.equals(this.text)) {
         return this;
      } else {
         this.oldText = this.text;
         this.text = text;
         this.screen.root.recalc(0);
         return this;
      }
   }

   public GUIText recalc(int subIndexChanged) {
      this.text = this.text.replaceAll("\r", "");
      this.lines.clear();
      this.fullLines.clear();
      if (this.parent instanceof CodeInput) {
         this.lines.add(this.text);
         this.fullLines.add(this.text);
         this.width = (double)MonoASCIIFontRenderer.getStringWidth(this.text) * this.scale / (double)this.screen.width;
         this.height = (double)(MonoASCIIFontRenderer.LINE_HEIGHT + 2) * this.scale / (double)this.screen.height;
      } else {
         StringBuilder previous = new StringBuilder();
         String[] words = Tools.preservedSplit(this.text, "[\n]|[ ]+", true);
         double parentW = this.parent == null ? 1.0D : this.parent.absoluteWidth();
         StringBuilder line = new StringBuilder();
         StringBuilder fullLine = new StringBuilder();
         int index = 0;
         double maxLineW = 0.0D;
         double lineW = 0.0D;

         while(index < words.length) {
            String word = words[index++];
            if (!word.equals("")) {
               if (word.equals("\n")) {
                  this.lines.add(line.toString());
                  this.fullLines.add(fullLine.toString());
                  line = new StringBuilder();
                  fullLine = new StringBuilder("\n");
                  maxLineW = 1.0D;
                  lineW = 0.0D;
               } else {
                  double wordW = (double)Render.getPartialStringWidth(previous.toString().replaceAll("\n", ""), word.replaceAll("\n", "")) * this.scale / (double)this.screen.width;
                  if (lineW + wordW > parentW) {
                     if (word.trim().equals("")) {
                        fullLine.append(word);
                        continue;
                     }

                     if (line.length() == 0) {
                        line.append(word);
                        fullLine.append(word);
                        lineW += wordW;
                     } else {
                        this.lines.add(line.toString());
                        this.fullLines.add(fullLine.toString());
                        line = new StringBuilder(word);
                        fullLine = new StringBuilder(word);
                        maxLineW = parentW;
                        lineW = (double)Render.getPartialStringWidth(previous.toString().replaceAll("\n", ""), word.replaceAll("\n", "")) * this.scale / (double)this.screen.width;
                     }
                  } else {
                     line.append(word);
                     fullLine.append(word);
                     lineW += wordW;
                  }
               }

               previous.append(word);
            }
         }

         if (line.length() > 0) {
            this.lines.add(line.toString());
            maxLineW = Tools.max(maxLineW, lineW);
         }

         if (fullLine.length() > 0) {
            this.fullLines.add(fullLine.toString());
         }

         this.width = maxLineW;
         if (this instanceof GUIMultilineTextInput && this.text.length() > 0 && this.text.charAt(this.text.length() - 1) == '\n') {
            this.height = (double)(Tools.max(1, this.fullLines.size()) * GUIScreen.FONT_RENDERER.FONT_HEIGHT - 1) * this.scale / (double)this.screen.height;
         } else {
            this.height = (double)(Tools.max(1, this.lines.size()) * GUIScreen.FONT_RENDERER.FONT_HEIGHT - 1) * this.scale / (double)this.screen.height;
         }
      }

      if (this.parent != null) {
         this.width /= this.parent.absoluteWidth();
         this.height /= this.parent.absoluteHeight();
      }

      this.width += 1.0D * this.width / (double)this.screen.width;
      this.recalcAndRepositionSubElements(0);
      this.postRecalc();
      return this;
   }

   public void draw() {
      Color c = this.active ? this.activeColor : (this.isMouseWithin() ? this.hoverColor : this.color);
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
      }
   }

   public String toString() {
      return this.text;
   }
}
