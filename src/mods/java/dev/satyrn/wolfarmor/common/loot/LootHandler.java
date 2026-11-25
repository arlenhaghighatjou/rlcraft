package dev.satyrn.wolfarmor.common.loot;

import dev.satyrn.wolfarmor.api.util.Resources;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LootHandler {
   private LootHandler() {
      MinecraftForge.EVENT_BUS.register(this);
      LootTableList.register(Resources.LOOT_TABLE_DUNGEON_CHEST_INJECT);
   }

   public static void init() {
      new LootHandler();
   }

   @SubscribeEvent
   public void dungeonLootLoad(LootTableLoadEvent event) {
      String name = event.getName().toString();
      String chests = "minecraft:chests/";
      if (name.startsWith(chests)) {
         String fileName = name.substring(chests.length());
         byte var6 = -1;
         switch(fileName.hashCode()) {
         case -2145596913:
            if (fileName.equals("jungle_temple")) {
               var6 = 2;
            }
            break;
         case -1469222421:
            if (fileName.equals("stronghold_crossing")) {
               var6 = 6;
            }
            break;
         case 88800038:
            if (fileName.equals("desert_pyramid")) {
               var6 = 1;
            }
            break;
         case 247895091:
            if (fileName.equals("stronghold_corridor")) {
               var6 = 5;
            }
            break;
         case 865515868:
            if (fileName.equals("abandoned_mineshaft")) {
               var6 = 0;
            }
            break;
         case 942643415:
            if (fileName.equals("village_blacksmith")) {
               var6 = 7;
            }
            break;
         case 1198563629:
            if (fileName.equals("simple_dungeon")) {
               var6 = 4;
            }
            break;
         case 1262642512:
            if (fileName.equals("nether_bridge")) {
               var6 = 3;
            }
         }

         switch(var6) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
            event.getTable().addPool(this.getInjectPool(Resources.LOOT_TABLE_DUNGEON_CHEST_INJECT));
         }
      }

   }

   private LootPool getInjectPool(ResourceLocation lootDungeonInject) {
      return new LootPool(new LootEntry[]{this.getInjectEntry(lootDungeonInject)}, new LootCondition[0], new RandomValueRange(1.0F), new RandomValueRange(1.0F), "wolfarmor_injected_pool");
   }

   private LootEntry getInjectEntry(ResourceLocation lootDungeonInject) {
      return new LootEntryTable(lootDungeonInject, 1, 0, new LootCondition[0], "wolfarmor_injected_entry");
   }
}
