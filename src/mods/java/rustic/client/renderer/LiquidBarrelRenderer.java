package rustic.client.renderer;

import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.Fluid;
import rustic.client.util.FluidClientUtil;
import rustic.common.tileentity.TileEntityLiquidBarrel;

public class LiquidBarrelRenderer extends TileEntitySpecialRenderer<TileEntityLiquidBarrel> {
   int blue;
   int green;
   int red;
   int a;
   int lightx;
   int lighty;
   double minU;
   double minV;
   double maxU;
   double maxV;
   double diffU;
   double diffV;

   public LiquidBarrelRenderer() {
   }

   public void render(TileEntityLiquidBarrel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
      int amount = te.getAmount();
      int capacity = te.getCapacity();
      Fluid fluid = te.getFluid();
      if (fluid != null) {
         int c = fluid.getColor();
         this.blue = c & 255;
         this.green = c >> 8 & 255;
         this.red = c >> 16 & 255;
         this.a = c >> 24 & 255;
         EaglerTextureAtlasSprite sprite = (EaglerTextureAtlasSprite)FluidClientUtil.stillTextures.get(fluid);
         if (sprite == null) {
            return;
         }

         this.diffU = this.maxU - this.minU;
         this.diffV = this.maxV - this.minV;
         this.minU = (double)sprite.getMinU() + this.diffU * 0.1875D;
         this.maxU = (double)sprite.getMaxU() - this.diffU * 0.1875D;
         this.minV = (double)sprite.getMinV() + this.diffV * 0.1875D;
         this.maxV = (double)sprite.getMaxV() - this.diffV * 0.1875D;
         int i = this.getWorld().getCombinedLight(te.getPos(), fluid.getLuminosity());
         this.lightx = i >> 16 & '\uffff';
         this.lighty = i & '\uffff';
         GlStateManager.pushAttrib();
         GlStateManager.disableCull();
         GlStateManager.disableLighting();
         GlStateManager.enableBlend();
         GlStateManager.enableAlpha();
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         Tessellator tess = Tessellator.getInstance();
         WorldRenderer buffer = tess.getBuffer();
         buffer.begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
         buffer.pos(x + 0.1875D, y + 0.125D + 0.8125D * (double)((float)amount / (float)capacity), z + 0.1875D).tex(this.minU, this.minV).lightmap(this.lightx, this.lighty).color(this.red, this.green, this.blue, this.a).endVertex();
         buffer.pos(x + 0.8125D, y + 0.125D + 0.8125D * (double)((float)amount / (float)capacity), z + 0.1875D).tex(this.maxU, this.minV).lightmap(this.lightx, this.lighty).color(this.red, this.green, this.blue, this.a).endVertex();
         buffer.pos(x + 0.8125D, y + 0.125D + 0.8125D * (double)((float)amount / (float)capacity), z + 0.8125D).tex(this.maxU, this.maxV).lightmap(this.lightx, this.lighty).color(this.red, this.green, this.blue, this.a).endVertex();
         buffer.pos(x + 0.1875D, y + 0.125D + 0.8125D * (double)((float)amount / (float)capacity), z + 0.8125D).tex(this.minU, this.maxV).lightmap(this.lightx, this.lighty).color(this.red, this.green, this.blue, this.a).endVertex();
         tess.draw();
         GlStateManager.disableBlend();
         GlStateManager.enableLighting();
         GlStateManager.enableCull();
         GlStateManager.popAttrib();
      }

   }
}
