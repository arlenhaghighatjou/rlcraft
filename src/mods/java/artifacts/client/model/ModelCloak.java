package artifacts.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCloak extends ModelBase {
   private final ModelRenderer cloak;
   private final ModelRenderer hoodUp;
   private final ModelRenderer hoodDown;

   public ModelCloak() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.cloak = new ModelRenderer(this);
      this.cloak.setRotationPoint(0.0F, 0.4F, 2.96F);
      this.setRotationAngle(this.cloak, 0.0436F, 0.0F, 0.0F);
      ModelRenderer neck = new ModelRenderer(this);
      neck.setRotationPoint(0.0F, 23.6F, -8.96F);
      this.cloak.addChild(neck);
      this.setRotationAngle(neck, -0.0436F, 0.0F, 0.0F);
      neck.cubeList.add(new ModelBox(neck, 32, 6, -5.5F, -24.8F, 2.5F, 11, 4, 5, 0.0F, false));
      ModelRenderer cloak1 = new ModelRenderer(this);
      cloak1.setRotationPoint(0.0F, 10.0F, -0.18F);
      this.cloak.addChild(cloak1);
      this.setRotationAngle(cloak1, 0.0F, -0.0436F, 0.1309F);
      cloak1.cubeList.add(new ModelBox(cloak1, 14, 0, -6.6F, -9.6F, -0.38F, 6, 20, 1, 0.0F, false));
      ModelRenderer cloak2 = new ModelRenderer(this);
      cloak2.setRotationPoint(3.6F, 9.9F, -0.06F);
      this.cloak.addChild(cloak2);
      this.setRotationAngle(cloak2, 0.0F, 0.0436F, -0.1309F);
      cloak2.cubeList.add(new ModelBox(cloak2, 13, 21, -3.0F, -10.0F, -0.5F, 6, 20, 1, 0.0F, false));
      ModelRenderer cloak3 = new ModelRenderer(this);
      cloak3.setRotationPoint(0.0F, 0.1F, -0.31F);
      this.cloak.addChild(cloak3);
      this.setRotationAngle(cloak3, 0.0436F, 0.0F, 0.0F);
      cloak3.cubeList.add(new ModelBox(cloak3, 0, 0, -2.5F, 0.0F, -1.0F, 5, 20, 2, 0.0F, false));
      this.hoodUp = new ModelRenderer(this);
      this.hoodUp.setRotationPoint(0.0F, 24.0F, 0.0F);
      this.hoodUp.cubeList.add(new ModelBox(this.hoodUp, 0, 44, -5.0F, -33.5F, -5.0F, 10, 10, 10, 0.0F, false));
      this.hoodDown = new ModelRenderer(this);
      this.hoodDown.setRotationPoint(0.0F, 24.0F, 0.0F);
      this.hoodDown.cubeList.add(new ModelBox(this.hoodDown, 38, 0, -5.0F, -24.5F, 3.0F, 10, 3, 3, 0.0F, false));
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.renderCloak(entity, f, f1, f2, f3, f4, f5, false);
   }

   public void renderCloak(Entity entity, float f, float f1, float f2, float f3, float f4, float f5, boolean renderHood) {
      this.cloak.render(f5);
      if (!renderHood) {
         this.hoodDown.render(f5);
      }

   }

   public void renderHood(Entity entity, float f, float f1, float f2, float f3, float f4, float f5, boolean renderHood) {
      if (renderHood) {
         this.hoodUp.render(f5);
      }

   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
   }
}
