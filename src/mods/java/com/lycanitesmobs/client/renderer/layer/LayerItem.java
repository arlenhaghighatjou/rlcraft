package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.renderer.IItemModelRenderer;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerItem {
   public IItemModelRenderer renderer;
   public String name;
   public String textureSuffix;
   public boolean glow = false;
   public int blending = 0;
   public Vector2f scrollSpeed;
   public Vector4f colorFadeSpeed;

   public LayerItem(IItemModelRenderer renderer, String name) {
      this.renderer = renderer;
      this.name = name;
   }

   public Vector4f getPartColor(String partName, ItemStack itemStack, float loop) {
      if (this.colorFadeSpeed != null) {
         float red = 1.0F;
         if (this.colorFadeSpeed.getX() != 0.0F) {
            red = loop % this.colorFadeSpeed.getX() / this.colorFadeSpeed.getX() * 2.0F;
         }

         if (red > 1.0F) {
            red = -(red - 1.0F);
         }

         float green = 1.0F;
         if (this.colorFadeSpeed.getY() != 0.0F) {
            green = loop % this.colorFadeSpeed.getY() / this.colorFadeSpeed.getY() * 2.0F;
         }

         if (green > 1.0F) {
            green = -(green - 1.0F);
         }

         float blue = 1.0F;
         if (this.colorFadeSpeed.getZ() != 0.0F) {
            blue = loop % this.colorFadeSpeed.getZ() / this.colorFadeSpeed.getZ() * 2.0F;
         }

         if (blue > 1.0F) {
            blue = -(blue - 1.0F);
         }

         float alpha = 1.0F;
         if (this.colorFadeSpeed.getW() != 0.0F) {
            alpha = loop % this.colorFadeSpeed.getW() / this.colorFadeSpeed.getW() * 2.0F;
         }

         if (alpha > 1.0F) {
            alpha = -(alpha - 1.0F);
         }

         return new Vector4f(red, green, blue, alpha);
      } else {
         return new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   public ResourceLocation getLayerTexture(BaseCreatureEntity entity) {
      return entity.getTexture(this.textureSuffix);
   }

   public void onRenderStart(ItemStack itemStack) {
      if (this.glow) {
         GlStateManager.disableLighting();
         int i = 15728880;
         int j = i % 65536;
         int k = i / 65536;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
      }

      if (this.blending == LayerItem.BLEND.ADD.id) {
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
      } else if (this.blending == LayerItem.BLEND.SUB.id) {
         GlStateManager.blendFunc(SourceFactor.DST_COLOR, DestFactor.ONE_MINUS_SRC_ALPHA);
      }

   }

   public void onRenderFinish(ItemStack itemStack) {
      if (this.glow) {
         GlStateManager.enableLighting();
      }

   }

   public Vector2f getTextureOffset(String partName, ItemStack itemStack, float loop) {
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
