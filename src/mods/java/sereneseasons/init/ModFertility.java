package sereneseasons.init;

import java.util.HashMap;
import java.util.HashSet;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.FertilityConfig;
import sereneseasons.config.SeasonsConfig;

public class ModFertility {
   private static HashSet<String> springPlants = new HashSet();
   private static HashSet<String> summerPlants = new HashSet();
   private static HashSet<String> autumnPlants = new HashSet();
   private static HashSet<String> winterPlants = new HashSet();
   private static HashSet<String> allListedPlants = new HashSet();
   private static HashMap<String, Integer> seedSeasons = new HashMap();

   public ModFertility() {
   }

   public static void init() {
      initSeasonCrops(FertilityConfig.seasonal_fertility.spring_crops, springPlants, 1);
      initSeasonCrops(FertilityConfig.seasonal_fertility.summer_crops, summerPlants, 2);
      initSeasonCrops(FertilityConfig.seasonal_fertility.autumn_crops, autumnPlants, 4);
      initSeasonCrops(FertilityConfig.seasonal_fertility.winter_crops, winterPlants, 8);
   }

   public static boolean isCropFertile(String cropName, World world, BlockPos pos) {
      Season season = SeasonHelper.getSeasonState(world).getSeason();
      Biome biome = world.getBiome(pos);
      if (BiomeConfig.disablesCrops(biome)) {
         return false;
      } else if (FertilityConfig.general_category.seasonal_crops && BiomeConfig.enablesSeasonalEffects(biome) && SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension())) {
         if (BiomeConfig.usesTropicalSeasons(biome)) {
            return summerPlants.contains(cropName) || !allListedPlants.contains(cropName);
         } else if (biome.getTemperature(pos) < 0.15F) {
            return winterPlants.contains(cropName);
         } else if (season == Season.SPRING && springPlants.contains(cropName)) {
            return true;
         } else if (season == Season.SUMMER && summerPlants.contains(cropName)) {
            return true;
         } else if (season == Season.AUTUMN && autumnPlants.contains(cropName)) {
            return true;
         } else if (season == Season.WINTER && winterPlants.contains(cropName)) {
            return true;
         } else if (!allListedPlants.contains(cropName)) {
            return season == Season.WINTER ? FertilityConfig.general_category.ignore_unlisted_crops : true;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   private static void initSeasonCrops(String[] seeds, HashSet<String> cropSet, int bitmask) {
      for(String seed : seeds) {
         Item item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(seed));
         if (item instanceof IPlantable) {
            String plantName = ((IPlantable)item).getPlant((IBlockAccess)null, (BlockPos)null).getBlock().getRegistryName().toString();
            cropSet.add(plantName);
            if (bitmask != 0) {
               allListedPlants.add(plantName);
               if (seedSeasons.containsKey(seed)) {
                  int seasons = ((Integer)seedSeasons.get(seed)).intValue();
                  seedSeasons.put(seed, Integer.valueOf(seasons | bitmask));
               } else {
                  seedSeasons.put(seed, Integer.valueOf(bitmask));
               }
            }
         } else {
            Block block = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(seed));
            if (block != null && block != Blocks.AIR) {
               String plantName = block.getRegistryName().toString();
               cropSet.add(plantName);
               if (bitmask != 0) {
                  allListedPlants.add(plantName);
                  if (seedSeasons.containsKey(seed)) {
                     int seasons = ((Integer)seedSeasons.get(seed)).intValue();
                     seedSeasons.put(seed, Integer.valueOf(seasons | bitmask));
                  } else {
                     seedSeasons.put(seed, Integer.valueOf(bitmask));
                  }
               }
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public static void setupTooltips(ItemTooltipEvent event) {
      if (FertilityConfig.general_category.crop_tooltips && FertilityConfig.general_category.seasonal_crops) {
         String name = event.getItemStack().getItem().getRegistryName().toString();
         if (seedSeasons.containsKey(name)) {
            int mask = ((Integer)seedSeasons.get(name)).intValue();
            event.getToolTip().add("Fertile Seasons:");
            if ((mask & 1) != 0 && (mask & 2) != 0 && (mask & 4) != 0 && (mask & 8) != 0) {
               event.getToolTip().add(TextFormatting.LIGHT_PURPLE + " Year-Round");
            } else {
               if ((mask & 1) != 0) {
                  event.getToolTip().add(TextFormatting.GREEN + " Spring");
               }

               if ((mask & 2) != 0) {
                  event.getToolTip().add(TextFormatting.YELLOW + " Summer");
               }

               if ((mask & 4) != 0) {
                  event.getToolTip().add(TextFormatting.GOLD + " Autumn");
               }

               if ((mask & 8) != 0) {
                  event.getToolTip().add(TextFormatting.AQUA + " Winter");
               }
            }
         }
      }

   }
}
