package com.teamacronymcoders.base.json.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;

public class EntityClassDeserializer implements JsonDeserializer<Class<? extends Entity>> {
   public EntityClassDeserializer() {
   }

   public Class<? extends Entity> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if (json != null) {
         return EntityList.getClass(new ResourceLocation(json.getAsString()));
      } else {
         throw new JsonParseException("Found null value for Entity");
      }
   }
}
