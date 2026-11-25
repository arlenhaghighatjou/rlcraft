package slimeknights.mantle.util;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class TradeGeneric implements ITradeList {
   private ItemStack input;
   private ItemStack input2;
   private ItemStack output;
   private PriceInfo inputPrice;
   private PriceInfo input2Price;
   private PriceInfo outputPrice;

   public TradeGeneric(ItemStack input, PriceInfo inputPrice, ItemStack input2, PriceInfo input2Price, ItemStack output, PriceInfo outputPrice) {
      this.input = input;
      this.inputPrice = inputPrice;
      this.input2 = input2;
      this.input2Price = input2Price;
      this.output = output;
      this.outputPrice = outputPrice;
   }

   public TradeGeneric(ItemStack input, PriceInfo inputPrice, ItemStack output, PriceInfo outputPrice) {
      this(input, inputPrice, ItemStack.EMPTY, (PriceInfo)null, output, outputPrice);
   }

   public TradeGeneric(ItemStack input, PriceInfo inputPrice, PriceInfo outputPrice) {
      this(input, inputPrice, ItemStack.EMPTY, (PriceInfo)null, ItemStack.EMPTY, outputPrice);
   }

   public TradeGeneric(PriceInfo inputPrice, ItemStack output, PriceInfo outputPrice) {
      this(ItemStack.EMPTY, inputPrice, ItemStack.EMPTY, (PriceInfo)null, output, outputPrice);
   }

   @Nonnull
   protected ItemStack getInput(EaglercraftRandom random) {
      return this.input.isEmpty() ? new ItemStack(Items.EMERALD) : this.input.copy();
   }

   protected ItemStack getInput2(EaglercraftRandom random) {
      return this.input2.isEmpty() ? ItemStack.EMPTY : this.input2.copy();
   }

   @Nonnull
   protected ItemStack getOutput(EaglercraftRandom random) {
      return this.output.isEmpty() ? new ItemStack(Items.EMERALD) : this.output.copy();
   }

   public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, EaglercraftRandom random) {
      ItemStack input = this.getInput(random);
      ItemStack output = this.getOutput(random);
      int size = 1;
      if (this.inputPrice != null) {
         size = this.inputPrice.getPrice(random);
      }

      input.setCount(size);
      size = 1;
      if (this.outputPrice != null) {
         size = this.outputPrice.getPrice(random);
      }

      output.setCount(size);
      ItemStack input2 = this.getInput2(random);
      if (!input2.isEmpty()) {
         size = 1;
         if (this.input2Price != null) {
            size = this.input2Price.getPrice(random);
         }

         input2.setCount(size);
      }

      recipeList.add(new MerchantRecipe(input, input2, output));
   }
}
