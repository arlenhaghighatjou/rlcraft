package net.konwboy.tumbleweed.client;

import net.konwboy.tumbleweed.common.EntityTumbleweed;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.lax1dude.eaglercraft.v1_8.vector.Quaternion;

public class RenderTumbleweed extends Render<EntityTumbleweed> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("tumbleweed", "textures/entity/tumbleweed.png");
   private ModelTumbleweed tumbleweed;
   private int lastV = 0;
   private final Quaternion interpolated = new Quaternion();

   public RenderTumbleweed(RenderManager manager) {
      super(manager);
      this.shadowSize = 0.4F;
      this.shadowOpaque = 0.8F;
      this.tumbleweed = new ModelTumbleweed(0.0F);
      this.lastV = this.tumbleweed.getV();
   }

   public void doRender(EntityTumbleweed entity, double x, double y, double z, float yaw, float partialTicks) {
      if (this.lastV != this.tumbleweed.getV()) {
         this.tumbleweed = new ModelTumbleweed(0.0F);
         this.lastV = this.tumbleweed.getV();
      }

      GlStateManager.pushMatrix();
      GlStateManager.disableLighting();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      float alpha = 1.0F - (float)entity.fadeAge / 80.0F;
      alpha = alpha * 0.7F;
      this.shadowOpaque = alpha;
      GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
      GlStateManager.translate(x, y + (double)(entity.height * 0.3F), z);
      float size = 1.0F + (float)entity.getSize() / 8.0F;
      GlStateManager.scale(size, size, size);
      float stretch = entity.prevStretch + (entity.stretch - entity.prevStretch) * partialTicks;
      GlStateManager.scale(1.0F, stretch, 1.0F);
      lerp(entity.prevQuat, entity.quat, partialTicks, this.interpolated);
      float axisLength = (float)Math.sqrt(this.interpolated.x * this.interpolated.x
         + this.interpolated.y * this.interpolated.y + this.interpolated.z * this.interpolated.z);
      if (axisLength > 0.0F) {
         float angle = (float)Math.toDegrees(2.0D * Math.atan2(axisLength, this.interpolated.w));
         GlStateManager.rotate(angle, this.interpolated.x / axisLength, this.interpolated.y / axisLength, this.interpolated.z / axisLength);
      }
      GlStateManager.rotate(entity.rot1, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(entity.rot2, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(entity.rot3, 0.0F, 0.0F, 1.0F);
      this.bindTexture(TEXTURE);
      this.tumbleweed.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.disableBlend();
      GlStateManager.enableLighting();
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, yaw, partialTicks);
   }

   protected ResourceLocation getEntityTexture(EntityTumbleweed entity) {
      return TEXTURE;
   }

   private static void lerp(Quaternion start, Quaternion end, float alpha, Quaternion result) {
      float d = start.x * end.x + start.y * end.y + start.z * end.z + start.w * end.w;
      float absDot = d < 0.0F ? -d : d;
      float scale0 = 1.0F - alpha;
      float scale1 = alpha;
      if ((double)(1.0F - absDot) > 0.1D) {
         float angle = (float)Math.acos((double)absDot);
         float invSinTheta = 1.0F / (float)Math.sin((double)angle);
         scale0 = (float)Math.sin((double)((1.0F - alpha) * angle)) * invSinTheta;
         scale1 = (float)Math.sin((double)(alpha * angle)) * invSinTheta;
      }

      if (d < 0.0F) {
         scale1 = -scale1;
      }

      result.x = scale0 * start.x + scale1 * end.x;
      result.y = scale0 * start.y + scale1 * end.y;
      result.z = scale0 * start.z + scale1 * end.z;
      result.w = scale0 * start.w + scale1 * end.w;
   }

   public static class Factory implements IRenderFactory<EntityTumbleweed> {
      public Factory() {
      }

      public Render<? super EntityTumbleweed> createRenderFor(RenderManager manager) {
         return new RenderTumbleweed(manager);
      }
   }
}
