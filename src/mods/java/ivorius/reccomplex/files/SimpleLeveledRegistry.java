package ivorius.reccomplex.files;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.files.loading.LeveledRegistry;
import ivorius.reccomplex.utils.RawResourceLocation;
import ivorius.reccomplex.utils.leveled.LeveledBiMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SimpleLeveledRegistry<S> implements LeveledRegistry<S> {
   protected LeveledBiMap<String, S> items = new LeveledBiMap<String, S>(LeveledRegistry.Level.values().length);
   protected LeveledBiMap<String, SimpleLeveledRegistry<S>.Status> stati;
   protected boolean activeCacheValid;
   protected Map<String, S> activeMap;
   public String description;
   private Map<Class<? extends SimpleLeveledRegistry.Module<SimpleLeveledRegistry<S>>>, SimpleLeveledRegistry.Module<SimpleLeveledRegistry<S>>> modules;

   public SimpleLeveledRegistry(String description) {
      this.stati = new LeveledBiMap<String, SimpleLeveledRegistry<S>.Status>(this.items.levels());
      this.activeCacheValid = false;
      this.activeMap = new HashMap();
      this.modules = new HashMap();
      this.description = description;
   }

   public LeveledBiMap<String, S> contents() {
      return this.items;
   }

   public LeveledBiMap<String, SimpleLeveledRegistry<S>.Status> stati() {
      return this.stati;
   }

   public Map<String, S> map() {
      return Collections.unmodifiableMap(this.items.getMap());
   }

   public Map<String, S> map(LeveledRegistry.ILevel level) {
      return Collections.unmodifiableMap(this.items.getMap(level.getLevel()));
   }

   public Map<String, S> activeMap() {
      this.ensureActiveCache();
      return Collections.unmodifiableMap(this.activeMap);
   }

   public Collection<S> all() {
      return Collections.unmodifiableCollection(this.items.getMap().values());
   }

   public Collection<S> allActive() {
      this.ensureActiveCache();
      return Collections.unmodifiableCollection(this.activeMap.values());
   }

   @Nullable
   public S getActive(String id) {
      this.ensureActiveCache();
      return (S)this.activeMap.get(id);
   }

   @Nullable
   public S get(String id) {
      return (S)this.items.getMap().get(id);
   }

   public SimpleLeveledRegistry<S>.Status status(String id) {
      return (SimpleLeveledRegistry.Status)this.stati.getMap().get(id);
   }

   @Nonnull
   public Set<String> activeIDs() {
      this.ensureActiveCache();
      return Collections.unmodifiableSet(this.activeMap.keySet());
   }

   @Nonnull
   public Set<String> ids() {
      return Collections.unmodifiableSet(this.items.getMap().keySet());
   }

   public boolean has(String id) {
      return this.items.getMap().containsKey(id);
   }

   public boolean hasActive(String id) {
      this.ensureActiveCache();
      return this.activeMap.containsKey(id);
   }

   public String id(S s) {
      return (String)this.items.getMap().inverse().get(s);
   }

   @Nullable
   public RawResourceLocation resourceLocation(S s) {
      String id = this.id(s);
      return id != null ? new RawResourceLocation(this.status(id).domain, id) : null;
   }

   public S register(String id, String domain, S s, boolean active, LeveledRegistry.ILevel level) {
      this.invalidateActiveCache();
      this.stati.put(id, new SimpleLeveledRegistry.Status(id, active, domain, level), level.getLevel());
      S old = (S)this.items.put(id, s, level.getLevel());
      RecurrentComplex.logger.trace(String.format(old != null ? "Replaced %s '%s' at level %s" : "Registered %s '%s' at level %s", this.description, id, level));
      this.invalidateCaches();
      return old;
   }

   public S unregister(String id, LeveledRegistry.ILevel level) {
      this.invalidateActiveCache();
      this.invalidateCaches();
      this.stati.remove(id, level.getLevel());
      return (S)this.items.remove(id, level.getLevel());
   }

   public void clear(LeveledRegistry.ILevel level) {
      RecurrentComplex.logger.trace(String.format("Cleared all %s at level %s", this.description, level));
      this.invalidateActiveCache();
      this.items.clear(level.getLevel());
      this.stati.clear(level.getLevel());
   }

   private void ensureActiveCache() {
      if (!this.activeCacheValid) {
         this.activeMap = (Map)this.stati.getMap().values().stream().filter(LeveledRegistry.Status::isActive).map(LeveledRegistry.Status::getId).collect(Collectors.toMap((s) -> {
            return s;
         }, (s) -> {
            return this.items.getMap().get(s);
         }));
         this.activeCacheValid = true;
      }

   }

   private void invalidateActiveCache() {
      this.activeCacheValid = false;
   }

   public <T extends SimpleLeveledRegistry.Module<SimpleLeveledRegistry<S>>> void registerModule(Class<T> type, T cache) {
      this.modules.put(type, cache);
      cache.setRegistry(this);
   }

   public <T extends SimpleLeveledRegistry.Module> void registerModule(T cache) {
      this.registerModule(cache.getClass(), cache);
   }

   public <T extends SimpleLeveledRegistry.Module> T module(Class<T> cache) {
      return (T)(this.modules.get(cache));
   }

   protected void invalidateCaches() {
      this.modules.values().forEach(SimpleLeveledRegistry.Module::invalidate);
   }

   public abstract static class Module<R extends SimpleLeveledRegistry> {
      protected R registry;

      public Module() {
      }

      public void setRegistry(R registry) {
         this.registry = registry;
      }

      public abstract void invalidate();
   }

   public class Status implements LeveledRegistry.Status {
      protected String id;
      protected boolean active;
      protected String domain;
      protected LeveledRegistry.ILevel level;

      public Status(String id, boolean active, String domain, LeveledRegistry.ILevel level) {
         this.id = id;
         this.active = active;
         this.domain = domain;
         this.level = level;
      }

      public String getId() {
         return this.id;
      }

      public boolean isActive() {
         return this.active;
      }

      public void setActive(boolean active) {
         this.active = active;
         SimpleLeveledRegistry.this.invalidateActiveCache();
      }

      public String getDomain() {
         return this.domain;
      }

      public LeveledRegistry.ILevel getLevel() {
         return this.level;
      }
   }
}
