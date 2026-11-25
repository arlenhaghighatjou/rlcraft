package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public class CastingAnyString implements ICastingRule {
   public static final CastingAnyString INSTANCE = new CastingAnyString();

   private CastingAnyString() {
   }

   public void compile(IEnvironmentMethod method) {
      method.getOutput().invokeVirtual(Object.class, "toString", String.class);
   }

   public ZenType getInputType() {
      return ZenType.ANY;
   }

   public ZenType getResultingType() {
      return ZenType.STRING;
   }
}
