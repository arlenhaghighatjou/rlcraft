package stanhebben.zenscript.type.casting;

import java.util.Map;
import stanhebben.zenscript.type.ZenType;

public class CastingRuleDelegateMap implements ICastingRuleDelegate {
   private final ZenType forType;
   private final Map<ZenType, ICastingRule> target;

   public CastingRuleDelegateMap(ZenType forType, Map<ZenType, ICastingRule> target) {
      this.forType = forType;
      this.target = target;
   }

   public void registerCastingRule(ZenType type, ICastingRule rule) {
      this.target.put(type, rule);
   }
}
