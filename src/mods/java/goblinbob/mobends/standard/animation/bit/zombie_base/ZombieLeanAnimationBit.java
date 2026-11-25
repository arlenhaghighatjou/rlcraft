package goblinbob.mobends.standard.animation.bit.zombie_base;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.standard.data.ZombieDataBase;

public class ZombieLeanAnimationBit extends AnimationBit<ZombieDataBase<?>> {
   public ZombieLeanAnimationBit() {
   }

   public String[] getActions(ZombieDataBase<?> entityData) {
      return new String[]{"lean"};
   }

   public void perform(ZombieDataBase<?> data) {
      data.globalOffset.slideY(-3.0F);
      data.body.rotation.localRotateX(30.0F);
      data.head.rotation.rotateX(-30.0F);
      data.rightArm.rotation.rotateX(-30.0F);
      data.leftArm.rotation.rotateX(-30.0F);
      data.rightLeg.rotation.rotateZ(10.0F);
      data.leftLeg.rotation.rotateZ(-10.0F);
      data.rightLeg.rotation.rotateX(-20.0F);
      data.leftLeg.rotation.rotateX(-20.0F);
      data.rightForeLeg.rotation.rotateX(25.0F);
      data.leftForeLeg.rotation.rotateX(25.0F);
      if (!data.isStillHorizontally() && data.getCurrentWalkingState() == 1) {
         data.rightArm.rotation.orientX(-120.0F);
         data.leftArm.rotation.orientX(-120.0F);
      }

   }
}
