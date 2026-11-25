package slimeknights.mantle.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemStackList extends NonNullList<ItemStack> {
   protected ItemStackList() {
      this(new ArrayList());
   }

   protected ItemStackList(List<ItemStack> delegate) {
      super(delegate, ItemStack.EMPTY);
   }

   public static ItemStackList create() {
      return new ItemStackList();
   }

   public static ItemStackList withSize(int size) {
      ItemStack[] aobject = new ItemStack[size];
      Arrays.fill(aobject, ItemStack.EMPTY);
      return new ItemStackList(Arrays.asList(aobject));
   }

   public static ItemStackList of(ItemStack... element) {
      ItemStackList itemStackList = create();
      itemStackList.addAll(Arrays.asList(element));
      return itemStackList;
   }

   public static ItemStackList of(Collection<ItemStack> boringList) {
      ItemStackList itemStackList = create();
      itemStackList.addAll(boringList);
      return itemStackList;
   }

   public static ItemStackList of(IInventory inventory) {
      ItemStackList itemStackList = withSize(inventory.getSizeInventory());

      for(int i = 0; i < inventory.getSizeInventory(); ++i) {
         itemStackList.add(inventory.getStackInSlot(i));
      }

      return itemStackList;
   }

   public boolean hasItem(int index) {
      return index >= 0 && index < this.size() && !((ItemStack)this.get(index)).isEmpty();
   }

   public void setEmpty(int index) {
      if (index >= 0 && index < this.size()) {
         this.set(index, ItemStack.EMPTY);
      }

   }

   public ItemStackList copy(boolean fixed) {
      ItemStackList copy = fixed ? withSize(this.size()) : create();

      for(int i = 0; i < this.size(); ++i) {
         copy.set(i, this.get(i));
      }

      return copy;
   }

   public ItemStackList deepCopy(boolean fixed) {
      ItemStackList copy = fixed ? withSize(this.size()) : create();

      for(int i = 0; i < this.size(); ++i) {
         copy.set(i, ((ItemStack)this.get(i)).copy());
      }

      return copy;
   }
}
