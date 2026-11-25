package xzeroair.trinkets.init;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.items.ViewerItem;
import xzeroair.trinkets.items.base.BaseArrow;
import xzeroair.trinkets.items.base.BaseBow;
import xzeroair.trinkets.items.base.ItemBase;
import xzeroair.trinkets.items.base.RaceFood;
import xzeroair.trinkets.items.base.TrinketRaceBase;
import xzeroair.trinkets.items.baubles.BaubleArcingOrb;
import xzeroair.trinkets.items.baubles.BaubleDamageShield;
import xzeroair.trinkets.items.baubles.BaubleDragonsEye;
import xzeroair.trinkets.items.baubles.BaubleEnderTiara;
import xzeroair.trinkets.items.baubles.BaubleFaelisClaws;
import xzeroair.trinkets.items.baubles.BaubleGlowRing;
import xzeroair.trinkets.items.baubles.BaubleGreaterInertia;
import xzeroair.trinkets.items.baubles.BaubleInertiaNull;
import xzeroair.trinkets.items.baubles.BaublePoison;
import xzeroair.trinkets.items.baubles.BaublePolarized;
import xzeroair.trinkets.items.baubles.BaubleRaceBase;
import xzeroair.trinkets.items.baubles.BaubleSea;
import xzeroair.trinkets.items.baubles.BaubleTeddyBear;
import xzeroair.trinkets.items.baubles.BaubleWeightless;
import xzeroair.trinkets.items.baubles.BaubleWitherRing;
import xzeroair.trinkets.items.foods.Mana_Candy;
import xzeroair.trinkets.items.foods.Mana_Crystal;
import xzeroair.trinkets.items.foods.Mana_Reagent;
import xzeroair.trinkets.items.foods.Restore_Item;
import xzeroair.trinkets.items.trinkets.TrinketArcingOrb;
import xzeroair.trinkets.items.trinkets.TrinketDamageShield;
import xzeroair.trinkets.items.trinkets.TrinketDragonsEye;
import xzeroair.trinkets.items.trinkets.TrinketEnderTiara;
import xzeroair.trinkets.items.trinkets.TrinketFaelisClaws;
import xzeroair.trinkets.items.trinkets.TrinketGlowRing;
import xzeroair.trinkets.items.trinkets.TrinketGreaterInertia;
import xzeroair.trinkets.items.trinkets.TrinketInertiaNull;
import xzeroair.trinkets.items.trinkets.TrinketPoisonStone;
import xzeroair.trinkets.items.trinkets.TrinketPolarized;
import xzeroair.trinkets.items.trinkets.TrinketRibbonBow;
import xzeroair.trinkets.items.trinkets.TrinketSeaStone;
import xzeroair.trinkets.items.trinkets.TrinketTeddyBear;
import xzeroair.trinkets.items.trinkets.TrinketWeightless;
import xzeroair.trinkets.items.trinkets.TrinketWitherRing;
import xzeroair.trinkets.util.TrinketsConfig;

public class ModItems {
   public static final String Weightless = "weightless_stone";
   public static final String InertiaNull = "inertia_null_stone";
   public static final String GreaterInertia = "greater_inertia_stone";
   public static final String GlowRing = "glow_ring";
   public static final String Sea = "sea_stone";
   public static final String Polarized = "polarized_stone";
   public static final String DragonsEye = "dragons_eye";
   public static final String FairyRing = "fairy_ring";
   public static final String DwarfRing = "dwarf_ring";
   public static final String WitherRing = "wither_ring";
   public static final String Poison = "poison_stone";
   public static final String EnderTiara = "ender_tiara";
   public static final String DamageShield = "damage_shield";
   public static final String ArcingOrb = "arcing_orb";
   public static final String TitanRing = "titan_ring";
   public static final String TeddyBear = "teddy_bear";
   public static final String GoblinRing = "goblin_ring";
   public static final String ElfRing = "elf_ring";
   public static final String SuccubusRing = "succubus_ring";
   public static final String FaelisRing = "faelis_ring";
   public static final String DragonRing = "dragon_ring";
   public static final String DragonShield = "dragon_shield";
   public static final String Horn = "horn_of_plenty";
   public static final String GlitterBow = "glitter_bow";
   public static final String RibbonBow = "ribbon_bow";
   public static final String FaelisClaws = "faelis_claw";

   public ModItems() {
   }

   public static void registerItems() {
      ModItems.crafting.registerItems();
      ModItems.foods.registerItems();
      ModItems.misc.registerItems();
      ModItems.trinkets.registerItems();
      ModItems.RaceTrinkets.registerItems();
      if (Trinkets.Baubles) {
         ModItems.baubles.registerItems();
      }

   }

   public static class Materials {
      public static final String GLOWING_NAME = "glowing";
      public static final ArmorMaterial GLOWING = EnumHelper.addArmorMaterial("glowing", "xat:glowing", 25, new int[]{0, 0, 0, 0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F);

      public Materials() {
      }
   }

   public static class RaceTrinkets {
      public static final List<Item> ITEMS = new ArrayList();
      public static final Item TrinketFairyRing;
      public static final Item TrinketDwarfRing;
      public static final Item TrinketTitanRing;
      public static final Item TrinketGoblinRing;
      public static final Item TrinketElfRing;
      public static final Item TrinketFaelisRing;
      public static final Item TrinketDragonRing;

      public RaceTrinkets() {
      }

      protected static void registerItems() {
         registerItem(TrinketFairyRing);
         registerItem(TrinketDwarfRing);
         registerItem(TrinketTitanRing);
         registerItem(TrinketGoblinRing);
         registerItem(TrinketElfRing);
         registerItem(TrinketFaelisRing);
         registerItem(TrinketDragonRing);
      }

      private static void registerItem(Item item) {
         ITEMS.add(item);
      }

      static {
         TrinketFairyRing = new TrinketRaceBase("fairy_ring", EntityRaces.fairy, TrinketsConfig.SERVER.Items.raceRings.FAIRY_RING);
         TrinketDwarfRing = new TrinketRaceBase("dwarf_ring", EntityRaces.dwarf, TrinketsConfig.SERVER.Items.raceRings.DWARF_RING);
         TrinketTitanRing = new TrinketRaceBase("titan_ring", EntityRaces.titan, TrinketsConfig.SERVER.Items.raceRings.TITAN_RING);
         TrinketGoblinRing = new TrinketRaceBase("goblin_ring", EntityRaces.goblin, TrinketsConfig.SERVER.Items.raceRings.GOBLIN_RING);
         TrinketElfRing = new TrinketRaceBase("elf_ring", EntityRaces.elf, TrinketsConfig.SERVER.Items.raceRings.ELF_RING);
         TrinketFaelisRing = new TrinketRaceBase("faelis_ring", EntityRaces.faelis, TrinketsConfig.SERVER.Items.raceRings.FAELIS_RING);
         TrinketDragonRing = new TrinketRaceBase("dragon_ring", EntityRaces.dragon, TrinketsConfig.SERVER.Items.raceRings.DRAGON_RING);
      }
   }

   public static class baubles {
      public static final List<Item> ITEMS = new ArrayList();
      public static final Item BaubleWeightless = new BaubleWeightless("weightless_stone");
      public static final Item BaubleInertiaNull = new BaubleInertiaNull("inertia_null_stone");
      public static final Item BaubleGreaterInertia = new BaubleGreaterInertia("greater_inertia_stone");
      public static final Item BaubleGlowRing = new BaubleGlowRing("glow_ring");
      public static final Item BaubleSea = new BaubleSea("sea_stone");
      public static final Item BaublePolarized = new BaublePolarized("polarized_stone");
      public static final Item BaubleDragonsEye = new BaubleDragonsEye("dragons_eye");
      public static final Item BaubleWitherRing = new BaubleWitherRing("wither_ring");
      public static final Item BaublePoison = new BaublePoison("poison_stone");
      public static final Item BaubleEnderTiara = new BaubleEnderTiara("ender_tiara");
      public static final Item BaubleDamageShield = new BaubleDamageShield("damage_shield");
      public static final Item BaubleArcingOrb = new BaubleArcingOrb("arcing_orb");
      public static final Item BaubleTeddyBear = new BaubleTeddyBear("teddy_bear");
      public static final Item BaubleFaelisClaw = new BaubleFaelisClaws("faelis_claw");
      public static final Item BaubleFairyRing;
      public static final Item BaubleDwarfRing;
      public static final Item BaubleTitanRing;
      public static final Item BaubleGoblinRing;
      public static final Item BaubleElfRing;
      public static final Item BaubleFaelisRing;
      public static final Item BaubleDragonRing;

      public baubles() {
      }

      protected static void registerItems() {
         registerItem(BaubleWeightless);
         registerItem(BaubleInertiaNull);
         registerItem(BaubleGreaterInertia);
         registerItem(BaubleGlowRing);
         registerItem(BaubleSea);
         registerItem(BaublePolarized);
         registerItem(BaubleDragonsEye);
         registerItem(BaubleWitherRing);
         registerItem(BaublePoison);
         registerItem(BaubleEnderTiara);
         registerItem(BaubleDamageShield);
         registerItem(BaubleArcingOrb);
         registerItem(BaubleTeddyBear);
         registerItem(BaubleFaelisClaw);
         registerItem(BaubleFairyRing);
         registerItem(BaubleDwarfRing);
         registerItem(BaubleTitanRing);
         registerItem(BaubleGoblinRing);
         registerItem(BaubleElfRing);
         registerItem(BaubleFaelisRing);
         registerItem(BaubleDragonRing);
      }

      private static void registerItem(Item item) {
         ITEMS.add(item);
      }

      static {
         BaubleFairyRing = new BaubleRaceBase("fairy_ring", EntityRaces.fairy, TrinketsConfig.SERVER.Items.raceRings.FAIRY_RING);
         BaubleDwarfRing = new BaubleRaceBase("dwarf_ring", EntityRaces.dwarf, TrinketsConfig.SERVER.Items.raceRings.DWARF_RING);
         BaubleTitanRing = new BaubleRaceBase("titan_ring", EntityRaces.titan, TrinketsConfig.SERVER.Items.raceRings.TITAN_RING);
         BaubleGoblinRing = new BaubleRaceBase("goblin_ring", EntityRaces.goblin, TrinketsConfig.SERVER.Items.raceRings.GOBLIN_RING);
         BaubleElfRing = new BaubleRaceBase("elf_ring", EntityRaces.elf, TrinketsConfig.SERVER.Items.raceRings.ELF_RING);
         BaubleFaelisRing = new BaubleRaceBase("faelis_ring", EntityRaces.faelis, TrinketsConfig.SERVER.Items.raceRings.FAELIS_RING);
         BaubleDragonRing = new BaubleRaceBase("dragon_ring", EntityRaces.dragon, TrinketsConfig.SERVER.Items.raceRings.DRAGON_RING);
      }
   }

   public static class crafting {
      public static final List<Item> ITEMS = new ArrayList();
      public static final Item glowing_powder = (new ItemBase("glowing_powder")).setMaxDamage(0);
      public static final Item glowing_ingot = (new ItemBase("glowing_ingot")).setMaxDamage(0);
      public static final Item glowing_gem = (new ItemBase("glowing_gem")).setMaxDamage(0);

      public crafting() {
      }

      protected static void registerItems() {
         registerItem(glowing_powder);
         registerItem(glowing_ingot);
         registerItem(glowing_gem);
      }

      private static void registerItem(Item item) {
         ITEMS.add(item);
      }
   }

   public static class foods {
      public static final List<Item> ITEMS = new ArrayList();
      public static final Item dwarf_stout = new RaceFood("dwarf_stout", 16, EnumAction.DRINK, EntityRaces.dwarf);
      public static final Item elf_sap = new RaceFood("elf_sap", 32, EnumAction.DRINK, EntityRaces.elf);
      public static final Item faelis_nip = new RaceFood("faelis_food", 32, EnumAction.EAT, EntityRaces.faelis);
      public static final Item fairy_food = new RaceFood("fairy_dew", 16, EnumAction.DRINK, EntityRaces.fairy);
      public static final Item goblin_soup = new RaceFood("goblin_soup", 32, EnumAction.DRINK, EntityRaces.goblin);
      public static final Item titan_spirit = new RaceFood("titan_spirit", 32, EnumAction.DRINK, EntityRaces.titan);
      public static final Item dragon_gem = new RaceFood("dragon_gem", 32, EnumAction.EAT, EntityRaces.dragon);
      public static final Item mana_candy = new Mana_Candy("mana_candy");
      public static final Item mana_crystal = new Mana_Crystal("mana_crystal");
      public static final Item mana_reagent = new Mana_Reagent("mana_reagent");
      public static final Item restore_Item = new Restore_Item("restoration_serum");

      public foods() {
      }

      protected static void registerItems() {
         registerItem(dwarf_stout);
         registerItem(elf_sap);
         registerItem(faelis_nip);
         registerItem(fairy_food);
         registerItem(goblin_soup);
         registerItem(titan_spirit);
         registerItem(dragon_gem);
         registerItem(mana_candy);
         registerItem(mana_crystal);
         registerItem(mana_reagent);
         registerItem(restore_Item);
      }

      private static void registerItem(Item item) {
         ITEMS.add(item);
      }
   }

   public static class misc {
      public static final List<Item> ITEMS = new ArrayList();
      public static final Item Bow = new BaseBow("trinket_bow");
      public static final Item BowArrow = new BaseArrow("trinket_arrow");
      public static final Item RibbonBow = new TrinketRibbonBow("ribbon_bow");
      public static final Item ViewerItem = new ViewerItem("viewer_item");

      public misc() {
      }

      protected static void registerItems() {
      }

      private static void registerItem(Item item) {
         ITEMS.add(item);
      }
   }

   public static class trinkets {
      public static final List<Item> ITEMS = new ArrayList();
      public static final Item TrinketWeightless = new TrinketWeightless("weightless_stone");
      public static final Item TrinketInertiaNull = new TrinketInertiaNull("inertia_null_stone");
      public static final Item TrinketGreaterInertia = new TrinketGreaterInertia("greater_inertia_stone");
      public static final Item TrinketGlowRing = new TrinketGlowRing("glow_ring");
      public static final Item TrinketSea = new TrinketSeaStone("sea_stone");
      public static final Item TrinketPolarized = new TrinketPolarized("polarized_stone");
      public static final Item TrinketDragonsEye = new TrinketDragonsEye("dragons_eye");
      public static final Item TrinketWitherRing = new TrinketWitherRing("wither_ring");
      public static final Item TrinketPoison = new TrinketPoisonStone("poison_stone");
      public static final Item TrinketEnderTiara = new TrinketEnderTiara("ender_tiara");
      public static final Item TrinketDamageShield = new TrinketDamageShield("damage_shield");
      public static final Item TrinketArcingOrb = new TrinketArcingOrb("arcing_orb");
      public static final Item TrinketTeddyBear = new TrinketTeddyBear("teddy_bear");
      public static final Item TrinketFaelisClaw = new TrinketFaelisClaws("faelis_claw");

      public trinkets() {
      }

      protected static void registerItems() {
         registerItem(TrinketWeightless);
         registerItem(TrinketInertiaNull);
         registerItem(TrinketGreaterInertia);
         registerItem(TrinketGlowRing);
         registerItem(TrinketSea);
         registerItem(TrinketPolarized);
         registerItem(TrinketDragonsEye);
         registerItem(TrinketWitherRing);
         registerItem(TrinketPoison);
         registerItem(TrinketEnderTiara);
         registerItem(TrinketDamageShield);
         registerItem(TrinketArcingOrb);
         registerItem(TrinketTeddyBear);
         registerItem(TrinketFaelisClaw);
      }

      private static void registerItem(Item item) {
         ITEMS.add(item);
      }
   }
}
