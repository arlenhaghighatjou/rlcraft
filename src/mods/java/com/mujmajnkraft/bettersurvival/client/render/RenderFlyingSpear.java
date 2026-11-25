package com.mujmajnkraft.bettersurvival.client.render;

import com.mujmajnkraft.bettersurvival.entities.projectiles.EntityFlyingSpear;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderFlyingSpear extends Render<EntityFlyingSpear> {
   private ItemStack spear;

   public RenderFlyingSpear(RenderManager renderManager) {
      super(renderManager);
   }

   public void doRender(@Nonnull EntityFlyingSpear entity, double x, double y, double z, float entityYaw, float partialTicks) {
      this.spear = entity.getSpear();
      GlStateManager.pushMatrix();
      GlStateManager.translate(x - 0.0D * Math.sin((double)entity.rotationYaw), y - 0.0D * Math.sin((double)entity.rotationYaw), z - 0.0D * Math.cos((double)entity.rotationYaw));
      GlStateManager.scale(1.0F, 1.0F, 1.0F);
      GlStateManager.rotate(entity.rotationYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-entity.rotationPitch, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-45.0F, 0.0F, 0.0F, 1.0F);
      Minecraft.getMinecraft().getRenderItem().renderItem(this.spear, TransformType.NONE);
      GlStateManager.popMatrix();
   }

   protected ResourceLocation getEntityTexture(EntityFlyingSpear entity) {
      return TextureMap.LOCATION_MISSING_TEXTURE;
   }
}
