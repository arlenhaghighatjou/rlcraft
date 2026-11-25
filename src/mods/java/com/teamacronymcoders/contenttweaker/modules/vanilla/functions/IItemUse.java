package com.teamacronymcoders.contenttweaker.modules.vanilla.functions;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.IBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.entity.player.ICTPlayer;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.ActionResult;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Facing;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Hand;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.IWorld;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.util.Position3f;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("mods.contenttweaker.IItemUse")
public interface IItemUse {
   ActionResult useItem(ICTPlayer var1, IWorld var2, IBlockPos var3, Hand var4, Facing var5, Position3f var6);
}
