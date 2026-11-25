package slimeknights.mantle.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import slimeknights.mantle.typesafe.config.ConfigException;
import slimeknights.mantle.typesafe.config.ConfigResolveOptions;

final class ResolveContext {
   private final ResolveMemos memos;
   private final ConfigResolveOptions options;
   private final Path restrictToChild;
   private final List<AbstractConfigValue> resolveStack;
   private final Set<AbstractConfigValue> cycleMarkers;

   ResolveContext(ResolveMemos memos, ConfigResolveOptions options, Path restrictToChild, List<AbstractConfigValue> resolveStack, Set<AbstractConfigValue> cycleMarkers) {
      this.memos = memos;
      this.options = options;
      this.restrictToChild = restrictToChild;
      this.resolveStack = Collections.unmodifiableList(resolveStack);
      this.cycleMarkers = Collections.unmodifiableSet(cycleMarkers);
   }

   private static Set<AbstractConfigValue> newCycleMarkers() {
      return Collections.newSetFromMap(new IdentityHashMap());
   }

   ResolveContext(ConfigResolveOptions options, Path restrictToChild) {
      this(new ResolveMemos(), options, restrictToChild, new ArrayList(), newCycleMarkers());
      if (ConfigImpl.traceSubstitutionsEnabled()) {
         ConfigImpl.trace(this.depth(), "ResolveContext restrict to child " + restrictToChild);
      }

   }

   ResolveContext addCycleMarker(AbstractConfigValue value) {
      if (ConfigImpl.traceSubstitutionsEnabled()) {
         ConfigImpl.trace(this.depth(), "++ Cycle marker " + value + "@" + System.identityHashCode(value));
      }

      if (this.cycleMarkers.contains(value)) {
         throw new ConfigException.BugOrBroken("Added cycle marker twice " + value);
      } else {
         Set<AbstractConfigValue> copy = newCycleMarkers();
         copy.addAll(this.cycleMarkers);
         copy.add(value);
         return new ResolveContext(this.memos, this.options, this.restrictToChild, this.resolveStack, copy);
      }
   }

   ResolveContext removeCycleMarker(AbstractConfigValue value) {
      if (ConfigImpl.traceSubstitutionsEnabled()) {
         ConfigImpl.trace(this.depth(), "-- Cycle marker " + value + "@" + System.identityHashCode(value));
      }

      Set<AbstractConfigValue> copy = newCycleMarkers();
      copy.addAll(this.cycleMarkers);
      copy.remove(value);
      return new ResolveContext(this.memos, this.options, this.restrictToChild, this.resolveStack, copy);
   }

   private ResolveContext memoize(MemoKey key, AbstractConfigValue value) {
      ResolveMemos changed = this.memos.put(key, value);
      return new ResolveContext(changed, this.options, this.restrictToChild, this.resolveStack, this.cycleMarkers);
   }

   ConfigResolveOptions options() {
      return this.options;
   }

   boolean isRestrictedToChild() {
      return this.restrictToChild != null;
   }

   Path restrictToChild() {
      return this.restrictToChild;
   }

   ResolveContext restrict(Path restrictTo) {
      return restrictTo == this.restrictToChild ? this : new ResolveContext(this.memos, this.options, restrictTo, this.resolveStack, this.cycleMarkers);
   }

   ResolveContext unrestricted() {
      return this.restrict((Path)null);
   }

   String traceString() {
      String separator = ", ";
      StringBuilder sb = new StringBuilder();

      for(AbstractConfigValue value : this.resolveStack) {
         if (value instanceof ConfigReference) {
            sb.append(((ConfigReference)value).expression().toString());
            sb.append(separator);
         }
      }

      if (sb.length() > 0) {
         sb.setLength(sb.length() - separator.length());
      }

      return sb.toString();
   }

   private ResolveContext pushTrace(AbstractConfigValue value) {
      if (ConfigImpl.traceSubstitutionsEnabled()) {
         ConfigImpl.trace(this.depth(), "pushing trace " + value);
      }

      List<AbstractConfigValue> copy = new ArrayList(this.resolveStack);
      copy.add(value);
      return new ResolveContext(this.memos, this.options, this.restrictToChild, copy, this.cycleMarkers);
   }

   ResolveContext popTrace() {
      List<AbstractConfigValue> copy = new ArrayList(this.resolveStack);
      AbstractConfigValue old = (AbstractConfigValue)copy.remove(this.resolveStack.size() - 1);
      if (ConfigImpl.traceSubstitutionsEnabled()) {
         ConfigImpl.trace(this.depth() - 1, "popped trace " + old);
      }

      return new ResolveContext(this.memos, this.options, this.restrictToChild, copy, this.cycleMarkers);
   }

   int depth() {
      if (this.resolveStack.size() > 30) {
         throw new ConfigException.BugOrBroken("resolve getting too deep");
      } else {
         return this.resolveStack.size();
      }
   }

   ResolveResult<? extends AbstractConfigValue> resolve(AbstractConfigValue original, ResolveSource source) throws AbstractConfigValue.NotPossibleToResolve {
      if (ConfigImpl.traceSubstitutionsEnabled()) {
         ConfigImpl.trace(this.depth(), "resolving " + original + " restrictToChild=" + this.restrictToChild + " in " + source);
      }

      return this.pushTrace(original).realResolve(original, source).popTrace();
   }

   private ResolveResult<? extends AbstractConfigValue> realResolve(AbstractConfigValue original, ResolveSource source) throws AbstractConfigValue.NotPossibleToResolve {
      MemoKey fullKey = new MemoKey(original, (Path)null);
      MemoKey restrictedKey = null;
      AbstractConfigValue cached = this.memos.get(fullKey);
      if (cached == null && this.isRestrictedToChild()) {
         restrictedKey = new MemoKey(original, this.restrictToChild());
         cached = this.memos.get(restrictedKey);
      }

      if (cached != null) {
         if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(this.depth(), "using cached resolution " + cached + " for " + original + " restrictToChild " + this.restrictToChild());
         }

         return ResolveResult.<AbstractConfigValue>make(this, cached);
      } else {
         if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(this.depth(), "not found in cache, resolving " + original + "@" + System.identityHashCode(original));
         }

         if (this.cycleMarkers.contains(original)) {
            if (ConfigImpl.traceSubstitutionsEnabled()) {
               ConfigImpl.trace(this.depth(), "Cycle detected, can't resolve; " + original + "@" + System.identityHashCode(original));
            }

            throw new AbstractConfigValue.NotPossibleToResolve(this);
         } else {
            ResolveResult<? extends AbstractConfigValue> result = original.resolveSubstitutions(this, source);
            AbstractConfigValue resolved = result.value;
            if (ConfigImpl.traceSubstitutionsEnabled()) {
               ConfigImpl.trace(this.depth(), "resolved to " + resolved + "@" + System.identityHashCode(resolved) + " from " + original + "@" + System.identityHashCode(resolved));
            }

            ResolveContext withMemo = result.context;
            if (resolved != null && resolved.resolveStatus() != ResolveStatus.RESOLVED) {
               if (this.isRestrictedToChild()) {
                  if (restrictedKey == null) {
                     throw new ConfigException.BugOrBroken("restrictedKey should not be null here");
                  }

                  if (ConfigImpl.traceSubstitutionsEnabled()) {
                     ConfigImpl.trace(this.depth(), "caching " + restrictedKey + " result " + resolved);
                  }

                  withMemo = withMemo.memoize(restrictedKey, resolved);
               } else {
                  if (!this.options().getAllowUnresolved()) {
                     throw new ConfigException.BugOrBroken("resolveSubstitutions() did not give us a resolved object");
                  }

                  if (ConfigImpl.traceSubstitutionsEnabled()) {
                     ConfigImpl.trace(this.depth(), "caching " + fullKey + " result " + resolved);
                  }

                  withMemo = withMemo.memoize(fullKey, resolved);
               }
            } else {
               if (ConfigImpl.traceSubstitutionsEnabled()) {
                  ConfigImpl.trace(this.depth(), "caching " + fullKey + " result " + resolved);
               }

               withMemo = withMemo.memoize(fullKey, resolved);
            }

            return ResolveResult.<AbstractConfigValue>make(withMemo, resolved);
         }
      }
   }

   static AbstractConfigValue resolve(AbstractConfigValue value, AbstractConfigObject root, ConfigResolveOptions options) {
      ResolveSource source = new ResolveSource(root);
      ResolveContext context = new ResolveContext(options, (Path)null);

      try {
         return context.resolve(value, source).value;
      } catch (AbstractConfigValue.NotPossibleToResolve var6) {
         throw new ConfigException.BugOrBroken("NotPossibleToResolve was thrown from an outermost resolve", var6);
      }
   }
}
