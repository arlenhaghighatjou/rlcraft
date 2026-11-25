package goblinbob.mobends.standard.animation.bit.spider;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.standard.data.SpiderData;
import net.minecraft.util.math.MathHelper;

public class SpiderJumpAnimationBit extends AnimationBit<SpiderData> {
   public SpiderJumpAnimationBit() {
   }

   public String[] getActions(SpiderData entityData) {
      return new String[]{"jump"};
   }

   public void perform(SpiderData data) {
      for(int i = 0; i < data.limbs.length; ++i) {
         boolean odd = i % 2 == 1;
         SpiderData.Limb limb = data.limbs[i];
         float naturalYaw = -((float)i / (float)(data.limbs.length - 1) * 2.0F - 1.0F);
         naturalYaw = odd ? -naturalYaw * 1.3F : naturalYaw * 1.3F;
         limb.upperPart.rotation.orientY(naturalYaw / 3.1415927F * 180.0F);
      }

      float motionY = (float)(-data.getInterpolatedMotionY()) * 5.0F;
      motionY = MathHelper.clamp(motionY, -1.0F, 1.0F);
      float legAngle = -20.0F + motionY * 25.0F;
      float smoothness = 1.0F;
      data.limbs[0].upperPart.rotation.setSmoothness(smoothness).localRotateZ(legAngle);
      data.limbs[1].upperPart.rotation.setSmoothness(smoothness).localRotateZ(-legAngle);
      data.limbs[2].upperPart.rotation.setSmoothness(smoothness).localRotateZ(legAngle);
      data.limbs[3].upperPart.rotation.setSmoothness(smoothness).localRotateZ(-legAngle);
      data.limbs[4].upperPart.rotation.setSmoothness(smoothness).localRotateZ(legAngle);
      data.limbs[5].upperPart.rotation.setSmoothness(smoothness).localRotateZ(-legAngle);
      data.limbs[6].upperPart.rotation.setSmoothness(smoothness).localRotateZ(legAngle);
      data.limbs[7].upperPart.rotation.setSmoothness(smoothness).localRotateZ(-legAngle);
      float foreLegAngle = -70.0F - motionY * 40.0F;
      data.limbs[0].lowerPart.rotation.setSmoothness(smoothness).orientZ(foreLegAngle);
      data.limbs[1].lowerPart.rotation.setSmoothness(smoothness).orientZ(-foreLegAngle);
      data.limbs[2].lowerPart.rotation.setSmoothness(smoothness).orientZ(foreLegAngle);
      data.limbs[3].lowerPart.rotation.setSmoothness(smoothness).orientZ(-foreLegAngle);
      data.limbs[4].lowerPart.rotation.setSmoothness(smoothness).orientZ(foreLegAngle);
      data.limbs[5].lowerPart.rotation.setSmoothness(smoothness).orientZ(-foreLegAngle);
      data.limbs[6].lowerPart.rotation.setSmoothness(smoothness).orientZ(foreLegAngle);
      data.limbs[7].lowerPart.rotation.setSmoothness(smoothness).orientZ(-foreLegAngle);
      data.localOffset.slideToZero();
      data.globalOffset.set(0.0F, 0.0F, 0.0F);
      data.centerRotation.orientZero();
      data.renderRotation.orientZero();
   }
}
