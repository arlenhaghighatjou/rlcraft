package svenhjol.charm.world.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration.Type;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import svenhjol.meson.Feature;
import svenhjol.meson.helper.ItemNBTHelper;
import svenhjol.meson.helper.WorldHelper;
import svenhjol.meson.registry.VillagerRegistry;

public class StructureMaps extends Feature {
   public static int tradeLevel;
   public static int biomeMinCost;
   public static int biomeMaxCost;
   public static int generalMinCost;
   public static int generalMaxCost;
   public static List<String> biomeStructures = new ArrayList();
   public static List<String> generalStructures = new ArrayList();
   public static List<StructureMaps.Structure> structures = new ArrayList();

   public StructureMaps() {
   }

   public String getDescription() {
      return "Cartographers sell Structure Maps that can be used to find overworld structures.";
   }

   public void configure() {
      super.configure();
      biomeStructures = Arrays.asList(this.propStringList("Biome-specific structures", "List of biome-specific structures that are available as map locations.", new String[]{WorldHelper.StructureType.DESERT_PYRAMID.getName(), WorldHelper.StructureType.IGLOO.getName(), WorldHelper.StructureType.JUNGLE_PYRAMID.getName(), WorldHelper.StructureType.SWAMP_HUT.getName()}));
      generalStructures = Arrays.asList(this.propStringList("General structures", "List of general structures that are available as map locations.", new String[]{WorldHelper.StructureType.MINESHAFT.getName(), WorldHelper.StructureType.VILLAGE.getName()}));
      tradeLevel = this.propInt("Trade level", "The level at which a cartographer will be unlocked for trading structure maps.", 3);
      generalMinCost = this.propInt("General structures minimum cost", "Minimum emerald cost of a general structure map.", 4);
      generalMaxCost = this.propInt("General structures maximum cost", "Maximum emerald cost of a general structure map.", 7);
      biomeMinCost = this.propInt("Biome-specific minimum cost", "Minimum emerald cost of a biome-specific structure map.", 16);
      biomeMaxCost = this.propInt("Biome-specific maximum cost", "Maximum emerald cost of a biome-specific structure map.", 22);
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      if (generalStructures.contains(WorldHelper.StructureType.MINESHAFT.getName())) {
         structures.add(new StructureMaps.Structure(WorldHelper.StructureType.MINESHAFT, 7816192));
      }

      if (generalStructures.contains(WorldHelper.StructureType.VILLAGE.getName())) {
         structures.add(new StructureMaps.Structure(WorldHelper.StructureType.VILLAGE, 13377536));
      }

      if (biomeStructures.contains(WorldHelper.StructureType.IGLOO.getName())) {
         structures.add(new StructureMaps.Structure(WorldHelper.StructureType.IGLOO, 10535167));
      }

      if (biomeStructures.contains(WorldHelper.StructureType.DESERT_PYRAMID.getName())) {
         structures.add(new StructureMaps.Structure(WorldHelper.StructureType.DESERT_PYRAMID, 8807936));
      }

      if (biomeStructures.contains(WorldHelper.StructureType.JUNGLE_PYRAMID.getName())) {
         structures.add(new StructureMaps.Structure(WorldHelper.StructureType.JUNGLE_PYRAMID, 2142240));
      }

      if (biomeStructures.contains(WorldHelper.StructureType.SWAMP_HUT.getName())) {
         structures.add(new StructureMaps.Structure(WorldHelper.StructureType.SWAMP_HUT, 1077248));
      }

      if (biomeStructures.contains(WorldHelper.StructureType.STRONGHOLD.getName())) {
         structures.add(new StructureMaps.Structure(WorldHelper.StructureType.STRONGHOLD, 5263440));
      }

      if (biomeStructures.contains(WorldHelper.StructureType.MANSION.getName())) {
         structures.add(new StructureMaps.Structure(WorldHelper.StructureType.MANSION, 7816192));
      }

   }

   @SubscribeEvent
   public void onRegisterVillagers(Register<VillagerProfession> event) {
      VillagerCareer cartographer = VillagerRegistry.cartographer;
      cartographer.addTrade(tradeLevel, new ITradeList[]{new StructureMaps.StructureMapTrade()});
   }

   public static ItemStack createMap(World world, BlockPos pos, StructureMaps.Structure structure) {
      BlockPos offset = pos.offset(EnumFacing.random(world.rand), 250);
      BlockPos structurePos = WorldHelper.getNearestStructure(world, offset, structure.type);
      if (structurePos == null) {
         return new ItemStack(Items.MAP, 2);
      } else {
         int id = world.getUniqueDataId("map");
         ItemStack stack = new ItemStack(Items.FILLED_MAP, 1, id);
         stack.setTranslatableName("charm.map." + structure.type.getName());
         NBTTagCompound tag = ItemNBTHelper.getCompound(stack, "display");
         tag.setInteger("MapColor", structure.color);
         ItemNBTHelper.setCompound(stack, "display", tag);
         String mapId = "map_" + id;
         MapData mapData = new MapData(mapId);
         world.setData(mapId, mapData);
         mapData.scale = 2;
         mapData.xCenter = structurePos.getX() + (int)((Math.random() - 0.5D) * 200.0D);
         mapData.zCenter = structurePos.getZ() + (int)((Math.random() - 0.5D) * 200.0D);
         mapData.dimension = structure.dimension;
         mapData.trackingPosition = true;
         mapData.unlimitedTracking = true;
         ItemMap.renderBiomePreviewMap(world, stack);
         MapData.addTargetDecoration(stack, structurePos, "+", Type.TARGET_X);
         return stack;
      }
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public class Structure {
      public WorldHelper.StructureType type;
      public int dimension = 0;
      public int color = 0;

      public Structure(WorldHelper.StructureType type) {
         this.type = type;
      }

      public Structure(WorldHelper.StructureType type, int color) {
         this.type = type;
         this.color = color;
      }

      public Structure(WorldHelper.StructureType type, int color, int dimension) {
         this.type = type;
         this.dimension = dimension;
         this.color = color;
      }
   }

   public class StructureMapTrade implements ITradeList {
      public StructureMapTrade() {
      }

      public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
         if (!StructureMaps.structures.isEmpty()) {
            StructureMaps.Structure structure = (StructureMaps.Structure)StructureMaps.structures.get(random.nextInt(StructureMaps.structures.size()));
            ItemStack map = StructureMaps.createMap(merchant.getWorld(), merchant.getPos(), structure);
            int emeraldCount;
            if (StructureMaps.generalStructures.contains(structure.type.getName())) {
               emeraldCount = random.nextInt(StructureMaps.generalMaxCost - StructureMaps.generalMinCost) + StructureMaps.generalMinCost;
            } else {
               emeraldCount = random.nextInt(StructureMaps.biomeMaxCost - StructureMaps.biomeMinCost) + StructureMaps.biomeMinCost;
            }

            recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, emeraldCount), new ItemStack(Items.COMPASS), map, 0, 1));
         }
      }
   }
}
