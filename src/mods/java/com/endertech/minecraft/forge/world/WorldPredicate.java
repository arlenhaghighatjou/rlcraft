package com.endertech.minecraft.forge.world;

import com.endertech.minecraft.forge.blocks.BlockState;
import java.util.Objects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface WorldPredicate {
   boolean test(World var1, BlockPos var2, BlockState var3);

   default WorldPredicate and(WorldPredicate other) {
      Objects.requireNonNull(other);
      return (world, pos, state) -> {
         return this.test(world, pos, state) && other.test(world, pos, state);
      };
   }

   default WorldPredicate negate() {
      return (world, pos, state) -> {
         return !this.test(world, pos, state);
      };
   }

   default WorldPredicate or(WorldPredicate other) {
      Objects.requireNonNull(other);
      return (world, pos, state) -> {
         return this.test(world, pos, state) || other.test(world, pos, state);
      };
   }
}
