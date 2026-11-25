package com.oblivioussp.spartanweaponry.client.render.projectile;

import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrownWeapon;
import com.oblivioussp.spartanweaponry.init.ItemRegistrySW;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderThrownWeapon<T extends EntityThrownWeapon> extends Render<T> {
   private final RenderItem itemRenderer;

   public RenderThrownWeapon(RenderManager renderManagerIn, RenderItem itemRendererIn) {
      super(renderManagerIn);
      this.itemRenderer = itemRendererIn;
   }

   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      double posX = x;
      double posY = y;
      double posZ = z;
      if (entity.isAirBorne) {
         posX = x + entity.motionX * (double)partialTicks;
         posY = y + entity.motionY * (double)partialTicks;
         posZ = z + entity.motionZ * (double)partialTicks;
      }

      GlStateManager.translate((float)posX, (float)posY, (float)posZ);
      GlStateManager.scale(2.0D, 2.0D, 2.0D);
      GlStateManager.enableRescaleNormal();
      this.doRenderTransformations(entity, partialTicks);
      this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(entity));
      }

      ItemStack weapon = entity.getWeaponStack();
      if (!weapon.isEmpty()) {
         this.itemRenderer.renderItem(entity.getWeaponStack(), TransformType.GROUND);
      } else {
         this.itemRenderer.renderItem(new ItemStack(ItemRegistrySW.daggerStone), TransformType.GROUND);
      }

      if (this.renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   protected void doRenderTransformations(T entity, float partialTicks) {
      GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks - 45.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(-0.1D, -0.2D, 0.0D);
   }

   protected ResourceLocation getEntityTexture(T entity) {
      return TextureMap.LOCATION_BLOCKS_TEXTURE;
   }
}
