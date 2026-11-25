package com.blamejared.compat.tcomplement.highoven;

import com.blamejared.ModTweaker;
import com.blamejared.compat.mantle.RecipeMatchIIngredient;
import com.blamejared.compat.tcomplement.highoven.recipes.HeatRecipeTweaker;
import com.blamejared.compat.tcomplement.highoven.recipes.HighOvenFuelTweaker;
import com.blamejared.compat.tcomplement.highoven.recipes.MixRecipeTweaker;
import com.blamejared.compat.tconstruct.recipes.MeltingRecipeTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.mc1120.item.MCItemStack;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.events.TCompRegisterEvent.HighOvenFuelRegisterEvent;
import knightminer.tcomplement.library.events.TCompRegisterEvent.HighOvenHeatRegisterEvent;
import knightminer.tcomplement.library.events.TCompRegisterEvent.HighOvenMixRegisterEvent;
import knightminer.tcomplement.library.events.TCompRegisterEvent.HighOvenOverrideRegisterEvent;
import knightminer.tcomplement.library.steelworks.HighOvenFuel;
import knightminer.tcomplement.library.steelworks.IHeatRecipe;
import knightminer.tcomplement.library.steelworks.IMixRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.util.Pair;

@ZenClass("mods.tcomplement.highoven.HighOven")
@ZenRegister
@ModOnly("tcomplement")
public class HighOven {
   public static final List<IIngredient> REMOVED_FUELS = new LinkedList();
   public static final Map<ILiquidStack, IItemStack> REMOVED_OVERRIDES = new LinkedHashMap();
   public static final List<Pair<FluidStack, FluidStack>> REMOVED_HEAT_RECIPES = new LinkedList();
   public static final List<Pair<FluidStack, FluidStack>> REMOVED_MIX_RECIPES = new LinkedList();
   private static boolean init = false;

   public HighOven() {
   }

   private static void init() {
      if (!init) {
         MinecraftForge.EVENT_BUS.register(new HighOven());
         init = true;
      }

   }

   @ZenMethod
   public static void removeFuel(IIngredient stack) {
      init();
      CraftTweakerAPI.apply(new HighOven.RemoveFuel(stack));
   }

   @ZenMethod
   public static void addFuel(IIngredient fuel, int burnTime, int tempRate) {
      init();
      ModTweaker.LATE_ADDITIONS.add(new HighOven.AddFuel(fuel, burnTime, tempRate));
   }

   @ZenMethod
   public static void addMeltingOverride(ILiquidStack output, IIngredient input, @Optional int temp) {
      init();
      ModTweaker.LATE_ADDITIONS.add(new HighOven.AddMelting(InputHelper.toFluid(output), input, temp == 0 ? -1 : temp));
   }

   @ZenMethod
   public static void removeMeltingOverride(ILiquidStack output, @Optional IItemStack input) {
      init();
      CraftTweakerAPI.apply(new HighOven.RemoveMelting(output, input));
   }

   @ZenMethod
   public static void removeHeatRecipe(ILiquidStack output, @Optional ILiquidStack input) {
      init();
      CraftTweakerAPI.apply(new HighOven.RemoveHeat(input, output));
   }

   @ZenMethod
   public static void addHeatRecipe(ILiquidStack output, ILiquidStack input, int temp) {
      init();
      ModTweaker.LATE_ADDITIONS.add(new HighOven.AddHeat(InputHelper.toFluid(output), InputHelper.toFluid(input), temp));
   }

   @ZenMethod
   public static void removeMixRecipe(ILiquidStack output, @Optional ILiquidStack input) {
      init();
      CraftTweakerAPI.apply(new HighOven.RemoveMix(output, input));
   }

   @ZenMethod
   public static MixRecipeBuilder newMixRecipe(ILiquidStack output, ILiquidStack input, int temp) {
      init();
      return new MixRecipeBuilder(output, input, temp);
   }

   @ZenMethod
   public static MixRecipeManager manageMixRecipe(ILiquidStack output, ILiquidStack input) {
      init();
      return new MixRecipeManager(output, input);
   }

   @SubscribeEvent
   public void onHighOvenFuelRegister(HighOvenFuelRegisterEvent event) {
      if (!(event.getRecipe() instanceof HighOvenFuelTweaker)) {
         label25:
         for(IIngredient entry : REMOVED_FUELS) {
            Iterator var4 = ((HighOvenFuel)event.getRecipe()).getFuels().iterator();

            while(true) {
               if (!var4.hasNext()) {
                  continue label25;
               }

               ItemStack fuel = (ItemStack)var4.next();
               if (entry.matches(new MCItemStack(fuel))) {
                  break;
               }
            }

            event.setCanceled(true);
            return;
         }

      }
   }

   @SubscribeEvent
   public void onHighOvenHeatRegister(HighOvenHeatRegisterEvent event) {
      if (!(event.getRecipe() instanceof HeatRecipeTweaker)) {
         for(Pair<FluidStack, FluidStack> entry : REMOVED_HEAT_RECIPES) {
            if (((IHeatRecipe)event.getRecipe()).matches((FluidStack)entry.getKey(), (FluidStack)entry.getValue())) {
               event.setCanceled(true);
               return;
            }
         }

      }
   }

   @SubscribeEvent
   public void onHighOvenMixRegister(HighOvenMixRegisterEvent event) {
      if (!(event.getRecipe() instanceof MixRecipeTweaker)) {
         for(Pair<FluidStack, FluidStack> entry : REMOVED_MIX_RECIPES) {
            if (((IMixRecipe)event.getRecipe()).matches((FluidStack)entry.getKey(), (FluidStack)entry.getValue())) {
               event.setCanceled(true);
               return;
            }
         }

      }
   }

   @SubscribeEvent
   public void onHighOvenOverrideRegister(HighOvenOverrideRegisterEvent event) {
      if (!(event.getRecipe() instanceof MeltingRecipeTweaker)) {
         for(Entry<ILiquidStack, IItemStack> entry : REMOVED_OVERRIDES.entrySet()) {
            if (((MeltingRecipe)event.getRecipe()).getResult().isFluidEqual((FluidStack)((ILiquidStack)entry.getKey()).getInternal())) {
               if (entry.getValue() != null) {
                  if (((MeltingRecipe)event.getRecipe()).input.matches(NonNullList.withSize(1, (ItemStack)((IItemStack)entry.getValue()).getInternal())).isPresent()) {
                     event.setCanceled(true);
                  }
               } else {
                  event.setCanceled(true);
               }
            }
         }

      }
   }

   private static class AddFuel extends BaseAction {
      private IIngredient fuel;
      private int time;
      private int rate;

      public AddFuel(IIngredient fuel, int time, int rate) {
         super("High Oven fuel");
         this.fuel = fuel;
         this.time = time;
         this.rate = rate;
      }

      public void apply() {
         TCompRegistry.registerFuel(new HighOvenFuelTweaker(new RecipeMatchIIngredient(this.fuel), this.time, this.rate));
      }

      public String describe() {
         return String.format("Adding %s as %s", this.getRecipeInfo(), this.name);
      }

      public String getRecipeInfo() {
         return LogHelper.getStackDescription(this.fuel);
      }
   }

   private static class AddHeat extends BaseAction {
      private FluidStack output;
      private FluidStack input;
      private int temp;

      public AddHeat(FluidStack output, FluidStack input, int temp) {
         super("High Oven Heat");
         this.output = output;
         this.input = input;
         this.temp = temp;
      }

      public void apply() {
         TCompRegistry.registerHeatRecipe(new HeatRecipeTweaker(this.input, this.output, this.temp));
      }

      public String describe() {
         return String.format("Adding %s Recipe for %s", this.name, this.getRecipeInfo());
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class AddMelting extends BaseAction {
      private IIngredient input;
      private FluidStack output;
      private int temp;

      public AddMelting(FluidStack output, IIngredient input, int temp) {
         super("High Oven melting override");
         this.input = input;
         this.output = output;
         this.temp = temp;
      }

      public void apply() {
         if (this.temp > 0) {
            TCompRegistry.registerHighOvenOverride(new MeltingRecipeTweaker(new RecipeMatchIIngredient(this.input, this.output.amount), this.output, this.temp));
         } else {
            TCompRegistry.registerHighOvenOverride(new MeltingRecipeTweaker(new RecipeMatchIIngredient(this.input, this.output.amount), this.output));
         }

      }

      public String describe() {
         return String.format("Adding %s for %s", this.name, this.getRecipeInfo());
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input) + ", now yields " + LogHelper.getStackDescription(this.output);
      }
   }

   private static class RemoveFuel extends BaseAction {
      private IIngredient fuel;

      public RemoveFuel(IIngredient fuel) {
         super("High Oven fuel");
         this.fuel = fuel;
      }

      public void apply() {
         HighOven.REMOVED_FUELS.add(this.fuel);
      }

      public String describe() {
         return String.format("Removing %s as %s", this.getRecipeInfo(), this.name);
      }

      public String getRecipeInfo() {
         return LogHelper.getStackDescription(this.fuel);
      }
   }

   private static class RemoveHeat extends BaseAction {
      private ILiquidStack input;
      private ILiquidStack output;

      public RemoveHeat(ILiquidStack input, ILiquidStack output) {
         super("High Oven Heat");
         this.input = input;
         this.output = output;
      }

      public void apply() {
         HighOven.REMOVED_HEAT_RECIPES.add(new Pair(InputHelper.toFluid(this.input), InputHelper.toFluid(this.output)));
      }

      public String describe() {
         return String.format("Removing %s Recipe(s) for %s", this.name, this.getRecipeInfo());
      }

      public String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output) + (this.input == null ? "" : " from " + LogHelper.getStackDescription(this.input));
      }
   }

   private static class RemoveMelting extends BaseAction {
      private ILiquidStack output;
      private IItemStack input;

      public RemoveMelting(ILiquidStack output, IItemStack input) {
         super("High Oven melting override");
         this.input = input;
         this.output = output;
      }

      public void apply() {
         HighOven.REMOVED_OVERRIDES.put(this.output, this.input);
      }

      public String describe() {
         return String.format("Removing %s Recipe(s) for %s", this.name, this.getRecipeInfo());
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class RemoveMix extends BaseAction {
      private ILiquidStack output;
      private ILiquidStack input;

      public RemoveMix(ILiquidStack output, ILiquidStack input) {
         super("High Oven Mix");
         this.output = output;
         this.input = input;
      }

      public void apply() {
         HighOven.REMOVED_MIX_RECIPES.add(new Pair(InputHelper.toFluid(this.input), InputHelper.toFluid(this.output)));
      }

      public String describe() {
         return String.format("Removing %s Recipe(s) for %s", this.name, this.getRecipeInfo());
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output) + (this.input == null ? "" : " from " + LogHelper.getStackDescription(this.input));
      }
   }
}
