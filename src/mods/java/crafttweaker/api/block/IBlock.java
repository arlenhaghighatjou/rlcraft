package crafttweaker.api.block;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.liquid.ILiquidDefinition;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenClass("crafttweaker.block.IBlock")
@ZenRegister
public interface IBlock extends IBlockPattern {
   @ZenGetter("definition")
   IBlockDefinition getDefinition();

   @ZenGetter("meta")
   int getMeta();

   @ZenGetter("data")
   IData getTileData();

   @ZenGetter("fluid")
   ILiquidDefinition getFluid();
}
