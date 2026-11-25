package net.mcft.copy.backpacks.client;

import net.mcft.copy.backpacks.ProxyClient;
import net.mcft.copy.backpacks.api.BackpackHelper;
import net.mcft.copy.backpacks.api.BackpackRegistry;
import net.mcft.copy.backpacks.api.IBackpack;
import net.mcft.copy.backpacks.api.IBackpackType;
import net.mcft.copy.backpacks.block.entity.TileEntityBackpack;
import net.mcft.copy.backpacks.config.ModConfig;
import net.mcft.copy.backpacks.misc.util.IntermodUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RendererBackpack {
   private static final ResourceLocation ENCHANTED_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

   private RendererBackpack() {
   }

   private static void renderBackpack(IBackpack backpack, float ticks, boolean renderStraps) {
      ItemStack stack = backpack.getStack();
      int color = ProxyClient.ITEM_COLOR.colorMultiplier(stack, 0);
      BlockModelRenderer renderer = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer();
      Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      float r = (float)(color >> 16 & 255) / 255.0F;
      float g = (float)(color >> 8 & 255) / 255.0F;
      float b = (float)(color & 255) / 255.0F;
      renderModel(backpack, renderer, ticks, renderStraps, r, g, b, false);
      if (stack.isItemEnchanted()) {
         renderEnchanted(backpack, renderer, ticks, renderStraps);
      }

   }

   private static void renderModel(IBackpack backpack, BlockModelRenderer renderer, float ticks, boolean renderStraps, float r, float g, float b, boolean useEnch) {
      IBakedModel baseModel = useEnch ? ProxyClient.MODEL_BACKPACK_ENCH : ProxyClient.MODEL_BACKPACK;
      renderer.renderModelBrightnessColor(baseModel, 1.0F, r, g, b);
      if (renderStraps) {
         renderer.renderModelBrightnessColor(ProxyClient.MODEL_BACKPACK_STRAPS, 1.0F, r, g, b);
      }

      float lidAngle = 0.0F;
      IBackpackType type = backpack.getType();
      if (type != null) {
         int lidTicks = backpack.getLidTicks();
         int prevLidTicks = backpack.getPrevLidTicks();
         lidAngle = type.getLidAngle(lidTicks, prevLidTicks, ticks % 1.0F);
      }

      float lidYOffset = 0.5625F;
      float lidZOffset = 0.3125F;
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, lidYOffset, lidZOffset);
      GlStateManager.rotate(lidAngle, -1.0F, 0.0F, 0.0F);
      GlStateManager.translate(0.0F, -lidYOffset, -lidZOffset);
      IBakedModel topModel = useEnch ? ProxyClient.MODEL_BACKPACK_ENCH_TOP : ProxyClient.MODEL_BACKPACK_TOP;
      renderer.renderModelBrightnessColor(topModel, 1.0F, r, g, b);
      GlStateManager.popMatrix();
   }

   private static void renderEnchanted(IBackpack backpack, BlockModelRenderer renderer, float ticks, boolean renderStraps) {
      Minecraft mc = Minecraft.getMinecraft();
      float glintStrength = (float)ModConfig.client.enchantEffectOpacity;
      if (glintStrength > 0.0F) {
         float glintScale = 1.5F;
         float animProgress = ticks / 10.0F;
         int color = IntermodUtils.getRuneColor(backpack.getStack());
         float r = (float)(color >> 16 & 255) / 255.0F * glintStrength;
         float g = (float)(color >> 8 & 255) / 255.0F * glintStrength;
         float b = (float)(color & 255) / 255.0F * glintStrength;
         mc.getTextureManager().bindTexture(ENCHANTED_ITEM_GLINT);
         mc.entityRenderer.setupFogColor(true);
         GlStateManager.disableLighting();
         GlStateManager.depthMask(false);
         GlStateManager.depthFunc(514);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(SourceFactor.SRC_COLOR, DestFactor.ONE);

         for(int i = 0; i < 2; ++i) {
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.scale(glintScale, glintScale, glintScale);
            GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, animProgress * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
            GlStateManager.matrixMode(5888);
            renderModel(backpack, renderer, ticks, renderStraps, r, g, b, true);
         }

         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         GlStateManager.matrixMode(5888);
         GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.disableBlend();
         GlStateManager.depthFunc(515);
         GlStateManager.depthMask(true);
         GlStateManager.enableLighting();
         mc.entityRenderer.setupFogColor(false);
      }
   }

   public static class Layer implements LayerRenderer<EntityLivingBase> {
      private static IBackpack _overrideBackpack = null;
      private static BackpackRegistry.RenderOptions _overrideRenderOptions = null;
      protected final RenderLivingBase<?> livingEntityRenderer;

      public Layer(RenderLivingBase<?> livingEntityRendererIn) {
         this.livingEntityRenderer = livingEntityRendererIn;
      }

      public boolean shouldCombineTextures() {
         return false;
      }

      public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
         IBackpack backpack;
         BackpackRegistry.RenderOptions renderOptions;
         if (_overrideBackpack != null) {
            backpack = _overrideBackpack;
            renderOptions = _overrideRenderOptions;
         } else {
            backpack = BackpackHelper.getBackpack(entity);
            BackpackRegistry.BackpackEntityEntry entry = BackpackRegistry.getDefaultEntityEntry(entity.getClass());
            renderOptions = entry != null ? entry.renderOptions : null;
         }

         if (backpack != null && renderOptions != null) {
            GlStateManager.pushMatrix();
            if (entity.isChild()) {
               GlStateManager.scale(0.5F, 0.5F, 0.5F);
               GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
            }

            ModelBase modelBase = this.livingEntityRenderer.getMainModel();
            if (modelBase instanceof ModelBiped) {
               ModelRenderer bipedBody = ((ModelBiped)modelBase).bipedBody;
               if (entity.isSneaking()) {
                  GlStateManager.translate(0.0F, 0.2F, 0.0F);
               }

               bipedBody.postRender(scale);
            } else {
               float swingProgress = entity.getSwingProgress(partialTicks);
               float swingAngle = MathHelper.sin(MathHelper.sqrt(swingProgress) * 6.2831855F) * 0.2F;
               if (entity.swingingHand == EnumHand.OFF_HAND ^ entity.getPrimaryHand() == EnumHandSide.LEFT) {
                  swingAngle *= -1.0F;
               }

               if (swingAngle != 0.0F) {
                  GlStateManager.rotate((float)Math.toDegrees((double)swingAngle), 0.0F, 1.0F, 0.0F);
               }

               if (entity.isSneaking()) {
                  GlStateManager.translate(0.0F, 0.2F, 0.0F);
                  GlStateManager.rotate(28.647888F, 1.0F, 0.0F, 0.0F);
               }
            }

            GlStateManager.scale(renderOptions.scale, renderOptions.scale, renderOptions.scale);
            GlStateManager.translate((double)(8.0F * scale), renderOptions.y * (double)scale, renderOptions.z * (double)scale);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate((float)renderOptions.rotate, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(0.0D, ProxyClient.MODEL_BACKPACK_BOX.maxY * (double)scale * -16.0D, ProxyClient.MODEL_BACKPACK_BOX.minZ * (double)scale * -16.0D);
            RendererBackpack.renderBackpack(backpack, (float)entity.ticksExisted + partialTicks, false);
            GlStateManager.popMatrix();
         }
      }

      public static void setOverride(IBackpack backpack, BackpackRegistry.RenderOptions renderOptions) {
         _overrideBackpack = backpack;
         _overrideRenderOptions = renderOptions;
      }

      public static void resetOverride() {
         setOverride((IBackpack)null, (BackpackRegistry.RenderOptions)null);
      }
   }

   public static class TileEntity extends TileEntitySpecialRenderer<TileEntityBackpack> {
      public TileEntity() {
      }

      public void render(TileEntityBackpack entity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
         IBackpack backpack = BackpackHelper.getBackpack(entity);
         if (backpack != null) {
            float angle = entity.facing.getHorizontalAngle();
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
            GlStateManager.rotate(angle, 0.0F, -1.0F, 0.0F);
            GlStateManager.translate(-0.5D, -0.5D, -0.5D);
            RendererBackpack.renderBackpack(backpack, (float)entity.getAge() + partialTicks, true);
            GlStateManager.popMatrix();
         }
      }
   }
}
