package crafttweaker.api.enchantments;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.enchantments.IEnchantmentDefinition")
@ZenRegister
public interface IEnchantmentDefinition {
   @ZenGetter("id")
   int getID();

   @ZenGetter("registryName")
   String getRegistryName();

   @ZenGetter("name")
   String getName();

   @ZenSetter("name")
   void setName(String var1);

   @ZenMethod
   boolean canApply(IItemStack var1);

   @ZenMethod
   boolean canApplyAtEnchantmentTable(IItemStack var1);

   @ZenGetter("maxLevel")
   int getMaxLevel();

   @ZenGetter("minLevel")
   int getMinLevel();

   @ZenMethod
   int getMaxEnchantability(int var1);

   @ZenMethod
   int getMinEnchantability(int var1);

   @ZenMethod
   String getTranslatedName(int var1);

   @ZenGetter
   boolean isAllowedOnBooks();

   @ZenMethod
   boolean isCompatibleWith(IEnchantmentDefinition var1);

   @ZenGetter
   boolean isCurse();

   @ZenGetter
   boolean isTreasureEnchantment();

   @ZenOperator(OperatorType.MUL)
   @ZenMethod
   IEnchantment makeEnchantment(int var1);

   @ZenOperator(OperatorType.COMPARE)
   int compare(IEnchantmentDefinition var1);

   Object getInternal();
}
