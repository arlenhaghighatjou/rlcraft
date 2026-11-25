package artifacts.client.model.entity;

import artifacts.common.entity.EntityMimic;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class ModelMimicInterior extends ModelBase {
   public ModelRenderer chestLidTeeth;
   public ModelRenderer chestBelowTeeth;
   public ModelRenderer chestLidMouth;
   public ModelRenderer chestBelowMouth;

   public ModelMimicInterior() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.chestLidTeeth = new ModelRenderer(this);
      this.chestLidTeeth.setRotationPoint(0.0F, 15.0F, 7.0F);
      this.chestLidTeeth.cubeList.add(new ModelBox(this.chestLidTeeth, 14, 27, -6.0F, 0.0F, -13.0F, 12, 2, 12, 0.0F, false));
      this.chestBelowTeeth = new ModelRenderer(this);
      this.chestBelowTeeth.setRotationPoint(0.0F, 14.0F, 7.0F);
      this.chestBelowTeeth.cubeList.add(new ModelBox(this.chestBelowTeeth, 14, 42, -6.0F, -3.0F, -13.0F, 12, 3, 12, 0.0F, false));
      this.chestLidMouth = new ModelRenderer(this);
      this.chestLidMouth.setRotationPoint(0.0F, 15.0F, 7.0F);
      this.chestLidMouth.cubeList.add(new ModelBox(this.chestLidMouth, 2, 14, -6.0F, 0.05F, -13.0F, 12, 0, 12, 0.0F, false));
      this.chestBelowMouth = new ModelRenderer(this);
      this.chestBelowMouth.setRotationPoint(0.0F, 14.0F, 7.0F);
      this.chestBelowMouth.cubeList.add(new ModelBox(this.chestBelowMouth, 14, 14, -6.0F, -0.05F, -13.0F, 12, 0, 12, 0.0F, false));
   }

   public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.chestLidTeeth.render(scale);
      this.chestBelowTeeth.render(scale);
      this.chestLidMouth.render(scale);
      this.chestBelowMouth.render(scale);
   }

   public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
      super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTickTime);
      if (((EntityMimic)entity).ticksInAir > 0) {
         float angle = Math.min(60.0F, ((float)(((EntityMimic)entity).ticksInAir - 1) + partialTickTime) * 6.0F);
         this.chestLidTeeth.rotateAngleX = this.chestLidMouth.rotateAngleX = -angle * 0.0174533F;
      } else {
         this.chestLidTeeth.rotateAngleX = this.chestLidMouth.rotateAngleX = 0.0F;
      }

      if (((EntityMimic)entity).ticksInAir > 0) {
         float angle = Math.max(-30.0F, ((float)(((EntityMimic)entity).ticksInAir - 1) + partialTickTime) * -3.0F);
         this.chestBelowTeeth.rotateAngleX = this.chestBelowMouth.rotateAngleX = -angle * 0.0174533F;
      } else {
         this.chestBelowTeeth.rotateAngleX = this.chestBelowMouth.rotateAngleX = 0.0F;
      }

   }
}
