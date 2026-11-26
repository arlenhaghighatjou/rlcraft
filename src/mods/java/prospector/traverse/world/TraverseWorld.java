package prospector.traverse.world;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockRedSandstone.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeForest.Type;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.terraingen.BiomeEvent.GetVillageBlockID;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import prospector.traverse.config.TraverseConfig;
import prospector.traverse.shadow.shootingstar.version.Version;
import prospector.traverse.world.biomes.BiomeAridHighland;
import prospector.traverse.world.biomes.BiomeAutumnalWoodedHills;
import prospector.traverse.world.biomes.BiomeAutumnalWoods;
import prospector.traverse.world.biomes.BiomeBadlands;
import prospector.traverse.world.biomes.BiomeCanyon;
import prospector.traverse.world.biomes.BiomeCliffs;
import prospector.traverse.world.biomes.BiomeConiferousForest;
import prospector.traverse.world.biomes.BiomeCragCliffs;
import prospector.traverse.world.biomes.BiomeDesertShrubland;
import prospector.traverse.world.biomes.BiomeForestedHills;
import prospector.traverse.world.biomes.BiomeGlacier;
import prospector.traverse.world.biomes.BiomeLushHills;
import prospector.traverse.world.biomes.BiomeLushSwamp;
import prospector.traverse.world.biomes.BiomeMeadow;
import prospector.traverse.world.biomes.BiomeMiniJungle;
import prospector.traverse.world.biomes.BiomeMountainousDesert;
import prospector.traverse.world.biomes.BiomeRedDesert;
import prospector.traverse.world.biomes.BiomeRockyPlains;
import prospector.traverse.world.biomes.BiomeRockyPlateau;
import prospector.traverse.world.biomes.BiomeThicket;
import prospector.traverse.world.biomes.BiomeWoodlands;

public class TraverseWorld {
   public static final Version V0 = new Version(1, 0, 0);
   public static final Version V1 = new Version(1, 1, 0);
   public static final Version V2 = new Version(1, 2, 0);
   public static final Version V3 = new Version(1, 3, 0);
   public static final Version V4 = new Version(1, 4, 0);
   public static final Version V5 = new Version(1, 5, 0);
   public static final TraverseWorld.VillageReplacements NO_VILLAGES = (originalState) -> {
      return null;
   };
   public static final TraverseWorld.VillageReplacements NO_REPLACEMENTS = (originalState) -> {
      return originalState;
   };
   public static final TraverseWorld.VillageReplacements RED_DESERT_REPLACEMENTS = (originalState) -> {
      if (originalState.getBlock() != Blocks.LOG && originalState.getBlock() != Blocks.LOG2) {
         if (originalState.getBlock() == Blocks.COBBLESTONE) {
            return Blocks.RED_SANDSTONE.getDefaultState().withProperty(BlockRedSandstone.TYPE, EnumType.DEFAULT);
         } else if (originalState.getBlock() == Blocks.PLANKS) {
            return Blocks.RED_SANDSTONE.getDefaultState().withProperty(BlockRedSandstone.TYPE, EnumType.SMOOTH);
         } else if (originalState.getBlock() == Blocks.OAK_STAIRS) {
            return Blocks.RED_SANDSTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, originalState.getValue(BlockStairs.FACING));
         } else if (originalState.getBlock() == Blocks.STONE_STAIRS) {
            return Blocks.RED_SANDSTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, originalState.getValue(BlockStairs.FACING));
         } else {
            return originalState.getBlock() == Blocks.GRAVEL ? Blocks.RED_SANDSTONE.getDefaultState() : originalState;
         }
      } else {
         return Blocks.RED_SANDSTONE.getDefaultState();
      }
   };
   public static final TraverseWorld.VillageReplacements DESERT_REPLACEMENTS = (originalState) -> {
      if (originalState.getBlock() != Blocks.LOG && originalState.getBlock() != Blocks.LOG2) {
         if (originalState.getBlock() == Blocks.COBBLESTONE) {
            return Blocks.SANDSTONE.getDefaultState().withProperty(BlockSandStone.TYPE, net.minecraft.block.BlockSandStone.EnumType.DEFAULT);
         } else if (originalState.getBlock() == Blocks.PLANKS) {
            return Blocks.SANDSTONE.getDefaultState().withProperty(BlockSandStone.TYPE, net.minecraft.block.BlockSandStone.EnumType.SMOOTH);
         } else if (originalState.getBlock() == Blocks.OAK_STAIRS) {
            return Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, originalState.getValue(BlockStairs.FACING));
         } else if (originalState.getBlock() == Blocks.STONE_STAIRS) {
            return Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, originalState.getValue(BlockStairs.FACING));
         } else {
            return originalState.getBlock() == Blocks.GRAVEL ? Blocks.SANDSTONE.getDefaultState() : originalState;
         }
      } else {
         return Blocks.SANDSTONE.getDefaultState();
      }
   };
   public static List<TraverseWorld.TraverseBiomeEntry> biomeList = new ArrayList();
   public static Map<Biome, TraverseWorld.VillageReplacements> villageReplacementBiomes = new HashMap();
   public static Biome autumnalWoodsBiome = new BiomeAutumnalWoods();
   public static Biome woodlandsBiome = new BiomeWoodlands();
   public static Biome miniJungleBiome = new BiomeMiniJungle();
   public static Biome meadowBiome = new BiomeMeadow();
   public static Biome lushSwampBiome = new BiomeLushSwamp();
   public static Biome redDesertBiome = new BiomeRedDesert();
   public static Biome temperateRainforestBiome = new BiomeConiferousForest(false);
   public static Biome badlandsBiome = new BiomeBadlands();
   public static Biome mountainousDesertBiome = new BiomeMountainousDesert();
   public static Biome rockyPlateauBiome = new BiomeRockyPlateau();
   public static Biome forestedHillsBiome = new BiomeForestedHills(Type.NORMAL, "Forested Hills");
   public static Biome birchForestedHillsBiome = new BiomeForestedHills(Type.BIRCH, "Birch Forested Hills");
   public static Biome autumnalWoodedHillsBiome = new BiomeAutumnalWoodedHills();
   public static Biome cliffsBiome = new BiomeCliffs();
   public static Biome glacierBiome = new BiomeGlacier(false);
   public static Biome glacierSpikesBiome = new BiomeGlacier(true);
   public static Biome snowyConiferousForestBiome = new BiomeConiferousForest(true);
   public static Biome lushHillsBiome = new BiomeLushHills();
   public static Biome canyonBiome = new BiomeCanyon();
   public static Biome cragCliffsBiome = new BiomeCragCliffs();
   public static Biome desertShrublandBiome = new BiomeDesertShrubland();
   public static Biome thicketBiome = new BiomeThicket();
   public static Biome aridHighlandBiome = new BiomeAridHighland();
   public static Biome rockyPlainsBiome = new BiomeRockyPlains();

   public TraverseWorld() {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Biome.class, TraverseWorld::registerBiomes);
      bus.register(GetVillageBlockID.class, TraverseWorld::replaceVillageBlocks);
   }

   public static void registerBiomes(Register<Biome> event) {
      register(V0, autumnalWoodsBiome, BiomeType.COOL, "autumnal_woods", 8, TraverseConfig.disableAutumnalWoods, true, event, net.minecraftforge.common.BiomeDictionary.Type.FOREST);
      register(V0, woodlandsBiome, BiomeType.WARM, "woodlands", 9, TraverseConfig.disableWoodlands, true, NO_REPLACEMENTS, event, net.minecraftforge.common.BiomeDictionary.Type.PLAINS);
      register(V0, miniJungleBiome, BiomeType.WARM, "mini_jungle", 3, TraverseConfig.disableMiniJungle, true, event, net.minecraftforge.common.BiomeDictionary.Type.DENSE, net.minecraftforge.common.BiomeDictionary.Type.JUNGLE, net.minecraftforge.common.BiomeDictionary.Type.HOT, net.minecraftforge.common.BiomeDictionary.Type.WET);
      register(V0, meadowBiome, BiomeType.COOL, "meadow", 7, TraverseConfig.disableMeadow, true, NO_REPLACEMENTS, event, net.minecraftforge.common.BiomeDictionary.Type.PLAINS, net.minecraftforge.common.BiomeDictionary.Type.LUSH, net.minecraftforge.common.BiomeDictionary.Type.WET);
      register(V0, lushSwampBiome, BiomeType.WARM, "green_swamp", 6, TraverseConfig.disableLushSwamp, false, event, net.minecraftforge.common.BiomeDictionary.Type.LUSH, net.minecraftforge.common.BiomeDictionary.Type.WET, net.minecraftforge.common.BiomeDictionary.Type.SWAMP);
      register(V0, redDesertBiome, BiomeType.DESERT, "red_desert", 6, TraverseConfig.disableRedDesert, false, RED_DESERT_REPLACEMENTS, event, net.minecraftforge.common.BiomeDictionary.Type.HOT, net.minecraftforge.common.BiomeDictionary.Type.DRY, net.minecraftforge.common.BiomeDictionary.Type.SANDY);
      register(V0, temperateRainforestBiome, BiomeType.COOL, "temperate_rainforest", 8, TraverseConfig.disableTemperateRainforest, true, event, net.minecraftforge.common.BiomeDictionary.Type.FOREST, net.minecraftforge.common.BiomeDictionary.Type.CONIFEROUS);
      register(V1, badlandsBiome, BiomeType.WARM, "badlands", 5, TraverseConfig.disableBadlands, false, NO_REPLACEMENTS, event, net.minecraftforge.common.BiomeDictionary.Type.PLAINS, net.minecraftforge.common.BiomeDictionary.Type.DRY, net.minecraftforge.common.BiomeDictionary.Type.HOT, net.minecraftforge.common.BiomeDictionary.Type.SPARSE);
      register(V1, mountainousDesertBiome, BiomeType.DESERT, "mountainous_desert", 2, TraverseConfig.disableMountainousDesert, false, event, net.minecraftforge.common.BiomeDictionary.Type.MOUNTAIN, net.minecraftforge.common.BiomeDictionary.Type.DRY, net.minecraftforge.common.BiomeDictionary.Type.HOT, net.minecraftforge.common.BiomeDictionary.Type.SANDY);
      register(V1, rockyPlateauBiome, BiomeType.WARM, "rocky_plateau", 4, TraverseConfig.disableRockyPlateau, false, NO_REPLACEMENTS, event, net.minecraftforge.common.BiomeDictionary.Type.HOT, net.minecraftforge.common.BiomeDictionary.Type.MOUNTAIN);
      register(V1, forestedHillsBiome, BiomeType.COOL, "forested_hills", 6, TraverseConfig.disableForestedHills, true, event, net.minecraftforge.common.BiomeDictionary.Type.FOREST, net.minecraftforge.common.BiomeDictionary.Type.HILLS);
      register(V1, birchForestedHillsBiome, BiomeType.COOL, "birch_forested_hills", 2, TraverseConfig.disableBirchForestedHills, true, event, net.minecraftforge.common.BiomeDictionary.Type.FOREST, net.minecraftforge.common.BiomeDictionary.Type.HILLS);
      register(V1, autumnalWoodedHillsBiome, BiomeType.COOL, "autumnal_wooded_hills", 1, TraverseConfig.disableAutumnalWoodedHills, true, event, net.minecraftforge.common.BiomeDictionary.Type.FOREST, net.minecraftforge.common.BiomeDictionary.Type.HILLS);
      register(V2, cliffsBiome, BiomeType.COOL, "cliffs", 2, TraverseConfig.disableCliffs, false, event, net.minecraftforge.common.BiomeDictionary.Type.MOUNTAIN, net.minecraftforge.common.BiomeDictionary.Type.COLD, net.minecraftforge.common.BiomeDictionary.Type.HILLS);
      register(V2, glacierBiome, BiomeType.ICY, "glacier", 6, TraverseConfig.disableGlacier, false, event, net.minecraftforge.common.BiomeDictionary.Type.MOUNTAIN, net.minecraftforge.common.BiomeDictionary.Type.COLD, net.minecraftforge.common.BiomeDictionary.Type.SNOWY);
      register(V2, glacierSpikesBiome, BiomeType.ICY, "glacier_spikes", 2, TraverseConfig.disableGlacierSpikes, false, event, net.minecraftforge.common.BiomeDictionary.Type.MOUNTAIN, net.minecraftforge.common.BiomeDictionary.Type.COLD, net.minecraftforge.common.BiomeDictionary.Type.SNOWY);
      register(V2, snowyConiferousForestBiome, BiomeType.ICY, "snowy_coniferous_forest", 5, TraverseConfig.disableSnowyConiferousForest, true, event, net.minecraftforge.common.BiomeDictionary.Type.COLD, net.minecraftforge.common.BiomeDictionary.Type.SNOWY, net.minecraftforge.common.BiomeDictionary.Type.FOREST, net.minecraftforge.common.BiomeDictionary.Type.CONIFEROUS);
      register(V3, lushHillsBiome, BiomeType.COOL, "lush_hills", 6, TraverseConfig.disableLushHills, true, NO_REPLACEMENTS, event, net.minecraftforge.common.BiomeDictionary.Type.LUSH, net.minecraftforge.common.BiomeDictionary.Type.HILLS, net.minecraftforge.common.BiomeDictionary.Type.SPARSE, net.minecraftforge.common.BiomeDictionary.Type.WET);
      register(V3, canyonBiome, BiomeType.DESERT, "canyon", 5, TraverseConfig.disableCanyon, false, event, net.minecraftforge.common.BiomeDictionary.Type.DRY, net.minecraftforge.common.BiomeDictionary.Type.DEAD);
      register(V3, cragCliffsBiome, BiomeType.COOL, "crag_cliffs", 4, TraverseConfig.disableCragCliffs, false, event, net.minecraftforge.common.BiomeDictionary.Type.COLD, net.minecraftforge.common.BiomeDictionary.Type.DEAD);
      register(V4, desertShrublandBiome, BiomeType.DESERT, "desert_shrubland", 5, TraverseConfig.disableDesertShrubland, false, DESERT_REPLACEMENTS, event, net.minecraftforge.common.BiomeDictionary.Type.HOT, net.minecraftforge.common.BiomeDictionary.Type.DRY, net.minecraftforge.common.BiomeDictionary.Type.SANDY, net.minecraftforge.common.BiomeDictionary.Type.DEAD);
      register(V5, thicketBiome, BiomeType.WARM, "thicket", 6, TraverseConfig.disableThicket, true, event, net.minecraftforge.common.BiomeDictionary.Type.DENSE, net.minecraftforge.common.BiomeDictionary.Type.FOREST);
      register(V5, aridHighlandBiome, BiomeType.DESERT, "arid_highland", 4, TraverseConfig.disableAridHighland, false, DESERT_REPLACEMENTS, event, net.minecraftforge.common.BiomeDictionary.Type.HOT, net.minecraftforge.common.BiomeDictionary.Type.DRY, net.minecraftforge.common.BiomeDictionary.Type.SPARSE, net.minecraftforge.common.BiomeDictionary.Type.MOUNTAIN, net.minecraftforge.common.BiomeDictionary.Type.HILLS);
      register(V5, rockyPlainsBiome, BiomeType.COOL, "rocky_plains", 4, TraverseConfig.disableRockyPlains, true, NO_REPLACEMENTS, event, net.minecraftforge.common.BiomeDictionary.Type.PLAINS, net.minecraftforge.common.BiomeDictionary.Type.HILLS, net.minecraftforge.common.BiomeDictionary.Type.SPARSE);
   }

   public static void register(Version versionAdded, Biome biome, BiomeType type, String name, int weight, boolean disabled, boolean canSpawn, TraverseWorld.VillageReplacements villageReplacements, Register<Biome> event, net.minecraftforge.common.BiomeDictionary.Type... biomeDictTypes) {
      if (!disabled) {
         biome.setRegistryName(new ResourceLocation("traverse", name));
         event.getRegistry().register(biome);

         for(net.minecraftforge.common.BiomeDictionary.Type biomeDictType : biomeDictTypes) {
            BiomeDictionary.addTypes(biome, new net.minecraftforge.common.BiomeDictionary.Type[]{biomeDictType});
         }

         biomeList.add(new TraverseWorld.TraverseBiomeEntry(biome, type, weight, canSpawn, !TraverseConfig.disallowVillages && villageReplacements != NO_VILLAGES, versionAdded));
         if (!TraverseConfig.disallowVillages && villageReplacements != NO_VILLAGES && villageReplacements != NO_REPLACEMENTS) {
            villageReplacementBiomes.put(biome, villageReplacements);
         }
      }

   }

   public static void register(Version versionAdded, Biome biome, BiomeType type, String name, int weight, boolean disabled, boolean canSpawn, Register<Biome> event, net.minecraftforge.common.BiomeDictionary.Type... biomeDictTypes) {
      register(versionAdded, biome, type, name, weight, disabled, canSpawn, NO_VILLAGES, event, biomeDictTypes);
   }

   public static void replaceVillageBlocks(GetVillageBlockID event) {
      if (villageReplacementBiomes.keySet().contains(event.getBiome())) {
         event.setReplacement(((TraverseWorld.VillageReplacements)villageReplacementBiomes.get(event.getBiome())).getBiomeSpecificState(event.getOriginal()));
         event.setResult(Result.DENY);
      }

   }

   public static class TraverseBiomeEntry {
      private Biome biome;
      private BiomeType type;
      private Version versionAdded;
      private int weight;
      private boolean canSpawn;
      private boolean hasVillages;
      private BiomeEntry entry;

      public TraverseBiomeEntry(Biome biome, BiomeType type, int weight, boolean canSpawn, boolean hasVillages, Version versionAdded) {
         this.biome = biome;
         this.type = type;
         this.weight = weight;
         this.canSpawn = canSpawn;
         this.hasVillages = hasVillages;
         this.versionAdded = versionAdded;
         this.entry = new BiomeEntry(biome, weight);
      }

      public Biome getBiome() {
         return this.biome;
      }

      public BiomeEntry getEntry() {
         return this.entry;
      }

      public BiomeType getType() {
         return this.type;
      }

      public Version getVersionAdded() {
         return this.versionAdded;
      }

      public int getWeight() {
         return this.weight;
      }

      public boolean hasVillages() {
         return this.hasVillages;
      }

      public boolean canSpawn() {
         return this.canSpawn;
      }
   }

   public interface VillageReplacements {
      IBlockState getBiomeSpecificState(IBlockState var1);
   }
}
