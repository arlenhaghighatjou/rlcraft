package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.value.IAny;

public class CastingAnyLong implements ICastingRule {
   public static final CastingAnyLong INSTANCE = new CastingAnyLong();

   private CastingAnyLong() {
   }

   public void compile(IEnvironmentMethod method) {
      method.getOutput().invokeInterface(IAny.class, "asLong", Long.TYPE);
   }

   public ZenType getInputType() {
      return ZenType.ANY;
   }

   public ZenType getResultingType() {
      return ZenType.LONG;
   }
}
