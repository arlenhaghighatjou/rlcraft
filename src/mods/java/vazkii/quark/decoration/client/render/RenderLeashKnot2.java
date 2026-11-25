package vazkii.quark.decoration.client.render;

import javax.annotation.Nonnull;
import net.minecraft.client.model.ModelLeashKnot;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.decoration.entity.EntityLeashKnot2TheKnotting;
import vazkii.quark.world.client.render.ChainRenderer;

public class RenderLeashKnot2 extends Render<EntityLeashKnot2TheKnotting> {
   private static final ResourceLocation LEASH_KNOT_TEXTURES = new ResourceLocation("textures/entity/lead_knot.png");
   private final ModelLeashKnot leashKnotModel = new ModelLeashKnot();
   public static final IRenderFactory<EntityLeashKnot2TheKnotting> FACTORY = RenderLeashKnot2::new;

   public RenderLeashKnot2(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   public boolean shouldRender(EntityLeashKnot2TheKnotting livingEntity, @Nonnull ICamera camera, double camX, double camY, double camZ) {
      if (super.shouldRender(livingEntity, camera, camX, camY, camZ)) {
         return true;
      } else if (livingEntity.getLeashed()) {
         Entity entity = livingEntity.getLeashHolder();
         return camera.isBoundingBoxInFrustum(entity.getRenderBoundingBox());
      } else {
         return false;
      }
   }

   public void doRender(@Nonnull EntityLeashKnot2TheKnotting entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      float f = 0.125F;
      GlStateManager.translate(x, y + (double)f, z);
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlpha();
      this.bindEntityTexture(entity);
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(entity));
      }

      this.leashKnotModel.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      if (this.renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
      if (!this.renderOutlines && entity.getLeashed()) {
         this.renderLeash(entity, x, y, z, partialTicks);
      }

   }

   protected ResourceLocation getEntityTexture(@Nonnull EntityLeashKnot2TheKnotting entity) {
      return LEASH_KNOT_TEXTURES;
   }

   protected void renderLeash(EntityLeashKnot2TheKnotting leash, double x, double y, double z, float partialTicks) {
      Entity entity = leash.getLeashHolder();
      if (entity != null) {
         y = y - (2.9D - (double)leash.height) * 0.5D;
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer bufferbuilder = tessellator.getBuffer();
         double d0 = this.interpolateValue((double)entity.prevRotationYaw, (double)entity.rotationYaw, (double)(partialTicks * 0.5F)) * 0.01745329238474369D;
         double d1 = this.interpolateValue((double)entity.prevRotationPitch, (double)entity.rotationPitch, (double)(partialTicks * 0.5F)) * 0.01745329238474369D;
         double d2 = Math.cos(d0);
         double d3 = Math.sin(d0);
         double d4 = Math.sin(d1);
         float off = 1.3F;
         if (entity instanceof EntityHanging) {
            off = 1.1F;
            d2 = 0.0D;
            d3 = 0.0D;
            d4 = -1.0D;
         }

         double d5 = Math.cos(d1);
         double d6 = this.interpolateValue(entity.prevPosX, entity.posX, (double)partialTicks) - d2 * 0.7D - d3 * 0.5D * d5;
         double d7 = this.interpolateValue(entity.prevPosY + (double)entity.getEyeHeight() * 0.7D + (double)off, entity.posY + (double)entity.getEyeHeight() * 0.7D + (double)off, (double)partialTicks) - d4 * 0.5D - 0.25D;
         double d8 = this.interpolateValue(entity.prevPosZ, entity.posZ, (double)partialTicks) - d3 * 0.7D + d2 * 0.5D * d5;
         double d9 = this.interpolateValue((double)leash.prevRenderYawOffset, (double)leash.renderYawOffset, (double)partialTicks) * 0.01745329238474369D + 1.5707963267948966D;
         d2 = Math.cos(d9) * (double)leash.width * 0.4D;
         d3 = Math.sin(d9) * (double)leash.width * 0.4D;
         double d10 = this.interpolateValue(leash.prevPosX, leash.posX, (double)partialTicks) + d2;
         double d11 = this.interpolateValue(leash.prevPosY, leash.posY, (double)partialTicks);
         double d12 = this.interpolateValue(leash.prevPosZ, leash.posZ, (double)partialTicks) + d3;
         x = x + d2;
         z = z + d3;
         double d13 = (double)((float)(d6 - d10));
         double d14 = (double)((float)(d7 - d11));
         double d15 = (double)((float)(d8 - d12));
         GlStateManager.disableTexture2D();
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         ChainRenderer.drawChainSegment(x, y, z, bufferbuilder, d13, d14, d15, 0.025D, 0.0D, 0.5F, 0.4F, 0.3F, 1.0D);
         ChainRenderer.drawChainSegment(x, y, z, bufferbuilder, d13, d14, d15, 0.0D, 0.025D, 0.5F, 0.4F, 0.3F, 1.0D);
         GlStateManager.enableLighting();
         GlStateManager.enableTexture2D();
         GlStateManager.enableCull();
      }
   }

   private double interpolateValue(double start, double end, double pct) {
      return start + (end - start) * pct;
   }
}
