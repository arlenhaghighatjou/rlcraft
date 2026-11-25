package vazkii.quark.oddities.client.model;

import net.minecraft.client.model.ModelRenderer;
import vazkii.arl.item.ModelModArmor;

public class ModelBackpack extends ModelModArmor {
   private final ModelRenderer straps = new ModelRenderer(this, 24, 0);
   private final ModelRenderer backpack;
   private final ModelRenderer fitting;
   private final ModelRenderer base = new ModelRenderer(this);

   public ModelBackpack() {
      this.straps.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.straps.addBox(-4.0F, 0.05F, -3.0F, 8, 8, 5, 0.0F);
      this.fitting = new ModelRenderer(this, 50, 0);
      this.fitting.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.fitting.addBox(-1.0F, 3.0F, 6.0F, 2, 3, 1, 0.0F);
      this.backpack = new ModelRenderer(this, 0, 0);
      this.backpack.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.backpack.addBox(-4.0F, 0.0F, 2.0F, 8, 10, 4, 0.0F);
      this.base.addChild(this.straps);
      this.base.addChild(this.backpack);
      this.base.addChild(this.fitting);
   }

   public void setModelParts() {
      this.bipedBody = this.base;
   }
}
