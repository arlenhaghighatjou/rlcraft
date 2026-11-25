package com.endertech.minecraft.forge.api;

import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import com.endertech.common.CommonMath;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBaubleWear extends IBauble, IRenderBauble {
   String IFACE = "com.endertech.minecraft.forge.api.IBaubleWear";
   String MODID = "baubles";
   float SCALE = 0.625F;

   default void translateForHead(EntityPlayer player) {
      float delta = 0.25F;
      if (player.isSneaking()) {
         CommonMath.Angle angle = CommonMath.Angle.fromDegrees((double)player.rotationPitch);
         float radians = (float)angle.getRadians();
         GlStateManager.translate(delta * MathHelper.sin(radians), delta * MathHelper.cos(radians), 0.0F);
      }

      GlStateManager.translate(0.0F, -delta, 0.0F);
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.scale(0.625F, -0.625F, -0.625F);
   }

   default void translateForChest(EntityPlayer player) {
      float delta = 0.25F;
      if (player.isSneaking()) {
         GlStateManager.translate(0.0F, 0.2F, 0.0F);
         GlStateManager.rotate(28.647888F, 1.0F, 0.0F, 0.0F);
      }

      GlStateManager.translate(0.0F, -delta, 0.0F);
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.scale(0.625F, -0.625F, -0.625F);
   }

   @SideOnly(Side.CLIENT)
   default void renderItemOn(EntityLivingBase entity, ItemStack stack, TransformType transform) {
      Minecraft.getMinecraft().getItemRenderer().renderItem(entity, stack, transform);
   }
}
