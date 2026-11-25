package org.orecruncher.lib.expression;

import java.util.TreeMap;
import javax.annotation.Nonnull;

public final class OperatorTable extends TreeMap<String, Operator> {
   public OperatorTable() {
      super(String.CASE_INSENSITIVE_ORDER);
   }

   public OperatorTable(@Nonnull OperatorTable table) {
      this();
      this.putAll(table);
   }
}
