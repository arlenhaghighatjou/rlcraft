package goblinbob.mobends.standard.animation.bit.biped;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.util.Tween;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.util.math.MathHelper;

public class SwimmingAnimationBit extends AnimationBit<BipedEntityData<?>> {
   private static final String[] ACTIONS = new String[]{"swimming", "swimming_surface"};
   private static final String[] ACTIONS_UNDERWATER = new String[]{"swimming", "swimming_deep"};
   private static final float PI = 3.1415927F;
   private static final float PI_2 = 6.2831855F;
   private float transformTransition = 0.0F;
   private float transitionSpeed = 0.1F;

   public SwimmingAnimationBit() {
   }

   public String[] getActions(BipedEntityData<?> data) {
      return data.isUnderwater() ? ACTIONS_UNDERWATER : ACTIONS;
   }

   public void onPlay(BipedEntityData<?> data) {
      this.transformTransition = 0.0F;
      this.transitionSpeed = 0.1F;
   }

   public void perform(BipedEntityData<?> data) {
      float ticks = DataUpdateHandler.getTicks();
      float armSway = (MathHelper.cos(ticks * 0.1625F) + 1.0F) / 2.0F;
      float armSway2 = (-MathHelper.sin(ticks * 0.1625F) + 1.0F) / 2.0F;
      float legFlap = MathHelper.cos(ticks * 0.4625F);
      float foreArmSway = ticks * 0.1625F % 6.2831855F / 6.2831855F;
      float foreArmStretch = armSway * 2.0F;
      --foreArmStretch;
      foreArmStretch = Math.max(foreArmStretch, 0.0F);
      float t = (float)Tween.easeInOut((double)this.transformTransition, 3.0D);
      if (!data.isStillHorizontally() && !data.isDrawingBow() && data.getTicksAfterAttack() >= 10.0F && data.isUnderwater()) {
         if (this.transformTransition < 1.0F) {
            this.transformTransition += DataUpdateHandler.ticksPerFrame * this.transitionSpeed;
            this.transformTransition = Math.min(this.transformTransition, 1.0F);
         }

         data.leftArm.rotation.setSmoothness(0.3F).orientX(armSway * -120.0F).rotateY(-90.0F * t).rotateX(armSway * 20.0F);
         data.rightArm.rotation.setSmoothness(0.3F).orientX(armSway * -120.0F).rotateY(90.0F * t).rotateX(armSway * 20.0F);
         data.leftForeArm.rotation.setSmoothness(0.3F).orientX(foreArmSway < 0.55F | (double)foreArmSway > 0.9D ? foreArmStretch * -60.0F : -60.0F);
         data.rightForeArm.rotation.setSmoothness(0.3F).orientX(foreArmSway < 0.55F | (double)foreArmSway > 0.9D ? foreArmStretch * -60.0F : -60.0F);
         data.leftLeg.rotation.setSmoothness(0.3F).orientX(legFlap * 40.0F);
         data.rightLeg.rotation.setSmoothness(0.3F).orientX(-legFlap * 40.0F);
         data.leftForeLeg.rotation.setSmoothness(0.4F).orientX(5.0F);
         data.rightForeLeg.rotation.setSmoothness(0.4F).orientX(5.0F);
         data.body.rotation.setSmoothness(0.5F).orientX(armSway * -20.0F);
         data.renderRightItemRotation.setSmoothness(0.3F).orientX(armSway * 50.0F);
      } else {
         if (this.transformTransition > 0.0F) {
            this.transformTransition -= DataUpdateHandler.ticksPerFrame * this.transitionSpeed;
            this.transformTransition = Math.max(0.0F, this.transformTransition);
         }

         armSway = (MathHelper.cos(ticks * 0.0825F) + 1.0F) / 2.0F;
         armSway2 = (-MathHelper.sin(ticks * 0.0825F) + 1.0F) / 2.0F;
         legFlap = MathHelper.cos(ticks * 0.2625F);
         data.leftArm.rotation.setSmoothness(0.3F).orientX(armSway2 * 30.0F - 15.0F).rotateZ(-armSway * 30.0F);
         data.rightArm.rotation.setSmoothness(0.3F).orientX(armSway2 * 30.0F - 15.0F).rotateZ(armSway * 30.0F);
         data.leftForeArm.rotation.setSmoothness(0.3F).orientX(armSway2 * -40.0F);
         data.rightForeArm.rotation.setSmoothness(0.3F).orientX(armSway2 * -40.0F);
         data.leftLeg.rotation.setSmoothness(0.3F).orientX(legFlap * 40.0F);
         data.rightLeg.rotation.setSmoothness(0.3F).orientX(-legFlap * 40.0F);
         data.leftForeLeg.rotation.setSmoothness(0.4F).orientX(5.0F);
         data.rightForeLeg.rotation.setSmoothness(0.4F).orientX(5.0F);
         data.body.rotation.orientX(armSway * 10.0F);
      }

      data.head.rotation.setSmoothness(1.0F).orientX(((Float)data.headPitch.get()).floatValue()).rotateY(((Float)data.headYaw.get()).floatValue()).rotateX(-80.0F * t);
      data.renderRotation.setSmoothness(0.7F).orientX(t * 80.0F);
      data.globalOffset.slideZ(-20.0F * t, 0.7F);
      data.globalOffset.slideY(14.0F * t, 0.7F);
      data.localOffset.slideToZero(0.3F);
   }
}
