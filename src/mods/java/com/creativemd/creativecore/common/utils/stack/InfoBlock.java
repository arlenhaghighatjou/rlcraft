package com.creativemd.creativecore.common.utils.stack;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class InfoBlock extends InfoStack {
   public Block block;

   public InfoBlock(Block block, int stackSize) {
      super(stackSize);
      this.block = block;
   }

   public InfoBlock(Block block) {
      this(block, 1);
   }

   public InfoBlock() {
   }

   protected void writeToNBTExtra(NBTTagCompound nbt) {
      ResourceLocation location = (ResourceLocation)Block.REGISTRY.getNameForObject(this.block);
      if (location != null) {
         nbt.setString("block", location.toString());
      } else {
         System.out.println("Invalid InfoBlock entry. Block does not exists");
      }

   }

   protected void loadFromNBTExtra(NBTTagCompound nbt) {
      this.block = (Block)Block.REGISTRY.getObject(new ResourceLocation(nbt.getString("block")));
   }

   public boolean isInstanceIgnoreSize(InfoStack info) {
      if (info instanceof InfoBlock) {
         return ((InfoBlock)info).block == this.block;
      } else if (info instanceof InfoItemStack) {
         return this.block == Block.getBlockFromItem(((InfoItemStack)info).stack.getItem());
      } else {
         return false;
      }
   }

   public InfoStack copy() {
      return new InfoBlock(this.block, this.stackSize);
   }

   public ItemStack getItemStack(int stacksize) {
      return new ItemStack(this.block, stacksize);
   }

   protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
      return this.block == Block.getBlockFromItem(stack.getItem());
   }

   public boolean equalsIgnoreSize(Object object) {
      return object instanceof InfoBlock && ((InfoBlock)object).block == this.block;
   }

   public ArrayList<ItemStack> getAllPossibleItemStacks() {
      ArrayList<ItemStack> stacks = new ArrayList();
      stacks.add(new ItemStack(this.block, 1, 32767));
      return stacks;
   }
}
