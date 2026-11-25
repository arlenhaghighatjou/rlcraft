package org.orecruncher.lib.expression;

import java.util.function.Supplier;
import javax.annotation.Nonnull;

public final class Dynamic {
   private Dynamic() {
   }

   public static final class DynamicBoolean extends BooleanValue implements IDynamicVariant {
      private boolean needsSet = true;
      private final Supplier<Boolean> supplier;

      public DynamicBoolean(@Nonnull String name, @Nonnull Supplier<Boolean> func) {
         super(name);
         this.supplier = func;
      }

      public void reset() {
         this.needsSet = true;
      }

      protected void update() {
         if (this.needsSet) {
            this.value = ((Boolean)this.supplier.get()).booleanValue();
            this.needsSet = false;
         }

      }

      public float asNumber() {
         this.update();
         return super.asNumber();
      }

      @Nonnull
      public String asString() {
         this.update();
         return super.asString();
      }

      public boolean asBoolean() {
         this.update();
         return super.asBoolean();
      }

      @Nonnull
      public IVariant add(@Nonnull IVariant term) {
         this.update();
         return super.add(term);
      }
   }

   public static final class DynamicNumber extends NumberValue implements IDynamicVariant {
      private boolean needsSet = true;
      private final Supplier<Float> supplier;

      public DynamicNumber(@Nonnull String name, @Nonnull Supplier<Float> func) {
         super(name);
         this.supplier = func;
      }

      public void reset() {
         this.needsSet = true;
      }

      protected void update() {
         if (this.needsSet) {
            this.value = ((Float)this.supplier.get()).floatValue();
            this.needsSet = false;
         }

      }

      public float asNumber() {
         this.update();
         return super.asNumber();
      }

      @Nonnull
      public String asString() {
         this.update();
         return super.asString();
      }

      public boolean asBoolean() {
         this.update();
         return super.asBoolean();
      }

      @Nonnull
      public IVariant add(@Nonnull IVariant term) {
         this.update();
         return super.add(term);
      }
   }

   public static final class DynamicString extends StringValue implements IDynamicVariant {
      private boolean needsSet = true;
      private final Supplier<String> supplier;

      public DynamicString(@Nonnull String name, @Nonnull Supplier<String> func) {
         super(name, "");
         this.supplier = func;
      }

      public void reset() {
         this.needsSet = true;
      }

      protected void update() {
         if (this.needsSet) {
            this.value = (String)this.supplier.get();
            this.needsSet = false;
         }

      }

      public float asNumber() {
         this.update();
         return super.asNumber();
      }

      @Nonnull
      public String asString() {
         this.update();
         return super.asString();
      }

      public boolean asBoolean() {
         this.update();
         return super.asBoolean();
      }

      @Nonnull
      public IVariant add(@Nonnull IVariant term) {
         this.update();
         return super.add(term);
      }
   }
}
