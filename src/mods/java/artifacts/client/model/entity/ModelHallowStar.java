package artifacts.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelHallowStar extends ModelBase {
   public ModelRenderer box1;
   public ModelRenderer box2;

   public ModelHallowStar() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.box1 = new ModelRenderer(this, 0, 0);
      this.box2 = new ModelRenderer(this, 0, 16);
      this.box1.addBox(-1.5F, -4.0F, -4.0F, 2, 8, 8);
      this.box2.addBox(-0.5F, -4.0F, -4.0F, 2, 8, 8);
      this.box1.rotateAngleX = 0.7853982F;
   }

   public void render(Entity entity, float f, float f1, float ageInTicks, float f3, float f4, float scale) {
      GlStateManager.rotate(ageInTicks / 2.5F, 0.7071F, 0.0F, 0.7071F);
      GlStateManager.rotate(ageInTicks, 0.0F, 1.0F, 0.0F);
      this.box1.render(scale);
      this.box2.render(scale);
   }
}
