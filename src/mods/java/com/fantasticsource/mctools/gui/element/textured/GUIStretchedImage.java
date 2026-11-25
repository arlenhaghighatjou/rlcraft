package com.fantasticsource.mctools.gui.element.textured;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GUIStretchedImage extends GUIElement {
   protected ResourceLocation texture;
   protected double u;
   protected double v;
   protected double uw;
   protected double vh;
   protected Color color;
   protected boolean ignoreMCGUIScale;

   public GUIStretchedImage(GUIScreen screen, double width, double height, ResourceLocation texture) {
      this(screen, width, height, texture, Color.WHITE);
   }

   public GUIStretchedImage(GUIScreen screen, double width, double height, ResourceLocation texture, Color color) {
      this(screen, width, height, texture, color, 0.0D, 0.0D, 1.0D, 1.0D);
   }

   public GUIStretchedImage(GUIScreen screen, double width, double height, ResourceLocation texture, double u, double v, double uw, double vh) {
      this(screen, width, height, texture, Color.WHITE, u, v, uw, vh);
   }

   public GUIStretchedImage(GUIScreen screen, double width, double height, ResourceLocation texture, Color color, double u, double v, double uw, double vh) {
      super(screen, width, height);
      this.ignoreMCGUIScale = false;
      this.texture = texture;
      this.color = color;
      this.u = u;
      this.v = v;
      this.uw = uw;
      this.vh = vh;
   }

   public GUIStretchedImage(GUIScreen screen, ResourceLocation texture) {
      this(screen, texture, Color.WHITE);
   }

   public GUIStretchedImage(GUIScreen screen, ResourceLocation texture, Color color) {
      this(screen, 0.0D, 0.0D, 1.0D, 1.0D, texture, color);
   }

   public GUIStretchedImage(GUIScreen screen, double x, double y, double width, double height, ResourceLocation texture) {
      this(screen, x, y, width, height, texture, Color.WHITE);
   }

   public GUIStretchedImage(GUIScreen screen, double x, double y, double width, double height, ResourceLocation texture, Color color) {
      this(screen, x, y, width, height, texture, color, 0.0D, 0.0D, 1.0D, 1.0D);
   }

   public GUIStretchedImage(GUIScreen screen, double x, double y, double width, double height, ResourceLocation texture, double u, double v, double uw, double vh) {
      this(screen, x, y, width, height, texture, Color.WHITE, u, v, uw, vh);
   }

   public GUIStretchedImage(GUIScreen screen, double x, double y, double width, double height, ResourceLocation texture, Color color, double u, double v, double uw, double vh) {
      super(screen, x, y, width, height);
      this.ignoreMCGUIScale = false;
      this.texture = texture;
      this.color = color;
      this.u = u;
      this.v = v;
      this.uw = uw;
      this.vh = vh;
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
      WorldRenderer builder = tessellator.getBuffer();
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
