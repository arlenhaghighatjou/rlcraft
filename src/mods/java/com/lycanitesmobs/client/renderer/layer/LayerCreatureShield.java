package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import javax.vecmath.Vector4f;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerCreatureShield extends LayerCreatureBase {
   public LayerCreatureShield(RenderCreature renderer) {
      super(renderer);
   }

   public boolean canRenderLayer(BaseCreatureEntity entity, float scale) {
      return !super.canRenderLayer(entity, scale) ? false : entity.isBlocking();
   }

   public boolean canRenderPart(String partName, BaseCreatureEntity entity, boolean trophy) {
      return "shield".equals(partName);
   }

   public Vector4f getPartColor(String partName, BaseCreatureEntity entity, boolean trophy) {
      return new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void onRenderStart(Entity entity, boolean trophy) {
      int i = 15728880;
      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
      GlStateManager.enableCull();
   }

   public void onRenderFinish(Entity entity, boolean trophy) {
      GlStateManager.disableCull();
   }
}
