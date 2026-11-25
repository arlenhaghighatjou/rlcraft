package com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate;

import com.teamacronymcoders.contenttweaker.api.ICTObject;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.IBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Facing;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.PushReaction;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.IWorld;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import net.minecraft.block.state.IBlockState;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenRegister
@ZenClass("mods.contenttweaker.BlockState")
public interface ICTBlockState extends ICTObject<IBlockState> {
   @ZenMethod
   @ZenGetter("block")
   IBlock getBlock();

   @ZenMethod
   @ZenGetter("meta")
   int getMeta();

   @ZenMethod
   boolean isReplaceable(IWorld var1, IBlockPos var2);

   @ZenMethod
   int getLightValue(IWorld var1, IBlockPos var2);

   @ZenMethod
   boolean canProvidePower();

   @ZenMethod
   int getWeakPower(IWorld var1, IBlockPos var2, Facing var3);

   @ZenMethod
   int getComparatorInputOverride(IWorld var1, IBlockPos var2);

   @ZenMethod
   PushReaction getMobilityFlag();

   @ZenMethod
   @ZenOperator(OperatorType.COMPARE)
   int compare(ICTBlockState var1);
}
