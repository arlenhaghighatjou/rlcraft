package com.attributestudios.wolfarmor.api;

import dev.satyrn.wolfarmor.api.entity.passive.IArmoredWolf;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/** @deprecated */
@Deprecated
public interface IWolfArmorCapability extends IArmoredWolf, ICapabilitySerializable<NBTTagCompound>, IInventoryChangedListener {
   /** @deprecated */
   @Deprecated
   boolean processInteract(@Nonnull EntityPlayer var1, @Nonnull EnumHand var2);

   /** @deprecated */
   @Deprecated
   boolean damageArmor(float var1);
}
