package cursedflames.bountifulbaubles.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelFlare extends ModelBase {
   public ModelRenderer flare;

   public ModelFlare() {
      this(0, 0, 32, 16);
   }

   public ModelFlare(int u, int v, int width, int height) {
      this.textureWidth = width;
      this.textureHeight = height;
      this.flare = new ModelRenderer(this, u, v);
      this.flare.addBox(-1.0F, -1.0F, -4.0F, 2, 2, 8, 0.0F);
      this.flare.setRotationPoint(0.0F, 0.0F, 0.0F);
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      this.flare.render(scale);
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      this.flare.rotateAngleY = netHeadYaw * 0.017453292F;
      this.flare.rotateAngleX = headPitch * 0.017453292F;
   }
}
