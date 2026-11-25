package xzeroair.trinkets.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nullable;
import net.minecraft.util.IntIdentityHashBiMap;

public class TrinketRegistry<K, V> extends TrinketRegistrySimple<K, V> {
   protected final IntIdentityHashBiMap<V> underlyingIntegerMap = new IntIdentityHashBiMap(256);
   protected final Map<V, K> inverseObjectRegistry;
   protected final Map<EaglercraftUUID, V> uuidObjectRegistry = new HashMap();

   public TrinketRegistry() {
      this.inverseObjectRegistry = ((BiMap)this.registryObjects).inverse();
   }

   public void register(int id, K key, EaglercraftUUID uuid, V value) {
      this.underlyingIntegerMap.put(value, id);
      this.uuidObjectRegistry.put(uuid, value);
      this.putObject(key, value);
   }

   protected Map<K, V> createUnderlyingMap() {
      return HashBiMap.create();
   }

   @Nullable
   public V getObject(@Nullable K name) {
      return (V)super.getObject(name);
   }

   @Nullable
   public K getNameForObject(V value) {
      return (K)this.inverseObjectRegistry.get(value);
   }

   public boolean containsKey(K key) {
      return super.containsKey(key);
   }

   public int getIDForObject(@Nullable V value) {
      return this.underlyingIntegerMap.getId(value);
   }

   @Nullable
   public V getObjectById(int id) {
      return (V)this.underlyingIntegerMap.get(id);
   }

   @Nullable
   public V getObjectByUUID(EaglercraftUUID uuid) {
      return (V)this.uuidObjectRegistry.get(uuid);
   }

   public Iterator<V> iterator() {
      return this.underlyingIntegerMap.iterator();
   }
}
