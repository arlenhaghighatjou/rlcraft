package crafttweaker.mc1120.data;

import crafttweaker.api.data.DataByte;
import crafttweaker.api.data.DataByteArray;
import crafttweaker.api.data.DataDouble;
import crafttweaker.api.data.DataFloat;
import crafttweaker.api.data.DataInt;
import crafttweaker.api.data.DataIntArray;
import crafttweaker.api.data.DataList;
import crafttweaker.api.data.DataLong;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.DataShort;
import crafttweaker.api.data.DataString;
import crafttweaker.api.data.IData;
import crafttweaker.api.data.IDataConverter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

public class NBTConverter implements IDataConverter<NBTBase> {
   private static final NBTConverter INSTANCE = new NBTConverter();

   public NBTConverter() {
   }

   public static NBTBase from(IData data) {
      return (NBTBase)data.convert(INSTANCE);
   }

   public static IData from(NBTBase nbt, boolean immutable) {
      if (nbt == null) {
         return null;
      } else {
         switch(nbt.getId()) {
         case 1:
            return new DataByte(((NBTPrimitive)nbt).getByte());
         case 2:
            return new DataShort(((NBTPrimitive)nbt).getShort());
         case 3:
            return new DataInt(((NBTPrimitive)nbt).getInt());
         case 4:
            return new DataLong(((NBTPrimitive)nbt).getLong());
         case 5:
            return new DataFloat(((NBTPrimitive)nbt).getFloat());
         case 6:
            return new DataDouble(((NBTPrimitive)nbt).getDouble());
         case 7:
            return new DataByteArray(((NBTTagByteArray)nbt).getByteArray(), immutable);
         case 8:
            return new DataString(((NBTTagString)nbt).getString());
         case 9:
            List<NBTBase> original = ((NBTTagList)nbt).tagList;
            List<IData> values = new ArrayList((Collection)original.stream().map((value) -> {
               return from(value, immutable);
            }).collect(Collectors.toList()));
            return new DataList(values, immutable);
         case 10:
            Map<String, IData> values = new HashMap();
            NBTTagCompound original = (NBTTagCompound)nbt;

            for(String key : original.getKeySet()) {
               values.put(key, from(original.getTag(key), immutable));
            }

            return new DataMap(values, immutable);
         case 11:
            return new DataIntArray(((NBTTagIntArray)nbt).getIntArray(), immutable);
         default:
            throw new RuntimeException("Unknown tag type: " + nbt.getId());
         }
      }
   }

   public static void updateMap(NBTTagCompound nbt, IData data) {
      NBTUpdater updater = new NBTUpdater(nbt);
      data.convert(updater);
   }

   public NBTBase fromBool(boolean value) {
      return new NBTTagInt(value ? 1 : 0);
   }

   public NBTBase fromByte(byte value) {
      return new NBTTagByte(value);
   }

   public NBTBase fromShort(short value) {
      return new NBTTagShort(value);
   }

   public NBTBase fromInt(int value) {
      return new NBTTagInt(value);
   }

   public NBTBase fromLong(long value) {
      return new NBTTagLong(value);
   }

   public NBTBase fromFloat(float value) {
      return new NBTTagFloat(value);
   }

   public NBTBase fromDouble(double value) {
      return new NBTTagDouble(value);
   }

   public NBTBase fromString(String value) {
      return new NBTTagString(value);
   }

   public NBTBase fromList(List<IData> values) {
      NBTTagList result = new NBTTagList();

      for(IData value : values) {
         result.appendTag(from(value));
      }

      return result;
   }

   public NBTBase fromMap(Map<String, IData> values) {
      NBTTagCompound result = new NBTTagCompound();

      for(Entry<String, IData> entry : values.entrySet()) {
         result.setTag((String)entry.getKey(), from((IData)entry.getValue()));
      }

      return result;
   }

   public NBTBase fromByteArray(byte[] value) {
      return new NBTTagByteArray(value);
   }

   public NBTBase fromIntArray(int[] value) {
      return new NBTTagIntArray(value);
   }
}
