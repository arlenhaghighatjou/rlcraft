package com.blamejared.compat.tconstruct;

import com.blamejared.ModTweaker;
import com.blamejared.compat.tconstruct.recipes.DryingRecipeTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.DryingRecipe;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.events.TinkerRegisterEvent.DryingRackRegisterEvent;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.tconstruct.Drying")
@ZenRegister
@ModOnly("tconstruct")
public class Drying {
   public static final Map<IItemStack, IItemStack> DRYING_RECIPES = new LinkedHashMap();
   private static boolean init = false;

   public Drying() {
   }

   private static void init() {
      if (!init) {
         MinecraftForge.EVENT_BUS.register(new Drying());
         init = true;
      }

   }

   @ZenMethod
   public static void addRecipe(IItemStack output, IIngredient input, int time) {
      init();
      ModTweaker.LATE_ADDITIONS.add(new Drying.Add(InputHelper.toStack(output), input, time));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack output, @Optional IItemStack input) {
      init();
      CraftTweakerAPI.apply(new Drying.Remove(output, input));
   }

   @SubscribeEvent
   public void onTinkerRegister(DryingRackRegisterEvent event) {
      if (!(event.getRecipe() instanceof DryingRecipeTweaker)) {
         for(Entry<IItemStack, IItemStack> ent : DRYING_RECIPES.entrySet()) {
            if (((IItemStack)ent.getKey()).matches(InputHelper.toIItemStack(((DryingRecipe)event.getRecipe()).getResult()))) {
               if (ent.getValue() != null) {
                  if (((DryingRecipe)event.getRecipe()).input.matches(NonNullList.withSize(1, (ItemStack)((IItemStack)ent.getValue()).getInternal())).isPresent()) {
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
      private final ItemStack output;
      private final IIngredient ingredient;
      private final int time;

      public Add(ItemStack output, IIngredient input, int time) {
         super("Drying");
         this.output = output;
         this.ingredient = input;
         this.time = time;
      }

      public void apply() {
         List<ItemStack> validIngredients = (List)this.ingredient.getItems().stream().map(CraftTweakerMC::getItemStack).collect(Collectors.toList());
         if (validIngredients.isEmpty()) {
            CraftTweakerAPI.logInfo("Could not find matching items for " + this.ingredient.toString() + ". Ignoring " + this.name + " recipe for " + this.output.getDisplayName());
         } else {
            TinkerRegistry.addDryingRecipe(new DryingRecipeTweaker(RecipeMatch.of(validIngredients), this.output, this.time));
         }

      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class Remove extends BaseAction {
      private IItemStack output;
      private IItemStack input;

      protected Remove(IItemStack output, IItemStack input) {
         super("Drying");
         this.output = output;
         this.input = input;
      }

      public void apply() {
         Drying.DRYING_RECIPES.put(this.output, this.input);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }
}
