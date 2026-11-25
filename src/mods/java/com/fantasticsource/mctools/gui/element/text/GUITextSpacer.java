package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.MonoASCIIFontRenderer;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class GUITextSpacer extends GUIElement {
   protected double scale;
   protected boolean vertical;

   public GUITextSpacer(GUIScreen screen) {
      this(screen, 1.0D);
   }

   public GUITextSpacer(GUIScreen screen, boolean vertical) {
      this(screen, 1.0D, vertical);
   }

   public GUITextSpacer(GUIScreen screen, double length) {
      this(screen, length, 1.0D);
   }

   public GUITextSpacer(GUIScreen screen, double length, double scale) {
      this(screen, length, scale, false);
   }

   public GUITextSpacer(GUIScreen screen, double length, boolean vertical) {
      this(screen, length, 1.0D, vertical);
   }

   public GUITextSpacer(GUIScreen screen, double length, double scale, boolean vertical) {
      super(screen, vertical ? 0.0D : length, vertical ? length : 0.0D);
      this.vertical = vertical;
      this.scale = scale * screen.textScale * 2.0D / (double)(new ScaledResolution(Minecraft.getMinecraft())).getScaleFactor();
      this.recalc(0);
   }

   public GUITextSpacer recalc(int subIndexChanged) {
      if (this.parent instanceof CodeInput) {
         if (this.vertical) {
            this.width = (double)(MonoASCIIFontRenderer.LINE_HEIGHT + 2) * this.scale / (double)this.screen.width;
         } else {
            this.height = (double)(MonoASCIIFontRenderer.LINE_HEIGHT + 2) * this.scale / (double)this.screen.height;
         }
      } else if (this.vertical) {
         this.width = (double)(GUIScreen.FONT_RENDERER.FONT_HEIGHT - 1) * this.scale / (double)this.screen.width;
      } else {
         this.height = (double)(GUIScreen.FONT_RENDERER.FONT_HEIGHT - 1) * this.scale / (double)this.screen.height;
      }

      if (this.parent != null) {
         this.width /= this.parent.absoluteWidth();
         this.height /= this.parent.absoluteHeight();
      }

      this.recalcAndRepositionSubElements(0);
      this.postRecalc();
      return this;
   }

   public String toString() {
      return this.getClass().getSimpleName();
   }
}
