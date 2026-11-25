package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.model.ModelCustom;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerCreatureBase implements LayerRenderer<BaseCreatureEntity> {
   public RenderCreature renderer;
   public String name;

   public LayerCreatureBase(RenderCreature renderer) {
      this.renderer = renderer;
      this.name = "Layer";
   }

   public void doRenderLayer(BaseCreatureEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (this.canRenderLayer(entity, scale)) {
         if (this.renderer.getMainModel() instanceof ModelCustom) {
            ResourceLocation layerTexture = this.getLayerTexture(entity);
            if (layerTexture != null) {
               this.renderer.bindTexture(layerTexture);
            }

            ((ModelCustom)this.renderer.getMainModel()).render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, this, true);
         }

      }
   }

   public boolean canRenderLayer(BaseCreatureEntity entity, float scale) {
      if (entity == null) {
         return false;
      } else {
         return !entity.isInvisible() || !entity.isInvisibleToPlayer(Minecraft.getMinecraft().player);
      }
   }

   public ResourceLocation getLayerTexture(BaseCreatureEntity entity) {
      return null;
   }

   public boolean canRenderPart(String partName, BaseCreatureEntity entity, boolean trophy) {
      if (this.renderer.getMainModel() instanceof ModelCustom) {
         ((ModelCustom)this.renderer.getMainModel()).canBaseRenderPart(partName, entity, trophy);
      }

      return true;
   }

   public Vector4f getPartColor(String partName, BaseCreatureEntity entity, boolean trophy) {
      return new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public Vector2f getTextureOffset(String partName, BaseCreatureEntity entity, boolean trophy, float loop) {
      return new Vector2f(0.0F, 0.0F);
   }

   public void onRenderStart(Entity entity, boolean trophy) {
   }

   public void onRenderFinish(Entity entity, boolean trophy) {
   }

   public boolean shouldCombineTextures() {
      return true;
   }
}
