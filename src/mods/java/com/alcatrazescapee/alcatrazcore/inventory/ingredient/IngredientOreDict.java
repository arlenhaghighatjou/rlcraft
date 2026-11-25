package com.alcatrazescapee.alcatrazcore.inventory.ingredient;

import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class IngredientOreDict implements IRecipeIngredient {
   private final String oreName;
   private final int amount;
   private List<ItemStack> stacks;

   IngredientOreDict(@Nonnull String oreName) {
      this(oreName, 1);
   }

   IngredientOreDict(@Nonnull String oreName, int amount) {
      this.oreName = oreName;
      this.amount = amount;
      this.stacks = null;
   }

   @Nonnull
   public String getName() {
      return this.oreName;
   }

   @Nonnull
   public List<ItemStack> getStacks() {
      if (this.stacks == null) {
         this.stacks = (List)OreDictionary.getOres(this.oreName, false).stream().map(ItemStack::copy).peek((x) -> {
            x.setCount(this.amount);
         }).collect(Collectors.toList());
      }

      return this.stacks;
   }

   public boolean test(Object obj) {
      return this.testIgnoreCount(obj) && ((ItemStack)obj).getCount() >= this.amount;
   }

   public boolean testIgnoreCount(Object obj) {
      return obj instanceof ItemStack && CoreHelpers.doesStackMatchOre((ItemStack)obj, this.oreName);
   }

   public boolean matches(IRecipeIngredient other) {
      return other instanceof IngredientOreDict && ((IngredientOreDict)other).oreName.equals(this.oreName);
   }
}
