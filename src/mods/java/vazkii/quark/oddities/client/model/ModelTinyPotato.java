package vazkii.quark.oddities.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelTinyPotato extends ModelBase {
   private final ModelRenderer potato;

   public ModelTinyPotato() {
      this.textureWidth = 16;
      this.textureHeight = 16;
      this.potato = new ModelRenderer(this, 0, 0);
      this.potato.addBox(0.0F, 0.0F, 0.0F, 4, 6, 4);
      this.potato.setRotationPoint(-2.0F, 18.0F, -2.0F);
      this.potato.setTextureSize(64, 32);
   }

   public void render() {
      this.potato.render(0.0625F);
   }
}
