package reborncore.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import reborncore.RebornRegistry;

public class LootManager {
   public static LootManager INSTANCE = new LootManager();

   private LootManager() {
   }

   @SubscribeEvent
   public void onLootTableLoad(LootTableLoadEvent evt) {
      LootManager.InnerPool lp = RebornRegistry.lp;

      for(LootItem item : lp.items) {
         if (item != null && item.getLootTableList() == evt.getName()) {
            LootPool main = evt.getTable().getPool("main");
            main.addEntry(new LootEntryItem(item.item.getItem(), (int)item.chance, 1, count(0.0F, (float)item.maxSize), new LootCondition[0], "Reborn Core"));
         }
      }

   }

   private static LootFunction[] count(float min, float max) {
      return new LootFunction[]{new SetCount(new LootCondition[0], new RandomValueRange(min, max))};
   }

   private static LootFunction[] countAndMeta(float minCount, float maxCount, int minMeta, int maxMeta) {
      return new LootFunction[]{new SetCount(new LootCondition[0], new RandomValueRange(minCount, maxCount)), new SetMetadata(new LootCondition[0], new RandomValueRange((float)minMeta, (float)maxMeta))};
   }

   public static LootItem createLootEntry(Item item, double chance, ResourceLocation loottablelist) {
      return new LootItem(new ItemStack(item), chance, 1, 1, loottablelist);
   }

   public static LootItem createLootEntry(Item item, int minSize, int maxSize, double chance, ResourceLocation loottablelist) {
      return new LootItem(new ItemStack(item), chance, minSize, maxSize, loottablelist);
   }

   public static LootItem createLootEntry(Item item, int ordinal, int minStackSize, int maxStackSize, double chance, ResourceLocation loottablelist) {
      return new LootItem(new ItemStack(item, 1, ordinal), chance, minStackSize, maxStackSize, loottablelist);
   }

   public static class InnerPool extends LootPool {
      public final List<LootItem> items = new ArrayList();

      public InnerPool() {
         super(new LootEntry[0], new LootCondition[0], new RandomValueRange(0.0F, 0.0F), new RandomValueRange(0.0F, 0.0F), "reborncore");
      }

      public boolean isEmpty() {
         return this.items.isEmpty();
      }

      public void addItem(LootItem entry) {
         if (entry != null) {
            this.items.add(entry);
         }

      }

      public void generateLoot(Collection<ItemStack> stacks, Random rand, LootContext context) {
         for(LootItem entry : this.items) {
            if (rand.nextDouble() < entry.chance) {
               ItemStack stack = entry.createStack(rand);
               if (stack != null) {
                  stacks.add(stack);
               }
            }
         }

      }
   }
}
