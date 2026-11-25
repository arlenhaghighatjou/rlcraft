package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public abstract class BaseCastingRule implements ICastingRule {
   private final ICastingRule baseRule;

   public BaseCastingRule(ICastingRule baseRule) {
      this.baseRule = baseRule;
   }

   public final void compile(IEnvironmentMethod method) {
      if (this.baseRule != null) {
         this.baseRule.compile(method);
      }

      this.compileInner(method);
   }

   public final ZenType getInputType() {
      return this.baseRule == null ? this.getInnerInputType() : this.baseRule.getInputType();
   }

   public abstract ZenType getInnerInputType();

   public abstract void compileInner(IEnvironmentMethod var1);
}
