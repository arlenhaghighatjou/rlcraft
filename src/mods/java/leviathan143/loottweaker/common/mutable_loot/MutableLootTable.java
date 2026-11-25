package leviathan143.loottweaker.common.mutable_loot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import leviathan143.loottweaker.common.darkmagic.LootTableAccessors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MutableLootTable {
   private static final Logger SANITY_LOGGER = LogManager.getLogger("loottweaker.sanity_checks");
   private final ResourceLocation id;
   private Map<String, MutableLootPool> pools;

   public MutableLootTable(LootTable table, ResourceLocation id) {
      this.id = id;
      List<LootPool> immutablePools = LootTableAccessors.getPools(table);
      this.pools = new HashMap(immutablePools.size());
      int uniqueSuffix = 0;

      for(LootPool pool : immutablePools) {
         MutableLootPool mutablePool = new MutableLootPool(pool);
         MutableLootPool existing = (MutableLootPool)this.pools.get(pool.getName());
         if (existing != null) {
            String newName = mutablePool.getName() + uniqueSuffix++;
            SANITY_LOGGER.error("Unexpected duplicate pool name '{}' in table '{}'. Duplicate added as '{}'.\nReport this to the loot adder.", mutablePool.getName(), this.getId(), newName);
            mutablePool.setName(newName);
            this.pools.put(newName, mutablePool);
         } else {
            this.pools.put(mutablePool.getName(), mutablePool);
         }
      }

   }

   public MutableLootTable(ResourceLocation id, Map<String, MutableLootPool> pools) {
      this.id = id;
      this.pools = pools;
   }

   public MutableLootTable deepClone() {
      BinaryOperator<MutableLootPool> mergeFunction = (a, b) -> {
         throw new IllegalStateException(String.format("Unexpected duplicate pool '%s' while deep cloning mutable table '%s'. Report this to the mod author", a.getName(), this.id));
      };
      Map<String, MutableLootPool> poolsDeepClone = (Map)this.pools.entrySet().stream().collect(Collectors.toMap(Entry::getKey, (e) -> {
         return ((MutableLootPool)e.getValue()).deepClone();
      }, mergeFunction, HashMap::new));
      return new MutableLootTable(this.id, poolsDeepClone);
   }

   public LootTable toImmutable() {
      LootPool[] poolsArray = (LootPool[])this.pools.values().stream().map(MutableLootPool::toImmutable).toArray((x$0) -> {
         return new LootPool[x$0];
      });
      return new LootTable(poolsArray);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public Map<String, MutableLootPool> getPools() {
      return this.pools;
   }

   @Nullable
   public MutableLootPool getPool(String name) {
      return (MutableLootPool)this.pools.get(name);
   }

   public void addPool(MutableLootPool pool) {
      if (this.pools.putIfAbsent(pool.getName(), pool) != null) {
         throw new IllegalArgumentException(String.format("Duplicate pool name '%s' in table '%s'", pool.getName(), this.id));
      }
   }

   public MutableLootPool removePool(String name) {
      return (MutableLootPool)this.pools.remove(name);
   }

   public void clearPools() {
      this.pools.clear();
   }
}
