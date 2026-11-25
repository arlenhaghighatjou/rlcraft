package vazkii.quark.world.client.layer;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

public class LayerAshenHeldItem implements LayerRenderer<EntitySkeleton> {
   protected final RenderLivingBase<?> livingEntityRenderer;

   public LayerAshenHeldItem(RenderLivingBase<?> livingEntityRendererIn) {
      this.livingEntityRenderer = livingEntityRendererIn;
   }

   public void doRenderLayer(@Nonnull EntitySkeleton living, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      boolean flag = living.getPrimaryHand() == EnumHandSide.RIGHT;
      ItemStack leftHand = flag ? ItemStack.EMPTY : living.getHeldItemMainhand();
      ItemStack rightHand = flag ? living.getHeldItemMainhand() : ItemStack.EMPTY;
      if (!leftHand.isEmpty() || !rightHand.isEmpty()) {
         GlStateManager.pushMatrix();
         if (this.livingEntityRenderer.getMainModel().isChild) {
            GlStateManager.translate(0.0F, 0.625F, 0.0F);
            GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
         }

         this.renderHeldItem(living, rightHand, TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
         this.renderHeldItem(living, leftHand, TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
         GlStateManager.popMatrix();
      }

   }

   private void renderHeldItem(EntityLivingBase entity, ItemStack stack, TransformType transform, EnumHandSide handSide) {
      if (!stack.isEmpty()) {
         GlStateManager.pushMatrix();
         if (entity.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         ((ModelBiped)this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625F, handSide);
         GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
         boolean flag = handSide == EnumHandSide.LEFT;
         GlStateManager.translate((float)(flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);
         Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, stack, transform, flag);
         GlStateManager.popMatrix();
      }

   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
