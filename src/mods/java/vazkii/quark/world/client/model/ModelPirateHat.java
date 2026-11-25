package vazkii.quark.world.client.model;

import net.minecraft.client.model.ModelRenderer;
import vazkii.arl.item.ModelModArmor;

public class ModelPirateHat extends ModelModArmor {
   public final ModelRenderer hat;

   public ModelPirateHat() {
      float modelOff = -6.0F;
      this.textureWidth = 64;
      this.textureHeight = 64;
      ModelRenderer hatPart3 = new ModelRenderer(this, 0, 30);
      hatPart3.setRotationPoint(0.0F, 0.0F, 0.0F);
      hatPart3.addBox(-5.0F, -4.0F + modelOff, -3.0F, 1, 4, 8, 0.0F);
      this.setRotateAngle(hatPart3, -0.08726646F, 0.0F, -0.08726646F);
      this.hat = new ModelRenderer(this, 0, 0);
      this.hat.setRotationPoint(0.0F, -8.53F, 0.0F);
      this.hat.addBox(-5.0F, 0.0F + modelOff, -5.0F, 10, 1, 10, 0.0F);
      this.setRotateAngle(this.hat, 0.05235988F, 0.08726646F, 0.0F);
      ModelRenderer hatPart1 = new ModelRenderer(this, 0, 11);
      hatPart1.setRotationPoint(0.0F, 0.0F, 0.0F);
      hatPart1.addBox(-3.5F, -4.0F + modelOff, -3.5F, 7, 4, 8, 1.0F);
      ModelRenderer hatPart4 = new ModelRenderer(this, 0, 23);
      hatPart4.setRotationPoint(0.0F, 0.0F, 0.0F);
      hatPart4.addBox(-5.0F, -6.4F + modelOff, -5.0F, 10, 6, 1, 0.0F);
      this.setRotateAngle(hatPart4, 0.08726646F, 0.0F, 0.0F);
      ModelRenderer hatPart2 = new ModelRenderer(this, 0, 30);
      hatPart2.mirror = true;
      hatPart2.setRotationPoint(0.0F, 0.0F, 0.0F);
      hatPart2.addBox(4.0F, -4.0F + modelOff, -3.0F, 1, 4, 8, 0.0F);
      this.setRotateAngle(hatPart2, -0.08726646F, 0.0F, 0.08726646F);
      this.hat.addChild(hatPart3);
      this.hat.addChild(hatPart1);
      this.hat.addChild(hatPart4);
      this.hat.addChild(hatPart2);
   }

   public void setModelParts() {
      this.bipedHead.showModel = false;
      this.bipedHeadwear = this.hat;
   }
}
