package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.type.ZenTypeArrayList;

public class CastingRuleDelegateArray implements ICastingRuleDelegate {
   private final ICastingRuleDelegate base;
   private final ZenTypeArrayBasic from;

   public CastingRuleDelegateArray(ICastingRuleDelegate base, ZenTypeArrayBasic from) {
      this.base = base;
      this.from = from;
   }

   public void registerCastingRule(ZenType type, ICastingRule rule) {
      this.base.registerCastingRule(new ZenTypeArrayBasic(type), new CastingRuleArrayArray(rule, this.from, new ZenTypeArrayBasic(type)));
      this.base.registerCastingRule(new ZenTypeArrayList(type), new CastingRuleArrayList(rule, this.from, new ZenTypeArrayList(type)));
   }
}
