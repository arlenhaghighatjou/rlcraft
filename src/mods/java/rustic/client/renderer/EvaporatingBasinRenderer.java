package rustic.client.renderer;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.opengl.GL11;
import rustic.client.util.FluidClientUtil;
import rustic.common.tileentity.TileEntityEvaporatingBasin;

public class EvaporatingBasinRenderer extends TileEntitySpecialRenderer<TileEntityEvaporatingBasin> {
   int blue;
   int green;
   int red;
   int alpha;
   int lightx;
   int lighty;
   double minU;
   double minV;
   double maxU;
   double maxV;
   double diffU;
   double diffV;

   public EvaporatingBasinRenderer() {
   }

   public void render(TileEntityEvaporatingBasin te, double x, double y, double z, float partialTicks, int destroyStage, float a) {
      IItemHandler itemStackHandler = (IItemHandler)te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
      if (itemStackHandler.getSlots() > 0 && !itemStackHandler.getStackInSlot(0).isEmpty() && te.getWorld() != null) {
         ItemStack stack = itemStackHandler.getStackInSlot(0);
         int itemCount = (int)Math.ceil((double)stack.getCount() / 16.0D);
         Random rand = new Random();
         rand.setSeed(te.getWorld().getSeed());

         for(int i = 0; i < itemCount; ++i) {
            GL11.glPushMatrix();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableLighting();
            GL11.glTranslated(x, y + 0.062D + (double)i * 0.0625D, z);
            GL11.glTranslated(0.5D, 0.0D, 0.5D);
            GL11.glRotated((double)rand.nextFloat() * 360.0D, 0.0D, 1.0D, 0.0D);
            GL11.glTranslated(-0.5D, 0.0D, -0.5D);
            GL11.glRotated(90.0D, 1.0D, 0.0D, 0.0D);
            GL11.glTranslated(0.5D, -0.1875D, 0.0D);
            GL11.glTranslated(0.0D, 0.6875D, 0.0D);
            GL11.glScaled(0.5D, 0.5D, 0.5D);
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.NONE);
            GL11.glPopMatrix();
         }
      }

      int amount = te.getAmount();
      int capacity = te.getCapacity();
      Fluid fluid = te.getFluid();
      if (fluid != null) {
         int c = fluid.getColor();
         this.blue = c & 255;
         this.green = c >> 8 & 255;
         this.red = c >> 16 & 255;
         this.alpha = c >> 24 & 255;
         TextureAtlasSprite sprite = (TextureAtlasSprite)FluidClientUtil.stillTextures.get(fluid);
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
         BufferBuilder buffer = tess.getBuffer();
         buffer.begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
         buffer.pos(x + 0.1875D, y + 0.0625D + 0.1875D * (double)((float)amount / (float)capacity), z + 0.1875D).tex(this.minU, this.minV).lightmap(this.lightx, this.lighty).color(this.red, this.green, this.blue, this.alpha).endVertex();
         buffer.pos(x + 0.8125D, y + 0.0625D + 0.1875D * (double)((float)amount / (float)capacity), z + 0.1875D).tex(this.maxU, this.minV).lightmap(this.lightx, this.lighty).color(this.red, this.green, this.blue, this.alpha).endVertex();
         buffer.pos(x + 0.8125D, y + 0.0625D + 0.1875D * (double)((float)amount / (float)capacity), z + 0.8125D).tex(this.maxU, this.maxV).lightmap(this.lightx, this.lighty).color(this.red, this.green, this.blue, this.alpha).endVertex();
         buffer.pos(x + 0.1875D, y + 0.0625D + 0.1875D * (double)((float)amount / (float)capacity), z + 0.8125D).tex(this.minU, this.maxV).lightmap(this.lightx, this.lighty).color(this.red, this.green, this.blue, this.alpha).endVertex();
         tess.draw();
         GlStateManager.disableAlpha();
         GlStateManager.disableBlend();
         GlStateManager.enableLighting();
         GlStateManager.enableCull();
         GlStateManager.popAttrib();
      }

   }
}
