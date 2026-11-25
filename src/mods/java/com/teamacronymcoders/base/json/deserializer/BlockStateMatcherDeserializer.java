package com.teamacronymcoders.base.json.deserializer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.teamacronymcoders.base.blocks.BlockStateMatcher;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BlockStateMatcherDeserializer implements JsonDeserializer<BlockStateMatcher> {
   public BlockStateMatcherDeserializer() {
   }

   public BlockStateMatcher deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if (json != null && json.isJsonObject()) {
         JsonObject jsonObject = json.getAsJsonObject();
         String blockName = JsonUtils.getString(jsonObject, "block");
         Block block = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
         if (block != null) {
            if (!jsonObject.has("properties")) {
               return new BlockStateMatcher(block);
            }

            BlockStateContainer blockStateContainer = block.getBlockState();
            JsonObject properties = jsonObject.getAsJsonObject("properties");
            Map<IProperty<?>, List<?>> propertyMatching = Maps.newHashMap();

            for(Entry<String, JsonElement> entry : properties.entrySet()) {
               IProperty<?> property = blockStateContainer.getProperty((String)entry.getKey());
               if (property == null) {
                  throw new JsonParseException("Failed to find property: " + (String)entry.getKey());
               }

               List acceptableValues = Lists.newArrayList();
               List<String> acceptableStringValues = Lists.newArrayList();
               JsonElement jsonValues = (JsonElement)entry.getValue();
               if (jsonValues.isJsonArray()) {
                  jsonValues.getAsJsonArray().iterator().forEachRemaining((valuex) -> {
                     acceptableStringValues.add(value.getAsString());
                  });
               } else {
                  acceptableStringValues.add(jsonValues.getAsString());
               }

               for(String value : acceptableStringValues) {
                  property.parseValue(value).toJavaUtil().ifPresent(acceptableValues::add);
               }

               propertyMatching.put(property, acceptableValues);
            }

            return new BlockStateMatcher(block, propertyMatching);
         }
      }

      if (json == null) {
         throw new JsonParseException("Found null value for BlockState Input");
      } else {
         throw new JsonParseException("Failed to deserialize BlockState for: " + json.toString());
      }
   }
}
