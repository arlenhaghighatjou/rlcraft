package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;

public class CastingRuleDelegateStaticMethod implements ICastingRuleDelegate {
   private final ICastingRuleDelegate target;
   private final IJavaMethod method;

   public CastingRuleDelegateStaticMethod(ICastingRuleDelegate target, IJavaMethod method) {
      this.target = target;
      this.method = method;
   }

   public void registerCastingRule(ZenType type, ICastingRule rule) {
      this.target.registerCastingRule(type, new CastingRuleStaticMethod(this.method, rule));
   }
}
