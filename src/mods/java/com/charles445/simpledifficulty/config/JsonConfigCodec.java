package com.charles445.simpledifficulty.config;

import com.charles445.simpledifficulty.api.config.json.JsonConsumableTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableThirst;
import com.charles445.simpledifficulty.api.config.json.JsonItemIdentity;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonTemperatureIdentity;
import com.charles445.simpledifficulty.api.config.json.migrate.JsonConsumableTemperatureMigrate;
import com.charles445.simpledifficulty.api.config.json.migrate.JsonConsumableThirstMigrate;
import com.charles445.simpledifficulty.api.config.json.migrate.JsonTemperatureMetadataMigrate;
import com.charles445.simpledifficulty.config.json.ExtraItem;
import com.charles445.simpledifficulty.config.json.MaterialTemperature;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class JsonConfigCodec {
   public JsonConfigCodec() {
   }

   public static Object fromJson(JsonFileName jfn, JsonElement json) {
      if (json == null || json.isJsonNull()) {
         return null;
      } else {
         switch(jfn) {
         case armorTemperatures:
         case heldItemTemperatures:
            return readListMap(json, JsonConfigCodec::readTemperatureIdentity);
         case blockTemperatures:
            return readListMap(json, JsonConfigCodec::readPropertyTemperature);
         case consumableTemperature:
            return readListMap(json, JsonConfigCodec::readConsumableTemperature);
         case consumableThirst:
            return readListMap(json, JsonConfigCodec::readConsumableThirst);
         case dimensionTemperature:
         case fluidTemperatures:
         case armorTemperatures_MIGRATE:
            return readMap(json, JsonConfigCodec::readTemperature);
         case materialTemperature:
            return readMaterialTemperature(json.getAsJsonObject());
         case extraItems:
            return readMap(json, JsonConfigCodec::readExtraItem);
         case consumableTemperature_MIGRATE:
            return readListMap(json, JsonConfigCodec::readConsumableTemperatureMigrate);
         case consumableThirst_MIGRATE:
            return readListMap(json, JsonConfigCodec::readConsumableThirstMigrate);
         case heldItemTemperatures_MIGRATE:
            return readListMap(json, JsonConfigCodec::readTemperatureMetadataMigrate);
         default:
            return null;
         }
      }
   }

   public static JsonElement toJson(JsonFileName jfn, Object container) {
      if (container == null) {
         return JsonNull.INSTANCE;
      } else {
         switch(jfn) {
         case armorTemperatures:
         case heldItemTemperatures:
            return writeListMap((Map)container, JsonConfigCodec::writeTemperatureIdentity);
         case blockTemperatures:
            return writeListMap((Map)container, JsonConfigCodec::writePropertyTemperature);
         case consumableTemperature:
            return writeListMap((Map)container, JsonConfigCodec::writeConsumableTemperature);
         case consumableThirst:
            return writeListMap((Map)container, JsonConfigCodec::writeConsumableThirst);
         case dimensionTemperature:
         case fluidTemperatures:
         case armorTemperatures_MIGRATE:
            return writeMap((Map)container, JsonConfigCodec::writeTemperature);
         case materialTemperature:
            return writeMaterialTemperature((MaterialTemperature)container);
         case extraItems:
            return writeMap((Map)container, JsonConfigCodec::writeExtraItem);
         case consumableTemperature_MIGRATE:
            return writeListMap((Map)container, JsonConfigCodec::writeConsumableTemperatureMigrate);
         case consumableThirst_MIGRATE:
            return writeListMap((Map)container, JsonConfigCodec::writeConsumableThirstMigrate);
         case heldItemTemperatures_MIGRATE:
            return writeListMap((Map)container, JsonConfigCodec::writeTemperatureMetadataMigrate);
         default:
            return JsonNull.INSTANCE;
         }
      }
   }

   private static <T> Map<String, List<T>> readListMap(JsonElement json, Function<JsonObject, T> reader) {
      Map<String, List<T>> map = new LinkedHashMap();

      for(Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
         JsonElement value = entry.getValue();
         if (value.isJsonNull()) {
            map.put(entry.getKey(), null);
         } else {
            List<T> list = new ArrayList();

            for(JsonElement element : value.getAsJsonArray()) {
               list.add(element.isJsonNull() ? null : reader.apply(element.getAsJsonObject()));
            }

            map.put(entry.getKey(), list);
         }
      }

      return map;
   }

   private static <T> Map<String, T> readMap(JsonElement json, Function<JsonObject, T> reader) {
      Map<String, T> map = new LinkedHashMap();

      for(Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
         JsonElement value = entry.getValue();
         map.put(entry.getKey(), value.isJsonNull() ? null : reader.apply(value.getAsJsonObject()));
      }

      return map;
   }

   private static <T> JsonObject writeListMap(Map<String, List<T>> map, Function<T, JsonObject> writer) {
      JsonObject json = new JsonObject();

      for(Entry<String, List<T>> entry : map.entrySet()) {
         if (entry.getValue() != null) {
            JsonArray array = new JsonArray();

            for(T value : entry.getValue()) {
               array.add((JsonElement)(value == null ? JsonNull.INSTANCE : writer.apply(value)));
            }

            json.add(entry.getKey(), array);
         }
      }

      return json;
   }

   private static <T> JsonObject writeMap(Map<String, T> map, Function<T, JsonObject> writer) {
      JsonObject json = new JsonObject();

      for(Entry<String, T> entry : map.entrySet()) {
         if (entry.getValue() != null) {
            json.add(entry.getKey(), writer.apply(entry.getValue()));
         }
      }

      return json;
   }

   private static boolean present(JsonObject json, String key) {
      return json.has(key) && !json.get(key).isJsonNull();
   }

   private static int readInt(JsonObject json, String key) {
      return present(json, key) ? json.get(key).getAsInt() : 0;
   }

   private static float readFloat(JsonObject json, String key) {
      return present(json, key) ? json.get(key).getAsFloat() : 0.0F;
   }

   private static boolean readBoolean(JsonObject json, String key) {
      return present(json, key) && json.get(key).getAsBoolean();
   }

   private static String readString(JsonObject json, String key) {
      return present(json, key) ? json.get(key).getAsString() : null;
   }

   private static Map<String, String> readStringMap(JsonObject json, String key) {
      if (!present(json, key)) {
         return null;
      } else {
         Map<String, String> map = new LinkedHashMap();

         for(Entry<String, JsonElement> entry : json.getAsJsonObject(key).entrySet()) {
            map.put(entry.getKey(), entry.getValue().isJsonNull() ? null : entry.getValue().getAsString());
         }

         return map;
      }
   }

   private static void writeStringMap(JsonObject json, String key, Map<String, String> map) {
      if (map != null) {
         JsonObject object = new JsonObject();

         for(Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue() != null) {
               object.addProperty(entry.getKey(), entry.getValue());
            }
         }

         json.add(key, object);
      }
   }

   private static JsonItemIdentity readIdentity(JsonObject json, String key) {
      if (!present(json, key)) {
         return null;
      } else {
         JsonObject object = json.getAsJsonObject(key);
         return new JsonItemIdentity(readInt(object, "metadata"), readString(object, "nbt"));
      }
   }

   private static void writeIdentity(JsonObject json, String key, JsonItemIdentity identity) {
      if (identity != null) {
         JsonObject object = new JsonObject();
         object.addProperty("metadata", identity.metadata);
         if (identity.nbt != null) {
            object.addProperty("nbt", identity.nbt);
         }

         json.add(key, object);
      }
   }

   private static JsonTemperatureIdentity readTemperatureIdentity(JsonObject json) {
      JsonTemperatureIdentity value = new JsonTemperatureIdentity(readFloat(json, "temperature"), (JsonItemIdentity)null);
      value.identity = readIdentity(json, "identity");
      return value;
   }

   private static JsonObject writeTemperatureIdentity(JsonTemperatureIdentity value) {
      JsonObject json = new JsonObject();
      writeIdentity(json, "identity", value.identity);
      json.addProperty("temperature", value.temperature);
      return json;
   }

   private static JsonPropertyTemperature readPropertyTemperature(JsonObject json) {
      JsonPropertyTemperature value = new JsonPropertyTemperature(readFloat(json, "temperature"));
      value.properties = readStringMap(json, "properties");
      return value;
   }

   private static JsonObject writePropertyTemperature(JsonPropertyTemperature value) {
      JsonObject json = new JsonObject();
      writeStringMap(json, "properties", value.properties);
      json.addProperty("temperature", value.temperature);
      return json;
   }

   private static JsonConsumableTemperature readConsumableTemperature(JsonObject json) {
      JsonConsumableTemperature value = new JsonConsumableTemperature(readString(json, "group"), readFloat(json, "temperature"), readInt(json, "duration"), (JsonItemIdentity)null);
      value.identity = readIdentity(json, "identity");
      return value;
   }

   private static JsonObject writeConsumableTemperature(JsonConsumableTemperature value) {
      JsonObject json = new JsonObject();
      writeIdentity(json, "identity", value.identity);
      if (value.group != null) {
         json.addProperty("group", value.group);
      }

      json.addProperty("temperature", value.temperature);
      json.addProperty("duration", value.duration);
      return json;
   }

   private static JsonConsumableThirst readConsumableThirst(JsonObject json) {
      JsonConsumableThirst value = new JsonConsumableThirst(readInt(json, "amount"), readFloat(json, "saturation"), readFloat(json, "thirstyChance"), (JsonItemIdentity)null);
      value.identity = readIdentity(json, "identity");
      return value;
   }

   private static JsonObject writeConsumableThirst(JsonConsumableThirst value) {
      JsonObject json = new JsonObject();
      writeIdentity(json, "identity", value.identity);
      json.addProperty("amount", value.amount);
      json.addProperty("saturation", value.saturation);
      json.addProperty("thirstyChance", value.thirstyChance);
      return json;
   }

   private static JsonTemperature readTemperature(JsonObject json) {
      return new JsonTemperature(readFloat(json, "temperature"));
   }

   private static JsonObject writeTemperature(JsonTemperature value) {
      JsonObject json = new JsonObject();
      json.addProperty("temperature", value.temperature);
      return json;
   }

   private static MaterialTemperature readMaterialTemperature(JsonObject json) {
      MaterialTemperature value = new MaterialTemperature();
      if (json.has("_comment")) {
         value._comment = readString(json, "_comment");
      }

      if (present(json, "fire")) {
         value.fire = json.get("fire").getAsFloat();
      }

      return value;
   }

   private static JsonObject writeMaterialTemperature(MaterialTemperature value) {
      JsonObject json = new JsonObject();
      if (value._comment != null) {
         json.addProperty("_comment", value._comment);
      }

      json.addProperty("fire", value.fire);
      return json;
   }

   private static ExtraItem readExtraItem(JsonObject json) {
      ExtraItem value = new ExtraItem(readString(json, "_comment"), readBoolean(json, "enabled"));
      value.settings = readStringMap(json, "settings");
      return value;
   }

   private static JsonObject writeExtraItem(ExtraItem value) {
      JsonObject json = new JsonObject();
      if (value._comment != null) {
         json.addProperty("_comment", value._comment);
      }

      json.addProperty("enabled", value.enabled);
      writeStringMap(json, "settings", value.settings);
      return json;
   }

   private static JsonConsumableTemperatureMigrate readConsumableTemperatureMigrate(JsonObject json) {
      return new JsonConsumableTemperatureMigrate(readString(json, "group"), readFloat(json, "temperature"), readInt(json, "metadata"), readInt(json, "duration"));
   }

   private static JsonObject writeConsumableTemperatureMigrate(JsonConsumableTemperatureMigrate value) {
      JsonObject json = new JsonObject();
      json.addProperty("metadata", value.metadata);
      if (value.group != null) {
         json.addProperty("group", value.group);
      }

      json.addProperty("temperature", value.temperature);
      json.addProperty("duration", value.duration);
      return json;
   }

   private static JsonConsumableThirstMigrate readConsumableThirstMigrate(JsonObject json) {
      return new JsonConsumableThirstMigrate(readInt(json, "metadata"), readInt(json, "amount"), readFloat(json, "saturation"), readFloat(json, "thirstyChance"));
   }

   private static JsonObject writeConsumableThirstMigrate(JsonConsumableThirstMigrate value) {
      JsonObject json = new JsonObject();
      json.addProperty("metadata", value.metadata);
      json.addProperty("amount", value.amount);
      json.addProperty("saturation", value.saturation);
      json.addProperty("thirstyChance", value.thirstyChance);
      return json;
   }

   private static JsonTemperatureMetadataMigrate readTemperatureMetadataMigrate(JsonObject json) {
      return new JsonTemperatureMetadataMigrate(readInt(json, "metadata"), readFloat(json, "temperature"));
   }

   private static JsonObject writeTemperatureMetadataMigrate(JsonTemperatureMetadataMigrate value) {
      JsonObject json = new JsonObject();
      json.addProperty("metadata", value.metadata);
      json.addProperty("temperature", value.temperature);
      return json;
   }
}
