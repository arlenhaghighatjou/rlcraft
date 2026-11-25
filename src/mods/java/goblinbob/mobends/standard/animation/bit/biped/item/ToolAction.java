package goblinbob.mobends.standard.animation.bit.biped.item;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.model.ModelPartTransform;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class ToolAction extends AnimationBit<BipedEntityData<?>> {
   protected final EnumHandSide actionHand;

   public ToolAction(EnumHandSide actionHand) {
      this.actionHand = actionHand;
   }

   public void perform(BipedEntityData<?> data) {
      EntityLivingBase entity = data.getEntity();
      if (entity.isSwingInProgress) {
         float headPitch = ((Float)data.headPitch.get()).floatValue();
         float headYaw = ((Float)data.headYaw.get()).floatValue();
         boolean mainHandSwitch = this.actionHand == EnumHandSide.RIGHT;
         float sideMultiplier = this.actionHand == EnumHandSide.RIGHT ? 1.0F : -1.0F;
         ModelPartTransform mainArm = mainHandSwitch ? data.rightArm : data.leftArm;
         if (mainHandSwitch) {
            ModelPartTransform var10000 = data.leftArm;
         } else {
            ModelPartTransform var14 = data.rightArm;
         }

         if (mainHandSwitch) {
            ModelPartTransform var15 = data.rightForeArm;
         } else {
            ModelPartTransform var16 = data.leftForeArm;
         }

         if (mainHandSwitch) {
            ModelPartTransform var17 = data.leftForeArm;
         } else {
            ModelPartTransform var18 = data.rightForeArm;
         }

         data.localOffset.slideToZero(0.3F);
         data.centerRotation.setSmoothness(0.3F).orientZero();
         float swingProgress = ((Float)data.swingProgress.get()).floatValue();
         float bodyYaw = MathHelper.sin(MathHelper.sqrt(swingProgress) * 6.2831855F) * 30.0F * sideMultiplier;
         data.body.rotation.setSmoothness(0.8F).orientY(bodyYaw);
         float bodyPitch = 0.0F;
         if (data.getEntity().isSneaking()) {
            data.body.rotation.rotateX(20.0F);
            bodyPitch = 20.0F;
         }

         data.head.rotation.setSmoothness(0.8F).orientX(headPitch - bodyPitch).rotateY(headYaw - bodyYaw);
         mainArm.rotation.orientInstantX(MathHelper.sin(MathHelper.sqrt(swingProgress) * 6.2831855F) * 50.0F - 30.0F);
         mainArm.rotation.localRotateZ(MathHelper.cos(MathHelper.sqrt(swingProgress) * 6.2831855F) * -20.0F + 10.0F).finish();
      }
   }
}
