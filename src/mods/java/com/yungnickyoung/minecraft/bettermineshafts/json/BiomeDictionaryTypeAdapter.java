package com.yungnickyoung.minecraft.bettermineshafts.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BiomeDictionaryTypeAdapter extends TypeAdapter<Type> {
   public BiomeDictionaryTypeAdapter() {
   }

   public Type read(JsonReader reader) throws IOException {
      if (reader.peek() == JsonToken.NULL) {
         reader.nextNull();
         return null;
      } else {
         return Type.getType(reader.nextString(), new Type[0]);
      }
   }

   public void write(JsonWriter writer, Type tag) throws IOException {
      if (tag == null) {
         writer.nullValue();
      } else {
         writer.value(tag.getName());
      }
   }
}
