package crafttweaker.api.world;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenClass("crafttweaker.world.IFacing")
@ZenRegister
public interface IFacing {
   @ZenMethod
   @ZenGetter("opposite")
   IFacing opposite();

   @ZenGetter("name")
   @ZenMethod
   String getName();

   Object getInternal();

   @ZenOperator(OperatorType.COMPARE)
   @ZenMethod
   int compare(IFacing var1);

   @ZenMethod
   @ZenGetter("rotateY")
   IFacing rotateY();
}
