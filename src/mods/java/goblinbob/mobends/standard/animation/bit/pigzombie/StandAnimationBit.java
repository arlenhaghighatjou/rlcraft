package goblinbob.mobends.standard.animation.bit.pigzombie;

import goblinbob.mobends.standard.data.PigZombieData;

public class StandAnimationBit extends goblinbob.mobends.standard.animation.bit.biped.StandAnimationBit<PigZombieData> {
   public StandAnimationBit() {
   }

   public void perform(PigZombieData data) {
      super.perform(data);
      data.globalOffset.slideY(-3.0F);
      data.body.rotation.localRotateX(20.0F).rotateZ(-10.0F);
      data.head.rotation.rotateX(-20.0F);
      data.rightArm.rotation.rotateX(-20.0F).rotateZ(10.0F);
      data.leftArm.rotation.rotateX(-20.0F).rotateZ(10.0F);
      data.rightLeg.rotation.rotateZ(10.0F);
      data.leftLeg.rotation.rotateZ(-10.0F);
      data.rightLeg.rotation.rotateX(-30.0F);
      data.leftLeg.rotation.rotateX(-10.0F).rotateY(-10.0F);
      data.rightForeLeg.rotation.rotateX(25.0F);
      data.leftForeLeg.rotation.rotateX(25.0F);
   }
}
