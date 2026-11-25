package com.ferreusveritas.dynamictrees.command;

import com.ferreusveritas.dynamictrees.util.ChunkTreeHelper;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class CommandPurgeTrees extends CommandChunkBased {
   public static final String PURGETREES = "purgeTrees";

   public CommandPurgeTrees() {
   }

   public String getName() {
      return "purgeTrees";
   }

   public String messageToThrow() {
      return "commands.dynamictrees.purgetrees.usage";
   }

   void processChunk(World world, ChunkPos cPos, int radius) {
      ChunkTreeHelper.removeAllBranchesFromChunk(world, cPos, radius);
   }
}
