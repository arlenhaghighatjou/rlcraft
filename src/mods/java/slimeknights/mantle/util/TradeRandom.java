package slimeknights.mantle.util;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.item.ItemStack;

public class TradeRandom extends TradeGeneric {
   private ItemStack[] inputs;
   private ItemStack[] outputs;

   public TradeRandom(ItemStack[] inputs, PriceInfo inputPrice, ItemStack[] outputs, PriceInfo outputPrice) {
      super(ItemStack.EMPTY, inputPrice, ItemStack.EMPTY, outputPrice);
      this.inputs = inputs;
      this.outputs = outputs;
   }

   public TradeRandom(ItemStack[] inputs, PriceInfo inputPrice, PriceInfo outputPrice) {
      this(inputs, inputPrice, (ItemStack[])null, outputPrice);
   }

   public TradeRandom(PriceInfo inputPrice, ItemStack[] outputs, PriceInfo outputPrice) {
      this((ItemStack[])null, inputPrice, outputs, outputPrice);
   }

   @Nonnull
   protected ItemStack getOutput(EaglercraftRandom random) {
      if (this.outputs == null) {
         return super.getOutput(random);
      } else {
         int i = random.nextInt(this.outputs.length);
         return this.outputs[i].copy();
      }
   }

   @Nonnull
   protected ItemStack getInput(EaglercraftRandom random) {
      if (this.inputs == null) {
         return super.getInput(random);
      } else {
         int i = random.nextInt(this.inputs.length);
         return this.inputs[i].copy();
      }
   }
}
