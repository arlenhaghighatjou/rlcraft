package org.orecruncher.lib.expression;

import java.util.TreeMap;
import javax.annotation.Nonnull;

public final class VariableTable extends TreeMap<String, LazyVariant> {
   public VariableTable() {
      super(String.CASE_INSENSITIVE_ORDER);
   }

   public VariableTable(@Nonnull VariableTable table) {
      this();
      this.putAll(table);
   }
}
