package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenTypeUtil;

public class CastingAnySubtype implements ICastingRule {
   private final ZenType fromType;
   private final ZenType toType;

   public CastingAnySubtype(ZenType fromType, ZenType toType) {
      this.fromType = fromType;
      this.toType = toType;
   }

   public void compile(IEnvironmentMethod method) {
      method.getOutput().checkCast(ZenTypeUtil.internal(this.toType.toJavaClass()));
   }

   public ZenType getInputType() {
      return this.fromType;
   }

   public ZenType getResultingType() {
      return this.toType;
   }
}
