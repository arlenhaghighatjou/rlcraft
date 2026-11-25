package svenhjol.charm.base;

import net.minecraft.util.ResourceLocation;
import svenhjol.meson.helper.LootHelper;

public class CharmLootTables {
   public static ResourceLocation TREASURE_COMMON;
   public static ResourceLocation TREASURE_JUNK;
   public static ResourceLocation VILLAGE_SMITH;
   public static ResourceLocation VILLAGE_LIBRARIAN;
   public static ResourceLocation VILLAGE_PRIEST;
   public static ResourceLocation VILLAGE_CARPENTER;
   public static ResourceLocation VILLAGE_BUTCHER;
   public static ResourceLocation VILLAGE_FARMER;
   public static ResourceLocation VILLAGE_SHEPHERD;
   public static ResourceLocation VILLAGE_FISHERMAN;
   public static ResourceLocation TREASURE_COMMON_BOOKS;
   public static ResourceLocation TREASURE_COMMON_POTIONS;
   public static ResourceLocation TREASURE_UNCOMMON;
   public static ResourceLocation TREASURE_VALUABLE;
   public static ResourceLocation TREASURE_RARE;
   public static ResourceLocation TREASURE_EXPLOSIVE;
   public static ResourceLocation TREASURE_DANGEROUS;

   public CharmLootTables() {
   }

   public static void registerLootTables() {
      TREASURE_COMMON = LootHelper.addLootLocation(LootHelper.RARITY.COMMON, LootHelper.TYPE.MISC, new ResourceLocation("charm", "treasure/common"));
      TREASURE_JUNK = LootHelper.addLootLocation(LootHelper.RARITY.COMMON, LootHelper.TYPE.MISC, new ResourceLocation("charm", "treasure/junk"));
      VILLAGE_SMITH = LootHelper.addLootLocation(LootHelper.RARITY.COMMON, LootHelper.TYPE.MISC, new ResourceLocation("charm", "village/smith"));
      VILLAGE_LIBRARIAN = LootHelper.addLootLocation(LootHelper.RARITY.COMMON, LootHelper.TYPE.MISC, new ResourceLocation("charm", "village/librarian"));
      VILLAGE_PRIEST = LootHelper.addLootLocation(LootHelper.RARITY.COMMON, LootHelper.TYPE.MISC, new ResourceLocation("charm", "village/priest"));
      VILLAGE_CARPENTER = LootHelper.addLootLocation(LootHelper.RARITY.COMMON, LootHelper.TYPE.MISC, new ResourceLocation("charm", "village/carpenter"));
      VILLAGE_BUTCHER = LootHelper.addLootLocation(LootHelper.RARITY.COMMON, LootHelper.TYPE.MISC, new ResourceLocation("charm", "village/butcher"));
      VILLAGE_FARMER = LootHelper.addLootLocation(LootHelper.RARITY.COMMON, LootHelper.TYPE.MISC, new ResourceLocation("charm", "village/farmer"));
      VILLAGE_SHEPHERD = LootHelper.addLootLocation(LootHelper.RARITY.COMMON, LootHelper.TYPE.MISC, new ResourceLocation("charm", "village/shepherd"));
      VILLAGE_FISHERMAN = LootHelper.addLootLocation(LootHelper.RARITY.COMMON, LootHelper.TYPE.MISC, new ResourceLocation("charm", "village/fisherman"));
      TREASURE_COMMON_BOOKS = LootHelper.addLootLocation(LootHelper.RARITY.COMMON, LootHelper.TYPE.BOOK, new ResourceLocation("charm", "treasure/common_books"));
      TREASURE_COMMON_POTIONS = LootHelper.addLootLocation(LootHelper.RARITY.COMMON, LootHelper.TYPE.POTION, new ResourceLocation("charm", "treasure/common_potions"));
      TREASURE_UNCOMMON = LootHelper.addLootLocation(LootHelper.RARITY.UNCOMMON, LootHelper.TYPE.MISC, new ResourceLocation("charm", "treasure/uncommon"));
      TREASURE_VALUABLE = LootHelper.addLootLocation(LootHelper.RARITY.VALUABLE, LootHelper.TYPE.MISC, new ResourceLocation("charm", "treasure/valuable"));
      TREASURE_RARE = LootHelper.addLootLocation(LootHelper.RARITY.RARE, LootHelper.TYPE.MISC, new ResourceLocation("charm", "treasure/rare"));
      TREASURE_EXPLOSIVE = LootHelper.addLootLocation(LootHelper.RARITY.SPECIAL, LootHelper.TYPE.MISC, new ResourceLocation("charm", "treasure/explosive"));
      TREASURE_DANGEROUS = LootHelper.addLootLocation(LootHelper.RARITY.SPECIAL, LootHelper.TYPE.MISC, new ResourceLocation("charm", "treasure/dangerous"));
   }
}
