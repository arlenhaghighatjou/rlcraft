package mezz.jei.plugins.vanilla.ingredients.fluid;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidStackRenderer implements IIngredientRenderer<FluidStack> {
   private static final int TEX_WIDTH = 16;
   private static final int TEX_HEIGHT = 16;
   private static final int MIN_FLUID_HEIGHT = 1;
   private final int capacityMb;
   private final FluidStackRenderer.TooltipMode tooltipMode;
   private final int width;
   private final int height;
   @Nullable
   private final IDrawable overlay;

   public FluidStackRenderer() {
      this(1000, FluidStackRenderer.TooltipMode.ITEM_LIST, 16, 16, (IDrawable)null);
   }

   public FluidStackRenderer(int capacityMb, boolean showCapacity, int width, int height, @Nullable IDrawable overlay) {
      this(capacityMb, showCapacity ? FluidStackRenderer.TooltipMode.SHOW_AMOUNT_AND_CAPACITY : FluidStackRenderer.TooltipMode.SHOW_AMOUNT, width, height, overlay);
   }

   public FluidStackRenderer(int capacityMb, FluidStackRenderer.TooltipMode tooltipMode, int width, int height, @Nullable IDrawable overlay) {
      this.capacityMb = capacityMb;
      this.tooltipMode = tooltipMode;
      this.width = width;
      this.height = height;
      this.overlay = overlay;
   }

   public void render(Minecraft minecraft, int xPosition, int yPosition, @Nullable FluidStack fluidStack) {
      GlStateManager.enableBlend();
      GlStateManager.enableAlpha();
      this.drawFluid(minecraft, xPosition, yPosition, fluidStack);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.overlay != null) {
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, 0.0F, 200.0F);
         this.overlay.draw(minecraft, xPosition, yPosition);
         GlStateManager.popMatrix();
      }

      GlStateManager.disableAlpha();
      GlStateManager.disableBlend();
   }

   private void drawFluid(Minecraft minecraft, int xPosition, int yPosition, @Nullable FluidStack fluidStack) {
      if (fluidStack != null) {
         Fluid fluid = fluidStack.getFluid();
         if (fluid != null) {
            EaglerTextureAtlasSprite fluidStillSprite = getStillFluidSprite(minecraft, fluid);
            int fluidColor = fluid.getColor(fluidStack);
            int scaledAmount = fluidStack.amount * this.height / this.capacityMb;
            if (fluidStack.amount > 0 && scaledAmount < 1) {
               scaledAmount = 1;
            }

            if (scaledAmount > this.height) {
               scaledAmount = this.height;
            }

            this.drawTiledSprite(minecraft, xPosition, yPosition, this.width, this.height, fluidColor, scaledAmount, fluidStillSprite);
         }
      }
   }

   private void drawTiledSprite(Minecraft minecraft, int xPosition, int yPosition, int tiledWidth, int tiledHeight, int color, int scaledAmount, EaglerTextureAtlasSprite sprite) {
      minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      setGLColorFromInt(color);
      int xTileCount = tiledWidth / 16;
      int xRemainder = tiledWidth - xTileCount * 16;
      int yTileCount = scaledAmount / 16;
      int yRemainder = scaledAmount - yTileCount * 16;
      int yStart = yPosition + tiledHeight;

      for(int xTile = 0; xTile <= xTileCount; ++xTile) {
         for(int yTile = 0; yTile <= yTileCount; ++yTile) {
            int width = xTile == xTileCount ? xRemainder : 16;
            int height = yTile == yTileCount ? yRemainder : 16;
            int x = xPosition + xTile * 16;
            int y = yStart - (yTile + 1) * 16;
            if (width > 0 && height > 0) {
               int maskTop = 16 - height;
               int maskRight = 16 - width;
               drawTextureWithMasking((double)x, (double)y, sprite, maskTop, maskRight, 100.0D);
            }
         }
      }

   }

   private static EaglerTextureAtlasSprite getStillFluidSprite(Minecraft minecraft, Fluid fluid) {
      TextureMap textureMapBlocks = minecraft.getTextureMapBlocks();
      ResourceLocation fluidStill = fluid.getStill();
      EaglerTextureAtlasSprite fluidStillSprite = null;
      if (fluidStill != null) {
         fluidStillSprite = textureMapBlocks.getTextureExtry(fluidStill.toString());
      }

      if (fluidStillSprite == null) {
         fluidStillSprite = textureMapBlocks.getMissingSprite();
      }

      return fluidStillSprite;
   }

   private static void setGLColorFromInt(int color) {
      float red = (float)(color >> 16 & 255) / 255.0F;
      float green = (float)(color >> 8 & 255) / 255.0F;
      float blue = (float)(color & 255) / 255.0F;
      GlStateManager.color(red, green, blue, 1.0F);
   }

   private static void drawTextureWithMasking(double xCoord, double yCoord, EaglerTextureAtlasSprite textureSprite, int maskTop, int maskRight, double zLevel) {
      double uMin = (double)textureSprite.getMinU();
      double uMax = (double)textureSprite.getMaxU();
      double vMin = (double)textureSprite.getMinV();
      double vMax = (double)textureSprite.getMaxV();
      uMax = uMax - (double)maskRight / 16.0D * (uMax - uMin);
      vMax = vMax - (double)maskTop / 16.0D * (vMax - vMin);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer bufferBuilder = tessellator.getBuffer();
      bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferBuilder.pos(xCoord, yCoord + 16.0D, zLevel).tex(uMin, vMax).endVertex();
      bufferBuilder.pos(xCoord + 16.0D - (double)maskRight, yCoord + 16.0D, zLevel).tex(uMax, vMax).endVertex();
      bufferBuilder.pos(xCoord + 16.0D - (double)maskRight, yCoord + (double)maskTop, zLevel).tex(uMax, vMin).endVertex();
      bufferBuilder.pos(xCoord, yCoord + (double)maskTop, zLevel).tex(uMin, vMin).endVertex();
      tessellator.draw();
   }

   public List<String> getTooltip(Minecraft minecraft, FluidStack fluidStack, ITooltipFlag tooltipFlag) {
      List<String> tooltip = new ArrayList();
      Fluid fluidType = fluidStack.getFluid();
      if (fluidType == null) {
         return tooltip;
      } else {
         String fluidName = fluidType.getLocalizedName(fluidStack);
         tooltip.add(fluidName);
         if (this.tooltipMode == FluidStackRenderer.TooltipMode.SHOW_AMOUNT_AND_CAPACITY) {
            String amount = Translator.translateToLocalFormatted("jei.tooltip.liquid.amount.with.capacity", fluidStack.amount, this.capacityMb);
            tooltip.add(TextFormatting.GRAY + amount);
         } else if (this.tooltipMode == FluidStackRenderer.TooltipMode.SHOW_AMOUNT) {
            String amount = Translator.translateToLocalFormatted("jei.tooltip.liquid.amount", fluidStack.amount);
            tooltip.add(TextFormatting.GRAY + amount);
         }

         return tooltip;
      }
   }

   static enum TooltipMode {
      SHOW_AMOUNT,
      SHOW_AMOUNT_AND_CAPACITY,
      ITEM_LIST;

      private TooltipMode() {
      }
   }
}
