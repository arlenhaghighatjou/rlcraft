package familiarfauna.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;

public class ModLootTable {
   public static ResourceLocation BUTTERFLY_LOOT;
   public static ResourceLocation DRAGONFLY_LOOT;
   public static ResourceLocation DEER_LOOT;
   public static ResourceLocation PIXIE_LOOT;
   public static ResourceLocation SNAIL_LOOT;
   public static ResourceLocation TURKEY_LOOT;

   public ModLootTable() {
   }

   public static void init() {
      BUTTERFLY_LOOT = LootTableList.register(new ResourceLocation("familiarfauna", "entities/butterfly"));
      DRAGONFLY_LOOT = LootTableList.register(new ResourceLocation("familiarfauna", "entities/dragonfly"));
      DEER_LOOT = LootTableList.register(new ResourceLocation("familiarfauna", "entities/deer"));
      PIXIE_LOOT = LootTableList.register(new ResourceLocation("familiarfauna", "entities/pixie"));
      SNAIL_LOOT = LootTableList.register(new ResourceLocation("familiarfauna", "entities/snail"));
      TURKEY_LOOT = LootTableList.register(new ResourceLocation("familiarfauna", "entities/turkey"));
   }
}
