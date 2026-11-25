package com.teamacronymcoders.base.json.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BlockStateDeserializer implements JsonDeserializer<IBlockState> {
   public BlockStateDeserializer() {
   }

   public IBlockState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if (json != null && json.isJsonObject()) {
         JsonObject jsonObject = json.getAsJsonObject();
         String blockName = JsonUtils.getString(jsonObject, "block");
         Block block = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
         if (block != null) {
            if (jsonObject.has("properties")) {
               BlockStateContainer blockStateContainer = block.getBlockState();
               IBlockState blockState = block.getDefaultState();
               JsonObject properties = jsonObject.getAsJsonObject("properties");

               for(Entry<String, JsonElement> entry : properties.entrySet()) {
                  IProperty<?> property = blockStateContainer.getProperty((String)entry.getKey());
                  if (property == null) {
                     throw new JsonParseException("Failed to find property: " + (String)entry.getKey());
                  }

                  blockState = setValueHelper(blockState, property, ((JsonElement)entry.getValue()).getAsString());
               }

               return blockState;
            }

            return block.getDefaultState();
         }
      }

      if (json == null) {
         throw new JsonParseException("Found null value for BlockState Input");
      } else {
         throw new JsonParseException("Failed to deserialize BlockState for: " + json.toString());
      }
   }

   private static <T extends Comparable<T>> IBlockState setValueHelper(IBlockState blockState, IProperty<T> property, String stringValue) throws JsonParseException {
      return (IBlockState)property.parseValue(stringValue).toJavaUtil().map((propertyValue) -> {
         return blockState.withProperty(property, propertyValue);
      }).orElseThrow(() -> {
         return new JsonParseException("Failed to find value " + stringValue + " for property " + property.getName());
      });
   }
}
