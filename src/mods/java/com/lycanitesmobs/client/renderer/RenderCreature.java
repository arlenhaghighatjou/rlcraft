package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.model.ModelCustom;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureManager;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCreature extends RenderLiving<BaseCreatureEntity> {
   public boolean multipass = true;
   protected ModelBase defaultModel;
   private static final DynamicTexture textureBrightness = new DynamicTexture(16, 16);
   public static final float[][] colorTable = new float[][]{{1.0F, 1.0F, 1.0F}, {0.85F, 0.5F, 0.2F}, {0.7F, 0.3F, 0.85F}, {0.4F, 0.6F, 0.85F}, {0.9F, 0.9F, 0.2F}, {0.5F, 0.8F, 0.1F}, {0.95F, 0.5F, 0.65F}, {0.3F, 0.3F, 0.3F}, {0.6F, 0.6F, 0.6F}, {0.3F, 0.5F, 0.6F}, {0.5F, 0.25F, 0.7F}, {0.2F, 0.3F, 0.7F}, {0.4F, 0.3F, 0.2F}, {0.4F, 0.5F, 0.2F}, {0.6F, 0.2F, 0.2F}, {0.1F, 0.1F, 0.1F}};

   public RenderCreature(String entityID, RenderManager renderManager, float shadowSize) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
      super(renderManager, AssetManager.getCreatureModel(CreatureManager.getInstance().getCreature(entityID)), shadowSize);
      this.defaultModel = this.mainModel;
      if (this.mainModel instanceof ModelCustom) {
         ModelCustom modelCustom = (ModelCustom)this.mainModel;
         modelCustom.addCustomLayers(this);
      }

      this.multipass = LycanitesMobs.config.getBool("Client", "Model Multipass", this.multipass, "Set to false to disable multipass rendering. This renders model layers twice so that they can show each over through alpha textures, disable for performance on low end systems.");
   }

   public boolean isMultipass() {
      return false;
   }

   public void doRender(BaseCreatureEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
      try {
         this.mainModel = AssetManager.getCreatureModel(entity);
      } catch (Exception var11) {
         var11.printStackTrace();
      }

      if (this.mainModel == null) {
         this.mainModel = this.defaultModel;
      }

      if (this.mainModel != null) {
         super.doRender(entity, x, y, z, entityYaw, partialTicks);
      }
   }

   public void renderMultipass(BaseCreatureEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
   }

   protected void renderModel(BaseCreatureEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
   }

   protected void renderLayers(BaseCreatureEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      super.renderLayers(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleFactor);
   }

   protected boolean bindEntityTexture(BaseCreatureEntity entity) {
      ResourceLocation texture = this.getEntityTexture(entity);
      if (texture == null) {
         return false;
      } else {
         this.bindTexture(texture);
         return true;
      }
   }

   protected ResourceLocation getEntityTexture(BaseCreatureEntity entity) {
      return entity instanceof BaseCreatureEntity ? entity.getTexture() : null;
   }

   protected void bindEquipmentTexture(Entity entity, String equipmentName) {
      this.bindTexture(this.getEquipmentTexture(entity, equipmentName));
   }

   protected ResourceLocation getEquipmentTexture(Entity entity, String equipmentName) {
      return entity instanceof BaseCreatureEntity ? ((BaseCreatureEntity)entity).getEquipmentTexture(equipmentName) : null;
   }

   protected void preRenderCallback(BaseCreatureEntity entity, float particleTickTime) {
   }

   protected boolean canRenderName(BaseCreatureEntity entity) {
      if (!Minecraft.isGuiEnabled()) {
         return false;
      } else if (entity == this.renderManager.renderViewEntity) {
         return false;
      } else if (entity.isInvisibleToPlayer(Minecraft.getMinecraft().player)) {
         return false;
      } else if (entity.getControllingPassenger() != null) {
         return false;
      } else if (entity.getAlwaysRenderNameTagForRender()) {
         if (entity instanceof TameableCreatureEntity && entity.isTamed()) {
            return entity == this.renderManager.pointedEntity;
         } else {
            return true;
         }
      } else {
         return entity.hasCustomName() && entity == this.renderManager.pointedEntity;
      }
   }

   public float interpolateRotation(float par1, float par2, float par3) {
      float f3;
      for(f3 = par2 - par1; f3 < -180.0F; f3 += 360.0F) {
         ;
      }

      while(f3 >= 180.0F) {
         f3 -= 360.0F;
      }

      return par1 + par3 * f3;
   }
}
