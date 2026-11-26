package com.lycanitesmobs.client.renderer.layer;

import net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerCreatureEffect extends LayerCreatureBase {
   public String textureSuffix;
   public boolean subspecies = true;
   public boolean glow = false;
   public int blending = 0;
   public Vector2f scrollSpeed;

   public LayerCreatureEffect(RenderCreature renderer, String textureSuffix) {
      super(renderer);
      this.name = textureSuffix;
      this.textureSuffix = textureSuffix;
   }

   public LayerCreatureEffect(RenderCreature renderer, String textureSuffix, boolean glow, int blending, boolean subspecies) {
      super(renderer);
      this.name = textureSuffix;
      this.textureSuffix = textureSuffix;
      this.glow = glow;
      this.blending = blending;
      this.subspecies = subspecies;
   }

   public LayerCreatureEffect(RenderCreature renderer, String name, String textureSuffix, boolean glow, int blending, boolean subspecies) {
      super(renderer);
      this.name = name;
      this.textureSuffix = textureSuffix;
      this.glow = glow;
      this.blending = blending;
      this.subspecies = subspecies;
   }

   public Vector4f getPartColor(String partName, BaseCreatureEntity entity, boolean trophy) {
      return new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public ResourceLocation getLayerTexture(BaseCreatureEntity entity) {
      return this.subspecies ? entity.getTexture(this.textureSuffix) : entity.getSubTexture(this.textureSuffix);
   }

   public void onRenderStart(Entity entity, boolean trophy) {
      int i = entity.getBrightnessForRender();
      if (this.glow) {
         GlStateManager.disableLighting();
         i = 15728880;
      }

      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
      if (this.blending == LayerCreatureEffect.BLEND.ADD.id) {
         GlStateManager.blendFunc(RealOpenGLEnums.GL_SRC_ALPHA, RealOpenGLEnums.GL_ONE);
      } else if (this.blending == LayerCreatureEffect.BLEND.SUB.id) {
         GlStateManager.blendFunc(RealOpenGLEnums.GL_DST_COLOR, RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA);
      }

   }

   public void onRenderFinish(Entity entity, boolean trophy) {
      if (this.glow) {
         GlStateManager.enableLighting();
      }

   }

   public Vector2f getTextureOffset(String partName, BaseCreatureEntity entity, boolean trophy, float loop) {
      if (this.scrollSpeed == null) {
         this.scrollSpeed = new Vector2f(0.0F, 0.0F);
      }

      return new Vector2f(loop * this.scrollSpeed.x, loop * this.scrollSpeed.y);
   }

   public static enum BLEND {
      NORMAL(0),
      ADD(1),
      SUB(2);

      public final int id;

      private BLEND(int value) {
         this.id = value;
      }

      public int getValue() {
         return this.id;
      }
   }
}
