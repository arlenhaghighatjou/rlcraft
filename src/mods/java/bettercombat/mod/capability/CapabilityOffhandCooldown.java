package bettercombat.mod.capability;

import bettercombat.mod.handler.EventHandlers;
import bettercombat.mod.network.PacketHandler;
import bettercombat.mod.network.PacketOffhandCooldown;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.INBTSerializable;

public class CapabilityOffhandCooldown implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {
   private int offhandCooldown = 0;
   private int offhandBeginningCooldown = 0;
   private EntityPlayer player;

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability != null && capability == EventHandlers.TUTO_CAP;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability != null && capability == EventHandlers.TUTO_CAP ? this : null);
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setInteger("offhandCooldown", this.offhandCooldown);
      compound.setInteger("offhandBeginningCooldown", this.offhandBeginningCooldown);
      return compound;
   }

   public void deserializeNBT(NBTTagCompound compound) {
      this.offhandCooldown = compound.getInteger("offhandCooldown");
      this.offhandBeginningCooldown = compound.getInteger("offhandBeginningCooldown");
   }

   public void tick() {
      if (this.offhandCooldown > 0) {
         --this.offhandCooldown;
      }

      if (this.offhandCooldown <= 0) {
         this.offhandCooldown = 0;
         this.offhandBeginningCooldown = 0;
      }

   }

   public void setOffhandCooldown(int in) {
      this.offhandCooldown = in;
   }

   public int getOffhandCooldown() {
      return this.offhandCooldown;
   }

   public void setOffhandBeginningCooldown(int in) {
      this.offhandBeginningCooldown = in;
   }

   public int getOffhandBeginningCooldown() {
      return this.offhandBeginningCooldown;
   }

   public static void register() {
      CapabilityManager.INSTANCE.register(CapabilityOffhandCooldown.class, new CapabilityOffhandCooldown.Storage(), new CapabilityOffhandCooldown.Factory());
   }

   public CapabilityOffhandCooldown(@Nonnull EntityPlayer player) {
      this.player = player;
   }

   public void sync() {
      PacketOffhandCooldown packet = new PacketOffhandCooldown(this.offhandCooldown, this.offhandBeginningCooldown);
      if (!this.player.world.isRemote) {
         EntityPlayerMP playerMP = (EntityPlayerMP)this.player;
         PacketHandler.instance.sendTo(packet, playerMP);
      } else {
         PacketHandler.instance.sendToServer(packet);
      }

   }

   public static class Factory implements Callable<CapabilityOffhandCooldown> {
      public Factory() {
      }

      public CapabilityOffhandCooldown call() {
         return null;
      }
   }

   public static class Storage implements IStorage<CapabilityOffhandCooldown> {
      public Storage() {
      }

      public NBTBase writeNBT(Capability<CapabilityOffhandCooldown> capability, CapabilityOffhandCooldown instance, EnumFacing side) {
         return null;
      }

      public void readNBT(Capability<CapabilityOffhandCooldown> capability, CapabilityOffhandCooldown instance, EnumFacing side, NBTBase nbt) {
      }
   }
}
