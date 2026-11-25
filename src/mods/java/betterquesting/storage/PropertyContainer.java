package betterquesting.storage;

import betterquesting.api.properties.IPropertyContainer;
import betterquesting.api.properties.IPropertyType;
import betterquesting.api2.storage.INBTSaveLoad;
import java.util.ArrayList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class PropertyContainer implements IPropertyContainer, INBTSaveLoad<NBTTagCompound> {
   private final NBTTagCompound nbtInfo = new NBTTagCompound();

   public PropertyContainer() {
   }

   public synchronized <T> T getProperty(IPropertyType<T> prop) {
      return (T)(prop == null ? null : this.getProperty(prop, prop.getDefault()));
   }

   public synchronized <T> T getProperty(IPropertyType<T> prop, T def) {
      if (prop == null) {
         return def;
      } else {
         NBTTagCompound jProp = this.getDomain(prop.getKey());
         return (T)(!jProp.hasKey(prop.getKey().getResourcePath()) ? def : prop.readValue(jProp.getTag(prop.getKey().getResourcePath())));
      }
   }

   public synchronized boolean hasProperty(IPropertyType<?> prop) {
      return prop == null ? false : this.getDomain(prop.getKey()).hasKey(prop.getKey().getResourcePath());
   }

   public synchronized void removeProperty(IPropertyType<?> prop) {
      if (prop != null) {
         NBTTagCompound jProp = this.getDomain(prop.getKey());
         if (jProp.hasKey(prop.getKey().getResourcePath())) {
            jProp.removeTag(prop.getKey().getResourcePath());
            if (jProp.hasNoTags()) {
               this.nbtInfo.removeTag(prop.getKey().getResourceDomain());
            }

         }
      }
   }

   public synchronized <T> void setProperty(IPropertyType<T> prop, T value) {
      if (prop != null && value != null) {
         NBTTagCompound dom = this.getDomain(prop.getKey());
         dom.setTag(prop.getKey().getResourcePath(), prop.writeValue(value));
         this.nbtInfo.setTag(prop.getKey().getResourceDomain(), dom);
      }
   }

   public synchronized void removeAllProps() {
      for(String key : new ArrayList(this.nbtInfo.getKeySet())) {
         this.nbtInfo.removeTag(key);
      }

   }

   public synchronized NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.merge(this.nbtInfo);
      return nbt;
   }

   public synchronized void readFromNBT(NBTTagCompound nbt) {
      for(String key : this.nbtInfo.getKeySet()) {
         this.nbtInfo.removeTag(key);
      }

      this.nbtInfo.merge(nbt);
   }

   private NBTTagCompound getDomain(ResourceLocation res) {
      return this.nbtInfo.getCompoundTag(res.getResourceDomain());
   }
}
