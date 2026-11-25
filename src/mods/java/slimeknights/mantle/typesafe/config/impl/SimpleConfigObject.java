package slimeknights.mantle.typesafe.config.impl;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import slimeknights.mantle.typesafe.config.ConfigException;
import slimeknights.mantle.typesafe.config.ConfigObject;
import slimeknights.mantle.typesafe.config.ConfigOrigin;
import slimeknights.mantle.typesafe.config.ConfigRenderOptions;
import slimeknights.mantle.typesafe.config.ConfigValue;

final class SimpleConfigObject extends AbstractConfigObject implements Serializable {
   private static final long serialVersionUID = 2L;
   private final Map<String, AbstractConfigValue> value;
   private final boolean resolved;
   private final boolean ignoresFallbacks;
   private static final String EMPTY_NAME = "empty config";
   private static final SimpleConfigObject emptyInstance = empty(SimpleConfigOrigin.newSimple("empty config"));

   SimpleConfigObject(ConfigOrigin origin, Map<String, AbstractConfigValue> value, ResolveStatus status, boolean ignoresFallbacks) {
      super(origin);
      if (value == null) {
         throw new ConfigException.BugOrBroken("creating config object with null map");
      } else {
         this.value = value;
         this.resolved = status == ResolveStatus.RESOLVED;
         this.ignoresFallbacks = ignoresFallbacks;
         if (status != ResolveStatus.fromValues(value.values())) {
            throw new ConfigException.BugOrBroken("Wrong resolved status on " + this);
         }
      }
   }

   SimpleConfigObject(ConfigOrigin origin, Map<String, AbstractConfigValue> value) {
      this(origin, value, ResolveStatus.fromValues(value.values()), false);
   }

   public SimpleConfigObject withOnlyKey(String key) {
      return this.withOnlyPath(Path.newKey(key));
   }

   public SimpleConfigObject withoutKey(String key) {
      return this.withoutPath(Path.newKey(key));
   }

   protected SimpleConfigObject withOnlyPathOrNull(Path path) {
      String key = path.first();
      Path next = path.remainder();
      AbstractConfigValue v = (AbstractConfigValue)this.value.get(key);
      if (next != null) {
         if (v != null && v instanceof AbstractConfigObject) {
            v = ((AbstractConfigObject)v).withOnlyPathOrNull(next);
         } else {
            v = null;
         }
      }

      return v == null ? null : new SimpleConfigObject(this.origin(), Collections.singletonMap(key, v), v.resolveStatus(), this.ignoresFallbacks);
   }

   SimpleConfigObject withOnlyPath(Path path) {
      SimpleConfigObject o = this.withOnlyPathOrNull(path);
      return o == null ? new SimpleConfigObject(this.origin(), Collections.emptyMap(), ResolveStatus.RESOLVED, this.ignoresFallbacks) : o;
   }

   SimpleConfigObject withoutPath(Path path) {
      String key = path.first();
      Path next = path.remainder();
      AbstractConfigValue v = (AbstractConfigValue)this.value.get(key);
      if (v != null && next != null && v instanceof AbstractConfigObject) {
         AbstractConfigValue var8 = ((AbstractConfigObject)v).withoutPath(next);
         Map<String, AbstractConfigValue> updated = new HashMap(this.value);
         updated.put(key, var8);
         return new SimpleConfigObject(this.origin(), updated, ResolveStatus.fromValues(updated.values()), this.ignoresFallbacks);
      } else if (next == null && v != null) {
         Map<String, AbstractConfigValue> smaller = new HashMap(this.value.size() - 1);

         for(Entry<String, AbstractConfigValue> old : this.value.entrySet()) {
            if (!((String)old.getKey()).equals(key)) {
               smaller.put(old.getKey(), old.getValue());
            }
         }

         return new SimpleConfigObject(this.origin(), smaller, ResolveStatus.fromValues(smaller.values()), this.ignoresFallbacks);
      } else {
         return this;
      }
   }

   public SimpleConfigObject withValue(String key, ConfigValue v) {
      if (v == null) {
         throw new ConfigException.BugOrBroken("Trying to store null ConfigValue in a ConfigObject");
      } else {
         Map<String, AbstractConfigValue> newMap;
         if (this.value.isEmpty()) {
            newMap = Collections.singletonMap(key, (AbstractConfigValue)v);
         } else {
            newMap = new HashMap(this.value);
            newMap.put(key, (AbstractConfigValue)v);
         }

         return new SimpleConfigObject(this.origin(), newMap, ResolveStatus.fromValues(newMap.values()), this.ignoresFallbacks);
      }
   }

   SimpleConfigObject withValue(Path path, ConfigValue v) {
      String key = path.first();
      Path next = path.remainder();
      if (next == null) {
         return this.withValue(key, v);
      } else {
         AbstractConfigValue child = (AbstractConfigValue)this.value.get(key);
         if (child != null && child instanceof AbstractConfigObject) {
            return this.withValue(key, ((AbstractConfigObject)child).withValue(next, v));
         } else {
            SimpleConfig subtree = ((AbstractConfigValue)v).atPath(SimpleConfigOrigin.newSimple("withValue(" + next.render() + ")"), next);
            return this.withValue(key, subtree.root());
         }
      }
   }

   protected AbstractConfigValue attemptPeekWithPartialResolve(String key) {
      return (AbstractConfigValue)this.value.get(key);
   }

   private SimpleConfigObject newCopy(ResolveStatus newStatus, ConfigOrigin newOrigin, boolean newIgnoresFallbacks) {
      return new SimpleConfigObject(newOrigin, this.value, newStatus, newIgnoresFallbacks);
   }

   protected SimpleConfigObject newCopy(ResolveStatus newStatus, ConfigOrigin newOrigin) {
      return this.newCopy(newStatus, newOrigin, this.ignoresFallbacks);
   }

   protected SimpleConfigObject withFallbacksIgnored() {
      return this.ignoresFallbacks ? this : this.newCopy(this.resolveStatus(), this.origin(), true);
   }

   ResolveStatus resolveStatus() {
      return ResolveStatus.fromBoolean(this.resolved);
   }

   public SimpleConfigObject replaceChild(AbstractConfigValue child, AbstractConfigValue replacement) {
      HashMap<String, AbstractConfigValue> newChildren = new HashMap(this.value);

      for(Entry<String, AbstractConfigValue> old : newChildren.entrySet()) {
         if (old.getValue() == child) {
            if (replacement != null) {
               old.setValue(replacement);
            } else {
               newChildren.remove(old.getKey());
            }

            return new SimpleConfigObject(this.origin(), newChildren, ResolveStatus.fromValues(newChildren.values()), this.ignoresFallbacks);
         }
      }

      throw new ConfigException.BugOrBroken("SimpleConfigObject.replaceChild did not find " + child + " in " + this);
   }

   public boolean hasDescendant(AbstractConfigValue descendant) {
      for(AbstractConfigValue child : this.value.values()) {
         if (child == descendant) {
            return true;
         }
      }

      for(AbstractConfigValue child : this.value.values()) {
         if (child instanceof Container && ((Container)child).hasDescendant(descendant)) {
            return true;
         }
      }

      return false;
   }

   protected boolean ignoresFallbacks() {
      return this.ignoresFallbacks;
   }

   public Map<String, Object> unwrapped() {
      Map<String, Object> m = new HashMap();

      for(Entry<String, AbstractConfigValue> e : this.value.entrySet()) {
         m.put(e.getKey(), ((AbstractConfigValue)e.getValue()).unwrapped());
      }

      return m;
   }

   protected SimpleConfigObject mergedWithObject(AbstractConfigObject abstractFallback) {
      this.requireNotIgnoringFallbacks();
      if (!(abstractFallback instanceof SimpleConfigObject)) {
         throw new ConfigException.BugOrBroken("should not be reached (merging non-SimpleConfigObject)");
      } else {
         SimpleConfigObject fallback = (SimpleConfigObject)abstractFallback;
         boolean changed = false;
         boolean allResolved = true;
         Map<String, AbstractConfigValue> merged = new HashMap();
         Set<String> allKeys = new HashSet();
         allKeys.addAll(this.keySet());
         allKeys.addAll(fallback.keySet());

         for(String key : allKeys) {
            AbstractConfigValue first = (AbstractConfigValue)this.value.get(key);
            AbstractConfigValue second = (AbstractConfigValue)fallback.value.get(key);
            AbstractConfigValue kept;
            if (first == null) {
               kept = second;
            } else if (second == null) {
               kept = first;
            } else {
               kept = first.withFallback(second);
            }

            merged.put(key, kept);
            if (first != kept) {
               changed = true;
            }

            if (kept.resolveStatus() == ResolveStatus.UNRESOLVED) {
               allResolved = false;
            }
         }

         ResolveStatus newResolveStatus = ResolveStatus.fromBoolean(allResolved);
         boolean newIgnoresFallbacks = fallback.ignoresFallbacks();
         if (changed) {
            return new SimpleConfigObject(mergeOrigins(new AbstractConfigObject[]{this, fallback}), merged, newResolveStatus, newIgnoresFallbacks);
         } else if (newResolveStatus == this.resolveStatus() && newIgnoresFallbacks == this.ignoresFallbacks()) {
            return this;
         } else {
            return this.newCopy(newResolveStatus, this.origin(), newIgnoresFallbacks);
         }
      }
   }

   private SimpleConfigObject modify(AbstractConfigValue.NoExceptionsModifier modifier) {
      try {
         return this.modifyMayThrow(modifier);
      } catch (RuntimeException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new ConfigException.BugOrBroken("unexpected checked exception", var4);
      }
   }

   private SimpleConfigObject modifyMayThrow(AbstractConfigValue.Modifier modifier) throws Exception {
      Map<String, AbstractConfigValue> changes = null;

      for(String k : this.keySet()) {
         AbstractConfigValue v = (AbstractConfigValue)this.value.get(k);
         AbstractConfigValue modified = modifier.modifyChildMayThrow(k, v);
         if (modified != v) {
            if (changes == null) {
               changes = new HashMap();
            }

            changes.put(k, modified);
         }
      }

      if (changes == null) {
         return this;
      } else {
         Map<String, AbstractConfigValue> modified = new HashMap();
         boolean sawUnresolved = false;

         for(String k : this.keySet()) {
            if (changes.containsKey(k)) {
               AbstractConfigValue newValue = (AbstractConfigValue)changes.get(k);
               if (newValue != null) {
                  modified.put(k, newValue);
                  if (newValue.resolveStatus() == ResolveStatus.UNRESOLVED) {
                     sawUnresolved = true;
                  }
               }
            } else {
               AbstractConfigValue newValue = (AbstractConfigValue)this.value.get(k);
               modified.put(k, newValue);
               if (newValue.resolveStatus() == ResolveStatus.UNRESOLVED) {
                  sawUnresolved = true;
               }
            }
         }

         return new SimpleConfigObject(this.origin(), modified, sawUnresolved ? ResolveStatus.UNRESOLVED : ResolveStatus.RESOLVED, this.ignoresFallbacks());
      }
   }

   ResolveResult<? extends AbstractConfigObject> resolveSubstitutions(ResolveContext context, ResolveSource source) throws AbstractConfigValue.NotPossibleToResolve {
      if (this.resolveStatus() == ResolveStatus.RESOLVED) {
         return ResolveResult.<AbstractConfigObject>make(context, this);
      } else {
         ResolveSource sourceWithParent = source.pushParent(this);

         try {
            SimpleConfigObject.ResolveModifier modifier = new SimpleConfigObject.ResolveModifier(context, sourceWithParent);
            AbstractConfigValue value = this.modifyMayThrow(modifier);
            return ResolveResult.<AbstractConfigObject>make(modifier.context, value).asObjectResult();
         } catch (AbstractConfigValue.NotPossibleToResolve var6) {
            throw var6;
         } catch (RuntimeException var7) {
            throw var7;
         } catch (Exception var8) {
            throw new ConfigException.BugOrBroken("unexpected checked exception", var8);
         }
      }
   }

   SimpleConfigObject relativized(final Path prefix) {
      return this.modify(new AbstractConfigValue.NoExceptionsModifier() {
         public AbstractConfigValue modifyChild(String key, AbstractConfigValue v) {
            return v.relativized(prefix);
         }
      });
   }

   protected void render(StringBuilder sb, int indent, boolean atRoot, ConfigRenderOptions options) {
      if (this.isEmpty()) {
         sb.append("{}");
      } else {
         boolean outerBraces = options.getJson() || !atRoot;
         int innerIndent;
         if (outerBraces) {
            innerIndent = indent + 1;
            sb.append("{");
            if (options.getFormatted()) {
               sb.append('\n');
            }
         } else {
            innerIndent = indent;
         }

         int separatorCount = 0;
         String[] keys = (String[])this.keySet().toArray(new String[this.size()]);
         Arrays.sort(keys, new SimpleConfigObject.RenderComparator());

         for(String k : keys) {
            AbstractConfigValue v = (AbstractConfigValue)this.value.get(k);
            if (options.getOriginComments()) {
               String[] lines = v.origin().description().split("\n");

               for(String l : lines) {
                  indent(sb, indent + 1, options);
                  sb.append('#');
                  if (!l.isEmpty()) {
                     sb.append(' ');
                  }

                  sb.append(l);
                  sb.append("\n");
               }
            }

            if (options.getComments()) {
               for(String comment : v.origin().comments()) {
                  indent(sb, innerIndent, options);
                  sb.append("#");
                  if (!comment.startsWith(" ")) {
                     sb.append(' ');
                  }

                  sb.append(comment);
                  sb.append("\n");
               }
            }

            indent(sb, innerIndent, options);
            v.render(sb, innerIndent, false, k, options);
            if (options.getFormatted()) {
               if (options.getJson()) {
                  sb.append(",");
                  separatorCount = 2;
               } else {
                  separatorCount = 1;
               }

               sb.append('\n');
            } else {
               sb.append(",");
               separatorCount = 1;
            }
         }

         sb.setLength(sb.length() - separatorCount);
         if (outerBraces) {
            if (options.getFormatted()) {
               sb.append('\n');
               if (outerBraces) {
                  indent(sb, indent, options);
               }
            }

            sb.append("}");
         }
      }

      if (atRoot && options.getFormatted()) {
         sb.append('\n');
      }

   }

   public AbstractConfigValue get(Object key) {
      return (AbstractConfigValue)this.value.get(key);
   }

   private static boolean mapEquals(Map<String, ConfigValue> a, Map<String, ConfigValue> b) {
      if (a == b) {
         return true;
      } else {
         Set<String> aKeys = a.keySet();
         Set<String> bKeys = b.keySet();
         if (!aKeys.equals(bKeys)) {
            return false;
         } else {
            for(String key : aKeys) {
               if (!((ConfigValue)a.get(key)).equals(b.get(key))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private static int mapHash(Map<String, ConfigValue> m) {
      List<String> keys = new ArrayList();
      keys.addAll(m.keySet());
      Collections.sort(keys);
      int valuesHash = 0;

      for(String k : keys) {
         valuesHash += ((ConfigValue)m.get(k)).hashCode();
      }

      return 41 * (41 + keys.hashCode()) + valuesHash;
   }

   protected boolean canEqual(Object other) {
      return other instanceof ConfigObject;
   }

   public boolean equals(Object other) {
      if (!(other instanceof ConfigObject)) {
         return false;
      } else {
         return this.canEqual(other) && mapEquals(this, (ConfigObject)other);
      }
   }

   public int hashCode() {
      return mapHash(this);
   }

   public boolean containsKey(Object key) {
      return this.value.containsKey(key);
   }

   public Set<String> keySet() {
      return this.value.keySet();
   }

   public boolean containsValue(Object v) {
      return this.value.containsValue(v);
   }

   public Set<Entry<String, ConfigValue>> entrySet() {
      HashSet<Entry<String, ConfigValue>> entries = new HashSet();

      for(Entry<String, AbstractConfigValue> e : this.value.entrySet()) {
         entries.add(new SimpleImmutableEntry(e.getKey(), e.getValue()));
      }

      return entries;
   }

   public boolean isEmpty() {
      return this.value.isEmpty();
   }

   public int size() {
      return this.value.size();
   }

   public Collection<ConfigValue> values() {
      return new HashSet(this.value.values());
   }

   static final SimpleConfigObject empty() {
      return emptyInstance;
   }

   static final SimpleConfigObject empty(ConfigOrigin origin) {
      return origin == null ? empty() : new SimpleConfigObject(origin, Collections.emptyMap());
   }

   static final SimpleConfigObject emptyMissing(ConfigOrigin baseOrigin) {
      return new SimpleConfigObject(SimpleConfigOrigin.newSimple(baseOrigin.description() + " (not found)"), Collections.emptyMap());
   }

   private Object writeReplace() throws ObjectStreamException {
      return new SerializedConfigValue(this);
   }

   private static final class RenderComparator implements Comparator<String>, Serializable {
      private static final long serialVersionUID = 1L;

      private RenderComparator() {
      }

      private static boolean isAllDigits(String s) {
         int length = s.length();
         if (length == 0) {
            return false;
         } else {
            for(int i = 0; i < length; ++i) {
               char c = s.charAt(i);
               if (!Character.isDigit(c)) {
                  return false;
               }
            }

            return true;
         }
      }

      public int compare(String a, String b) {
         boolean aDigits = isAllDigits(a);
         boolean bDigits = isAllDigits(b);
         if (aDigits && bDigits) {
            return Integer.compare(Integer.parseInt(a), Integer.parseInt(b));
         } else if (aDigits) {
            return -1;
         } else {
            return bDigits ? 1 : a.compareTo(b);
         }
      }
   }

   private static final class ResolveModifier implements AbstractConfigValue.Modifier {
      final Path originalRestrict;
      ResolveContext context;
      final ResolveSource source;

      ResolveModifier(ResolveContext context, ResolveSource source) {
         this.context = context;
         this.source = source;
         this.originalRestrict = context.restrictToChild();
      }

      public AbstractConfigValue modifyChildMayThrow(String key, AbstractConfigValue v) throws AbstractConfigValue.NotPossibleToResolve {
         if (this.context.isRestrictedToChild()) {
            if (key.equals(this.context.restrictToChild().first())) {
               Path remainder = this.context.restrictToChild().remainder();
               if (remainder != null) {
                  ResolveResult<? extends AbstractConfigValue> result = this.context.restrict(remainder).resolve(v, this.source);
                  this.context = result.context.unrestricted().restrict(this.originalRestrict);
                  return result.value;
               } else {
                  return v;
               }
            } else {
               return v;
            }
         } else {
            ResolveResult<? extends AbstractConfigValue> result = this.context.unrestricted().resolve(v, this.source);
            this.context = result.context.unrestricted().restrict(this.originalRestrict);
            return result.value;
         }
      }
   }
}
