package pl.asie.foamfix.common.nbt;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTBase;

public class FoamNBTTagCompoundMap implements Map<String, NBTBase> {
   private final FoamNBTTagCompound delegate;

   FoamNBTTagCompoundMap(FoamNBTTagCompound delegate) {
      this.delegate = delegate;
   }

   public int size() {
      return this.delegate.size;
   }

   public boolean isEmpty() {
      return this.delegate.size == 0;
   }

   public boolean containsKey(Object key) {
      return this.delegate.ffGetTag((String)key) == null;
   }

   public boolean containsValue(Object value) {
      throw new UnsupportedOperationException();
   }

   public NBTBase get(Object key) {
      return this.delegate.ffGetTag((String)key);
   }

   public NBTBase put(String key, NBTBase value) {
      return this.delegate.ffPutTag(key, value);
   }

   public NBTBase remove(Object key) {
      return this.delegate.ffRemove((String)key);
   }

   public void putAll(Map<? extends String, ? extends NBTBase> map) {
      for(Entry var3 : map.entrySet()) {
         ;
      }

   }

   public void clear() {
      this.delegate.ffClear();
   }

   public Set<String> keySet() {
      Set<String> keys = new HashSet(this.delegate.data.length);

      for(int i = 0; i < this.delegate.data.length; i += 2) {
         Object key = this.delegate.data[i];
         if (key != null) {
            keys.add((String)key);
         }
      }

      return keys;
   }

   public Collection<NBTBase> values() {
      throw new UnsupportedOperationException();
   }

   public Set<Entry<String, NBTBase>> entrySet() {
      throw new UnsupportedOperationException();
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof Map)) {
         return false;
      } else {
         Map other = (Map)obj;
         if (other.size() != this.size()) {
            return false;
         } else {
            for(int i = 0; i < this.delegate.data.length; i += 2) {
               Object key = this.delegate.data[i];
               if (key != null) {
                  Object value = this.delegate.data[i + 1];
                  if (!Objects.equals(other.get(key), value)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      int hash = 0;

      for(int i = 0; i < this.delegate.data.length; i += 2) {
         Object key = this.delegate.data[i];
         if (key != null) {
            Object value = this.delegate.data[i + 1];
            hash += key.hashCode() + (value != null ? value.hashCode() : 0);
         }
      }

      return hash;
   }
}
