package xzeroair.trinkets.capabilities.InventoryContainerCapability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import xzeroair.trinkets.container.TrinketContainerHandler;

public class TrinketContainerProvider implements ICapabilitySerializable<NBTTagCompound> {
   @CapabilityInject(ITrinketContainerHandler.class)
   public static final Capability<ITrinketContainerHandler> containerCap = null;
   private final TrinketContainerHandler container;

   public TrinketContainerProvider(TrinketContainerHandler container) {
      this.container = container;
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == containerCap;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == containerCap ? this.container : null);
   }

   public NBTTagCompound serializeNBT() {
      return this.container.serializeNBT();
   }

   public void deserializeNBT(NBTTagCompound nbt) {
      this.container.deserializeNBT(nbt);
   }
}
