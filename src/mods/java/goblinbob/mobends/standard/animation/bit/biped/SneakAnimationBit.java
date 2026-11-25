package goblinbob.mobends.standard.animation.bit.biped;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.util.math.MathHelper;

public class SneakAnimationBit extends AnimationBit<BipedEntityData<?>> {
   private static final String[] ACTIONS = new String[]{"sneak"};

   public SneakAnimationBit() {
   }

   public String[] getActions(BipedEntityData<?> entityData) {
      return ACTIONS;
   }

   public void perform(BipedEntityData<?> data) {
      data.localOffset.slideToZero(0.3F);
      data.globalOffset.slideY(-1.3F);
      float PI = 3.1415927F;
      float limbSwing = ((Float)data.limbSwing.get()).floatValue() * 0.6662F;
      float limbSwingAmount = ((Float)data.limbSwingAmount.get()).floatValue() * 1.4F * 1.1F / 3.1415927F * 180.0F;
      float var = limbSwing / 3.1415927F % 2.0F;
      data.rightLeg.rotation.setSmoothness(1.0F).orientX(MathHelper.cos(limbSwing) * limbSwingAmount - 5.0F).rotateZ(10.0F);
      data.leftLeg.rotation.setSmoothness(1.0F).orientX(MathHelper.cos(limbSwing + 3.1415927F) * limbSwingAmount - 5.0F).rotateZ(-10.0F);
      data.rightArm.rotation.setSmoothness(0.8F).orientX(20.0F * MathHelper.cos(limbSwing + 3.1415927F) - 20.0F).rotateZ(10.0F);
      data.leftArm.rotation.setSmoothness(0.8F).orientX(20.0F * MathHelper.cos(limbSwing) - 20.0F).rotateZ(-10.0F);
      data.leftForeLeg.rotation.setSmoothness(0.3F).orientX(var > 1.0F ? 45.0F : 10.0F);
      data.rightForeLeg.rotation.setSmoothness(0.3F).orientX(var > 1.0F ? 10.0F : 45.0F);
      float var2 = 25.0F + MathHelper.cos(limbSwing * 2.0F) * 5.0F;
      data.body.rotation.localRotateX(var2);
      data.head.rotation.rotateX(-var2);
   }
}
