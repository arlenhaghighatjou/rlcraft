package me.lucko.spark.lib.adventure.text.serializer.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import me.lucko.spark.lib.adventure.key.Key;

final class KeySerializer extends TypeAdapter<Key> {
   static final TypeAdapter<Key> INSTANCE = (new KeySerializer()).nullSafe();

   private KeySerializer() {
   }

   public void write(JsonWriter out, Key value) throws IOException {
      out.value(value.asString());
   }

   public Key read(JsonReader in) throws IOException {
      return Key.key(in.nextString());
   }
}
