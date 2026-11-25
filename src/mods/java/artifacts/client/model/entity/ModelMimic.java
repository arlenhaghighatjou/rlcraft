package artifacts.client.model.entity;

import artifacts.common.entity.EntityMimic;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class ModelMimic extends ModelBase {
   public ModelRenderer chestLid;
   public ModelRenderer chestBelow;
   public ModelRenderer chestKnob;

   public ModelMimic() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.chestLid = new ModelRenderer(this);
      this.chestLid.setRotationPoint(0.0F, 15.0F, 7.0F);
      this.chestLid.cubeList.add(new ModelBox(this.chestLid, 0, 0, -7.0F, -5.0F, -14.0F, 14, 5, 14, 0.0F, false));
      this.chestBelow = new ModelRenderer(this);
      this.chestBelow.setRotationPoint(0.0F, 14.0F, 7.0F);
      this.chestBelow.cubeList.add(new ModelBox(this.chestBelow, 0, 19, -7.0F, 0.0F, -14.0F, 14, 10, 14, 0.0F, false));
      this.chestKnob = new ModelRenderer(this);
      this.chestKnob.setRotationPoint(0.0F, 15.0F, 7.0F);
      this.chestKnob.cubeList.add(new ModelBox(this.chestKnob, 0, 0, -1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F, false));
   }

   public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.chestLid.render(scale);
      this.chestBelow.render(scale);
      this.chestKnob.render(scale);
   }

   public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
      super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTickTime);
      if (((EntityMimic)entity).ticksInAir > 0) {
         float angle = Math.min(60.0F, ((float)(((EntityMimic)entity).ticksInAir - 1) + partialTickTime) * 6.0F);
         this.chestLid.rotateAngleX = this.chestKnob.rotateAngleX = -angle * 0.0174533F;
      } else {
         this.chestLid.rotateAngleX = this.chestKnob.rotateAngleX = 0.0F;
      }

      if (((EntityMimic)entity).ticksInAir > 0) {
         float angle = Math.max(-30.0F, ((float)(((EntityMimic)entity).ticksInAir - 1) + partialTickTime) * -3.0F);
         this.chestBelow.rotateAngleX = -angle * 0.0174533F;
      } else {
         this.chestBelow.rotateAngleX = 0.0F;
      }

   }
}
