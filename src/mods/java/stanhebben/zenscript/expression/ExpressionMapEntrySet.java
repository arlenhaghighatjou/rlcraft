package stanhebben.zenscript.expression;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.type.ZenTypeAssociative;
import stanhebben.zenscript.type.ZenTypeEntry;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ExpressionMapEntrySet extends Expression {
   private final Expression map;
   private final ExpressionMapEntrySet.SetType setType;
   private final ZenTypeArrayBasic type;

   public ExpressionMapEntrySet(ZenPosition position, Expression map, String whichSet) {
      super(position);
      this.map = map;
      if (whichSet.contains("value")) {
         this.setType = ExpressionMapEntrySet.SetType.VALUES;
      } else if (whichSet.contains("key")) {
         this.setType = ExpressionMapEntrySet.SetType.KEYS;
      } else {
         this.setType = ExpressionMapEntrySet.SetType.ENTRIES;
      }

      this.type = this.makeType(map);
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      this.map.compile(result, environment);
      MethodOutput output = environment.getOutput();
      switch(this.setType) {
      case KEYS:
         output.invokeInterface(Map.class, "keySet", Set.class);
         break;
      case VALUES:
         output.invokeInterface(Map.class, "values", Collection.class);
         break;
      case ENTRIES:
         output.invokeInterface(Map.class, "entrySet", Set.class);
      }

      output.iConst0();
      output.newArray(this.type.getBaseType().toASMType());
      output.invokeInterface(Collection.class, "toArray", Object[].class, Object[].class);
      output.checkCast(ZenTypeUtil.internal(this.getType().toJavaClass()));
   }

   public ZenType getType() {
      return this.type;
   }

   private ZenTypeArrayBasic makeType(Expression map) {
      ZenTypeAssociative mapType = (ZenTypeAssociative)map.getType();
      switch(this.setType) {
      case KEYS:
         return new ZenTypeArrayBasic(mapType.getKeyType());
      case VALUES:
         return new ZenTypeArrayBasic(mapType.getValueType());
      default:
         return new ZenTypeArrayBasic(new ZenTypeEntry(mapType));
      }
   }

   private static enum SetType {
      VALUES,
      KEYS,
      ENTRIES;

      private SetType() {
      }
   }
}
