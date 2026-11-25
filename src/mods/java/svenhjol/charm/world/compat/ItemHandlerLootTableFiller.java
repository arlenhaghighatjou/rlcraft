package svenhjol.charm.world.compat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerLootTableFiller {
   public ItemHandlerLootTableFiller() {
   }

   public static void fillWithLoot(IItemHandler inventory, World world, ResourceLocation lootTable, int lootSize) {
      if (!world.isRemote) {
         LootTable table = world.getLootTableManager().getLootTableFromLocation(lootTable);
         Builder builder = new Builder((WorldServer)world);
         LootContext context = builder.build();
         List<ItemStack> list = generateLootForPools(table, world.rand, context, lootSize);
         List<Integer> slots = new ArrayList();

         for(int i = 0; i < inventory.getSlots(); ++i) {
            slots.add(Integer.valueOf(i));
         }

         Collections.shuffle(slots);
         int i = 0;

         for(ItemStack item : list) {
            if (i < inventory.getSlots()) {
               inventory.insertItem(((Integer)slots.get(i++)).intValue(), item, false);
            }
         }
      }

   }

   private static List<ItemStack> generateLootForPools(LootTable table, Random rand, LootContext context, int lootSize) {
      List<ItemStack> loot = table.generateLootForPools(rand, context);
      if (lootSize > 0) {
         int i = 0;

         while(loot.size() < lootSize && i++ < 10) {
            loot.addAll(table.generateLootForPools(rand, context));
         }

         if (loot.size() > lootSize) {
            loot = loot.subList(0, lootSize);
         }
      }

      return loot;
   }
}
