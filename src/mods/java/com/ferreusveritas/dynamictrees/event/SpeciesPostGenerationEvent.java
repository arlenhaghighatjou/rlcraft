package com.ferreusveritas.dynamictrees.event;

import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SpeciesPostGenerationEvent extends Event {
   private final World world;
   private final Species species;
   private final BlockPos rootPos;
   private final List<BlockPos> endPoints;
   private final SafeChunkBounds safeBounds;
   private final IBlockState initialDirtState;

   public SpeciesPostGenerationEvent(World world, Species species, BlockPos rootPos, List<BlockPos> endPoints, SafeChunkBounds safeBounds, IBlockState initialDirtState) {
      this.world = world;
      this.species = species;
      this.rootPos = rootPos;
      this.endPoints = endPoints;
      this.safeBounds = safeBounds;
      this.initialDirtState = initialDirtState;
   }

   public World getWorld() {
      return this.world;
   }

   public Species getSpecies() {
      return this.species;
   }

   public BlockPos getRootPos() {
      return this.rootPos;
   }

   public List<BlockPos> getEndPoints() {
      return this.endPoints;
   }

   public SafeChunkBounds getSafeBounds() {
      return this.safeBounds;
   }

   public IBlockState getInitialDirtState() {
      return this.initialDirtState;
   }
}
