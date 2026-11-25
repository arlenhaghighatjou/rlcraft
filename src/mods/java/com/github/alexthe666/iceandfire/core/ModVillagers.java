package com.github.alexthe666.iceandfire.core;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.enums.EnumBestiaryPages;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import com.google.common.collect.Maps;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemFishFood.FishType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

public class ModVillagers {
   public static final ModVillagers INSTANCE = new ModVillagers();
   public VillagerProfession fisherman;
   public VillagerProfession craftsman;
   public VillagerProfession shaman;
   public VillagerProfession desertMyrmexWorker;
   public VillagerProfession jungleMyrmexWorker;
   public VillagerProfession desertMyrmexSoldier;
   public VillagerProfession jungleMyrmexSoldier;
   public VillagerProfession desertMyrmexSentinel;
   public VillagerProfession jungleMyrmexSentinel;
   public VillagerProfession desertMyrmexRoyal;
   public VillagerProfession jungleMyrmexRoyal;
   public VillagerProfession desertMyrmexQueen;
   public VillagerProfession jungleMyrmexQueen;
   public Map<Integer, VillagerProfession> professions = Maps.newHashMap();

   public ModVillagers() {
   }

   public void init() {
      this.fisherman = new VillagerProfession("iceandfire:fisherman", "iceandfire:textures/models/snowvillager/fisherman.png", "minecraft:textures/entity/zombie_villager/zombie_farmer.png");
      VillagerCareer career = new VillagerCareer(this.fisherman, "fisherman");
      career.addTrade(1, new ITradeList[]{new ModVillagers.SapphireForItems(Items.FISH, new PriceInfo(1, 10))});
      career.addTrade(2, new ITradeList[]{new ModVillagers.ListItemForSapphires(Items.FISHING_ROD, new PriceInfo(1, 3))});
      career.addTrade(1, new ITradeList[]{new ModVillagers.ListItemForSapphires(ModItems.fishing_spear, new PriceInfo(1, 5))});
      career.addTrade(2, new ITradeList[]{new ModVillagers.ListItemForSapphires(Items.COOKED_FISH, new PriceInfo(1, 5))});
      career.addTrade(2, new ITradeList[]{new ModVillagers.ListItemForSapphires(Items.COOKED_FISH, new PriceInfo(1, 5))});
      career.addTrade(3, new ITradeList[]{new ModVillagers.ListItemForSapphires(new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()), new PriceInfo(2, 1))});
      career.addTrade(3, new ITradeList[]{new ModVillagers.ListItemForSapphires(new ItemStack(Blocks.TRIPWIRE_HOOK), new PriceInfo(1, 3))});
      career.addTrade(3, new ITradeList[]{new ModVillagers.ListItemForSapphires(new ItemStack(Items.FISH, 1, FishType.PUFFERFISH.getMetadata()), new PriceInfo(1, 4))});
      this.register(this.fisherman, 0);
      this.craftsman = new VillagerProfession("iceandfire:craftsman", "iceandfire:textures/models/snowvillager/craftsman.png", "minecraft:textures/entity/zombie_villager/zombie_farmer.png");
      career = new VillagerCareer(this.craftsman, "craftsman");
      career.addTrade(1, new ITradeList[]{new ModVillagers.SapphireForItems(Item.getItemFromBlock(Blocks.SNOW), new PriceInfo(1, 32))});
      career.addTrade(2, new ITradeList[]{new ModVillagers.ListItemForSapphires(Item.getItemFromBlock(Blocks.PACKED_ICE), new PriceInfo(1, 3))});
      career.addTrade(3, new ITradeList[]{new ModVillagers.ListItemForSapphires(Item.getItemFromBlock(ModBlocks.dragon_ice), new PriceInfo(4, 1))});
      career.addTrade(1, new ITradeList[]{new ModVillagers.ListItemForSapphires(Items.IRON_SHOVEL, new PriceInfo(1, 4))});
      career.addTrade(2, new ITradeList[]{new ModVillagers.ListItemForSapphires(ModItems.silver_shovel, new PriceInfo(1, 5))});
      career.addTrade(3, new ITradeList[]{new ModVillagers.ListItemForSapphires(Items.DIAMOND_SHOVEL, new PriceInfo(1, 9))});
      career.addTrade(2, new ITradeList[]{new ModVillagers.ListItemForSapphires(Items.LEATHER, new PriceInfo(10, 1))});
      career.addTrade(3, new ITradeList[]{new ModVillagers.ListItemForSapphires(Items.LEATHER_BOOTS, new PriceInfo(1, 3))});
      career.addTrade(3, new ITradeList[]{new ModVillagers.ListItemForSapphires(Items.LEATHER_HELMET, new PriceInfo(1, 4))});
      career.addTrade(3, new ITradeList[]{new ModVillagers.ListItemForSapphires(Items.LEATHER_CHESTPLATE, new PriceInfo(1, 6))});
      career.addTrade(3, new ITradeList[]{new ModVillagers.ListItemForSapphires(Items.LEATHER_LEGGINGS, new PriceInfo(1, 6))});
      career.addTrade(3, new ITradeList[]{new ModVillagers.ListItemForSapphires(Items.DIAMOND_SHOVEL, new PriceInfo(1, 7))});
      career.addTrade(3, new ITradeList[]{new ModVillagers.ListItemForSapphires(EnumTroll.FROST.leather, new PriceInfo(1, 5))});
      this.register(this.craftsman, 1);
      this.shaman = new VillagerProfession("iceandfire:shaman", "iceandfire:textures/models/snowvillager/shaman.png", "minecraft:textures/entity/zombie_villager/zombie_farmer.png");
      career = new VillagerCareer(this.shaman, "shaman");
      career.addTrade(1, new ITradeList[]{new ModVillagers.SapphireForItems(Items.BLAZE_POWDER, new PriceInfo(2, 3))});
      career.addTrade(1, new ITradeList[]{new ModVillagers.SapphireForItems(Items.GHAST_TEAR, new PriceInfo(1, 4))});
      career.addTrade(2, new ITradeList[]{new ModVillagers.SapphireForItems(Items.BREWING_STAND, new PriceInfo(9, 1))});
      career.addTrade(1, new ITradeList[]{new ModVillagers.SapphireForItems(ModItems.dragonbone, new PriceInfo(1, 8))});
      ItemStack stack = new ItemStack(ModItems.bestiary);
      stack.setTagCompound(new NBTTagCompound());
      stack.getTagCompound().setIntArray("Pages", new int[]{EnumBestiaryPages.INTRODUCTION.ordinal(), EnumBestiaryPages.ICEDRAGON.ordinal(), EnumBestiaryPages.ICEDRAGONEGG.ordinal(), EnumBestiaryPages.MATERIALS.ordinal(), EnumBestiaryPages.VILLAGERS.ordinal()});
      career.addTrade(2, new ITradeList[]{new ModVillagers.ListItemForSapphires(stack, new PriceInfo(1, 3))});
      career.addTrade(1, new ITradeList[]{new ModVillagers.ListItemForSapphires(ModItems.manuscript, new PriceInfo(1, 2))});
      career.addTrade(3, new ITradeList[]{new ModVillagers.ListItemForSapphires(ModItems.ice_dragon_flesh, new PriceInfo(1, 5))});
      career.addTrade(3, new ITradeList[]{new ModVillagers.ListItemForSapphires(ModItems.ice_dragon_blood, new PriceInfo(1, 12))});
      career.addTrade(3, new ITradeList[]{new ModVillagers.ListItemForSapphires(ModItems.dragon_flute, new PriceInfo(1, 5))});
      career.addTrade(2, new ITradeList[]{new ModVillagers.ListItemForSapphires(Items.ENDER_EYE, new PriceInfo(2, 5))});
      career.addTrade(2, new ITradeList[]{new ModVillagers.ListItemForSapphires(ModItems.witherbone, new PriceInfo(2, 5))});
      career.addTrade(2, new ITradeList[]{new ModVillagers.ListItemForSapphires(ModItems.wither_shard, new PriceInfo(3, 2))});
      this.register(this.shaman, 2);
      this.desertMyrmexWorker = new VillagerProfession("iceandfire:desertMyrmexWorker", "minecraft:textures/entity/zombie_villager/zombie_farmer.png", "minecraft:textures/entity/zombie_villager/zombie_farmer.png");
      career = new VillagerCareer(this.desertMyrmexWorker, "desert_myrmex_worker");
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Blocks.DIRT), new ItemStack(ModItems.myrmex_desert_resin), new PriceInfo(32, 64), new PriceInfo(1, 2))});
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Blocks.SAND), new ItemStack(ModItems.myrmex_desert_resin), new PriceInfo(32, 64), new PriceInfo(1, 2))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Blocks.DEADBUSH), new ItemStack(ModItems.myrmex_desert_resin), new PriceInfo(5, 10), new PriceInfo(1, 2))});
      career.addTrade(3, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Blocks.IRON_ORE), new ItemStack(ModItems.myrmex_desert_resin), new PriceInfo(10, 15), new PriceInfo(1, 4))});
      career.addTrade(4, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.BONE), new PriceInfo(1, 5), new PriceInfo(5, 15))});
      career.addTrade(4, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Items.SUGAR), new ItemStack(ModItems.myrmex_desert_resin), new PriceInfo(5, 8), new PriceInfo(1, 2))});
      this.jungleMyrmexWorker = new VillagerProfession("iceandfire:jungleMyrmexWorker", "minecraft:textures/entity/zombie_villager/zombie_farmer.png", "minecraft:textures/entity/zombie_villager/zombie_farmer.png");
      career = new VillagerCareer(this.jungleMyrmexWorker, "jungle_myrmex_worker");
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Blocks.DIRT), new ItemStack(ModItems.myrmex_jungle_resin), new PriceInfo(32, 64), new PriceInfo(1, 2))});
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Items.MELON), new ItemStack(ModItems.myrmex_jungle_resin), new PriceInfo(10, 20), new PriceInfo(1, 2))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Blocks.LEAVES, 1, 3), new ItemStack(ModItems.myrmex_jungle_resin), new PriceInfo(48, 64), new PriceInfo(1, 1))});
      career.addTrade(3, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Blocks.GOLD_ORE), new ItemStack(ModItems.myrmex_jungle_resin), new PriceInfo(7, 10), new PriceInfo(1, 4))});
      career.addTrade(4, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.BONE), new PriceInfo(1, 5), new PriceInfo(5, 15))});
      career.addTrade(4, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Items.SUGAR), new ItemStack(ModItems.myrmex_jungle_resin), new PriceInfo(5, 8), new PriceInfo(1, 2))});
      this.desertMyrmexSoldier = new VillagerProfession("iceandfire:desertMyrmexSoldier", "minecraft:textures/entity/zombie_villager/zombie_farmer.png", "minecraft:textures/entity/zombie_villager/zombie_farmer.png");
      career = new VillagerCareer(this.desertMyrmexSoldier, "desert_myrmex_soldier");
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.BONE), new PriceInfo(1, 5), new PriceInfo(1, 15))});
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.FEATHER), new PriceInfo(1, 3), new PriceInfo(1, 3))});
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.STRING), new PriceInfo(1, 3), new PriceInfo(1, 3))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.GUNPOWDER), new PriceInfo(5, 7), new PriceInfo(1, 3))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.RABBIT), new PriceInfo(3, 6), new PriceInfo(1, 1))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.DYE, 1, 2), new PriceInfo(1, 3), new PriceInfo(1, 3))});
      career.addTrade(3, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.IRON_NUGGET), new PriceInfo(1, 3), new PriceInfo(1, 3))});
      career.addTrade(3, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.CHICKEN), new PriceInfo(3, 6), new PriceInfo(1, 1))});
      career.addTrade(4, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.GOLD_NUGGET), new PriceInfo(1, 3), new PriceInfo(1, 3))});
      career.addTrade(4, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(ModItems.silverNugget), new PriceInfo(1, 3), new PriceInfo(1, 3))});
      career.addTrade(5, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.troll_tusk), new ItemStack(ModItems.myrmex_desert_resin), new PriceInfo(1, 1), new PriceInfo(5, 15))});
      this.jungleMyrmexSoldier = new VillagerProfession("iceandfire:jungleMyrmexSoldier", "minecraft:textures/entity/zombie_villager/zombie_farmer.png", "minecraft:textures/entity/zombie_villager/zombie_farmer.png");
      career = new VillagerCareer(this.jungleMyrmexSoldier, "jungle_myrmex_soldier");
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.BONE), new PriceInfo(1, 5), new PriceInfo(1, 15))});
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.FEATHER), new PriceInfo(1, 3), new PriceInfo(1, 3))});
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.STRING), new PriceInfo(1, 3), new PriceInfo(1, 3))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.GUNPOWDER), new PriceInfo(5, 7), new PriceInfo(1, 3))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.RABBIT), new PriceInfo(3, 6), new PriceInfo(1, 1))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.DYE, 1, 3), new PriceInfo(1, 3), new PriceInfo(1, 3))});
      career.addTrade(3, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.IRON_NUGGET), new PriceInfo(1, 3), new PriceInfo(1, 3))});
      career.addTrade(3, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.CHICKEN), new PriceInfo(3, 6), new PriceInfo(1, 1))});
      career.addTrade(4, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.GOLD_NUGGET), new PriceInfo(1, 3), new PriceInfo(1, 3))});
      career.addTrade(4, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(ModItems.silverNugget), new PriceInfo(1, 3), new PriceInfo(1, 3))});
      career.addTrade(5, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.troll_tusk), new ItemStack(ModItems.myrmex_jungle_resin), new PriceInfo(1, 1), new PriceInfo(5, 15))});
      this.desertMyrmexSentinel = new VillagerProfession("iceandfire:desertMyrmexSentinel", "minecraft:textures/entity/zombie_villager/zombie_farmer.png", "minecraft:textures/entity/zombie_villager/zombie_farmer.png");
      career = new VillagerCareer(this.desertMyrmexSentinel, "desert_myrmex_sentinel");
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Items.SPIDER_EYE), new ItemStack(ModItems.myrmex_desert_resin), new PriceInfo(1, 1), new PriceInfo(1, 2))});
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Items.REDSTONE), new ItemStack(ModItems.myrmex_desert_resin), new PriceInfo(3, 4), new PriceInfo(1, 4))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.EGG), new PriceInfo(3, 5), new PriceInfo(1, 1))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.PORKCHOP), new PriceInfo(3, 7), new PriceInfo(1, 1))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.BEEF), new PriceInfo(3, 7), new PriceInfo(1, 1))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.MUTTON), new PriceInfo(3, 7), new PriceInfo(1, 1))});
      career.addTrade(3, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Items.POISONOUS_POTATO), new ItemStack(ModItems.myrmex_desert_resin), new PriceInfo(1, 1), new PriceInfo(10, 20))});
      career.addTrade(4, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Items.FISH, 1, 3), new ItemStack(ModItems.myrmex_desert_resin), new PriceInfo(1, 1), new PriceInfo(5, 10))});
      career.addTrade(5, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.SKULL), new PriceInfo(20, 50), new PriceInfo(1, 1))});
      this.jungleMyrmexSentinel = new VillagerProfession("iceandfire:jungleMyrmexSentinel", "minecraft:textures/entity/zombie_villager/zombie_farmer.png", "minecraft:textures/entity/zombie_villager/zombie_farmer.png");
      career = new VillagerCareer(this.jungleMyrmexSentinel, "jungle_myrmex_sentinel");
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Items.SPIDER_EYE), new ItemStack(ModItems.myrmex_jungle_resin), new PriceInfo(1, 1), new PriceInfo(1, 2))});
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Items.REDSTONE), new ItemStack(ModItems.myrmex_jungle_resin), new PriceInfo(3, 4), new PriceInfo(1, 4))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.EGG), new PriceInfo(3, 5), new PriceInfo(1, 1))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.PORKCHOP), new PriceInfo(3, 7), new PriceInfo(1, 1))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.BEEF), new PriceInfo(3, 7), new PriceInfo(1, 1))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.MUTTON), new PriceInfo(3, 7), new PriceInfo(1, 1))});
      career.addTrade(3, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Items.POISONOUS_POTATO), new ItemStack(ModItems.myrmex_jungle_resin), new PriceInfo(1, 1), new PriceInfo(10, 20))});
      career.addTrade(4, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Items.FISH, 1, 3), new ItemStack(ModItems.myrmex_jungle_resin), new PriceInfo(1, 1), new PriceInfo(5, 10))});
      career.addTrade(5, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.SKULL), new PriceInfo(20, 50), new PriceInfo(1, 1))});
      this.desertMyrmexRoyal = new VillagerProfession("iceandfire:desertMyrmexRoyal", "minecraft:textures/entity/zombie_villager/zombie_farmer.png", "minecraft:textures/entity/zombie_villager/zombie_farmer.png");
      career = new VillagerCareer(this.desertMyrmexRoyal, "desert_myrmex_royal");
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.manuscript), new ItemStack(ModItems.myrmex_desert_resin), new PriceInfo(1, 1), new PriceInfo(4, 6))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Items.GOLD_INGOT), new ItemStack(ModItems.myrmex_desert_resin), new PriceInfo(1, 4), new PriceInfo(5, 8))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.silverIngot), new ItemStack(ModItems.myrmex_desert_resin), new PriceInfo(1, 4), new PriceInfo(5, 8))});
      career.addTrade(3, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.RABBIT_FOOT), new PriceInfo(5, 10), new PriceInfo(1, 1))});
      career.addTrade(4, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.ENDER_PEARL), new PriceInfo(10, 15), new PriceInfo(1, 1))});
      career.addTrade(4, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(ModItems.wither_shard), new PriceInfo(5, 8), new PriceInfo(1, 1))});
      career.addTrade(5, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.MAGMA_CREAM), new PriceInfo(6, 15), new PriceInfo(1, 1))});
      career.addTrade(5, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.QUARTZ), new PriceInfo(7, 15), new PriceInfo(1, 1))});
      career.addTrade(6, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.GOLDEN_CARROT), new PriceInfo(15, 20), new PriceInfo(1, 1))});
      career.addTrade(7, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(Items.EMERALD), new PriceInfo(20, 30), new PriceInfo(1, 1))});
      this.jungleMyrmexRoyal = new VillagerProfession("iceandfire:jungleMyrmexRoyal", "minecraft:textures/entity/zombie_villager/zombie_farmer.png", "minecraft:textures/entity/zombie_villager/zombie_farmer.png");
      career = new VillagerCareer(this.jungleMyrmexRoyal, "jungle_myrmex_royal");
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.manuscript), new ItemStack(ModItems.myrmex_jungle_resin), new PriceInfo(1, 1), new PriceInfo(4, 6))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(Items.GOLD_INGOT), new ItemStack(ModItems.myrmex_jungle_resin), new PriceInfo(1, 4), new PriceInfo(5, 8))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.silverIngot), new ItemStack(ModItems.myrmex_jungle_resin), new PriceInfo(1, 4), new PriceInfo(5, 8))});
      career.addTrade(3, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.RABBIT_FOOT), new PriceInfo(5, 10), new PriceInfo(1, 1))});
      career.addTrade(4, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.ENDER_PEARL), new PriceInfo(10, 15), new PriceInfo(1, 1))});
      career.addTrade(4, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(ModItems.wither_shard), new PriceInfo(5, 8), new PriceInfo(1, 1))});
      career.addTrade(5, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.MAGMA_CREAM), new PriceInfo(6, 15), new PriceInfo(1, 1))});
      career.addTrade(5, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.QUARTZ), new PriceInfo(7, 15), new PriceInfo(1, 1))});
      career.addTrade(6, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.GOLDEN_CARROT), new PriceInfo(15, 20), new PriceInfo(1, 1))});
      career.addTrade(7, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(Items.EMERALD), new PriceInfo(20, 30), new PriceInfo(1, 1))});
      this.desertMyrmexQueen = new VillagerProfession("iceandfire:desertMyrmexQueen", "minecraft:textures/entity/zombie_villager/zombie_farmer.png", "minecraft:textures/entity/zombie_villager/zombie_farmer.png");
      career = new VillagerCareer(this.desertMyrmexQueen, "desert_myrmex_queen");
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(ModItems.myrmex_desert_egg, 1, 0), new PriceInfo(1, 10), new PriceInfo(1, 1))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(ModItems.myrmex_desert_egg, 1, 1), new PriceInfo(10, 20), new PriceInfo(1, 1))});
      career.addTrade(3, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(ModItems.myrmex_desert_egg, 1, 2), new PriceInfo(20, 30), new PriceInfo(1, 1))});
      career.addTrade(4, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(ModItems.myrmex_desert_egg, 1, 3), new PriceInfo(30, 40), new PriceInfo(1, 1))});
      career.addTrade(5, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_desert_resin), new ItemStack(ModItems.myrmex_desert_egg, 1, 4), new PriceInfo(50, 64), new PriceInfo(1, 1))});
      this.jungleMyrmexQueen = new VillagerProfession("iceandfire:jungleMyrmexQueen", "minecraft:textures/entity/zombie_villager/zombie_farmer.png", "minecraft:textures/entity/zombie_villager/zombie_farmer.png");
      career = new VillagerCareer(this.jungleMyrmexQueen, "jungle_myrmex_queen");
      career.addTrade(1, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(ModItems.myrmex_jungle_egg, 1, 0), new PriceInfo(1, 10), new PriceInfo(1, 1))});
      career.addTrade(2, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(ModItems.myrmex_jungle_egg, 1, 1), new PriceInfo(10, 20), new PriceInfo(1, 1))});
      career.addTrade(3, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(ModItems.myrmex_jungle_egg, 1, 2), new PriceInfo(20, 30), new PriceInfo(1, 1))});
      career.addTrade(4, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(ModItems.myrmex_jungle_egg, 1, 3), new PriceInfo(30, 40), new PriceInfo(1, 1))});
      career.addTrade(5, new ITradeList[]{new EntityMyrmexBase.BasicTrade(new ItemStack(ModItems.myrmex_jungle_resin), new ItemStack(ModItems.myrmex_jungle_egg, 1, 4), new PriceInfo(50, 64), new PriceInfo(1, 1))});
   }

   public void setRandomProfession(EntityVillager entity, EaglercraftRandom rand) {
      entity.setProfession((VillagerProfession)this.professions.get(Integer.valueOf(rand.nextInt(this.professions.size()))));
   }

   private void register(VillagerProfession prof, int id) {
      this.professions.put(Integer.valueOf(id), prof);
   }

   public static class ListItemForSapphires implements ITradeList {
      public ItemStack itemToBuy;
      public PriceInfo priceInfo;

      public ListItemForSapphires(Item par1Item, PriceInfo priceInfo) {
         this.itemToBuy = new ItemStack(par1Item);
         this.priceInfo = priceInfo;
      }

      public ListItemForSapphires(ItemStack stack, PriceInfo priceInfo) {
         this.itemToBuy = stack;
         this.priceInfo = priceInfo;
      }

      public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, EaglercraftRandom random) {
         int i = 1;
         if (this.priceInfo != null) {
            i = this.priceInfo.getPrice(random);
         }

         ItemStack itemstack;
         ItemStack itemstack1;
         if (i < 0) {
            itemstack = new ItemStack(ModItems.sapphireGem);
            itemstack1 = new ItemStack(this.itemToBuy.getItem(), -i, this.itemToBuy.getMetadata());
         } else {
            itemstack = new ItemStack(ModItems.sapphireGem, i, 0);
            itemstack1 = new ItemStack(this.itemToBuy.getItem(), 1, this.itemToBuy.getMetadata());
         }

         recipeList.add(new MerchantRecipe(itemstack, itemstack1));
      }
   }

   public static class SapphireForItems implements ITradeList {
      public Item buyingItem;
      public PriceInfo price;

      public SapphireForItems(Item itemIn, PriceInfo priceIn) {
         this.buyingItem = itemIn;
         this.price = priceIn;
      }

      public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, EaglercraftRandom random) {
         int i = 1;
         if (this.price != null) {
            i = this.price.getPrice(random);
         }

         recipeList.add(new MerchantRecipe(new ItemStack(this.buyingItem, i, 0), ModItems.sapphireGem));
      }
   }
}
