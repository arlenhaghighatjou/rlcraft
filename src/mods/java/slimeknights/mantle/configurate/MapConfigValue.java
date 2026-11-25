package slimeknights.mantle.configurate;

import com.google.common.base.Objects;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

class MapConfigValue extends ConfigValue {
   volatile ConcurrentMap<Object, SimpleConfigurationNode> values = this.newMap();

   public MapConfigValue(SimpleConfigurationNode holder) {
      super(holder);
   }

   public Object getValue() {
      Map<Object, Object> value = new LinkedHashMap();

      for(Entry<Object, ? extends SimpleConfigurationNode> ent : this.values.entrySet()) {
         value.put(ent.getKey(), ((SimpleConfigurationNode)ent.getValue()).getValue());
      }

      return value;
   }

   public void setValue(Object value) {
      if (value instanceof Map) {
         ConcurrentMap<Object, SimpleConfigurationNode> newValue = this.newMap();

         for(Entry<?, ?> ent : ((Map)value).entrySet()) {
            if (ent.getValue() != null) {
               SimpleConfigurationNode child = this.holder.createNode(ent.getKey());
               newValue.put(ent.getKey(), child);
               child.attached = true;
               child.setValue(ent.getValue());
            }
         }

         synchronized(this) {
            ConcurrentMap<Object, SimpleConfigurationNode> oldMap = this.values;
            this.values = newValue;
            this.detachChildren(oldMap);
         }
      } else {
         throw new IllegalArgumentException("Map configuration values can only be set to values of type Map");
      }
   }

   SimpleConfigurationNode putChild(Object key, SimpleConfigurationNode value) {
      return value == null ? (SimpleConfigurationNode)this.values.remove(key) : (SimpleConfigurationNode)this.values.put(key, value);
   }

   SimpleConfigurationNode putChildIfAbsent(Object key, SimpleConfigurationNode value) {
      return value == null ? (SimpleConfigurationNode)this.values.remove(key) : (SimpleConfigurationNode)this.values.putIfAbsent(key, value);
   }

   public SimpleConfigurationNode getChild(Object key) {
      return (SimpleConfigurationNode)this.values.get(key);
   }

   public Iterable<SimpleConfigurationNode> iterateChildren() {
      return this.values.values();
   }

   private void detachChildren(Map<Object, SimpleConfigurationNode> map) {
      for(SimpleConfigurationNode value : map.values()) {
         value.attached = false;
         value.clear();
      }

   }

   public void clear() {
      synchronized(this) {
         ConcurrentMap<Object, SimpleConfigurationNode> oldMap = this.values;
         this.values = this.newMap();
         this.detachChildren(oldMap);
      }
   }

   private ConcurrentMap<Object, SimpleConfigurationNode> newMap() {
      return this.holder.getOptions().getMapFactory().<Object, SimpleConfigurationNode>create();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MapConfigValue that = (MapConfigValue)o;
         return Objects.equal(this.values, that.values);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hashCode(new Object[]{this.values});
   }
}
