package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import javax.vecmath.Vector4f;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerCreatureGlow extends LayerCreatureBase {
   public LayerCreatureGlow(RenderCreature renderer) {
      super(renderer);
   }

   public Vector4f getPartColor(String partName, BaseCreatureEntity entity, boolean trophy) {
      return new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public ResourceLocation getLayerTexture(BaseCreatureEntity entity) {
      String textureName = entity.getTextureName();
      if (entity.getVariant() != null) {
         textureName = textureName + "_" + entity.getVariant().color;
      }

      textureName = textureName + "_glow";
      if (AssetManager.getTexture(textureName) == null) {
         AssetManager.addTexture(textureName, entity.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
      }

      return AssetManager.getTexture(textureName);
   }

   public void onRenderStart(Entity entity, boolean trophy) {
      int i = 15728880;
      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
   }

   public void onRenderFinish(Entity entity, boolean trophy) {
      int i = entity.getBrightnessForRender();
      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
   }
}
