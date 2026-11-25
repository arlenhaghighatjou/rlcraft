package com.dhanantry.scapeandrunparasites.client.renderer.entity.crude;

import com.dhanantry.scapeandrunparasites.client.model.entity.crude.ModelMes;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.layer.LayerSnow;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityMes;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderMes extends RenderMalleable<EntityMes> {
   public static final ResourceLocation TEXTUREM = new ResourceLocation("srparasites:textures/entity/monster/mes.png");
   public static final ResourceLocation TEXTURE1 = new ResourceLocation("srparasites:textures/entity/monster/mes1.png");

   public RenderMes(RenderManager manager) {
      super(manager, new ModelMes(), 0.7F);
      this.addLayer(new LayerSnow(this, new ResourceLocation("srparasites:textures/entity/layer/messnow.png"), 1.0D, 1.0D, 1.0D));
   }

   protected void preRenderCallback(EntityMes entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      GlStateManager.scale(f2, f3, f2);
   }

   protected ResourceLocation getEntityTexture(EntityMes entity) {
      switch(entity.getSkin()) {
      case 1:
         return TEXTURE1;
      default:
         return TEXTUREM;
      }
   }

   protected void applyRotations(EntityMes entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }

   protected void renderLivingLabel(EntityMes entityIn, String str, double x, double y, double z, int maxDistance) {
      double d0 = entityIn.getDistanceSq(this.renderManager.renderViewEntity);
      if (d0 <= (double)(maxDistance * maxDistance)) {
         boolean flag = entityIn.isSneaking();
         float f = this.renderManager.playerViewY;
         float f1 = this.renderManager.playerViewX;
         boolean flag1 = this.renderManager.options.thirdPersonView == 2;
         float f2 = entityIn.height + 0.5F - (flag ? 0.25F : 0.0F);
         int i = "deadmau5".equals(str) ? -10 : 0;
         EntityRenderer.drawNameplate(this.getFontRendererFromRenderManager(), str, (float)x, (float)y + f2, (float)z, i, f, f1, flag1, flag);
      }

   }
}
