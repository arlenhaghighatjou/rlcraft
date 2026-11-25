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
import java.util.LinkedList;
import java.util.List;
import knightminer.tcomplement.library.IBlacklist;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.events.TCompRegisterEvent.MelterBlackListRegisterEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.mantle.util.RecipeMatch;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.tcomplement.Blacklist")
@ZenRegister
@ModOnly("tcomplement")
public class Blacklist {
   public static final List<IItemStack> REMOVED_RECIPES = new LinkedList();
   private static boolean init = false;

   public Blacklist() {
   }

   private static void init() {
      if (!init) {
         MinecraftForge.EVENT_BUS.register(new Blacklist());
         init = true;
      }

   }

   @ZenMethod
   public static void addRecipe(ILiquidStack output, IItemStack input) {
      init();
      ModTweaker.LATE_ADDITIONS.add(new Blacklist.Add(InputHelper.toFluid(output), InputHelper.toStack(input)));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack input) {
      init();
      CraftTweakerAPI.apply(new Blacklist.Remove(input));
   }

   @SubscribeEvent
   public void onTinkerRegister(MelterBlackListRegisterEvent event) {
      if (!(event.getRecipe() instanceof MeltingRecipeTweaker)) {
         for(IItemStack ent : REMOVED_RECIPES) {
            if (((IBlacklist)event.getRecipe()).matches(InputHelper.toStack(ent))) {
               event.setCanceled(true);
            }
         }

      }
   }

   private static class Add extends BaseAction {
      private FluidStack output;
      private ItemStack input;

      public Add(FluidStack output, ItemStack input) {
         super("Blacklist");
         this.output = output;
         this.input = input;
      }

      public void apply() {
         TCompRegistry.registerMelterBlacklist(RecipeMatch.of(this.input, this.output.amount));
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class Remove extends BaseAction {
      private IItemStack input;

      protected Remove(IItemStack input) {
         super("Blacklist");
         this.input = input;
      }

      public void apply() {
         Blacklist.REMOVED_RECIPES.add(this.input);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input);
      }
   }
}
