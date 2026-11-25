package xzeroair.trinkets.capabilities.InventoryContainerCapability;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface ITrinketContainerHandler extends IItemHandlerModifiable {
   boolean isItemValidForSlot(int var1, ItemStack var2, EntityLivingBase var3);

   boolean isEventBlocked();

   void setEventBlock(boolean var1);

   void setPlayer(EntityLivingBase var1);
}
