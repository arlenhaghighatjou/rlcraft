package net.ilexiconn.llibrary.server.capability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.ilexiconn.llibrary.LLibrary;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class EntityDataCapabilityImplementation implements IEntityDataCapability {
   private final Map<String, IEntityData> attachedData;
   private final List<IEntityData> attachedDataList;

   public EntityDataCapabilityImplementation(List<IEntityData> data) {
      this.attachedData = new LinkedHashMap(data.size());
      this.attachedDataList = new ArrayList(data.size());

      for(IEntityData d : data) {
         this.attachedData.put(d.getID(), d);
         this.attachedDataList.add(d);
      }

   }

   public EntityDataCapabilityImplementation() {
      this(new ArrayList());
   }

   /** @deprecated */
   @Deprecated
   public static IEntityDataCapability getCapability(Entity entity) {
      return (IEntityDataCapability)entity.getCapability(LLibrary.ENTITY_DATA_CAPABILITY, (EnumFacing)null);
   }

   public void init(Entity entity, World world, boolean init) {
      if (init) {
         for(IEntityData entityData : this.attachedData.values()) {
            entityData.init(entity, world);
         }
      }

   }

   public void saveToNBT(NBTTagCompound compound) {
      for(IEntityData entityData : this.attachedData.values()) {
         NBTTagCompound managerTag = new NBTTagCompound();
         entityData.saveNBTData(managerTag);
         compound.setTag(entityData.getID(), managerTag);
      }

   }

   public void loadFromNBT(NBTTagCompound compound) {
      for(IEntityData entityData : this.attachedData.values()) {
         NBTTagCompound managerTag = compound.getCompoundTag(entityData.getID());
         entityData.loadNBTData(managerTag);
      }

   }

   public <T extends Entity> void registerData(IEntityData<T> data) {
      this.attachedData.put(data.getID(), data);
      this.attachedDataList.add(data);
   }

   @Nullable
   public <T extends Entity> IEntityData<T> getData(String identifier) {
      IEntityData retrieved = (IEntityData)this.attachedData.get(identifier);
      return retrieved;
   }

   public <T extends Entity> List<IEntityData<T>> getData() {
      List data = Collections.unmodifiableList(this.attachedDataList);
      return data;
   }

   public NBTBase serializeNBT() {
      Capability<IEntityDataCapability> capability = LLibrary.ENTITY_DATA_CAPABILITY;
      return capability.getStorage().writeNBT(capability, this, (EnumFacing)null);
   }

   public void deserializeNBT(NBTBase nbt) {
      Capability<IEntityDataCapability> capability = LLibrary.ENTITY_DATA_CAPABILITY;
      capability.getStorage().readNBT(capability, this, (EnumFacing)null, nbt);
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return LLibrary.ENTITY_DATA_CAPABILITY == capability;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == LLibrary.ENTITY_DATA_CAPABILITY ? LLibrary.ENTITY_DATA_CAPABILITY.cast(this) : null);
   }
}
