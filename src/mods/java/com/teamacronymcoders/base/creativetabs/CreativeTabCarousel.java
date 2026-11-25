package com.teamacronymcoders.base.creativetabs;

import com.google.common.base.Stopwatch;
import com.teamacronymcoders.base.util.ItemStackUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CreativeTabCarousel extends CreativeTabBase {
   private ItemStack defaultIconStack = ItemStack.EMPTY;
   private List<ItemStack> iconStacks = new ArrayList();
   private int currentStackSize;
   private ItemStack currentIconStack;
   private Stopwatch watch = Stopwatch.createStarted();
   private Random random = new Random();

   public CreativeTabCarousel(String label) {
      super(label, (Supplier)null);
      this.updateIcon();
      this.setFunction(this::getCurrentIconStack);
   }

   private ItemStack getCurrentIconStack() {
      this.updateIcon();
      return this.currentIconStack;
   }

   private void updateIcon() {
      if (this.currentStackSize > 0) {
         if (this.watch.elapsed(TimeUnit.MILLISECONDS) >= 1500L) {
            this.watch.reset();
            this.watch.start();
            this.currentIconStack = (ItemStack)this.iconStacks.get(this.random.nextInt(this.iconStacks.size()));
         }
      } else {
         if (!ItemStackUtils.isValid(this.defaultIconStack)) {
            this.defaultIconStack = new ItemStack(Items.STICK);
         }

         this.currentIconStack = this.defaultIconStack;
      }

   }

   public void setIconStacks(List<ItemStack> iconStacks) {
      this.iconStacks = iconStacks;
      this.currentStackSize = this.iconStacks.size();
   }

   public void addIconStacks(List<ItemStack> iconStacks) {
      this.iconStacks.addAll(iconStacks);
      this.currentStackSize = this.iconStacks.size();
   }
}
