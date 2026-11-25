package com.lycanitesmobs.core;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

public interface IWorldGenBase {
   void onWorldGen(EaglercraftRandom var1, int var2, int var3, World var4, IChunkGenerator var5, IChunkProvider var6);
}
