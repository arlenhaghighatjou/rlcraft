package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.value.IAny;

public class CastingAnyBool implements ICastingRule {
   public static final CastingAnyBool INSTANCE = new CastingAnyBool();

   private CastingAnyBool() {
   }

   public void compile(IEnvironmentMethod method) {
      method.getOutput().invokeInterface(IAny.class, "asBool", Boolean.TYPE);
   }

   public ZenType getInputType() {
      return ZenType.ANY;
   }

   public ZenType getResultingType() {
      return ZenType.BOOL;
   }
}
