package goblinbob.mobends.standard.animation.bit.biped;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.math.SmoothOrientation;
import goblinbob.mobends.core.util.GUtil;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.util.vector.Vector3f;

public class AttackWhirlSlashAnimationBit extends AnimationBit<BipedEntityData<?>> {
   public AttackWhirlSlashAnimationBit() {
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
      if (data.getTicksAfterAttack() < 0.5F) {
         data.swordTrail.reset();
      }

      if (living.getHeldItem(EnumHand.MAIN_HAND) != null) {
         data.swordTrail.add(data);
      }

      float attackState = data.getTicksAfterAttack() / 10.0F;
      float armSwing = attackState * 2.0F;
      armSwing = Math.min(armSwing, 1.0F);
      float var5 = GUtil.clamp(attackState * 1.6F, 0.0F, 1.0F);
      Vector3f bodyRot = new Vector3f(0.0F, 0.0F, 0.0F);
      bodyRot.x = 20.0F - attackState * 20.0F;
      bodyRot.y = 20.0F * attackState * handDirMtp;
      data.body.rotation.setSmoothness(0.9F).orientX(bodyRot.x).orientY(bodyRot.y);
      data.head.rotation.orientX(MathHelper.wrapDegrees(((Float)data.headPitch.get()).floatValue()) - bodyRot.x).rotateY(MathHelper.wrapDegrees(((Float)data.headYaw.get()).floatValue()) - bodyRot.y - 30.0F * handDirMtp);
      offArm.getRotation().setSmoothness(0.3F).orientZ(20.0F * handDirMtp);
      offArm.getRotation().setSmoothness(0.3F).orientZ(-80.0F * handDirMtp);
      mainArm.getRotation().setSmoothness(0.3F).orientZ(-(-10.0F - var5 * 120.0F) * handDirMtp).rotateInstantY((-20.0F + armSwing * 70.0F) * handDirMtp);
      mainForeArm.getRotation().setSmoothness(0.3F).orientX(-20.0F);
      offForeArm.getRotation().setSmoothness(0.3F).orientX(-60.0F);
      if (data.isStillHorizontally()) {
         data.rightLeg.rotation.setSmoothness(0.3F).orientX(-30.0F).rotateZ(10.0F).rotateY(25.0F);
         data.leftLeg.rotation.setSmoothness(0.3F).orientX(-30.0F).rotateZ(-10.0F).rotateY(-25.0F);
         data.rightForeLeg.rotation.setSmoothness(0.3F).orientX(30.0F);
         data.leftForeLeg.rotation.setSmoothness(0.3F).orientX(30.0F);
      }

      data.globalOffset.slideY(-2.0F);
      mainItemRotation.setSmoothness(0.9F).orientX(90.0F * attackState);
      float renderRotationY = 30.0F + 360.0F * var5;
      data.renderRotation.orientInstantY(MathHelper.wrapDegrees(-renderRotationY * handDirMtp));
   }
}
