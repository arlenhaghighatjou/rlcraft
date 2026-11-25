package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

public class EntityAIBlockInfest extends EntityAIBase {
   private final EntityParasiteBase parentEntity;
   private int ticks = 0;
   private int stage;

   public EntityAIBlockInfest(EntityParasiteBase ghast, int stage) {
      this.parentEntity = ghast;
      this.stage = stage;
   }

   public boolean shouldExecute() {
      return true;
   }

   public void startExecuting() {
   }

   public void resetTask() {
   }

   public void updateTask() {
      ++this.ticks;
      if (this.ticks > 200) {
         this.ticks = 0;
         World world = this.parentEntity.world;
         Block block = world.getBlockState(this.parentEntity.getPosition()).getBlock();
         if (block instanceof IMetaName) {
            return;
         }

         ParasiteEventWorld.canInfestBlock(world, this.parentEntity.getPosition(), new Random(), this.stage, true);
      }

   }
}
