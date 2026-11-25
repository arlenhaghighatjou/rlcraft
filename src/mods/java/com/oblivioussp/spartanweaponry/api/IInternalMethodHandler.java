package com.oblivioussp.spartanweaponry.api;

import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public interface IInternalMethodHandler {
   /** @deprecated */
   @Deprecated
   default Item addDagger(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addDagger(material, modId, tab, properties);
   }

   Item addDagger(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   /** @deprecated */
   @Deprecated
   default Item addLongsword(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addLongsword(material, modId, tab, properties);
   }

   Item addLongsword(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   /** @deprecated */
   @Deprecated
   default Item addKatana(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addKatana(material, modId, tab, properties);
   }

   Item addKatana(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   /** @deprecated */
   @Deprecated
   default Item addSaber(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addSaber(material, modId, tab, properties);
   }

   Item addSaber(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   /** @deprecated */
   @Deprecated
   default Item addRapier(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addRapier(material, modId, tab, properties);
   }

   Item addRapier(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   /** @deprecated */
   @Deprecated
   default Item addGreatsword(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addGreatsword(material, modId, tab, properties);
   }

   Item addGreatsword(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   /** @deprecated */
   @Deprecated
   default Item addHammer(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addHammer(material, modId, tab, properties);
   }

   Item addHammer(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   /** @deprecated */
   @Deprecated
   default Item addWarhammer(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addWarhammer(material, modId, tab, properties);
   }

   Item addWarhammer(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   /** @deprecated */
   @Deprecated
   default Item addSpear(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addSpear(material, modId, tab, properties);
   }

   Item addSpear(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   /** @deprecated */
   @Deprecated
   default Item addHalberd(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addHalberd(material, modId, tab, properties);
   }

   Item addHalberd(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   /** @deprecated */
   @Deprecated
   default Item addPike(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addPike(material, modId, tab, properties);
   }

   Item addPike(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   /** @deprecated */
   @Deprecated
   default Item addLance(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addLance(material, modId, tab, properties);
   }

   Item addLance(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   Item addLongbow(ToolMaterialEx var1, String var2, CreativeTabs var3, IWeaponCallback var4);

   Item addCrossbow(ToolMaterialEx var1, String var2, CreativeTabs var3, IWeaponCallback var4);

   /** @deprecated */
   @Deprecated
   default Item addThrowingKnife(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addThrowingKnife(material, modId, tab, properties);
   }

   Item addThrowingKnife(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   /** @deprecated */
   @Deprecated
   default Item addThrowingAxe(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addThrowingAxe(material, modId, tab, properties);
   }

   Item addThrowingAxe(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   /** @deprecated */
   @Deprecated
   default Item addJavelin(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addJavelin(material, modId, tab, properties);
   }

   Item addJavelin(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   /** @deprecated */
   @Deprecated
   default Item addBoomerang(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addBoomerang(material, modId, tab, properties);
   }

   Item addBoomerang(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   /** @deprecated */
   @Deprecated
   default Item addBattleaxe(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addBattleaxe(material, modId, tab, properties);
   }

   Item addBattleaxe(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   /** @deprecated */
   @Deprecated
   default Item addMace(ToolMaterialEx material, String modId, float damage, CreativeTabs tab, WeaponProperty... properties) {
      return this.addMace(material, modId, tab, properties);
   }

   Item addMace(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   Item addGlaive(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   Item addQuarterstaff(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   Item addParryingDagger(ToolMaterialEx var1, String var2, CreativeTabs var3, WeaponProperty... var4);

   void registerColourHandler(Item var1, ToolMaterialEx var2);

   /** @deprecated */
   @Deprecated
   default void registerItemModelRender(Item item, String modId, String modelName) {
   }

   /** @deprecated */
   @Deprecated
   default void registerItemModelRender(Item item) {
   }

   void addItemModelToRegistry(Item var1);

   void addItemModelToRegistry(Item var1, ResourceLocation var2);

   void addItemModelToRegistry(Item var1, String var2, String var3);

   String translateString(String var1, String var2, String var3);

   String translateFormattedString(String var1, String var2, String var3, Object... var4);

   boolean hasTranslateKey(String var1, String var2, String var3);
}
