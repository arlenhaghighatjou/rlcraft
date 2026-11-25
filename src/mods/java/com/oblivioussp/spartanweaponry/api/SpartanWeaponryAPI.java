package com.oblivioussp.spartanweaponry.api;

import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class SpartanWeaponryAPI {
   public static final String ModID = "spartanweaponry";
   public static IInternalMethodHandler internalHandler = new DummyInternalMethodHandler();

   public SpartanWeaponryAPI() {
   }

   /** @deprecated */
   @Deprecated
   public static Item createDagger(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addDagger(material, modId, damage, tab, properties);
   }

   public static Item createDagger(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addDagger(material, modId, tab, properties);
   }

   /** @deprecated */
   @Deprecated
   public static Item createLongsword(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addLongsword(material, modId, damage, tab, properties);
   }

   public static Item createLongsword(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addLongsword(material, modId, tab, properties);
   }

   /** @deprecated */
   @Deprecated
   public static Item createKatana(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addKatana(material, modId, damage, tab, properties);
   }

   public static Item createKatana(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addKatana(material, modId, tab, properties);
   }

   /** @deprecated */
   @Deprecated
   public static Item createSaber(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addSaber(material, modId, damage, tab, properties);
   }

   public static Item createSaber(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addSaber(material, modId, tab, properties);
   }

   /** @deprecated */
   @Deprecated
   public static Item createRapier(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addRapier(material, modId, damage, tab, properties);
   }

   public static Item createRapier(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addRapier(material, modId, tab, properties);
   }

   /** @deprecated */
   @Deprecated
   public static Item createGreatsword(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addGreatsword(material, modId, damage, tab, properties);
   }

   public static Item createGreatsword(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addGreatsword(material, modId, tab, properties);
   }

   /** @deprecated */
   @Deprecated
   public static Item createHammer(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addHammer(material, modId, damage, tab, properties);
   }

   public static Item createHammer(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addHammer(material, modId, tab, properties);
   }

   /** @deprecated */
   @Deprecated
   public static Item createWarhammer(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addWarhammer(material, modId, damage, tab, properties);
   }

   public static Item createWarhammer(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addWarhammer(material, modId, tab, properties);
   }

   /** @deprecated */
   @Deprecated
   public static Item createSpear(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addSpear(material, modId, damage, tab, properties);
   }

   public static Item createSpear(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addSpear(material, modId, tab, properties);
   }

   /** @deprecated */
   @Deprecated
   public static Item createHalberd(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addHalberd(material, modId, damage, tab, properties);
   }

   public static Item createHalberd(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addHalberd(material, modId, tab, properties);
   }

   /** @deprecated */
   @Deprecated
   public static Item createPike(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addPike(material, modId, damage, tab, properties);
   }

   public static Item createPike(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addPike(material, modId, tab, properties);
   }

   /** @deprecated */
   @Deprecated
   public static Item createLance(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addLance(material, modId, damage, tab, properties);
   }

   public static Item createLance(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addLance(material, modId, tab, properties);
   }

   public static Item createLongbow(ToolMaterialEx material, String modId, CreativeTabs tab, IWeaponCallback callback) {
      return internalHandler.addLongbow(material, modId, tab, callback);
   }

   public static Item createCrossbow(ToolMaterialEx material, String modId, CreativeTabs tab, IWeaponCallback callback) {
      return internalHandler.addCrossbow(material, modId, tab, callback);
   }

   /** @deprecated */
   @Deprecated
   public static Item createThrowingKnife(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addThrowingKnife(material, modId, damage, tab, properties);
   }

   public static Item createThrowingKnife(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addThrowingKnife(material, modId, tab, properties);
   }

   /** @deprecated */
   @Deprecated
   public static Item createThrowingAxe(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addThrowingAxe(material, modId, damage, tab, properties);
   }

   public static Item createThrowingAxe(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addThrowingAxe(material, modId, tab, properties);
   }

   /** @deprecated */
   @Deprecated
   public static Item createJavelin(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addJavelin(material, modId, damage, tab, properties);
   }

   public static Item createJavelin(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addJavelin(material, modId, tab, properties);
   }

   /** @deprecated */
   @Deprecated
   public static Item createBoomerang(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addBoomerang(material, modId, damage, tab, properties);
   }

   public static Item createBoomerang(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addBoomerang(material, modId, tab, properties);
   }

   /** @deprecated */
   @Deprecated
   public static Item createBattleaxe(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addBattleaxe(material, modId, damage, tab, properties);
   }

   public static Item createBattleaxe(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addBattleaxe(material, modId, tab, properties);
   }

   /** @deprecated */
   @Deprecated
   public static Item createMace(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addMace(material, modId, damage, tab, properties);
   }

   public static Item createMace(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addMace(material, modId, tab, properties);
   }

   public static Item createGlaive(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addGlaive(material, modId, tab, properties);
   }

   public static Item createQuarterstaff(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addQuarterstaff(material, modId, tab, properties);
   }

   public static Item createParryingDagger(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      return internalHandler.addParryingDagger(material, modId, tab, properties);
   }

   public static void registerColourHandler(ToolMaterialEx material, Item... items) {
      for(Item item : items) {
         internalHandler.registerColourHandler(item, material);
      }

   }

   /** @deprecated */
   @Deprecated
   public static void registerItemModelRender(Item item, String modId, String modelName) {
      internalHandler.registerItemModelRender(item, modId, modelName);
   }

   /** @deprecated */
   @Deprecated
   public static void registerItemModelRender(Item item) {
      internalHandler.registerItemModelRender(item);
   }

   public static void addItemModelToRegistry(Item item) {
      internalHandler.addItemModelToRegistry(item);
   }

   public static void addItemModelToRegistry(Item item, ResourceLocation modelLocation) {
      internalHandler.addItemModelToRegistry(item, modelLocation);
   }

   public static void addItemModelToRegistry(Item item, String modId, String modelLocation) {
      internalHandler.addItemModelToRegistry(item, modId, modelLocation);
   }
}
