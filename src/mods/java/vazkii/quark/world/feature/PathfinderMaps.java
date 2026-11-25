package vazkii.quark.world.feature;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration.Type;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.experimental.world.BiomeLocator;

public class PathfinderMaps extends Feature {
   public static Multimap<Integer, PathfinderMaps.TradeInfo> trades;
   public static boolean unlockAllAtOnce;
   public static boolean multipleAtFirstUnlock;
   public static boolean printAllBiomeNames;
   private String[] customs;

   public PathfinderMaps() {
   }

   private static String getBiomeDescriptor(Biome biome) {
      ResourceLocation rl = biome.getRegistryName();
      return rl == null ? "unknown" : rl.getResourcePath();
   }

   public void setupConfig() {
      trades = HashMultimap.create();
      unlockAllAtOnce = this.loadPropBool("Unlock All Trades at Once", "By default, when a Cartographer levels up, a random Pathfinder Map from that level is added to their trades.\nSet this to true to add all the maps from that level to the trades instead.", false);
      multipleAtFirstUnlock = this.loadPropBool("Unlock Multiples At Level 2", "By default, when a Cartographer evolves to level 2, two or three Pathfinder Maps are unlocked.\nSet this to false to disable this, and make it only unlock one, like in the other levels.", true);
      this.loadTradeInfo(Biomes.ICE_PLAINS, true, 2, 8, 14, 8381695, "ice_plains");
      this.loadTradeInfo(Biomes.EXTREME_HILLS, true, 2, 8, 14, 9079434);
      this.loadTradeInfo(Biomes.ROOFED_FOREST, true, 2, 8, 14, 22794);
      this.loadTradeInfo(Biomes.DESERT, true, 2, 8, 14, 13416782);
      this.loadTradeInfo(Biomes.SAVANNA, true, 2, 8, 14, 10200418);
      this.loadTradeInfo(Biomes.SWAMPLAND, true, 3, 12, 18, 2242319);
      this.loadTradeInfo(Biomes.REDWOOD_TAIGA, true, 3, 12, 18, 5980703);
      this.loadTradeInfo(Biomes.MUTATED_FOREST, true, 3, 12, 18, 14449642, "flower_forest");
      this.loadTradeInfo(Biomes.JUNGLE, true, 4, 16, 22, 2274816);
      this.loadTradeInfo(Biomes.MESA, true, 4, 16, 22, 13008674);
      this.loadTradeInfo(Biomes.MUSHROOM_ISLAND, true, 5, 20, 26, 5063283);
      this.loadTradeInfo(Biomes.MUTATED_ICE_FLATS, true, 5, 20, 26, 4314825, "ice_spikes");
      String desc = "In this section you can add custom Pathfinder Maps. This works for both vanilla and modded biomes.\nEach custom map must be on its own line.\nThe format for a custom map is as follows:\n<id>,<level>,<min_price>,<max_price>,<color>,<name>\n\nWith the following descriptions:\n - <id> being the biome's ID NAME. You can find vanilla names here - https://minecraft.gamepedia.com/Biome#Biome_IDs\n - <level> being the Cartographer villager level required for the map to be unlockable\n - <min_price> being the cheapest (in Emeralds) the map can be\n - <max_price> being the most expensive (in Emeralds) the map can be\n - <color> being a hex color (without the #) for the map to display. You can generate one here - http://htmlcolorcodes.com/\n - <name> being the display name of the map\n\nHere's an example of a map to locate Ice Mountains:\nminecraft:ice_mountains,2,8,14,7FE4FF,Ice Mountains Pathfinder Map";
      this.customs = this.loadPropStringList("Custom Map Info", desc, new String[0]);
   }

   @SubscribeEvent
   public void onRegisterVillagers(Register<VillagerProfession> event) {
      this.loadCustomMaps(this.customs);
      VillagerProfession librarian = (VillagerProfession)event.getRegistry().getValue(new ResourceLocation("minecraft:librarian"));
      if (librarian != null) {
         VillagerCareer cartographer = librarian.getCareer(1);
         Iterator var4 = trades.keySet().iterator();

         while(var4.hasNext()) {
            int level = ((Integer)var4.next()).intValue();
            cartographer.addTrade(level, new ITradeList[]{new PathfinderMaps.PathfinderMapTrade(level)});
         }
      }

   }

   private void loadTradeInfo(Biome biome, boolean enabled, int level, int minPrice, int maxPrice, int color) {
      this.loadTradeInfo(biome, enabled, level, minPrice, maxPrice, color, "", "");
   }

   private void loadTradeInfo(Biome biome, boolean enabled, int level, int minPrice, int maxPrice, int color, String overrideCategory) {
      this.loadTradeInfo(biome, enabled, level, minPrice, maxPrice, color, overrideCategory, "");
   }

   private void loadTradeInfo(Biome biome, boolean enabled, int level, int minPrice, int maxPrice, int color, String overrideCategory, String overrideName) {
      String category = this.configCategory + ".";
      if (!overrideCategory.isEmpty()) {
         category = category + overrideCategory;
      } else {
         category = category + getBiomeDescriptor(biome);
      }

      PathfinderMaps.TradeInfo info;
      if (overrideName.isEmpty()) {
         info = new PathfinderMaps.TradeInfo(category, biome, enabled, level, minPrice, maxPrice, color);
      } else {
         info = new PathfinderMaps.TradeInfo(category, biome, enabled, level, minPrice, maxPrice, color, overrideName);
      }

      if (info.enabled) {
         trades.put(Integer.valueOf(info.level), info);
      }

   }

   private void loadTradeInfo(String line) throws IllegalArgumentException {
      String[] tokens = line.split(",");
      if (tokens.length != 6) {
         throw new IllegalArgumentException("Wrong number of parameters " + tokens.length + " (expected 6)");
      } else {
         ResourceLocation biomeName = new ResourceLocation(tokens[0]);
         if (!Biome.REGISTRY.containsKey(biomeName)) {
            throw new IllegalArgumentException("No biome exists with name " + biomeName);
         } else {
            Biome biome = (Biome)Biome.REGISTRY.getObject(biomeName);
            int level = Integer.parseInt(tokens[1]);
            int minPrice = Integer.parseInt(tokens[2]);
            int maxPrice = Integer.parseInt(tokens[3]);
            int color = Integer.parseInt(tokens[4], 16);
            String name = tokens[5];
            this.loadTradeInfo(biome, true, level, minPrice, maxPrice, color, "", name);
         }
      }
   }

   private void loadCustomMaps(String[] lines) {
      for(String s : lines) {
         try {
            this.loadTradeInfo(s);
         } catch (IllegalArgumentException var7) {
            Quark.LOG.warn("[Custom Pathfinder Maps] Error while reading custom map string \"%s\"", s);
            Quark.LOG.warn("[Custom Pathfinder Maps] - %s", var7.getMessage());
         }
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public static ItemStack createMap(World world, BlockPos pos, PathfinderMaps.TradeInfo info) {
      BlockPos biomePos = BiomeLocator.spiralOutwardsLookingForBiome(world, info.biome, (double)pos.getX(), (double)pos.getZ());
      if (biomePos == null) {
         return ItemStack.EMPTY;
      } else {
         int id = world.getUniqueDataId("map");
         ItemStack stack = new ItemStack(Items.FILLED_MAP, 1, id);
         stack.setTranslatableName(info.name);
         NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, "display", false);
         cmp.setInteger("MapColor", info.color);
         ItemNBTHelper.setCompound(stack, "display", cmp);
         String s = "map_" + id;
         MapData mapdata = new MapData(s);
         world.setData(s, mapdata);
         mapdata.scale = 2;
         mapdata.xCenter = biomePos.getX() + (int)((Math.random() - 0.5D) * 200.0D);
         mapdata.zCenter = biomePos.getZ() + (int)((Math.random() - 0.5D) * 200.0D);
         mapdata.dimension = 0;
         mapdata.trackingPosition = true;
         mapdata.unlimitedTracking = true;
         ItemMap.renderBiomePreviewMap(world, stack);
         MapData.addTargetDecoration(stack, biomePos, "+", Type.TARGET_X);
         return stack;
      }
   }

   private static class PathfinderMapTrade implements ITradeList {
      public final int level;

      public PathfinderMapTrade(int level) {
         this.level = level;
      }

      public void addMerchantRecipe(@Nonnull IMerchant merchant, @Nonnull MerchantRecipeList recipeList, @Nonnull EaglercraftRandom random) {
         List<PathfinderMaps.TradeInfo> tradeInfo = new ArrayList(PathfinderMaps.trades.get(Integer.valueOf(this.level)));
         if (!tradeInfo.isEmpty()) {
            if (PathfinderMaps.unlockAllAtOnce) {
               for(PathfinderMaps.TradeInfo info : tradeInfo) {
                  this.unlock(merchant, recipeList, random, info);
               }
            } else {
               int amount = this.level == 2 && PathfinderMaps.multipleAtFirstUnlock ? Math.min(tradeInfo.size(), 2 + random.nextInt(2)) : 1;

               for(int i = 0; i < amount; ++i) {
                  PathfinderMaps.TradeInfo info = (PathfinderMaps.TradeInfo)tradeInfo.get(random.nextInt(tradeInfo.size()));
                  this.unlock(merchant, recipeList, random, info);
                  tradeInfo.remove(info);
               }
            }

         }
      }

      private void unlock(IMerchant merchant, MerchantRecipeList recipeList, EaglercraftRandom random, PathfinderMaps.TradeInfo info) {
         int i = random.nextInt(info.maxPrice - info.minPrice + 1) + info.minPrice;
         ItemStack itemstack = PathfinderMaps.createMap(merchant.getWorld(), merchant.getPos(), info);
         if (!itemstack.isEmpty()) {
            MerchantRecipe recipe = new MerchantRecipe(new ItemStack(Items.EMERALD, i), new ItemStack(Items.COMPASS), itemstack, 0, 1);
            recipeList.add(recipe);
         }
      }
   }

   public static class TradeInfo {
      public final boolean enabled;
      public final Biome biome;
      public final int level;
      public final int minPrice;
      public final int maxPrice;
      public final int color;
      public final String name;

      TradeInfo(String category, Biome biome, boolean enabled, int level, int minPrice, int maxPrice, int color) {
         this(category, biome, enabled, level, minPrice, maxPrice, color, "quark.biomeMap." + PathfinderMaps.getBiomeDescriptor(biome));
      }

      TradeInfo(String category, Biome biome, boolean enabled, int level, int minPrice, int maxPrice, int color, String name) {
         this.enabled = ModuleLoader.config.getBoolean("Enabled", category, enabled, "");
         this.biome = biome;
         this.level = ModuleLoader.config.getInt("Required Villager Level", category, level, 0, 10, "");
         this.minPrice = ModuleLoader.config.getInt("Minimum Emerald Price", category, minPrice, 1, 64, "");
         this.maxPrice = Math.max(minPrice, ModuleLoader.config.getInt("Maximum Emerald Price", category, maxPrice, 1, 64, ""));
         this.color = color;
         this.name = name;
      }
   }
}
