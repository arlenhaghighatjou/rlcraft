package snownee.kiwi.util;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

public class NBTHelper {
   @Nullable
   private ItemStack stack;
   @Nullable
   private NBTTagCompound tag;

   private NBTHelper(@Nullable NBTTagCompound tag, @Nullable ItemStack stack) {
      this.stack = stack;
      this.tag = tag;
   }

   public NBTTagCompound getTag(String key) {
      return this.getTag(key, false);
   }

   public NBTTagCompound getTag(String key, boolean createIfNull) {
      return this.getTagInternal(key, createIfNull, false);
   }

   private NBTTagCompound getTagInternal(String key, boolean createIfNull, boolean ignoreLastNode) {
      if (this.tag == null) {
         if (!createIfNull) {
            return null;
         }

         this.tag = new NBTTagCompound();
         if (this.stack != null) {
            this.stack.setTagCompound(this.tag);
         }
      }

      if (key.isEmpty()) {
         return this.tag;
      } else {
         NBTTagCompound subTag = this.tag;
         String[] parts = key.split("\\.");
         int length = parts.length;
         if (ignoreLastNode) {
            --length;
         }

         for(int i = 0; i < length; ++i) {
            if (!subTag.hasKey(parts[i], 10)) {
               if (!createIfNull) {
                  return null;
               }

               subTag.setTag(parts[i], new NBTTagCompound());
            }

            subTag = subTag.getCompoundTag(parts[i]);
         }

         return subTag;
      }
   }

   private NBTTagCompound getTagInternal(String key) {
      return this.getTagInternal(key, true, true);
   }

   private String getLastNode(String key) {
      int index = key.lastIndexOf(".");
      return index < 0 ? key : key.substring(index + 1);
   }

   public NBTHelper setTag(String key, NBTBase value) {
      this.getTagInternal(key).setTag(this.getLastNode(key), value);
      return this;
   }

   public NBTHelper setInt(String key, int value) {
      this.getTagInternal(key).setInteger(this.getLastNode(key), value);
      return this;
   }

   public int getInt(String key) {
      return this.getInt(key, 0);
   }

   public int getInt(String key, int defaultValue) {
      NBTTagCompound subTag = this.getTagInternal(key, false, true);
      if (subTag != null) {
         String actualKey = this.getLastNode(key);
         if (subTag.hasKey(actualKey, 3)) {
            return subTag.getInteger(actualKey);
         }
      }

      return defaultValue;
   }

   public NBTHelper setLong(String key, long value) {
      this.getTagInternal(key).setLong(this.getLastNode(key), value);
      return this;
   }

   public long getLong(String key) {
      return this.getLong(key, 0L);
   }

   public long getLong(String key, long defaultValue) {
      NBTTagCompound subTag = this.getTagInternal(key, false, true);
      if (subTag != null) {
         String actualKey = this.getLastNode(key);
         if (subTag.hasKey(actualKey, 4)) {
            return subTag.getLong(actualKey);
         }
      }

      return defaultValue;
   }

   public NBTHelper setShort(String key, short value) {
      this.getTagInternal(key).setShort(this.getLastNode(key), value);
      return this;
   }

   public short getShort(String key) {
      return this.getShort(key, (short)0);
   }

   public short getShort(String key, short defaultValue) {
      NBTTagCompound subTag = this.getTagInternal(key, false, true);
      if (subTag != null) {
         String actualKey = this.getLastNode(key);
         if (subTag.hasKey(actualKey, 2)) {
            return subTag.getShort(actualKey);
         }
      }

      return defaultValue;
   }

   public NBTHelper setDouble(String key, double value) {
      this.getTagInternal(key).setDouble(this.getLastNode(key), value);
      return this;
   }

   public double getDouble(String key) {
      return this.getDouble(key, 0.0D);
   }

   public double getDouble(String key, double defaultValue) {
      NBTTagCompound subTag = this.getTagInternal(key, false, true);
      if (subTag != null) {
         String actualKey = this.getLastNode(key);
         if (subTag.hasKey(actualKey, 6)) {
            return subTag.getDouble(actualKey);
         }
      }

      return defaultValue;
   }

   public NBTHelper setFloat(String key, float value) {
      this.getTagInternal(key).setFloat(this.getLastNode(key), value);
      return this;
   }

   public float getFloat(String key) {
      return this.getFloat(key, 0.0F);
   }

   public float getFloat(String key, float defaultValue) {
      NBTTagCompound subTag = this.getTagInternal(key, false, true);
      if (subTag != null) {
         String actualKey = this.getLastNode(key);
         if (subTag.hasKey(actualKey, 5)) {
            return subTag.getFloat(actualKey);
         }
      }

      return defaultValue;
   }

   public NBTHelper setByte(String key, byte value) {
      this.getTagInternal(key).setFloat(this.getLastNode(key), (float)value);
      return this;
   }

   public byte getByte(String key) {
      return this.getByte(key, (byte)0);
   }

   public byte getByte(String key, byte defaultValue) {
      NBTTagCompound subTag = this.getTagInternal(key, false, true);
      if (subTag != null) {
         String actualKey = this.getLastNode(key);
         if (subTag.hasKey(actualKey, 1)) {
            return subTag.getByte(actualKey);
         }
      }

      return defaultValue;
   }

   public NBTHelper setBoolean(String key, boolean value) {
      this.getTagInternal(key).setBoolean(this.getLastNode(key), value);
      return this;
   }

   public boolean getBoolean(String key) {
      return this.getBoolean(key, false);
   }

   public boolean getBoolean(String key, boolean defaultValue) {
      NBTTagCompound subTag = this.getTagInternal(key, false, true);
      if (subTag != null) {
         String actualKey = this.getLastNode(key);
         if (subTag.hasKey(actualKey, 1)) {
            return subTag.getBoolean(actualKey);
         }
      }

      return defaultValue;
   }

   public NBTHelper setPos(String key, BlockPos value) {
      this.getTagInternal(key).setTag(this.getLastNode(key), NBTUtil.createPosTag(value));
      return this;
   }

   @Nullable
   public BlockPos getPos(String key) {
      NBTTagCompound subTag = this.getTagInternal(key, false, true);
      if (subTag != null) {
         String actualKey = this.getLastNode(key);
         if (subTag.hasKey(actualKey, 10)) {
            return NBTUtil.getPosFromTag(this.getTag(actualKey));
         }
      }

      return null;
   }

   public NBTHelper setBlockState(String key, IBlockState value) {
      NBTUtil.writeBlockState(this.getTag(key, true), value);
      return this;
   }

   @Nullable
   public IBlockState getBlockState(String key) {
      NBTTagCompound subTag = this.getTagInternal(key, false, false);
      return subTag != null ? NBTUtil.readBlockState(subTag) : null;
   }

   public NBTHelper setGameProfile(String key, GameProfile value) {
      NBTUtil.writeGameProfile(this.getTag(key, true), value);
      return this;
   }

   @Nullable
   public GameProfile getGameProfile(String key) {
      NBTTagCompound subTag = this.getTagInternal(key, false, false);
      return subTag != null ? NBTUtil.readGameProfileFromNBT(subTag) : null;
   }

   public NBTHelper setString(String key, String value) {
      this.getTagInternal(key).setString(this.getLastNode(key), value);
      return this;
   }

   @Nullable
   public String getString(String key) {
      return this.getString(key, (String)null);
   }

   public String getString(String key, String defaultValue) {
      NBTTagCompound subTag = this.getTagInternal(key, false, true);
      if (subTag != null) {
         String actualKey = this.getLastNode(key);
         if (subTag.hasKey(actualKey, 8)) {
            return subTag.getString(actualKey);
         }
      }

      return defaultValue;
   }

   public NBTHelper setIntArray(String key, int[] value) {
      this.getTagInternal(key).setIntArray(this.getLastNode(key), value);
      return this;
   }

   public int[] getIntArray(String key) {
      NBTTagCompound subTag = this.getTagInternal(key, false, true);
      if (subTag != null) {
         String actualKey = this.getLastNode(key);
         if (subTag.hasKey(actualKey, 11)) {
            return subTag.getIntArray(actualKey);
         }
      }

      return new int[0];
   }

   public NBTHelper setByteArray(String key, byte[] value) {
      this.getTagInternal(key).setByteArray(this.getLastNode(key), value);
      return this;
   }

   public byte[] getByteArray(String key) {
      NBTTagCompound subTag = this.getTagInternal(key, false, true);
      if (subTag != null) {
         String actualKey = this.getLastNode(key);
         if (subTag.hasKey(actualKey, 7)) {
            return subTag.getByteArray(actualKey);
         }
      }

      return new byte[0];
   }

   public NBTHelper setUUID(String key, UUID value) {
      this.getTagInternal(key).setUniqueId(this.getLastNode(key), value);
      return this;
   }

   @Nullable
   public UUID getUUID(String key) {
      NBTTagCompound subTag = this.getTagInternal(key, false, true);
      if (subTag != null) {
         String actualKey = this.getLastNode(key);
         if (!subTag.hasKey(actualKey + "Most", 4) || !subTag.hasKey(actualKey + "Least", 4)) {
            return subTag.getUniqueId(actualKey);
         }
      }

      return null;
   }

   public NBTTagList getTagList(String key, int type) {
      NBTTagCompound subTag = this.getTagInternal(key, false, true);
      if (subTag != null) {
         String actualKey = this.getLastNode(key);
         if (subTag.hasKey(actualKey, 9)) {
            return subTag.getTagList(actualKey, type);
         }
      }

      return null;
   }

   public boolean hasTag(String key, int type) {
      NBTTagCompound subTag = this.getTagInternal(key, false, true);
      if (subTag != null) {
         String actualKey = this.getLastNode(key);
         return subTag.hasKey(actualKey, type);
      } else {
         return false;
      }
   }

   public NBTHelper remove(String key) {
      NBTTagCompound subTag = this.getTagInternal(key, false, true);
      if (subTag != null) {
         String actualKey = this.getLastNode(key);
         subTag.removeTag(actualKey);
      }

      return this;
   }

   @Nullable
   public NBTTagCompound get() {
      return this.tag;
   }

   public ItemStack getItem() {
      return this.stack == null ? ItemStack.EMPTY : this.stack;
   }

   public static NBTHelper of(ItemStack stack) {
      return new NBTHelper(stack.getTagCompound(), stack);
   }

   public static NBTHelper of(NBTTagCompound tag) {
      return new NBTHelper(tag, (ItemStack)null);
   }

   public static NBTHelper create() {
      return new NBTHelper((NBTTagCompound)null, (ItemStack)null);
   }

   public static class Tag {
      public static final int END = 0;
      public static final int BYTE = 1;
      public static final int SHORT = 2;
      public static final int INT = 3;
      public static final int LONG = 4;
      public static final int FLOAT = 5;
      public static final int DOUBLE = 6;
      public static final int BYTE_ARRAY = 7;
      public static final int STRING = 8;
      public static final int LIST = 9;
      public static final int COMPOUND = 10;
      public static final int INT_ARRAY = 11;
      public static final int LONG_ARRAY = 12;
      public static final int ANY_NUMERIC = 99;

      public Tag() {
      }
   }
}
