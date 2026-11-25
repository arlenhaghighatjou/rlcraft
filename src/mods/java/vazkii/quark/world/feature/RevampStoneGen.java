package vazkii.quark.world.feature;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import org.apache.commons.lang3.text.WordUtils;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.base.handler.BiomeTypeConfigHandler;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.handler.ModIntegrationHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.feature.VanillaWalls;
import vazkii.quark.world.block.BlockJasper;
import vazkii.quark.world.block.BlockLimestone;
import vazkii.quark.world.block.BlockMarble;
import vazkii.quark.world.block.BlockSlate;
import vazkii.quark.world.block.slab.BlockBasicStoneSlab;
import vazkii.quark.world.world.StoneInfoBasedGenerator;

public class RevampStoneGen extends Feature {
   public static BlockMod marble;
   public static BlockMod limestone;
   public static BlockMod jasper;
   public static BlockMod slate;
   public static boolean enableStairsAndSlabs;
   public static boolean enableWalls;
   public static boolean outputCSV;
   public static boolean generateBasedOnBiomes;
   public static boolean enableMarble;
   public static boolean enableLimestone;
   public static boolean enableJasper;
   public static boolean enableSlate;
   public static RevampStoneGen.StoneInfo graniteInfo;
   public static RevampStoneGen.StoneInfo dioriteInfo;
   public static RevampStoneGen.StoneInfo andesiteInfo;
   public static RevampStoneGen.StoneInfo marbleInfo;
   public static RevampStoneGen.StoneInfo limestoneInfo;
   public static RevampStoneGen.StoneInfo jasperInfo;
   public static RevampStoneGen.StoneInfo slateInfo;
   private static List<StoneInfoBasedGenerator> generators;
   private Queue<Runnable> deferedInit = new ArrayDeque();

   public RevampStoneGen() {
   }

   public void setupConfig() {
      enableStairsAndSlabs = this.loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
      enableWalls = this.loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
      enableMarble = this.loadPropBool("Enable Marble", "", true);
      enableLimestone = this.loadPropBool("Enable Limestone", "", true);
      enableJasper = this.loadPropBool("Enable Jasper", "", true);
      enableSlate = this.loadPropBool("Enable Slate", "", true);
      generateBasedOnBiomes = this.loadPropBool("Generate Based on Biomes", "Note: The stone rarity values are tuned based on this being true. If you turn it off, also change the stones' rarity (around 50 is fine).", true);
      outputCSV = this.loadPropBool("Output CSV Debug Info", "If this is true, CSV debug info will be printed out to the console on init, to help test biome spreads.", false);
      int defSize = 14;
      int defRarity = 9;
      int defUpper = 80;
      int defLower = 20;
      graniteInfo = this.loadStoneInfo("granite", defSize, defRarity, defUpper, defLower, true, Type.MOUNTAIN, Type.HILLS);
      dioriteInfo = this.loadStoneInfo("diorite", defSize, defRarity, defUpper, defLower, true, Type.SAVANNA, Type.JUNGLE, Type.MUSHROOM);
      andesiteInfo = this.loadStoneInfo("andesite", defSize, defRarity, defUpper, defLower, true, Type.FOREST);
      marbleInfo = this.loadStoneInfo("marble", defSize, defRarity, defUpper, defLower, enableMarble, Type.PLAINS);
      limestoneInfo = this.loadStoneInfo("limestone", defSize, defRarity, defUpper, defLower, enableLimestone, Type.SWAMP, Type.OCEAN);
      jasperInfo = this.loadStoneInfo("jasper", defSize, defRarity, defUpper, defLower, enableJasper, Type.MESA, Type.SANDY);
      slateInfo = this.loadStoneInfo("slate", defSize, defRarity, defUpper, defLower, enableSlate, Type.COLD);
   }

   public RevampStoneGen.StoneInfo loadStoneInfo(String name, int clusterSize, int clusterRarity, int upperBound, int lowerBound, boolean enabled, Type... biomes) {
      return loadStoneInfo(this.configCategory, name, clusterSize, clusterRarity, upperBound, lowerBound, enabled, "0", biomes);
   }

   public static RevampStoneGen.StoneInfo loadStoneInfo(String configCategory, String name, int clusterSize, int clusterRarity, int upperBound, int lowerBound, boolean enabled, String dims, Type... biomes) {
      String category = configCategory + "." + name;
      return new RevampStoneGen.StoneInfo(category, clusterSize, clusterRarity, upperBound, lowerBound, enabled, dims, biomes);
   }

   public void preInit(FMLPreInitializationEvent event) {
      generators = new ArrayList();
      IBlockState graniteState = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.GRANITE);
      IBlockState dioriteState = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.DIORITE);
      IBlockState andesiteState = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.ANDESITE);
      generators.add(new StoneInfoBasedGenerator(() -> {
         return graniteInfo;
      }, graniteState, "granite"));
      generators.add(new StoneInfoBasedGenerator(() -> {
         return dioriteInfo;
      }, dioriteState, "diorite"));
      generators.add(new StoneInfoBasedGenerator(() -> {
         return andesiteInfo;
      }, andesiteState, "andesite"));
      marble = this.makeStone(BlockMarble.class, "marble", marbleInfo, enableMarble);
      limestone = this.makeStone(BlockLimestone.class, "limestone", limestoneInfo, enableLimestone);
      jasper = this.makeStone(BlockJasper.class, "jasper", jasperInfo, enableJasper);
      slate = this.makeStone(BlockSlate.class, "slate", slateInfo, enableSlate);
      if (outputCSV) {
         BiomeTypeConfigHandler.debugStoneGeneration(generators);
      }

   }

   private BlockMod makeStone(Class<? extends BlockMod> clazz, String name, RevampStoneGen.StoneInfo info, boolean enable) {
      if (!enable) {
         return null;
      } else {
         try {
            BlockMod block = (BlockMod)clazz.newInstance();
            if (enableStairsAndSlabs) {
               BlockBasicStoneSlab.initSlab(block, 0, "stone_" + name + "_slab");
               BlockModStairs.initStairs(block, 0, new BlockQuarkStairs("stone_" + name + "_stairs", block.getDefaultState()));
            }

            VanillaWalls.add(name, block, 0, enableWalls);
            RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(block, 4, 1), new Object[]{"BB", "BB", 'B', ProxyRegistry.newStack(block, 1, 0)});
            generators.add(new StoneInfoBasedGenerator(() -> {
               return info;
            }, block.getDefaultState(), name));
            String capName = WordUtils.capitalize(name);
            addOreDict("stone" + capName, ProxyRegistry.newStack(block, 1, 0));
            addOreDict("stone" + capName + "Polished", ProxyRegistry.newStack(block, 1, 1));
            this.deferedInit.add(() -> {
               ModIntegrationHandler.registerChiselVariant(name, ProxyRegistry.newStack(block, 1, 0));
               ModIntegrationHandler.registerChiselVariant(name, ProxyRegistry.newStack(block, 1, 1));
            });
            return block;
         } catch (Exception var7) {
            throw new RuntimeException(var7);
         }
      }
   }

   public void init() {
      while(!this.deferedInit.isEmpty()) {
         ((Runnable)this.deferedInit.poll()).run();
      }

   }

   @SubscribeEvent
   public void onOreGenerate(GenerateMinable event) {
      switch(event.getType()) {
      case GRANITE:
         if (graniteInfo.enabled) {
            event.setResult(Result.DENY);
         }
         break;
      case DIORITE:
         if (dioriteInfo.enabled) {
            event.setResult(Result.DENY);
         }
         break;
      case ANDESITE:
         if (andesiteInfo.enabled) {
            event.setResult(Result.DENY);
         }
         break;
      case DIRT:
         this.generateNewStones(event);
      }

   }

   private void generateNewStones(GenerateMinable event) {
      World world = event.getWorld();
      BlockPos pos = event.getPos();
      Chunk chunk = world.getChunkFromBlockCoords(pos);

      for(StoneInfoBasedGenerator gen : generators) {
         gen.generate(chunk.x, chunk.z, world);
      }

   }

   public boolean hasOreGenSubscriptions() {
      return true;
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public static class StoneInfo {
      public final boolean enabled;
      public final int clusterSize;
      public final int clusterRarity;
      public final int upperBound;
      public final int lowerBound;
      public final boolean clustersRarityPerChunk;
      public final DimensionConfig dims;
      public final List<Type> allowedBiomes;

      private StoneInfo(String category, int clusterSize, int clusterRarity, int upperBound, int lowerBound, boolean enabled, String dimStr, Type... biomes) {
         this.enabled = ModuleLoader.config.getBoolean("Enabled", category, true, "") && enabled;
         this.clusterSize = ModuleLoader.config.getInt("Cluster Radius", category, clusterSize, 0, Integer.MAX_VALUE, "");
         this.clusterRarity = ModuleLoader.config.getInt("Cluster Rarity", category, clusterRarity, 0, Integer.MAX_VALUE, "Out of how many chunks would one of these clusters generate");
         this.upperBound = ModuleLoader.config.getInt("Y Level Max", category, upperBound, 0, 255, "");
         this.lowerBound = ModuleLoader.config.getInt("Y Level Min", category, lowerBound, 0, 255, "");
         this.clustersRarityPerChunk = ModuleLoader.config.getBoolean("Invert Cluster Rarity", category, false, "Setting this to true will make the 'Cluster Rarity' feature be X per chunk rather than 1 per X chunks");
         this.dims = new DimensionConfig(category, dimStr);
         this.allowedBiomes = BiomeTypeConfigHandler.parseBiomeTypeArrayConfig("Allowed Biome Types", category, biomes);
      }
   }
}
