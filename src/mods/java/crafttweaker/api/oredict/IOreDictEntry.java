package crafttweaker.api.oredict;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenClass("crafttweaker.oredict.IOreDictEntry")
@ZenRegister
public interface IOreDictEntry extends IIngredient {
   @ZenGetter("name")
   String getName();

   @ZenGetter("empty")
   boolean isEmpty();

   @ZenGetter("firstItem")
   IItemStack getFirstItem();

   @ZenMethod
   void add(IItemStack... var1);

   @ZenMethod
   void addItems(IItemStack[] var1);

   @ZenMethod
   void addAll(IOreDictEntry... var1);

   @ZenMethod
   void remove(IItemStack... var1);

   @ZenMethod
   void removeItems(IItemStack[] var1);

   @ZenOperator(OperatorType.CONTAINS)
   boolean contains(IItemStack var1);

   @ZenMethod
   void mirror(IOreDictEntry var1);
}
