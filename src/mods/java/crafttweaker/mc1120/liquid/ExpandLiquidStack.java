package crafttweaker.mc1120.liquid;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.liquid.WeightedLiquidStack;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenRegister
@ZenExpansion("crafttweaker.liquid.ILiquidStack")
public class ExpandLiquidStack {
   public ExpandLiquidStack() {
   }

   @ZenOperator(OperatorType.MOD)
   public static WeightedLiquidStack percent(ILiquidStack thisStack, float p) {
      return new WeightedLiquidStack(thisStack, p * 0.01F);
   }

   @ZenMethod
   public static WeightedLiquidStack weight(ILiquidStack thisStack, float p) {
      return new WeightedLiquidStack(thisStack, p);
   }

   @ZenCaster
   public static WeightedLiquidStack asWeightedLiquidStack(ILiquidStack thisStack) {
      return new WeightedLiquidStack(thisStack, 1.0F);
   }
}
