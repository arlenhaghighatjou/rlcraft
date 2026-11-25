package me.lucko.spark.lib.adventure.text.serializer.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.UUID;
import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.event.HoverEvent;

final class ShowEntitySerializer implements JsonDeserializer<HoverEvent.ShowEntity>, JsonSerializer<HoverEvent.ShowEntity> {
   static final String TYPE = "type";
   static final String ID = "id";
   static final String NAME = "name";

   ShowEntitySerializer() {
   }

   public HoverEvent.ShowEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = json.getAsJsonObject();
      if (object.has("type") && object.has("id")) {
         Key type = (Key)context.deserialize(object.getAsJsonPrimitive("type"), Key.class);
         UUID id = UUID.fromString(object.getAsJsonPrimitive("id").getAsString());
         Component name = null;
         if (object.has("name")) {
            name = (Component)context.deserialize(object.get("name"), Component.class);
         }

         return HoverEvent.ShowEntity.of(type, id, name);
      } else {
         throw new JsonParseException("A show entity hover event needs type and id fields to be deserialized");
      }
   }

   public JsonElement serialize(HoverEvent.ShowEntity src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject json = new JsonObject();
      json.add("type", context.serialize(src.type()));
      json.addProperty("id", src.id().toString());
      Component name = src.name();
      if (name != null) {
         json.add("name", context.serialize(name));
      }

      return json;
   }
}
