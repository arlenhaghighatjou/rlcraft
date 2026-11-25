package goblinbob.mobends.core.flux;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObservableMap<K, V> implements IObservable<Map<K, V>> {
   private Map<K, V> map = new HashMap();
   private Set<IObserver<Map<K, V>>> observers = new HashSet();

   public ObservableMap() {
   }

   public void put(K key, V value) {
      this.map.put(key, value);
      this.notifyObservers();
   }

   public V get(K key) {
      ComputedDependencyHelper.linkDependency(this);
      return (V)this.map.get(key);
   }

   public Collection<V> values() {
      ComputedDependencyHelper.linkDependency(this);
      return this.map.values();
   }

   public void clear() {
      this.map.clear();
      this.notifyObservers();
   }

   private void notifyObservers() {
      for(IObserver<Map<K, V>> observer : this.observers) {
         observer.onChanged(this.map);
      }

   }

   public Map<K, V> getValue() {
      ComputedDependencyHelper.linkDependency(this);
      return this.map;
   }

   public Set<IObserver<Map<K, V>>> getObservers() {
      return this.observers;
   }
}
