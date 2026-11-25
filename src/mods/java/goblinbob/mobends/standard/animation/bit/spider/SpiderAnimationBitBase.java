package goblinbob.mobends.standard.animation.bit.spider;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.standard.animation.controller.SpiderController;
import goblinbob.mobends.standard.data.SpiderData;
import net.minecraft.util.math.MathHelper;

public abstract class SpiderAnimationBitBase extends AnimationBit<SpiderData> {
   protected float startTransition = 0.0F;

   public SpiderAnimationBitBase() {
   }

   protected void animateMovingLimb(SpiderData data, float groundLevel, float limbSwing, int index, float minDist, float maxDist, float minRot, float maxRot) {
      boolean odd = index % 2 == 1;
      float offset = (index + 1) / 2 % 2 == 0 ? 3.1415927F : 0.0F;
      float smoothness = 1.0F;
      float sideRotation = minRot + (MathHelper.sin(limbSwing + offset) * 0.5F + 0.5F) * (maxRot - minRot);
      float dist = minDist + (MathHelper.sin(limbSwing + offset) * 0.5F + 0.5F) * (maxDist - minDist);
      groundLevel = groundLevel + -7.0F + Math.max(0.0F, MathHelper.cos(limbSwing + offset)) * 4.0F;
      SpiderData.Limb limb = data.limbs[index];
      limb.upperPart.rotation.setSmoothness(smoothness).orientY(odd ? sideRotation : -sideRotation);
      if (this.startTransition >= 1.0F) {
         SpiderController.putLimbOnGround(limb.upperPart.rotation, limb.lowerPart.rotation, odd, (double)dist, (double)groundLevel);
      } else {
         SpiderController.putLimbOnGround(limb.upperPart.rotation, limb.lowerPart.rotation, odd, (double)dist, (double)groundLevel, this.startTransition);
      }

      limb.setAngleAndDistance(odd ? sideRotation / 180.0F * 3.1415927F : 3.1415927F - sideRotation / 180.0F * 3.1415927F, dist * 0.0625F);
   }
}
