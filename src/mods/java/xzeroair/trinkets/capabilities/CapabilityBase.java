package xzeroair.trinkets.capabilities;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.nbt.NBTTagCompound;
import xzeroair.trinkets.util.Reference;
import xzeroair.trinkets.util.handlers.TickHandler;

public abstract class CapabilityBase<T, E> implements ITrinketCapability<T> {
   protected EaglercraftRandom random = Reference.random;
   protected NBTTagCompound tag = new NBTTagCompound();
   protected TickHandler tickHandler = new TickHandler();
   protected E object;

   public CapabilityBase(E object) {
      this.object = object;
   }

   public NBTTagCompound getTag() {
      if (this.tag == null) {
         this.tag = new NBTTagCompound();
      }

      return this.tag;
   }

   public TickHandler getTickHandler() {
      return this.tickHandler;
   }

   public void onUpdate() {
   }

   public NBTTagCompound saveToNBT(NBTTagCompound tag) {
      return tag;
   }

   public void loadFromNBT(NBTTagCompound tag) {
   }

   public void copyFrom(T capability, boolean wasDeath, boolean keepInv) {
   }
}
