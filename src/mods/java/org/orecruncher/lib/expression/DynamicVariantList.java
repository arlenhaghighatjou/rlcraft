package org.orecruncher.lib.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public class DynamicVariantList {
   protected final List<IDynamicVariant> variants = new ArrayList();

   public DynamicVariantList() {
   }

   void reset() {
      this.variants.forEach(IDynamicVariant::reset);
   }

   public void add(@Nonnull IDynamicVariant dv) {
      this.variants.add(dv);
   }

   public void attach(@Nonnull Expression exp) {
      this.variants.forEach((v) -> {
         exp.addVariable((Variant)v);
      });
   }

   @Nonnull
   public List<IDynamicVariant> getList() {
      return new ArrayList(this.variants);
   }
}
