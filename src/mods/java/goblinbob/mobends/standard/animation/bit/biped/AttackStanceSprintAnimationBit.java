package goblinbob.mobends.standard.animation.bit.biped;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.client.model.ModelPartTransform;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;

public class AttackStanceSprintAnimationBit extends AnimationBit<BipedEntityData<?>> {
   public AttackStanceSprintAnimationBit() {
   }

   public void perform(BipedEntityData<?> data) {
      data.localOffset.slideToZero(0.3F);
      EntityLivingBase living = data.getEntity();
      EnumHandSide primaryHand = living.getPrimaryHand();
      boolean mainHandSwitch = primaryHand == EnumHandSide.RIGHT;
      float handDirMtp = mainHandSwitch ? 1.0F : -1.0F;
      IModelPart mainArm = mainHandSwitch ? data.rightArm : data.leftArm;
      IModelPart offArm = mainHandSwitch ? data.leftArm : data.rightArm;
      if (mainHandSwitch) {
         ModelPartTransform var10000 = data.rightForeArm;
      } else {
         ModelPartTransform var10 = data.leftForeArm;
      }

      if (mainHandSwitch) {
         ModelPartTransform var11 = data.leftForeArm;
      } else {
         ModelPartTransform var12 = data.rightForeArm;
      }

      if (living.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword) {
         data.swordTrail.add(data, 0.0F, 0.0F, -10.0F);
      }

      data.body.rotation.rotateY(20.0F * handDirMtp);
      data.head.rotation.rotateY(-20.0F * handDirMtp);
      mainArm.getRotation().orientZ(60.0F * handDirMtp);
      mainArm.getRotation().rotateY(60.0F * handDirMtp);
      offArm.getRotation().rotateZ(-30.0F * handDirMtp);
      if (mainHandSwitch) {
         data.renderRightItemRotation.setSmoothness(0.3F).orientX(45.0F);
      } else {
         data.renderLeftItemRotation.setSmoothness(0.3F).orientX(45.0F);
      }

   }
}
