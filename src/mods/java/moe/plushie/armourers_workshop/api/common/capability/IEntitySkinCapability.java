package moe.plushie.armourers_workshop.api.common.capability;

import moe.plushie.armourers_workshop.api.common.ISkinInventoryContainer;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public interface IEntitySkinCapability {
   ISkinType[] getValidSkinTypes();

   boolean canHoldSkinType(ISkinType var1);

   int getSlotCountForSkinType(ISkinType var1);

   ISkinDescriptor getSkinDescriptor(ISkinType var1, int var2);

   ISkinDescriptor setSkinDescriptor(ISkinType var1, int var2, ISkinDescriptor var3);

   ItemStack getSkinStack(ISkinType var1, int var2);

   ItemStack setSkinStack(ISkinType var1, int var2, ItemStack var3);

   boolean setStackInNextFreeSlot(ItemStack var1);

   ISkinInventoryContainer getSkinInventoryContainer();

   void clear();

   void clearSkin(ISkinType var1, int var2);

   void syncToPlayer(EntityPlayerMP var1);

   void syncToAllTracking();
}
