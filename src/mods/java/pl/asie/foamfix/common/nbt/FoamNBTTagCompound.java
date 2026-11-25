package pl.asie.foamfix.common.nbt;

import java.util.Map;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.math.MathHelper;

public class FoamNBTTagCompound implements IFoamFixNBTTagCompound {
   private static final Object[] EMPTY = new Object[0];
   Object[] data;
   int size;
   int mask;
   private final Map<String, NBTBase> tagMap;

   public FoamNBTTagCompound() {
      this.data = EMPTY;
      this.tagMap = new FoamNBTTagCompoundMap(this);
      this.size = 0;
      this.mask = 0;
   }

   private NBTBase ffPutAfterRehash(Object key, Object base) {
      int hash = key.hashCode() & this.mask;

      while(true) {
         int hKey = hash << 1;
         Object cmpKey = this.data[hKey];
         if (cmpKey == null) {
            this.data[hKey] = key;
            this.data[hKey | 1] = base;
            ++this.size;
            return null;
         }

         if (key.equals(cmpKey)) {
            NBTBase oldBase = (NBTBase)this.data[hKey | 1];
            this.data[hKey | 1] = base;
            return oldBase;
         }

         hash = hash + 1 & this.mask;
      }
   }

   private void ffRehash(int newExpectedSize) {
      int idealSize = MathHelper.smallestEncompassingPowerOfTwo((newExpectedSize * 4 + 2) / 3);
      if (this.data.length != idealSize * 2) {
         if (newExpectedSize == 0) {
            this.data = EMPTY;
            this.size = 0;
            this.mask = 0;
            return;
         }

         Object[] oldData = this.data;
         this.data = new Object[idealSize * 2];
         this.size = 0;
         this.mask = idealSize - 1;

         for(int i = 0; i < oldData.length; i += 2) {
            if (oldData[i] != null) {
               this.ffPutAfterRehash(oldData[i], oldData[i | 1]);
            }
         }
      }

   }

   NBTBase ffGetTag(String key) {
      if (key != null && this.size != 0) {
         int hash = key.hashCode() & this.mask;

         while(true) {
            int hKey = hash << 1;
            Object cmpKey = this.data[hKey];
            if (cmpKey == null) {
               return null;
            }

            if (key.equals(cmpKey)) {
               return (NBTBase)this.data[hKey | 1];
            }

            hash = hash + 1 & this.mask;
         }
      } else {
         return null;
      }
   }

   NBTBase ffPutTag(String key, NBTBase base) {
      if (key == null) {
         throw new IllegalArgumentException("key = null");
      } else {
         this.ffRehash(this.size + 1);
         return this.ffPutAfterRehash(key, base);
      }
   }

   NBTBase ffRemove(String key) {
      if (key == null) {
         return null;
      } else {
         int hash = key.hashCode() & this.mask;

         while(true) {
            int hKey = hash << 1;
            Object cmpKey = this.data[hKey];
            if (cmpKey == null) {
               return null;
            }

            if (key.equals(cmpKey)) {
               NBTBase base = (NBTBase)this.data[hKey | 1];
               this.data[hKey] = null;
               this.data[hKey | 1] = null;
               --this.size;
               this.ffRehash(this.size);
               return base;
            }

            hash = hash + 1 & this.mask;
         }
      }
   }

   void ffClear() {
      this.data = null;
      this.size = 0;
      this.mask = 0;
   }

   public Map<String, NBTBase> foamfix_getTagMap() {
      return this.tagMap;
   }

   public boolean equals(Object p_equals_1_) {
      return super.equals(p_equals_1_) && this.tagMap.equals(((IFoamFixNBTTagCompound)p_equals_1_).foamfix_getTagMap());
   }
}
