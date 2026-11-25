package org.orecruncher.lib.expression;

import javax.annotation.Nonnull;

public interface IVariant extends LazyVariant, Comparable<IVariant> {
   default String getName() {
      return "<ANON>";
   }

   float asNumber();

   String asString();

   boolean asBoolean();

   @Nonnull
   IVariant add(@Nonnull IVariant var1);

   @Nonnull
   String toString();

   @Nonnull
   default IVariant eval() {
      return this;
   }
}
