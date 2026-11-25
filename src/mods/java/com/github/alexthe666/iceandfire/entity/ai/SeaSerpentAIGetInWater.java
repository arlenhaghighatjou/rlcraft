package com.github.alexthe666.iceandfire.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.math.Vec3d;

public class SeaSerpentAIGetInWater extends AquaticAIGetInWater {
   public SeaSerpentAIGetInWater(EntityCreature theCreatureIn, double movementSpeedIn) {
      super(theCreatureIn, movementSpeedIn);
   }

   public boolean isAttackerInWater() {
      return false;
   }

   @Nullable
   public Vec3d findPossibleShelter() {
      return this.findPossibleShelter(30, 20);
   }
}
