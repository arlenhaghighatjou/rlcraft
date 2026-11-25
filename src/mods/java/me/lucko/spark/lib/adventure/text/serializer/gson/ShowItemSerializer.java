package me.lucko.spark.lib.adventure.text.serializer.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.nbt.api.BinaryTagHolder;
import me.lucko.spark.lib.adventure.text.event.HoverEvent;

final class ShowItemSerializer implements JsonDeserializer<HoverEvent.ShowItem>, JsonSerializer<HoverEvent.ShowItem> {
   static final String ID = "id";
   static final String COUNT = "count";
   static final String TAG = "tag";

   ShowItemSerializer() {
   }

   public HoverEvent.ShowItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = json.getAsJsonObject();
      if (!object.has("id")) {
         throw new JsonParseException("Not sure how to deserialize show_item hover event");
      } else {
         Key id = (Key)context.deserialize(object.getAsJsonPrimitive("id"), Key.class);
         int count = 1;
         if (object.has("count")) {
            count = object.get("count").getAsInt();
         }

         BinaryTagHolder nbt = null;
         if (object.has("tag")) {
            JsonElement tag = object.get("tag");
            if (tag.isJsonPrimitive()) {
               nbt = BinaryTagHolder.of(tag.getAsString());
            } else if (!tag.isJsonNull()) {
               throw new JsonParseException("Expected tag to be a string");
            }
         }

         return HoverEvent.ShowItem.of(id, count, nbt);
      }
   }

   public JsonElement serialize(HoverEvent.ShowItem src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject json = new JsonObject();
      json.add("id", context.serialize(src.item()));
      int count = src.count();
      if (count != 1) {
         json.addProperty("count", Integer.valueOf(count));
      }

      BinaryTagHolder nbt = src.nbt();
      if (nbt != null) {
         json.addProperty("tag", nbt.string());
      }

      return json;
   }
}
