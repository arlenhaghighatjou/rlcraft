package ivorius.reccomplex.world.storage.loot;

import ivorius.reccomplex.files.SimpleLeveledRegistry;

public class WeightedItemCollectionRegistry extends SimpleLeveledRegistry<LootTable> {
   public static WeightedItemCollectionRegistry INSTANCE = new WeightedItemCollectionRegistry();

   public WeightedItemCollectionRegistry() {
      super("weighted item collection");
   }
}
