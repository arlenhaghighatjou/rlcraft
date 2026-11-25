package goblinbob.mobends.standard.animation.bit.zombie_base;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.standard.data.ZombieDataBase;
import net.minecraft.util.math.MathHelper;

public class ZombieStumblingAnimationBit extends AnimationBit<ZombieDataBase<?>> {
   public ZombieStumblingAnimationBit() {
   }

   public String[] getActions(ZombieDataBase<?> data) {
      return new String[]{"stumbling"};
   }

   public void perform(ZombieDataBase<?> data) {
      float PI = 3.1415927F;
      float limbSwing = ((Float)data.limbSwing.get()).floatValue() * 0.6662F;
      limbSwing = (float)((double)limbSwing + Math.cos((double)(limbSwing * 2.0F)) * 0.30000001192092896D);
      float swingAmount = 45.0F * ((Float)data.limbSwingAmount.get()).floatValue();
      data.rightLeg.rotation.setSmoothness(1.0F).orientX(MathHelper.cos(limbSwing) * swingAmount);
      data.leftLeg.rotation.setSmoothness(1.0F).orientX(MathHelper.cos(limbSwing + 3.1415927F) * swingAmount);
      data.rightArm.rotation.setSmoothness(1.0F).orientX(MathHelper.cos(limbSwing + 3.1415927F) * swingAmount);
      data.leftArm.rotation.setSmoothness(1.0F).orientX(MathHelper.cos(limbSwing) * swingAmount);
      data.body.rotation.setSmoothness(0.5F).orientY(MathHelper.cos(limbSwing + 3.1415927F) * swingAmount);
      float heavyStompValue = Math.min(limbSwing % 3.1415927F / 3.1415927F, 1.0F);
      float heavyStompValueInv = 1.0F - Math.min(limbSwing % 3.1415927F / 3.1415927F, 1.0F);
      data.body.rotation.rotateX(heavyStompValueInv * 40.0F);
      data.body.rotation.rotateZ(MathHelper.cos(limbSwing) * 10.0F);
      data.head.rotation.rotateX(-heavyStompValueInv * 40.0F);
      data.head.rotation.rotateZ(-40.0F + heavyStompValue * 20.0F);
   }
}
