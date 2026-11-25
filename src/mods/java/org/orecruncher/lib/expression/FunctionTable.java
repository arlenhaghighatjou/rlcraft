package org.orecruncher.lib.expression;

import java.util.TreeMap;
import javax.annotation.Nonnull;

public final class FunctionTable extends TreeMap<String, LazyFunction> {
   public FunctionTable() {
      super(String.CASE_INSENSITIVE_ORDER);
   }

   public FunctionTable(@Nonnull FunctionTable table) {
      this();
      this.putAll(table);
   }
}
