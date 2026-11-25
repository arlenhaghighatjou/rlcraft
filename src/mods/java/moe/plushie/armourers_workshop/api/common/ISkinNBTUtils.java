package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface ISkinNBTUtils {
   void setSkinDescriptor(ItemStack var1, ISkinDescriptor var2);

   ISkinDescriptor getSkinDescriptor(ItemStack var1);

   void removeSkinDescriptor(ItemStack var1);

   boolean hasSkinDescriptor(ItemStack var1);

   void setSkinDescriptor(NBTTagCompound var1, ISkinDescriptor var2);

   ISkinDescriptor getSkinDescriptor(NBTTagCompound var1);

   void removeSkinDescriptor(NBTTagCompound var1);

   boolean hasSkinDescriptor(NBTTagCompound var1);
}
