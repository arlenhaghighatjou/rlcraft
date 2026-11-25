package com.ferreusveritas.dynamictrees.systems.substances;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.substances.ISubstanceEffect;
import com.ferreusveritas.dynamictrees.entities.EntityLingeringEffector;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SubstanceGrowth implements ISubstanceEffect {
   private final int pulses;
   private final int ticksPerPulse;
   private final int ticksPerParticlePulse;
   private final boolean fillFertility;
   private int pulseCount;

   public SubstanceGrowth() {
      this(-1, 24);
   }

   public SubstanceGrowth(int pulses, int ticksPerPulse) {
      this(pulses, ticksPerPulse, 8, true);
   }

   public SubstanceGrowth(int pulses, int ticksPerPulse, int ticksPerParticlePulse, boolean fillFertility) {
      this.pulses = pulses;
      this.ticksPerPulse = ticksPerPulse;
      this.ticksPerParticlePulse = ticksPerParticlePulse;
      this.fillFertility = fillFertility;
   }

   public ISubstanceEffect.Result apply(World world, BlockPos rootPos, BlockPos hitPos) {
      if (EntityLingeringEffector.treeHasEffectorForEffect(world, rootPos, this)) {
         return ISubstanceEffect.Result.failure("substance.dynamictrees.growth.error.already_has_effect");
      } else {
         if (this.fillFertility) {
            (new SubstanceFertilize()).setAmount(15).apply(world, rootPos, hitPos);
         }

         TreeHelper.treeParticles(world, rootPos, EnumParticleTypes.SPELL, 8);
         return ISubstanceEffect.Result.successful();
      }
   }

   public boolean update(World world, BlockPos rootPos, int deltaTicks, int fertility) {
      if (fertility > 0 && (this.pulses <= -1 || this.pulseCount < this.pulses)) {
         if (world.isRemote) {
            if (deltaTicks % this.ticksPerParticlePulse == 0) {
               TreeHelper.treeParticles(world, rootPos, EnumParticleTypes.SPELL, 2);
            }
         } else if (deltaTicks % this.ticksPerPulse == 0) {
            TreeHelper.growPulse(world, rootPos);
            ++this.pulseCount;
         }

         return true;
      } else {
         return false;
      }
   }

   public String getName() {
      return "growth";
   }

   public boolean isLingering() {
      return true;
   }
}
