package org.orecruncher.dsurround.capabilities.entitydata;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IEntityData extends INBTSerializable<NBTTagCompound> {
   int getEntityId();

   boolean isAttacking();

   boolean isFleeing();
}
