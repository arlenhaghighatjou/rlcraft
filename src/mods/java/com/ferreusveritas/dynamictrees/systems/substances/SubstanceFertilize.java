package com.ferreusveritas.dynamictrees.systems.substances;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.substances.ISubstanceEffect;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SubstanceFertilize implements ISubstanceEffect {
   private int amount = 2;
   private boolean grow;
   private int pulses = 1;

   public SubstanceFertilize() {
   }

   public ISubstanceEffect.Result apply(World world, BlockPos rootPos, BlockPos hitPos) {
      BlockRooty dirt = TreeHelper.getRooty(world.getBlockState(rootPos));
      if ((dirt == null || !dirt.fertilize(world, rootPos, this.amount)) && !this.grow) {
         return ISubstanceEffect.Result.failure();
      } else {
         if (world.isRemote) {
            TreeHelper.treeParticles(world, rootPos, EnumParticleTypes.VILLAGER_HAPPY, 8);
         } else if (this.grow) {
            for(int i = 0; i < this.pulses; ++i) {
               TreeHelper.growPulse(world, rootPos);
            }
         }

         return ISubstanceEffect.Result.successful();
      }
   }

   public boolean update(World world, BlockPos rootPos, int deltaTicks, int fertility) {
      return false;
   }

   public String getName() {
      return "fertilize";
   }

   public SubstanceFertilize setAmount(int amount) {
      this.amount = amount;
      return this;
   }

   public SubstanceFertilize setGrow(boolean grow) {
      this.grow = grow;
      return this;
   }

   public SubstanceFertilize setPulses(int pulses) {
      this.pulses = pulses;
      return this;
   }

   public boolean isLingering() {
      return false;
   }
}
