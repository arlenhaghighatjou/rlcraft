package org.jwaresoftware.mcmods.lib;

import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEnd;
import net.minecraft.world.biome.BiomeHell;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

@ParametersAreNonnullByDefault
public final class Locations {
   public static final boolean isBiomeLike(Biome biome, Biome baseline, Type... plus) {
      Set<Type> biomeTypes = BiomeDictionary.getTypes(biome);

      for(Type wantedType : BiomeDictionary.getTypes(baseline)) {
         if (!biomeTypes.contains(wantedType)) {
            return false;
         }
      }

      if (plus != null) {
         for(Type wantedType : plus) {
            if (!biomeTypes.contains(wantedType)) {
               return false;
            }
         }
      }

      return true;
   }

   public static final boolean isOverworld(@Nullable World world) {
      return world != null && world.provider.isSurfaceWorld();
   }

   public static final boolean inTheOverworld(@Nullable World world, @Nonnull Entity wrt) {
      return isOverworld(world != null ? world : wrt.getEntityWorld());
   }

   public static final boolean inTheNether(@Nullable Biome biome, @Nonnull World world, @Nonnull BlockPos blockpos) {
      if (biome == null) {
         biome = world.getBiome(blockpos);
      }

      return biome instanceof BiomeHell || BiomeDictionary.hasType(biome, Type.NETHER);
   }

   public static final boolean inTheNether(@Nullable World world, @Nonnull Entity wrt) {
      return inTheNether((Biome)null, world != null ? world : wrt.getEntityWorld(), wrt.getPosition());
   }

   public static final boolean inTheEnd(@Nullable Biome biome, @Nonnull World world, @Nonnull BlockPos blockpos) {
      if (biome == null) {
         biome = world.getBiome(blockpos);
      }

      return biome instanceof BiomeEnd || BiomeDictionary.hasType(biome, Type.END);
   }

   public static final boolean inTheEnd(@Nullable World world, @Nonnull EntityPlayer player) {
      return inTheEnd((Biome)null, world != null ? world : player.getEntityWorld(), player.getPosition());
   }

   public static final boolean isLikelyRealPosition(@Nullable IBlockAccess aworld, BlockPos blockpos) {
      return !BlockPos.ORIGIN.equals(blockpos) && aworld instanceof World && ((World)aworld).isBlockLoaded(blockpos);
   }

   public static final boolean isGrowableYPos(IBlockAccess aworld, BlockPos blockpos) {
      return blockpos.getY() >= 15 && blockpos.getY() < SharedGlue.WORLD_HEIGHT();
   }

   private Locations() {
   }

   public static final void init() {
   }
}
