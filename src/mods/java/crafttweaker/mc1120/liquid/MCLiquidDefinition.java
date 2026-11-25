package crafttweaker.mc1120.liquid;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.liquid.ILiquidDefinition;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.Objects;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class MCLiquidDefinition implements ILiquidDefinition {
   private final Fluid fluid;

   public MCLiquidDefinition(Fluid fluid) {
      this.fluid = fluid;
   }

   public String getName() {
      return this.fluid.getName();
   }

   public String getDisplayName() {
      return this.fluid.getLocalizedName(new FluidStack(this.fluid, 1000));
   }

   public ILiquidStack asStack(int millibuckets) {
      return new MCLiquidStack(new FluidStack(this.fluid, millibuckets));
   }

   public int getLuminosity() {
      return this.fluid.getLuminosity();
   }

   public void setLuminosity(int value) {
      CraftTweakerAPI.apply(new MCLiquidDefinition.ActionSetLuminosity(value));
   }

   public int getDensity() {
      return this.fluid.getDensity();
   }

   public void setDensity(int density) {
      CraftTweakerAPI.apply(new MCLiquidDefinition.ActionSetDensity(density));
   }

   public int getTemperature() {
      return this.fluid.getTemperature();
   }

   public void setTemperature(int temperature) {
      CraftTweakerAPI.apply(new MCLiquidDefinition.ActionSetTemperature(temperature));
   }

   public int getViscosity() {
      return this.fluid.getViscosity();
   }

   public void setViscosity(int viscosity) {
      CraftTweakerAPI.apply(new MCLiquidDefinition.ActionSetViscosity(viscosity));
   }

   public boolean isGaseous() {
      return this.fluid.isGaseous();
   }

   public void setGaseous(boolean gaseous) {
      CraftTweakerAPI.apply(new MCLiquidDefinition.ActionSetGaseous(gaseous));
   }

   public IBlock getBlock() {
      return CraftTweakerMC.getBlock(this.fluid.getBlock(), 0);
   }

   public Fluid getInternal() {
      return this.fluid;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCLiquidDefinition that = (MCLiquidDefinition)o;
         return Objects.equals(this.fluid, that.fluid);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.fluid});
   }

   private class ActionSetDensity implements IAction {
      private final int oldValue = MCLiquidDefinition.this.getDensity();
      private final int newValue;

      public ActionSetDensity(int newValue) {
         this.newValue = newValue;
      }

      public void apply() {
         MCLiquidDefinition.this.fluid.setDensity(this.newValue);
      }

      public String describe() {
         return "Setting " + MCLiquidDefinition.this.fluid.getName() + " density from " + this.oldValue + " to " + this.newValue;
      }
   }

   private class ActionSetGaseous implements IAction {
      private final boolean oldValue = MCLiquidDefinition.this.isGaseous();
      private final boolean newValue;

      public ActionSetGaseous(boolean newValue) {
         this.newValue = newValue;
      }

      public void apply() {
         MCLiquidDefinition.this.fluid.setGaseous(this.newValue);
      }

      public String describe() {
         return "Setting " + MCLiquidDefinition.this.fluid.getName() + " gaseous from " + this.oldValue + " to " + this.newValue;
      }
   }

   private class ActionSetLuminosity implements IAction {
      private final int oldValue = MCLiquidDefinition.this.getLuminosity();
      private final int newValue;

      public ActionSetLuminosity(int newValue) {
         this.newValue = newValue;
      }

      public void apply() {
         MCLiquidDefinition.this.fluid.setLuminosity(this.newValue);
      }

      public String describe() {
         return "Setting " + MCLiquidDefinition.this.fluid.getName() + " luminosity from " + this.oldValue + " to " + this.newValue;
      }
   }

   private class ActionSetTemperature implements IAction {
      private final int oldValue = MCLiquidDefinition.this.getTemperature();
      private final int newValue;

      public ActionSetTemperature(int newValue) {
         this.newValue = newValue;
      }

      public void apply() {
         MCLiquidDefinition.this.fluid.setTemperature(this.newValue);
      }

      public String describe() {
         return "Setting " + MCLiquidDefinition.this.fluid.getName() + " temperature from " + this.oldValue + " to " + this.newValue;
      }
   }

   private class ActionSetViscosity implements IAction {
      private final int oldValue = MCLiquidDefinition.this.getViscosity();
      private final int newValue;

      public ActionSetViscosity(int newValue) {
         this.newValue = newValue;
      }

      public void apply() {
         MCLiquidDefinition.this.fluid.setViscosity(this.newValue);
      }

      public String describe() {
         return "Setting " + MCLiquidDefinition.this.fluid.getName() + " viscosity from " + this.oldValue + " to " + this.newValue;
      }
   }
}
