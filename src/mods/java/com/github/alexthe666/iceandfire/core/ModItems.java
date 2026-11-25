package com.github.alexthe666.iceandfire.core;

import com.github.alexthe666.iceandfire.enums.EnumDragonArmor;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import com.github.alexthe666.iceandfire.enums.EnumSeaSerpent;
import com.github.alexthe666.iceandfire.item.ItemAlchemySword;
import com.github.alexthe666.iceandfire.item.ItemAmbrosia;
import com.github.alexthe666.iceandfire.item.ItemAmphithereArrow;
import com.github.alexthe666.iceandfire.item.ItemBestiary;
import com.github.alexthe666.iceandfire.item.ItemBlindfold;
import com.github.alexthe666.iceandfire.item.ItemDeathWormChitin;
import com.github.alexthe666.iceandfire.item.ItemDeathwormArmor;
import com.github.alexthe666.iceandfire.item.ItemDeathwormEgg;
import com.github.alexthe666.iceandfire.item.ItemDragonArmor;
import com.github.alexthe666.iceandfire.item.ItemDragonBone;
import com.github.alexthe666.iceandfire.item.ItemDragonBow;
import com.github.alexthe666.iceandfire.item.ItemDragonEgg;
import com.github.alexthe666.iceandfire.item.ItemDragonFlesh;
import com.github.alexthe666.iceandfire.item.ItemDragonFlute;
import com.github.alexthe666.iceandfire.item.ItemDragonHornActive;
import com.github.alexthe666.iceandfire.item.ItemDragonHornStatic;
import com.github.alexthe666.iceandfire.item.ItemDragonScales;
import com.github.alexthe666.iceandfire.item.ItemDragonSkull;
import com.github.alexthe666.iceandfire.item.ItemDragonStaff;
import com.github.alexthe666.iceandfire.item.ItemFishingSpear;
import com.github.alexthe666.iceandfire.item.ItemGeneric;
import com.github.alexthe666.iceandfire.item.ItemGenericDesc;
import com.github.alexthe666.iceandfire.item.ItemGorgonHead;
import com.github.alexthe666.iceandfire.item.ItemHippogryphEgg;
import com.github.alexthe666.iceandfire.item.ItemModArmor;
import com.github.alexthe666.iceandfire.item.ItemModAxe;
import com.github.alexthe666.iceandfire.item.ItemModHoe;
import com.github.alexthe666.iceandfire.item.ItemModPickaxe;
import com.github.alexthe666.iceandfire.item.ItemModShovel;
import com.github.alexthe666.iceandfire.item.ItemModSword;
import com.github.alexthe666.iceandfire.item.ItemMyrmexEgg;
import com.github.alexthe666.iceandfire.item.ItemMyrmexStaff;
import com.github.alexthe666.iceandfire.item.ItemPixieDust;
import com.github.alexthe666.iceandfire.item.ItemRottenEgg;
import com.github.alexthe666.iceandfire.item.ItemSeaSerpentArrow;
import com.github.alexthe666.iceandfire.item.ItemStoneStatue;
import com.github.alexthe666.iceandfire.item.ItemStymphalianArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

public class ModItems {
   public static ArmorMaterial silverMetal = EnumHelper.addArmorMaterial("Silver", "iceandfire:armor_silver_metal", 15, new int[]{1, 4, 5, 2}, 20, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.0F);
   public static ArmorMaterial blindfoldArmor = EnumHelper.addArmorMaterial("Blindfold", "iceandfire:blindfold", 5, new int[]{1, 1, 1, 1}, 10, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F);
   public static ArmorMaterial sheep = EnumHelper.addArmorMaterial("Sheep", "iceandfire:sheep_disguise", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F);
   public static ArmorMaterial myrmexDesert = EnumHelper.addArmorMaterial("MyrmexDesert", "iceandfire:myrmex_desert", 20, new int[]{3, 5, 8, 4}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F);
   public static ArmorMaterial myrmexJungle = EnumHelper.addArmorMaterial("MyrmexJungle", "iceandfire:myrmex_jungle", 20, new int[]{3, 5, 8, 4}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F);
   public static ArmorMaterial earplugsArmor = EnumHelper.addArmorMaterial("Earplugs", "iceandfire:earplugs", 5, new int[]{1, 1, 1, 1}, 10, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F);
   public static ArmorMaterial yellow_deathworm = EnumHelper.addArmorMaterial("Yellow Deathworm", "iceandfire:yellow_deathworm", 15, new int[]{2, 5, 7, 3}, 5, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.5F);
   public static ArmorMaterial white_deathworm = EnumHelper.addArmorMaterial("White Deathworm", "iceandfire:white_deathworm", 15, new int[]{2, 5, 7, 3}, 5, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.5F);
   public static ArmorMaterial red_deathworm = EnumHelper.addArmorMaterial("Red Deathworm", "iceandfire:red_deathworm", 15, new int[]{2, 5, 7, 3}, 5, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.5F);
   public static ArmorMaterial troll_mountain = EnumHelper.addArmorMaterial("Mountain Troll", "iceandfire:troll_mountain", 20, new int[]{4, 5, 7, 3}, 10, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F);
   public static ArmorMaterial troll_forest = EnumHelper.addArmorMaterial("Forest Troll", "iceandfire:troll_forest", 20, new int[]{2, 5, 7, 3}, 10, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F);
   public static ArmorMaterial troll_frost = EnumHelper.addArmorMaterial("Frost Troll", "iceandfire:troll_frost", 20, new int[]{2, 5, 7, 3}, 10, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F);
   public static ToolMaterial silverTools = EnumHelper.addToolMaterial("Silver", 2, 460, 11.0F, 1.0F, 18);
   public static ToolMaterial boneTools = EnumHelper.addToolMaterial("Dragonbone", 4, 1660, 10.0F, 4.0F, 22);
   public static ToolMaterial fireBoneTools = EnumHelper.addToolMaterial("FireDragonbone", 4, 2000, 10.0F, 5.5F, 22);
   public static ToolMaterial iceBoneTools = EnumHelper.addToolMaterial("IceDragonbone", 4, 2000, 10.0F, 5.5F, 22);
   public static ToolMaterial trollWeapon = EnumHelper.addToolMaterial("trollWeapon", 2, 300, 11.0F, 1.0F, 1);
   public static ToolMaterial myrmexChitin = EnumHelper.addToolMaterial("MyrmexChitin", 3, 600, 6.0F, -1.0F, 8);
   @ObjectHolder("iceandfire:bestiary")
   public static Item bestiary = new ItemBestiary();
   @ObjectHolder("iceandfire:manuscript")
   public static Item manuscript = new ItemGeneric("manuscript", "iceandfire.manuscript");
   @ObjectHolder("iceandfire:sapphire_gem")
   public static Item sapphireGem = new ItemGeneric("sapphire_gem", "iceandfire.sapphireGem");
   @ObjectHolder("iceandfire:silver_ingot")
   public static Item silverIngot = new ItemGeneric("silver_ingot", "iceandfire.silverIngot");
   @ObjectHolder("iceandfire:silver_nugget")
   public static Item silverNugget = new ItemGeneric("silver_nugget", "iceandfire.silverNugget");
   @ObjectHolder("iceandfire:silver_helmet")
   public static Item silver_helmet = new ItemModArmor(silverMetal, 0, EntityEquipmentSlot.HEAD, "armor_silver_metal_helmet", "iceandfire.silver_helmet");
   @ObjectHolder("iceandfire:silver_chestplate")
   public static Item silver_chestplate = new ItemModArmor(silverMetal, 1, EntityEquipmentSlot.CHEST, "armor_silver_metal_chestplate", "iceandfire.silver_chestplate");
   @ObjectHolder("iceandfire:silver_leggings")
   public static Item silver_leggings = new ItemModArmor(silverMetal, 2, EntityEquipmentSlot.LEGS, "armor_silver_metal_leggings", "iceandfire.silver_leggings");
   @ObjectHolder("iceandfire:silver_boots")
   public static Item silver_boots = new ItemModArmor(silverMetal, 3, EntityEquipmentSlot.FEET, "armor_silver_metal_boots", "iceandfire.silver_boots");
   @ObjectHolder("iceandfire:silver_sword")
   public static Item silver_sword = new ItemModSword(silverTools, "silver_sword", "iceandfire.silver_sword");
   @ObjectHolder("iceandfire:silver_shovel")
   public static Item silver_shovel = new ItemModShovel(silverTools, "silver_shovel", "iceandfire.silver_shovel");
   @ObjectHolder("iceandfire:silver_pickaxe")
   public static Item silver_pickaxe = new ItemModPickaxe(silverTools, "silver_pickaxe", "iceandfire.silver_pickaxe");
   @ObjectHolder("iceandfire:silver_axe")
   public static Item silver_axe = new ItemModAxe(silverTools, "silver_axe", "iceandfire.silver_axe");
   @ObjectHolder("iceandfire:silver_hoe")
   public static Item silver_hoe = new ItemModHoe(silverTools, "silver_hoe", "iceandfire.silver_hoe");
   @ObjectHolder("iceandfire:fire_stew")
   public static Item fire_stew = new ItemGeneric("fire_stew", "iceandfire.fire_stew");
   @ObjectHolder("iceandfire:frost_stew")
   public static Item frost_stew = new ItemGeneric("frost_stew", "iceandfire.frost_stew");
   @ObjectHolder("iceandfire:dragonegg_red")
   public static Item dragonegg_red = new ItemDragonEgg("dragonegg_red", EnumDragonEgg.RED);
   @ObjectHolder("iceandfire:dragonegg_green")
   public static Item dragonegg_green = new ItemDragonEgg("dragonegg_green", EnumDragonEgg.GREEN);
   @ObjectHolder("iceandfire:dragonegg_bronze")
   public static Item dragonegg_bronze = new ItemDragonEgg("dragonegg_bronze", EnumDragonEgg.BRONZE);
   @ObjectHolder("iceandfire:dragonegg_gray")
   public static Item dragonegg_gray = new ItemDragonEgg("dragonegg_gray", EnumDragonEgg.GRAY);
   @ObjectHolder("iceandfire:dragonegg_blue")
   public static Item dragonegg_blue = new ItemDragonEgg("dragonegg_blue", EnumDragonEgg.BLUE);
   @ObjectHolder("iceandfire:dragonegg_white")
   public static Item dragonegg_white = new ItemDragonEgg("dragonegg_white", EnumDragonEgg.WHITE);
   @ObjectHolder("iceandfire:dragonegg_sapphire")
   public static Item dragonegg_sapphire = new ItemDragonEgg("dragonegg_sapphire", EnumDragonEgg.SAPPHIRE);
   @ObjectHolder("iceandfire:dragonegg_silver")
   public static Item dragonegg_silver = new ItemDragonEgg("dragonegg_silver", EnumDragonEgg.SILVER);
   @ObjectHolder("iceandfire:dragonscales_red")
   public static Item dragonscales_red = new ItemDragonScales("dragonscales_red", EnumDragonEgg.RED);
   @ObjectHolder("iceandfire:dragonscales_green")
   public static Item dragonscales_green = new ItemDragonScales("dragonscales_green", EnumDragonEgg.GREEN);
   @ObjectHolder("iceandfire:dragonscales_bronze")
   public static Item dragonscales_bronze = new ItemDragonScales("dragonscales_bronze", EnumDragonEgg.BRONZE);
   @ObjectHolder("iceandfire:dragonscales_gray")
   public static Item dragonscales_gray = new ItemDragonScales("dragonscales_gray", EnumDragonEgg.GRAY);
   @ObjectHolder("iceandfire:dragonscales_blue")
   public static Item dragonscales_blue = new ItemDragonScales("dragonscales_blue", EnumDragonEgg.BLUE);
   @ObjectHolder("iceandfire:dragonscales_white")
   public static Item dragonscales_white = new ItemDragonScales("dragonscales_white", EnumDragonEgg.WHITE);
   @ObjectHolder("iceandfire:dragonscales_sapphire")
   public static Item dragonscales_sapphire = new ItemDragonScales("dragonscales_sapphire", EnumDragonEgg.SAPPHIRE);
   @ObjectHolder("iceandfire:dragonscales_silver")
   public static Item dragonscales_silver = new ItemDragonScales("dragonscales_silver", EnumDragonEgg.SILVER);
   @ObjectHolder("iceandfire:dragonbone")
   public static Item dragonbone = new ItemDragonBone();
   @ObjectHolder("iceandfire:witherbone")
   public static Item witherbone = new ItemGeneric("witherbone", "iceandfire.witherbone");
   @ObjectHolder("iceandfire:fishing_spear")
   public static Item fishing_spear = new ItemFishingSpear();
   @ObjectHolder("iceandfire:wither_shard")
   public static Item wither_shard = new ItemGeneric("wither_shard", "iceandfire.wither_shard");
   @ObjectHolder("iceandfire:dragonbone_sword")
   public static Item dragonbone_sword = new ItemModSword(boneTools, "dragonbone_sword", "iceandfire.dragonbone_sword");
   @ObjectHolder("iceandfire:dragonbone_shovel")
   public static Item dragonbone_shovel = new ItemModShovel(boneTools, "dragonbone_shovel", "iceandfire.dragonbone_shovel");
   @ObjectHolder("iceandfire:dragonbone_pickaxe")
   public static Item dragonbone_pickaxe = new ItemModPickaxe(boneTools, "dragonbone_pickaxe", "iceandfire.dragonbone_pickaxe");
   @ObjectHolder("iceandfire:dragonbone_axe")
   public static Item dragonbone_axe = new ItemModAxe(boneTools, "dragonbone_axe", "iceandfire.dragonbone_axe");
   @ObjectHolder("iceandfire:dragonbone_hoe")
   public static Item dragonbone_hoe = new ItemModHoe(boneTools, "dragonbone_hoe", "iceandfire.dragonbone_hoe");
   @ObjectHolder("iceandfire:dragonbone_sword_fire")
   public static Item dragonbone_sword_fire = new ItemAlchemySword(fireBoneTools, "dragonbone_sword_fire", "iceandfire.dragonbone_sword_fire");
   @ObjectHolder("iceandfire:dragonbone_sword_ice")
   public static Item dragonbone_sword_ice = new ItemAlchemySword(iceBoneTools, "dragonbone_sword_ice", "iceandfire.dragonbone_sword_ice");
   @ObjectHolder("iceandfire:dragonbone_arrow")
   public static Item dragonbone_arrow = new ItemGeneric("dragonbone_arrow", "iceandfire.dragonbone_arrow");
   @ObjectHolder("iceandfire:dragonbone_bow")
   public static Item dragonbone_bow = new ItemDragonBow();
   @ObjectHolder("iceandfire:dragon_skull")
   public static Item dragon_skull = new ItemDragonSkull();
   @ObjectHolder("iceandfire:dragonarmor_iron")
   public static Item dragon_armor_iron = new ItemDragonArmor(0, "dragonarmor_iron");
   @ObjectHolder("iceandfire:dragonarmor_gold")
   public static Item dragon_armor_gold = new ItemDragonArmor(1, "dragonarmor_gold");
   @ObjectHolder("iceandfire:dragonarmor_diamond")
   public static Item dragon_armor_diamond = new ItemDragonArmor(2, "dragonarmor_diamond");
   @ObjectHolder("iceandfire:dragon_meal")
   public static Item dragon_meal = new ItemGeneric("dragon_meal", "iceandfire.dragon_meal");
   @ObjectHolder("iceandfire:sickly_dragon_meal")
   public static Item sickly_dragon_meal = new ItemGenericDesc("sickly_dragon_meal", "iceandfire.sickly_dragon_meal");
   @ObjectHolder("iceandfire:fire_dragon_flesh")
   public static Item fire_dragon_flesh = new ItemDragonFlesh(true);
   @ObjectHolder("iceandfire:ice_dragon_flesh")
   public static Item ice_dragon_flesh = new ItemDragonFlesh(false);
   @ObjectHolder("iceandfire:fire_dragon_heart")
   public static Item fire_dragon_heart = new ItemGeneric("fire_dragon_heart", "iceandfire.fire_dragon_heart");
   @ObjectHolder("iceandfire:ice_dragon_heart")
   public static Item ice_dragon_heart = new ItemGeneric("ice_dragon_heart", "iceandfire.ice_dragon_heart");
   @ObjectHolder("iceandfire:fire_dragon_blood")
   public static Item fire_dragon_blood = new ItemGeneric("fire_dragon_blood", "iceandfire.fire_dragon_blood");
   @ObjectHolder("iceandfire:ice_dragon_blood")
   public static Item ice_dragon_blood = new ItemGeneric("ice_dragon_blood", "iceandfire.ice_dragon_blood");
   @ObjectHolder("iceandfire:dragon_stick")
   public static Item dragon_stick = new ItemDragonStaff();
   @ObjectHolder("iceandfire:dragon_horn")
   public static Item dragon_horn = new ItemDragonHornStatic();
   @ObjectHolder("iceandfire:dragon_horn_fire")
   public static Item dragon_horn_fire = new ItemDragonHornActive("dragon_horn_fire");
   @ObjectHolder("iceandfire:dragon_horn_ice")
   public static Item dragon_horn_ice = new ItemDragonHornActive("dragon_horn_ice");
   @ObjectHolder("iceandfire:dragon_flute")
   public static Item dragon_flute = new ItemDragonFlute();
   @ObjectHolder("iceandfire:hippogryph_egg")
   public static Item hippogryph_egg = new ItemHippogryphEgg();
   @ObjectHolder("iceandfire:iron_hippogryph_armor")
   public static Item iron_hippogryph_armor = (new ItemGeneric("iron_hippogryph_armor", "iceandfire.iron_hippogryph_armor")).setMaxStackSize(1);
   @ObjectHolder("iceandfire:gold_hippogryph_armor")
   public static Item gold_hippogryph_armor = (new ItemGeneric("gold_hippogryph_armor", "iceandfire.gold_hippogryph_armor")).setMaxStackSize(1);
   @ObjectHolder("iceandfire:diamond_hippogryph_armor")
   public static Item diamond_hippogryph_armor = (new ItemGeneric("diamond_hippogryph_armor", "iceandfire.diamond_hippogryph_armor")).setMaxStackSize(1);
   @ObjectHolder("iceandfire:gorgon_head")
   public static Item gorgon_head = new ItemGorgonHead();
   @ObjectHolder("iceandfire:stone_statue")
   public static Item stone_statue = new ItemStoneStatue();
   @ObjectHolder("iceandfire:blindfold")
   public static Item blindfold = new ItemBlindfold();
   @ObjectHolder("iceandfire:pixie_dust")
   public static Item pixie_dust = new ItemPixieDust();
   @ObjectHolder("iceandfire:ambrosia")
   public static Item ambrosia = new ItemAmbrosia();
   @ObjectHolder("iceandfire:sheep_helmet")
   public static Item sheep_helmet = new ItemModArmor(sheep, 0, EntityEquipmentSlot.HEAD, "sheep_helmet", "iceandfire.sheep_helmet");
   @ObjectHolder("iceandfire:sheep_chestplate")
   public static Item sheep_chestplate = new ItemModArmor(sheep, 1, EntityEquipmentSlot.CHEST, "sheep_chestplate", "iceandfire.sheep_chestplate");
   @ObjectHolder("iceandfire:sheep_leggings")
   public static Item sheep_leggings = new ItemModArmor(sheep, 2, EntityEquipmentSlot.LEGS, "sheep_leggings", "iceandfire.sheep_leggings");
   @ObjectHolder("iceandfire:sheep_boots")
   public static Item sheep_boots = new ItemModArmor(sheep, 3, EntityEquipmentSlot.FEET, "sheep_boots", "iceandfire.sheep_boots");
   @ObjectHolder("iceandfire:shiny_scales")
   public static Item shiny_scales = new ItemGeneric("shiny_scales", "iceandfire.shiny_scales");
   @ObjectHolder("iceandfire:earplugs")
   public static Item earplugs = new ItemModArmor(earplugsArmor, 0, EntityEquipmentSlot.HEAD, "earplugs", "iceandfire.earplugs");
   @ObjectHolder("iceandfire:deathworm_chitin")
   public static Item deathworm_chitin = new ItemDeathWormChitin();
   @ObjectHolder("iceandfire:deathworm_yellow_helmet")
   public static Item deathworm_yellow_helmet = new ItemDeathwormArmor(yellow_deathworm, 0, EntityEquipmentSlot.HEAD, "deathworm_yellow_helmet", "iceandfire.deathworm_yellow_helmet");
   @ObjectHolder("iceandfire:deathworm_yellow_chestplate")
   public static Item deathworm_yellow_chestplate = new ItemDeathwormArmor(yellow_deathworm, 1, EntityEquipmentSlot.CHEST, "deathworm_yellow_chestplate", "iceandfire.deathworm_yellow_chestplate");
   @ObjectHolder("iceandfire:deathworm_yellow_leggings")
   public static Item deathworm_yellow_leggings = new ItemDeathwormArmor(yellow_deathworm, 2, EntityEquipmentSlot.LEGS, "deathworm_yellow_leggings", "iceandfire.deathworm_yellow_leggings");
   @ObjectHolder("iceandfire:deathworm_yellow_boots")
   public static Item deathworm_yellow_boots = new ItemDeathwormArmor(yellow_deathworm, 3, EntityEquipmentSlot.FEET, "deathworm_yellow_boots", "iceandfire.deathworm_yellow_boots");
   @ObjectHolder("iceandfire:deathworm_white_helmet")
   public static Item deathworm_white_helmet = new ItemDeathwormArmor(white_deathworm, 0, EntityEquipmentSlot.HEAD, "deathworm_white_helmet", "iceandfire.deathworm_white_helmet");
   @ObjectHolder("iceandfire:deathworm_white_chestplate")
   public static Item deathworm_white_chestplate = new ItemDeathwormArmor(white_deathworm, 1, EntityEquipmentSlot.CHEST, "deathworm_white_chestplate", "iceandfire.deathworm_white_chestplate");
   @ObjectHolder("iceandfire:deathworm_white_leggings")
   public static Item deathworm_white_leggings = new ItemDeathwormArmor(white_deathworm, 2, EntityEquipmentSlot.LEGS, "deathworm_white_leggings", "iceandfire.deathworm_white_leggings");
   @ObjectHolder("iceandfire:deathworm_white_boots")
   public static Item deathworm_white_boots = new ItemDeathwormArmor(white_deathworm, 3, EntityEquipmentSlot.FEET, "deathworm_white_boots", "iceandfire.deathworm_white_boots");
   @ObjectHolder("iceandfire:deathworm_red_helmet")
   public static Item deathworm_red_helmet = new ItemDeathwormArmor(red_deathworm, 0, EntityEquipmentSlot.HEAD, "deathworm_red_helmet", "iceandfire.deathworm_red_helmet");
   @ObjectHolder("iceandfire:deathworm_red_chworm_whhestplate")
   public static Item deathworm_red_chestplate = new ItemDeathwormArmor(red_deathworm, 1, EntityEquipmentSlot.CHEST, "deathworm_red_chestplate", "iceandfire.deathworm_red_chestplate");
   @ObjectHolder("iceandfire:deathworm_red_leggings")
   public static Item deathworm_red_leggings = new ItemDeathwormArmor(red_deathworm, 2, EntityEquipmentSlot.LEGS, "deathworm_red_leggings", "iceandfire.deathworm_red_leggings");
   @ObjectHolder("iceandfire:deathworm_red_boots")
   public static Item deathworm_red_boots = new ItemDeathwormArmor(red_deathworm, 3, EntityEquipmentSlot.FEET, "deathworm_red_boots", "iceandfire.deathworm_red_boots");
   @ObjectHolder("iceandfire:deathworm_egg")
   public static Item deathworm_egg = new ItemDeathwormEgg();
   @ObjectHolder("iceandfire:rotten_egg")
   public static Item rotten_egg = new ItemRottenEgg();
   @ObjectHolder("iceandfire:stymphalian_bird_feather")
   public static Item stymphalian_bird_feather = new ItemGeneric("stymphalian_bird_feather", "iceandfire.stymphalian_bird_feather");
   @ObjectHolder("iceandfire:stymphalian_arrow")
   public static Item stymphalian_arrow = new ItemStymphalianArrow();
   @ObjectHolder("iceandfire:troll_tusk")
   public static Item troll_tusk = new ItemGeneric("troll_tusk", "iceandfire.troll_tusk");
   @ObjectHolder("iceandfire:myrmex_desert_egg")
   public static Item myrmex_desert_egg = new ItemMyrmexEgg(false);
   @ObjectHolder("iceandfire:myrmex_jungle_egg")
   public static Item myrmex_jungle_egg = new ItemMyrmexEgg(true);
   @ObjectHolder("iceandfire:myrmex_desert_resin")
   public static Item myrmex_desert_resin = new ItemGeneric("myrmex_desert_resin", "iceandfire.myrmex_desert_resin");
   @ObjectHolder("iceandfire:myrmex_jungle_resin")
   public static Item myrmex_jungle_resin = new ItemGeneric("myrmex_jungle_resin", "iceandfire.myrmex_jungle_resin");
   @ObjectHolder("iceandfire:myrmex_desert_chitin")
   public static Item myrmex_desert_chitin = new ItemGeneric("myrmex_desert_chitin", "iceandfire.myrmex_desert_chitin");
   @ObjectHolder("iceandfire:myrmex_jungle_chitin")
   public static Item myrmex_jungle_chitin = new ItemGeneric("myrmex_jungle_chitin", "iceandfire.myrmex_jungle_chitin");
   @ObjectHolder("iceandfire:myrmex_stinger")
   public static Item myrmex_stinger = new ItemGeneric("myrmex_stinger", "iceandfire.myrmex_stinger");
   @ObjectHolder("iceandfire:myrmex_desert_sword")
   public static Item myrmex_desert_sword = new ItemModSword(myrmexChitin, "myrmex_desert_sword", "iceandfire.myrmex_desert_sword");
   @ObjectHolder("iceandfire:myrmex_desert_sword_venom")
   public static Item myrmex_desert_sword_venom = new ItemModSword(myrmexChitin, "myrmex_desert_sword_venom", "iceandfire.myrmex_desert_sword_venom");
   @ObjectHolder("iceandfire:myrmex_desert_shovel")
   public static Item myrmex_desert_shovel = new ItemModShovel(myrmexChitin, "myrmex_desert_shovel", "iceandfire.myrmex_desert_shovel");
   @ObjectHolder("iceandfire:myrmex_desert_pickaxe")
   public static Item myrmex_desert_pickaxe = new ItemModPickaxe(myrmexChitin, "myrmex_desert_pickaxe", "iceandfire.myrmex_desert_pickaxe");
   @ObjectHolder("iceandfire:myrmex_desert_axe")
   public static Item myrmex_desert_axe = new ItemModAxe(myrmexChitin, "myrmex_desert_axe", "iceandfire.myrmex_desert_axe");
   @ObjectHolder("iceandfire:myrmex_desert_hoe")
   public static Item myrmex_desert_hoe = new ItemModHoe(myrmexChitin, "myrmex_desert_hoe", "iceandfire.myrmex_desert_hoe");
   @ObjectHolder("iceandfire:myrmex_jungle_sword")
   public static Item myrmex_jungle_sword = new ItemModSword(myrmexChitin, "myrmex_jungle_sword", "iceandfire.myrmex_jungle_sword");
   @ObjectHolder("iceandfire:myrmex_jungle_sword_venom")
   public static Item myrmex_jungle_sword_venom = new ItemModSword(myrmexChitin, "myrmex_jungle_sword_venom", "iceandfire.myrmex_jungle_sword_venom");
   @ObjectHolder("iceandfire:myrmex_jungle_shovel")
   public static Item myrmex_jungle_shovel = new ItemModShovel(myrmexChitin, "myrmex_jungle_shovel", "iceandfire.myrmex_jungle_shovel");
   @ObjectHolder("iceandfire:myrmex_jungle_pickaxe")
   public static Item myrmex_jungle_pickaxe = new ItemModPickaxe(myrmexChitin, "myrmex_jungle_pickaxe", "iceandfire.myrmex_jungle_pickaxe");
   @ObjectHolder("iceandfire:myrmex_jungle_axe")
   public static Item myrmex_jungle_axe = new ItemModAxe(myrmexChitin, "myrmex_jungle_axe", "iceandfire.myrmex_jungle_axe");
   @ObjectHolder("iceandfire:myrmex_jungle_hoe")
   public static Item myrmex_jungle_hoe = new ItemModHoe(myrmexChitin, "myrmex_jungle_hoe", "iceandfire.myrmex_jungle_hoe");
   @ObjectHolder("iceandfire:myrmex_desert_staff")
   public static Item myrmex_desert_staff = new ItemMyrmexStaff(false);
   @ObjectHolder("iceandfire:myrmex_jungle_staff")
   public static Item myrmex_jungle_staff = new ItemMyrmexStaff(true);
   @ObjectHolder("iceandfire:myrmex_desert_helmet")
   public static Item myrmex_desert_helmet = new ItemModArmor(myrmexDesert, 0, EntityEquipmentSlot.HEAD, "myrmex_desert_helmet", "iceandfire.myrmex_desert_helmet");
   @ObjectHolder("iceandfire:myrmex_desert_chestplate")
   public static Item myrmex_desert_chestplate = new ItemModArmor(myrmexDesert, 1, EntityEquipmentSlot.CHEST, "myrmex_desert_chestplate", "iceandfire.myrmex_desert_chestplate");
   @ObjectHolder("iceandfire:myrmex_desert_leggings")
   public static Item myrmex_desert_leggings = new ItemModArmor(myrmexDesert, 2, EntityEquipmentSlot.LEGS, "myrmex_desert_leggings", "iceandfire.myrmex_desert_leggings");
   @ObjectHolder("iceandfire:myrmex_desert_boots")
   public static Item myrmex_desert_boots = new ItemModArmor(myrmexDesert, 3, EntityEquipmentSlot.FEET, "myrmex_desert_boots", "iceandfire.myrmex_desert_boots");
   @ObjectHolder("iceandfire:myrmex_jungle_helmet")
   public static Item myrmex_jungle_helmet = new ItemModArmor(myrmexJungle, 0, EntityEquipmentSlot.HEAD, "myrmex_jungle_helmet", "iceandfire.myrmex_jungle_helmet");
   @ObjectHolder("iceandfire:myrmex_jungle_chestplate")
   public static Item myrmex_jungle_chestplate = new ItemModArmor(myrmexJungle, 1, EntityEquipmentSlot.CHEST, "myrmex_jungle_chestplate", "iceandfire.myrmex_jungle_chestplate");
   @ObjectHolder("iceandfire:myrmex_jungle_leggings")
   public static Item myrmex_jungle_leggings = new ItemModArmor(myrmexJungle, 2, EntityEquipmentSlot.LEGS, "myrmex_jungle_leggings", "iceandfire.myrmex_jungle_leggings");
   @ObjectHolder("iceandfire:myrmex_jungle_boots")
   public static Item myrmex_jungle_boots = new ItemModArmor(myrmexJungle, 3, EntityEquipmentSlot.FEET, "myrmex_jungle_boots", "iceandfire.myrmex_jungle_boots");
   @ObjectHolder("iceandfire:amphithere_feather")
   public static Item amphithere_feather = new ItemGeneric("amphithere_feather", "iceandfire.amphithere_feather");
   @ObjectHolder("iceandfire:amphithere_arrow")
   public static Item amphithere_arrow = new ItemAmphithereArrow();
   @ObjectHolder("iceandfire:sea_serpent_fang")
   public static Item sea_serpent_fang = new ItemGeneric("sea_serpent_fang", "iceandfire.sea_serpent_fang");
   @ObjectHolder("iceandfire:sea_serpent_arrow")
   public static Item sea_serpent_arrow = new ItemSeaSerpentArrow();

   public ModItems() {
   }

   static {
      EnumDragonArmor.initArmors();
      EnumSeaSerpent.initArmors();
   }
}
