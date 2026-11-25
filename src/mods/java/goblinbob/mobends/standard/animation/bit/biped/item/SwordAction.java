package goblinbob.mobends.standard.animation.bit.biped.item;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.animation.layer.HardAnimationLayer;
import goblinbob.mobends.standard.animation.bit.biped.AttackSlashDownAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.AttackSlashInwardAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.AttackSlashOutwardAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.AttackSlashUpAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.AttackStanceAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.AttackStanceSprintAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.AttackWhirlSlashAnimationBit;
import goblinbob.mobends.standard.data.BipedEntityData;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;

public class SwordAction extends AnimationBit<BipedEntityData<?>> {
   protected final HardAnimationLayer<BipedEntityData<?>> layerBase = new HardAnimationLayer<BipedEntityData<?>>();
   protected final AttackStanceAnimationBit bitAttackStance = new AttackStanceAnimationBit();
   protected final AttackStanceSprintAnimationBit bitAttackStanceSprint = new AttackStanceSprintAnimationBit();
   protected float lastTicksAfterAttack = 0.0F;
   protected int moveId = 0;
   private static final List<AnimationBit<BipedEntityData<?>>> bits = Arrays.asList(new AttackSlashUpAnimationBit(), new AttackSlashDownAnimationBit(), new AttackSlashInwardAnimationBit(), new AttackSlashOutwardAnimationBit(), new AttackWhirlSlashAnimationBit());

   public SwordAction(EnumHandSide handSide) {
   }

   private void nextMove(BipedEntityData<?> entityData) {
      AnimationBit<BipedEntityData<?>> bit = (AnimationBit)bits.get(this.moveId);
      if (bit != null) {
         this.layerBase.playBit(bit, entityData);
      } else {
         this.layerBase.clearAnimation();
      }

      this.moveId = (this.moveId + 1) % bits.size();
   }

   public void perform(BipedEntityData<?> entityData) {
      float ticksAfterAttack = entityData.getTicksAfterAttack();
      if (ticksAfterAttack < this.lastTicksAfterAttack) {
         this.nextMove(entityData);
      }

      this.lastTicksAfterAttack = ticksAfterAttack;
      EntityLivingBase entity = entityData.getEntity();
      int comboClearTime = 20;
      if (ticksAfterAttack > (float)comboClearTime) {
         this.moveId = 0;
      }

      if (ticksAfterAttack >= 10.0F) {
         if (ticksAfterAttack < 60.0F && entityData.isOnGround()) {
            if (entity.isSprinting()) {
               this.layerBase.playOrContinueBit(this.bitAttackStanceSprint, entityData);
            } else if (entityData.isStillHorizontally()) {
               this.layerBase.playOrContinueBit(this.bitAttackStance, entityData);
            } else {
               this.layerBase.clearAnimation();
            }
         } else {
            this.layerBase.clearAnimation();
         }
      }

      this.layerBase.perform(entityData);
   }
}
