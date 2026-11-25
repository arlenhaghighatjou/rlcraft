package svenhjol.meson.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class LootHelper {
   private static Map<LootHelper.RARITY, HashMap<LootHelper.TYPE, List<ResourceLocation>>> lootTables = new HashMap();

   public LootHelper() {
   }

   public static ResourceLocation addLootLocation(LootHelper.RARITY rarity, LootHelper.TYPE type, ResourceLocation location) {
      checkLootTable(rarity, type);
      ((List)((HashMap)lootTables.get(rarity)).get(type)).add(location);
      return location;
   }

   public static void addLootLocation(LootHelper.RARITY rarity, LootHelper.TYPE type, List<ResourceLocation> locations) {
      checkLootTable(rarity, type);
      ((List)((HashMap)lootTables.get(rarity)).get(type)).addAll(locations);
   }

   public static void addToVanillaLoot(String modId, LootTable table, ResourceLocation res) {
      LootPool pool = new LootPool(new LootEntry[]{new LootEntryTable(res, 1, 0, new LootCondition[0], modId)}, new LootCondition[0], new RandomValueRange(1.0F), new RandomValueRange(0.0F), modId);
      table.addPool(pool);
   }

   public static void addToLootTable(LootTable table, Item item, int weight, int quality, @Nullable LootFunction[] functions, @Nullable LootCondition[] conditions) {
      if (functions == null) {
         functions = new LootFunction[0];
      }

      if (conditions == null) {
         conditions = new LootCondition[0];
      }

      LootEntryItem lootItem = new LootEntryItem(item, weight, quality, functions, conditions, ((ResourceLocation)Objects.requireNonNull(item.getRegistryName())).toString());
      LootPool pool = table.getPool("main");
      if (pool != null) {
         pool.addEntry(lootItem);
      }

   }

   public static List<ResourceLocation> getLootTables(LootHelper.RARITY rarity, LootHelper.TYPE type) {
      checkLootTable(rarity, type);
      return (List)((List)((HashMap)lootTables.get(rarity)).get(type)).stream().distinct().collect(Collectors.toList());
   }

   public static ResourceLocation getRandomLootTable(LootHelper.RARITY rarity, LootHelper.TYPE type) {
      List<ResourceLocation> tables = getLootTables(rarity, type);
      return (ResourceLocation)tables.get((new EaglercraftRandom()).nextInt(tables.size()));
   }

   private static void checkLootTable(LootHelper.RARITY rarity, LootHelper.TYPE type) {
      if (!lootTables.containsKey(rarity)) {
         lootTables.put(rarity, new HashMap());
      }

      if (!((HashMap)lootTables.get(rarity)).containsKey(type)) {
         ((HashMap)lootTables.get(rarity)).put(type, new ArrayList());
      }

   }

   static {
      lootTables.put(LootHelper.RARITY.COMMON, new HashMap<LootHelper.TYPE, List<ResourceLocation>>() {
         {
            this.put(LootHelper.TYPE.MISC, new ArrayList<ResourceLocation>() {
               {
                  this.add(LootTableList.CHESTS_VILLAGE_BLACKSMITH);
                  this.add(LootTableList.CHESTS_IGLOO_CHEST);
               }
            });
         }
      });
      lootTables.put(LootHelper.RARITY.UNCOMMON, new HashMap<LootHelper.TYPE, List<ResourceLocation>>() {
         {
            this.put(LootHelper.TYPE.MISC, new ArrayList<ResourceLocation>() {
               {
                  this.add(LootTableList.CHESTS_SIMPLE_DUNGEON);
                  this.add(LootTableList.CHESTS_JUNGLE_TEMPLE);
                  this.add(LootTableList.CHESTS_WOODLAND_MANSION);
                  this.add(LootTableList.CHESTS_DESERT_PYRAMID);
                  this.add(LootTableList.CHESTS_ABANDONED_MINESHAFT);
               }
            });
         }
      });
      lootTables.put(LootHelper.RARITY.VALUABLE, new HashMap<LootHelper.TYPE, List<ResourceLocation>>() {
         {
            this.put(LootHelper.TYPE.MISC, new ArrayList<ResourceLocation>() {
               {
                  this.add(LootTableList.CHESTS_STRONGHOLD_LIBRARY);
                  this.add(LootTableList.CHESTS_STRONGHOLD_CROSSING);
                  this.add(LootTableList.CHESTS_STRONGHOLD_CORRIDOR);
                  this.add(LootTableList.CHESTS_NETHER_BRIDGE);
               }
            });
         }
      });
      lootTables.put(LootHelper.RARITY.RARE, new HashMap<LootHelper.TYPE, List<ResourceLocation>>() {
         {
            this.put(LootHelper.TYPE.MISC, new ArrayList<ResourceLocation>() {
               {
                  this.add(LootTableList.CHESTS_END_CITY_TREASURE);
               }
            });
         }
      });
   }

   public static enum RARITY {
      COMMON,
      UNCOMMON,
      VALUABLE,
      RARE,
      SPECIAL;

      private RARITY() {
      }
   }

   public static enum TYPE {
      BOOK,
      MISC,
      POTION;

      private TYPE() {
      }
   }
}
