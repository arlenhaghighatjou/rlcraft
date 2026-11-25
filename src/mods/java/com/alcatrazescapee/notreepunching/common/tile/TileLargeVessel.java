package com.alcatrazescapee.notreepunching.common.tile;

import com.alcatrazescapee.alcatrazcore.tile.TileInventory;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

@ParametersAreNonnullByDefault
public class TileLargeVessel extends TileInventory {
   public TileLargeVessel() {
      super(9);
   }

   public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
   }

   public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.inventory : super.getCapability(capability, facing));
   }
}
