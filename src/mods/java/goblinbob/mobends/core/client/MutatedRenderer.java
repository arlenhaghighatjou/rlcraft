package goblinbob.mobends.core.client;

import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.util.GlHelper;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public abstract class MutatedRenderer<T extends EntityLivingBase> {
   protected final float scale = 0.0625F;
   protected final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

   public MutatedRenderer() {
   }

   public void beforeRender(EntityData<T> data, T entity, float partialTicks) {
      double entityX = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
      double entityY = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks;
      double entityZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
      Entity viewEntity = Minecraft.getMinecraft().getRenderViewEntity();
      double viewX = entityX;
      double viewY = entityY;
      double viewZ = entityZ;
      if (viewEntity != null) {
         viewX = viewEntity.prevPosX + (viewEntity.posX - viewEntity.prevPosX) * (double)partialTicks;
         viewY = viewEntity.prevPosY + (viewEntity.posY - viewEntity.prevPosY) * (double)partialTicks;
         viewZ = viewEntity.prevPosZ + (viewEntity.posZ - viewEntity.prevPosZ) * (double)partialTicks;
      }

      GlStateManager.translate(entityX - viewX, entityY - viewY, entityZ - viewZ);
      GlStateManager.rotate(-interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks), 0.0F, 1.0F, 0.0F);
      this.renderLocalAccessories(entity, data, partialTicks);
      float globalScale = entity.isChild() ? this.getChildScale() : 1.0F;
      GlStateManager.translate(data.globalOffset.getX() * 0.0625F * globalScale, data.globalOffset.getY() * 0.0625F * globalScale, data.globalOffset.getZ() * 0.0625F * globalScale);
      GlStateManager.translate(0.0F, entity.height / 2.0F, 0.0F);
      GlHelper.rotate(data.centerRotation.getSmooth());
      GlStateManager.translate(0.0F, -entity.height / 2.0F, 0.0F);
      GlHelper.rotate(data.renderRotation.getSmooth());
      GlStateManager.translate(data.localOffset.getX() * 0.0625F * globalScale, data.localOffset.getY() * 0.0625F * globalScale, data.localOffset.getZ() * 0.0625F * globalScale);
      this.transformLocally(entity, data, partialTicks);
      GlStateManager.rotate(interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks), 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(viewX - entityX, viewY - entityY, viewZ - entityZ);
   }

   public void afterRender(T entity, float partialTicks) {
   }

   protected void renderLocalAccessories(T entity, EntityData<?> data, float partialTicks) {
   }

   protected void transformLocally(T entity, EntityData<?> data, float partialTicks) {
   }

   protected static float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
      float f;
      for(f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F) {
         ;
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return prevYawOffset + partialTicks * f;
   }

   protected float getChildScale() {
      return 0.5F;
   }
}
