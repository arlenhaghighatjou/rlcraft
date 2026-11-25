package ivorius.reccomplex.json;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

public class StringTypeAdapterFactory<B> implements JsonSerializer<B>, JsonDeserializer<B> {
   private Map<String, JsonDeserializer<? extends B>> deserializerMap;
   private Map<Class<? extends B>, JsonSerializer<? extends B>> serializerMap;
   private BiMap<Class<? extends B>, String> classMap;
   private Map<String, Pair<Class<? extends B>, JsonDeserializer<? extends B>>> legacyMap;
   private Gson gson;
   private String typeKey;
   private String objectKey;

   public StringTypeAdapterFactory(String objectKey, String typeKey) {
      this.deserializerMap = new HashMap();
      this.serializerMap = new HashMap();
      this.classMap = HashBiMap.create();
      this.legacyMap = new HashMap();
      this.gson = new Gson();
      if (objectKey.equals(typeKey)) {
         throw new IllegalArgumentException("Type key must be different from object key");
      } else {
         this.objectKey = objectKey;
         this.typeKey = typeKey;
      }
   }

   public StringTypeAdapterFactory() {
      this("object", "type");
   }

   public String getTypeKey() {
      return this.typeKey;
   }

   public String getObjectKey() {
      return this.objectKey;
   }

   public <T extends B> void registerLegacy(String id, Class<? extends T> clazz, @Nullable JsonDeserializer<T> deserializer) {
      this.legacyMap.put(id, Pair.of(clazz, deserializer));
   }

   public <T extends B> void register(String id, Class<? extends T> clazz, @Nullable JsonSerializer<T> serializer, @Nullable JsonDeserializer<T> deserializer) {
      this.deserializerMap.put(id, deserializer);
      this.serializerMap.put(clazz, serializer);
      this.classMap.put(clazz, id);
   }

   public <K extends B, MultiSerializer extends JsonSerializer<K> & JsonDeserializer<K>> void register(String id, Class<K> clazz, @Nullable MultiSerializer multiSerializer) {
      this.register(id, clazz, multiSerializer, (JsonDeserializer)multiSerializer);
   }

   @Nullable
   public JsonDeserializer<? extends B> deserializer(String key) {
      return (JsonDeserializer)this.deserializerMap.get(key);
   }

   @Nullable
   public <T extends B> JsonSerializer<T> serializer(Class<? extends T> aClass) {
      return (JsonSerializer)this.serializerMap.get(aClass);
   }

   public String type(Class<? extends B> aClass) {
      return (String)this.classMap.get(aClass);
   }

   public Class<? extends B> objectClass(String type) {
      return (Class)this.classMap.inverse().get(type);
   }

   public Collection<String> allIDs() {
      return this.deserializerMap.keySet();
   }

   public B deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if (json.isJsonObject()) {
         JsonObject jsonObject = json.getAsJsonObject();
         if (jsonObject.has(this.typeKey) && jsonObject.has(this.objectKey)) {
            String type = JsonUtils.getString(jsonObject, this.typeKey);
            JsonElement objectElement = jsonObject.get(this.objectKey);
            Pair<Class<? extends B>, JsonDeserializer<? extends B>> legacy = (Pair)this.legacyMap.get(type);
            if (legacy != null) {
               return (B)((JsonDeserializer)legacy.getRight()).deserialize(objectElement, (Type)legacy.getLeft(), context);
            }

            Class<? extends B> typeClass = this.objectClass(type);
            if (typeClass == null) {
               throw new JsonParseException("Unknown type: " + type);
            }

            JsonDeserializer<? extends B> deserializer = this.deserializer(type);
            return (B)(deserializer != null ? deserializer.deserialize(objectElement, typeClass, context) : this.gson.fromJson(objectElement, typeClass));
         }
      }

      return (B)null;
   }

   public JsonElement serialize(B src, Type typeOfSrc, JsonSerializationContext context) {
      Class<? extends B> objectClass = src.getClass();
      String id = this.type(objectClass);
      JsonSerializer serializer = this.serializer(objectClass);
      if (id != null) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty(this.typeKey, id);
         jsonObject.add(this.objectKey, serializer != null ? serializer.serialize(src, typeOfSrc, context) : this.gson.toJsonTree(src, typeOfSrc));
         return jsonObject;
      } else {
         return null;
      }
   }
}
