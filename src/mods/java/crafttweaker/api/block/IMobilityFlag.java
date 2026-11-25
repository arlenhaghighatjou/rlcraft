package crafttweaker.api.block;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.block.IMobilityFlag")
@ZenRegister
public interface IMobilityFlag {
   @ZenMethod
   boolean matches(IMobilityFlag var1);

   Object getInternal();
}
