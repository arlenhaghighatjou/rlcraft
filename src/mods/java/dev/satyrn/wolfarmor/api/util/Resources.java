package dev.satyrn.wolfarmor.api.util;

import net.minecraft.util.ResourceLocation;

public abstract class Resources {
   public static final String MOD_NAME = "Wolf Armor and Storage";
   public static final String MOD_ID = "wolfarmor";
   public static final ResourceLocation TEXTURE_WOLF_BACKPACK = new ResourceLocation("wolfarmor", "textures/models/wolf_pack.png");
   public static final ResourceLocation LOOT_TABLE_DUNGEON_CHEST_INJECT = new ResourceLocation("wolfarmor", "chests/inject/dungeon");
   public static final ResourceLocation ADVANCEMENT_ARMOR_UP = new ResourceLocation("wolfarmor", "husbandry/armor_up");
   public static final ResourceLocation ADVANCEMENT_WOLF_PACK = new ResourceLocation("wolfarmor", "husbandry/wolf_pack");
   public static final ResourceLocation CAPABILITY_WOLF_ARMOR = new ResourceLocation("wolfarmor", "wolf_armor");
   public static final ResourceLocation CRITERIA_TRIGGER_EQUIP_WOLF_ARMOR = new ResourceLocation("wolfarmor", "equip_wolf_armor");
   public static final ResourceLocation CRITERIA_TRIGGER_EQUIP_WOLF_CHEST = new ResourceLocation("wolfarmor", "equip_wolf_backpack");
   public static final ResourceLocation RECIPE_LEATHER_ARMOR_DYES = new ResourceLocation("wolfarmor", "leather_wolf_armor_dyed");
   public static final ResourceLocation ITEM_DIAMOND_WOLF_ARMOR = new ResourceLocation("wolfarmor", "diamond_wolf_armor");
   public static final ResourceLocation ITEM_GOLD_WOLF_ARMOR = new ResourceLocation("wolfarmor", "gold_wolf_armor");
   public static final ResourceLocation ITEM_IRON_WOLF_ARMOR = new ResourceLocation("wolfarmor", "iron_wolf_armor");
   public static final ResourceLocation ITEM_CHAINMAIL_WOLF_ARMOR = new ResourceLocation("wolfarmor", "chainmail_wolf_armor");
   public static final ResourceLocation ITEM_LEATHER_WOLF_ARMOR = new ResourceLocation("wolfarmor", "leather_wolf_armor");
   /** @deprecated */
   @Deprecated
   public static final ResourceLocation ENTITY_WOLF_ARMORED_LOCATION = new ResourceLocation("wolfarmor", "wolf_armored");

   public Resources() {
   }
}
