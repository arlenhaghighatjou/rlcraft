package svenhjol.charm.world.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.BiomeEvent.GetVillageBlockID;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import svenhjol.meson.Feature;

public class MoreVillageBiomes extends Feature {
   public static Map<String, List<Biome>> biomes = new HashMap();
   public static List<Biome> allBiomes = new ArrayList();
   public static String[] plainsBiomes;
   public static String[] jungleBiomes;
   public static String[] savannaBiomes;
   public static String[] coldBiomes;
   public static String[] desertBiomes;

   public MoreVillageBiomes() {
   }

   public String getDescription() {
      return "Villages spawn in more biomes.  The type of biome determines what materials the village buildings are constructed from.";
   }

   public void configure() {
      super.configure();
      coldBiomes = this.propStringList("Cold biomes", "Cold biomes where villages are constructed from Taiga wood.", new String[]{"taiga", "mutated_taiga", "mutated_taiga_cold", "ice_flats", "mutated_ice_flats"});
      jungleBiomes = this.propStringList("Jungle biomes", "Jungle biomes where villages are constructed from Jungle wood.", new String[]{"jungle", "mutated_jungle"});
      savannaBiomes = this.propStringList("Savanna biomes", "Savanna biomes where villages are constructed from Acacia wood.", new String[]{"savanna", "mutated_savanna"});
      plainsBiomes = this.propStringList("Plains biomes", "Plains biomes where villages are constructed from Oak wood.", new String[]{"plains", "mutated_plains", "swampland", "mutated_swampland"});
      desertBiomes = this.propStringList("Desert biomes", "Desert biomes where villages are made from sandstone.", new String[]{"desert", "mutated_desert"});
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      biomes.put("cold", this.makeBiomeList(coldBiomes));
      biomes.put("jungle", this.makeBiomeList(jungleBiomes));
      biomes.put("savanna", this.makeBiomeList(savannaBiomes));
      biomes.put("plains", this.makeBiomeList(plainsBiomes));
      biomes.put("desert", this.makeBiomeList(desertBiomes));

      for(String biomeSet : biomes.keySet()) {
         allBiomes.addAll((Collection)biomes.get(biomeSet));
      }

      MapGenVillage.VILLAGE_SPAWN_BIOMES = allBiomes;
   }

   private List<Biome> makeBiomeList(String[] biomes) {
      List<Biome> biomeList = new ArrayList();
      if (biomes.length > 0) {
         for(String b : biomes) {
            biomeList.add(Biome.REGISTRY.getObject(new ResourceLocation(b)));
         }
      }

      return biomeList;
   }

   @SubscribeEvent
   public void getVillageBlockID(GetVillageBlockID event) {
      IBlockState original = event.getOriginal();
      Block block = original.getBlock();
      Biome biome = event.getBiome();
      if (biome != null) {
         IBlockState replacement = null;
         EnumType planks = null;
         Block stairs = null;
         Block fence = null;
         Block door = null;
         if (((List)biomes.get("cold")).contains(biome) || ((List)biomes.get("jungle")).contains(biome)) {
            planks = getWoodForBiome(biome);
            fence = getFenceForWood(planks);
            stairs = getStairsForWood(planks);
            door = getDoorForWood(planks);
         }

         if (block == Blocks.PLANKS && planks != null) {
            replacement = original.withProperty(BlockPlanks.VARIANT, planks);
         }

         if (block == Blocks.LOG && planks != null) {
            replacement = original.withProperty(BlockOldLog.VARIANT, planks).withProperty(BlockLog.LOG_AXIS, original.getValue(BlockLog.LOG_AXIS));
         }

         if (block == Blocks.OAK_STAIRS && stairs != null) {
            replacement = stairs.getDefaultState().withProperty(BlockStairs.FACING, original.getValue(BlockStairs.FACING)).withProperty(BlockStairs.HALF, original.getValue(BlockStairs.HALF)).withProperty(BlockStairs.SHAPE, original.getValue(BlockStairs.SHAPE));
         }

         if (block == Blocks.OAK_FENCE && fence != null) {
            replacement = fence.getDefaultState();
         }

         if (block instanceof BlockDoor && door != null) {
            replacement = door.getDefaultState();
         }

         if (replacement != null) {
            event.setReplacement(replacement);
            event.setResult(Result.DENY);
         }

      }
   }

   public static EnumType getWoodForBiome(Biome biome) {
      if (((List)biomes.get("cold")).contains(biome)) {
         return EnumType.SPRUCE;
      } else if (((List)biomes.get("jungle")).contains(biome)) {
         return EnumType.JUNGLE;
      } else {
         return ((List)biomes.get("savanna")).contains(biome) ? EnumType.ACACIA : EnumType.OAK;
      }
   }

   public static BlockFence getFenceForWood(EnumType wood) {
      if (wood == EnumType.SPRUCE) {
         return (BlockFence)Blocks.SPRUCE_FENCE;
      } else if (wood == EnumType.JUNGLE) {
         return (BlockFence)Blocks.JUNGLE_FENCE;
      } else {
         return wood == EnumType.ACACIA ? (BlockFence)Blocks.ACACIA_FENCE : (BlockFence)Blocks.OAK_FENCE;
      }
   }

   public static BlockStairs getStairsForWood(EnumType wood) {
      if (wood == EnumType.SPRUCE) {
         return (BlockStairs)Blocks.SPRUCE_STAIRS;
      } else if (wood == EnumType.JUNGLE) {
         return (BlockStairs)Blocks.JUNGLE_STAIRS;
      } else {
         return wood == EnumType.ACACIA ? (BlockStairs)Blocks.ACACIA_STAIRS : (BlockStairs)Blocks.OAK_STAIRS;
      }
   }

   public static BlockDoor getDoorForWood(EnumType wood) {
      if (wood == EnumType.SPRUCE) {
         return Blocks.SPRUCE_DOOR;
      } else if (wood == EnumType.JUNGLE) {
         return Blocks.JUNGLE_DOOR;
      } else {
         return wood == EnumType.ACACIA ? Blocks.ACACIA_DOOR : Blocks.OAK_DOOR;
      }
   }

   public static BlockDoor villageDoorsForBiome(Start start) {
      if (start != null && start.biome != null) {
         Biome biome = start.biome;
         IBlockState door = Blocks.OAK_DOOR.getDefaultState();
         GetVillageBlockID event = new GetVillageBlockID(biome, door);
         MinecraftForge.TERRAIN_GEN_BUS.post(event);
         if (event.getResult() == Result.DENY) {
            return (BlockDoor)event.getReplacement().getBlock();
         } else {
            if (biome == Biomes.SAVANNA) {
               door = Blocks.ACACIA_DOOR.getDefaultState();
            }

            if (biome == Biomes.TAIGA) {
               door = Blocks.SPRUCE_DOOR.getDefaultState();
            }

            return (BlockDoor)door.getBlock();
         }
      } else {
         return Blocks.OAK_DOOR;
      }
   }

   public boolean hasTerrainSubscriptions() {
      return true;
   }
}
