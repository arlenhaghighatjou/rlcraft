package com.ferreusveritas.dynamictrees.systems.substances;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.substances.ISubstanceEffect;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SubstanceDeplete implements ISubstanceEffect {
   int amount;

   public SubstanceDeplete() {
   }

   public ISubstanceEffect.Result apply(World world, BlockPos rootPos, BlockPos hitPos) {
      BlockRooty dirt = TreeHelper.getRooty(world.getBlockState(rootPos));
      if (dirt.fertilize(world, rootPos, -this.amount)) {
         TreeHelper.treeParticles(world, rootPos, EnumParticleTypes.VILLAGER_ANGRY, 8);
         return ISubstanceEffect.Result.successful();
      } else {
         return ISubstanceEffect.Result.failure("substance.dynamictrees.deplete.error.already_depleted");
      }
   }

   public boolean update(World world, BlockPos rootPos, int deltaTicks, int fertility) {
      return false;
   }

   public String getName() {
      return "deplete";
   }

   public SubstanceDeplete setAmount(int amount) {
      this.amount = amount;
      return this;
   }

   public boolean isLingering() {
      return false;
   }
}
