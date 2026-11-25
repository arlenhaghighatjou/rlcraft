package com.teamacronymcoders.contenttweaker.modules.vanilla.functions;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate.ICTBlockState;
import com.teamacronymcoders.contenttweaker.api.ctobjects.itemlist.ICTItemList;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IBlockAccess;
import crafttweaker.api.world.IBlockPos;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("mods.contenttweaker.DropHandler")
public interface IBlockDropHandler {
   void handleDrops(ICTItemList var1, IBlockAccess var2, IBlockPos var3, ICTBlockState var4, int var5);
}
