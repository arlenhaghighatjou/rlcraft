package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerCreatureFire extends LayerCreatureBase {
   public LayerCreatureFire(RenderCreature renderer) {
      super(renderer);
   }

   public boolean canRenderLayer(BaseCreatureEntity entity, float scale) {
      return !super.canRenderLayer(entity, scale) ? false : entity.isAttackOnCooldown();
   }

   public ResourceLocation getLayerTexture(BaseCreatureEntity entity) {
      return entity.getSubTexture("fire");
   }
}
