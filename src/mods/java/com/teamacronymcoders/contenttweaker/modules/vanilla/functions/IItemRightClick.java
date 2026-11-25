package com.teamacronymcoders.contenttweaker.modules.vanilla.functions;

import com.teamacronymcoders.contenttweaker.api.ctobjects.entity.player.ICTPlayer;
import com.teamacronymcoders.contenttweaker.api.ctobjects.mutableitemstack.IMutableItemStack;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.IWorld;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("mods.contenttweaker.IItemRightClick")
public interface IItemRightClick {
   String onRightClick(IMutableItemStack var1, IWorld var2, ICTPlayer var3, String var4);
}
