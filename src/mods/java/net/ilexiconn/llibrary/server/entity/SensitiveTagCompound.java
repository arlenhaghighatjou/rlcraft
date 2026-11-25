package net.ilexiconn.llibrary.server.entity;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

public class SensitiveTagCompound extends NBTTagCompound {
   private boolean changed = false;
   private Set<String> changedTags = new HashSet();

   public SensitiveTagCompound() {
   }

   public void setByte(String key, byte value) {
      NBTBase b = (NBTBase)this.tagMap.put(key, new NBTTagByte(value));
      if (b == null) {
         this.updateChanged(key);
      } else if (b instanceof NBTTagByte && ((NBTTagByte)b).getByte() != value) {
         this.updateChanged(key);
      }

   }

   public void setByteArray(String key, byte[] value) {
      NBTBase b = (NBTBase)this.tagMap.put(key, new NBTTagByteArray(value));
      if (b == null) {
         this.updateChanged(key);
      } else if (b instanceof NBTTagByteArray && ((NBTTagByteArray)b).getByteArray() != value) {
         this.updateChanged(key);
      }

   }

   public void setDouble(String key, double value) {
      NBTBase b = (NBTBase)this.tagMap.put(key, new NBTTagDouble(value));
      if (b == null) {
         this.updateChanged(key);
      } else if (b instanceof NBTTagDouble && ((NBTTagDouble)b).getDouble() != value) {
         this.updateChanged(key);
      }

   }

   public void setFloat(String key, float value) {
      NBTBase b = (NBTBase)this.tagMap.put(key, new NBTTagFloat(value));
      if (b == null) {
         this.updateChanged(key);
      } else if (b instanceof NBTTagFloat && ((NBTTagFloat)b).getFloat() != value) {
         this.updateChanged(key);
      }

   }

   public void setIntArray(String key, int[] value) {
      NBTBase b = (NBTBase)this.tagMap.put(key, new NBTTagIntArray(value));
      if (b == null) {
         this.updateChanged(key);
      } else if (b instanceof NBTTagIntArray && ((NBTTagIntArray)b).getIntArray() != value) {
         this.updateChanged(key);
      }

   }

   public void setInteger(String key, int value) {
      NBTBase b = (NBTBase)this.tagMap.put(key, new NBTTagInt(value));
      if (b == null) {
         this.updateChanged(key);
      } else if (b instanceof NBTTagInt && ((NBTTagInt)b).getInt() != value) {
         this.updateChanged(key);
      }

   }

   public void setLong(String key, long value) {
      NBTBase b = (NBTBase)this.tagMap.put(key, new NBTTagLong(value));
      if (b == null) {
         this.updateChanged(key);
      } else if (b instanceof NBTTagLong && ((NBTTagLong)b).getLong() != value) {
         this.updateChanged(key);
      }

   }

   public void setShort(String key, short value) {
      NBTBase b = (NBTBase)this.tagMap.put(key, new NBTTagShort(value));
      if (b == null) {
         this.updateChanged(key);
      } else if (b instanceof NBTTagShort && ((NBTTagShort)b).getShort() != value) {
         this.updateChanged(key);
      }

   }

   public void setString(String key, String value) {
      NBTBase b = (NBTBase)this.tagMap.put(key, new NBTTagString(value));
      if (b == null) {
         this.updateChanged(key);
      } else if (b instanceof NBTTagString && !((NBTTagString)b).getString().equals(value)) {
         this.updateChanged(key);
      }

   }

   public void setTag(String key, NBTBase value) {
      NBTBase b = (NBTBase)this.tagMap.put(key, value);
      if (b == null) {
         this.updateChanged(key);
      } else if (!b.equals(value)) {
         this.updateChanged(key);
      }

   }

   public void updateChanged(String key) {
      this.changedTags.add(key);
      this.changed = true;
   }

   public NBTTagCompound getChangedCopy() {
      NBTTagCompound tag = new NBTTagCompound();

      for(String s : this.changedTags) {
         tag.tagMap.put(s, this.tagMap.get(s));
      }

      return tag;
   }

   public boolean hasChanged() {
      return this.changed;
   }

   public void reset() {
      this.changed = false;
      this.changedTags.clear();
   }
}
