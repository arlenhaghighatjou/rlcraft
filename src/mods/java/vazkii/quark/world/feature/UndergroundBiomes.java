package vazkii.quark.world.feature;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.base.handler.BiomeTypeConfigHandler;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.feature.VanillaWalls;
import vazkii.quark.world.block.BlockBiomeBrick;
import vazkii.quark.world.block.BlockBiomeCobblestone;
import vazkii.quark.world.block.BlockElderPrismarine;
import vazkii.quark.world.block.BlockElderSeaLantern;
import vazkii.quark.world.block.BlockGlowcelium;
import vazkii.quark.world.block.BlockGlowshroom;
import vazkii.quark.world.block.BlockHugeGlowshroom;
import vazkii.quark.world.block.slab.BlockBasicStoneSlab;
import vazkii.quark.world.world.UndergroundBiomeGenerator;
import vazkii.quark.world.world.underground.UndergroundBiome;
import vazkii.quark.world.world.underground.UndergroundBiomeGlowshroom;
import vazkii.quark.world.world.underground.UndergroundBiomeIcy;
import vazkii.quark.world.world.underground.UndergroundBiomeLava;
import vazkii.quark.world.world.underground.UndergroundBiomeLush;
import vazkii.quark.world.world.underground.UndergroundBiomeOvergrown;
import vazkii.quark.world.world.underground.UndergroundBiomePrismarine;
import vazkii.quark.world.world.underground.UndergroundBiomeSandstone;
import vazkii.quark.world.world.underground.UndergroundBiomeSlime;
import vazkii.quark.world.world.underground.UndergroundBiomeSpiderNest;

public class UndergroundBiomes extends Feature {
   public static List<UndergroundBiomeGenerator> biomes;
   public static BlockMod biome_cobblestone;
   public static BlockMod biome_brick;
   public static BlockMod glowcelium;
   public static Block glowshroom;
   public static Block glowshroom_block;
   public static BlockMetaVariants<BlockElderPrismarine.Variants> elder_prismarine;
   public static Block elder_sea_lantern;
   public static int glowshroomGrowthRate;
   public static IBlockState firestoneState;
   public static IBlockState icystoneState;
   public static IBlockState cobbedstoneState;
   public static boolean firestoneEnabled;
   public static boolean icystoneEnabled;
   public static boolean glowceliumEnabled;
   public static boolean bigGlowshroomsEnabled;
   public static boolean elderPrismarineEnabled;
   public static boolean cobbedstoneEnabled;
   public static boolean enableStairsAndSlabs;
   public static boolean enableWalls;
   public static boolean allowCraftingElderPrismarine;
   public static boolean allowCraftingCobbedstone;
   private static UndergroundBiomeGenerator prismarineBiomeGen;

   public UndergroundBiomes() {
   }

   public void setupConfig() {
      biomes = new ArrayList();
      firestoneEnabled = this.loadPropBool("Enable Firestone", "", true);
      icystoneEnabled = this.loadPropBool("Enable Froststone", "", true);
      glowceliumEnabled = this.loadPropBool("Enable Glowcelium and Glowshrooms", "", true);
      bigGlowshroomsEnabled = this.loadPropBool("Enable Big Glowshrooms", "", true);
      elderPrismarineEnabled = this.loadPropBool("Enable Elder Prismarine", "", true);
      cobbedstoneEnabled = this.loadPropBool("Enable Cobbedstone", "", true);
      enableStairsAndSlabs = this.loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
      enableWalls = this.loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
      allowCraftingElderPrismarine = this.loadPropBool("Allow crafting Elder Prismarine", "", true);
      allowCraftingCobbedstone = this.loadPropBool("Allow crafting Cobbedstone", "", true);
      glowshroomGrowthRate = this.loadPropInt("Glowshroom Growth Rate", "The smaller, the faster glowshrooms will spread. Vanilla mushroom speed is 25.", 20);
      biomes.add(this.loadUndergroundBiomeInfo("Lush", new UndergroundBiomeLush(), 80, Type.JUNGLE));
      biomes.add(this.loadUndergroundBiomeInfo("Sandstone", new UndergroundBiomeSandstone(), 80, Type.SANDY));
      biomes.add(this.loadUndergroundBiomeInfo("Slime", new UndergroundBiomeSlime(), 120, Type.SWAMP));
      biomes.add(prismarineBiomeGen = this.loadUndergroundBiomeInfo("Prismarine", new UndergroundBiomePrismarine(), 100, Type.OCEAN));
      biomes.add(this.loadUndergroundBiomeInfo("Spider", new UndergroundBiomeSpiderNest(), 80, Type.PLAINS));
      biomes.add(this.loadUndergroundBiomeInfo("Overgrown", new UndergroundBiomeOvergrown(), 80, Type.FOREST));
      biomes.add(this.loadUndergroundBiomeInfo("Icy", new UndergroundBiomeIcy(), 80, Type.COLD));
      biomes.add(this.loadUndergroundBiomeInfo("Lava", new UndergroundBiomeLava(), 80, Type.MESA));
      biomes.add(this.loadUndergroundBiomeInfo("Glowshroom", new UndergroundBiomeGlowshroom(), 80, Type.MOUNTAIN, Type.MUSHROOM));
      if (elder_prismarine != null) {
         ((UndergroundBiomePrismarine)prismarineBiomeGen.info.biome).update();
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      if (firestoneEnabled || icystoneEnabled || cobbedstoneEnabled) {
         biome_cobblestone = new BlockBiomeCobblestone();
         if (firestoneEnabled || icystoneEnabled) {
            biome_brick = new BlockBiomeBrick();
         }
      }

      if (elderPrismarineEnabled) {
         elder_prismarine = new BlockElderPrismarine();
         elder_sea_lantern = new BlockElderSeaLantern();
      }

      ((UndergroundBiomePrismarine)prismarineBiomeGen.info.biome).update();
      if (enableStairsAndSlabs) {
         if (firestoneEnabled) {
            BlockBasicStoneSlab.initSlab(biome_cobblestone, 0, "fire_stone_slab");
            BlockModStairs.initStairs(biome_cobblestone, 0, new BlockQuarkStairs("fire_stone_stairs", biome_cobblestone.getDefaultState()));
            BlockBasicStoneSlab.initSlab(biome_brick, 0, "fire_stone_brick_slab");
            BlockModStairs.initStairs(biome_brick, 0, new BlockQuarkStairs("fire_stone_brick_stairs", biome_brick.getDefaultState()));
         }

         if (icystoneEnabled) {
            BlockBasicStoneSlab.initSlab(biome_cobblestone, 1, "icy_stone_slab");
            BlockModStairs.initStairs(biome_cobblestone, 1, new BlockQuarkStairs("icy_stone_stairs", biome_cobblestone.getDefaultState().withProperty(biome_cobblestone.getVariantProp(), BlockBiomeCobblestone.Variants.ICY_STONE)));
            BlockBasicStoneSlab.initSlab(biome_brick, 1, "icy_stone_brick_slab");
            BlockModStairs.initStairs(biome_brick, 1, new BlockQuarkStairs("icy_stone_brick_stairs", biome_brick.getDefaultState().withProperty(biome_brick.getVariantProp(), BlockBiomeBrick.Variants.ICY_STONE_BRICK)));
         }

         if (cobbedstoneEnabled) {
            BlockBasicStoneSlab.initSlab(biome_cobblestone, 2, "cobbed_stone_slab");
            BlockModStairs.initStairs(biome_cobblestone, 2, new BlockQuarkStairs("cobbed_stone_stairs", biome_cobblestone.getDefaultState().withProperty(biome_cobblestone.getVariantProp(), BlockBiomeCobblestone.Variants.COBBED_STONE)));
         }

         if (elderPrismarineEnabled) {
            for(BlockElderPrismarine.Variants v : BlockElderPrismarine.Variants.values()) {
               BlockBasicStoneSlab.initSlab(elder_prismarine, v.ordinal(), v.getName() + "_slab");
               BlockModStairs.initStairs(elder_prismarine, v.ordinal(), new BlockQuarkStairs(v.getName() + "_stairs", elder_prismarine.getDefaultState().withProperty(elder_prismarine.getVariantProp(), v)));
            }
         }
      }

      VanillaWalls.add("fire_stone", biome_cobblestone, 0, enableWalls && firestoneEnabled);
      VanillaWalls.add("icy_stone", biome_cobblestone, 1, enableWalls && icystoneEnabled);
      VanillaWalls.add("cobbed_stone", biome_cobblestone, 2, enableWalls && cobbedstoneEnabled);
      VanillaWalls.add("fire_stone_brick", biome_brick, 0, enableWalls && firestoneEnabled);
      VanillaWalls.add("icy_stone_brick", biome_brick, 1, enableWalls && icystoneEnabled);

      for(BlockElderPrismarine.Variants v : BlockElderPrismarine.Variants.values()) {
         VanillaWalls.add(v.getName(), elder_prismarine, v.ordinal(), enableWalls && elderPrismarineEnabled);
      }

      if (glowceliumEnabled) {
         glowcelium = new BlockGlowcelium();
         glowshroom = new BlockGlowshroom();
         if (bigGlowshroomsEnabled) {
            glowshroom_block = new BlockHugeGlowshroom();
         }

         RecipeHandler.addShapelessOreDictRecipe(new ItemStack(Items.MUSHROOM_STEW), new Object[]{"mushroomAny", "mushroomAny", new ItemStack(Items.BOWL)});
      }

      if (firestoneEnabled) {
         firestoneState = biome_cobblestone.getDefaultState().withProperty(biome_cobblestone.getVariantProp(), BlockBiomeCobblestone.Variants.FIRE_STONE);
      }

      if (icystoneEnabled) {
         icystoneState = biome_cobblestone.getDefaultState().withProperty(biome_cobblestone.getVariantProp(), BlockBiomeCobblestone.Variants.ICY_STONE);
      }

      if (cobbedstoneEnabled) {
         cobbedstoneState = biome_cobblestone.getDefaultState().withProperty(biome_cobblestone.getVariantProp(), BlockBiomeCobblestone.Variants.COBBED_STONE);
      }

      this.addOreDict();
   }

   public void postPreInit() {
      if (firestoneEnabled) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(biome_brick, 4), new Object[]{"SS", "SS", 'S', "stoneBrimstone"});
      }

      if (icystoneEnabled) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(biome_brick, 4, 1), new Object[]{"SS", "SS", 'S', "stonePermafrost"});
      }

      if (elderPrismarineEnabled) {
         if (allowCraftingElderPrismarine) {
            RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(elder_prismarine, 2), new Object[]{"blockPrismarineElder", "blockPrismarine"});
         }

         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(elder_prismarine, 4, 1), new Object[]{"PPP", "PPP", "PPP", 'P', "blockPrismarineElder"});
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(elder_prismarine, 1, 2), new Object[]{"PPP", "PBP", "PPP", 'P', "blockPrismarineElder", 'B', "dyeBlack"});
         RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(elder_sea_lantern), new Object[]{"blockPrismarineElder", ProxyRegistry.newStack(Blocks.SEA_LANTERN)});
      }

      if (cobbedstoneEnabled) {
         if (allowCraftingCobbedstone) {
            RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(biome_cobblestone, 1, 2), new Object[]{" C ", "CSC", " C ", 'C', ProxyRegistry.newStack(Blocks.WEB), 'S', "cobblestone"});
         }

         FurnaceRecipes.instance().addSmeltingRecipe(ProxyRegistry.newStack(biome_cobblestone, 1, 2), ProxyRegistry.newStack(Blocks.STONE), 0.1F);
      }

   }

   private void addOreDict() {
      if (glowceliumEnabled) {
         addOreDict("mushroomAny", Blocks.RED_MUSHROOM);
         addOreDict("mushroomAny", Blocks.BROWN_MUSHROOM);
         addOreDict("mushroomAny", glowshroom);
      }

      if (firestoneEnabled) {
         addOreDict("stoneBrimstone", ProxyRegistry.newStack(biome_cobblestone));
         addOreDict("stoneBrimstonePolished", ProxyRegistry.newStack(biome_brick));
      }

      if (icystoneEnabled) {
         addOreDict("stonePermafrost", ProxyRegistry.newStack(biome_cobblestone, 1, 1));
         addOreDict("stonePermafrostPolished", ProxyRegistry.newStack(biome_cobblestone, 1, 1));
      }

      if (elderPrismarineEnabled) {
         addOreDict("blockPrismarineElder", ProxyRegistry.newStack(elder_prismarine));
         addOreDict("blockPrismarine", ProxyRegistry.newStack(elder_prismarine));
         addOreDict("blockPrismarineElderBrick", ProxyRegistry.newStack(elder_prismarine, 1, 1));
         addOreDict("blockPrismarineBrick", ProxyRegistry.newStack(elder_prismarine, 1, 1));
         addOreDict("blockPrismarineElderDark", ProxyRegistry.newStack(elder_prismarine, 1, 2));
         addOreDict("blockPrismarineDark", ProxyRegistry.newStack(elder_prismarine, 1, 2));
      }

   }

   @SubscribeEvent
   public void onOreGenerate(GenerateMinable event) {
      if (event.getType() == EventType.DIRT) {
         World world = event.getWorld();
         BlockPos pos = event.getPos();
         Chunk chunk = world.getChunkFromBlockCoords(pos);

         for(UndergroundBiomeGenerator gen : biomes) {
            gen.generate(chunk.x, chunk.z, world);
         }
      }

   }

   public boolean hasOreGenSubscriptions() {
      return true;
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   private UndergroundBiomeGenerator loadUndergroundBiomeInfo(String name, UndergroundBiome biome, int rarity, Type... biomes) {
      String category = this.configCategory + "." + name;
      UndergroundBiomes.UndergroundBiomeInfo info = new UndergroundBiomes.UndergroundBiomeInfo(category, biome, rarity, biomes);
      return new UndergroundBiomeGenerator(info);
   }

   public static class UndergroundBiomeInfo {
      public final boolean enabled;
      public final UndergroundBiome biome;
      public final DimensionConfig dims;
      public final List<Type> types;
      public final int rarity;
      public final int minXSize;
      public final int minYSize;
      public final int minZSize;
      public final int xVariation;
      public final int yVariation;
      public final int zVariation;
      public final int minY;
      public final int maxY;

      private UndergroundBiomeInfo(String category, UndergroundBiome biome, int rarity, Type... biomes) {
         this.enabled = ModuleLoader.config.getBoolean("Enabled", category, true, "");
         this.biome = biome;
         this.types = BiomeTypeConfigHandler.parseBiomeTypeArrayConfig("Allowed Biome Types", category, biomes);
         this.rarity = ModuleLoader.config.getInt("Rarity", category, rarity, 0, Integer.MAX_VALUE, "This biome will spawn in 1 of X valid chunks");
         this.dims = new DimensionConfig(category);
         this.minY = ModuleLoader.config.getInt("Minimum Y Level", category, 10, 0, 255, "");
         this.maxY = ModuleLoader.config.getInt("Maximum Y Level", category, 40, 0, 255, "");
         this.minXSize = ModuleLoader.config.getInt("X Minimum", category, 26, 0, Integer.MAX_VALUE, "");
         this.minYSize = ModuleLoader.config.getInt("Y Minimum", category, 12, 0, Integer.MAX_VALUE, "");
         this.minZSize = ModuleLoader.config.getInt("Z Minimum", category, 26, 0, Integer.MAX_VALUE, "");
         this.xVariation = ModuleLoader.config.getInt("X Variation", category, 14, 0, Integer.MAX_VALUE, "");
         this.yVariation = ModuleLoader.config.getInt("Y Variation", category, 6, 0, Integer.MAX_VALUE, "");
         this.zVariation = ModuleLoader.config.getInt("Z Variation", category, 14, 0, Integer.MAX_VALUE, "");
         biome.setupBaseConfig(category);
      }
   }
}
