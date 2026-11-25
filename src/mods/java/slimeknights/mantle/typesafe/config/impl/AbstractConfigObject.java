package slimeknights.mantle.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import slimeknights.mantle.typesafe.config.ConfigException;
import slimeknights.mantle.typesafe.config.ConfigMergeable;
import slimeknights.mantle.typesafe.config.ConfigObject;
import slimeknights.mantle.typesafe.config.ConfigOrigin;
import slimeknights.mantle.typesafe.config.ConfigRenderOptions;
import slimeknights.mantle.typesafe.config.ConfigValue;
import slimeknights.mantle.typesafe.config.ConfigValueType;

abstract class AbstractConfigObject extends AbstractConfigValue implements ConfigObject, Container {
   private final SimpleConfig config = new SimpleConfig(this);

   protected AbstractConfigObject(ConfigOrigin origin) {
      super(origin);
   }

   public SimpleConfig toConfig() {
      return this.config;
   }

   public AbstractConfigObject toFallbackValue() {
      return this;
   }

   public abstract AbstractConfigObject withOnlyKey(String var1);

   public abstract AbstractConfigObject withoutKey(String var1);

   public abstract AbstractConfigObject withValue(String var1, ConfigValue var2);

   protected abstract AbstractConfigObject withOnlyPathOrNull(Path var1);

   abstract AbstractConfigObject withOnlyPath(Path var1);

   abstract AbstractConfigObject withoutPath(Path var1);

   abstract AbstractConfigObject withValue(Path var1, ConfigValue var2);

   protected final AbstractConfigValue peekAssumingResolved(String key, Path originalPath) {
      try {
         return this.attemptPeekWithPartialResolve(key);
      } catch (ConfigException.NotResolved var4) {
         throw ConfigImpl.improveNotResolved(originalPath, var4);
      }
   }

   abstract AbstractConfigValue attemptPeekWithPartialResolve(String var1);

   protected AbstractConfigValue peekPath(Path path) {
      return peekPath(this, path);
   }

   private static AbstractConfigValue peekPath(AbstractConfigObject self, Path path) {
      try {
         Path next = path.remainder();
         AbstractConfigValue v = self.attemptPeekWithPartialResolve(path.first());
         if (next == null) {
            return v;
         } else {
            return v instanceof AbstractConfigObject ? peekPath((AbstractConfigObject)v, next) : null;
         }
      } catch (ConfigException.NotResolved var4) {
         throw ConfigImpl.improveNotResolved(path, var4);
      }
   }

   public ConfigValueType valueType() {
      return ConfigValueType.OBJECT;
   }

   protected abstract AbstractConfigObject newCopy(ResolveStatus var1, ConfigOrigin var2);

   protected AbstractConfigObject newCopy(ConfigOrigin origin) {
      return this.newCopy(this.resolveStatus(), origin);
   }

   protected AbstractConfigObject constructDelayedMerge(ConfigOrigin origin, List<AbstractConfigValue> stack) {
      return new ConfigDelayedMergeObject(origin, stack);
   }

   protected abstract AbstractConfigObject mergedWithObject(AbstractConfigObject var1);

   public AbstractConfigObject withFallback(ConfigMergeable mergeable) {
      return (AbstractConfigObject)super.withFallback(mergeable);
   }

   static ConfigOrigin mergeOrigins(Collection<? extends AbstractConfigValue> stack) {
      if (stack.isEmpty()) {
         throw new ConfigException.BugOrBroken("can't merge origins on empty list");
      } else {
         List<ConfigOrigin> origins = new ArrayList();
         ConfigOrigin firstOrigin = null;
         int numMerged = 0;

         for(AbstractConfigValue v : stack) {
            if (firstOrigin == null) {
               firstOrigin = v.origin();
            }

            if (!(v instanceof AbstractConfigObject) || ((AbstractConfigObject)v).resolveStatus() != ResolveStatus.RESOLVED || !((ConfigObject)v).isEmpty()) {
               origins.add(v.origin());
               ++numMerged;
            }
         }

         if (numMerged == 0) {
            origins.add(firstOrigin);
         }

         return SimpleConfigOrigin.mergeOrigins(origins);
      }
   }

   static ConfigOrigin mergeOrigins(AbstractConfigObject... stack) {
      return mergeOrigins(Arrays.asList(stack));
   }

   abstract ResolveResult<? extends AbstractConfigObject> resolveSubstitutions(ResolveContext var1, ResolveSource var2) throws AbstractConfigValue.NotPossibleToResolve;

   abstract AbstractConfigObject relativized(Path var1);

   public abstract AbstractConfigValue get(Object var1);

   protected abstract void render(StringBuilder var1, int var2, boolean var3, ConfigRenderOptions var4);

   private static UnsupportedOperationException weAreImmutable(String method) {
      return new UnsupportedOperationException("ConfigObject is immutable, you can't call Map." + method);
   }

   public void clear() {
      throw weAreImmutable("clear");
   }

   public ConfigValue put(String arg0, ConfigValue arg1) {
      throw weAreImmutable("put");
   }

   public void putAll(Map<? extends String, ? extends ConfigValue> arg0) {
      throw weAreImmutable("putAll");
   }

   public ConfigValue remove(Object arg0) {
      throw weAreImmutable("remove");
   }

   public AbstractConfigObject withOrigin(ConfigOrigin origin) {
      return (AbstractConfigObject)super.withOrigin(origin);
   }
}
