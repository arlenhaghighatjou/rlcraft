package goblinbob.mobends.standard.animation.bit.pigzombie;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.math.SmoothOrientation;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import org.lwjgl.util.vector.Vector3f;

public class AttackSlashInwardAnimationBit extends AnimationBit<BipedEntityData<?>> {
   private static final String[] ACTIONS = new String[]{"attack", "attack_slash_inward"};

   public AttackSlashInwardAnimationBit() {
   }

   public String[] getActions(BipedEntityData<?> entityData) {
      return ACTIONS;
   }

   public void onPlay(BipedEntityData<?> data) {
      data.swordTrail.reset();
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

      float attackState = data.getTicksAfterAttack() / 10.0F;
      float armSwing = attackState * 3.0F;
      armSwing = Math.min(armSwing, 1.0F);
      Vector3f bodyRot = new Vector3f(0.0F, 0.0F, 0.0F);
      bodyRot.x = 20.0F - armSwing * 20.0F;
      bodyRot.y = -70.0F * armSwing * handDirMtp;
      data.body.rotation.setSmoothness(0.9F).orientX(bodyRot.x).orientY(bodyRot.y);
      data.head.rotation.setSmoothness(0.9F).orientX(((Float)data.headPitch.get()).floatValue() - bodyRot.x).rotateY(((Float)data.headYaw.get()).floatValue() - bodyRot.y);
      mainArm.getRotation().setSmoothness(0.9F).orientZ(90.0F * handDirMtp).rotateY((60.0F - armSwing * 180.0F) * handDirMtp);
      offArm.getRotation().setSmoothness(0.3F).orientZ(-20.0F * handDirMtp);
      mainForeArm.getRotation().setSmoothness(0.3F).orientX(-10.0F);
      offForeArm.getRotation().setSmoothness(0.3F).orientX(-60.0F);
      if (data.isStillHorizontally() && !living.isRiding()) {
         data.rightLeg.rotation.orientZ(5.0F).rotateY(15.0F).rotateX(-20.0F);
         data.leftLeg.rotation.orientZ(-5.0F).rotateY(-15.0F).rotateX(-20.0F);
         data.rightForeLeg.rotation.orientX(25.0F);
         data.renderRotation.setSmoothness(0.3F).orientY(0.0F * handDirMtp);
         data.globalOffset.slideY(-1.0F);
      }

      mainItemRotation.setSmoothness(0.9F).orientInstantX(50.0F);
   }
}
