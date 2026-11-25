package crafttweaker.api.block;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import java.util.List;
import java.util.Map;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenClass("crafttweaker.block.IBlockState")
@ZenRegister
public interface IBlockState extends IBlockProperties, IBlockStateMatcher {
   @ZenMethod
   @ZenGetter("block")
   IBlock getBlock();

   @ZenMethod
   @ZenGetter("meta")
   int getMeta();

   @ZenMethod
   boolean isReplaceable(IWorld var1, IBlockPos var2);

   @ZenMethod
   @ZenOperator(OperatorType.COMPARE)
   int compare(IBlockState var1);

   @ZenMethod
   IBlockState withProperty(String var1, String var2);

   @ZenMethod
   List<String> getPropertyNames();

   @ZenMethod
   String getPropertyValue(String var1);

   @ZenMethod
   List<String> getAllowedValuesForProperty(String var1);

   @ZenMethod
   Map<String, String> getProperties();

   @ZenMethod
   IBlockStateMatcher matchBlock();
}
