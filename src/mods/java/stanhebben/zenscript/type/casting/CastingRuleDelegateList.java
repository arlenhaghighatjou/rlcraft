package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.type.ZenTypeArrayList;

public class CastingRuleDelegateList implements ICastingRuleDelegate {
   private final ICastingRuleDelegate base;
   private final ZenTypeArrayList from;

   public CastingRuleDelegateList(ICastingRuleDelegate base, ZenTypeArrayList from) {
      this.base = base;
      this.from = from;
   }

   public void registerCastingRule(ZenType type, ICastingRule rule) {
      this.base.registerCastingRule(new ZenTypeArrayBasic(type), new CastingRuleListArray(rule, this.from, new ZenTypeArrayBasic(type)));
      this.base.registerCastingRule(new ZenTypeArrayList(type), new CastingRuleListList(rule, this.from, new ZenTypeArrayList(type)));
   }
}
