package vazkii.quark.world.client.model;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import vazkii.arl.item.ModelModArmor;

public class ModelArchaeologistHat extends ModelModArmor {
   public final ModelRenderer hat;

   public ModelArchaeologistHat() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.hat = new ModelRenderer(this);
      this.hat.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.hat.cubeList.add(new ModelBox(this.hat, 0, 0, -4.0F, -10.0F, -4.0F, 8, 10, 8, 0.6F, false));
      this.hat.cubeList.add(new ModelBox(this.hat, 0, 18, -6.0F, -6.0F, -6.0F, 12, 1, 12, 0.0F, false));
   }

   public void setModelParts() {
      this.bipedHead.showModel = false;
      this.bipedHeadwear = this.hat;
   }
}
