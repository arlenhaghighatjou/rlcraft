package com.github.fnar.roguelike.worldgen;

import com.github.fnar.minecraft.WorldEditor1_12;
import greymerk.roguelike.dungeon.Dungeon;
import greymerk.roguelike.worldgen.WorldEditor;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class DungeonGenerator1_12 implements IWorldGenerator {
   public DungeonGenerator1_12() {
   }

   public void generate(EaglercraftRandom random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      WorldEditor editor = new WorldEditor1_12(world);
      Dungeon dungeon = new Dungeon(editor);
      dungeon.spawnInChunk(random, chunkX, chunkZ);
   }
}
