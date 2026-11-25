package stanhebben.zenscript.expression;

import java.util.Map.Entry;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeEntry;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ExpressionEntryGet extends Expression {
   private final Expression entry;
   private final ZenTypeEntry type;
   private final boolean isKey;

   public ExpressionEntryGet(ZenPosition position, Expression entry, boolean isKey) {
      super(position);
      this.entry = entry;
      this.type = (ZenTypeEntry)entry.getType();
      this.isKey = isKey;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      this.entry.compile(result, environment);
      environment.getOutput().invokeInterface(Entry.class, this.isKey ? "getKey" : "getValue", Object.class);
      environment.getOutput().checkCast(ZenTypeUtil.internal(this.getType().toJavaClass()));
   }

   public ZenType getType() {
      return this.isKey ? this.type.getKeyType() : this.type.getValueType();
   }
}
