package leviathan143.loottweaker.common.zenscript.wrapper;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import leviathan143.loottweaker.common.lib.LootTableFinder;
import leviathan143.loottweaker.common.mutable_loot.MutableLootPool;
import leviathan143.loottweaker.common.mutable_loot.MutableLootTable;
import leviathan143.loottweaker.common.zenscript.LootTweakerContext;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("loottweaker.vanilla.loot.LootTable")
public class ZenLootTableWrapper {
   private final ResourceLocation id;
   private final List<ZenLootTableWrapper.LootTableTweaker> tweaks = new ArrayList();
   private final Map<String, ZenLootPoolWrapper> tweakedPools = new HashMap();
   private final LootTweakerContext context;

   public ZenLootTableWrapper(LootTweakerContext context, ResourceLocation id) {
      this.context = context;
      this.id = id;
   }

   @ZenMethod
   public ZenLootPoolWrapper getPool(String poolName) {
      if (this.tweakedPools.containsKey(poolName)) {
         return (ZenLootPoolWrapper)this.tweakedPools.get(poolName);
      } else {
         ZenLootPoolWrapper pool = this.context.wrapPool(this.id, poolName);
         this.tweakedPools.put(poolName, pool);
         this.enqueueTweaker((table) -> {
            MutableLootPool targetPool = table.getPool(poolName);
            if (targetPool != null) {
               pool.tweak(targetPool);
            } else {
               this.context.getErrorHandler().error("No loot pool with name %s exists in table %s!", poolName, this.id);
            }

         }, "Retrieved pool %s from table %s", poolName, this.id);
         return pool;
      }
   }

   @ZenMethod
   public ZenLootPoolWrapper addPool(String poolName, float minRolls, float maxRolls, float minBonusRolls, float maxBonusRolls) {
      ZenLootPoolWrapper pool = this.context.wrapPool(this.id, poolName);
      this.enqueueTweaker((table) -> {
         MutableLootPool existing = table.getPool(poolName);
         if (existing != null) {
            this.context.getErrorHandler().error("Cannot add pool '%s' to table '%s'. Pool names must be unique within their table.", poolName, this.id);
         } else {
            MutableLootPool newPool = new MutableLootPool(poolName, new HashMap(), new ArrayList(), new RandomValueRange(minRolls, maxRolls), new RandomValueRange(minBonusRolls, maxBonusRolls));
            pool.tweak(newPool);
            table.addPool(newPool);
            CraftTweakerAPI.logInfo(String.format("Added new pool %s to table %s", poolName, this.id));
         }
      }, "Queued pool %s for addition to table %s", poolName, this.id);
      if (this.tweakedPools.putIfAbsent(poolName, pool) != null) {
         this.context.getErrorHandler().error("Cannot add pool '%s' to table '%s'. Pool names must be unique within their table.", poolName, this.id);
         return pool;
      } else {
         return pool;
      }
   }

   @ZenMethod
   public void removePool(String poolName) {
      this.enqueueTweaker((table) -> {
         if (table.getPool(poolName) == null) {
            this.context.getErrorHandler().error("No loot pool with name %s exists in table %s!", poolName, this.id);
         } else {
            table.removePool(poolName);
         }
      }, "Queued pool %s of table %s for removal", poolName, this.id);
   }

   @ZenMethod
   public void clear() {
      this.enqueueTweaker(MutableLootTable::clearPools, "Queued all pools of table %s for removal", this.id);
   }

   private void enqueueTweaker(ZenLootTableWrapper.LootTableTweaker tweaker, String format, Object... args) {
      this.tweaks.add(tweaker);
      CraftTweakerAPI.logInfo(String.format(format, args));
   }

   public void applyTweakers(MutableLootTable mutableTable) {
      for(ZenLootTableWrapper.LootTableTweaker tweaker : this.tweaks) {
         tweaker.tweak(mutableTable);
      }

   }

   public ResourceLocation getId() {
      return this.id;
   }

   public boolean isValid() {
      return LootTableFinder.DEFAULT.exists(this.id) || LootTableList.getAll().contains(this.id);
   }

   @FunctionalInterface
   public interface LootTableTweaker {
      void tweak(MutableLootTable var1);
   }
}
