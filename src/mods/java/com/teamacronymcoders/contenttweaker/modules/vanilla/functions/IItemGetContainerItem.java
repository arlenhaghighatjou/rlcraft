package com.teamacronymcoders.contenttweaker.modules.vanilla.functions;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import javax.annotation.Nullable;
import stanhebben.zenscript.annotations.ZenClass;

@ZenClass("mods.contenttweaker.IItemGetContainerItem")
@ZenRegister
public interface IItemGetContainerItem {
   @Nullable
   IItemStack getContainerItem(IItemStack var1);
}
