package com.blamejared.compat.tconstruct;

import com.blamejared.ModTweaker;
import com.blamejared.compat.tconstruct.recipes.MeltingRecipeTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.events.TinkerRegisterEvent.EntityMeltingRegisterEvent;
import slimeknights.tconstruct.library.events.TinkerRegisterEvent.MeltingRegisterEvent;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.tconstruct.Melting")
@ZenRegister
@ModOnly("tconstruct")
public class Melting {
   public static final Multimap<ILiquidStack, IItemStack> REMOVED_RECIPES = LinkedListMultimap.create();
   public static final List<EntityEntry> REMOVED_ENTITIES = new LinkedList();
   public static final Map<EntityEntry, FluidStack> ADDED_ENTITIES = new LinkedHashMap();
   private static boolean init = false;

   public Melting() {
   }

   private static void init() {
      if (!init) {
         MinecraftForge.EVENT_BUS.register(new Melting());
         init = true;
      }

   }

   @ZenMethod
   public static void addRecipe(ILiquidStack output, IIngredient input, @Optional int temp) {
      init();
      ModTweaker.LATE_ADDITIONS.add(new Melting.Add(InputHelper.toFluid(output), input, temp));
   }

   @ZenMethod
   public static void addEntityMelting(IEntityDefinition entity, ILiquidStack stack) {
      init();
      ModTweaker.LATE_ADDITIONS.add(new Melting.AddEntityMelting((EntityEntry)entity.getInternal(), InputHelper.toFluid(stack)));
   }

   @ZenMethod
   public static void removeRecipe(ILiquidStack output, @Optional IItemStack input) {
      init();
      CraftTweakerAPI.apply(new Melting.Remove(output, input));
   }

   @ZenMethod
   public static void removeEntityMelting(IEntityDefinition entity) {
      init();
      CraftTweakerAPI.apply(new Melting.RemoveEntityMelting((EntityEntry)entity.getInternal()));
   }

   @SubscribeEvent
   public void onTinkerRegister(MeltingRegisterEvent event) {
      if (!(event.getRecipe() instanceof MeltingRecipeTweaker)) {
         for(Entry<ILiquidStack, IItemStack> ent : REMOVED_RECIPES.entries()) {
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

   @SubscribeEvent
   public void onTinkerRegister(EntityMeltingRegisterEvent event) {
      boolean remove = true;

      for(Entry<EntityEntry, FluidStack> entry : ADDED_ENTITIES.entrySet()) {
         if (((Class)event.getRecipe()).equals(((EntityEntry)entry.getKey()).getEntityClass())) {
            remove = false;
            event.setNewFluidStack((FluidStack)entry.getValue());
         }
      }

      if (remove) {
         for(EntityEntry entity : REMOVED_ENTITIES) {
            if (entity.getEntityClass().equals(event.getRecipe())) {
               event.setCanceled(true);
            }
         }
      }

   }

   private static class Add extends BaseAction {
      private final FluidStack output;
      private final IIngredient ingredient;
      private final int temp;

      public Add(FluidStack output, IIngredient ingredient, int temp) {
         super("Melting");
         this.output = output;
         this.temp = temp;
         this.ingredient = ingredient;
      }

      public void apply() {
         List<ItemStack> validIngredients = (List)this.ingredient.getItems().stream().map(CraftTweakerMC::getItemStack).collect(Collectors.toList());
         if (validIngredients.isEmpty()) {
            CraftTweakerAPI.logInfo("Could not find matching items for " + this.ingredient.toString() + ". Ignoring Melting recipe for " + this.output.getLocalizedName());
         } else {
            RecipeMatch rm;
            if (validIngredients.size() == 1 && ((ItemStack)validIngredients.get(0)).getMetadata() != 32767) {
               rm = RecipeMatch.ofNBT((ItemStack)validIngredients.get(0), this.output.amount);
            } else {
               rm = RecipeMatch.of(validIngredients, this.output.amount);
            }

            if (this.temp == 0) {
               TinkerRegistry.registerMelting(new MeltingRecipeTweaker(rm, this.output));
            } else {
               TinkerRegistry.registerMelting(new MeltingRecipeTweaker(rm, this.output, this.temp));
            }

         }
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class AddEntityMelting extends BaseAction {
      private EntityEntry entity;
      private FluidStack output;

      public AddEntityMelting(EntityEntry entity, FluidStack input) {
         super("EntityMelting");
         this.entity = entity;
         this.output = input;
      }

      public void apply() {
         TinkerRegistry.registerEntityMelting(this.entity.getEntityClass(), this.output);
         Melting.ADDED_ENTITIES.put(this.entity, this.output);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class Remove extends BaseAction {
      private ILiquidStack output;
      private IItemStack input;

      protected Remove(ILiquidStack output, IItemStack input) {
         super("Melting");
         this.output = output;
         this.input = input;
      }

      public void apply() {
         Melting.REMOVED_RECIPES.put(this.output, this.input);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class RemoveEntityMelting extends BaseAction {
      private EntityEntry entity;

      protected RemoveEntityMelting(EntityEntry input) {
         super("EntityMelting");
         this.entity = input;
      }

      public void apply() {
         Melting.REMOVED_ENTITIES.add(this.entity);
      }

      protected String getRecipeInfo() {
         return this.entity == null ? "null" : this.entity.getName();
      }
   }
}
