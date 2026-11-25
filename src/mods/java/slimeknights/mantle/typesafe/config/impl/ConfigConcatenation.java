package slimeknights.mantle.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import slimeknights.mantle.typesafe.config.ConfigException;
import slimeknights.mantle.typesafe.config.ConfigObject;
import slimeknights.mantle.typesafe.config.ConfigOrigin;
import slimeknights.mantle.typesafe.config.ConfigRenderOptions;
import slimeknights.mantle.typesafe.config.ConfigValueType;

final class ConfigConcatenation extends AbstractConfigValue implements Unmergeable, Container {
   private final List<AbstractConfigValue> pieces;

   ConfigConcatenation(ConfigOrigin origin, List<AbstractConfigValue> pieces) {
      super(origin);
      this.pieces = pieces;
      if (pieces.size() < 2) {
         throw new ConfigException.BugOrBroken("Created concatenation with less than 2 items: " + this);
      } else {
         boolean hadUnmergeable = false;

         for(AbstractConfigValue p : pieces) {
            if (p instanceof ConfigConcatenation) {
               throw new ConfigException.BugOrBroken("ConfigConcatenation should never be nested: " + this);
            }

            if (p instanceof Unmergeable) {
               hadUnmergeable = true;
            }
         }

         if (!hadUnmergeable) {
            throw new ConfigException.BugOrBroken("Created concatenation without an unmergeable in it: " + this);
         }
      }
   }

   private ConfigException.NotResolved notResolved() {
      return new ConfigException.NotResolved("need to Config#resolve(), see the API docs for Config#resolve(); substitution not resolved: " + this);
   }

   public ConfigValueType valueType() {
      throw this.notResolved();
   }

   public Object unwrapped() {
      throw this.notResolved();
   }

   protected ConfigConcatenation newCopy(ConfigOrigin newOrigin) {
      return new ConfigConcatenation(newOrigin, this.pieces);
   }

   protected boolean ignoresFallbacks() {
      return false;
   }

   public Collection<ConfigConcatenation> unmergedValues() {
      return Collections.singleton(this);
   }

   private static boolean isIgnoredWhitespace(AbstractConfigValue value) {
      return value instanceof ConfigString && !((ConfigString)value).wasQuoted();
   }

   private static void join(ArrayList<AbstractConfigValue> builder, AbstractConfigValue origRight) {
      AbstractConfigValue left = (AbstractConfigValue)builder.get(builder.size() - 1);
      AbstractConfigValue right = origRight;
      if (left instanceof ConfigObject && origRight instanceof SimpleConfigList) {
         left = DefaultTransformer.transform(left, ConfigValueType.LIST);
      } else if (left instanceof SimpleConfigList && origRight instanceof ConfigObject) {
         right = DefaultTransformer.transform(origRight, ConfigValueType.LIST);
      }

      AbstractConfigValue joined = null;
      if (left instanceof ConfigObject && right instanceof ConfigObject) {
         joined = right.withFallback(left);
      } else if (left instanceof SimpleConfigList && right instanceof SimpleConfigList) {
         joined = ((SimpleConfigList)left).concatenate((SimpleConfigList)right);
      } else if ((left instanceof SimpleConfigList || left instanceof ConfigObject) && isIgnoredWhitespace(right)) {
         joined = left;
      } else {
         if (left instanceof ConfigConcatenation || right instanceof ConfigConcatenation) {
            throw new ConfigException.BugOrBroken("unflattened ConfigConcatenation");
         }

         if (!(left instanceof Unmergeable) && !(right instanceof Unmergeable)) {
            String s1 = left.transformToString();
            String s2 = right.transformToString();
            if (s1 == null || s2 == null) {
               throw new ConfigException.WrongType(left.origin(), "Cannot concatenate object or list with a non-object-or-list, " + left + " and " + right + " are not compatible");
            }

            ConfigOrigin joinedOrigin = SimpleConfigOrigin.mergeOrigins(left.origin(), right.origin());
            joined = new ConfigString.Quoted(joinedOrigin, s1 + s2);
         }
      }

      if (joined == null) {
         builder.add(right);
      } else {
         builder.remove(builder.size() - 1);
         builder.add(joined);
      }

   }

   static List<AbstractConfigValue> consolidate(List<AbstractConfigValue> pieces) {
      if (pieces.size() < 2) {
         return pieces;
      } else {
         List<AbstractConfigValue> flattened = new ArrayList(pieces.size());

         for(AbstractConfigValue v : pieces) {
            if (v instanceof ConfigConcatenation) {
               flattened.addAll(((ConfigConcatenation)v).pieces);
            } else {
               flattened.add(v);
            }
         }

         ArrayList<AbstractConfigValue> consolidated = new ArrayList(flattened.size());

         for(AbstractConfigValue v : flattened) {
            if (consolidated.isEmpty()) {
               consolidated.add(v);
            } else {
               join(consolidated, v);
            }
         }

         return consolidated;
      }
   }

   static AbstractConfigValue concatenate(List<AbstractConfigValue> pieces) {
      List<AbstractConfigValue> consolidated = consolidate(pieces);
      if (consolidated.isEmpty()) {
         return null;
      } else if (consolidated.size() == 1) {
         return (AbstractConfigValue)consolidated.get(0);
      } else {
         ConfigOrigin mergedOrigin = SimpleConfigOrigin.mergeOrigins(consolidated);
         return new ConfigConcatenation(mergedOrigin, consolidated);
      }
   }

   ResolveResult<? extends AbstractConfigValue> resolveSubstitutions(ResolveContext context, ResolveSource source) throws AbstractConfigValue.NotPossibleToResolve {
      if (ConfigImpl.traceSubstitutionsEnabled()) {
         int indent = context.depth() + 2;
         ConfigImpl.trace(indent - 1, "concatenation has " + this.pieces.size() + " pieces:");
         int count = 0;

         for(AbstractConfigValue v : this.pieces) {
            ConfigImpl.trace(indent, count + ": " + v);
            ++count;
         }
      }

      ResolveSource sourceWithParent = source;
      ResolveContext newContext = context;
      List<AbstractConfigValue> resolved = new ArrayList(this.pieces.size());

      for(AbstractConfigValue p : this.pieces) {
         Path restriction = newContext.restrictToChild();
         ResolveResult<? extends AbstractConfigValue> result = newContext.unrestricted().resolve(p, sourceWithParent);
         AbstractConfigValue r = result.value;
         newContext = result.context.restrict(restriction);
         if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(context.depth(), "resolved concat piece to " + r);
         }

         if (r != null) {
            resolved.add(r);
         }
      }

      List<AbstractConfigValue> joined = consolidate(resolved);
      if (joined.size() > 1 && context.options().getAllowUnresolved()) {
         return ResolveResult.<AbstractConfigValue>make(newContext, new ConfigConcatenation(this.origin(), joined));
      } else if (joined.isEmpty()) {
         return ResolveResult.<AbstractConfigValue>make(newContext, (AbstractConfigValue)null);
      } else if (joined.size() == 1) {
         return ResolveResult.<AbstractConfigValue>make(newContext, (AbstractConfigValue)joined.get(0));
      } else {
         throw new ConfigException.BugOrBroken("Bug in the library; resolved list was joined to too many values: " + joined);
      }
   }

   ResolveStatus resolveStatus() {
      return ResolveStatus.UNRESOLVED;
   }

   public ConfigConcatenation replaceChild(AbstractConfigValue child, AbstractConfigValue replacement) {
      List<AbstractConfigValue> newPieces = replaceChildInList(this.pieces, child, replacement);
      return newPieces == null ? null : new ConfigConcatenation(this.origin(), newPieces);
   }

   public boolean hasDescendant(AbstractConfigValue descendant) {
      return hasDescendantInList(this.pieces, descendant);
   }

   ConfigConcatenation relativized(Path prefix) {
      List<AbstractConfigValue> newPieces = new ArrayList();

      for(AbstractConfigValue p : this.pieces) {
         newPieces.add(p.relativized(prefix));
      }

      return new ConfigConcatenation(this.origin(), newPieces);
   }

   protected boolean canEqual(Object other) {
      return other instanceof ConfigConcatenation;
   }

   public boolean equals(Object other) {
      if (!(other instanceof ConfigConcatenation)) {
         return false;
      } else {
         return this.canEqual(other) && this.pieces.equals(((ConfigConcatenation)other).pieces);
      }
   }

   public int hashCode() {
      return this.pieces.hashCode();
   }

   protected void render(StringBuilder sb, int indent, boolean atRoot, ConfigRenderOptions options) {
      for(AbstractConfigValue p : this.pieces) {
         p.render(sb, indent, atRoot, options);
      }

   }
}
