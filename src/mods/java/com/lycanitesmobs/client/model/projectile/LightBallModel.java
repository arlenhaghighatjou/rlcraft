package com.lycanitesmobs.client.model.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelCreatureObj;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureEffect;
import javax.vecmath.Vector4f;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LightBallModel extends ModelCreatureObj {
   LayerCreatureEffect ballGlowLayer;

   public LightBallModel() {
      this(1.0F);
   }

   public LightBallModel(float shadowSize) {
      this.initModel("lightball", LycanitesMobs.modInfo, "projectile/lightball");
   }

   public void addCustomLayers(RenderCreature renderer) {
      super.addCustomLayers(renderer);
      this.ballGlowLayer = new LayerCreatureEffect(renderer, "", true, LayerCreatureEffect.BLEND.ADD.id, true);
      renderer.addLayer(this.ballGlowLayer);
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
      this.rotate(loop * 8.0F, 0.0F, 0.0F);
   }

   public boolean canRenderPart(String partName, Entity entity, LayerCreatureBase layer, boolean trophy) {
      if (!partName.equals("ball02") && !partName.equals("ball03")) {
         return layer == null;
      } else {
         return layer == this.ballGlowLayer;
      }
   }

   public Vector4f getPartColor(String partName, Entity entity, LayerCreatureBase layer, boolean trophy, float loop) {
      float glowSpeed = 40.0F;
      float glow = loop * glowSpeed % 360.0F;
      float color = (float)Math.cos(Math.toRadians((double)glow)) * 0.1F + 0.9F;
      return new Vector4f(color, color, color, 1.0F);
   }

   public void onRenderStart(LayerCreatureBase layer, Entity entity, boolean renderAsTrophy) {
      super.onRenderStart(layer, entity, renderAsTrophy);
      int i = 15728880;
      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
      GlStateManager.disableLighting();
   }

   public void onRenderFinish(LayerCreatureBase layer, Entity entity, boolean renderAsTrophy) {
      super.onRenderFinish(layer, entity, renderAsTrophy);
      int i = entity.getBrightnessForRender();
      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
      GlStateManager.enableLighting();
   }
}
