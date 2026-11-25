package goblinbob.mobends.standard.animation.bit.player;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.util.EnumHandSide;

public class PunchAnimationBit extends AnimationBit<BipedEntityData<?>> {
   private final EnumHandSide fistPunchArm;

   public PunchAnimationBit(EnumHandSide fistPunchArm) {
      this.fistPunchArm = fistPunchArm;
   }

   public void perform(BipedEntityData<?> data) {
      data.rightArm.rotation.setSmoothness(0.3F).orientX(-90.0F).rotateZ(20.0F);
      data.leftArm.rotation.setSmoothness(0.3F).orientZ(-20.0F).rotateX(-90.0F);
      data.rightForeArm.rotation.setSmoothness(0.3F).orientX(-80.0F);
      data.leftForeArm.rotation.setSmoothness(0.3F).orientX(-80.0F);
      float renderRotationY = 0.0F;
      if (data.isStillHorizontally()) {
         renderRotationY = -20.0F;
         data.globalOffset.slideY(-2.0F);
         data.rightLeg.rotation.setSmoothness(0.3F).orientX(-30.0F).rotateZ(10.0F);
         data.leftLeg.rotation.setSmoothness(0.3F).orientX(-30.0F).rotateY(-25.0F).rotateZ(-10.0F);
         data.rightForeLeg.rotation.setSmoothness(0.3F).orientX(30.0F);
         data.leftForeLeg.rotation.setSmoothness(0.3F).orientX(30.0F);
      }

      if (this.fistPunchArm == EnumHandSide.RIGHT) {
         data.rightArm.rotation.setSmoothness(0.9F).orientY(-90.0F).rotateX(-90.0F + ((Float)data.headPitch.get()).floatValue()).rotateY(10.0F);
         data.rightForeArm.rotation.setSmoothness(0.9F).orientX(0.0F);
         data.body.rotation.setSmoothness(0.6F).orientY(-20.0F + renderRotationY);
         data.head.rotation.rotateY(20.0F);
      } else {
         data.leftArm.rotation.setSmoothness(0.9F).orientY(100.0F).rotateX(-90.0F + ((Float)data.headPitch.get()).floatValue()).rotateY(-16.0F);
         data.leftForeArm.rotation.setSmoothness(0.9F).orientX(0.0F);
         data.body.rotation.setSmoothness(0.6F).orientY(20.0F + renderRotationY);
         data.head.rotation.rotateY(-20.0F);
      }

      data.renderRotation.orientY(renderRotationY);
   }
}
