package goblinbob.mobends.standard.animation.bit.biped.item;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.animation.layer.HardAnimationLayer;
import goblinbob.mobends.standard.animation.bit.biped.FistGuardAnimationBit;
import goblinbob.mobends.standard.animation.bit.player.PunchAnimationBit;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.util.EnumHandSide;

public class PunchingAction extends AnimationBit<BipedEntityData<?>> {
   protected final HardAnimationLayer<BipedEntityData<?>> layerBase = new HardAnimationLayer<BipedEntityData<?>>();
   protected final FistGuardAnimationBit bitFistGuard = new FistGuardAnimationBit();
   protected final PunchAnimationBit bitPunchLeft = new PunchAnimationBit(EnumHandSide.LEFT);
   protected final PunchAnimationBit bitPunchRight = new PunchAnimationBit(EnumHandSide.RIGHT);
   protected EnumHandSide punchingFist = EnumHandSide.LEFT;
   protected float lastTicksAfterAttack = 0.0F;

   public PunchingAction(EnumHandSide handSide) {
   }

   public void perform(BipedEntityData<?> entityData) {
      float ticksAfterAttack = entityData.getTicksAfterAttack();
      if (ticksAfterAttack < this.lastTicksAfterAttack) {
         this.punchingFist = this.punchingFist == EnumHandSide.LEFT ? EnumHandSide.RIGHT : EnumHandSide.LEFT;
      }

      this.lastTicksAfterAttack = ticksAfterAttack;
      if (ticksAfterAttack < 10.0F) {
         this.layerBase.playOrContinueBit(this.punchingFist == EnumHandSide.LEFT ? this.bitPunchLeft : this.bitPunchRight, entityData);
      } else if (ticksAfterAttack < 60.0F) {
         this.layerBase.playOrContinueBit(this.bitFistGuard, entityData);
      } else {
         this.layerBase.clearAnimation();
      }

      this.layerBase.perform(entityData);
   }
}
