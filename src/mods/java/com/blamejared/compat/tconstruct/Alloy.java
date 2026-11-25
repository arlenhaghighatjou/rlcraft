package com.blamejared.compat.tconstruct;

import com.blamejared.ModTweaker;
import com.blamejared.compat.tconstruct.recipes.AlloyRecipeTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.events.TinkerRegisterEvent.AlloyRegisterEvent;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.tconstruct.Alloy")
@ZenRegister
@ModOnly("tconstruct")
public class Alloy {
   public static final Map<ILiquidStack, List<ILiquidStack>> REMOVED_RECIPES = new LinkedHashMap();
   private static boolean init = false;

   public Alloy() {
   }

   private static void init() {
      if (!init) {
         MinecraftForge.EVENT_BUS.register(new Alloy());
         init = true;
      }

   }

   @ZenMethod
   public static void addRecipe(ILiquidStack output, ILiquidStack[] inputs) {
      init();
      ModTweaker.LATE_ADDITIONS.add(new Alloy.Add(InputHelper.toFluid(output), InputHelper.toFluids(inputs)));
   }

   @ZenMethod
   public static void removeRecipe(ILiquidStack output, @Optional ILiquidStack[] input) {
      init();
      List<ILiquidStack> in = new ArrayList();
      if (input != null && input.length != 0) {
         Collections.addAll(in, input);
      } else {
         in = null;
      }

      CraftTweakerAPI.apply(new Alloy.Remove(output, in));
   }

   @SubscribeEvent
   public void onTinkerRegister(AlloyRegisterEvent event) {
      if (!(event.getRecipe() instanceof AlloyRecipeTweaker)) {
         for(Entry<ILiquidStack, List<ILiquidStack>> entry : REMOVED_RECIPES.entrySet()) {
            if (((AlloyRecipe)event.getRecipe()).getResult().isFluidEqual((FluidStack)((ILiquidStack)entry.getKey()).getInternal())) {
               if (entry.getValue() != null) {
                  List<ILiquidStack> in = (List)entry.getValue();
                  List<FluidStack> rin = ((AlloyRecipe)event.getRecipe()).getFluids();
                  if (rin.size() == in.size()) {
                     boolean valid = true;

                     for(int i = 0; i < in.size(); ++i) {
                        ILiquidStack stack = (ILiquidStack)in.get(i);
                        FluidStack lStack = (FluidStack)rin.get(i);
                        if (!lStack.isFluidEqual((FluidStack)stack.getInternal())) {
                           valid = false;
                        }
                     }

                     if (valid) {
                        event.setCanceled(true);
                     }
                  }
               } else {
                  event.setCanceled(true);
               }
            }
         }

      }
   }

   private static class Add extends BaseAction {
      private FluidStack output;
      private FluidStack[] input;

      public Add(FluidStack output, FluidStack[] input) {
         super("Alloy");
         this.output = output;
         this.input = input;
      }

      public void apply() {
         TinkerRegistry.registerAlloy(new AlloyRecipeTweaker(this.output, this.input));
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class Remove extends BaseAction {
      private ILiquidStack output;
      private List<ILiquidStack> inputs;

      protected Remove(ILiquidStack output) {
         super("Alloy");
         this.output = output;
      }

      protected Remove(ILiquidStack output, List<ILiquidStack> inputs) {
         super("Alloy");
         this.output = output;
         this.inputs = inputs;
      }

      public void apply() {
         Alloy.REMOVED_RECIPES.put(this.output, this.inputs);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }
}
