package com.creativemd.creativecore.common.world;

import java.util.Collections;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;

public class ChunkGeneratorFake implements IChunkGenerator {
   public final World world;

   public ChunkGeneratorFake(World world) {
      this.world = world;
   }

   public Chunk generateChunk(int x, int z) {
      return new Chunk(this.world, x, z);
   }

   public void populate(int x, int z) {
   }

   public boolean generateStructures(Chunk chunkIn, int x, int z) {
      return false;
   }

   public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      return Collections.emptyList();
   }

   public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
      return position;
   }

   public void recreateStructures(Chunk chunkIn, int x, int z) {
   }

   public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
      return false;
   }
}
