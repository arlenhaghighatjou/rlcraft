package com.dhanantry.scapeandrunparasites.client.renderer.entity.misc;

import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelBombHost;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelBombJinjo;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelBombOmboo;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import net.minecraft.client.model.ModelBase;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderBomb extends Render<EntityBomb> {
   protected ModelBase modelO = new ModelBombOmboo();
   protected ModelBase modelH = new ModelBombHost();
   protected ModelBase modelJ = new ModelBombJinjo();
   public static final ResourceLocation TEXTUREO = new ResourceLocation("srparasites:textures/entity/monster/bombo.png");
   public static final ResourceLocation TEXTUREH = new ResourceLocation("srparasites:textures/entity/monster/bombh.png");
   public static final ResourceLocation TEXTUREJ = new ResourceLocation("srparasites:textures/entity/monster/bombj.png");

   public RenderBomb(RenderManager p_i47208_1_) {
      super(p_i47208_1_);
      this.shadowSize = 0.5F;
   }

   protected ResourceLocation getEntityTexture(EntityBomb entity) {
      switch(entity.getSkin()) {
      case 0:
         return TEXTUREO;
      case 1:
         return TEXTUREH;
      case 2:
         return TEXTUREJ;
      case 3:
         return TEXTUREJ;
      default:
         return TEXTUREO;
      }
   }

   public void doRender(EntityBomb entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      switch(entity.getSkin()) {
      case 0:
         GlStateManager.translate((float)x, (float)y + 1.5F, (float)z);
         this.bindTexture(TEXTUREO);
         break;
      case 1:
         GlStateManager.translate((float)x, (float)y + 1.5F, (float)z);
         this.bindTexture(TEXTUREH);
         break;
      case 2:
         GlStateManager.translate((float)x, (float)y + 1.5F, (float)z);
         this.bindTexture(TEXTUREJ);
         break;
      case 3:
         GlStateManager.translate((float)x, (float)y + 1.5F, (float)z);
         this.bindTexture(TEXTUREJ);
      }

      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(entity));
      }

      switch(entity.getSkin()) {
      case 0:
         this.modelO.render(entity, 0.0F, 0.0F, (float)entity.ticksExisted, entity.rotationYaw, entity.rotationPitch, 0.0625F);
         break;
      case 1:
         this.modelH.render(entity, 0.0F, 0.0F, (float)entity.ticksExisted, entity.rotationYaw, entity.rotationPitch, 0.0625F);
         break;
      case 2:
         this.modelJ.render(entity, 0.0F, 0.0F, (float)entity.ticksExisted, entity.rotationYaw, entity.rotationPitch, 0.0625F);
         break;
      case 3:
         this.modelJ.render(entity, 0.0F, 0.0F, (float)entity.ticksExisted, entity.rotationYaw, entity.rotationPitch, 0.0625F);
      }

      if (this.renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   protected float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
      float f;
      for(f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F) {
         ;
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return prevYawOffset + partialTicks * f;
   }
}
