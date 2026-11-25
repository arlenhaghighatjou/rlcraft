package rlmixins.models;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;

public class ModelScarliteArmor extends ModelBiped {
   public ModelRenderer RightLeg;
   public ModelRenderer LeftLeg;
   public ModelRenderer RightFoot;
   public ModelRenderer LeftFoot;

   public ModelScarliteArmor(float scale) {
      super(scale, 0.0F, 128, 128);
      this.textureWidth = 128;
      this.textureHeight = 128;
      this.bipedHead.cubeList.add(new ModelBox(this.bipedHead, 0, 64, -5.0F, -9.25F, -3.0F, 10, 8, 8, 0.0F, false));
      this.bipedHead.cubeList.add(new ModelBox(this.bipedHead, 16, 80, 4.0F, -5.25F, -5.0F, 1, 4, 2, 0.0F, false));
      this.bipedHead.cubeList.add(new ModelBox(this.bipedHead, 0, 64, -5.0F, -5.25F, -5.0F, 1, 4, 2, 0.0F, false));
      this.bipedHead.cubeList.add(new ModelBox(this.bipedHead, 48, 86, -5.0F, -9.25F, -5.0F, 10, 4, 2, 0.0F, false));
      this.bipedHead.cubeList.add(new ModelBox(this.bipedHead, 0, 80, -1.0F, -10.25F, -6.0F, 2, 5, 12, 0.0F, false));
      this.bipedHead.cubeList.add(new ModelBox(this.bipedHead, 49, 113, 5.0F, -6.25F, -4.0F, 1, 2, 9, 0.0F, false));
      this.bipedHead.cubeList.add(new ModelBox(this.bipedHead, 29, 113, 6.0F, -6.25F, -3.0F, 1, 2, 9, 0.0F, false));
      this.bipedHead.cubeList.add(new ModelBox(this.bipedHead, 42, 75, -6.0F, -6.25F, -4.0F, 1, 2, 9, 0.0F, false));
      this.bipedHead.cubeList.add(new ModelBox(this.bipedHead, 0, 110, -7.0F, -6.25F, -3.0F, 1, 2, 9, 0.0F, false));
      this.bipedHead.cubeList.add(new ModelBox(this.bipedHead, 16, 92, -1.0F, -11.25F, -5.0F, 2, 1, 12, 0.0F, false));
      this.bipedBody.cubeList.add(new ModelBox(this.bipedBody, 16, 80, -5.0F, -0.1F, -3.0F, 10, 6, 6, 0.0F, false));
      this.bipedBody.cubeList.add(new ModelBox(this.bipedBody, 32, 92, -5.0F, 5.9F, -2.5F, 10, 6, 5, 0.0F, false));
      this.bipedRightArm.cubeList.add(new ModelBox(this.bipedRightArm, 36, 64, -3.9F, -2.75F, -3.0F, 5, 5, 6, 0.0F, false));
      this.bipedRightArm.cubeList.add(new ModelBox(this.bipedRightArm, 73, 70, -3.65F, 4.25F, -2.5F, 5, 5, 5, 0.0F, false));
      this.bipedRightArm.cubeList.add(new ModelBox(this.bipedRightArm, 56, 97, -4.9F, -2.75F, -2.0F, 1, 2, 6, 0.0F, false));
      this.bipedRightArm.cubeList.add(new ModelBox(this.bipedRightArm, 16, 97, -4.65F, 4.25F, -1.75F, 1, 2, 5, 0.0F, false));
      this.bipedLeftArm.cubeList.add(new ModelBox(this.bipedLeftArm, 0, 99, -1.1F, -2.75F, -3.0F, 5, 5, 6, 0.0F, true));
      this.bipedLeftArm.cubeList.add(new ModelBox(this.bipedLeftArm, 93, 72, 3.9F, -2.75F, -2.0F, 1, 2, 6, 0.0F, true));
      this.bipedLeftArm.cubeList.add(new ModelBox(this.bipedLeftArm, 0, 80, 3.65F, 4.25F, -1.75F, 1, 2, 5, 0.0F, true));
      this.bipedLeftArm.cubeList.add(new ModelBox(this.bipedLeftArm, 53, 70, -1.35F, 4.25F, -2.5F, 5, 5, 5, 0.0F, true));
      this.RightLeg = this.bipedRightLeg;
      this.LeftLeg = this.bipedLeftLeg;
      this.RightFoot = this.bipedRightLeg;
      this.LeftFoot = this.bipedLeftLeg;
      this.RightLeg.cubeList.add(new ModelBox(this.RightLeg, 108, 100, -2.7F, 0.1F, -2.5F, 5, 9, 5, 0.0F, false));
      this.LeftLeg.cubeList.add(new ModelBox(this.LeftLeg, 108, 114, -2.3F, 0.1F, -2.5F, 5, 9, 5, 0.0F, true));
      this.RightFoot.cubeList.add(new ModelBox(this.RightFoot, 15, 115, -3.71F, 7.25F, -2.0F, 1, 2, 6, 0.0F, false));
      this.RightFoot.cubeList.add(new ModelBox(this.RightFoot, 28, 64, -2.71F, 6.25F, -3.0F, 5, 2, 2, 0.0F, false));
      this.RightFoot.cubeList.add(new ModelBox(this.RightFoot, 38, 103, -2.71F, 8.25F, -3.0F, 5, 4, 6, 0.0F, false));
      this.LeftFoot.cubeList.add(new ModelBox(this.LeftFoot, 36, 75, -2.29F, 6.25F, -3.0F, 5, 2, 2, 0.0F, false));
      this.LeftFoot.cubeList.add(new ModelBox(this.LeftFoot, 16, 105, -2.29F, 8.25F, -3.0F, 5, 4, 6, 0.0F, false));
      this.LeftFoot.cubeList.add(new ModelBox(this.LeftFoot, 40, 113, 2.79F, 7.25F, -2.0F, 1, 2, 6, 0.0F, false));
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      super.render(entity, f, f1, f2, f3, f4, f5);
      this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      if (entityIn instanceof EntityArmorStand) {
         EntityArmorStand entityarmorstand = (EntityArmorStand)entityIn;
         this.bipedHead.rotateAngleX = 0.017453292F * entityarmorstand.getHeadRotation().getX();
         this.bipedHead.rotateAngleY = 0.017453292F * entityarmorstand.getHeadRotation().getY();
         this.bipedHead.rotateAngleZ = 0.017453292F * entityarmorstand.getHeadRotation().getZ();
         this.bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
         this.bipedBody.rotateAngleX = 0.017453292F * entityarmorstand.getBodyRotation().getX();
         this.bipedBody.rotateAngleY = 0.017453292F * entityarmorstand.getBodyRotation().getY();
         this.bipedBody.rotateAngleZ = 0.017453292F * entityarmorstand.getBodyRotation().getZ();
         this.bipedLeftArm.rotateAngleX = 0.017453292F * entityarmorstand.getLeftArmRotation().getX();
         this.bipedLeftArm.rotateAngleY = 0.017453292F * entityarmorstand.getLeftArmRotation().getY();
         this.bipedLeftArm.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftArmRotation().getZ();
         this.bipedRightArm.rotateAngleX = 0.017453292F * entityarmorstand.getRightArmRotation().getX();
         this.bipedRightArm.rotateAngleY = 0.017453292F * entityarmorstand.getRightArmRotation().getY();
         this.bipedRightArm.rotateAngleZ = 0.017453292F * entityarmorstand.getRightArmRotation().getZ();
         this.bipedLeftLeg.rotateAngleX = 0.017453292F * entityarmorstand.getLeftLegRotation().getX();
         this.bipedLeftLeg.rotateAngleY = 0.017453292F * entityarmorstand.getLeftLegRotation().getY();
         this.bipedLeftLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftLegRotation().getZ();
         this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
         this.bipedRightLeg.rotateAngleX = 0.017453292F * entityarmorstand.getRightLegRotation().getX();
         this.bipedRightLeg.rotateAngleY = 0.017453292F * entityarmorstand.getRightLegRotation().getY();
         this.bipedRightLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getRightLegRotation().getZ();
         this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
         copyModelAngles(this.bipedHead, this.bipedHeadwear);
      } else {
         super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      }

   }
}
