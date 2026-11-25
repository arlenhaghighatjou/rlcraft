package stanhebben.zenscript.type.casting;

import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.value.IAny;

public class CastingRuleAnyAs implements ICastingRule {
   private final ZenType type;

   public CastingRuleAnyAs(ZenType type) {
      this.type = type;
   }

   public void compile(IEnvironmentMethod method) {
      method.getOutput().constant(Type.getType(this.type.toJavaClass()));
      method.getOutput().invokeInterface(IAny.class, "as", Class.class, Object.class);
   }

   public ZenType getInputType() {
      return ZenType.ANY;
   }

   public ZenType getResultingType() {
      return this.type;
   }
}
