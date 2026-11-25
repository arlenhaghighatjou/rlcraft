package com.blamejared.compat.tcomplement;

import com.blamejared.ModTweaker;
import com.blamejared.compat.tconstruct.recipes.MeltingRecipeTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.events.TCompRegisterEvent.MelterOverrideRegisterEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.tcomplement.Overrides")
@ZenRegister
@ModOnly("tcomplement")
public class Overrides {
   public static final Map<ILiquidStack, IItemStack> REMOVED_RECIPES = new LinkedHashMap();
   private static boolean init = false;

   public Overrides() {
   }

   private static void init() {
      if (!init) {
         MinecraftForge.EVENT_BUS.register(new Overrides());
         init = true;
      }

   }

   @ZenMethod
   public static void addRecipe(ILiquidStack output, IItemStack input, @Optional int temp) {
      init();
      ModTweaker.LATE_ADDITIONS.add(new Overrides.Add(InputHelper.toFluid(output), InputHelper.toStack(input), temp));
   }

   @ZenMethod
   public static void removeRecipe(ILiquidStack output, @Optional IItemStack input) {
      init();
      CraftTweakerAPI.apply(new Overrides.Remove(output, input));
   }

   @SubscribeEvent
   public void onTinkerRegister(MelterOverrideRegisterEvent event) {
      if (!(event.getRecipe() instanceof MeltingRecipeTweaker)) {
         for(Entry<ILiquidStack, IItemStack> ent : REMOVED_RECIPES.entrySet()) {
            if (((MeltingRecipe)event.getRecipe()).getResult().isFluidEqual((FluidStack)((ILiquidStack)ent.getKey()).getInternal())) {
               if (ent.getValue() != null) {
                  if (((MeltingRecipe)event.getRecipe()).input.matches(NonNullList.withSize(1, (ItemStack)((IItemStack)ent.getValue()).getInternal())).isPresent()) {
                     event.setCanceled(true);
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
      private ItemStack input;
      private int temp;

      public Add(FluidStack output, ItemStack input, int temp) {
         super("Overrides");
         this.output = output;
         this.input = input;
         this.temp = temp;
      }

      public void apply() {
         if (this.temp != 0) {
            TCompRegistry.registerMelterOverride(new MeltingRecipeTweaker(RecipeMatch.of(this.input, this.output.amount), this.output, this.temp));
         } else {
            TCompRegistry.registerMelterOverride(new MeltingRecipeTweaker(RecipeMatch.of(this.input, this.output.amount), this.output));
         }

      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class Remove extends BaseAction {
      private ILiquidStack output;
      private IItemStack input;

      protected Remove(ILiquidStack output, IItemStack input) {
         super("Overrides");
         this.output = output;
         this.input = input;
      }

      public void apply() {
         Overrides.REMOVED_RECIPES.put(this.output, this.input);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }
}
