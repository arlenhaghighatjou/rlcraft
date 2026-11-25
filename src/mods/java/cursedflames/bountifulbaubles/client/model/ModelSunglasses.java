package cursedflames.bountifulbaubles.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSunglasses extends ModelBiped {
   public ModelRenderer sunglasses;

   public ModelSunglasses() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      float s = 0.75F;
      this.sunglasses = new ModelRenderer(this, 0, 0);
      this.sunglasses.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.sunglasses.cubeList.add(new ModelCustomUVBox(this.sunglasses, 0, 0, -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.75F, false, 16, 16, 16));
      this.bipedHead.addChild(this.sunglasses);
      this.bipedHeadwear.showModel = false;
   }

   public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.bipedHeadwear.showModel = false;
      super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
   }

   public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
   }
}
