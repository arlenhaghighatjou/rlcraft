package me.lucko.spark.lib.adventure.key;

import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.Examinable;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

public interface Key extends Comparable<Key>, Examinable {
   String MINECRAFT_NAMESPACE = "minecraft";

   @NotNull
   static Key key(@NotNull @Pattern("([a-z0-9_\\-.]+:)?[a-z0-9_\\-./]+") String string) {
      return key(string, ':');
   }

   @NotNull
   static Key key(@NotNull String string, char character) {
      int index = string.indexOf(character);
      String namespace = index >= 1 ? string.substring(0, index) : "minecraft";
      String value = index >= 0 ? string.substring(index + 1) : string;
      return key(namespace, value);
   }

   @NotNull
   static Key key(@NotNull Namespaced namespaced, @NotNull @Pattern("[a-z0-9_\\-./]+") String value) {
      return key(namespaced.namespace(), value);
   }

   @NotNull
   static Key key(@NotNull @Pattern("[a-z0-9_\\-.]+") String namespace, @NotNull @Pattern("[a-z0-9_\\-./]+") String value) {
      return new KeyImpl(namespace, value);
   }

   @NotNull
   String namespace();

   @NotNull
   String value();

   @NotNull
   String asString();

   @NotNull
   default Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(ExaminableProperty.of("namespace", this.namespace()), ExaminableProperty.of("value", this.value()));
   }

   default int compareTo(@NotNull Key that) {
      int value = this.value().compareTo(that.value());
      return value != 0 ? KeyImpl.clampCompare(value) : KeyImpl.clampCompare(this.namespace().compareTo(that.namespace()));
   }
}
