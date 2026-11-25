package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelStonePlayer;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerStonePlayerEntityCrack;
import com.github.alexthe666.iceandfire.entity.EntityStoneStatue;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.Profile;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;

public class RenderStoneStatue extends RenderLiving<EntityStoneStatue> {
   private static final ModelStonePlayer MODEL = new ModelStonePlayer(0.0F, false);
   private static final ModelStonePlayer MODEL_SLIM = new ModelStonePlayer(0.0F, true);

   public RenderStoneStatue(RenderManager renderManager) {
      super(renderManager, MODEL, 0.5F);
      this.layerRenderers.add(new LayerStonePlayerEntityCrack(this));
      LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
         protected void initArmor() {
            this.modelLeggings = new ModelStonePlayer(0.5F, true);
            this.modelArmor = new ModelStonePlayer(1.0F, true);
         }
      };
      this.addLayer(layerbipedarmor);
   }

   public ModelBase getMainModel() {
      return MODEL;
   }

   protected void renderModel(EntityStoneStatue entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      boolean flag = !entity.isInvisible() || this.renderOutlines;
      boolean flag1 = !flag && !entity.isInvisibleToPlayer(Minecraft.getMinecraft().player);
      if (flag || flag1) {
         if (!this.bindEntityTexture(entity)) {
            return;
         }

         if (flag1) {
            GlStateManager.enableBlendProfile(Profile.TRANSPARENT_MODEL);
         }

         if (entity.smallArms) {
            MODEL_SLIM.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
         } else {
            MODEL.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
         }

         if (flag1) {
            GlStateManager.disableBlendProfile(Profile.TRANSPARENT_MODEL);
         }
      }

   }

   public String getStoneType(ModelBase model, int size) {
      int x = model.textureWidth;
      int y = model.textureHeight;
      int sizeX = Math.min(128, x * size);
      int sizeY = Math.min(128, y * size);
      String str = "iceandfire:textures/models/gorgon/stone" + sizeX + "x" + sizeY + ".png";
      return sizeX <= 16 && sizeY <= 16 ? "textures/blocks/stone.png" : str;
   }

   @Nullable
   protected ResourceLocation getEntityTexture(EntityStoneStatue entity) {
      return new ResourceLocation(this.getStoneType(entity.smallArms ? MODEL : MODEL_SLIM, 1));
   }
}
