package slimeknights.mantle.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import slimeknights.mantle.typesafe.config.ConfigException;
import slimeknights.mantle.typesafe.config.ConfigOrigin;
import slimeknights.mantle.typesafe.config.ConfigRenderOptions;
import slimeknights.mantle.typesafe.config.ConfigValueType;

final class ConfigDelayedMerge extends AbstractConfigValue implements Unmergeable, ReplaceableMergeStack {
   private final List<AbstractConfigValue> stack;

   ConfigDelayedMerge(ConfigOrigin origin, List<AbstractConfigValue> stack) {
      super(origin);
      this.stack = stack;
      if (stack.isEmpty()) {
         throw new ConfigException.BugOrBroken("creating empty delayed merge value");
      } else {
         for(AbstractConfigValue v : stack) {
            if (v instanceof ConfigDelayedMerge || v instanceof ConfigDelayedMergeObject) {
               throw new ConfigException.BugOrBroken("placed nested DelayedMerge in a ConfigDelayedMerge, should have consolidated stack");
            }
         }

      }
   }

   public ConfigValueType valueType() {
      throw new ConfigException.NotResolved("called valueType() on value with unresolved substitutions, need to Config#resolve() first, see API docs");
   }

   public Object unwrapped() {
      throw new ConfigException.NotResolved("called unwrapped() on value with unresolved substitutions, need to Config#resolve() first, see API docs");
   }

   ResolveResult<? extends AbstractConfigValue> resolveSubstitutions(ResolveContext context, ResolveSource source) throws AbstractConfigValue.NotPossibleToResolve {
      return resolveSubstitutions(this, this.stack, context, source);
   }

   static ResolveResult<? extends AbstractConfigValue> resolveSubstitutions(ReplaceableMergeStack replaceable, List<AbstractConfigValue> stack, ResolveContext context, ResolveSource source) throws AbstractConfigValue.NotPossibleToResolve {
      if (ConfigImpl.traceSubstitutionsEnabled()) {
         ConfigImpl.trace(context.depth(), "delayed merge stack has " + stack.size() + " items:");
         int count = 0;

         for(AbstractConfigValue v : stack) {
            ConfigImpl.trace(context.depth() + 1, count + ": " + v);
            ++count;
         }
      }

      ResolveContext newContext = context;
      int count = 0;
      AbstractConfigValue merged = null;

      for(AbstractConfigValue end : stack) {
         if (end instanceof ReplaceableMergeStack) {
            throw new ConfigException.BugOrBroken("A delayed merge should not contain another one: " + replaceable);
         }

         ResolveSource sourceForEnd;
         if (end instanceof Unmergeable) {
            AbstractConfigValue remainder = replaceable.makeReplacement(context, count + 1);
            if (ConfigImpl.traceSubstitutionsEnabled()) {
               ConfigImpl.trace(newContext.depth(), "remainder portion: " + remainder);
            }

            if (ConfigImpl.traceSubstitutionsEnabled()) {
               ConfigImpl.trace(newContext.depth(), "building sourceForEnd");
            }

            sourceForEnd = source.replaceWithinCurrentParent((AbstractConfigValue)replaceable, remainder);
            if (ConfigImpl.traceSubstitutionsEnabled()) {
               ConfigImpl.trace(newContext.depth(), "  sourceForEnd before reset parents but after replace: " + sourceForEnd);
            }

            sourceForEnd = sourceForEnd.resetParents();
         } else {
            if (ConfigImpl.traceSubstitutionsEnabled()) {
               ConfigImpl.trace(newContext.depth(), "will resolve end against the original source with parent pushed");
            }

            sourceForEnd = source.pushParent(replaceable);
         }

         if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(newContext.depth(), "sourceForEnd      =" + sourceForEnd);
         }

         if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(newContext.depth(), "Resolving highest-priority item in delayed merge " + end + " against " + sourceForEnd + " endWasRemoved=" + (source != sourceForEnd));
         }

         ResolveResult<? extends AbstractConfigValue> result = newContext.resolve(end, sourceForEnd);
         AbstractConfigValue resolvedEnd = result.value;
         newContext = result.context;
         if (resolvedEnd != null) {
            if (merged == null) {
               merged = resolvedEnd;
            } else {
               if (ConfigImpl.traceSubstitutionsEnabled()) {
                  ConfigImpl.trace(newContext.depth() + 1, "merging " + merged + " with fallback " + resolvedEnd);
               }

               merged = merged.withFallback(resolvedEnd);
            }
         }

         ++count;
         if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(newContext.depth(), "stack merged, yielding: " + merged);
         }
      }

      return ResolveResult.<AbstractConfigValue>make(newContext, merged);
   }

   public AbstractConfigValue makeReplacement(ResolveContext context, int skipping) {
      return makeReplacement(context, this.stack, skipping);
   }

   static AbstractConfigValue makeReplacement(ResolveContext context, List<AbstractConfigValue> stack, int skipping) {
      List<AbstractConfigValue> subStack = stack.subList(skipping, stack.size());
      if (subStack.isEmpty()) {
         if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(context.depth(), "Nothing else in the merge stack, replacing with null");
         }

         return null;
      } else {
         AbstractConfigValue merged = null;

         for(AbstractConfigValue v : subStack) {
            if (merged == null) {
               merged = v;
            } else {
               merged = merged.withFallback(v);
            }
         }

         return merged;
      }
   }

   ResolveStatus resolveStatus() {
      return ResolveStatus.UNRESOLVED;
   }

   public AbstractConfigValue replaceChild(AbstractConfigValue child, AbstractConfigValue replacement) {
      List<AbstractConfigValue> newStack = replaceChildInList(this.stack, child, replacement);
      return newStack == null ? null : new ConfigDelayedMerge(this.origin(), newStack);
   }

   public boolean hasDescendant(AbstractConfigValue descendant) {
      return hasDescendantInList(this.stack, descendant);
   }

   ConfigDelayedMerge relativized(Path prefix) {
      List<AbstractConfigValue> newStack = new ArrayList();

      for(AbstractConfigValue o : this.stack) {
         newStack.add(o.relativized(prefix));
      }

      return new ConfigDelayedMerge(this.origin(), newStack);
   }

   static boolean stackIgnoresFallbacks(List<AbstractConfigValue> stack) {
      AbstractConfigValue last = (AbstractConfigValue)stack.get(stack.size() - 1);
      return last.ignoresFallbacks();
   }

   protected boolean ignoresFallbacks() {
      return stackIgnoresFallbacks(this.stack);
   }

   protected AbstractConfigValue newCopy(ConfigOrigin newOrigin) {
      return new ConfigDelayedMerge(newOrigin, this.stack);
   }

   protected final ConfigDelayedMerge mergedWithTheUnmergeable(Unmergeable fallback) {
      return (ConfigDelayedMerge)this.mergedWithTheUnmergeable(this.stack, fallback);
   }

   protected final ConfigDelayedMerge mergedWithObject(AbstractConfigObject fallback) {
      return (ConfigDelayedMerge)this.mergedWithObject(this.stack, fallback);
   }

   protected ConfigDelayedMerge mergedWithNonObject(AbstractConfigValue fallback) {
      return (ConfigDelayedMerge)this.mergedWithNonObject(this.stack, fallback);
   }

   public Collection<AbstractConfigValue> unmergedValues() {
      return this.stack;
   }

   protected boolean canEqual(Object other) {
      return other instanceof ConfigDelayedMerge;
   }

   public boolean equals(Object other) {
      if (!(other instanceof ConfigDelayedMerge)) {
         return false;
      } else {
         return this.canEqual(other) && (this.stack == ((ConfigDelayedMerge)other).stack || this.stack.equals(((ConfigDelayedMerge)other).stack));
      }
   }

   public int hashCode() {
      return this.stack.hashCode();
   }

   protected void render(StringBuilder sb, int indent, boolean atRoot, String atKey, ConfigRenderOptions options) {
      render(this.stack, sb, indent, atRoot, atKey, options);
   }

   protected void render(StringBuilder sb, int indent, boolean atRoot, ConfigRenderOptions options) {
      this.render(sb, indent, atRoot, (String)null, options);
   }

   static void render(List<AbstractConfigValue> stack, StringBuilder sb, int indent, boolean atRoot, String atKey, ConfigRenderOptions options) {
      boolean commentMerge = options.getComments();
      if (commentMerge) {
         sb.append("# unresolved merge of " + stack.size() + " values follows (\n");
         if (atKey == null) {
            indent(sb, indent, options);
            sb.append("# this unresolved merge will not be parseable because it's at the root of the object\n");
            indent(sb, indent, options);
            sb.append("# the HOCON format has no way to list multiple root objects in a single file\n");
         }
      }

      List<AbstractConfigValue> reversed = new ArrayList();
      reversed.addAll(stack);
      Collections.reverse(reversed);
      int i = 0;

      for(AbstractConfigValue v : reversed) {
         if (commentMerge) {
            indent(sb, indent, options);
            if (atKey != null) {
               sb.append("#     unmerged value " + i + " for key " + ConfigImplUtil.renderJsonString(atKey) + " from ");
            } else {
               sb.append("#     unmerged value " + i + " from ");
            }

            ++i;
            sb.append(v.origin().description());
            sb.append("\n");

            for(String comment : v.origin().comments()) {
               indent(sb, indent, options);
               sb.append("# ");
               sb.append(comment);
               sb.append("\n");
            }
         }

         indent(sb, indent, options);
         if (atKey != null) {
            sb.append(ConfigImplUtil.renderJsonString(atKey));
            if (options.getFormatted()) {
               sb.append(" : ");
            } else {
               sb.append(":");
            }
         }

         v.render(sb, indent, atRoot, options);
         sb.append(",");
         if (options.getFormatted()) {
            sb.append('\n');
         }
      }

      sb.setLength(sb.length() - 1);
      if (options.getFormatted()) {
         sb.setLength(sb.length() - 1);
         sb.append("\n");
      }

      if (commentMerge) {
         indent(sb, indent, options);
         sb.append("# ) end of unresolved merge\n");
      }

   }
}
