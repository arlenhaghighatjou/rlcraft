package xzeroair.trinkets.util.registry;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class TrinketRegistrySimple<K, V> implements ITrinketRegistry<K, V> {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final Map<K, V> registryObjects = this.createUnderlyingMap();
   private Object[] values;

   public TrinketRegistrySimple() {
   }

   protected Map<K, V> createUnderlyingMap() {
      return Maps.newHashMap();
   }

   @Nullable
   public V getObject(@Nullable K name) {
      return (V)this.registryObjects.get(name);
   }

   public void putObject(K key, V value) {
      Validate.notNull(key);
      Validate.notNull(value);
      this.values = null;
      if (this.registryObjects.containsKey(key)) {
         LOGGER.debug("Adding duplicate key '{}' to registry", key);
      }

      this.registryObjects.put(key, value);
   }

   public Set<K> getKeys() {
      return Collections.unmodifiableSet(this.registryObjects.keySet());
   }

   @Nullable
   public V getRandomObject(EaglercraftRandom random) {
      if (this.values == null) {
         Collection<?> collection = this.registryObjects.values();
         if (collection.isEmpty()) {
            return (V)null;
         }

         this.values = collection.toArray(new Object[collection.size()]);
      }

      return (V)this.values[random.nextInt(this.values.length)];
   }

   public boolean containsKey(K key) {
      return this.registryObjects.containsKey(key);
   }

   public Iterator<V> iterator() {
      return this.registryObjects.values().iterator();
   }
}
