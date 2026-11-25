package com.blamejared.compat.tconstruct;

import com.blamejared.ModTweaker;
import com.blamejared.compat.tconstruct.recipes.CastingRecipeTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.events.TinkerRegisterEvent.BasinCastingRegisterEvent;
import slimeknights.tconstruct.library.events.TinkerRegisterEvent.TableCastingRegisterEvent;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.tconstruct.Casting")
@ZenRegister
@ModOnly("tconstruct")
public class Casting {
   public static final Map<IItemStack, ILiquidStack> REMOVED_RECIPES_TABLE = new LinkedHashMap();
   public static final Map<IItemStack, ILiquidStack> REMOVED_RECIPES_BASIN = new LinkedHashMap();
   private static boolean init = false;

   public Casting() {
   }

   private static void init() {
      if (!init) {
         MinecraftForge.EVENT_BUS.register(new Casting());
         init = true;
      }

   }

   @ZenMethod
   public static void addTableRecipe(IItemStack output, IIngredient cast, ILiquidStack fluid, int amount, @Optional boolean consumeCast, @Optional int time) {
      init();
      ModTweaker.LATE_ADDITIONS.add(new Casting.Add(InputHelper.toStack(output), cast, InputHelper.toFluid(fluid), amount, true, consumeCast, time));
   }

   @ZenMethod
   public static void addBasinRecipe(IItemStack output, IIngredient cast, ILiquidStack fluid, int amount, @Optional boolean consumeCast, @Optional int time) {
      init();
      ModTweaker.LATE_ADDITIONS.add(new Casting.Add(InputHelper.toStack(output), cast, InputHelper.toFluid(fluid), amount, false, consumeCast, time));
   }

   @ZenMethod
   public static void removeTableRecipe(IItemStack output, @Optional ILiquidStack input) {
      init();
      CraftTweakerAPI.apply(new Casting.Remove(output, input, true));
   }

   @ZenMethod
   public static void removeBasinRecipe(IItemStack output, @Optional ILiquidStack input) {
      init();
      CraftTweakerAPI.apply(new Casting.Remove(output, input, false));
   }

   @SubscribeEvent
   public void onTinkerRegister(TableCastingRegisterEvent event) {
      if (!(event.getRecipe() instanceof CastingRecipeTweaker) && event.getRecipe() instanceof CastingRecipe) {
         for(Entry<IItemStack, ILiquidStack> ent : REMOVED_RECIPES_TABLE.entrySet()) {
            IItemStack stack = (IItemStack)ent.getKey();
            if (stack != null && ((CastingRecipe)event.getRecipe()).getResult() != null) {
               if (ent.getValue() == null) {
                  if (stack.matches(InputHelper.toIItemStack(((CastingRecipe)event.getRecipe()).getResult()))) {
                     event.setCanceled(true);
                  }
               } else if (stack.matches(InputHelper.toIItemStack(((CastingRecipe)event.getRecipe()).getResult())) && ((CastingRecipe)event.getRecipe()).getFluid().isFluidEqual((FluidStack)((ILiquidStack)ent.getValue()).getInternal())) {
                  event.setCanceled(true);
               }
            }
         }

      }
   }

   @SubscribeEvent
   public void onTinkerRegister(BasinCastingRegisterEvent event) {
      if (!(event.getRecipe() instanceof CastingRecipeTweaker) && event.getRecipe() instanceof CastingRecipe) {
         for(Entry<IItemStack, ILiquidStack> ent : REMOVED_RECIPES_BASIN.entrySet()) {
            IItemStack stack = (IItemStack)ent.getKey();
            if (stack != null && ((CastingRecipe)event.getRecipe()).getResult() != null && stack.matches(InputHelper.toIItemStack(((CastingRecipe)event.getRecipe()).getResult()))) {
               if (ent.getValue() != null) {
                  if (((CastingRecipe)event.getRecipe()).getFluid().isFluidEqual((FluidStack)((ILiquidStack)ent.getValue()).getInternal())) {
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
      private ItemStack output;
      IIngredient cast;
      private FluidStack fluid;
      private int amount;
      private boolean table;
      private boolean consumeCast;
      private int time;

      public Add(ItemStack output, IIngredient cast, FluidStack fluid, int amount, boolean table, boolean consumeCast, int time) {
         super("Casting");
         this.output = output;
         this.cast = cast;
         this.fluid = fluid;
         this.amount = amount;
         this.table = table;
         this.consumeCast = consumeCast;
         this.time = time != 0 ? time : CastingRecipe.calcCooldownTime(fluid.getFluid(), amount);
      }

      public void apply() {
         RecipeMatch rm = null;
         if (this.cast != null) {
            List<ItemStack> validCasts = (List)this.cast.getItems().stream().map(CraftTweakerMC::getItemStack).collect(Collectors.toList());
            if (validCasts.isEmpty()) {
               CraftTweakerAPI.logInfo("Could not find matching items for " + this.cast.toString() + ". Substituting empty cast for recipe with output " + this.output.getDisplayName());
            } else if (validCasts.size() == 1) {
               if (((ItemStack)validCasts.get(0)).getMetadata() == 32767) {
                  rm = RecipeMatch.of(validCasts, this.output.getCount());
               } else {
                  rm = RecipeMatch.ofNBT((ItemStack)validCasts.get(0), this.output.getCount());
               }
            } else {
               rm = RecipeMatch.of(validCasts, this.output.getCount());
            }
         }

         if (this.table) {
            TinkerRegistry.registerTableCasting(new CastingRecipeTweaker(this.output, rm, new FluidStack(this.fluid, this.amount), this.time, this.consumeCast, false));
         } else {
            TinkerRegistry.registerBasinCasting(new CastingRecipeTweaker(this.output, rm, new FluidStack(this.fluid, this.amount), this.time, this.consumeCast, false));
         }

      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class Remove extends BaseAction {
      private IItemStack output;
      private ILiquidStack input;
      private boolean table;

      protected Remove(IItemStack output, ILiquidStack input, boolean table) {
         super("Casting");
         this.output = output;
         this.input = input;
         this.table = table;
      }

      public void apply() {
         if (this.table) {
            Casting.REMOVED_RECIPES_TABLE.put(this.output, this.input);
         } else {
            Casting.REMOVED_RECIPES_BASIN.put(this.output, this.input);
         }

      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }
}
