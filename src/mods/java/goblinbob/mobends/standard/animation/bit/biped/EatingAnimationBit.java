package goblinbob.mobends.standard.animation.bit.biped;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.client.model.ModelPartTransform;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class EatingAnimationBit extends AnimationBit<BipedEntityData<?>> {
   protected final EnumHandSide actionHand;
   protected float bringUpAnimation;

   public EatingAnimationBit(EnumHandSide handSide) {
      this.actionHand = handSide;
   }

   public void onPlay(BipedEntityData<?> data) {
      this.bringUpAnimation = 0.0F;
   }

   public void perform(BipedEntityData<?> data) {
      float ticks = DataUpdateHandler.getTicks();
      boolean mainHandSwitch = this.actionHand == EnumHandSide.RIGHT;
      float handDirMtp = mainHandSwitch ? 1.0F : -1.0F;
      ModelPartTransform mainArm = mainHandSwitch ? data.rightArm : data.leftArm;
      ModelPartTransform mainForeArm = mainHandSwitch ? data.rightForeArm : data.leftForeArm;
      if (this.bringUpAnimation < 1.0F) {
         this.bringUpAnimation += DataUpdateHandler.ticksPerFrame * 0.15F;
         this.bringUpAnimation = Math.min(this.bringUpAnimation, 1.0F);
      } else {
         float wiggle = MathHelper.cos(ticks * 1.0F);
         data.head.rotation.orientX(wiggle * 5.0F).rotateY(15.0F * handDirMtp);
      }

      mainArm.rotation.orientX(this.bringUpAnimation * -80.0F).rotateZ(45.0F * this.bringUpAnimation * handDirMtp);
      mainForeArm.rotation.orientX(this.bringUpAnimation * -45.0F);
   }
}
