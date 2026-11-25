package artifacts.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelAmulet extends ModelBase {
   protected ModelRenderer boxChain;
   protected ModelRenderer boxGem;

   public ModelAmulet() {
      this.textureWidth = 32;
      this.textureHeight = 16;
      this.boxChain = new ModelRenderer(this, 0, 0);
      this.boxChain.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
      this.boxGem = new ModelRenderer(this, 24, 0);
      this.boxGem.addBox(-0.5F, 3.0F, -4.5F, 1, 1, 1, 0.0F);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
      GlStateManager.translate(0.0D, -0.02D, 0.0D);
      GlStateManager.scale(1.1666666F, 1.1666666F, 1.1666666F);
      this.boxChain.render(scale);
      GlStateManager.scale(1.0F, 1.0F, 0.5F);
      this.boxGem.render(scale);
   }
}
