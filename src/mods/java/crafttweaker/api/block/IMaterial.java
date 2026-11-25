package crafttweaker.api.block;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.block.IMaterial")
@ZenRegister
public interface IMaterial {
   @ZenMethod
   @ZenGetter("blocksLight")
   boolean blocksLight();

   @ZenMethod
   @ZenGetter("blocksMovement")
   boolean blocksMovement();

   @ZenMethod
   @ZenGetter("canBurn")
   boolean getCanBurn();

   @ZenMethod
   @ZenGetter("mobilityFlag")
   IMobilityFlag getMobilityFlag();

   @ZenMethod
   @ZenGetter("liquid")
   boolean isLiquid();

   @ZenMethod
   @ZenGetter("opaque")
   boolean isOpaque();

   @ZenMethod
   @ZenGetter("replaceable")
   boolean isReplaceable();

   @ZenMethod
   @ZenGetter("solid")
   boolean isSolid();

   @ZenMethod
   @ZenGetter("toolNotRequired")
   boolean isToolNotRequired();

   @ZenMethod
   IMaterial setReplaceable();

   @ZenMethod
   boolean matches(IMaterial var1);

   Object getInternal();
}
