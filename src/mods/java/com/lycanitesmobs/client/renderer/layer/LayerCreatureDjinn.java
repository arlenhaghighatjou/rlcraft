package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerCreatureDjinn extends LayerCreatureBase {
   public LayerCreatureDjinn(RenderCreature renderer) {
      super(renderer);
   }

   public boolean canRenderPart(String partName, BaseCreatureEntity entity, boolean trophy) {
      return partName.contains("ribbon");
   }

   public ResourceLocation getLayerTexture(BaseCreatureEntity entity) {
      String textureName = entity.getTextureName();
      if (entity.getVariant() != null) {
         textureName = textureName + "_" + entity.getVariant().color;
      }

      textureName = textureName + "_ribbon";
      if (AssetManager.getTexture(textureName) == null) {
         AssetManager.addTexture(textureName, entity.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
      }

      return AssetManager.getTexture(textureName);
   }

   public Vector2f getTextureOffset(String partName, BaseCreatureEntity entity, boolean trophy, float loop) {
      return new Vector2f(-loop * 25.0F, 0.0F);
   }

   public Vector4f getPartColor(String partName, BaseCreatureEntity entity, boolean trophy) {
      return new Vector4f(1.0F, 1.0F, 1.0F, 0.75F);
   }

   public void onRenderStart(Entity entity, boolean trophy) {
   }

   public void onRenderFinish(Entity entity, boolean trophy) {
   }
}
