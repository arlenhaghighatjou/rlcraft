package artifacts.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;

public class ModelUltimatePendant extends ModelBase {
   protected ModelRenderer bipedBody;
   protected ModelRenderer gem;

   public ModelUltimatePendant() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.bipedBody = new ModelRenderer(this, 0, 0);
      this.bipedBody.addBox(-8.5F, 0.0F, -4.0F, 17, 24, 8, 0.0F);
      this.gem = new ModelRenderer(this, 50, 0);
      this.gem.addBox(1.0F, -1.0F, 0.0F, 3, 3, 1, 0.0F);
   }

   public void render(Entity entity, float f, float f1, float ageInTicks, float f3, float f4, float scale) {
      GlStateManager.translate(0.0F, -0.02F, 0.0F);
      GlStateManager.scale(1.1666666F, 1.1666666F, 1.1666666F);
      GlStateManager.scale(0.5F, 0.5F, 0.5F);
      this.bipedBody.render(scale);
      GlStateManager.translate(-0.15625D, 0.375D, -0.28125D);
      float lastLightmapX = OpenGlHelper.lastBrightnessX;
      float lastLightmapY = OpenGlHelper.lastBrightnessY;
      float light = 2.0F * (ageInTicks % 100.0F);
      if (ageInTicks % 200.0F < 100.0F) {
         light = 200.0F - light;
      }

      light = light + 40.0F;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light, light);
      this.gem.render(scale);
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastLightmapX, lastLightmapY);
   }
}
