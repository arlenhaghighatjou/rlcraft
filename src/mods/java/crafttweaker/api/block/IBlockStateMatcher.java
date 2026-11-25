package crafttweaker.api.block;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenClass("crafttweaker.block.IBlockStateMatcher")
@ZenRegister
public interface IBlockStateMatcher {
   @ZenMethod
   @ZenOperator(OperatorType.CONTAINS)
   boolean matches(IBlockState var1);

   @ZenOperator(OperatorType.OR)
   IBlockStateMatcher or(IBlockStateMatcher var1);

   @ZenMethod
   Collection<IBlockState> getMatchingBlockStates();

   /** @deprecated */
   @Deprecated
   @ZenMethod
   IBlockStateMatcher allowValuesForProperty(String var1, String... var2);

   @ZenMethod
   IBlockStateMatcher withMatchedValuesForProperty(String var1, String... var2);

   @ZenMethod
   List<String> getMatchedValuesForProperty(String var1);

   @ZenMethod
   Map<String, List<String>> getMatchedProperties();

   @ZenMethod
   boolean isCompound();

   @ZenGetter("commandString")
   default String toCommandString() {
      CraftTweakerAPI.logError(this.getClass() + " does not overwrite IBlockStateMatcher#toCommandString");
      return this.toString();
   }
}
