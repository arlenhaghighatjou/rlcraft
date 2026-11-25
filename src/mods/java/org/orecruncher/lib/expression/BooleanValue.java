package org.orecruncher.lib.expression;

import javax.annotation.Nonnull;

public class BooleanValue extends Variant {
   protected boolean value;

   public BooleanValue() {
      this.value = false;
   }

   public BooleanValue(boolean b) {
      this.value = b;
   }

   public BooleanValue(float f) {
      this.value = f != 0.0F;
   }

   public BooleanValue(@Nonnull String name) {
      super(name);
   }

   public BooleanValue(@Nonnull String name, boolean b) {
      super(name);
      this.value = b;
   }

   public BooleanValue(@Nonnull String name, float f) {
      super(name);
      this.value = f != 0.0F;
   }

   public float asNumber() {
      return this.value ? 1.0F : 0.0F;
   }

   @Nonnull
   public String asString() {
      return this.value ? "TRUE" : "FALSE";
   }

   public boolean asBoolean() {
      return this.value;
   }

   public int compareTo(@Nonnull IVariant variant) {
      return Boolean.compare(this.value, variant.asBoolean());
   }

   @Nonnull
   public IVariant add(@Nonnull IVariant term) {
      return new BooleanValue(this.value || term.asBoolean());
   }
}
