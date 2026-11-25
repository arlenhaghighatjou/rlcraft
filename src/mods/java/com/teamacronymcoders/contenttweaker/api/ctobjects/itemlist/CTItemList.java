package com.teamacronymcoders.contenttweaker.api.ctobjects.itemlist;

import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.item.MCItemStack;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class CTItemList implements ICTItemList {
   private static final Random RANDOM = new Random();
   private final NonNullList<ItemStack> nonNullList;

   public CTItemList(NonNullList<ItemStack> nonNullList) {
      this.nonNullList = nonNullList;
   }

   public void add(IItemStack iItemStack) {
      this.nonNullList.add(CraftTweakerMC.getItemStack(iItemStack));
   }

   public void add(WeightedItemStack weightedItemStack) {
      if (RANDOM.nextFloat() < weightedItemStack.getChance()) {
         this.add(weightedItemStack.getStack());
      }

   }

   public void remove(int index) {
      this.nonNullList.remove(index);
   }

   public IItemStack get(int index) {
      return new MCItemStack((ItemStack)this.nonNullList.get(index));
   }

   public IItemStack[] getArray() {
      return (IItemStack[])this.nonNullList.parallelStream().map(MCItemStack::new).toArray((x$0) -> {
         return new IItemStack[x$0];
      });
   }

   public List<IItemStack> getList() {
      return (List)this.nonNullList.parallelStream().map(MCItemStack::new).collect(Collectors.toList());
   }

   public int getLength() {
      return this.nonNullList.size();
   }

   public void clear() {
      this.nonNullList.clear();
   }
}
