package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelHippogryph;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class RenderHippogryph extends RenderLiving<EntityHippogryph> {
   public RenderHippogryph(RenderManager renderManager) {
      super(renderManager, new ModelHippogryph(), 0.8F);
      this.layerRenderers.add(new RenderHippogryph.LayerHippogriffSaddle(this));
      this.layerRenderers.add(new RenderHippogryph.LayerHippogriffBridle(this));
      this.layerRenderers.add(new RenderHippogryph.LayerHippogriffChest(this));
      this.layerRenderers.add(new RenderHippogryph.LayerHippogriffArmor(this));
   }

   protected void preRenderCallback(EntityHippogryph entity, float partialTickTime) {
      GL11.glScalef(1.2F, 1.2F, 1.2F);
   }

   @Nullable
   protected ResourceLocation getEntityTexture(EntityHippogryph entity) {
      return entity.isBlinking() ? entity.getEnumVariant().TEXTURE_BLINK : entity.getEnumVariant().TEXTURE;
   }

   @SideOnly(Side.CLIENT)
   private class LayerHippogriffArmor implements LayerRenderer {
      private final RenderHippogryph renderer;
      private final ResourceLocation TEXTURE_DIAMOND = new ResourceLocation("iceandfire:textures/models/hippogryph/armor_diamond.png");
      private final ResourceLocation TEXTURE_GOLD = new ResourceLocation("iceandfire:textures/models/hippogryph/armor_gold.png");
      private final ResourceLocation TEXTURE_IRON = new ResourceLocation("iceandfire:textures/models/hippogryph/armor_iron.png");

      public LayerHippogriffArmor(RenderHippogryph renderer) {
         this.renderer = renderer;
      }

      public void doRenderLayer(EntityHippogryph entity, float f, float f1, float i, float f2, float f3, float f4, float f5) {
         if (entity.getArmor() != 0) {
            switch(entity.getArmor()) {
            case 1:
               this.renderer.bindTexture(this.TEXTURE_IRON);
               break;
            case 2:
               this.renderer.bindTexture(this.TEXTURE_GOLD);
               break;
            case 3:
               this.renderer.bindTexture(this.TEXTURE_DIAMOND);
            }

            this.renderer.getMainModel().render(entity, f, f1, f2, f3, f4, f5);
         }

      }

      public boolean shouldCombineTextures() {
         return false;
      }

      public void doRenderLayer(EntityLivingBase entity, float f, float f1, float f2, float f3, float f4, float f5, float f6) {
         this.doRenderLayer((EntityHippogryph)entity, f, f1, f2, f3, f4, f5, f6);
      }
   }

   @SideOnly(Side.CLIENT)
   private class LayerHippogriffBridle implements LayerRenderer {
      private final RenderHippogryph renderer;
      private final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/models/hippogryph/bridle.png");

      public LayerHippogriffBridle(RenderHippogryph renderer) {
         this.renderer = renderer;
      }

      public void doRenderLayer(EntityHippogryph entity, float f, float f1, float i, float f2, float f3, float f4, float f5) {
         if (entity.isSaddled() && entity.getControllingPassenger() != null) {
            this.renderer.bindTexture(this.TEXTURE);
            this.renderer.getMainModel().render(entity, f, f1, f2, f3, f4, f5);
         }

      }

      public boolean shouldCombineTextures() {
         return false;
      }

      public void doRenderLayer(EntityLivingBase entity, float f, float f1, float f2, float f3, float f4, float f5, float f6) {
         this.doRenderLayer((EntityHippogryph)entity, f, f1, f2, f3, f4, f5, f6);
      }
   }

   @SideOnly(Side.CLIENT)
   private class LayerHippogriffChest implements LayerRenderer {
      private final RenderHippogryph renderer;
      private final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/models/hippogryph/chest.png");

      public LayerHippogriffChest(RenderHippogryph renderer) {
         this.renderer = renderer;
      }

      public void doRenderLayer(EntityHippogryph entity, float f, float f1, float i, float f2, float f3, float f4, float f5) {
         if (entity.isChested()) {
            this.renderer.bindTexture(this.TEXTURE);
            this.renderer.getMainModel().render(entity, f, f1, f2, f3, f4, f5);
         }

      }

      public boolean shouldCombineTextures() {
         return false;
      }

      public void doRenderLayer(EntityLivingBase entity, float f, float f1, float f2, float f3, float f4, float f5, float f6) {
         this.doRenderLayer((EntityHippogryph)entity, f, f1, f2, f3, f4, f5, f6);
      }
   }

   @SideOnly(Side.CLIENT)
   private class LayerHippogriffSaddle implements LayerRenderer {
      private final RenderHippogryph renderer;
      private final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/models/hippogryph/saddle.png");

      public LayerHippogriffSaddle(RenderHippogryph renderer) {
         this.renderer = renderer;
      }

      public void doRenderLayer(EntityHippogryph entity, float f, float f1, float i, float f2, float f3, float f4, float f5) {
         if (entity.isSaddled()) {
            this.renderer.bindTexture(this.TEXTURE);
            this.renderer.getMainModel().render(entity, f, f1, f2, f3, f4, f5);
         }

      }

      public boolean shouldCombineTextures() {
         return false;
      }

      public void doRenderLayer(EntityLivingBase entity, float f, float f1, float f2, float f3, float f4, float f5, float f6) {
         this.doRenderLayer((EntityHippogryph)entity, f, f1, f2, f3, f4, f5, f6);
      }
   }
}
