package com.teamacronymcoders.contenttweaker.api.ctobjects.capabilities.item;

import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Facing;
import com.teamacronymcoders.contenttweaker.api.ctobjects.tileentity.IMCTileEntity;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import stanhebben.zenscript.annotations.ZenMethod;

public class TileEntityItemCapExpansion {
   public TileEntityItemCapExpansion() {
   }

   @ZenMethod
   public static ItemCapability getItemHandler(IMCTileEntity tileEntity, Facing facing) {
      return (ItemCapability)Optional.ofNullable(((TileEntity)tileEntity.getInternal()).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getInternal())).map(ItemCapability::new).orElse((Object)null);
   }
}
