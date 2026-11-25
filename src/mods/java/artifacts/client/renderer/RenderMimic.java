package artifacts.client.renderer;

import artifacts.client.model.entity.ModelMimic;
import artifacts.client.model.entity.ModelMimicInterior;
import artifacts.common.entity.EntityMimic;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager.Profile;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@ParametersAreNonnullByDefault
public class RenderMimic extends RenderLiving<EntityMimic> {
   public static final RenderMimic.Factory FACTORY = new RenderMimic.Factory();
   private static final ResourceLocation MIMIC_TEXTURE = new ResourceLocation("artifacts", "textures/entity/mimic/mimic.png");
   private static final ResourceLocation VANILLA_CHEST = new ResourceLocation("textures/entity/chest/normal.png");
   private ModelBase secondModel = new ModelMimicInterior();

   protected RenderMimic(RenderManager renderManager) {
      super(renderManager, new ModelMimic(), 0.45F);
   }

   protected ResourceLocation getEntityTexture(EntityMimic entity) {
      return VANILLA_CHEST;
   }

   protected ResourceLocation getSecondaryEntityTexture(EntityMimic entity) {
      return MIMIC_TEXTURE;
   }

   public void doRender(EntityMimic entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (!MinecraftForge.EVENT_BUS.post(new Pre(entity, this, partialTicks, x, y, z))) {
         GlStateManager.pushMatrix();
         GlStateManager.disableCull();
         this.mainModel.swingProgress = this.secondModel.swingProgress = this.getSwingProgress(entity, partialTicks);
         boolean shouldSit = entity.isRiding() && entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit();
         this.mainModel.isRiding = this.secondModel.isRiding = shouldSit;
         this.mainModel.isChild = this.secondModel.isChild = entity.isChild();

         try {
            float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
            float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
            float f2 = f1 - f;
            if (shouldSit && entity.getRidingEntity() instanceof EntityLivingBase) {
               EntityLivingBase entitylivingbase = (EntityLivingBase)entity.getRidingEntity();
               f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
               f2 = f1 - f;
               float f3 = MathHelper.wrapDegrees(f2);
               if (f3 < -85.0F) {
                  f3 = -85.0F;
               }

               if (f3 >= 85.0F) {
                  f3 = 85.0F;
               }

               f = f1 - f3;
               if (f3 * f3 > 2500.0F) {
                  f += f3 * 0.2F;
               }

               f2 = f1 - f;
            }

            float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            this.renderLivingAt(entity, x, y, z);
            float f8 = this.handleRotationFloat(entity, partialTicks);
            this.applyRotations(entity, f8, f, partialTicks);
            float f4 = this.prepareScale(entity, partialTicks);
            float f5 = 0.0F;
            float f6 = 0.0F;
            if (!entity.isRiding()) {
               f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
               f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
               if (entity.isChild()) {
                  f6 *= 3.0F;
               }

               if (f5 > 1.0F) {
                  f5 = 1.0F;
               }

               f2 = f1 - f;
            }

            GlStateManager.enableAlpha();
            this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
            this.secondModel.setLivingAnimations(entity, f6, f5, partialTicks);
            this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);
            this.secondModel.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);
            if (this.renderOutlines) {
               boolean flag1 = this.setScoreTeamColor(entity);
               GlStateManager.enableColorMaterial();
               GlStateManager.enableOutlineMode(this.getTeamColor(entity));
               if (!this.renderMarker) {
                  this.renderModel(entity, f6, f5, f8, f2, f7, f4);
               }

               this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
               GlStateManager.disableOutlineMode();
               GlStateManager.disableColorMaterial();
               if (flag1) {
                  this.unsetScoreTeamColor();
               }
            } else {
               boolean flag = this.setDoRenderBrightness(entity, partialTicks);
               this.renderModel(entity, f6, f5, f8, f2, f7, f4);
               if (flag) {
                  this.unsetBrightness();
               }

               GlStateManager.depthMask(true);
               this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
            }

            GlStateManager.disableRescaleNormal();
         } catch (Exception var20) {
            ;
         }

         GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
         GlStateManager.enableTexture2D();
         GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
         GlStateManager.enableCull();
         GlStateManager.popMatrix();
         if (!this.renderOutlines) {
            this.renderName(entity, x, y, z);
         }

         MinecraftForge.EVENT_BUS.post(new Post(entity, this, partialTicks, x, y, z));
      }
   }

   protected void renderModel(EntityMimic entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      boolean flag = this.isVisible(entitylivingbaseIn);
      boolean flag1 = !flag && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().player);
      if (flag || flag1) {
         if (!this.bindEntityTexture(entitylivingbaseIn)) {
            return;
         }

         if (flag1) {
            GlStateManager.enableBlendProfile(Profile.TRANSPARENT_MODEL);
            this.mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            GlStateManager.disableBlendProfile(Profile.TRANSPARENT_MODEL);
         } else {
            this.mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
         }

         if (!this.bindSecondaryTexture(entitylivingbaseIn)) {
            return;
         }

         if (flag1) {
            GlStateManager.enableBlendProfile(Profile.TRANSPARENT_MODEL);
            this.secondModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            GlStateManager.disableBlendProfile(Profile.TRANSPARENT_MODEL);
         } else {
            this.secondModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
         }
      }

   }

   private boolean bindSecondaryTexture(EntityMimic entity) {
      ResourceLocation resourcelocation = this.getSecondaryEntityTexture(entity);
      if (resourcelocation == null) {
         return false;
      } else {
         this.bindTexture(resourcelocation);
         return true;
      }
   }

   public static class Factory implements IRenderFactory<EntityMimic> {
      public Factory() {
      }

      public Render<? super EntityMimic> createRenderFor(RenderManager manager) {
         return new RenderMimic(manager);
      }
   }
}
