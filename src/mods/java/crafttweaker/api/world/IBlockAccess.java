package crafttweaker.api.world;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.world.IBlockAccess")
@ZenRegister
public interface IBlockAccess {
   @ZenMethod
   IBlockState getBlockState(IBlockPos var1);

   @ZenMethod
   boolean isAirBlock(IBlockPos var1);

   @ZenMethod
   int getStrongPower(IBlockPos var1, IFacing var2);

   Object getInternal();
}
