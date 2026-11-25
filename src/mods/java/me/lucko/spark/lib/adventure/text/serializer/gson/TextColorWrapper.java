package me.lucko.spark.lib.adventure.text.serializer.gson;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import me.lucko.spark.lib.adventure.text.format.TextColor;
import me.lucko.spark.lib.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;

final class TextColorWrapper {
   @Nullable
   final TextColor color;
   @Nullable
   final TextDecoration decoration;
   final boolean reset;

   TextColorWrapper(@Nullable TextColor color, @Nullable TextDecoration decoration, boolean reset) {
      this.color = color;
      this.decoration = decoration;
      this.reset = reset;
   }

   static class Serializer extends TypeAdapter<TextColorWrapper> {
      Serializer() {
      }

      public void write(JsonWriter out, TextColorWrapper value) {
         throw new JsonSyntaxException("Cannot write TextColorWrapper instances");
      }

      public TextColorWrapper read(JsonReader in) throws IOException {
         String input = in.nextString();
         TextColor color = TextColorSerializer.fromString(input);
         TextDecoration decoration = TextDecoration.NAMES.value(input);
         boolean reset = decoration == null && input.equals("reset");
         if (color == null && decoration == null && !reset) {
            throw new JsonParseException("Don't know how to parse " + input + " at " + in.getPath());
         } else {
            return new TextColorWrapper(color, decoration, reset);
         }
      }
   }
}
