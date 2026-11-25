package com.yungnickyoung.minecraft.bettermineshafts.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.yungnickyoung.minecraft.bettermineshafts.BetterMineshafts;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.IStringSerializable;

public class BlockStateAdapter extends TypeAdapter<IBlockState> {
   public BlockStateAdapter() {
   }

   public IBlockState read(JsonReader reader) throws IOException {
      if (reader.peek() == JsonToken.NULL) {
         reader.nextNull();
         return null;
      } else {
         Map<String, String> properties = new HashMap();
         String fullString = reader.nextString();
         String blockString = fullString;
         int startIndex = fullString.indexOf(91);
         int stopIndex = fullString.indexOf(93);
         if (startIndex != -1) {
            blockString = fullString.substring(0, startIndex);
            if (stopIndex < startIndex) {
               BetterMineshafts.LOGGER.error("variants.json: Malformed property {}. Missing a bracket?", fullString);
               BetterMineshafts.LOGGER.error("Using air instead...");
               return Blocks.AIR.getDefaultState();
            }

            int index = startIndex + 1;
            String currKey = "";

            for(StringBuilder currString = new StringBuilder(); index <= stopIndex; ++index) {
               char currChar = fullString.charAt(index);
               if (currChar == '=') {
                  currKey = currString.toString();
                  currString = new StringBuilder();
               } else if (currChar != ',' && currChar != ']') {
                  currString.append(fullString.charAt(index));
               } else {
                  properties.put(currKey, currString.toString());
                  currString = new StringBuilder();
               }
            }
         }

         IBlockState blockState;
         try {
            blockState = Block.getBlockFromName(blockString).getDefaultState();
         } catch (Exception var12) {
            BetterMineshafts.LOGGER.error("variants.json: Unable to read block '{}': {}", blockString, var12.toString());
            BetterMineshafts.LOGGER.error("Using air instead...");
            return Blocks.AIR.getDefaultState();
         }

         if (blockState == null) {
            BetterMineshafts.LOGGER.error("variants.json: Unable to read block '{}': null block returned.", blockString);
            BetterMineshafts.LOGGER.error("Using air instead...");
            return Blocks.AIR.getDefaultState();
         } else {
            if (properties.size() > 0) {
               blockState = this.getConfiguredBlockState(blockState, properties);
            }

            return blockState;
         }
      }
   }

   private <T extends Enum<T> & IStringSerializable> IBlockState getConfiguredBlockState(IBlockState blockState, Map<String, String> properties) {
      Block block = blockState.getBlock();

      for(Entry<String, String> entry : properties.entrySet()) {
         String key = (String)entry.getKey();
         String value = (String)entry.getValue();

         try {
            boolean found = false;

            for(Class<?> blockClass = block.getClass(); blockClass != Block.class && !found; blockClass = blockClass.getSuperclass()) {
               Field[] fields = blockClass.getDeclaredFields();

               for(Field field : fields) {
                  field.setAccessible(true);

                  Object _field;
                  try {
                     _field = field.get(blockState);
                  } catch (Exception var22) {
                     try {
                        _field = field.get(blockState.getBlock());
                     } catch (Exception var21) {
                        continue;
                     }
                  }

                  if (_field instanceof IProperty) {
                     IProperty<T> property = (IProperty)_field;
                     if (property.getName().equals(key)) {
                        Object val = property.parseValue(value).orNull();
                        Class<T> blockEnumClass = property.getValueClass();

                        try {
                           blockState = blockState.withProperty(property, (Comparable)Objects.requireNonNull(blockEnumClass.cast(val)));
                        } catch (Exception var20) {
                           continue;
                        }

                        found = true;
                        break;
                     }
                  }
               }
            }

            if (!found) {
               BetterMineshafts.LOGGER.error("variants.json: Unable to find property {} for block {}", key, block.getLocalizedName());
            }
         } catch (Exception var23) {
            BetterMineshafts.LOGGER.error("variants.json: Encountered error while attempting to apply property {}={} to block {}: {}", key, value, block.getLocalizedName(), var23.toString());
         }
      }

      return blockState;
   }

   public void write(JsonWriter writer, IBlockState blockState) throws IOException {
      if (blockState == null) {
         writer.nullValue();
      } else {
         writer.value(String.valueOf(blockState));
      }
   }
}
