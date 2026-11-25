package com.teamacronymcoders.contenttweaker.api.ctobjects.mutableitemstack;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.contenttweaker.MutableItemStack")
public interface IMutableItemStack extends IItemStack {
   @ZenMethod
   void setCount(int var1);

   @ZenMethod
   void shrink(int var1);

   @ZenMethod
   void grow(int var1);

   @ZenMethod
   void damage(int var1, IEntityLivingBase var2);
}
