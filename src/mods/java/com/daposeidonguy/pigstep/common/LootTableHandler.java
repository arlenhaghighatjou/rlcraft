package com.daposeidonguy.pigstep.common;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.daposeidonguy.pigstep.common.items.ItemHandler;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryEmpty;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;

public class LootTableHandler {
   public static LootEntry entry = new LootEntryItem(ItemHandler.discPigstep, 10, 50, new LootFunction[0], new LootCondition[0], "pigstep:loot_pigstep");
   public static LootEntry empty = new LootEntryEmpty(90, 50, new LootCondition[0], "pigstep:empty");

   public LootTableHandler() {
   }

   public static void register(EventBus bus) {
      bus.register(LootTableLoadEvent.class, LootTableHandler::lootLoad);
   }

   public static void lootLoad(LootTableLoadEvent event) {
      if (event.getName().toString().equals("minecraft:chests/nether_bridge")) {
         event.getTable().addPool(new LootPool(new LootEntry[]{entry, empty}, new LootCondition[0], new RandomValueRange(1.0F), new RandomValueRange(0.0F), "pigstep:pool_pigstep"));
      }

   }
}
