package ivorius.reccomplex.world.storage.loot;

import com.google.common.collect.Lists;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.files.SimpleLeveledRegistry;
import ivorius.reccomplex.files.loading.LeveledRegistry;
import java.util.Collection;

public class GenericItemCollectionRegistry extends SimpleLeveledRegistry<GenericLootTable.Component> {
   public static final GenericItemCollectionRegistry INSTANCE = new GenericItemCollectionRegistry();

   public GenericItemCollectionRegistry() {
      super("generic item collection component");
   }

   public GenericLootTable.Component register(String id, String domain, GenericLootTable.Component component, boolean active, LeveledRegistry.ILevel level) {
      if (component.tableID == null || component.tableID.length() == 0) {
         component.tableID = id;
      }

      if (active && !RCConfig.shouldLootGenerate(id, domain)) {
         active = false;
      }

      GenericLootTable.Component prev = (GenericLootTable.Component)super.register(id, domain, component, active, level);
      this.invalidateCache(component.tableID);
      return prev;
   }

   public GenericLootTable.Component unregister(String id, LeveledRegistry.ILevel level) {
      GenericLootTable.Component rt = (GenericLootTable.Component)super.unregister(id, level);
      this.invalidateCache(rt.tableID);
      return rt;
   }

   public void clear(LeveledRegistry.ILevel level) {
      Collection<GenericLootTable.Component> removed = Lists.newArrayList(this.map(level).values());
      super.clear(level);

      for(GenericLootTable.Component component : removed) {
         this.invalidateCache(component.tableID);
      }

   }

   private void invalidateCache(String generatorID) {
      WeightedItemCollectionRegistry.INSTANCE.unregister(generatorID, LeveledRegistry.Level.CUSTOM);

      for(String key : this.activeIDs()) {
         GenericLootTable.Component component = (GenericLootTable.Component)this.get(key);
         if (component.tableID.equals(generatorID)) {
            GenericLootTable collection = this.registerGetGenericItemCollection(component.tableID, this.status(key).getDomain());
            collection.components.add(component);
         }
      }

   }

   private GenericLootTable registerGetGenericItemCollection(String key, String domain) {
      LootTable collection = (LootTable)WeightedItemCollectionRegistry.INSTANCE.get(key);
      if (collection == null || !(collection instanceof GenericLootTable)) {
         collection = new GenericLootTable();
         WeightedItemCollectionRegistry.INSTANCE.register(key, domain, collection, true, LeveledRegistry.Level.CUSTOM);
      }

      return (GenericLootTable)collection;
   }

   private static class ComponentData {
      public boolean disabled;
      public String domain;

      public ComponentData(boolean disabled, String domain) {
         this.disabled = disabled;
         this.domain = domain;
      }
   }
}
