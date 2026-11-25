package artifacts.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelPanicNecklace extends ModelBase {
   public ModelRenderer bipedBody;
   public ModelRenderer gem2;
   public ModelRenderer gem1;
   public ModelRenderer gem3;
   public ModelRenderer gem4;

   public ModelPanicNecklace() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.bipedBody = new ModelRenderer(this, 0, 0);
      this.bipedBody.addBox(-8.5F, 0.0F, -4.0F, 17, 24, 8, 0.0F);
      this.gem1 = new ModelRenderer(this, 50, 0);
      this.gem1.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
      this.gem2 = new ModelRenderer(this, 56, 0);
      this.gem2.addBox(3.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
      this.gem3 = new ModelRenderer(this, 50, 3);
      this.gem3.addBox(1.0F, 1.0F, 0.0F, 3, 2, 1, 0.0F);
      this.gem4 = new ModelRenderer(this, 58, 4);
      this.gem4.addBox(2.0F, 3.0F, 0.0F, 1, 1, 1, 0.0F);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      GlStateManager.translate(0.0F, -0.02F, 0.0F);
      GlStateManager.scale(1.1666666F, 1.1666666F, 1.1666666F);
      GlStateManager.scale(0.5F, 0.5F, 0.5F);
      this.bipedBody.render(f5);
      GlStateManager.translate(-0.15625D, 0.375D, -0.28125D);
      this.gem4.render(f5);
      this.gem3.render(f5);
      this.gem2.render(f5);
      this.gem1.render(f5);
   }
}
