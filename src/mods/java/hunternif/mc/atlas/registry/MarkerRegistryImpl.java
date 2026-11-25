package hunternif.mc.atlas.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import org.apache.commons.lang3.Validate;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

class MarkerRegistryImpl<V extends IRegistryEntry> implements IRegistry<ResourceLocation, V> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, V> registryObjects = this.createUnderlyingMap();
   private List<V> values;
   private final ResourceLocation defaultKey;

   public MarkerRegistryImpl(ResourceLocation defaultKey) {
      this.defaultKey = defaultKey;
   }

   private Map<ResourceLocation, V> createUnderlyingMap() {
      return Maps.newHashMap();
   }

   @Nullable
   public V getObject(@Nullable ResourceLocation name) {
      V value = this.registryObjects.get(name);
      if (value == null) {
         value = this.registryObjects.get(this.defaultKey);
      }

      return value;
   }

   public void putObject(ResourceLocation key, V value) {
      Validate.notNull(key);
      Validate.notNull(value);
      this.values = null;
      if (this.registryObjects.containsKey(key)) {
         LOGGER.debug("Adding duplicate key '" + key + "' to registry");
      }

      this.registryObjects.put(key, value);
   }

   public void register(V value) {
      ResourceLocation key = value.getRegistryName();
      this.putObject(key, value);
   }

   public Set<ResourceLocation> getKeys() {
      return Collections.unmodifiableSet(this.registryObjects.keySet());
   }

   @Nullable
   public V getRandomObject(EaglercraftRandom random) {
      this.getValues();
      return (V)(this.values.get(random.nextInt(this.values.size())));
   }

   public List<V> getValues() {
      if (this.values == null) {
         Collection<?> collection = this.registryObjects.values();
         if (collection.isEmpty()) {
            this.values = ImmutableList.of();
         } else {
            this.values = ImmutableList.copyOf(collection);
         }
      }

      return this.values;
   }

   public boolean containsKey(ResourceLocation key) {
      return this.registryObjects.containsKey(key);
   }

   public Iterator<V> iterator() {
      return this.registryObjects.values().iterator();
   }
}
