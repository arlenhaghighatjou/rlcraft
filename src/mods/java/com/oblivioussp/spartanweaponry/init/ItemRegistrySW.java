package com.oblivioussp.spartanweaponry.init;

import net.lax1dude.eaglercraft.v1_8.HString;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.client.gui.CreativeTabsSW;
import com.oblivioussp.spartanweaponry.item.ItemArrowDiamond;
import com.oblivioussp.spartanweaponry.item.ItemArrowExplosive;
import com.oblivioussp.spartanweaponry.item.ItemArrowIron;
import com.oblivioussp.spartanweaponry.item.ItemArrowSW;
import com.oblivioussp.spartanweaponry.item.ItemArrowTippedDiamond;
import com.oblivioussp.spartanweaponry.item.ItemArrowTippedIron;
import com.oblivioussp.spartanweaponry.item.ItemArrowTippedWood;
import com.oblivioussp.spartanweaponry.item.ItemArrowWood;
import com.oblivioussp.spartanweaponry.item.ItemBattleaxe;
import com.oblivioussp.spartanweaponry.item.ItemBolt;
import com.oblivioussp.spartanweaponry.item.ItemBoltDiamond;
import com.oblivioussp.spartanweaponry.item.ItemBoltDiamondTipped;
import com.oblivioussp.spartanweaponry.item.ItemBoltSpectral;
import com.oblivioussp.spartanweaponry.item.ItemBoltTipped;
import com.oblivioussp.spartanweaponry.item.ItemBoomerang;
import com.oblivioussp.spartanweaponry.item.ItemCaestus;
import com.oblivioussp.spartanweaponry.item.ItemClub;
import com.oblivioussp.spartanweaponry.item.ItemCrossbow;
import com.oblivioussp.spartanweaponry.item.ItemDagger;
import com.oblivioussp.spartanweaponry.item.ItemDynamite;
import com.oblivioussp.spartanweaponry.item.ItemGlaive;
import com.oblivioussp.spartanweaponry.item.ItemGreatsword;
import com.oblivioussp.spartanweaponry.item.ItemHalberd;
import com.oblivioussp.spartanweaponry.item.ItemHammer;
import com.oblivioussp.spartanweaponry.item.ItemJavelin;
import com.oblivioussp.spartanweaponry.item.ItemKatana;
import com.oblivioussp.spartanweaponry.item.ItemLance;
import com.oblivioussp.spartanweaponry.item.ItemLongbow;
import com.oblivioussp.spartanweaponry.item.ItemLongsword;
import com.oblivioussp.spartanweaponry.item.ItemMace;
import com.oblivioussp.spartanweaponry.item.ItemMultiSW;
import com.oblivioussp.spartanweaponry.item.ItemParryingDagger;
import com.oblivioussp.spartanweaponry.item.ItemPike;
import com.oblivioussp.spartanweaponry.item.ItemQuarterstaff;
import com.oblivioussp.spartanweaponry.item.ItemQuiverArrow;
import com.oblivioussp.spartanweaponry.item.ItemQuiverBolt;
import com.oblivioussp.spartanweaponry.item.ItemRapier;
import com.oblivioussp.spartanweaponry.item.ItemSaber;
import com.oblivioussp.spartanweaponry.item.ItemSpear;
import com.oblivioussp.spartanweaponry.item.ItemSwordBase;
import com.oblivioussp.spartanweaponry.item.ItemThrowingAxe;
import com.oblivioussp.spartanweaponry.item.ItemThrowingKnife;
import com.oblivioussp.spartanweaponry.item.ItemThrowingWeapon;
import com.oblivioussp.spartanweaponry.item.ItemWarhammer;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import com.oblivioussp.spartanweaponry.util.Log;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemRegistrySW {
   public static ToolMaterialEx materialCopper;
   public static ToolMaterialEx materialTin;
   public static ToolMaterialEx materialBronze;
   public static ToolMaterialEx materialSteel;
   public static ToolMaterialEx materialSilver;
   public static ToolMaterialEx materialInvar;
   public static ToolMaterialEx materialPlatinum;
   public static ToolMaterialEx materialElectrum;
   public static ToolMaterialEx materialNickel;
   public static ToolMaterialEx materialLead;
   public static ToolMaterialEx materialTitanium;
   public static List<Item> items = new ArrayList();
   public static ArrayList<Item> customWeapons = new ArrayList();
   public static ArrayList<ToolMaterialEx> customMaterials = new ArrayList();
   public static ItemMultiSW material;
   public static ItemSwordBase daggerWood;
   public static ItemSwordBase daggerStone;
   public static ItemSwordBase daggerIron;
   public static ItemSwordBase daggerGold;
   public static ItemSwordBase daggerDiamond;
   public static ItemSwordBase longswordWood;
   public static ItemSwordBase longswordStone;
   public static ItemSwordBase longswordIron;
   public static ItemSwordBase longswordGold;
   public static ItemSwordBase longswordDiamond;
   public static ItemSwordBase katanaWood;
   public static ItemSwordBase katanaStone;
   public static ItemSwordBase katanaIron;
   public static ItemSwordBase katanaGold;
   public static ItemSwordBase katanaDiamond;
   public static ItemSwordBase saberWood;
   public static ItemSwordBase saberStone;
   public static ItemSwordBase saberIron;
   public static ItemSwordBase saberGold;
   public static ItemSwordBase saberDiamond;
   public static ItemSwordBase rapierWood;
   public static ItemSwordBase rapierStone;
   public static ItemSwordBase rapierIron;
   public static ItemSwordBase rapierGold;
   public static ItemSwordBase rapierDiamond;
   public static ItemSwordBase greatswordWood;
   public static ItemSwordBase greatswordStone;
   public static ItemSwordBase greatswordIron;
   public static ItemSwordBase greatswordGold;
   public static ItemSwordBase greatswordDiamond;
   public static ItemSwordBase caestusBasic;
   public static ItemSwordBase caestusStudded;
   public static ItemSwordBase clubWood;
   public static ItemSwordBase clubStudded;
   public static ItemSwordBase hammerWood;
   public static ItemSwordBase hammerStone;
   public static ItemSwordBase hammerIron;
   public static ItemSwordBase hammerGold;
   public static ItemSwordBase hammerDiamond;
   public static ItemSwordBase warhammerWood;
   public static ItemSwordBase warhammerStone;
   public static ItemSwordBase warhammerIron;
   public static ItemSwordBase warhammerGold;
   public static ItemSwordBase warhammerDiamond;
   public static ItemSwordBase spearWood;
   public static ItemSwordBase spearStone;
   public static ItemSwordBase spearIron;
   public static ItemSwordBase spearGold;
   public static ItemSwordBase spearDiamond;
   public static ItemSwordBase halberdWood;
   public static ItemSwordBase halberdStone;
   public static ItemSwordBase halberdIron;
   public static ItemSwordBase halberdGold;
   public static ItemSwordBase halberdDiamond;
   public static ItemSwordBase pikeWood;
   public static ItemSwordBase pikeStone;
   public static ItemSwordBase pikeIron;
   public static ItemSwordBase pikeGold;
   public static ItemSwordBase pikeDiamond;
   public static ItemSwordBase lanceWood;
   public static ItemSwordBase lanceStone;
   public static ItemSwordBase lanceIron;
   public static ItemSwordBase lanceGold;
   public static ItemSwordBase lanceDiamond;
   public static ItemLongbow longbowWood;
   public static ItemLongbow longbowLeather;
   public static ItemLongbow longbowIron;
   public static ItemLongbow longbowDiamond;
   public static ItemCrossbow crossbowWood;
   public static ItemCrossbow crossbowLeather;
   public static ItemCrossbow crossbowIron;
   public static ItemCrossbow crossbowDiamond;
   public static ItemThrowingWeapon throwingKnifeWood;
   public static ItemThrowingWeapon throwingKnifeStone;
   public static ItemThrowingWeapon throwingKnifeIron;
   public static ItemThrowingWeapon throwingKnifeGold;
   public static ItemThrowingWeapon throwingKnifeDiamond;
   public static ItemThrowingWeapon throwingAxeWood;
   public static ItemThrowingWeapon throwingAxeStone;
   public static ItemThrowingWeapon throwingAxeIron;
   public static ItemThrowingWeapon throwingAxeGold;
   public static ItemThrowingWeapon throwingAxeDiamond;
   public static ItemThrowingWeapon javelinWood;
   public static ItemThrowingWeapon javelinStone;
   public static ItemThrowingWeapon javelinIron;
   public static ItemThrowingWeapon javelinGold;
   public static ItemThrowingWeapon javelinDiamond;
   public static ItemQuiverArrow quiverArrowLight;
   public static ItemQuiverArrow quiverArrowModerate;
   public static ItemQuiverArrow quiverArrowHeavy;
   public static ItemArrowSW arrowWood;
   public static ItemArrowSW arrowIron;
   public static ItemArrowSW arrowDiamond;
   public static ItemArrowSW arrowExplosive;
   public static ItemArrowSW arrowTippedWood;
   public static ItemArrowSW arrowTippedIron;
   public static ItemArrowSW arrowTippedDiamond;
   public static ItemQuiverBolt quiverBoltLight;
   public static ItemQuiverBolt quiverBoltModerate;
   public static ItemQuiverBolt quiverBoltHeavy;
   public static ItemBolt bolt;
   public static ItemBolt boltDiamond;
   public static ItemBolt boltSpectral;
   public static ItemBolt boltTipped;
   public static ItemBolt boltTippedDiamond;
   public static ItemSwordBase parryingDaggerWood;
   public static ItemSwordBase parryingDaggerStone;
   public static ItemSwordBase parryingDaggerIron;
   public static ItemSwordBase parryingDaggerGold;
   public static ItemSwordBase parryingDaggerDiamond;
   public static ItemBoomerang boomerangWood;
   public static ItemBoomerang boomerangStone;
   public static ItemBoomerang boomerangIron;
   public static ItemBoomerang boomerangGold;
   public static ItemBoomerang boomerangDiamond;
   public static ItemBattleaxe battleaxeWood;
   public static ItemBattleaxe battleaxeStone;
   public static ItemBattleaxe battleaxeIron;
   public static ItemBattleaxe battleaxeGold;
   public static ItemBattleaxe battleaxeDiamond;
   public static ItemMace maceWood;
   public static ItemMace maceStone;
   public static ItemMace maceIron;
   public static ItemMace maceGold;
   public static ItemMace maceDiamond;
   public static ItemGlaive glaiveWood;
   public static ItemGlaive glaiveStone;
   public static ItemGlaive glaiveIron;
   public static ItemGlaive glaiveGold;
   public static ItemGlaive glaiveDiamond;
   public static ItemSwordBase quarterstaffWood;
   public static ItemSwordBase quarterstaffStone;
   public static ItemSwordBase quarterstaffIron;
   public static ItemSwordBase quarterstaffGold;
   public static ItemSwordBase quarterstaffDiamond;
   public static ItemDynamite dynamite = new ItemDynamite("dynamite");

   public ItemRegistrySW() {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Item.class, ItemRegistrySW::registerItems);
   }

   public static void registerItems(Register<Item> ev) {
      IForgeRegistry<Item> registry = ev.getRegistry();
      material = new ItemMultiSW("material", new String[]{"handle", "pole", "explosive_charge"});
      registry.register(material);
      if (!ConfigHandler.disableDagger) {
         daggerWood = new ItemDagger("dagger_wood", ToolMaterialEx.WOOD);
         daggerStone = new ItemDagger("dagger_stone", ToolMaterialEx.STONE);
         daggerIron = new ItemDagger("dagger_iron", ToolMaterialEx.IRON);
         daggerGold = new ItemDagger("dagger_gold", ToolMaterialEx.GOLD);
         daggerDiamond = new ItemDagger("dagger_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(daggerWood, daggerStone, daggerIron, daggerGold, daggerDiamond);
      }

      if (!ConfigHandler.disableParryingDagger) {
         parryingDaggerWood = new ItemParryingDagger("parrying_dagger_wood", ToolMaterialEx.WOOD);
         parryingDaggerStone = new ItemParryingDagger("parrying_dagger_stone", ToolMaterialEx.STONE);
         parryingDaggerIron = new ItemParryingDagger("parrying_dagger_iron", ToolMaterialEx.IRON);
         parryingDaggerGold = new ItemParryingDagger("parrying_dagger_gold", ToolMaterialEx.GOLD);
         parryingDaggerDiamond = new ItemParryingDagger("parrying_dagger_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(parryingDaggerWood, parryingDaggerStone, parryingDaggerIron, parryingDaggerGold, parryingDaggerDiamond);
      }

      if (!ConfigHandler.disableLongsword) {
         longswordWood = new ItemLongsword("longsword_wood", ToolMaterialEx.WOOD);
         longswordStone = new ItemLongsword("longsword_stone", ToolMaterialEx.STONE);
         longswordIron = new ItemLongsword("longsword_iron", ToolMaterialEx.IRON);
         longswordGold = new ItemLongsword("longsword_gold", ToolMaterialEx.GOLD);
         longswordDiamond = new ItemLongsword("longsword_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(longswordWood, longswordStone, longswordIron, longswordGold, longswordDiamond);
      }

      if (!ConfigHandler.disableKatana) {
         katanaWood = new ItemKatana("katana_wood", ToolMaterialEx.WOOD);
         katanaStone = new ItemKatana("katana_stone", ToolMaterialEx.STONE);
         katanaIron = new ItemKatana("katana_iron", ToolMaterialEx.IRON);
         katanaGold = new ItemKatana("katana_gold", ToolMaterialEx.GOLD);
         katanaDiamond = new ItemKatana("katana_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(katanaWood, katanaStone, katanaIron, katanaGold, katanaDiamond);
      }

      if (!ConfigHandler.disableSaber) {
         saberWood = new ItemSaber("saber_wood", ToolMaterialEx.WOOD);
         saberStone = new ItemSaber("saber_stone", ToolMaterialEx.STONE);
         saberIron = new ItemSaber("saber_iron", ToolMaterialEx.IRON);
         saberGold = new ItemSaber("saber_gold", ToolMaterialEx.GOLD);
         saberDiamond = new ItemSaber("saber_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(saberWood, saberStone, saberIron, saberGold, saberDiamond);
      }

      if (!ConfigHandler.disableRapier) {
         rapierWood = new ItemRapier("rapier_wood", ToolMaterialEx.WOOD);
         rapierStone = new ItemRapier("rapier_stone", ToolMaterialEx.STONE);
         rapierIron = new ItemRapier("rapier_iron", ToolMaterialEx.IRON);
         rapierGold = new ItemRapier("rapier_gold", ToolMaterialEx.GOLD);
         rapierDiamond = new ItemRapier("rapier_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(rapierWood, rapierStone, rapierIron, rapierGold, rapierDiamond);
      }

      if (!ConfigHandler.disableGreatsword) {
         greatswordWood = new ItemGreatsword("greatsword_wood", ToolMaterialEx.WOOD);
         greatswordStone = new ItemGreatsword("greatsword_stone", ToolMaterialEx.STONE);
         greatswordIron = new ItemGreatsword("greatsword_iron", ToolMaterialEx.IRON);
         greatswordGold = new ItemGreatsword("greatsword_gold", ToolMaterialEx.GOLD);
         greatswordDiamond = new ItemGreatsword("greatsword_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(greatswordWood, greatswordStone, greatswordIron, greatswordGold, greatswordDiamond);
      }

      if (!ConfigHandler.disableCaestus) {
         caestusBasic = new ItemCaestus("caestus", ToolMaterialEx.WOOD);
         caestusStudded = new ItemCaestus("caestus_studded", ToolMaterialEx.IRON);
         addItemsToRegister(caestusBasic, caestusStudded);
      }

      if (!ConfigHandler.disableClub) {
         clubWood = new ItemClub("club_wood", ToolMaterialEx.WOOD, 131);
         clubStudded = new ItemClub("club_studded", ToolMaterialEx.IRON, 250);
         addItemsToRegister(clubWood, clubStudded);
      }

      if (!ConfigHandler.disableHammer) {
         hammerWood = new ItemHammer("hammer_wood", ToolMaterialEx.WOOD);
         hammerStone = new ItemHammer("hammer_stone", ToolMaterialEx.STONE);
         hammerIron = new ItemHammer("hammer_iron", ToolMaterialEx.IRON);
         hammerGold = new ItemHammer("hammer_gold", ToolMaterialEx.GOLD);
         hammerDiamond = new ItemHammer("hammer_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(hammerWood, hammerStone, hammerIron, hammerGold, hammerDiamond);
      }

      if (!ConfigHandler.disableWarhammer) {
         warhammerWood = new ItemWarhammer("warhammer_wood", ToolMaterialEx.WOOD);
         warhammerStone = new ItemWarhammer("warhammer_stone", ToolMaterialEx.STONE);
         warhammerIron = new ItemWarhammer("warhammer_iron", ToolMaterialEx.IRON);
         warhammerGold = new ItemWarhammer("warhammer_gold", ToolMaterialEx.GOLD);
         warhammerDiamond = new ItemWarhammer("warhammer_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(warhammerWood, warhammerStone, warhammerIron, warhammerGold, warhammerDiamond);
      }

      if (!ConfigHandler.disableSpear) {
         spearWood = new ItemSpear("spear_wood", ToolMaterialEx.WOOD);
         spearStone = new ItemSpear("spear_stone", ToolMaterialEx.STONE);
         spearIron = new ItemSpear("spear_iron", ToolMaterialEx.IRON);
         spearGold = new ItemSpear("spear_gold", ToolMaterialEx.GOLD);
         spearDiamond = new ItemSpear("spear_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(spearWood, spearStone, spearIron, spearGold, spearDiamond);
      }

      if (!ConfigHandler.disableHalberd) {
         halberdWood = new ItemHalberd("halberd_wood", ToolMaterialEx.WOOD);
         halberdStone = new ItemHalberd("halberd_stone", ToolMaterialEx.STONE);
         halberdIron = new ItemHalberd("halberd_iron", ToolMaterialEx.IRON);
         halberdGold = new ItemHalberd("halberd_gold", ToolMaterialEx.GOLD);
         halberdDiamond = new ItemHalberd("halberd_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(halberdWood, halberdStone, halberdIron, halberdGold, halberdDiamond);
      }

      if (!ConfigHandler.disablePike) {
         pikeWood = new ItemPike("pike_wood", ToolMaterialEx.WOOD);
         pikeStone = new ItemPike("pike_stone", ToolMaterialEx.STONE);
         pikeIron = new ItemPike("pike_iron", ToolMaterialEx.IRON);
         pikeGold = new ItemPike("pike_gold", ToolMaterialEx.GOLD);
         pikeDiamond = new ItemPike("pike_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(pikeWood, pikeStone, pikeIron, pikeGold, pikeDiamond);
      }

      if (!ConfigHandler.disableLance) {
         lanceWood = new ItemLance("lance_wood", ToolMaterialEx.WOOD);
         lanceStone = new ItemLance("lance_stone", ToolMaterialEx.STONE);
         lanceIron = new ItemLance("lance_iron", ToolMaterialEx.IRON);
         lanceGold = new ItemLance("lance_gold", ToolMaterialEx.GOLD);
         lanceDiamond = new ItemLance("lance_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(lanceWood, lanceStone, lanceIron, lanceGold, lanceDiamond);
      }

      if (!ConfigHandler.disableLongbow) {
         longbowWood = new ItemLongbow("longbow_wood", ToolMaterialEx.WOOD);
         if (!ConfigHandler.woodenLongbowOnly) {
            longbowLeather = new ItemLongbow("longbow_leather", ToolMaterialEx.LEATHER);
            longbowIron = new ItemLongbow("longbow_iron", ToolMaterialEx.IRON);
            longbowDiamond = new ItemLongbow("longbow_diamond", ToolMaterialEx.DIAMOND);
         }

         addItemsToRegister(longbowWood, longbowLeather, longbowIron, longbowDiamond);
      }

      if (!ConfigHandler.disableCrossbow) {
         crossbowWood = new ItemCrossbow("crossbow_wood", ToolMaterialEx.WOOD);
         if (!ConfigHandler.woodenCrossbowOnly) {
            crossbowLeather = new ItemCrossbow("crossbow_leather", ToolMaterialEx.LEATHER);
            crossbowIron = new ItemCrossbow("crossbow_iron", ToolMaterialEx.IRON);
            crossbowDiamond = new ItemCrossbow("crossbow_diamond", ToolMaterialEx.DIAMOND);
         }

         addItemsToRegister(crossbowWood, crossbowLeather, crossbowIron, crossbowDiamond);
      }

      if (!ConfigHandler.disableThrowingKnife) {
         throwingKnifeWood = new ItemThrowingKnife("throwing_knife_wood", ToolMaterialEx.WOOD);
         throwingKnifeStone = new ItemThrowingKnife("throwing_knife_stone", ToolMaterialEx.STONE);
         throwingKnifeIron = new ItemThrowingKnife("throwing_knife_iron", ToolMaterialEx.IRON);
         throwingKnifeGold = new ItemThrowingKnife("throwing_knife_gold", ToolMaterialEx.GOLD);
         throwingKnifeDiamond = new ItemThrowingKnife("throwing_knife_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(throwingKnifeWood, throwingKnifeStone, throwingKnifeIron, throwingKnifeGold, throwingKnifeDiamond);
      }

      if (!ConfigHandler.disableThrowingAxe) {
         throwingAxeWood = new ItemThrowingAxe("throwing_axe_wood", ToolMaterialEx.WOOD);
         throwingAxeStone = new ItemThrowingAxe("throwing_axe_stone", ToolMaterialEx.STONE);
         throwingAxeIron = new ItemThrowingAxe("throwing_axe_iron", ToolMaterialEx.IRON);
         throwingAxeGold = new ItemThrowingAxe("throwing_axe_gold", ToolMaterialEx.GOLD);
         throwingAxeDiamond = new ItemThrowingAxe("throwing_axe_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(throwingAxeWood, throwingAxeStone, throwingAxeIron, throwingAxeGold, throwingAxeDiamond);
      }

      if (!ConfigHandler.disableJavelin) {
         javelinWood = new ItemJavelin("javelin_wood", ToolMaterialEx.WOOD);
         javelinStone = new ItemJavelin("javelin_stone", ToolMaterialEx.STONE);
         javelinIron = new ItemJavelin("javelin_iron", ToolMaterialEx.IRON);
         javelinGold = new ItemJavelin("javelin_gold", ToolMaterialEx.GOLD);
         javelinDiamond = new ItemJavelin("javelin_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(javelinWood, javelinStone, javelinIron, javelinGold, javelinDiamond);
      }

      if (!ConfigHandler.disableBoomerang) {
         boomerangWood = new ItemBoomerang("boomerang_wood", ToolMaterialEx.WOOD);
         if (!ConfigHandler.woodenBoomerangOnly) {
            boomerangStone = new ItemBoomerang("boomerang_stone", ToolMaterialEx.STONE);
            boomerangIron = new ItemBoomerang("boomerang_iron", ToolMaterialEx.IRON);
            boomerangGold = new ItemBoomerang("boomerang_gold", ToolMaterialEx.GOLD);
            boomerangDiamond = new ItemBoomerang("boomerang_diamond", ToolMaterialEx.DIAMOND);
         }

         addItemsToRegister(boomerangWood, boomerangStone, boomerangIron, boomerangGold, boomerangDiamond);
      }

      if (!ConfigHandler.disableBattleaxe) {
         battleaxeWood = new ItemBattleaxe("battleaxe_wood", ToolMaterialEx.WOOD);
         battleaxeStone = new ItemBattleaxe("battleaxe_stone", ToolMaterialEx.STONE);
         battleaxeIron = new ItemBattleaxe("battleaxe_iron", ToolMaterialEx.IRON);
         battleaxeGold = new ItemBattleaxe("battleaxe_gold", ToolMaterialEx.GOLD);
         battleaxeDiamond = new ItemBattleaxe("battleaxe_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(battleaxeWood, battleaxeStone, battleaxeIron, battleaxeGold, battleaxeDiamond);
      }

      if (!ConfigHandler.disableMace) {
         maceWood = new ItemMace("mace_wood", ToolMaterialEx.WOOD);
         maceStone = new ItemMace("mace_stone", ToolMaterialEx.STONE);
         maceIron = new ItemMace("mace_iron", ToolMaterialEx.IRON);
         maceGold = new ItemMace("mace_gold", ToolMaterialEx.GOLD);
         maceDiamond = new ItemMace("mace_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(maceWood, maceStone, maceIron, maceGold, maceDiamond);
      }

      if (!ConfigHandler.disableGlaive) {
         glaiveWood = new ItemGlaive("glaive_wood", ToolMaterialEx.WOOD);
         glaiveStone = new ItemGlaive("glaive_stone", ToolMaterialEx.STONE);
         glaiveIron = new ItemGlaive("glaive_iron", ToolMaterialEx.IRON);
         glaiveGold = new ItemGlaive("glaive_gold", ToolMaterialEx.GOLD);
         glaiveDiamond = new ItemGlaive("glaive_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(glaiveWood, glaiveStone, glaiveIron, glaiveGold, glaiveDiamond);
      }

      if (!ConfigHandler.disableQuarterstaff) {
         quarterstaffWood = new ItemQuarterstaff("staff", ToolMaterialEx.WOOD);
         quarterstaffStone = new ItemQuarterstaff("staff_stone", ToolMaterialEx.STONE);
         quarterstaffIron = new ItemQuarterstaff("staff_iron", ToolMaterialEx.IRON);
         quarterstaffGold = new ItemQuarterstaff("staff_gold", ToolMaterialEx.GOLD);
         quarterstaffDiamond = new ItemQuarterstaff("staff_diamond", ToolMaterialEx.DIAMOND);
         addItemsToRegister(quarterstaffWood, quarterstaffStone, quarterstaffIron, quarterstaffGold, quarterstaffDiamond);
      }

      if (!ConfigHandler.disableExplosives) {
         dynamite = new ItemDynamite("dynamite");
         addItemsToRegister(dynamite);
      }

      if (!ConfigHandler.disableQuivers) {
         quiverArrowLight = new ItemQuiverArrow("quiver_arrow", 4);
         quiverArrowModerate = new ItemQuiverArrow("quiver_arrow_moderate", 6);
         quiverArrowHeavy = new ItemQuiverArrow("quiver_arrow_heavy", 9);
         addItemsToRegister(quiverArrowLight, quiverArrowModerate, quiverArrowHeavy);
      }

      if (!ConfigHandler.disableNewArrows) {
         arrowWood = new ItemArrowWood("arrow_wood");
         arrowIron = new ItemArrowIron("arrow_iron");
         if (!ConfigHandler.disableExplosives) {
            arrowExplosive = new ItemArrowExplosive("arrow_explosive");
         }

         arrowTippedWood = new ItemArrowTippedWood("arrow_wood_tipped", "arrow_wood");
         arrowTippedIron = new ItemArrowTippedIron("arrow_iron_tipped", "arrow_iron");
         if (!ConfigHandler.disableDiamondArrowsAndBolts) {
            arrowDiamond = new ItemArrowDiamond("arrow_diamond");
            arrowTippedDiamond = new ItemArrowTippedDiamond("arrow_diamond_tipped", "arrow_diamond");
         }

         addItemsToRegister(arrowWood, arrowIron, arrowDiamond, arrowExplosive, arrowTippedWood, arrowTippedIron, arrowTippedDiamond);
      }

      if (!ConfigHandler.disableCrossbow) {
         if (!ConfigHandler.disableQuivers) {
            quiverBoltLight = new ItemQuiverBolt("quiver_bolt", 4);
            quiverBoltModerate = new ItemQuiverBolt("quiver_bolt_moderate", 6);
            quiverBoltHeavy = new ItemQuiverBolt("quiver_bolt_heavy", 9);
         }

         bolt = new ItemBolt("bolt", ConfigHandler.boltBaseDamage, ConfigHandler.boltRangeMultiplier, ConfigHandler.boltArmorPiercingFactor);
         boltSpectral = new ItemBoltSpectral("bolt_spectral", ConfigHandler.boltBaseDamage, ConfigHandler.boltRangeMultiplier, ConfigHandler.boltArmorPiercingFactor);
         boltTipped = new ItemBoltTipped("bolt_tipped", ConfigHandler.boltBaseDamage, ConfigHandler.boltRangeMultiplier, ConfigHandler.boltArmorPiercingFactor);
         if (!ConfigHandler.disableDiamondArrowsAndBolts) {
            boltDiamond = new ItemBoltDiamond("bolt_diamond");
            boltTippedDiamond = new ItemBoltDiamondTipped("bolt_diamond_tipped");
         }

         addItemsToRegister(quiverBoltLight, quiverBoltModerate, quiverBoltHeavy, bolt, boltDiamond, boltSpectral, boltTipped, boltTippedDiamond);
      }

      for(Item item : items) {
         registry.register(item);
         ModelRenderRegistry.addItemToRegistry(item, item.getRegistryName());
      }

      items.clear();
      if (ConfigHandler.enableModdedMaterialWeapons) {
         registerModdedMaterialWeapons(registry);
      } else {
         Log.info("Weapons made from modded materials have been disabled! Ending item registration.");
      }

      Log.info("Items Registered!");
   }

   public static void addItemsToRegister(Item... itemsToAdd) {
      for(Item item : itemsToAdd) {
         if (item != null) {
            items.add(item);
         }
      }

   }

   public static void registerModdedMaterials() {
      materialCopper = new ToolMaterialEx("copper", "ingotCopper", "spartanweaponry", ConfigHandler.materialPrimaryColourCopper, ConfigHandler.materialSecondaryColourCopper, 1, ConfigHandler.durabilityMaterialCopper, 5.0F, ConfigHandler.damageMaterialCopper, 8);
      materialTin = new ToolMaterialEx("tin", "ingotTin", "spartanweaponry", ConfigHandler.materialPrimaryColourTin, ConfigHandler.materialSecondaryColourTin, 1, ConfigHandler.durabilityMaterialTin, 5.25F, ConfigHandler.damageMaterialTin, 6);
      materialBronze = new ToolMaterialEx("bronze", "ingotBronze", "spartanweaponry", ConfigHandler.materialPrimaryColourBronze, ConfigHandler.materialSecondaryColourBronze, 2, ConfigHandler.durabilityMaterialBronze, 5.75F, ConfigHandler.damageMaterialBronze, 12);
      materialSteel = new ToolMaterialEx("steel", "ingotSteel", "spartanweaponry", ConfigHandler.materialPrimaryColourSteel, ConfigHandler.materialSecondaryColourSteel, 2, ConfigHandler.durabilityMaterialSteel, 6.5F, ConfigHandler.damageMaterialSteel, 14);
      materialSilver = new ToolMaterialEx("silver", "ingotSilver", "spartanweaponry", ConfigHandler.materialPrimaryColourSilver, ConfigHandler.materialSecondaryColourSilver, 1, ConfigHandler.durabilityMaterialSilver, 5.0F, ConfigHandler.damageMaterialSilver, 16, new WeaponProperty[]{WeaponProperties.EXTRA_DAMAGE_50P_UNDEAD});
      materialInvar = new ToolMaterialEx("invar", "ingotInvar", "spartanweaponry", ConfigHandler.materialPrimaryColourInvar, ConfigHandler.materialSecondaryColourInvar, 2, ConfigHandler.durabilityMaterialInvar, 6.0F, ConfigHandler.damageMaterialInvar, 12);
      materialPlatinum = new ToolMaterialEx("platinum", "ingotPlatinum", "spartanweaponry", ConfigHandler.materialPrimaryColourPlatinum, ConfigHandler.materialSecondaryColourPlatinum, 3, ConfigHandler.durabilityMaterialPlatinum, 4.0F, ConfigHandler.damageMaterialPlatinum, 18);
      materialElectrum = new ToolMaterialEx("electrum", "ingotElectrum", "spartanweaponry", ConfigHandler.materialPrimaryColourElectrum, ConfigHandler.materialSecondaryColourElectrum, 1, ConfigHandler.durabilityMaterialElectrum, 3.5F, ConfigHandler.damageMaterialElectrum, 8);
      materialNickel = new ToolMaterialEx("nickel", "ingotNickel", "spartanweaponry", ConfigHandler.materialPrimaryColourNickel, ConfigHandler.materialSecondaryColourNickel, 1, ConfigHandler.durabilityMaterialNickel, 4.5F, ConfigHandler.damageMaterialNickel, 6);
      materialLead = new ToolMaterialEx("lead", "ingotLead", "spartanweaponry", ConfigHandler.materialPrimaryColourLead, ConfigHandler.materialSecondaryColourLead, 1, ConfigHandler.durabilityMaterialLead, 4.5F, ConfigHandler.damageMaterialLead, 5);
      if (!ConfigHandler.disableMaterialCopper) {
         customMaterials.add(materialCopper);
      }

      if (!ConfigHandler.disableMaterialTin) {
         customMaterials.add(materialTin);
      }

      if (!ConfigHandler.disableMaterialBronze) {
         customMaterials.add(materialBronze);
      }

      if (!ConfigHandler.disableMaterialSteel) {
         customMaterials.add(materialSteel);
      }

      if (!ConfigHandler.disableMaterialSilver) {
         customMaterials.add(materialSilver);
      }

      if (!ConfigHandler.disableMaterialInvar) {
         customMaterials.add(materialInvar);
      }

      if (!ConfigHandler.disableMaterialPlatinum) {
         customMaterials.add(materialPlatinum);
      }

      if (!ConfigHandler.disableMaterialElectrum) {
         customMaterials.add(materialElectrum);
      }

      if (!ConfigHandler.disableMaterialNickel) {
         customMaterials.add(materialNickel);
      }

      if (!ConfigHandler.disableMaterialLead) {
         customMaterials.add(materialLead);
      }

   }

   protected static void registerModdedMaterialWeapons(IForgeRegistry<Item> registry) {
      Log.info(HString.format("Registering %d modded material based weapons", customMaterials.size()));
      String matNames = "";

      for(ToolMaterialEx material : customMaterials) {
         String name = material.getUnlocName().substring(0, 1).toUpperCase() + material.getUnlocName().substring(1);
         matNames = matNames + (matNames == "" ? "" : ", ") + name;
         ItemSwordBase dagger = new ItemDagger("dagger_" + material.getUnlocName(), "spartanweaponry", material);
         ItemSwordBase longsword = new ItemLongsword("longsword_" + material.getUnlocName(), "spartanweaponry", material);
         ItemSwordBase katana = new ItemKatana("katana_" + material.getUnlocName(), "spartanweaponry", material);
         ItemSwordBase saber = new ItemSaber("saber_" + material.getUnlocName(), "spartanweaponry", material);
         ItemSwordBase rapier = new ItemRapier("rapier_" + material.getUnlocName(), "spartanweaponry", material);
         ItemSwordBase greatsword = new ItemGreatsword("greatsword_" + material.getUnlocName(), "spartanweaponry", material);
         ItemSwordBase hammer = new ItemHammer("hammer_" + material.getUnlocName(), "spartanweaponry", material);
         ItemSwordBase warhammer = new ItemWarhammer("warhammer_" + material.getUnlocName(), "spartanweaponry", material);
         ItemSwordBase spear = new ItemSpear("spear_" + material.getUnlocName(), "spartanweaponry", material);
         ItemSwordBase halberd = new ItemHalberd("halberd_" + material.getUnlocName(), "spartanweaponry", material);
         ItemSwordBase pike = new ItemPike("pike_" + material.getUnlocName(), "spartanweaponry", material);
         ItemSwordBase lance = new ItemLance("lance_" + material.getUnlocName(), "spartanweaponry", material);
         ItemLongbow longbow = new ItemLongbow("longbow_" + material.getUnlocName(), "spartanweaponry", material);
         ItemCrossbow crossbow = new ItemCrossbow("crossbow_" + material.getUnlocName(), "spartanweaponry", material);
         ItemThrowingWeapon throwingKnife = new ItemThrowingKnife("throwing_knife_" + material.getUnlocName(), "spartanweaponry", material);
         ItemThrowingWeapon throwingAxe = new ItemThrowingAxe("throwing_axe_" + material.getUnlocName(), "spartanweaponry", material);
         ItemThrowingWeapon javelin = new ItemJavelin("javelin_" + material.getUnlocName(), "spartanweaponry", material);
         ItemThrowingWeapon boomerang = new ItemBoomerang("boomerang_" + material.getUnlocName(), "spartanweaponry", material);
         ItemSwordBase battleaxe = new ItemBattleaxe("battleaxe_" + material.getUnlocName(), "spartanweaponry", material);
         ItemSwordBase mace = new ItemMace("mace_" + material.getUnlocName(), "spartanweaponry", material);
         ItemSwordBase glaive = new ItemGlaive("glaive_" + material.getUnlocName(), "spartanweaponry", material);
         ItemSwordBase quarterstaff = new ItemQuarterstaff("staff_" + material.getUnlocName(), "spartanweaponry", material);
         ItemSwordBase parryingDagger = new ItemParryingDagger("parrying_dagger_" + material.getUnlocName(), "spartanweaponry", material);
         if (!ConfigHandler.disableDagger) {
            customWeapons.add(dagger);
            ModelRenderRegistry.addItemToRegistry(dagger, dagger.getRegistryName(), material);
         }

         if (!ConfigHandler.disableLongsword) {
            customWeapons.add(longsword);
            ModelRenderRegistry.addItemToRegistry(longsword, longsword.getRegistryName(), material);
         }

         if (!ConfigHandler.disableKatana) {
            customWeapons.add(katana);
            ModelRenderRegistry.addItemToRegistry(katana, katana.getRegistryName(), material);
         }

         if (!ConfigHandler.disableSaber) {
            customWeapons.add(saber);
            ModelRenderRegistry.addItemToRegistry(saber, saber.getRegistryName(), material);
         }

         if (!ConfigHandler.disableRapier) {
            customWeapons.add(rapier);
            ModelRenderRegistry.addItemToRegistry(rapier, rapier.getRegistryName(), material);
         }

         if (!ConfigHandler.disableGreatsword) {
            customWeapons.add(greatsword);
            ModelRenderRegistry.addItemToRegistry(greatsword, greatsword.getRegistryName(), material);
         }

         if (!ConfigHandler.disableHammer) {
            customWeapons.add(hammer);
            ModelRenderRegistry.addItemToRegistry(hammer, hammer.getRegistryName(), material);
         }

         if (!ConfigHandler.disableWarhammer) {
            customWeapons.add(warhammer);
            ModelRenderRegistry.addItemToRegistry(warhammer, warhammer.getRegistryName(), material);
         }

         if (!ConfigHandler.disableSpear) {
            customWeapons.add(spear);
            ModelRenderRegistry.addItemToRegistry(spear, spear.getRegistryName(), material);
         }

         if (!ConfigHandler.disableHalberd) {
            customWeapons.add(halberd);
            ModelRenderRegistry.addItemToRegistry(halberd, halberd.getRegistryName(), material);
         }

         if (!ConfigHandler.disablePike) {
            customWeapons.add(pike);
            ModelRenderRegistry.addItemToRegistry(pike, pike.getRegistryName(), material);
         }

         if (!ConfigHandler.disableLance) {
            customWeapons.add(lance);
            ModelRenderRegistry.addItemToRegistry(lance, lance.getRegistryName(), material);
         }

         if (!ConfigHandler.disableLongbow && !ConfigHandler.woodenLongbowOnly) {
            customWeapons.add(longbow);
            ModelRenderRegistry.addItemToRegistry(longbow, longbow.getRegistryName(), material);
         }

         if (!ConfigHandler.disableCrossbow && !ConfigHandler.woodenCrossbowOnly) {
            customWeapons.add(crossbow);
            ModelRenderRegistry.addItemToRegistry(crossbow, crossbow.getRegistryName(), material);
         }

         if (!ConfigHandler.disableThrowingKnife) {
            customWeapons.add(throwingKnife);
            ModelRenderRegistry.addItemToRegistry(throwingKnife, throwingKnife.getRegistryName(), material);
         }

         if (!ConfigHandler.disableThrowingAxe) {
            customWeapons.add(throwingAxe);
            ModelRenderRegistry.addItemToRegistry(throwingAxe, throwingAxe.getRegistryName(), material);
         }

         if (!ConfigHandler.disableJavelin) {
            customWeapons.add(javelin);
            ModelRenderRegistry.addItemToRegistry(javelin, javelin.getRegistryName(), material);
         }

         if (!ConfigHandler.disableBoomerang && !ConfigHandler.woodenBoomerangOnly) {
            customWeapons.add(boomerang);
            ModelRenderRegistry.addItemToRegistry(boomerang, boomerang.getRegistryName(), material);
         }

         if (!ConfigHandler.disableBattleaxe) {
            customWeapons.add(battleaxe);
            ModelRenderRegistry.addItemToRegistry(battleaxe, battleaxe.getRegistryName(), material);
         }

         if (!ConfigHandler.disableMace) {
            customWeapons.add(mace);
            ModelRenderRegistry.addItemToRegistry(mace, mace.getRegistryName(), material);
         }

         if (!ConfigHandler.disableGlaive) {
            customWeapons.add(glaive);
            ModelRenderRegistry.addItemToRegistry(glaive, glaive.getRegistryName(), material);
         }

         if (!ConfigHandler.disableQuarterstaff) {
            customWeapons.add(quarterstaff);
            ModelRenderRegistry.addItemToRegistry(quarterstaff, quarterstaff.getRegistryName(), material);
         }

         if (!ConfigHandler.disableParryingDagger) {
            customWeapons.add(parryingDagger);
            ModelRenderRegistry.addItemToRegistry(parryingDagger, parryingDagger.getRegistryName(), material);
         }
      }

      Log.info("Adding weapons for materials: " + matNames);

      for(Item weapon : customWeapons) {
         weapon.setCreativeTab(CreativeTabsSW.TAB_SW_MOD);
         registry.register(weapon);
      }

      customWeapons.clear();
   }
}
