package org.orecruncher.lib.expression;

import javax.annotation.Nonnull;

public class NumberValue extends Variant {
   protected float value;

   public NumberValue() {
      this.value = 0.0F;
   }

   public NumberValue(float v) {
      this.value = v;
   }

   public NumberValue(@Nonnull Float v) {
      this.value = v.floatValue();
   }

   public NumberValue(double d) {
      this.value = (float)d;
   }

   public NumberValue(@Nonnull String name) {
      super(name);
      this.value = 0.0F;
   }

   public NumberValue(@Nonnull String name, float v) {
      super(name);
      this.value = v;
   }

   public NumberValue(@Nonnull String name, @Nonnull Float v) {
      super(name);
      this.value = v.floatValue();
   }

   public NumberValue(@Nonnull String name, double d) {
      super(name);
      this.value = (float)d;
   }

   public float asNumber() {
      return this.value;
   }

   @Nonnull
   public String asString() {
      int i = (int)this.value;
      return (float)i == this.value ? Integer.toString(i) : Float.toString(this.value);
   }

   public boolean asBoolean() {
      return this.value != 0.0F;
   }

   public int compareTo(@Nonnull IVariant variant) {
      return Float.compare(this.value, variant.asNumber());
   }

   @Nonnull
   public IVariant add(@Nonnull IVariant term) {
      return new NumberValue(this.value + term.asNumber());
   }
}
