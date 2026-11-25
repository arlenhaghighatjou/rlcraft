package com.blamejared.compat.tcomplement.highoven;

import com.blamejared.compat.mantle.RecipeMatchIIngredient;
import com.blamejared.mtlib.helpers.InputHelper;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.mc1120.item.MCItemStack;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.Consumer;
import knightminer.tcomplement.library.events.TCompRegisterEvent.HighOvenMixAdditiveEvent;
import knightminer.tcomplement.library.events.TCompRegisterEvent.HighOvenMixRegisterEvent;
import knightminer.tcomplement.library.steelworks.IMixRecipe;
import knightminer.tcomplement.library.steelworks.MixAdditive;
import knightminer.tcomplement.library.steelworks.MixRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.mantle.util.RecipeMatch;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.tcomplement.highoven.MixRecipeManager")
@ZenRegister
@ModOnly("tcomplement")
public class MixRecipeManager {
   private FluidStack input;
   private FluidStack output;
   private List<SimpleEntry<RecipeMatch, MixAdditive>> additives;
   private Map<MixAdditive, List<IIngredient>> removedAdditives;

   public MixRecipeManager(ILiquidStack output, ILiquidStack input) {
      this.input = InputHelper.toFluid(input);
      this.output = InputHelper.toFluid(output);
      this.additives = new LinkedList();
      this.removedAdditives = new HashMap(MixAdditive.values().length, 1.0F);

      for(MixAdditive type : MixAdditive.values()) {
         this.removedAdditives.put(type, new LinkedList());
      }

      MinecraftForge.EVENT_BUS.register(this);
   }

   @ZenMethod
   public MixRecipeManager addOxidizer(@NotNull IIngredient oxidizer, int consumeChance) {
      this.additives.add(new SimpleEntry(new RecipeMatchIIngredient(oxidizer, consumeChance), MixAdditive.OXIDIZER));
      return this;
   }

   @ZenMethod
   public MixRecipeManager addReducer(@NotNull IIngredient reducer, int consumeChance) {
      this.additives.add(new SimpleEntry(new RecipeMatchIIngredient(reducer, consumeChance), MixAdditive.REDUCER));
      return this;
   }

   @ZenMethod
   public MixRecipeManager addPurifier(@NotNull IIngredient purifier, int consumeChance) {
      this.additives.add(new SimpleEntry(new RecipeMatchIIngredient(purifier, consumeChance), MixAdditive.PURIFIER));
      return this;
   }

   @ZenMethod
   public MixRecipeManager removeOxidizer(IIngredient oxidizer) {
      if (oxidizer != null && this.removedAdditives.get(MixAdditive.OXIDIZER) != null) {
         ((List)this.removedAdditives.get(MixAdditive.OXIDIZER)).add(oxidizer);
      } else {
         this.removedAdditives.put(MixAdditive.OXIDIZER, (Object)null);
      }

      return this;
   }

   @ZenMethod
   public MixRecipeManager removeReducer(IIngredient reducer) {
      if (reducer != null && this.removedAdditives.get(MixAdditive.REDUCER) != null) {
         ((List)this.removedAdditives.get(MixAdditive.REDUCER)).add(reducer);
      } else {
         this.removedAdditives.put(MixAdditive.REDUCER, (Object)null);
      }

      return this;
   }

   @ZenMethod
   public MixRecipeManager removePurifier(IIngredient purifier) {
      if (purifier != null && this.removedAdditives.get(MixAdditive.PURIFIER) != null) {
         ((List)this.removedAdditives.get(MixAdditive.PURIFIER)).add(purifier);
      } else {
         this.removedAdditives.put(MixAdditive.PURIFIER, (Object)null);
      }

      return this;
   }

   @SubscribeEvent
   public void onHighOvenMixRegister(HighOvenMixRegisterEvent event) {
      if (((IMixRecipe)event.getRecipe()).matches(this.input, this.output)) {
         this.additives.forEach((entry) -> {
            ((IMixRecipe)event.getRecipe()).addAdditive((MixAdditive)entry.getValue(), (RecipeMatch)entry.getKey());
         });
      }

   }

   @SubscribeEvent
   public void onHighOvenAdditiveRegister(HighOvenMixAdditiveEvent event) {
      if (((MixRecipe)event.getRecipe()).matches(this.input, this.output)) {
         List<IIngredient> removals = (List)this.removedAdditives.get(event.getType());
         if (removals != null) {
            label26:
            for(IIngredient removal : removals) {
               Iterator var5 = event.getAdditive().getInputs().iterator();

               while(true) {
                  if (!var5.hasNext()) {
                     continue label26;
                  }

                  ItemStack additive = (ItemStack)var5.next();
                  if (removal.matches(new MCItemStack(additive))) {
                     break;
                  }
               }

               event.setCanceled(true);
               return;
            }
         } else {
            event.setCanceled(true);
         }
      }

   }
}
