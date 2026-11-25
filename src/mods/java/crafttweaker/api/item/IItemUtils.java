package crafttweaker.api.item;

import crafttweaker.api.data.IData;
import crafttweaker.api.enchantments.IEnchantment;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.potions.IPotionEffect;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenMethod;

public interface IItemUtils {
   @ZenMethod
   IItemStack createPotion(Object[]... var1);

   @ZenMethod
   IItemStack createPotion(IPotionEffect... var1);

   @ZenMethod
   IItemStack enchantItem(IItemStack var1, IEnchantment... var2);

   @ZenMethod
   IItemStack createEnchantedBook(IEnchantment... var1);

   @ZenMethod
   IData combineEnchantments(String var1, IEnchantment... var2);

   @ZenMethod
   IData combineEnchantments(IEnchantment... var1);

   @ZenMethod
   IItemStack[] getItemsByRegexRegistryName(String var1);

   @ZenMethod
   IItemStack getItem(String var1, @Optional int var2);

   @ZenMethod
   IItemStack[] getItemsByRegexUnlocalizedName(String var1);

   @ZenMethod
   IItemStack createSpawnEgg(IEntityDefinition var1, @Optional IData var2);
}
