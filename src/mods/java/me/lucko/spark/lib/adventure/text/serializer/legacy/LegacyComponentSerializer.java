package me.lucko.spark.lib.adventure.text.serializer.legacy;

import java.util.function.Consumer;
import java.util.regex.Pattern;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.TextComponent;
import me.lucko.spark.lib.adventure.text.flattener.ComponentFlattener;
import me.lucko.spark.lib.adventure.text.format.Style;
import me.lucko.spark.lib.adventure.text.serializer.ComponentSerializer;
import me.lucko.spark.lib.adventure.util.Buildable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface LegacyComponentSerializer extends ComponentSerializer<Component, TextComponent, String>, Buildable<LegacyComponentSerializer, LegacyComponentSerializer.Builder> {
   char SECTION_CHAR = '§';
   char AMPERSAND_CHAR = '&';
   char HEX_CHAR = '#';

   @NotNull
   static LegacyComponentSerializer legacySection() {
      return LegacyComponentSerializerImpl.Instances.SECTION;
   }

   @NotNull
   static LegacyComponentSerializer legacyAmpersand() {
      return LegacyComponentSerializerImpl.Instances.AMPERSAND;
   }

   @NotNull
   static LegacyComponentSerializer legacy(char legacyCharacter) {
      if (legacyCharacter == 167) {
         return legacySection();
      } else {
         return legacyCharacter == '&' ? legacyAmpersand() : builder().character(legacyCharacter).build();
      }
   }

   @Nullable
   static LegacyFormat parseChar(char character) {
      return LegacyComponentSerializerImpl.legacyFormat(character);
   }

   @NotNull
   static LegacyComponentSerializer.Builder builder() {
      return new LegacyComponentSerializerImpl.BuilderImpl();
   }

   @NotNull
   TextComponent deserialize(@NotNull String var1);

   @NotNull
   String serialize(@NotNull Component var1);

   public interface Builder extends Buildable.Builder<LegacyComponentSerializer> {
      @NotNull
      LegacyComponentSerializer.Builder character(char var1);

      @NotNull
      LegacyComponentSerializer.Builder hexCharacter(char var1);

      @NotNull
      LegacyComponentSerializer.Builder extractUrls();

      @NotNull
      LegacyComponentSerializer.Builder extractUrls(@NotNull Pattern var1);

      @NotNull
      LegacyComponentSerializer.Builder extractUrls(@Nullable Style var1);

      @NotNull
      LegacyComponentSerializer.Builder extractUrls(@NotNull Pattern var1, @Nullable Style var2);

      @NotNull
      LegacyComponentSerializer.Builder hexColors();

      @NotNull
      LegacyComponentSerializer.Builder useUnusualXRepeatedCharacterHexFormat();

      @NotNull
      LegacyComponentSerializer.Builder flattener(@NotNull ComponentFlattener var1);

      @NotNull
      LegacyComponentSerializer build();
   }

   @Internal
   public interface Provider {
      @Internal
      @NotNull
      LegacyComponentSerializer legacyAmpersand();

      @Internal
      @NotNull
      LegacyComponentSerializer legacySection();

      @Internal
      @NotNull
      Consumer<LegacyComponentSerializer.Builder> legacy();
   }
}
