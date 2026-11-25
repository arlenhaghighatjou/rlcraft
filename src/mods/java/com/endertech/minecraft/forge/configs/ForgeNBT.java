package com.endertech.minecraft.forge.configs;

import com.endertech.common.CommonString;
import com.endertech.minecraft.forge.math.Vect3d;
import io.netty.buffer.ByteBuf;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public final class ForgeNBT {
   public ForgeNBT() {
   }

   public static boolean hasCompoundTag(NBTTagCompound compound, String tagName) {
      return compound.hasKey(tagName, ForgeNBT.Types.COMPOUND.id);
   }

   @Nullable
   public static NBTTagCompound getNestedCompound(NBTTagCompound compound, String path) {
      if (path.isEmpty()) {
         return compound;
      } else {
         String[] split = path.split("/");

         for(String key : split) {
            NBTBase tag = compound.getTag(key);
            if (!(tag instanceof NBTTagCompound)) {
               return null;
            }

            compound = (NBTTagCompound)tag;
         }

         return compound;
      }
   }

   @Nullable
   public static NBTBase getTag(NBTTagCompound compound, ForgeNBT.KeyPathPair pair) {
      compound = getNestedCompound(compound, pair.path);
      return compound != null ? compound.getTag(pair.key) : null;
   }

   public static ForgeNBT.Types getType(NBTTagCompound compound, String key) {
      if (compound != null) {
         int id = compound.getTagId(key);

         for(ForgeNBT.Types type : ForgeNBT.Types.values()) {
            if (type.id == id) {
               return type;
            }
         }
      }

      return ForgeNBT.Types.NULL;
   }

   public static void writeUUID(NBTTagCompound compound, String key, UUID uuid) {
      if (compound != null) {
         NBTTagCompound tag = new NBTTagCompound();
         tag.setLong(ForgeNBT.UUIDKeys.m.name(), uuid.getMostSignificantBits());
         tag.setLong(ForgeNBT.UUIDKeys.l.name(), uuid.getLeastSignificantBits());
         compound.setTag(key, tag);
      }

   }

   public static UUID readUUID(NBTTagCompound compound, String key) {
      NBTTagCompound tag = compound.getCompoundTag(key);
      return new UUID(tag.getLong(ForgeNBT.UUIDKeys.m.name()), tag.getLong(ForgeNBT.UUIDKeys.l.name()));
   }

   public static void writeVec3d(NBTTagCompound compound, String key, Vect3d vec) {
      if (compound != null && vec != null) {
         NBTTagCompound tag = new NBTTagCompound();
         tag.setDouble(ForgeNBT.Vec3Keys.x.name(), vec.x);
         tag.setDouble(ForgeNBT.Vec3Keys.y.name(), vec.y);
         tag.setDouble(ForgeNBT.Vec3Keys.z.name(), vec.z);
         compound.setTag(key, tag);
      }

   }

   public static Vect3d readVec3d(NBTTagCompound compound, String key) {
      NBTTagCompound tag = compound.getCompoundTag(key);
      return Vect3d.from(tag.getDouble(ForgeNBT.Vec3Keys.x.name()), tag.getDouble(ForgeNBT.Vec3Keys.y.name()), tag.getDouble(ForgeNBT.Vec3Keys.z.name()));
   }

   public static void writeBlockPos(NBTTagCompound compound, String key, BlockPos pos) {
      if (compound != null && pos != null) {
         NBTTagCompound tag = new NBTTagCompound();
         tag.setInteger(ForgeNBT.Vec3Keys.x.name(), pos.getX());
         tag.setInteger(ForgeNBT.Vec3Keys.y.name(), pos.getY());
         tag.setInteger(ForgeNBT.Vec3Keys.z.name(), pos.getZ());
         compound.setTag(key, tag);
      }

   }

   public static NBTTagCompound readFromByteBuf(ByteBuf buf) {
      return ByteBufUtils.readTag(buf);
   }

   public static void writeToByteBuf(NBTTagCompound compound, ByteBuf buf) {
      ByteBufUtils.writeTag(buf, compound);
   }

   public static BlockPos readBlockPos(NBTTagCompound compound, String key) {
      NBTTagCompound tag = compound.getCompoundTag(key);
      return new BlockPos(tag.getInteger(ForgeNBT.Vec3Keys.x.name()), tag.getInteger(ForgeNBT.Vec3Keys.y.name()), tag.getInteger(ForgeNBT.Vec3Keys.z.name()));
   }

   @Nullable
   public static <T extends Enum<T>> T readEnumValue(NBTTagCompound compound, Class<T> enumClass) {
      if (compound != null && enumClass != null) {
         String name = compound.getString(enumClass.getSimpleName());
         return (T)Enum.valueOf(enumClass, name);
      } else {
         return (T)null;
      }
   }

   public static <T extends Enum<T>> void writeEnumValue(NBTTagCompound compound, T enumValue) {
      if (compound != null && enumValue != null) {
         compound.setString(enumValue.getClass().getSimpleName(), enumValue.name());
      }

   }

   public static String[] readStringArray(NBTTagCompound compound, String key) {
      NBTTagList tagList = compound.getTagList(key, ForgeNBT.Types.STRING.id);
      int count = tagList.tagCount();
      String[] strings = new String[count];

      for(int i = 0; i < count; ++i) {
         strings[i] = tagList.getStringTagAt(i);
      }

      return strings;
   }

   public static void writeStringArray(NBTTagCompound compound, String key, String... strings) {
      NBTTagList tagList = new NBTTagList();

      for(String str : strings) {
         NBTTagString defTag = new NBTTagString(str);
         tagList.appendTag(defTag);
      }

      compound.setTag(key, tagList);
   }

   public static class KeyPathPair {
      public final String path;
      public final String key;

      public KeyPathPair(String path, String key) {
         this.path = path;
         this.key = key;
      }

      public static ForgeNBT.KeyPathPair from(String str) {
         int pos = str.lastIndexOf("/");
         return pos < 0 ? new ForgeNBT.KeyPathPair("", str) : new ForgeNBT.KeyPathPair(str.substring(0, pos), str.substring(pos + 1));
      }
   }

   public static enum Types {
      NULL(0),
      BYTE(1),
      SHORT(2),
      INT(3),
      LONG(4),
      FLOAT(5),
      DOUBLE(6),
      BYTE$$(7),
      STRING(8),
      LIST(9),
      COMPOUND(10),
      INT$$(11),
      ORDINAL(99);

      public final int id;

      private Types(int id) {
         this.id = id;
      }

      public boolean isOrdinal() {
         return this.id == 99 || this.id >= 1 && this.id <= 6;
      }

      public String toString() {
         String string = this.name().toLowerCase(Locale.ROOT);
         return this.isOrdinal() ? string : CommonString.capitalizeFirstChar(string).replace("$$", "[]");
      }
   }

   static enum UUIDKeys {
      m,
      l;

      private UUIDKeys() {
      }
   }

   static enum Vec3Keys {
      x,
      y,
      z;

      private Vec3Keys() {
      }
   }
}
