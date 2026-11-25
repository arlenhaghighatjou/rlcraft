package ivorius.reccomplex.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import ivorius.reccomplex.utils.ByteArrays;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.bind.DatatypeConverter;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

public class NBTToJson {
   public static final Gson nbtJson = createGson();

   public NBTToJson() {
   }

   public static Gson createGson() {
      GsonBuilder builder = new GsonBuilder();
      registerAllNBTSerializers(builder);
      return builder.create();
   }

   public static StringTypeAdapterFactory<NBTBase> createSafeNBTSerializer() {
      StringTypeAdapterFactory<NBTBase> nbtAdapterFactory = new StringTypeAdapterFactory<NBTBase>("nbt", "nbtType");
      nbtAdapterFactory.register("byteArray", NBTTagByteArray.class, new NBTTagByteArraySerializer());
      nbtAdapterFactory.register("byte", NBTTagByte.class, new NBTTagByteSerializer());
      nbtAdapterFactory.register("compound", NBTTagCompound.class, new NBTTagCompoundSerializer());
      nbtAdapterFactory.register("double", NBTTagDouble.class, new NBTTagDoubleSerializer());
      nbtAdapterFactory.register("end", NBTTagEnd.class, new NBTTagEndSerializer());
      nbtAdapterFactory.register("float", NBTTagFloat.class, new NBTTagFloatSerializer());
      nbtAdapterFactory.register("intArray", NBTTagIntArray.class, new NBTTagIntArraySerializer());
      nbtAdapterFactory.register("int", NBTTagInt.class, new NBTTagIntSerializer());
      nbtAdapterFactory.register("list", NBTTagList.class, new NBTTagListSerializer());
      nbtAdapterFactory.register("long", NBTTagLong.class, new NBTTagLongSerializer());
      nbtAdapterFactory.register("short", NBTTagShort.class, new NBTTagShortSerializer());
      nbtAdapterFactory.register("string", NBTTagString.class, new NBTTagStringSerializer());
      return nbtAdapterFactory;
   }

   public static void registerSafeNBTSerializer(GsonBuilder builder) {
      builder.registerTypeHierarchyAdapter(NBTBase.class, createSafeNBTSerializer());
   }

   public static void registerAllNBTSerializers(GsonBuilder builder) {
      builder.registerTypeAdapter(NBTTagByteArray.class, new NBTTagByteArraySerializer());
      builder.registerTypeAdapter(NBTTagByte.class, new NBTTagByteSerializer());
      builder.registerTypeAdapter(NBTTagCompound.class, new NBTTagCompoundSerializer());
      builder.registerTypeAdapter(NBTTagDouble.class, new NBTTagDoubleSerializer());
      builder.registerTypeAdapter(NBTTagEnd.class, new NBTTagEndSerializer());
      builder.registerTypeAdapter(NBTTagFloat.class, new NBTTagFloatSerializer());
      builder.registerTypeAdapter(NBTTagIntArray.class, new NBTTagIntArraySerializer());
      builder.registerTypeAdapter(NBTTagInt.class, new NBTTagIntSerializer());
      builder.registerTypeAdapter(NBTTagList.class, new NBTTagListSerializer());
      builder.registerTypeAdapter(NBTTagLong.class, new NBTTagLongSerializer());
      builder.registerTypeAdapter(NBTTagShort.class, new NBTTagShortSerializer());
      builder.registerTypeAdapter(NBTTagString.class, new NBTTagStringSerializer());
   }

   public static void registerSmartNBTSerializer(GsonBuilder builder) {
      builder.registerTypeHierarchyAdapter(NBTBase.class, new NBTBaseSerializerSmart());
   }

   public static Class<? extends NBTBase> getNBTTypeSmart(JsonElement element) {
      if (!element.isJsonArray()) {
         if (element.isJsonObject()) {
            return NBTTagCompound.class;
         } else {
            if (element.isJsonPrimitive()) {
               JsonPrimitive primitive = element.getAsJsonPrimitive();
               if (primitive.isString()) {
                  return NBTTagString.class;
               }

               if (primitive.isNumber()) {
                  return NBTPrimitive.class;
               }
            }

            return null;
         }
      } else {
         JsonArray array = element.getAsJsonArray();
         if (array.size() == 0) {
            return NBTTagList.class;
         } else {
            boolean allByte = true;
            boolean allInt = true;

            for(JsonElement arrayElement : array) {
               if (arrayElement.isJsonPrimitive()) {
                  JsonPrimitive primitive = arrayElement.getAsJsonPrimitive();
                  if (!primitive.isNumber() || !(primitive.getAsNumber() instanceof Byte)) {
                     allByte = false;
                  }

                  if (!primitive.isNumber() || !(primitive.getAsNumber() instanceof Integer)) {
                     allInt = false;
                  }
               } else {
                  allByte = false;
                  allInt = false;
               }
            }

            if (allByte) {
               return NBTTagByteArray.class;
            } else if (allInt) {
               return NBTTagIntArray.class;
            } else {
               return NBTTagList.class;
            }
         }
      }
   }

   public static NBTTagCompound getNBTFromBase64(String elementString) {
      byte[] nbtBytes = DatatypeConverter.parseBase64Binary(elementString);

      try {
         return CompressedStreamTools.readCompressed(new ByteArrayInputStream(nbtBytes));
      } catch (IOException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public static String getBase64FromNBT(NBTTagCompound compound) {
      byte[] worldDataByteArray;
      try {
         worldDataByteArray = ByteArrays.toByteArray((s) -> {
            CompressedStreamTools.writeCompressed(compound, s);
         });
      } catch (IOException var3) {
         var3.printStackTrace();
         return null;
      }

      return DatatypeConverter.printBase64Binary(worldDataByteArray);
   }
}
