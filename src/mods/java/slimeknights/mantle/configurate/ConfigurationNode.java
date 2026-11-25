package slimeknights.mantle.configurate;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import slimeknights.mantle.configurate.objectmapping.ObjectMappingException;
import slimeknights.mantle.configurate.objectmapping.serialize.TypeSerializer;

public interface ConfigurationNode {
   int NUMBER_DEF = 0;

   Object getKey();

   Object[] getPath();

   ConfigurationNode getParent();

   ConfigurationOptions getOptions();

   default Object getValue() {
      return this.getValue((Object)null);
   }

   Object getValue(Object var1);

   Object getValue(Supplier<Object> var1);

   default <T> T getValue(Function<Object, T> transformer) {
      return (T)this.getValue(transformer, (Object)null);
   }

   <T> T getValue(Function<Object, T> var1, T var2);

   <T> T getValue(Function<Object, T> var1, Supplier<T> var2);

   <T> List<T> getList(Function<Object, T> var1);

   <T> List<T> getList(Function<Object, T> var1, List<T> var2);

   <T> List<T> getList(Function<Object, T> var1, Supplier<List<T>> var2);

   default <T> List<T> getList(TypeToken<T> type) throws ObjectMappingException {
      return this.getList(type, ImmutableList.of());
   }

   <T> List<T> getList(TypeToken<T> var1, List<T> var2) throws ObjectMappingException;

   <T> List<T> getList(TypeToken<T> var1, Supplier<List<T>> var2) throws ObjectMappingException;

   default String getString() {
      return this.getString((String)null);
   }

   default String getString(String def) {
      return (String)this.getValue(Types::asString, def);
   }

   default float getFloat() {
      return this.getFloat(0.0F);
   }

   default float getFloat(float def) {
      return ((Float)this.getValue(Types::asFloat, Float.valueOf(def))).floatValue();
   }

   default double getDouble() {
      return this.getDouble(0.0D);
   }

   default double getDouble(double def) {
      return ((Double)this.getValue(Types::asDouble, Double.valueOf(def))).doubleValue();
   }

   default int getInt() {
      return this.getInt(0);
   }

   default int getInt(int def) {
      return ((Integer)this.getValue(Types::asInt, Integer.valueOf(def))).intValue();
   }

   default long getLong() {
      return this.getLong(0L);
   }

   default long getLong(long def) {
      return ((Long)this.getValue(Types::asLong, Long.valueOf(def))).longValue();
   }

   default boolean getBoolean() {
      return this.getBoolean(false);
   }

   default boolean getBoolean(boolean def) {
      return ((Boolean)this.getValue(Types::asBoolean, Boolean.valueOf(def))).booleanValue();
   }

   ConfigurationNode setValue(Object var1);

   default <T> T getValue(TypeToken<T> type) throws ObjectMappingException {
      return (T)this.getValue(type, (Object)null);
   }

   <T> T getValue(TypeToken<T> var1, T var2) throws ObjectMappingException;

   <T> T getValue(TypeToken<T> var1, Supplier<T> var2) throws ObjectMappingException;

   default <T> ConfigurationNode setValue(TypeToken<T> type, T value) throws ObjectMappingException {
      if (value == null) {
         this.setValue((Object)null);
         return this;
      } else {
         TypeSerializer serial = this.getOptions().getSerializers().get(type);
         if (serial != null) {
            serial.serialize(type, value, this);
         } else {
            if (!this.getOptions().acceptsType(value.getClass())) {
               throw new ObjectMappingException("No serializer available for type " + type);
            }

            this.setValue(value);
         }

         return this;
      }
   }

   ConfigurationNode mergeValuesFrom(ConfigurationNode var1);

   boolean hasListChildren();

   boolean hasMapChildren();

   List<? extends ConfigurationNode> getChildrenList();

   Map<Object, ? extends ConfigurationNode> getChildrenMap();

   boolean removeChild(Object var1);

   ConfigurationNode getAppendedNode();

   ConfigurationNode getNode(Object... var1);

   boolean isVirtual();
}
