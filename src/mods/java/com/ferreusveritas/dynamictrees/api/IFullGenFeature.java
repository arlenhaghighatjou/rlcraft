package com.ferreusveritas.dynamictrees.api;

import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public interface IFullGenFeature extends IGenFeature {
   boolean generate(World var1, BlockPos var2, Species var3, Biome var4, EaglercraftRandom var5, int var6, SafeChunkBounds var7);
}
