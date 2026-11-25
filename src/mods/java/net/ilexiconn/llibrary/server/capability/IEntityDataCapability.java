package net.ilexiconn.llibrary.server.capability;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IEntityDataCapability extends ICapabilitySerializable<NBTBase> {
   void init(Entity var1, World var2, boolean var3);

   void saveToNBT(NBTTagCompound var1);

   void loadFromNBT(NBTTagCompound var1);

   <T extends Entity> void registerData(IEntityData<T> var1);

   @Nullable
   <T extends Entity> IEntityData<T> getData(String var1);

   <T extends Entity> List<IEntityData<T>> getData();
}
