package org.orecruncher.dsurround.registry.biome;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.registry.IDataAccessor;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.lib.Color;
import org.orecruncher.lib.ReflectedField;
import org.orecruncher.lib.WorldUtils;
import org.orecruncher.lib.Color.ImmutableColor;
import org.orecruncher.lib.ReflectedField.FloatField;
import org.orecruncher.lib.ReflectedField.IntegerField;
import org.orecruncher.lib.ReflectedField.ObjectField;

@SideOnly(Side.CLIENT)
public final class BiomeUtil {
   private static final Color NO_COLOR = new ImmutableColor(1.0F, 1.0F, 1.0F);
   private static final ObjectField<Biome, String> biomeName = new ObjectField(Biome.class, "biomeName", "biomeName");
   private static final FloatField<Object> bopBiomeFogDensity = new FloatField("biomesoplenty.common.biome.BOPBiome", "fogDensity", (String)null);
   private static final IntegerField<Object> bopBiomeFogColor = new IntegerField("biomesoplenty.common.biome.BOPBiome", "fogColor", (String)null);
   private static final ObjectField<Type, Map<String, Type>> biomeType = new ObjectField(Type.class, "byName", (String)null);
   private static final Class<?> bopBiome = ReflectedField.resolveClass("biomesoplenty.common.biome.BOPBiome");

   public BiomeUtil() {
   }

   @Nonnull
   public static BiomeInfo getBiomeData(@Nonnull Biome biome) {
      IDataAccessor<BiomeInfo> accessor = (IDataAccessor)biome;
      BiomeInfo result = accessor.getData();
      if (result == null) {
         RegistryManager.BIOME.reload();
         result = accessor.getData();
      }

      if (result == null) {
         ModBase.log().warn("Unable to find configuration for biome [%s] (hc=%d)", new Object[]{biome.getRegistryName(), System.identityHashCode(biome)});
         result = RegistryManager.BIOME.WTF_INFO;
         accessor.setData(result);
      }

      return result;
   }

   public static void setBiomeData(@Nonnull Biome biome, @Nullable BiomeInfo data) {
      ((IDataAccessor)biome).setData(data);
   }

   @Nonnull
   public static String getBiomeName(@Nonnull Biome biome) {
      String result = (String)biomeName.get(biome);
      return StringUtils.isEmpty(result) ? "UNKNOWN" : result;
   }

   public static boolean isBoPBiome(@Nonnull Biome biome) {
      return bopBiome != null && bopBiome.isInstance(biome);
   }

   public static int getBoPBiomeFogColor(@Nonnull Biome biome) {
      return bopBiomeFogColor.isAvailable() ? bopBiomeFogColor.get(biome) : 0;
   }

   public static float getBoPBiomeFogDensity(@Nonnull Biome biome) {
      return bopBiomeFogDensity.isAvailable() ? bopBiomeFogDensity.get(biome) : 0.0F;
   }

   @Nonnull
   public static Set<Type> getBiomeTypes() {
      if (biomeType.isAvailable()) {
         return new ReferenceOpenHashSet(((Map)biomeType.get((Object)null)).values());
      } else {
         throw new IllegalStateException("Cannot locate BiomeDictionary.Type table!");
      }
   }

   @Nonnull
   public static Color getBiomeWaterColor(@Nonnull World world, @Nonnull BlockPos pos) {
      return new Color(BiomeColorHelper.getWaterColorAtPos(world, pos));
   }

   @Nonnull
   public static Color getColorForLiquid(@Nonnull World world, @Nonnull BlockPos pos) {
      return getColorForLiquid(world, WorldUtils.getBlockState(world, pos), pos);
   }

   @Nonnull
   public static Color getColorForLiquid(@Nonnull World world, @Nonnull IBlockState state, @Nonnull BlockPos pos) {
      Block liquid = state.getBlock();
      if (liquid == Blocks.WATER) {
         return getBiomeWaterColor(world, pos);
      } else {
         Fluid fluid = FluidRegistry.lookupFluidForBlock(liquid);
         return fluid != null ? new Color(fluid.getColor()) : NO_COLOR;
      }
   }

   @Nonnull
   public static Set<Type> getBiomeTypes(@Nonnull Biome biome) {
      try {
         return new ReferenceOpenHashSet(BiomeDictionary.getTypes(biome));
      } catch (Throwable var3) {
         String name = (String)biomeName.get(biome);
         ModBase.log().warn("Unable to get biome type data for biome '%s'", new Object[]{name});
         return new ReferenceOpenHashSet();
      }
   }

   public static boolean areBiomesSimilar(@Nonnull Biome b1, @Nonnull Biome b2) {
      return BiomeDictionary.areSimilar(b1, b2);
   }
}
