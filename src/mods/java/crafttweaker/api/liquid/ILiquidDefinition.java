package crafttweaker.api.liquid;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenOperator;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.liquid.ILiquidDefinition")
@ZenRegister
public interface ILiquidDefinition {
   @ZenOperator(OperatorType.MUL)
   ILiquidStack asStack(int var1);

   @ZenGetter("name")
   String getName();

   @ZenGetter("displayName")
   String getDisplayName();

   @ZenGetter("luminosity")
   int getLuminosity();

   @ZenSetter("luminosity")
   void setLuminosity(int var1);

   @ZenGetter("density")
   int getDensity();

   @ZenSetter("density")
   void setDensity(int var1);

   @ZenGetter("temperature")
   int getTemperature();

   @ZenSetter("temperature")
   void setTemperature(int var1);

   @ZenGetter("viscosity")
   int getViscosity();

   @ZenSetter("viscosity")
   void setViscosity(int var1);

   @ZenGetter("gaseous")
   boolean isGaseous();

   @ZenSetter("gaseous")
   void setGaseous(boolean var1);

   @ZenGetter("block")
   IBlock getBlock();

   Object getInternal();
}
