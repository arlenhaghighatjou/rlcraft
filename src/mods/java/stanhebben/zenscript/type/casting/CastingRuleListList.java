package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArrayList;

public class CastingRuleListList implements ICastingRule {
   private final ICastingRule base;
   private final ZenTypeArrayList from;
   private final ZenTypeArrayList to;

   public CastingRuleListList(ICastingRule base, ZenTypeArrayList from, ZenTypeArrayList to) {
      this.base = base;
      this.from = from;
      this.to = to;
   }

   public void compile(IEnvironmentMethod method) {
      throw new UnsupportedOperationException("Not yet implemented");
   }

   public ZenType getInputType() {
      return this.from;
   }

   public ZenType getResultingType() {
      return this.to;
   }
}
