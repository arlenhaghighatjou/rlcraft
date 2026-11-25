package cursedflames.bountifulbaubles.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCrownGold extends ModelBiped {
   public ModelRenderer crown;

   public ModelCrownGold() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      float s = 0.25F;
      this.crown = new ModelRenderer(this, 0, 0);
      this.crown.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.crown.cubeList.add(new ModelCustomUVBox(this.crown, 0, 0, -4.0F, -14.5F, -4.0F, 8.0F, 8.0F, 8.0F, 0.25F, false, 16, 16, 16));
      this.bipedHead.addChild(this.crown);
   }

   public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
   }

   public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
   }
}
