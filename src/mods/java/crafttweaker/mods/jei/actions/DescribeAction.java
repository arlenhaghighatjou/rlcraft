package crafttweaker.mods.jei.actions;

import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mods.jei.JEIAddonPlugin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import mezz.jei.api.ingredients.VanillaTypes;

public class DescribeAction implements IAction {
   private final List<IItemStack> itemStacks;
   private final List<ILiquidStack> fluidStacks;
   private final String[] description;
   private final String name;

   /** @deprecated */
   @Deprecated
   public DescribeAction(IItemStack stack, String[] description) {
      this(Collections.singletonList(stack), description, stack.toString());
   }

   public DescribeAction(List<? extends IIngredient> ingredients, String[] description, String name) {
      this.itemStacks = new ArrayList();
      this.fluidStacks = new ArrayList();
      ingredients.forEach((it) -> {
         List<ILiquidStack> liquids = it.getLiquids();
         if (liquids.isEmpty()) {
            this.itemStacks.addAll(it.getItems());
         } else {
            this.fluidStacks.addAll(liquids);
         }

      });
      this.description = description;
      this.name = name;
   }

   public void apply() {
      if (!this.itemStacks.isEmpty()) {
         JEIAddonPlugin.modRegistry.addIngredientInfo((List)this.itemStacks.stream().map(CraftTweakerMC::getItemStack).collect(Collectors.toList()), VanillaTypes.ITEM, this.description);
      }

      if (!this.fluidStacks.isEmpty()) {
         JEIAddonPlugin.modRegistry.addIngredientInfo((List)this.fluidStacks.stream().map(CraftTweakerMC::getLiquidStack).collect(Collectors.toList()), VanillaTypes.FLUID, this.description);
      }

   }

   public String describe() {
      return "Adding description in JEI for: " + this.name;
   }
}
