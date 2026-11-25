package goblinbob.mobends.standard.animation.controller;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.animation.controller.IAnimationController;
import goblinbob.mobends.core.animation.layer.HardAnimationLayer;
import goblinbob.mobends.standard.animation.bit.biped.AttackSlashInwardAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.JumpAnimationBit;
import goblinbob.mobends.standard.animation.bit.pigzombie.StandAnimationBit;
import goblinbob.mobends.standard.animation.bit.pigzombie.WalkAnimationBit;
import goblinbob.mobends.standard.data.BipedEntityData;
import goblinbob.mobends.standard.data.PigZombieData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.monster.EntityPigZombie;

public class PigZombieController implements IAnimationController<PigZombieData> {
   protected HardAnimationLayer<BipedEntityData<EntityPigZombie>> layerBase = new HardAnimationLayer<BipedEntityData<EntityPigZombie>>();
   protected HardAnimationLayer<BipedEntityData<?>> layerAction = new HardAnimationLayer<BipedEntityData<?>>();
   protected AnimationBit<? extends BipedEntityData<EntityPigZombie>> bitStand = new StandAnimationBit();
   protected AnimationBit<? extends BipedEntityData<EntityPigZombie>> bitWalk = new WalkAnimationBit();
   protected AnimationBit<? extends BipedEntityData<EntityPigZombie>> bitJump = new JumpAnimationBit<BipedEntityData<EntityPigZombie>>();
   protected AttackSlashInwardAnimationBit bitAttack = new AttackSlashInwardAnimationBit();

   public PigZombieController() {
   }

   public Collection<String> perform(PigZombieData pigZombieData) {
      EntityPigZombie pigZombie = (EntityPigZombie)pigZombieData.getEntity();
      if (pigZombieData.isOnGround() && pigZombieData.getTicksAfterTouchdown() >= 1.0F) {
         if (pigZombieData.isStillHorizontally()) {
            this.layerBase.playOrContinueBit(this.bitStand, pigZombieData);
         } else {
            this.layerBase.playOrContinueBit(this.bitWalk, pigZombieData);
         }
      } else {
         this.layerBase.playOrContinueBit(this.bitJump, pigZombieData);
      }

      if (pigZombie.swingProgress > 0.0F) {
         this.layerAction.playOrContinueBit(this.bitAttack, pigZombieData);
      } else {
         this.layerAction.clearAnimation();
      }

      List<String> actions = new ArrayList();
      this.layerBase.perform(pigZombieData, actions);
      this.layerAction.perform(pigZombieData, actions);
      return actions;
   }
}
