package com.endertech.minecraft.forge.messages;

import com.endertech.minecraft.forge.configs.ColorARGB;
import com.endertech.minecraft.forge.configs.ForgeNBT;
import com.endertech.minecraft.forge.data.INetSerializable;
import com.endertech.minecraft.forge.math.Vect3d;
import io.netty.buffer.ByteBuf;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class ForgeNetMsg implements IMessage {
   private static final Map<Class<?>, ForgeNetMsg.FieldHandler<?>> fieldHandlers = new HashMap();
   private static final Map<Class<?>, Field[]> fieldCache = new HashMap();

   public ForgeNetMsg() {
   }

   public final void fromBytes(ByteBuf buf) {
      try {
         Field[] classFields = getClassFields(this.getClass());

         for(Field field : classFields) {
            if (this.acceptField(field)) {
               this.readField(field, buf);
            }
         }

      } catch (Exception var7) {
         throw new RuntimeException("Error at reading packet " + this, var7);
      }
   }

   public final void toBytes(ByteBuf buf) {
      try {
         Field[] classFields = getClassFields(this.getClass());

         for(Field field : classFields) {
            if (this.acceptField(field)) {
               this.writeField(field, buf);
            }
         }

      } catch (Exception var7) {
         throw new RuntimeException("Error at writing packet " + this, var7);
      }
   }

   private static Field[] getClassFields(Class<?> clazz) {
      Field[] fields = (Field[])fieldCache.get(clazz);
      if (fields == null) {
         fields = clazz.getFields();
         Arrays.sort(fields, (f1, f2) -> {
            return f1.getName().compareTo(f2.getName());
         });
         fieldCache.put(clazz, fields);
      }

      return fields;
   }

   private final void writeField(Field field, ByteBuf buf) throws IllegalArgumentException, IllegalAccessException {
      Class<?> type = field.getType();
      Object value = field.get(this);
      if (INetSerializable.class.isAssignableFrom(type)) {
         ((INetSerializable)value).writeTo(buf);
      } else if (type.isEnum()) {
         int ordinal = ((Enum)value).ordinal();
         buf.writeInt(ordinal);
      } else {
         ForgeNetMsg.FieldHandler<Object> handler = getFieldHandler(type);
         handler.writer.write(value, buf);
      }

   }

   private final void readField(Field field, ByteBuf buf) throws IllegalArgumentException, IllegalAccessException {
      Class<?> type = field.getType();
      Object value = field.get(this);
      if (INetSerializable.class.isAssignableFrom(type)) {
         value = ((INetSerializable)value).readFrom(buf);
      } else if (type.isEnum()) {
         int ordinal = buf.readInt();
         value = type.getEnumConstants()[ordinal];
      } else {
         ForgeNetMsg.FieldHandler<?> handler = getFieldHandler(type);
         value = handler.reader.read(buf);
      }

      field.set(this, value);
   }

   private static ForgeNetMsg.FieldHandler<?> getFieldHandler(Class<?> clazz) {
      ForgeNetMsg.FieldHandler<?> handler = (ForgeNetMsg.FieldHandler)fieldHandlers.get(clazz);
      if (handler == null) {
         throw new RuntimeException("No R/W handler for  " + clazz);
      } else {
         return handler;
      }
   }

   private boolean acceptField(Field field) throws IllegalArgumentException, IllegalAccessException {
      Class<?> type = field.getType();
      int mods = field.getModifiers();
      if (!Modifier.isFinal(mods) && !Modifier.isStatic(mods) && !Modifier.isTransient(mods)) {
         if (INetSerializable.class.isAssignableFrom(type)) {
            return true;
         } else if (type.isEnum()) {
            return true;
         } else {
            return fieldHandlers.containsKey(type);
         }
      } else {
         return false;
      }
   }

   public static <T> void addFieldHandler(Class<T> type, ForgeNetMsg.IReader<T> reader, ForgeNetMsg.IWriter<T> writer) {
      fieldHandlers.put(type, new ForgeNetMsg.FieldHandler(reader, writer));
   }

   static {
      addFieldHandler(Byte.TYPE, (buf) -> {
         return buf.readByte();
      }, (value, buf) -> {
         buf.writeByte(value.byteValue());
      });
      addFieldHandler(Short.TYPE, (buf) -> {
         return buf.readShort();
      }, (value, buf) -> {
         buf.writeShort(value.shortValue());
      });
      addFieldHandler(Integer.TYPE, (buf) -> {
         return buf.readInt();
      }, (value, buf) -> {
         buf.writeInt(value.intValue());
      });
      addFieldHandler(Long.TYPE, (buf) -> {
         return buf.readLong();
      }, (value, buf) -> {
         buf.writeLong(value.longValue());
      });
      addFieldHandler(Float.TYPE, (buf) -> {
         return buf.readFloat();
      }, (value, buf) -> {
         buf.writeFloat(value.floatValue());
      });
      addFieldHandler(Double.TYPE, (buf) -> {
         return buf.readDouble();
      }, (value, buf) -> {
         buf.writeDouble(value.doubleValue());
      });
      addFieldHandler(Boolean.TYPE, (buf) -> {
         return buf.readBoolean();
      }, (value, buf) -> {
         buf.writeBoolean(value.booleanValue());
      });
      addFieldHandler(Character.TYPE, (buf) -> {
         return buf.readChar();
      }, (value, buf) -> {
         buf.writeChar(value.charValue());
      });
      addFieldHandler(String.class, (buf) -> {
         return ByteBufUtils.readUTF8String(buf);
      }, (string, buf) -> {
         ByteBufUtils.writeUTF8String(buf, string);
      });
      addFieldHandler(NBTTagCompound.class, (buf) -> {
         return ForgeNBT.readFromByteBuf(buf);
      }, (compound, buf) -> {
         ForgeNBT.writeToByteBuf(compound, buf);
      });
      addFieldHandler(ItemStack.class, (buf) -> {
         return ByteBufUtils.readItemStack(buf);
      }, (stack, buf) -> {
         ByteBufUtils.writeItemStack(buf, stack);
      });
      addFieldHandler(BlockPos.class, (buf) -> {
         return BlockPos.fromLong(buf.readLong());
      }, (pos, buf) -> {
         buf.writeLong(pos.toLong());
      });
      addFieldHandler(Vect3d.class, (buf) -> {
         return Vect3d.readFromByteBuf(buf);
      }, (vec, buf) -> {
         Vect3d.writeToByteBuf(vec, buf);
      });
      addFieldHandler(Block.class, (buf) -> {
         return Block.getBlockById(buf.readInt());
      }, (block, buf) -> {
         buf.writeInt(Block.getIdFromBlock(block));
      });
      addFieldHandler(ColorARGB.class, (buf) -> {
         return ColorARGB.from(buf.readInt());
      }, (color, buf) -> {
         buf.writeInt(color.getARGB());
      });
      addFieldHandler(Enum.class, (ForgeNetMsg.IReader)null, (value, buf) -> {
         buf.writeInt(value.ordinal());
      });
   }

   private static class FieldHandler<T> {
      public final ForgeNetMsg.IReader<T> reader;
      public final ForgeNetMsg.IWriter<T> writer;

      public FieldHandler(ForgeNetMsg.IReader<T> reader, ForgeNetMsg.IWriter<T> writer) {
         this.reader = reader;
         this.writer = writer;
      }
   }

   public interface IReader<T> {
      T read(ByteBuf var1);
   }

   public interface IWriter<T> {
      void write(T var1, ByteBuf var2);
   }
}
