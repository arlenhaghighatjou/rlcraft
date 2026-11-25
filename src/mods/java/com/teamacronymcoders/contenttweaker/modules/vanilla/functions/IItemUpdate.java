package com.teamacronymcoders.contenttweaker.modules.vanilla.functions;

import com.teamacronymcoders.contenttweaker.api.ctobjects.mutableitemstack.IMutableItemStack;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.IWorld;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("mods.contenttweaker.IItemUpdate")
public interface IItemUpdate {
   void onItemUpdate(IMutableItemStack var1, IWorld var2, IEntity var3, int var4, boolean var5);
}
