package com.teamacronymcoders.contenttweaker.modules.vanilla.functions;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.IBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate.ICTBlockState;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.IWorld;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("mods.contenttweaker.IBlockAction")
public interface IBlockAction {
   void onBlockAction(IWorld var1, IBlockPos var2, ICTBlockState var3);
}
