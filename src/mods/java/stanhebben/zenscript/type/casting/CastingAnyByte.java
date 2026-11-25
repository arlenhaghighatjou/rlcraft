package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.value.IAny;

public class CastingAnyByte implements ICastingRule {
   public static final CastingAnyByte INSTANCE = new CastingAnyByte();

   private CastingAnyByte() {
   }

   public void compile(IEnvironmentMethod method) {
      method.getOutput().invokeInterface(IAny.class, "asByte", Byte.TYPE);
   }

   public ZenType getInputType() {
      return ZenType.ANY;
   }

   public ZenType getResultingType() {
      return ZenType.BYTE;
   }
}
