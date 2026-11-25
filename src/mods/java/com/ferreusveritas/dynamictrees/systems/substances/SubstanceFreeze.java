package com.ferreusveritas.dynamictrees.systems.substances;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.api.substances.ISubstanceEffect;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeFreezer;
import com.ferreusveritas.dynamictrees.trees.Species;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SubstanceFreeze implements ISubstanceEffect {
   public SubstanceFreeze() {
   }

   public ISubstanceEffect.Result apply(World world, BlockPos rootPos, BlockPos hitPos) {
      IBlockState rootyState = world.getBlockState(rootPos);
      BlockRooty dirt = TreeHelper.getRooty(rootyState);
      Species species = dirt.getSpecies(rootyState, world, rootPos);
      if (species != Species.NULLSPECIES && dirt != null) {
         if (world.isRemote) {
            TreeHelper.treeParticles(world, rootPos, EnumParticleTypes.FIREWORKS_SPARK, 8);
         } else {
            dirt.startAnalysis(world, rootPos, new MapSignal(new INodeInspector[]{new NodeFreezer(species)}));
            dirt.fertilize(world, rootPos, -15);
         }

         return ISubstanceEffect.Result.successful();
      } else {
         return ISubstanceEffect.Result.failure();
      }
   }

   public boolean update(World world, BlockPos rootPos, int deltaTicks, int fertility) {
      return false;
   }

   public String getName() {
      return "freeze";
   }

   public boolean isLingering() {
      return false;
   }
}
