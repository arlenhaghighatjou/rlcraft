package net.mcft.copy.backpacks.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public interface IBackpack {
   @CapabilityInject(IBackpack.class)
   Capability<IBackpack> CAPABILITY = null;

   ItemStack getStack();

   void setStack(ItemStack var1);

   IBackpackData getData();

   void setData(IBackpackData var1);

   int getPlayersUsing();

   void setPlayersUsing(int var1);

   int getLidTicks();

   int getPrevLidTicks();

   void setLidTicks(int var1);

   default IBackpackType getType() {
      return BackpackHelper.getBackpackType(this.getStack());
   }

   public static class Impl implements IBackpack {
      protected ItemStack stack = ItemStack.EMPTY;
      protected IBackpackData data = null;
      protected int playersUsing = 0;
      protected int lidTicks = 0;
      protected int prevLidTicks = 0;

      public Impl() {
      }

      public ItemStack getStack() {
         return this.stack;
      }

      public void setStack(ItemStack value) {
         this.stack = value;
      }

      public IBackpackData getData() {
         return this.data;
      }

      public void setData(IBackpackData value) {
         this.data = value;
      }

      public int getPlayersUsing() {
         return this.playersUsing;
      }

      public void setPlayersUsing(int value) {
         this.playersUsing = value;
      }

      public int getLidTicks() {
         return this.lidTicks;
      }

      public int getPrevLidTicks() {
         return this.prevLidTicks;
      }

      public void setLidTicks(int value) {
         this.prevLidTicks = this.lidTicks;
         this.lidTicks = value;
      }
   }
}
