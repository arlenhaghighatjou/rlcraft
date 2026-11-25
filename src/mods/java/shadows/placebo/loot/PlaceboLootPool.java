package shadows.placebo.loot;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class PlaceboLootPool extends LootPool {
   private static int k = 0;

   public PlaceboLootPool(LootEntry[] entries, LootCondition[] conditions, RandomValueRange rolls, RandomValueRange bonusRolls) {
      super(entries, conditions, rolls, bonusRolls, "placebo_loot_pool_" + k++);
   }

   public static class PoolBuilder {
      RandomValueRange rolls;
      RandomValueRange bonusRolls;
      List<LootEntry> entries = new ArrayList();
      List<LootCondition> conditions = new ArrayList();

      public PoolBuilder(float rollsMin, float rollsMax, float bonusRollsMin, float bonusRollsMax) {
         this.rolls = new RandomValueRange(rollsMin, rollsMax);
         this.bonusRolls = new RandomValueRange(bonusRollsMin, bonusRollsMax);
      }

      public PlaceboLootPool.PoolBuilder addEntries(LootEntry... entries) {
         for(LootEntry e : entries) {
            this.entries.add(e);
         }

         return this;
      }

      public PlaceboLootPool.PoolBuilder addCondition(LootCondition... conditions) {
         for(LootCondition c : conditions) {
            this.conditions.add(c);
         }

         return this;
      }

      public PlaceboLootPool build() {
         return new PlaceboLootPool((LootEntry[])this.entries.toArray(new LootEntry[0]), (LootCondition[])this.conditions.toArray(new LootCondition[0]), this.rolls, this.bonusRolls);
      }
   }
}
