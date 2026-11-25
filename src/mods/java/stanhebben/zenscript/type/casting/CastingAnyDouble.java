package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.value.IAny;

public class CastingAnyDouble implements ICastingRule {
   public static final CastingAnyDouble INSTANCE = new CastingAnyDouble();

   private CastingAnyDouble() {
   }

   public void compile(IEnvironmentMethod method) {
      method.getOutput().invokeInterface(IAny.class, "asDouble", Double.TYPE);
   }

   public ZenType getInputType() {
      return ZenType.ANY;
   }

   public ZenType getResultingType() {
      return ZenType.DOUBLE;
   }
}
