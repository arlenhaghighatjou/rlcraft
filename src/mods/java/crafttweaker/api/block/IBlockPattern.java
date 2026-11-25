package crafttweaker.api.block;

import crafttweaker.annotations.ZenRegister;
import java.util.List;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenClass("crafttweaker.block.IBlockPattern")
@ZenRegister
public interface IBlockPattern {
   @ZenMethod("blocks")
   List<IBlock> getBlocks();

   @ZenOperator(OperatorType.CONTAINS)
   boolean matches(IBlock var1);

   @ZenOperator(OperatorType.OR)
   IBlockPattern or(IBlockPattern var1);

   @ZenGetter("displayName")
   String getDisplayName();
}
