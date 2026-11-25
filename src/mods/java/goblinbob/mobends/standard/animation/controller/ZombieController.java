package goblinbob.mobends.standard.animation.controller;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.animation.controller.IAnimationController;
import goblinbob.mobends.core.animation.layer.HardAnimationLayer;
import goblinbob.mobends.standard.animation.bit.biped.JumpAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.StandAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.WalkAnimationBit;
import goblinbob.mobends.standard.animation.bit.zombie_base.ZombieLeanAnimationBit;
import goblinbob.mobends.standard.animation.bit.zombie_base.ZombieStumblingAnimationBit;
import goblinbob.mobends.standard.data.ZombieData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ZombieController implements IAnimationController<ZombieData> {
   protected HardAnimationLayer<ZombieData> layerBase = new HardAnimationLayer<ZombieData>();
   protected HardAnimationLayer<ZombieData> layerSet = new HardAnimationLayer<ZombieData>();
   protected AnimationBit<ZombieData> bitStand = new StandAnimationBit<ZombieData>();
   protected AnimationBit<ZombieData> bitWalk = new WalkAnimationBit<ZombieData>();
   protected AnimationBit<ZombieData> bitJump = new JumpAnimationBit<ZombieData>();
   protected AnimationBit<ZombieData>[] bitAnimationSet = new AnimationBit[]{new ZombieLeanAnimationBit(), new ZombieStumblingAnimationBit()};

   public ZombieController() {
   }

   public Collection<String> perform(ZombieData zombieData) {
      if (zombieData.isOnGround() && zombieData.getTicksAfterTouchdown() >= 1.0F) {
         if (zombieData.isStillHorizontally()) {
            this.layerBase.playOrContinueBit(this.bitStand, zombieData);
         } else {
            this.layerBase.playOrContinueBit(this.bitWalk, zombieData);
         }
      } else {
         this.layerBase.playOrContinueBit(this.bitJump, zombieData);
      }

      this.layerSet.playOrContinueBit(this.bitAnimationSet[zombieData.getAnimationSet()], zombieData);
      List<String> actions = new ArrayList();
      this.layerBase.perform(zombieData, actions);
      this.layerSet.perform(zombieData, actions);
      return actions;
   }
}
