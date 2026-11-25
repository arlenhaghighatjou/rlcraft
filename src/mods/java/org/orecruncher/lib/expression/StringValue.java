package org.orecruncher.lib.expression;

import javax.annotation.Nonnull;

public class StringValue extends Variant {
   protected String value;

   public StringValue() {
      this.value = "";
   }

   public StringValue(@Nonnull String v) {
      this.value = v;
   }

   public StringValue(@Nonnull String name, @Nonnull String value) {
      super(name);
      this.value = value;
   }

   public float asNumber() {
      return Float.parseFloat(this.value);
   }

   @Nonnull
   public String asString() {
      return this.value;
   }

   public boolean asBoolean() {
      return "TRUE".equalsIgnoreCase(this.value);
   }

   public int compareTo(@Nonnull IVariant variant) {
      return this.value.compareTo(variant.asString());
   }

   @Nonnull
   public IVariant add(@Nonnull IVariant term) {
      return new StringValue(this.value.concat(term.asString()));
   }
}
