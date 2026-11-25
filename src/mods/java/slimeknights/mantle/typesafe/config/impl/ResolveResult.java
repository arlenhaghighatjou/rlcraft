package slimeknights.mantle.typesafe.config.impl;

import slimeknights.mantle.typesafe.config.ConfigException;

final class ResolveResult<V extends AbstractConfigValue> {
   public final ResolveContext context;
   public final V value;

   private ResolveResult(ResolveContext context, V value) {
      this.context = context;
      this.value = value;
   }

   static <V extends AbstractConfigValue> ResolveResult<V> make(ResolveContext context, V value) {
      return new ResolveResult<V>(context, value);
   }

   ResolveResult<AbstractConfigObject> asObjectResult() {
      if (!(this.value instanceof AbstractConfigObject)) {
         throw new ConfigException.BugOrBroken("Expecting a resolve result to be an object, but it was " + this.value);
      } else {
         return (ResolveResult)this;
      }
   }

   ResolveResult<AbstractConfigValue> asValueResult() {
      return (ResolveResult)this;
   }

   ResolveResult<V> popTrace() {
      return make(this.context.popTrace(), this.value);
   }

   public String toString() {
      return "ResolveResult(" + this.value + ")";
   }
}
