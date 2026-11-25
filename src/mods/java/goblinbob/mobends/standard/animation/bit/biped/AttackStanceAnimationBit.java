package goblinbob.mobends.standard.animation.bit.biped;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.math.SmoothOrientation;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class AttackStanceAnimationBit extends AnimationBit<BipedEntityData<?>> {
   protected final float PI = 3.1415927F;
   protected final float kneelDuration = 0.15F;
   protected final float legSpreadSpeed = 0.1F;
   protected float legSpreadAnimation = 0.0F;

   public AttackStanceAnimationBit() {
   }

   public void onPlay(BipedEntityData<?> entityData) {
      this.legSpreadAnimation = 0.0F;
   }

   public void perform(BipedEntityData<?> data) {
      EntityLivingBase entity = data.getEntity();
      EnumHandSide primaryHand = entity.getPrimaryHand();
      boolean mainHandSwitch = primaryHand == EnumHandSide.RIGHT;
      float handDirMtp = mainHandSwitch ? 1.0F : -1.0F;
      IModelPart mainArm = mainHandSwitch ? data.rightArm : data.leftArm;
      IModelPart offArm = mainHandSwitch ? data.leftArm : data.rightArm;
      IModelPart mainForeArm = mainHandSwitch ? data.rightForeArm : data.leftForeArm;
      IModelPart offForeArm = mainHandSwitch ? data.leftForeArm : data.rightForeArm;
      SmoothOrientation mainItemRotation = mainHandSwitch ? data.renderRightItemRotation : data.renderLeftItemRotation;
      float breath0 = (float)Math.sin((double)DataUpdateHandler.getTicks() / 5.0D);
      float breath1 = (float)Math.cos((double)DataUpdateHandler.getTicks() / 5.7D);
      data.renderRotation.setSmoothness(0.3F).orientY(-30.0F * handDirMtp);
      float bodyRotationX = 20.0F + breath0 * 2.0F;
      data.body.rotation.setSmoothness(0.3F).orientX(bodyRotationX);
      data.head.rotation.rotateY(-30.0F * handDirMtp);
      data.head.rotation.rotateX(-bodyRotationX);
      data.rightLeg.rotation.setSmoothness(0.3F).orientX(-30.0F).rotateZ(10.0F).rotateY(25.0F);
      data.leftLeg.rotation.setSmoothness(0.3F).orientX(-30.0F).rotateZ(-10.0F).rotateY(-25.0F);
      data.rightForeLeg.rotation.setSmoothness(0.3F).orientX(30.0F);
      data.leftForeLeg.rotation.setSmoothness(0.3F).orientX(30.0F);
      mainArm.getRotation().setSmoothness(0.3F).orientZ(60.0F * handDirMtp + breath0 * 5.0F).rotateY(breath1 * 5.0F);
      offArm.getRotation().setSmoothness(0.3F).orientZ(-60.0F * handDirMtp + breath1 * 5.0F);
      mainForeArm.getRotation().setSmoothness(0.3F).orientX(-20.0F);
      offForeArm.getRotation().setSmoothness(0.3F).orientX(-60.0F);
      mainItemRotation.setSmoothness(0.3F).orientX(65.0F);
      data.globalOffset.slideY(-2.0F);
      float touchdown = Math.min(data.getTicksAfterTouchdown() * 0.15F, 1.0F);
      if (touchdown < 1.0F) {
         data.body.rotation.setSmoothness(1.0F);
         data.body.rotation.orientX(5.0F * (1.0F - touchdown) + 15.0F);
         data.globalOffset.setY(-MathHelper.sin(touchdown * 3.1415927F) * 2.0F - 2.0F);
      }

   }
}
