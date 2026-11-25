package com.github.alexthe666.iceandfire.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.world.World;

public class PathNavigateExperimentalGround extends PathNavigateGround {
   public PathNavigateExperimentalGround(EntityLiving entityDragonBase, World worldIn) {
      super(entityDragonBase, worldIn);
   }

   protected PathFinder getPathFinder() {
      this.nodeProcessor = new ExperimentalWalkNodeProcessor();
      this.nodeProcessor.setCanEnterDoors(true);
      return new PathFinder(this.nodeProcessor);
   }
}
