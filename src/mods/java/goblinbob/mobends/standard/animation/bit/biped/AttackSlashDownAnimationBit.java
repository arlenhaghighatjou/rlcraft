package goblinbob.mobends.standard.animation.bit.biped;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.math.SmoothOrientation;
import goblinbob.mobends.core.util.GUtil;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class AttackSlashDownAnimationBit extends AnimationBit<BipedEntityData<?>> {
   private float ticksPlayed;

   public AttackSlashDownAnimationBit() {
   }

   public void onPlay(BipedEntityData<?> data) {
      data.swordTrail.reset();
      this.ticksPlayed = 0.0F;
   }

   public void perform(BipedEntityData<?> data) {
      data.localOffset.slideToZero(0.3F);
      EntityLivingBase living = data.getEntity();
      EnumHandSide primaryHand = living.getPrimaryHand();
      boolean mainHandSwitch = primaryHand == EnumHandSide.RIGHT;
      float handDirMtp = mainHandSwitch ? 1.0F : -1.0F;
      IModelPart mainArm = mainHandSwitch ? data.rightArm : data.leftArm;
      IModelPart offArm = mainHandSwitch ? data.leftArm : data.rightArm;
      IModelPart mainForeArm = mainHandSwitch ? data.rightForeArm : data.leftForeArm;
      IModelPart offForeArm = mainHandSwitch ? data.leftForeArm : data.rightForeArm;
      SmoothOrientation mainItemRotation = mainHandSwitch ? data.renderRightItemRotation : data.renderLeftItemRotation;
      if (data.getTicksAfterAttack() < 4.0F && living.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword) {
         data.swordTrail.add(data);
      }

      float attackState = this.ticksPlayed / 10.0F;
      float armSwing = GUtil.clamp(attackState * 3.0F, 0.0F, 1.0F);
      float bodyRotationX = 20.0F - attackState * 20.0F;
      float bodyRotationY = (30.0F + 10.0F * attackState) * handDirMtp;
      data.body.rotation.setSmoothness(0.9F).orientX(bodyRotationX).orientY(bodyRotationY);
      data.head.rotation.setSmoothness(0.9F).orientX(MathHelper.wrapDegrees(((Float)data.headPitch.get()).floatValue()) - bodyRotationX).rotateY(MathHelper.wrapDegrees(((Float)data.headYaw.get()).floatValue()) - bodyRotationY);
      mainArm.getRotation().setSmoothness(0.3F).orientZ(60.0F * handDirMtp).rotateInstantY((-20.0F + armSwing * 70.0F) * handDirMtp);
      offArm.getRotation().setSmoothness(0.3F).orientZ(-80.0F * handDirMtp);
      mainForeArm.getRotation().setSmoothness(0.3F).orientX(-20.0F);
      offForeArm.getRotation().setSmoothness(0.3F).orientX(-60.0F);
      if (data.isStillHorizontally() && !living.isRiding()) {
         data.rightLeg.rotation.setSmoothness(0.3F).orientX(-30.0F).rotateZ(10.0F).rotateY(25.0F);
         data.leftLeg.rotation.setSmoothness(0.3F).orientX(-30.0F).rotateZ(-10.0F).rotateY(-25.0F);
         data.rightForeLeg.rotation.setSmoothness(0.3F).orientX(30.0F);
         data.leftForeLeg.rotation.setSmoothness(0.3F).orientX(30.0F);
         data.head.rotation.rotateY(-30.0F * handDirMtp);
         data.globalOffset.slideY(-2.0F);
         data.renderRotation.setSmoothness(0.3F).orientY(-30.0F * handDirMtp);
      }

      mainItemRotation.orientInstantX(90.0F);
      this.ticksPlayed += DataUpdateHandler.ticksPerFrame;
   }
}
