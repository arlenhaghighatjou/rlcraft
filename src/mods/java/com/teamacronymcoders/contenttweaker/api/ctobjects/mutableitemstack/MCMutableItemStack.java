package com.teamacronymcoders.contenttweaker.api.ctobjects.mutableitemstack;

import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.mc1120.item.MCItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class MCMutableItemStack extends MCItemStack implements IMutableItemStack {
   private ItemStack itemStack;

   public MCMutableItemStack(ItemStack itemStack) {
      super(itemStack);
      this.itemStack = itemStack;
   }

   public void setCount(int count) {
      this.itemStack.setCount(count);
   }

   public void shrink(int count) {
      this.itemStack.shrink(count);
   }

   public void grow(int count) {
      this.itemStack.grow(count);
   }

   public void damage(int amount, IEntityLivingBase entity) {
      this.itemStack.damageItem(amount, (EntityLivingBase)entity.getInternal());
   }
}
