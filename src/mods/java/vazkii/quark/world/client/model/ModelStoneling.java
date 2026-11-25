package vazkii.quark.world.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import vazkii.quark.world.entity.EntityStoneling;

public class ModelStoneling extends ModelBase {
   private final ModelRenderer body;
   private final ModelRenderer head;
   private final ModelRenderer arm_right;
   private final ModelRenderer arm_left;
   private final ModelRenderer leg_right;
   private final ModelRenderer leg_left;

   public ModelStoneling() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.body = new ModelRenderer(this);
      this.body.setRotationPoint(0.0F, 14.0F, 0.0F);
      this.head = new ModelRenderer(this);
      this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.body.addChild(this.head);
      this.head.cubeList.add(new ModelBox(this.head, 0, 0, -3.0F, -2.0F, -3.0F, 6, 8, 6, 0.0F, false));
      this.head.cubeList.add(new ModelBox(this.head, 8, 24, -1.0F, -4.0F, -5.0F, 2, 4, 2, 0.0F, false));
      this.head.cubeList.add(new ModelBox(this.head, 16, 20, -1.0F, 6.0F, -3.0F, 2, 2, 2, 0.0F, false));
      this.head.cubeList.add(new ModelBox(this.head, 0, 24, -1.0F, -4.0F, 3.0F, 2, 4, 2, 0.0F, false));
      this.head.cubeList.add(new ModelBox(this.head, 16, 24, -1.0F, -4.0F, -3.0F, 2, 2, 6, 0.0F, false));
      this.head.cubeList.add(new ModelBox(this.head, 24, 20, -1.0F, -4.0F, -1.0F, 2, 2, 2, 0.0F, false));
      this.arm_right = new ModelRenderer(this);
      this.arm_right.setRotationPoint(-3.0F, 2.0F, 0.0F);
      this.setRotationAngle(this.arm_right, 3.1416F, 0.0F, 0.0F);
      this.body.addChild(this.arm_right);
      this.arm_right.cubeList.add(new ModelBox(this.arm_right, 0, 14, -2.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, false));
      this.arm_left = new ModelRenderer(this);
      this.arm_left.setRotationPoint(3.0F, 2.0F, 0.0F);
      this.setRotationAngle(this.arm_left, 3.1416F, 0.0F, 0.0F);
      this.body.addChild(this.arm_left);
      this.arm_left.cubeList.add(new ModelBox(this.arm_left, 8, 14, 0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, false));
      this.leg_right = new ModelRenderer(this);
      this.leg_right.setRotationPoint(-2.0F, 4.0F, 0.0F);
      this.body.addChild(this.leg_right);
      this.leg_right.cubeList.add(new ModelBox(this.leg_right, 16, 14, -1.0F, 2.0F, -1.0F, 2, 4, 2, 0.0F, false));
      this.leg_left = new ModelRenderer(this);
      this.leg_left.setRotationPoint(1.0F, 4.0F, 0.0F);
      this.body.addChild(this.leg_left);
      this.leg_left.cubeList.add(new ModelBox(this.leg_left, 24, 14, 0.0F, 2.0F, -1.0F, 2, 4, 2, 0.0F, false));
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      this.leg_right.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
      this.leg_left.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * limbSwingAmount;
      EntityStoneling stoneling = (EntityStoneling)entityIn;
      ItemStack carry = stoneling.getCarryingItem();
      if (carry.isEmpty() && !stoneling.isBeingRidden()) {
         this.arm_right.rotateAngleX = 0.0F;
         this.arm_left.rotateAngleX = 0.0F;
      } else {
         this.arm_right.rotateAngleX = 3.1416F;
         this.arm_left.rotateAngleX = 3.1416F;
      }

   }

   public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.body.render(scale);
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
   }
}
