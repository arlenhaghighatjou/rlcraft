package goblinbob.mobends.standard.animation.bit.biped;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.client.model.ModelPartTransform;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.util.EnumHandSide;

public class ShieldAnimationBit extends AnimationBit<BipedEntityData<?>> {
   private static final String[] ACTIONS = new String[]{"shield"};
   protected final EnumHandSide actionHand;
   protected float bringUpAnimation;

   public ShieldAnimationBit(EnumHandSide handSide) {
      this.actionHand = handSide;
   }

   public String[] getActions(BipedEntityData<?> data) {
      return ACTIONS;
   }

   public void onPlay(BipedEntityData<?> data) {
      this.bringUpAnimation = 0.0F;
   }

   public void perform(BipedEntityData<?> data) {
      boolean mainHandSwitch = this.actionHand == EnumHandSide.RIGHT;
      float handDirMtp = mainHandSwitch ? 1.0F : -1.0F;
      ModelPartTransform mainArm = mainHandSwitch ? data.rightArm : data.leftArm;
      ModelPartTransform mainForeArm = mainHandSwitch ? data.rightForeArm : data.leftForeArm;
      if (this.bringUpAnimation < 1.0F) {
         this.bringUpAnimation += DataUpdateHandler.ticksPerFrame * 0.7F;
         this.bringUpAnimation = Math.min(this.bringUpAnimation, 1.0F);
      }

      mainArm.rotation.orientX(this.bringUpAnimation * 0.0F).rotateY(-45.0F * this.bringUpAnimation * handDirMtp);
      mainForeArm.rotation.orientX(this.bringUpAnimation * -45.0F);
   }
}
