package com.tmtravlr.qualitytools.config;

import com.google.common.collect.HashMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLLog;

public class QualityEntry {
   public String name;
   public TextFormatting color;
   public int weight;
   public HashMultimap<String, AttributeModifier> attributeMap;

   public QualityEntry() {
   }

   public static class Serializer implements JsonDeserializer<QualityEntry> {
      public Serializer() {
      }

      public QualityEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
         JsonObject json = jsonElement.getAsJsonObject();
         QualityEntry entry = new QualityEntry();
         entry.weight = JsonUtils.getInt(json, "weight", 1);
         if (entry.weight < 1) {
            entry.weight = 1;
         }

         entry.name = JsonUtils.getString(json, "name");
         if (entry.name.equalsIgnoreCase("normal")) {
            return entry;
         } else {
            String colorName = json.has("color") ? JsonUtils.getString(json, "color") : "white";
            entry.color = TextFormatting.getValueByName(colorName);
            if (entry.color == null) {
               FMLLog.warning("[Quality Tools] Unknown formatting color %s in %s. Ignoring and setting to white.", new Object[]{colorName, ConfigLoader.typeDeserializing});
               entry.color = TextFormatting.WHITE;
            }

            entry.attributeMap = HashMultimap.create();
            JsonArray attributes = JsonUtils.getJsonArray(json, "attributes");

            for(int i = 0; i < attributes.size(); ++i) {
               if (attributes.get(i).isJsonObject()) {
                  JsonObject jsonAttribute = attributes.get(i).getAsJsonObject();
                  String attributeName = JsonUtils.getString(jsonAttribute, "name");
                  float amount = JsonUtils.getFloat(jsonAttribute, "amount");
                  int operation = JsonUtils.getInt(jsonAttribute, "operation", 0);
                  AttributeModifier modifier = new AttributeModifier(new EaglercraftUUID(1L, 1L), "qualitytools", (double)amount, operation);
                  entry.attributeMap.put(attributeName, modifier);
               }
            }

            return entry;
         }
      }
   }
}
