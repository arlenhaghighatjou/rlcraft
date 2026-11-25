package com.teamacronymcoders.base.json.deserializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Type;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLongArray;

public class NBTDeserializer implements JsonDeserializer<NBTTagCompound> {
   public NBTDeserializer() {
   }

   public NBTTagCompound deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if (json.isJsonObject()) {
         return this.parseObject(json.getAsJsonObject());
      } else {
         throw new JsonParseException("NBT is required to be an object");
      }
   }

   private NBTTagCompound parseObject(JsonObject json) {
      NBTTagCompound tagCompound = new NBTTagCompound();

      for(Entry<String, JsonElement> property : json.entrySet()) {
         String name = (String)property.getKey();
         JsonElement jsonElement = (JsonElement)property.getValue();
         if (jsonElement.isJsonObject()) {
            tagCompound.setTag(name, this.parseObject(json));
         } else if (json.isJsonArray()) {
            this.handleJsonArray(tagCompound, name, jsonElement.getAsJsonArray());
         } else if (json.isJsonPrimitive()) {
            this.handleJsonPrimitive(tagCompound, name, jsonElement.getAsJsonPrimitive());
         }
      }

      return tagCompound;
   }

   private void handleJsonPrimitive(NBTTagCompound tagCompound, String propertyName, JsonPrimitive jsonPrimitive) {
      if (jsonPrimitive.isBoolean()) {
         tagCompound.setBoolean(propertyName, jsonPrimitive.getAsBoolean());
      } else if (jsonPrimitive.isString()) {
         tagCompound.setString(propertyName, jsonPrimitive.getAsString());
      } else if (jsonPrimitive.isNumber()) {
         Number number = jsonPrimitive.getAsNumber();
         if (number instanceof Long) {
            tagCompound.setLong(propertyName, number.longValue());
         } else if (number instanceof Integer) {
            tagCompound.setInteger(propertyName, number.intValue());
         } else if (number instanceof Short) {
            tagCompound.setShort(propertyName, number.shortValue());
         } else if (number instanceof Double) {
            tagCompound.setDouble(propertyName, number.doubleValue());
         } else if (number instanceof Byte) {
            tagCompound.setByte(propertyName, number.byteValue());
         } else if (number instanceof Float) {
            tagCompound.setFloat(propertyName, number.floatValue());
         }
      }

   }

   private void handleJsonArray(NBTTagCompound tagCompound, String propertyName, JsonArray jsonArray) {
      boolean allLongs = true;
      boolean allInts = true;
      boolean allBytes = true;

      for(JsonElement element : jsonArray) {
         if (!element.isJsonPrimitive()) {
            throw new JsonParseException("Only Longs, Ints, and Bytes are supported in NBT Arrays");
         }

         JsonPrimitive primitive = element.getAsJsonPrimitive();
         if (!primitive.isNumber()) {
            throw new JsonParseException("Only Longs, Ints, and Bytes are supported in NBT Arrays");
         }

         Number number = primitive.getAsNumber();
         if (number instanceof Byte) {
            allLongs = false;
            allInts = false;
         } else if (number instanceof Integer) {
            allLongs = false;
            allBytes = false;
         } else {
            if (!(number instanceof Long)) {
               throw new JsonParseException("Only Longs, Ints, and Bytes are supported in NBT Arrays");
            }

            allInts = false;
            allBytes = false;
         }
      }

      if (allLongs) {
         this.handleJsonLongArray(tagCompound, propertyName, jsonArray);
      } else if (allInts) {
         this.handleJsonIntArray(tagCompound, propertyName, jsonArray);
      } else {
         if (!allBytes) {
            throw new JsonParseException("NBT Arrays must all be the Same Type");
         }

         this.handleJsonByteArray(tagCompound, propertyName, jsonArray);
      }

   }

   private void handleJsonByteArray(NBTTagCompound tagCompound, String propertyName, JsonArray jsonArray) {
      byte[] byteArray = new byte[jsonArray.size()];

      for(int i = 0; i < jsonArray.size(); ++i) {
         byteArray[i] = jsonArray.get(i).getAsJsonPrimitive().getAsByte();
      }

      tagCompound.setByteArray(propertyName, byteArray);
   }

   private void handleJsonIntArray(NBTTagCompound tagCompound, String propertyName, JsonArray jsonArray) {
      int[] intArray = new int[jsonArray.size()];

      for(int i = 0; i < jsonArray.size(); ++i) {
         intArray[i] = jsonArray.get(i).getAsJsonPrimitive().getAsInt();
      }

      tagCompound.setIntArray(propertyName, intArray);
   }

   private void handleJsonLongArray(NBTTagCompound tagCompound, String propertyName, JsonArray jsonArray) {
      long[] longArray = new long[jsonArray.size()];

      for(int i = 0; i < jsonArray.size(); ++i) {
         longArray[i] = jsonArray.get(i).getAsJsonPrimitive().getAsLong();
      }

      tagCompound.setTag(propertyName, new NBTTagLongArray(longArray));
   }
}
