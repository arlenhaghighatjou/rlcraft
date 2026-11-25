package com.github.fnar.minecraft;

import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.PositionInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class PositionInfo1_12 implements PositionInfo {
   private final World world;
   private final Coord pos;

   public PositionInfo1_12(World world, Coord pos) {
      this.world = world;
      this.pos = pos;
   }

   public int getDimension() {
      return this.world.provider.getDimension();
   }

   public Biome getBiome() {
      return this.world.getBiome(new BlockPos(this.pos.getX(), this.pos.getY(), this.pos.getZ()));
   }
}
