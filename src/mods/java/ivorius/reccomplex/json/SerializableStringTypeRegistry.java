package ivorius.reccomplex.json;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import java.util.Collection;
import javax.annotation.Nullable;

public class SerializableStringTypeRegistry<T> {
   private StringTypeAdapterFactory<T> adapterFactory;
   private Class<T> typeClass;

   public SerializableStringTypeRegistry(String objectKey, String typeKey, Class<T> typeClass) {
      this.adapterFactory = new StringTypeAdapterFactory<T>(objectKey, typeKey);
      this.typeClass = typeClass;
   }

   public void constructGson(GsonBuilder builder) {
      builder.registerTypeHierarchyAdapter(this.typeClass, this.adapterFactory);
   }

   public StringTypeAdapterFactory<T> adapterFactory() {
      return this.adapterFactory;
   }

   public <T1 extends T> void registerLegacy(String id, Class<T1> clazz, @Nullable JsonDeserializer<T1> deserializer) {
      this.adapterFactory.registerLegacy(id, clazz, deserializer);
   }

   public <TI extends T> void registerType(String id, Class<TI> clazz, JsonSerializer<TI> serializer, JsonDeserializer<TI> deserializer) {
      this.adapterFactory.register(id, clazz, serializer, deserializer);
   }

   public <TI extends T, S extends JsonSerializer<TI> & JsonDeserializer<TI>> void registerType(String id, Class<TI> clazz, S serializer) {
      this.registerType(id, clazz, serializer, (JsonDeserializer)serializer);
   }

   public String typeKey() {
      return this.adapterFactory.getTypeKey();
   }

   public String objectKey() {
      return this.adapterFactory.getObjectKey();
   }

   @Nullable
   public Class<? extends T> typeForID(String id) {
      return this.adapterFactory.objectClass(id);
   }

   @Nullable
   public String iDForType(Class<? extends T> type) {
      return this.adapterFactory.type(type);
   }

   public Collection<String> allIDs() {
      return this.adapterFactory.allIDs();
   }
}
