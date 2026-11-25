package org.orecruncher.lib.expression;

import javax.annotation.Nonnull;

public abstract class Variant implements IVariant, Comparable<IVariant> {
   protected final String name;

   public Variant() {
      this("<ANON>");
   }

   public Variant(@Nonnull String name) {
      this.name = name;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nonnull
   public final String toString() {
      return this.asString();
   }
}
