package me.lucko.spark.lib.adventure.key;

import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

final class KeyImpl implements Key {
   static final String NAMESPACE_PATTERN = "[a-z0-9_\\-.]+";
   static final String VALUE_PATTERN = "[a-z0-9_\\-./]+";
   private static final IntPredicate NAMESPACE_PREDICATE = (value) -> {
      return value == 95 || value == 45 || value >= 97 && value <= 122 || value >= 48 && value <= 57 || value == 46;
   };
   private static final IntPredicate VALUE_PREDICATE = (value) -> {
      return value == 95 || value == 45 || value >= 97 && value <= 122 || value >= 48 && value <= 57 || value == 47 || value == 46;
   };
   private final String namespace;
   private final String value;

   KeyImpl(@NotNull String namespace, @NotNull String value) {
      if (!namespaceValid(namespace)) {
         throw new InvalidKeyException(namespace, value, String.format("Non [a-z0-9_.-] character in namespace of Key[%s]", asString(namespace, value)));
      } else if (!valueValid(value)) {
         throw new InvalidKeyException(namespace, value, String.format("Non [a-z0-9/._-] character in value of Key[%s]", asString(namespace, value)));
      } else {
         this.namespace = (String)Objects.requireNonNull(namespace, "namespace");
         this.value = (String)Objects.requireNonNull(value, "value");
      }
   }

   @VisibleForTesting
   static boolean namespaceValid(@NotNull String namespace) {
      int i = 0;

      for(int length = namespace.length(); i < length; ++i) {
         if (!NAMESPACE_PREDICATE.test(namespace.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   @VisibleForTesting
   static boolean valueValid(@NotNull String value) {
      int i = 0;

      for(int length = value.length(); i < length; ++i) {
         if (!VALUE_PREDICATE.test(value.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   @NotNull
   public String namespace() {
      return this.namespace;
   }

   @NotNull
   public String value() {
      return this.value;
   }

   @NotNull
   public String asString() {
      return asString(this.namespace, this.value);
   }

   @NotNull
   private static String asString(@NotNull String namespace, @NotNull String value) {
      return namespace + ':' + value;
   }

   @NotNull
   public String toString() {
      return this.asString();
   }

   @NotNull
   public Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(ExaminableProperty.of("namespace", this.namespace), ExaminableProperty.of("value", this.value));
   }

   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Key)) {
         return false;
      } else {
         Key that = (Key)other;
         return Objects.equals(this.namespace, that.namespace()) && Objects.equals(this.value, that.value());
      }
   }

   public int hashCode() {
      int result = this.namespace.hashCode();
      result = 31 * result + this.value.hashCode();
      return result;
   }

   public int compareTo(@NotNull Key that) {
      return super.compareTo(that);
   }

   static int clampCompare(int value) {
      if (value < 0) {
         return -1;
      } else {
         return value > 0 ? 1 : value;
      }
   }
}
