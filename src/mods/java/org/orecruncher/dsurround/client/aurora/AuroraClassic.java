package org.orecruncher.dsurround.client.aurora;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.Color;
import org.orecruncher.lib.gfx.OpenGlState;

@SideOnly(Side.CLIENT)
public final class AuroraClassic extends AuroraBase {
   public AuroraClassic(long seed) {
      super(seed);
   }

   public void update() {
      super.update();
      this.band.update();
   }

   public void render(float partialTick) {
      float alpha = this.getAlpha();
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder renderer = tess.getBuffer();
      double tranY = this.getTranslationY(partialTick);
      double tranX = this.getTranslationX(partialTick);
      double tranZ = this.getTranslationZ(partialTick);
      Color base = this.getBaseColor();
      Color fade = this.getFadeColor();
      double zero = 0.0D;
      OpenGlState glState = OpenGlState.push();
      GlStateManager.disableLighting();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.disableTexture2D();
      GlStateManager.shadeModel(7425);
      GlStateManager.disableAlpha();
      GlStateManager.disableCull();
      GlStateManager.depthMask(false);
      this.band.translate(partialTick);
      Panel[] array = this.band.getNodeList();

      for(int b = 0; b < this.bandCount; ++b) {
         GlStateManager.pushMatrix();
         GlStateManager.translate(tranX, tranY, tranZ + (double)(this.offset * (float)b));
         GlStateManager.scale(0.5D, 8.0D, 0.5D);
         renderer.begin(4, DefaultVertexFormats.POSITION_COLOR);

         for(int i = 0; i < array.length - 1; ++i) {
            Panel node = array[i];
            double posY = (double)node.getModdedY();
            double posX = (double)node.tetX;
            double posZ = (double)node.tetZ;
            double tetX = (double)node.tetX2;
            double tetZ = (double)node.tetZ2;
            double posX2;
            double posZ2;
            double tetX2;
            double tetZ2;
            double posY2;
            if (i < array.length - 2) {
               Panel nodePlus = array[i + 1];
               posX2 = (double)nodePlus.tetX;
               posZ2 = (double)nodePlus.tetZ;
               tetX2 = (double)nodePlus.tetX2;
               tetZ2 = (double)nodePlus.tetZ2;
               posY2 = (double)nodePlus.getModdedY();
            } else {
               posX2 = tetX2 = (double)node.posX;
               posZ2 = tetZ2 = (double)node.getModdedZ();
               posY2 = 0.0D;
            }

            renderer.pos(posX, 0.0D, posZ).color(base.red, base.green, base.blue, alpha).endVertex();
            renderer.pos(posX, posY, posZ).color(fade.red, fade.green, fade.blue, 0.0F).endVertex();
            renderer.pos(posX2, posY2, posZ2).color(fade.red, fade.green, fade.blue, 0.0F).endVertex();
            renderer.pos(posX2, posY2, posZ2).color(fade.red, fade.green, fade.blue, 0.0F).endVertex();
            renderer.pos(posX2, 0.0D, posZ2).color(base.red, base.green, base.blue, alpha).endVertex();
            renderer.pos(posX, 0.0D, posZ).color(base.red, base.green, base.blue, alpha).endVertex();
            renderer.pos(posX, 0.0D, posZ).color(base.red, base.green, base.blue, alpha).endVertex();
            renderer.pos(posX2, 0.0D, posZ2).color(base.red, base.green, base.blue, alpha).endVertex();
            renderer.pos(tetX2, 0.0D, tetZ2).color(base.red, base.green, base.blue, alpha).endVertex();
            renderer.pos(tetX2, 0.0D, tetZ2).color(base.red, base.green, base.blue, alpha).endVertex();
            renderer.pos(tetX, 0.0D, tetZ).color(base.red, base.green, base.blue, alpha).endVertex();
            renderer.pos(posX, 0.0D, posZ).color(base.red, base.green, base.blue, alpha).endVertex();
            renderer.pos(tetX, 0.0D, tetZ).color(base.red, base.green, base.blue, alpha).endVertex();
            renderer.pos(tetX, posY, tetZ).color(fade.red, fade.green, fade.blue, 0.0F).endVertex();
            renderer.pos(tetX2, posY2, tetZ2).color(fade.red, fade.green, fade.blue, 0.0F).endVertex();
            renderer.pos(tetX2, posY2, tetZ2).color(fade.red, fade.green, fade.blue, 0.0F).endVertex();
            renderer.pos(tetX2, 0.0D, tetZ2).color(base.red, base.green, base.blue, alpha).endVertex();
            renderer.pos(tetX, 0.0D, tetZ).color(base.red, base.green, base.blue, alpha).endVertex();
         }

         tess.draw();
         GlStateManager.popMatrix();
      }

      OpenGlState.pop(glState);
   }

   public String toString() {
      return "<CLASSIC> " + super.toString();
   }
}
