package reborncore.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class RenderUtil {
   public static final ResourceLocation BLOCK_TEX = TextureMap.LOCATION_BLOCKS_TEXTURE;

   public RenderUtil() {
   }

   public static TextureManager engine() {
      return Minecraft.getMinecraft().renderEngine;
   }

   public static void bindBlockTexture() {
      engine().bindTexture(BLOCK_TEX);
   }

   public static EaglerTextureAtlasSprite getStillTexture(FluidStack fluid) {
      return fluid != null && fluid.getFluid() != null ? getStillTexture(fluid.getFluid()) : null;
   }

   public static EaglerTextureAtlasSprite getStillTexture(Fluid fluid) {
      ResourceLocation iconKey = fluid.getStill();
      return iconKey == null ? null : Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(iconKey.toString());
   }

   public static void renderGuiTank(FluidTank tank, double x, double y, double zLevel, double width, double height) {
      renderGuiTank(tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), x, y, zLevel, width, height);
   }

   public static void renderGuiTank(FluidStack fluid, int capacity, int amount, double x, double y, double zLevel, double width, double height) {
      if (fluid != null && fluid.getFluid() != null && fluid.amount > 0) {
         EaglerTextureAtlasSprite icon = getStillTexture(fluid);
         if (icon != null) {
            int renderAmount = (int)Math.max(Math.min(height, (double)amount * height / (double)capacity), 1.0D);
            int posY = (int)(y + height - (double)renderAmount);
            bindBlockTexture();
            int color = fluid.getFluid().getColor(fluid);
            GL11.glColor3ub((byte)(color >> 16 & 255), (byte)(color >> 8 & 255), (byte)(color & 255));
            GlStateManager.enableBlend();

            for(int i = 0; (double)i < width; i += 16) {
               for(int j = 0; j < renderAmount; j += 16) {
                  int drawWidth = (int)Math.min(width - (double)i, 16.0D);
                  int drawHeight = Math.min(renderAmount - j, 16);
                  int drawX = (int)(x + (double)i);
                  int drawY = posY + j;
                  double minU = (double)icon.getMinU();
                  double maxU = (double)icon.getMaxU();
                  double minV = (double)icon.getMinV();
                  double maxV = (double)icon.getMaxV();
                  Tessellator tessellator = Tessellator.getInstance();
                  WorldRenderer tes = tessellator.getBuffer();
                  tes.begin(7, DefaultVertexFormats.POSITION_TEX);
                  tes.pos((double)drawX, (double)(drawY + drawHeight), 0.0D).tex(minU, minV + (maxV - minV) * (double)drawHeight / 16.0D).endVertex();
                  tes.pos((double)(drawX + drawWidth), (double)(drawY + drawHeight), 0.0D).tex(minU + (maxU - minU) * (double)drawWidth / 16.0D, minV + (maxV - minV) * (double)drawHeight / 16.0D).endVertex();
                  tes.pos((double)(drawX + drawWidth), (double)drawY, 0.0D).tex(minU + (maxU - minU) * (double)drawWidth / 16.0D, minV).endVertex();
                  tes.pos((double)drawX, (double)drawY, 0.0D).tex(minU, minV).endVertex();
                  tessellator.draw();
               }
            }

            GlStateManager.disableBlend();
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public static void renderBlockInWorld(Block block, int meta) {
      renderItemInWorld(new ItemStack(block, 1, meta));
   }

   @SideOnly(Side.CLIENT)
   public static void renderItemInWorld(ItemStack stack) {
      GlStateManager.pushMatrix();
      GlStateManager.disableLighting();
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      if (stack.getItem() instanceof ItemBlock) {
         GlStateManager.scale(0.6D, 0.6D, 0.6D);
      } else {
         GlStateManager.scale(0.5D, 0.5D, 0.5D);
      }

      Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.FIXED);
      GlStateManager.enableLighting();
      GlStateManager.popMatrix();
   }

   public static void drawBar(WorldRenderer renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
      renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      renderer.pos((double)x, (double)y, 0.0D).color(red, green, blue, alpha).endVertex();
      renderer.pos((double)x, (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
      renderer.pos((double)(x + width), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
      renderer.pos((double)(x + width), (double)y, 0.0D).color(red, green, blue, alpha).endVertex();
      Tessellator.getInstance().draw();
   }
}
