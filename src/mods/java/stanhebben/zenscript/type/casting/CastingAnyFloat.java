package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.value.IAny;

public class CastingAnyFloat implements ICastingRule {
   public static final CastingAnyFloat INSTANCE = new CastingAnyFloat();

   private CastingAnyFloat() {
   }

   public void compile(IEnvironmentMethod method) {
      method.getOutput().invokeInterface(IAny.class, "asFloat", Float.TYPE);
   }

   public ZenType getInputType() {
      return ZenType.ANY;
   }

   public ZenType getResultingType() {
      return ZenType.FLOAT;
   }
}
