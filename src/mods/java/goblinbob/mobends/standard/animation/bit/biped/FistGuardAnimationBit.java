package goblinbob.mobends.standard.animation.bit.biped;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;

public class FistGuardAnimationBit extends AnimationBit<BipedEntityData<?>> {
   private static final String[] ACTIONS = new String[]{"fist_guard"};

   public FistGuardAnimationBit() {
   }

   public String[] getActions(BipedEntityData<?> entityData) {
      return ACTIONS;
   }

   public void perform(BipedEntityData<?> data) {
      EntityLivingBase living = data.getEntity();
      EnumHandSide primaryHand = living.getPrimaryHand();
      boolean mainHandSwitch = primaryHand == EnumHandSide.RIGHT;
      float handDirMtp = mainHandSwitch ? 1.0F : -1.0F;
      if (data.isStillHorizontally()) {
         data.globalOffset.slideY(-2.0F);
         data.renderRotation.setSmoothness(0.3F).orientY(-20.0F * handDirMtp);
         data.rightArm.rotation.setSmoothness(0.3F).orientX(-90.0F).rotateZ(20.0F);
         data.rightForeArm.rotation.setSmoothness(0.3F).orientX(-80.0F);
         data.leftArm.rotation.setSmoothness(0.3F).orientX(-90.0F).rotateZ(-20.0F);
         data.leftForeArm.rotation.setSmoothness(0.3F).orientX(-80.0F);
         data.body.rotation.rotateX(10.0F);
         data.rightLeg.rotation.setSmoothness(0.3F).orientX(-30.0F).rotateZ(10.0F);
         data.leftLeg.rotation.setSmoothness(0.3F).orientX(-30.0F).rotateY(-25.0F).rotateZ(-10.0F);
         data.rightForeLeg.rotation.setSmoothness(0.3F).orientX(30.0F);
         data.leftForeLeg.rotation.setSmoothness(0.3F).orientX(30.0F);
         data.head.rotation.rotateX(-10.0F);
         data.head.rotation.rotateY(-20.0F * handDirMtp);
      }
   }
}
