package rtg.api.util;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMesa;
import rtg.api.RTGAPI;
import rtg.api.config.BiomeConfig;
import rtg.api.util.storage.BiomeMap;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;

public final class PlateauUtil {
   private static final HashMap<IRealisticBiome, List<IBlockState>> BIOME_PLATEAU_BANDS = Maps.newHashMap();
   private static final IBlockState DEFAULT_PLATEAU_BLOCK = Blocks.HARDENED_CLAY.getDefaultState();
   private static final Collection<Biome> PLATEAU_BIOMES = Collections.unmodifiableCollection(Arrays.asList(Biomes.MESA, Biomes.MUTATED_MESA, Biomes.MESA_ROCK, Biomes.MUTATED_MESA_ROCK, Biomes.MESA_CLEAR_ROCK, Biomes.MUTATED_MESA_CLEAR_ROCK, Biomes.SAVANNA_PLATEAU, Biomes.MUTATED_SAVANNA_ROCK));
   private static final Collection<String> MESA_PLATEAU_BLOCKS = Collections.unmodifiableCollection(Arrays.asList("minecraft:stained_hardened_clay[color=yellow]", "minecraft:stained_hardened_clay[color=yellow]", "minecraft:stained_hardened_clay[color=yellow]", "minecraft:hardened_clay", "minecraft:hardened_clay", "minecraft:stained_hardened_clay[color=orange]", "minecraft:stained_hardened_clay[color=red]", "minecraft:stained_hardened_clay[color=red]", "minecraft:hardened_clay", "minecraft:stained_hardened_clay[color=silver]", "minecraft:stained_hardened_clay[color=white]", "minecraft:hardened_clay", "minecraft:hardened_clay", "minecraft:hardened_clay", "minecraft:stained_hardened_clay[color=orange]", "minecraft:hardened_clay", "minecraft:hardened_clay", "minecraft:hardened_clay", "minecraft:stained_hardened_clay[color=orange]", "minecraft:stained_hardened_clay[color=silver]", "minecraft:stained_hardened_clay[color=white]", "minecraft:stained_hardened_clay[color=brown]", "minecraft:stained_hardened_clay[color=brown]", "minecraft:stained_hardened_clay[color=brown]", "minecraft:hardened_clay", "minecraft:stained_hardened_clay[color=orange]", "minecraft:hardened_clay", "minecraft:stained_hardened_clay[color=orange]", "minecraft:hardened_clay", "minecraft:stained_hardened_clay[color=orange]", "minecraft:hardened_clay", "minecraft:hardened_clay", "minecraft:hardened_clay"));
   private static final Collection<String> SAVANNA_PLATEAU_BLOCKS = Collections.unmodifiableCollection(Arrays.asList("minecraft:stained_hardened_clay[color=white]", "minecraft:stained_hardened_clay[color=white]", "minecraft:stained_hardened_clay[color=white]", "minecraft:stained_hardened_clay[color=white]", "minecraft:stained_hardened_clay[color=silver]", "minecraft:stained_hardened_clay[color=silver]", "minecraft:stained_hardened_clay[color=brown]", "minecraft:stained_hardened_clay[color=brown]", "minecraft:stained_hardened_clay[color=silver]", "minecraft:stained_hardened_clay[color=white]", "minecraft:stained_hardened_clay[color=silver]", "minecraft:stained_hardened_clay[color=brown]", "minecraft:stained_hardened_clay[color=brown]", "minecraft:stained_hardened_clay[color=silver]", "minecraft:stained_hardened_clay[color=brown]", "minecraft:stained_hardened_clay[color=silver]", "minecraft:stained_hardened_clay[color=white]", "minecraft:stained_hardened_clay[color=white]", "minecraft:stained_hardened_clay[color=silver]", "minecraft:stained_hardened_clay[color=brown]", "minecraft:stained_hardened_clay[color=brown]"));

   private PlateauUtil() {
   }

   public static String[] getMesaPlateauBlocks() {
      return (String[])MESA_PLATEAU_BLOCKS.toArray(new String[0]);
   }

   public static String[] getSavannaPlateauBlocks() {
      return (String[])SAVANNA_PLATEAU_BLOCKS.toArray(new String[0]);
   }

   private static Collection<String> getConfigBlocks(IRealisticBiome rtgBiome) {
      BiomeConfig config = rtgBiome.getConfig();
      return config.hasProperty(config.PLATEAU_GRADIENT_BLOCK_LIST) && ((String[])config.PLATEAU_GRADIENT_BLOCK_LIST.getValues()).length > 0 ? config.PLATEAU_GRADIENT_BLOCK_LIST.getAsCollection() : (rtgBiome.baseBiome() instanceof BiomeMesa ? MESA_PLATEAU_BLOCKS : SAVANNA_PLATEAU_BLOCKS);
   }

   public static void init() {
      Stream var10000 = PLATEAU_BIOMES.stream();
      BiomeMap var10001 = RTGAPI.RTG_BIOMES;
      RTGAPI.RTG_BIOMES.getClass();
      var10000.map(var10001::get).filter(Objects::nonNull).forEach((rtgBiome) -> {
         Collection<String> blocks = getConfigBlocks(rtgBiome);
         List<IBlockState> bands = (List)blocks.stream().map(BlockUtil::getBlockStateFromCfgString).filter(Objects::nonNull).collect(Collectors.toList());
         if (bands.isEmpty()) {
            bands.add(DEFAULT_PLATEAU_BLOCK);
         }

         BIOME_PLATEAU_BANDS.put(rtgBiome, bands);
      });
   }

   public static IBlockState getPlateauBand(RTGWorld rtgWorld, IRealisticBiome rBiome, int x, int y, int z) {
      return getBand(rBiome, y);
   }

   public static float stepIncrease(float simplexVal, float start, float finish, float height) {
      return simplexVal <= start ? 0.0F : (simplexVal >= finish ? height : (simplexVal - start) / (finish - start) * height);
   }

   private static IBlockState getBand(IRealisticBiome rBiome, int index) {
      List<IBlockState> bands = (List)BIOME_PLATEAU_BANDS.get(rBiome);
      return bands != null ? (IBlockState)bands.get(index % bands.size()) : DEFAULT_PLATEAU_BLOCK;
   }
}
