package com.fantasticsource.mctools.gui.element.textured;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GUIImage extends GUIElement {
   protected ResourceLocation texture;
   protected double unscaledWidth;
   protected double unscaledHeight;
   protected double u;
   protected double v;
   protected double uw;
   protected double vh;
   protected Color color;
   protected boolean ignoreMCGUIScale;

   public GUIImage(GUIScreen screen, double unscaledWidth, double unscaledHeight, ResourceLocation texture) {
      this(screen, unscaledWidth, unscaledHeight, texture, Color.WHITE);
   }

   public GUIImage(GUIScreen screen, double unscaledWidth, double unscaledHeight, ResourceLocation texture, Color color) {
      this(screen, unscaledWidth, unscaledHeight, texture, color, 0.0D, 0.0D, 1.0D, 1.0D);
   }

   public GUIImage(GUIScreen screen, double unscaledWidth, double unscaledHeight, ResourceLocation texture, double u, double v, double uw, double vh) {
      this(screen, unscaledWidth, unscaledHeight, texture, Color.WHITE, u, v, uw, vh);
   }

   public GUIImage(GUIScreen screen, double unscaledWidth, double unscaledHeight, ResourceLocation texture, Color color, double u, double v, double uw, double vh) {
      super(screen, 1.0D, 1.0D);
      this.ignoreMCGUIScale = false;
      this.unscaledWidth = unscaledWidth;
      this.unscaledHeight = unscaledHeight;
      this.texture = texture;
      this.color = color;
      this.u = u;
      this.v = v;
      this.uw = uw;
      this.vh = vh;
   }

   public GUIImage(GUIScreen screen, double x, double y, double unscaledWidth, double unscaledHeight, ResourceLocation texture) {
      this(screen, x, y, unscaledWidth, unscaledHeight, texture, Color.WHITE);
   }

   public GUIImage(GUIScreen screen, double x, double y, double unscaledWidth, double unscaledHeight, ResourceLocation texture, Color color) {
      this(screen, x, y, unscaledWidth, unscaledHeight, texture, color, 0.0D, 0.0D, 1.0D, 1.0D);
   }

   public GUIImage(GUIScreen screen, double x, double y, double unscaledWidth, double unscaledHeight, ResourceLocation texture, double u, double v, double uw, double vh) {
      this(screen, x, y, unscaledWidth, unscaledHeight, texture, Color.WHITE, u, v, uw, vh);
   }

   public GUIImage(GUIScreen screen, double x, double y, double unscaledWidth, double unscaledHeight, ResourceLocation texture, Color color, double u, double v, double uw, double vh) {
      super(screen, x, y, 1.0D, 1.0D);
      this.ignoreMCGUIScale = false;
      this.unscaledWidth = unscaledWidth;
      this.unscaledHeight = unscaledHeight;
      this.texture = texture;
      this.color = color;
      this.u = u;
      this.v = v;
      this.uw = uw;
      this.vh = vh;
   }

   public GUIImage ignoreMCGUIScale(boolean ignoreMCGUIScale) {
      this.ignoreMCGUIScale = ignoreMCGUIScale;
      this.recalc(0);
      return this;
   }

   public GUIImage recalc(int subIndexChanged) {
      this.width = this.unscaledWidth / (double)this.screen.width;
      this.height = this.unscaledHeight / (double)this.screen.height;
      if (this.ignoreMCGUIScale) {
         int mcScale = (new ScaledResolution(Minecraft.getMinecraft())).getScaleFactor();
         this.width /= (double)mcScale;
         this.height /= (double)mcScale;
      }

      if (this.parent != null) {
         this.width /= this.parent.absoluteWidth();
         this.height /= this.parent.absoluteHeight();
      }

      this.recalcAndRepositionSubElements(0);
      this.postRecalc();
      return this;
   }

   public void setColor(Color color) {
      this.color = color;
   }

   public Color getColor() {
      return this.color.copy();
   }

   public void draw() {
      GlStateManager.enableTexture2D();
      GlStateManager.color(this.color.rf(), this.color.gf(), this.color.bf(), this.color.af());
      Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
      double u2 = this.u + this.uw;
      double v2 = this.v + this.vh;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder builder = tessellator.getBuffer();
      builder.begin(7, DefaultVertexFormats.POSITION_TEX);
      builder.pos(1.0D, 0.0D, 0.0D).tex(u2, this.v).endVertex();
      builder.pos(0.0D, 0.0D, 0.0D).tex(this.u, this.v).endVertex();
      builder.pos(0.0D, 1.0D, 0.0D).tex(this.u, v2).endVertex();
      builder.pos(1.0D, 1.0D, 0.0D).tex(u2, v2).endVertex();
      tessellator.draw();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.drawChildren();
   }
}
