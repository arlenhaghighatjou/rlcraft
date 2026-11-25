package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityThresher;
import javax.vecmath.Vector4f;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerCreatureThresher extends LayerCreatureBase {
   public LayerCreatureThresher(RenderCreature renderer) {
      super(renderer);
   }

   public boolean canRenderLayer(BaseCreatureEntity entity, float scale) {
      if (entity instanceof EntityThresher) {
         EntityThresher entityThresher = (EntityThresher)entity;
         return entityThresher.canWhirlpool();
      } else {
         return false;
      }
   }

   public boolean canRenderPart(String partName, BaseCreatureEntity entity, boolean trophy) {
      return "effect".equals(partName);
   }

   public Vector4f getPartColor(String partName, BaseCreatureEntity entity, boolean trophy) {
      return new Vector4f(1.0F, 1.0F, 1.0F, 0.5F);
   }

   public ResourceLocation getLayerTexture(BaseCreatureEntity entity) {
      return entity.getSubTexture("effect");
   }

   public void onRenderStart(Entity entity, boolean trophy) {
   }

   public void onRenderFinish(Entity entity, boolean trophy) {
   }
}
