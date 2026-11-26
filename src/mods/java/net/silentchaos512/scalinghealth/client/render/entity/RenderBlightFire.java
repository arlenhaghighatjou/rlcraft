package net.silentchaos512.scalinghealth.client.render.entity;

import com.arlenh7.eaglercraft.awt.Color;
import javax.annotation.Nonnull;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.scalinghealth.entity.EntityBlightFire;
import net.silentchaos512.scalinghealth.lib.module.ModuleAprilTricks;

public final class RenderBlightFire extends Render<EntityBlightFire> {
   private static final float FIRE_SCALE = 1.8F;
   private final ResourceLocation TEXTURE;
   private final ResourceLocation TEXTURE_GRAY;

   private RenderBlightFire(RenderManager renderManager) {
      super(renderManager);
      this.TEXTURE = new ResourceLocation("scalinghealth", "textures/entity/blightfire.png");
      this.TEXTURE_GRAY = new ResourceLocation("scalinghealth", "textures/entity/blightfire_gray.png");
   }

   @Nonnull
   protected ResourceLocation getEntityTexture(EntityBlightFire entity) {
      return ModuleAprilTricks.instance.isRightDay() && ModuleAprilTricks.instance.isEnabled() ? this.TEXTURE_GRAY : this.TEXTURE;
   }

   public boolean shouldRender(EntityBlightFire fire, ICamera camera, double camX, double camY, double camZ) {
      EntityLivingBase parent = fire.getParent();
      if (parent == null) {
         return false;
      } else {
         AxisAlignedBB axisalignedbb = parent.getRenderBoundingBox().grow(0.5D);
         if (axisalignedbb.hasNaN() || axisalignedbb.getAverageEdgeLength() == 0.0D) {
            axisalignedbb = new AxisAlignedBB(parent.posX - 2.0D, parent.posY - 2.0D, parent.posZ - 2.0D, parent.posX + 2.0D, parent.posY + 2.0D, parent.posZ + 2.0D);
         }

         return parent.isInRangeToRender3d(camX, camY, camZ) && (parent.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(axisalignedbb));
      }
   }

   public void doRender(EntityBlightFire fire, double x, double y, double z, float entityYaw, float partialTicks) {
      EntityLivingBase parent = fire.getParent();
      if (parent != null) {
         GlStateManager.disableLighting();
         GlStateManager.pushMatrix();
         GlStateManager.translate(x, y - (double)parent.height + 0.5D, z);
         float f = parent.width * 1.8F;
         GlStateManager.scale(f, f, f);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer vertexbuffer = tessellator.getBuffer();
         float f1 = 0.5F;
         float f2 = 0.0F;
         float f3 = parent.height / f;
         float f4 = (float)(parent.posY - parent.getEntityBoundingBox().minY);
         GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, 0.0F, (float)((int)f3) * 0.02F);
         if (ModuleAprilTricks.instance.isRightDay() && ModuleAprilTricks.instance.isEnabled()) {
            float changeRate = 40.0F + (float)parent.getEntityId() % 80.0F;
            float hue = (float)(ClientTicks.ticksInGame + parent.getEntityId()) % changeRate / changeRate;
            Color color = new Color(Color.HSBtoRGB(hue, 1.0F, 1.0F));
            GlStateManager.color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 1.0F);
         } else {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }

         float f5 = 0.0F;
         int i = 0;
         vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
         this.bindTexture(this.getEntityTexture(fire));

         while(f3 > 0.0F) {
            boolean flag = i % 2 == 0;
            int frame = ClientTicks.ticksInGame % 32;
            float minU = flag ? 0.5F : 0.0F;
            float minV = (float)frame / 32.0F;
            float maxU = flag ? 1.0F : 0.5F;
            float maxV = (float)(frame + 1) / 32.0F;
            if (flag) {
               float f10 = maxU;
               maxU = minU;
               minU = f10;
            }

            vertexbuffer.pos((double)(f1 - f2), (double)(0.0F - f4), (double)f5).tex((double)maxU, (double)maxV).endVertex();
            vertexbuffer.pos((double)(-f1 - f2), (double)(0.0F - f4), (double)f5).tex((double)minU, (double)maxV).endVertex();
            vertexbuffer.pos((double)(-f1 - f2), (double)(1.4F - f4), (double)f5).tex((double)minU, (double)minV).endVertex();
            vertexbuffer.pos((double)(f1 - f2), (double)(1.4F - f4), (double)f5).tex((double)maxU, (double)minV).endVertex();
            f3 -= 0.45F;
            f4 -= 0.45F;
            f1 *= 0.9F;
            f5 += 0.03F;
            ++i;
         }

         tessellator.draw();
         GlStateManager.popMatrix();
         GlStateManager.enableLighting();
      }
   }

   public static class Factory implements IRenderFactory<EntityBlightFire> {
      public static final RenderBlightFire.Factory INSTANCE = new RenderBlightFire.Factory();

      public Factory() {
      }

      public Render<EntityBlightFire> createRenderFor(RenderManager manager) {
         return new RenderBlightFire(manager);
      }
   }
}
