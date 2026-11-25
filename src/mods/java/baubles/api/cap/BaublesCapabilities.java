package baubles.api.cap;

import baubles.api.IBauble;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class BaublesCapabilities {
   public static Capability<IBaublesItemHandler> CAPABILITY_BAUBLES = null;
   public static Capability<IBauble> CAPABILITY_ITEM_BAUBLE = null;

   public BaublesCapabilities() {
   }

   public static void register() {
      CapabilityManager.INSTANCE.onRegistered(IBaublesItemHandler.class, capability -> CAPABILITY_BAUBLES = capability);
      CapabilityManager.INSTANCE.onRegistered(IBauble.class, capability -> CAPABILITY_ITEM_BAUBLE = capability);
   }

   public static class CapabilityBaubles<T extends IBaublesItemHandler> implements IStorage<IBaublesItemHandler> {
      public CapabilityBaubles() {
      }

      public NBTBase writeNBT(Capability<IBaublesItemHandler> capability, IBaublesItemHandler instance, EnumFacing side) {
         return null;
      }

      public void readNBT(Capability<IBaublesItemHandler> capability, IBaublesItemHandler instance, EnumFacing side, NBTBase nbt) {
      }
   }

   public static class CapabilityItemBaubleStorage implements IStorage<IBauble> {
      public CapabilityItemBaubleStorage() {
      }

      public NBTBase writeNBT(Capability<IBauble> capability, IBauble instance, EnumFacing side) {
         return null;
      }

      public void readNBT(Capability<IBauble> capability, IBauble instance, EnumFacing side, NBTBase nbt) {
      }
   }
}
