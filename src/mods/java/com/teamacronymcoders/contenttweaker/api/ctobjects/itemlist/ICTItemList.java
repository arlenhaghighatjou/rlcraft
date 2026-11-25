package com.teamacronymcoders.contenttweaker.api.ctobjects.itemlist;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.WeightedItemStack;
import java.util.List;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenRegister
@ZenClass("mods.contenttweaker.ItemList")
public interface ICTItemList {
   @ZenMethod
   @ZenOperator(OperatorType.ADD)
   void add(IItemStack var1);

   @ZenMethod
   @ZenOperator(OperatorType.ADD)
   void add(WeightedItemStack var1);

   @ZenMethod
   void remove(int var1);

   @ZenMethod
   IItemStack get(int var1);

   @ZenMethod
   IItemStack[] getArray();

   @ZenMethod
   List<IItemStack> getList();

   @ZenMethod
   int getLength();

   @ZenMethod
   void clear();
}
