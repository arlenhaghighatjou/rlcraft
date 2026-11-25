package goblinbob.mobends.standard.animation.bit.player;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.standard.data.PlayerData;
import net.minecraft.util.math.MathHelper;

public class SprintJumpAnimationBit extends AnimationBit<PlayerData> {
   private float relax = 0.0F;

   public SprintJumpAnimationBit() {
   }

   public String[] getActions(PlayerData entityData) {
      return new String[]{"sprint_jump"};
   }

   public void onPlay(PlayerData entityData) {
      this.relax = 0.0F;
   }

   public void perform(PlayerData data) {
      if (data.getPrevMotionY() < 0.0D && data.getMotionY() > 0.0D) {
         this.onPlay(data);
      }

      boolean sprintLegSwitch = data.getSprintJumpLeg();
      float legSwitchMtp = sprintLegSwitch ? 1.0F : -1.0F;
      IModelPart mainArm = sprintLegSwitch ? data.rightArm : data.leftArm;
      IModelPart offArm = sprintLegSwitch ? data.leftArm : data.rightArm;
      IModelPart mainLeg = sprintLegSwitch ? data.rightLeg : data.leftLeg;
      IModelPart offLeg = sprintLegSwitch ? data.leftLeg : data.rightLeg;
      IModelPart mainForeLeg = sprintLegSwitch ? data.rightForeLeg : data.leftForeLeg;
      IModelPart offForeLeg = sprintLegSwitch ? data.leftForeLeg : data.rightForeLeg;
      float bodyRotationY = 20.0F * legSwitchMtp;
      float bodyLean = MathHelper.clamp((float)data.getMotionY(), -0.2F, 0.2F);
      bodyLean = bodyLean * -100.0F + 20.0F;
      if (this.relax < 1.0F) {
         this.relax += DataUpdateHandler.ticksPerFrame * 0.1F;
         this.relax = Math.min(this.relax, 1.0F);
      }

      float relaxAngle = MathHelper.sqrt(MathHelper.sqrt(this.relax));
      data.centerRotation.setSmoothness(0.3F).orientZero();
      data.globalOffset.slideToZero(0.5F);
      data.body.rotation.setSmoothness(0.3F).orientX(bodyLean).rotateY(bodyRotationY);
      data.rightLeg.rotation.setSmoothness(0.8F).orientZ(5.0F);
      data.leftLeg.rotation.setSmoothness(0.8F).orientZ(-5.0F);
      data.rightArm.rotation.setSmoothness(0.3F).orientZ(10.0F);
      data.leftArm.rotation.setSmoothness(0.3F).orientZ(-10.0F);
      mainLeg.getRotation().rotateX(-45.0F);
      offLeg.getRotation().rotateX(45.0F);
      mainArm.getRotation().rotateX(50.0F);
      offArm.getRotation().rotateX(-50.0F);
      mainForeLeg.getRotation().orientX(80.0F - relaxAngle * 80.0F);
      offForeLeg.getRotation().orientX(relaxAngle * 70.0F);
      data.head.rotation.orientX(((Float)data.headPitch.get()).floatValue() - 20.0F);
      data.head.rotation.rotateY(((Float)data.headYaw.get()).floatValue() - bodyRotationY);
   }
}
