package goblinbob.mobends.standard.animation.controller;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.animation.controller.IAnimationController;
import goblinbob.mobends.core.animation.layer.HardAnimationLayer;
import goblinbob.mobends.standard.animation.bit.biped.JumpAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.StandAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.WalkAnimationBit;
import goblinbob.mobends.standard.animation.bit.zombie_base.ZombieLeanAnimationBit;
import goblinbob.mobends.standard.animation.bit.zombie_base.ZombieStumblingAnimationBit;
import goblinbob.mobends.standard.data.ZombieVillagerData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ZombieVillagerController implements IAnimationController<ZombieVillagerData> {
   protected HardAnimationLayer<ZombieVillagerData> layerBase = new HardAnimationLayer<ZombieVillagerData>();
   protected HardAnimationLayer<ZombieVillagerData> layerSet = new HardAnimationLayer<ZombieVillagerData>();
   protected AnimationBit<ZombieVillagerData> bitStand = new StandAnimationBit<ZombieVillagerData>();
   protected AnimationBit<ZombieVillagerData> bitWalk = new WalkAnimationBit<ZombieVillagerData>();
   protected AnimationBit<ZombieVillagerData> bitJump = new JumpAnimationBit<ZombieVillagerData>();
   protected AnimationBit<ZombieVillagerData>[] bitAnimationSet = new AnimationBit[]{new ZombieLeanAnimationBit(), new ZombieStumblingAnimationBit()};

   public ZombieVillagerController() {
   }

   public Collection<String> perform(ZombieVillagerData zombieData) {
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
