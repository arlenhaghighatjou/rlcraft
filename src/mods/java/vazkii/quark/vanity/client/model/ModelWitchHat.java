package vazkii.quark.vanity.client.model;

import net.minecraft.client.model.ModelRenderer;
import vazkii.arl.item.ModelModArmor;

public class ModelWitchHat extends ModelModArmor {
   private final ModelRenderer witchHat;

   public ModelWitchHat() {
      this.textureWidth = 64;
      this.textureHeight = 128;
      float offX = -5.0F;
      float offY = -10.0F;
      float offZ = -5.0F;
      this.witchHat = new ModelRenderer(this);
      this.witchHat.setTextureSize(64, 128);
      this.witchHat.setRotationPoint(-5.0F, -10.03125F, -5.0F);
      this.witchHat.setTextureOffset(0, 64).addBox(offX, offY, offZ, 10, 2, 10);
      ModelRenderer mainBox = (new ModelRenderer(this)).setTextureSize(64, 128);
      mainBox.setRotationPoint(1.75F, -3.8F, 2.0F);
      mainBox.setTextureOffset(0, 76).addBox(offX, offY, offZ, 7, 4, 7);
      mainBox.rotateAngleX = -0.05235988F;
      mainBox.rotateAngleZ = 0.02617994F;
      this.witchHat.addChild(mainBox);
      ModelRenderer towerBox = (new ModelRenderer(this)).setTextureSize(64, 128);
      towerBox.setRotationPoint(1.75F, -3.0F, 2.0F);
      towerBox.setTextureOffset(0, 87).addBox(offX, offY, offZ, 4, 4, 4);
      towerBox.rotateAngleX = -0.10471976F;
      towerBox.rotateAngleZ = 0.05235988F;
      mainBox.addChild(towerBox);
      ModelRenderer smallerBox = (new ModelRenderer(this)).setTextureSize(64, 128);
      smallerBox.setRotationPoint(1.0F, -1.0F, 0.0F);
      smallerBox.setTextureOffset(0, 95).addBox(offX, offY, offZ, 1, 2, 1, 0.25F);
      smallerBox.rotateAngleX = -0.20943952F;
      smallerBox.rotateAngleZ = 0.10471976F;
      towerBox.addChild(smallerBox);
   }

   public void setModelParts() {
      this.bipedHead.showModel = false;
      this.bipedHeadwear = this.witchHat;
   }
}
