package xzeroair.trinkets.capabilities.InventoryContainerCapability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class TrinketContainerStorage implements IStorage<ITrinketContainerHandler> {
   public static final TrinketContainerStorage storage = new TrinketContainerStorage();

   public TrinketContainerStorage() {
   }

   public NBTBase writeNBT(Capability<ITrinketContainerHandler> capability, ITrinketContainerHandler instance, EnumFacing side) {
      return null;
   }

   public void readNBT(Capability<ITrinketContainerHandler> capability, ITrinketContainerHandler instance, EnumFacing side, NBTBase nbt) {
   }
}
