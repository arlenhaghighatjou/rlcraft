package net.silentchaos512.lib.client.render;

import net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.silentchaos512.lib.client.model.ILayeredBakedModel;
import net.silentchaos512.lib.item.ICustomEnchantColor;

@ParametersAreNonnullByDefault
public class TEISRCustomEnchantedEffect extends TileEntityItemStackRenderer {
   public static final TEISRCustomEnchantedEffect INSTANCE = new TEISRCustomEnchantedEffect();
   private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   private static final ImmutableSet<EnumFacing> EMPTY = ImmutableSet.of();
   private static final ImmutableSet<EnumFacing> STUPID_FACES = ImmutableSet.of(EnumFacing.SOUTH, EnumFacing.NORTH);

   private TEISRCustomEnchantedEffect() {
   }

   public void renderByItem(ItemStack stack) {
      this.renderByItem(stack, 1.0F);
   }

   public void renderByItem(ItemStack stack, float partialTicks) {
      IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, (World)null, (EntityLivingBase)null);
      if (!(model instanceof ILayeredBakedModel)) {
         throw new IllegalArgumentException("Model must be a ILayeredBakedModel");
      } else {
         int effectColor = this.getEffectColor(stack);
         int layerCount = ((ILayeredBakedModel)model).getLayerCount();

         for(int i = 0; i < layerCount; ++i) {
            this.renderModelExcluding(model, -1, stack, i, EMPTY);
         }

         if (stack.hasEffect()) {
            this.renderEffectExcluding(model, effectColor, 0, EMPTY);

            for(int i = 1; i < layerCount; ++i) {
               this.renderEffectExcluding(model, effectColor, i, STUPID_FACES);
            }
         }

      }
   }

   private int getEffectColor(ItemStack stack) {
      int effectColor = 8405196;
      if (stack.getItem() instanceof ICustomEnchantColor) {
         ICustomEnchantColor item = (ICustomEnchantColor)stack.getItem();
         effectColor = item.getEffectColor(stack);
         if (item.shouldTruncateBrightness(stack)) {
            int r = effectColor >> 16 & 255;
            int g = effectColor >> 8 & 255;
            int b = effectColor & 255;
            int maxBrightness = item.getEffectMaxBrightness(stack);
            int total = r + g + b;
            if (total > maxBrightness) {
               float scale = (float)maxBrightness / (float)total;
               r = (int)((float)r * scale);
               g = (int)((float)g * scale);
               b = (int)((float)b * scale);
               effectColor = (r << 16) + (g << 8) + b;
            }
         }
      }

      return effectColor;
   }

   private void renderQuads(WorldRenderer renderer, List<BakedQuad> quads, int color, ItemStack stack) {
      boolean flag = color == -1 && !stack.isEmpty();
      int i = 0;

      for(int j = quads.size(); i < j; ++i) {
         BakedQuad bakedquad = (BakedQuad)quads.get(i);
         int k = color;
         if (flag && bakedquad.hasTintIndex()) {
            k = Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, bakedquad.getTintIndex());
            k = k | -16777216;
         }

         LightUtil.renderQuadColor(renderer, bakedquad, k);
      }

   }

   private void renderEffectExcluding(IBakedModel model, int effectColor, int layerIndex, Collection<EnumFacing> excludeFaces) {
      int color = effectColor | -16777216;
      TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
      GlStateManager.depthMask(false);
      GlStateManager.depthFunc(514);
      GlStateManager.disableLighting();
      GlStateManager.blendFunc(RealOpenGLEnums.GL_SRC_COLOR, RealOpenGLEnums.GL_ONE);
      textureManager.bindTexture(RES_ITEM_GLINT);
      GlStateManager.matrixMode(5890);
      GlStateManager.pushMatrix();
      GlStateManager.scale(8.0F, 8.0F, 8.0F);
      float f = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
      GlStateManager.translate(f, 0.0F, 0.0F);
      GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
      this.renderModelExcluding(model, color, layerIndex, excludeFaces);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.scale(8.0F, 8.0F, 8.0F);
      float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
      GlStateManager.translate(-f1, 0.0F, 0.0F);
      GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
      this.renderModelExcluding(model, color, layerIndex, excludeFaces);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.blendFunc(RealOpenGLEnums.GL_SRC_ALPHA, RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA);
      GlStateManager.enableLighting();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
   }

   private void renderModelExcluding(IBakedModel model, int color, int layerIndex, Collection<EnumFacing> excludeFaces) {
      this.renderModelExcluding(model, color, ItemStack.EMPTY, layerIndex, excludeFaces);
   }

   private void renderModelExcluding(IBakedModel model, int color, ItemStack stack, int layerIndex, Collection<EnumFacing> excludeFaces) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
      List<BakedQuad> quads = new ArrayList();
      if (layerIndex < 0) {
         for(int i = 0; i < ((ILayeredBakedModel)model).getLayerCount(); ++i) {
            for(BakedQuad quad : model.getQuads((IBlockState)null, (EnumFacing)null, (long)i)) {
               if (!excludeFaces.contains(quad.getFace())) {
                  quads.add(quad);
               }
            }
         }
      } else {
         for(BakedQuad quad : model.getQuads((IBlockState)null, (EnumFacing)null, (long)layerIndex)) {
            if (!excludeFaces.contains(quad.getFace())) {
               quads.add(quad);
            }
         }
      }

      this.renderQuads(bufferbuilder, quads, color, stack);
      tessellator.draw();
   }
}
