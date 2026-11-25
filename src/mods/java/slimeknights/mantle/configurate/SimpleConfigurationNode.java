package slimeknights.mantle.configurate;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import slimeknights.mantle.configurate.objectmapping.ObjectMappingException;
import slimeknights.mantle.configurate.objectmapping.serialize.TypeSerializer;

public class SimpleConfigurationNode implements ConfigurationNode {
   private final ConfigurationOptions options;
   volatile boolean attached;
   volatile Object key;
   private SimpleConfigurationNode parent;
   private volatile ConfigValue value;

   public static SimpleConfigurationNode root() {
      return root(ConfigurationOptions.defaults());
   }

   public static SimpleConfigurationNode root(ConfigurationOptions options) {
      return new SimpleConfigurationNode((Object)null, (SimpleConfigurationNode)null, options);
   }

   protected SimpleConfigurationNode(Object key, SimpleConfigurationNode parent, ConfigurationOptions options) {
      Preconditions.checkNotNull(options, "options");
      this.key = key;
      if (parent == null) {
         this.attached = true;
      }

      this.options = options;
      this.parent = parent == null ? null : parent;
      this.value = new NullConfigValue(this);
   }

   public Object getValue(Object def) {
      Object ret = this.value.getValue();
      return ret == null ? this.calculateDef(def) : ret;
   }

   public Object getValue(Supplier<Object> defSupplier) {
      Object ret = this.value.getValue();
      return ret == null ? this.calculateDef(defSupplier.get()) : ret;
   }

   public <T> T getValue(Function<Object, T> transformer, T def) {
      T ret = (T)transformer.apply(this.getValue());
      return (T)(ret == null ? this.calculateDef(def) : ret);
   }

   public <T> T getValue(Function<Object, T> transformer, Supplier<T> defSupplier) {
      T ret = (T)transformer.apply(this.getValue());
      return (T)(ret == null ? this.calculateDef(defSupplier.get()) : ret);
   }

   private <T> T calculateDef(T defValue) {
      if (defValue != null && this.getOptions().shouldCopyDefaults()) {
         this.setValue(defValue);
      }

      return defValue;
   }

   public <T> List<T> getList(Function<Object, T> transformer) {
      Builder<T> build = ImmutableList.builder();
      ConfigValue value = this.value;
      if (value instanceof ListConfigValue) {
         for(SimpleConfigurationNode o : value.iterateChildren()) {
            T transformed = (T)transformer.apply(o.getValue());
            if (transformed != null) {
               build.add(transformed);
            }
         }
      } else {
         T transformed = (T)transformer.apply(value.getValue());
         if (transformed != null) {
            build.add(transformed);
         }
      }

      return build.build();
   }

   public <T> List<T> getList(Function<Object, T> transformer, List<T> def) {
      List<T> ret = this.<T>getList(transformer);
      return ret.isEmpty() ? (List)this.calculateDef(def) : ret;
   }

   public <T> List<T> getList(Function<Object, T> transformer, Supplier<List<T>> defSupplier) {
      List<T> ret = this.<T>getList(transformer);
      return ret.isEmpty() ? (List)this.calculateDef(defSupplier.get()) : ret;
   }

   public <T> List<T> getList(TypeToken<T> type, List<T> def) throws ObjectMappingException {
      List<T> ret = (List)this.getValue((new TypeToken<List<T>>() {
      }).where(new TypeParameter<T>() {
      }, type), def);
      return ret.isEmpty() ? (List)this.calculateDef(def) : ret;
   }

   public <T> List<T> getList(TypeToken<T> type, Supplier<List<T>> defSupplier) throws ObjectMappingException {
      List<T> ret = (List)this.getValue((new TypeToken<List<T>>() {
      }).where(new TypeParameter<T>() {
      }, type), defSupplier);
      return ret.isEmpty() ? (List)this.calculateDef(defSupplier.get()) : ret;
   }

   public SimpleConfigurationNode setValue(Object newValue) {
      if (newValue instanceof ConfigurationNode) {
         ConfigurationNode newNode = (ConfigurationNode)newValue;
         if (newNode.hasListChildren()) {
            this.attachIfNecessary();
            ListConfigValue newList = new ListConfigValue(this);
            synchronized(newNode) {
               List<? extends ConfigurationNode> children = newNode.getChildrenList();

               for(int i = 0; i < children.size(); ++i) {
                  SimpleConfigurationNode child = this.createNode(Integer.valueOf(i));
                  child.attached = true;
                  newList.putChild(Integer.valueOf(i), child);
                  child.setValue(children.get(i));
               }
            }

            this.value = newList;
            return this;
         }

         if (newNode.hasMapChildren()) {
            this.attachIfNecessary();
            MapConfigValue newMap = new MapConfigValue(this);
            synchronized(newNode) {
               Map<Object, ? extends ConfigurationNode> children = newNode.getChildrenMap();

               for(Entry<Object, ? extends ConfigurationNode> ent : children.entrySet()) {
                  SimpleConfigurationNode child = this.createNode(ent.getKey());
                  child.attached = true;
                  newMap.putChild(ent.getKey(), child);
                  child.setValue(ent.getValue());
               }
            }

            this.value = newMap;
            return this;
         }

         newValue = newNode.getValue();
      }

      if (newValue == null) {
         if (this.parent == null) {
            this.clear();
         } else {
            this.parent.removeChild(this.key);
         }

         return this;
      } else {
         this.insertNewValue(newValue, false);
         return this;
      }
   }

   public <T> T getValue(TypeToken<T> type, T def) throws ObjectMappingException {
      Object value = this.getValue();
      if (value == null) {
         if (def != null && this.getOptions().shouldCopyDefaults()) {
            this.setValue(type, def);
         }

         return def;
      } else {
         TypeSerializer serial = this.getOptions().getSerializers().get(type);
         if (serial == null) {
            if (type.getRawType().isInstance(value)) {
               return (T)type.getRawType().cast(value);
            } else {
               if (def != null && this.getOptions().shouldCopyDefaults()) {
                  this.setValue(type, def);
               }

               return def;
            }
         } else {
            return (T)serial.deserialize(type, this);
         }
      }
   }

   public <T> T getValue(TypeToken<T> type, Supplier<T> defSupplier) throws ObjectMappingException {
      Object value = this.getValue();
      if (value == null) {
         T def = (T)defSupplier.get();
         if (def != null && this.getOptions().shouldCopyDefaults()) {
            this.setValue(type, def);
         }

         return def;
      } else {
         TypeSerializer serial = this.getOptions().getSerializers().get(type);
         if (serial == null) {
            if (type.getRawType().isInstance(value)) {
               return (T)type.getRawType().cast(value);
            } else {
               T def = (T)defSupplier.get();
               if (def != null && this.getOptions().shouldCopyDefaults()) {
                  this.setValue(type, def);
               }

               return def;
            }
         } else {
            return (T)serial.deserialize(type, this);
         }
      }
   }

   private void insertNewValue(Object newValue, boolean onlyIfNull) {
      this.attachIfNecessary();
      synchronized(this) {
         ConfigValue value = this.value;
         ConfigValue oldValue = this.value;
         if (!onlyIfNull || oldValue instanceof NullConfigValue) {
            if (newValue instanceof Collection) {
               if (!(value instanceof ListConfigValue)) {
                  value = new ListConfigValue(this);
               }
            } else if (newValue instanceof Map) {
               if (!(value instanceof MapConfigValue)) {
                  value = new MapConfigValue(this);
               }
            } else if (!(value instanceof ScalarConfigValue)) {
               value = new ScalarConfigValue(this);
            }

            value.setValue(newValue);
            this.value = value;
         }
      }
   }

   public ConfigurationNode mergeValuesFrom(ConfigurationNode other) {
      if (other.hasMapChildren()) {
         synchronized(this) {
            ConfigValue newValue = this.value;
            ConfigValue oldValue = this.value;
            if (!(oldValue instanceof MapConfigValue)) {
               if (!(oldValue instanceof NullConfigValue)) {
                  return this;
               }

               newValue = new MapConfigValue(this);
            }

            for(Entry<Object, ? extends ConfigurationNode> ent : other.getChildrenMap().entrySet()) {
               SimpleConfigurationNode newChild = this.createNode(ent.getKey());
               newChild.attached = true;
               newChild.setValue(ent.getValue());
               SimpleConfigurationNode existing = newValue.putChildIfAbsent(ent.getKey(), newChild);
               if (existing != null) {
                  existing.mergeValuesFrom(newChild);
               }
            }

            this.value = newValue;
         }
      } else if (other.getValue() != null) {
         this.insertNewValue(other.getValue(), true);
      }

      return this;
   }

   public SimpleConfigurationNode getNode(Object... path) {
      SimpleConfigurationNode ret = this;

      for(Object el : path) {
         ret = ret.getChild(el, false);
      }

      return ret;
   }

   public boolean isVirtual() {
      return !this.attached;
   }

   public boolean hasListChildren() {
      return this.value instanceof ListConfigValue;
   }

   public boolean hasMapChildren() {
      return this.value instanceof MapConfigValue;
   }

   public List<? extends SimpleConfigurationNode> getChildrenList() {
      ConfigValue value = this.value;
      return (List<? extends SimpleConfigurationNode>)(value instanceof ListConfigValue ? ImmutableList.copyOf((Collection)((ListConfigValue)value).values.get()) : Collections.emptyList());
   }

   public Map<Object, ? extends SimpleConfigurationNode> getChildrenMap() {
      ConfigValue value = this.value;
      return (Map<Object, ? extends SimpleConfigurationNode>)(value instanceof MapConfigValue ? ImmutableMap.copyOf(((MapConfigValue)value).values) : Collections.emptyMap());
   }

   protected SimpleConfigurationNode getChild(Object key, boolean attach) {
      SimpleConfigurationNode child = this.value.getChild(key);
      if (child == null) {
         if (attach) {
            this.attachIfNecessary();
            SimpleConfigurationNode existingChild = this.value.putChildIfAbsent(key, child = this.createNode(key));
            if (existingChild != null) {
               child = existingChild;
            } else {
               this.attachChild(child);
            }
         } else {
            child = this.createNode(key);
         }
      }

      return child;
   }

   public boolean removeChild(Object key) {
      return this.possiblyDetach(this.value.putChild(key, (SimpleConfigurationNode)null)) != null;
   }

   private SimpleConfigurationNode possiblyDetach(SimpleConfigurationNode node) {
      if (node != null) {
         node.attached = false;
         node.clear();
      }

      return node;
   }

   public SimpleConfigurationNode getAppendedNode() {
      return this.getChild(Integer.valueOf(-1), false);
   }

   public Object getKey() {
      return this.key;
   }

   public Object[] getPath() {
      LinkedList<Object> pathElements = new LinkedList();
      ConfigurationNode ptr = this;
      if (this.getParent() == null) {
         return new Object[]{this.getKey()};
      } else {
         while(true) {
            pathElements.addFirst(ptr.getKey());
            if ((ptr = ptr.getParent()).getParent() == null) {
               break;
            }
         }

         return pathElements.toArray();
      }
   }

   public SimpleConfigurationNode getParent() {
      return this.parent;
   }

   public ConfigurationOptions getOptions() {
      return this.options;
   }

   SimpleConfigurationNode getParentAttached() {
      SimpleConfigurationNode parent = this.parent;
      if (parent.isVirtual()) {
         parent = parent.getParentAttached().attachChildIfAbsent(parent);
      }

      return this.parent = parent;
   }

   private SimpleConfigurationNode attachChildIfAbsent(SimpleConfigurationNode child) {
      if (this.isVirtual()) {
         throw new IllegalStateException("This parent is not currently attached. This is an internal state violation.");
      } else if (!child.getParentAttached().equals(this)) {
         throw new IllegalStateException("Child " + child + " path is not a direct parent of me (" + this + "), cannot attach");
      } else {
         ConfigValue oldValue;
         ConfigValue newValue;
         synchronized(this) {
            oldValue = this.value;
            newValue = this.value;
            if (!(oldValue instanceof MapConfigValue)) {
               if (child.key instanceof Integer) {
                  if (oldValue instanceof NullConfigValue) {
                     newValue = new ListConfigValue(this);
                  } else if (!(oldValue instanceof ListConfigValue)) {
                     newValue = new ListConfigValue(this, oldValue.getValue());
                  }
               } else {
                  newValue = new MapConfigValue(this);
               }
            }

            SimpleConfigurationNode oldChild = newValue.putChildIfAbsent(child.key, child);
            if (oldChild != null) {
               return oldChild;
            }

            this.value = newValue;
         }

         if (newValue != oldValue) {
            oldValue.clear();
         }

         child.attached = true;
         return child;
      }
   }

   protected SimpleConfigurationNode createNode(Object path) {
      return new SimpleConfigurationNode(path, this, this.options);
   }

   protected void attachIfNecessary() {
      if (!this.attached) {
         this.getParentAttached().attachChild(this);
      }

   }

   protected void attachChild(SimpleConfigurationNode child) {
      if (this.isVirtual()) {
         throw new IllegalStateException("This parent is not currently attached. This is an internal state violation.");
      } else if (!child.getParentAttached().equals(this)) {
         throw new IllegalStateException("Child " + child + " path is not a direct parent of me (" + this + "), cannot attach");
      } else {
         ConfigValue oldValue;
         ConfigValue newValue;
         synchronized(this) {
            oldValue = this.value;
            newValue = this.value;
            if (!(oldValue instanceof MapConfigValue)) {
               if (child.key instanceof Integer) {
                  if (oldValue instanceof NullConfigValue) {
                     newValue = new ListConfigValue(this);
                  } else if (!(oldValue instanceof ListConfigValue)) {
                     newValue = new ListConfigValue(this, oldValue.getValue());
                  }
               } else {
                  newValue = new MapConfigValue(this);
               }
            }

            this.possiblyDetach(newValue.putChild(child.key, child));
            this.value = newValue;
         }

         if (newValue != oldValue) {
            oldValue.clear();
         }

         child.attached = true;
      }
   }

   protected void clear() {
      synchronized(this) {
         ConfigValue oldValue = this.value;
         this.value = new NullConfigValue(this);
         oldValue.clear();
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof SimpleConfigurationNode)) {
         return false;
      } else {
         SimpleConfigurationNode that = (SimpleConfigurationNode)o;
         if (this.attached != that.attached) {
            return false;
         } else {
            if (this.key != null) {
               if (!this.key.equals(that.key)) {
                  return false;
               }
            } else if (that.key != null) {
               return false;
            }

            if (!this.options.equals(that.options)) {
               return false;
            } else {
               if (this.parent != null) {
                  if (!this.parent.equals(that.parent)) {
                     return false;
                  }
               } else if (that.parent != null) {
                  return false;
               }

               if (!this.value.equals(that.value)) {
                  return false;
               } else {
                  return true;
               }
            }
         }
      }
   }

   public int hashCode() {
      int result = this.options.hashCode();
      result = 31 * result + (this.attached ? 1 : 0);
      result = 31 * result + (this.key != null ? this.key.hashCode() : 0);
      result = 31 * result + (this.parent != null ? this.parent.hashCode() : 0);
      result = 31 * result + this.value.hashCode();
      return result;
   }

   public String toString() {
      return "SimpleConfigurationNode{options=" + this.options + ", attached=" + this.attached + ", key=" + this.key + ", parent=" + this.parent + ", value=" + this.value + '}';
   }
}
