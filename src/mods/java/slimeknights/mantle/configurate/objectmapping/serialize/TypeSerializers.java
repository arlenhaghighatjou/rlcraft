package slimeknights.mantle.configurate.objectmapping.serialize;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import slimeknights.mantle.configurate.ConfigurationNode;
import slimeknights.mantle.configurate.SimpleConfigurationNode;
import slimeknights.mantle.configurate.Types;
import slimeknights.mantle.configurate.objectmapping.InvalidTypeException;
import slimeknights.mantle.configurate.objectmapping.ObjectMappingException;
import slimeknights.mantle.configurate.util.EnumLookup;

public class TypeSerializers {
   private static final TypeSerializerCollection DEFAULT_SERIALIZERS = new TypeSerializerCollection((TypeSerializerCollection)null);

   public TypeSerializers() {
   }

   public static TypeSerializerCollection getDefaultSerializers() {
      return DEFAULT_SERIALIZERS;
   }

   static {
      DEFAULT_SERIALIZERS.registerType(TypeToken.of(URI.class), new TypeSerializers.URISerializer());
      DEFAULT_SERIALIZERS.registerType(TypeToken.of(URL.class), new TypeSerializers.URLSerializer());
      DEFAULT_SERIALIZERS.registerType(TypeToken.of(UUID.class), new TypeSerializers.UUIDSerializer());
      DEFAULT_SERIALIZERS.registerPredicate((input) -> {
         return input.getRawType().isAnnotationPresent(ConfigSerializable.class);
      }, new TypeSerializers.AnnotatedObjectSerializer());
      DEFAULT_SERIALIZERS.registerType(TypeToken.of(Number.class), new TypeSerializers.NumberSerializer());
      DEFAULT_SERIALIZERS.registerType(TypeToken.of(String.class), new TypeSerializers.StringSerializer());
      DEFAULT_SERIALIZERS.registerType(TypeToken.of(Boolean.class), new TypeSerializers.BooleanSerializer());
      DEFAULT_SERIALIZERS.registerType(new TypeToken<Map<?, ?>>() {
      }, new TypeSerializers.MapSerializer());
      DEFAULT_SERIALIZERS.registerType(new TypeToken<List<?>>() {
      }, new TypeSerializers.ListSerializer());
      DEFAULT_SERIALIZERS.registerType(new TypeToken<Enum<?>>() {
      }, new TypeSerializers.EnumValueSerializer());
      DEFAULT_SERIALIZERS.registerType(TypeToken.of(Pattern.class), new TypeSerializers.PatternSerializer());
   }

   private static class AnnotatedObjectSerializer implements TypeSerializer<Object> {
      private AnnotatedObjectSerializer() {
      }

      public Object deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
         Class<?> clazz = this.getInstantiableType(type, value.getNode("__class__").getString());
         return value.getOptions().getObjectMapperFactory().getMapper(clazz).bindToNew().populate(value);
      }

      private Class<?> getInstantiableType(TypeToken<?> type, String configuredName) throws ObjectMappingException {
         Class<?> retClass;
         if (!type.getRawType().isInterface() && !Modifier.isAbstract(type.getRawType().getModifiers())) {
            retClass = type.getRawType();
         } else {
            if (configuredName == null) {
               throw new ObjectMappingException("No available configured type for instances of " + type);
            }

            try {
               retClass = Class.forName(configuredName);
            } catch (ClassNotFoundException var5) {
               throw new ObjectMappingException("Unknown class of object " + configuredName, var5);
            }
         }

         return retClass;
      }

      public void serialize(TypeToken<?> type, Object obj, ConfigurationNode value) throws ObjectMappingException {
         if (type.getRawType().isInterface() || Modifier.isAbstract(type.getRawType().getModifiers())) {
            value.getNode("__class__").setValue(type.getRawType().getCanonicalName());
         }

         value.getOptions().getObjectMapperFactory().getMapper(obj.getClass()).bind(obj).serialize(value);
      }
   }

   private static class BooleanSerializer implements TypeSerializer<Boolean> {
      private BooleanSerializer() {
      }

      public Boolean deserialize(TypeToken<?> type, ConfigurationNode value) throws InvalidTypeException {
         return value.getBoolean();
      }

      public void serialize(TypeToken<?> type, Boolean obj, ConfigurationNode value) {
         value.setValue(Types.asBoolean(obj));
      }
   }

   private static class EnumValueSerializer implements TypeSerializer<Enum> {
      private EnumValueSerializer() {
      }

      public Enum deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
         String enumConstant = value.getString();
         if (enumConstant == null) {
            throw new ObjectMappingException("No value present in node " + value);
         } else {
            Optional<Enum> ret = EnumLookup.<Enum>lookupEnum(type.getRawType().asSubclass(Enum.class), enumConstant);
            if (!ret.isPresent()) {
               throw new ObjectMappingException("Invalid enum constant provided for " + value.getKey() + ": Expected a value of enum " + type + ", got " + enumConstant);
            } else {
               return (Enum)ret.get();
            }
         }
      }

      public void serialize(TypeToken<?> type, Enum obj, ConfigurationNode value) throws ObjectMappingException {
         value.setValue(obj.name());
      }
   }

   private static class ListSerializer implements TypeSerializer<List<?>> {
      private ListSerializer() {
      }

      public List<?> deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
         if (!(type.getType() instanceof ParameterizedType)) {
            throw new ObjectMappingException("Raw types are not supported for collections");
         } else {
            TypeToken<?> entryType = type.resolveType(List.class.getTypeParameters()[0]);
            TypeSerializer entrySerial = value.getOptions().getSerializers().get(entryType);
            if (entrySerial == null) {
               throw new ObjectMappingException("No applicable type serializer for type " + entryType);
            } else if (!value.hasListChildren()) {
               Object unwrappedVal = value.getValue();
               return unwrappedVal != null ? Lists.newArrayList(new Object[]{entrySerial.deserialize(entryType, value)}) : new ArrayList();
            } else {
               List<? extends ConfigurationNode> values = value.getChildrenList();
               List<Object> ret = new ArrayList(values.size());

               for(ConfigurationNode ent : values) {
                  ret.add(entrySerial.deserialize(entryType, ent));
               }

               return ret;
            }
         }
      }

      public void serialize(TypeToken<?> type, List<?> obj, ConfigurationNode value) throws ObjectMappingException {
         if (!(type.getType() instanceof ParameterizedType)) {
            throw new ObjectMappingException("Raw types are not supported for collections");
         } else {
            TypeToken<?> entryType = type.resolveType(List.class.getTypeParameters()[0]);
            TypeSerializer entrySerial = value.getOptions().getSerializers().get(entryType);
            if (entrySerial == null) {
               throw new ObjectMappingException("No applicable type serializer for type " + entryType);
            } else {
               value.setValue(ImmutableList.of());

               for(Object ent : obj) {
                  entrySerial.serialize(entryType, ent, value.getAppendedNode());
               }

            }
         }
      }
   }

   private static class MapSerializer implements TypeSerializer<Map<?, ?>> {
      private MapSerializer() {
      }

      public Map<?, ?> deserialize(TypeToken<?> type, ConfigurationNode node) throws ObjectMappingException {
         Map<Object, Object> ret = new LinkedHashMap();
         if (node.hasMapChildren()) {
            if (!(type.getType() instanceof ParameterizedType)) {
               throw new ObjectMappingException("Raw types are not supported for collections");
            }

            TypeToken<?> key = type.resolveType(Map.class.getTypeParameters()[0]);
            TypeToken<?> value = type.resolveType(Map.class.getTypeParameters()[1]);
            TypeSerializer keySerial = node.getOptions().getSerializers().get(key);
            TypeSerializer valueSerial = node.getOptions().getSerializers().get(value);
            if (keySerial == null) {
               throw new ObjectMappingException("No type serializer available for type " + key);
            }

            if (valueSerial == null) {
               throw new ObjectMappingException("No type serializer available for type " + value);
            }

            for(Entry<Object, ? extends ConfigurationNode> ent : node.getChildrenMap().entrySet()) {
               Object keyValue = keySerial.deserialize(key, SimpleConfigurationNode.root().setValue(ent.getKey()));
               Object valueValue = valueSerial.deserialize(value, (ConfigurationNode)ent.getValue());
               if (keyValue != null && valueValue != null) {
                  ret.put(keyValue, valueValue);
               }
            }
         }

         return ret;
      }

      public void serialize(TypeToken<?> type, Map<?, ?> obj, ConfigurationNode node) throws ObjectMappingException {
         if (!(type.getType() instanceof ParameterizedType)) {
            throw new ObjectMappingException("Raw types are not supported for collections");
         } else {
            TypeToken<?> key = type.resolveType(Map.class.getTypeParameters()[0]);
            TypeToken<?> value = type.resolveType(Map.class.getTypeParameters()[1]);
            TypeSerializer keySerial = node.getOptions().getSerializers().get(key);
            TypeSerializer valueSerial = node.getOptions().getSerializers().get(value);
            if (keySerial == null) {
               throw new ObjectMappingException("No type serializer available for type " + key);
            } else if (valueSerial == null) {
               throw new ObjectMappingException("No type serializer available for type " + value);
            } else {
               node.setValue(ImmutableMap.of());

               for(Entry<?, ?> ent : obj.entrySet()) {
                  SimpleConfigurationNode keyNode = SimpleConfigurationNode.root();
                  keySerial.serialize(key, ent.getKey(), keyNode);
                  valueSerial.serialize(value, ent.getValue(), node.getNode(keyNode.getValue()));
               }

            }
         }
      }
   }

   private static class NumberSerializer implements TypeSerializer<Number> {
      private NumberSerializer() {
      }

      public Number deserialize(TypeToken<?> type, ConfigurationNode value) throws InvalidTypeException {
         type = type.wrap();
         Class<?> clazz = type.getRawType();
         if (Integer.class.equals(clazz)) {
            return value.getInt();
         } else if (Long.class.equals(clazz)) {
            return value.getLong();
         } else if (Short.class.equals(clazz)) {
            return (short)value.getInt();
         } else if (Byte.class.equals(clazz)) {
            return (byte)value.getInt();
         } else if (Float.class.equals(clazz)) {
            return value.getFloat();
         } else {
            return Double.class.equals(clazz) ? value.getDouble() : null;
         }
      }

      public void serialize(TypeToken<?> type, Number obj, ConfigurationNode value) {
         value.setValue(obj);
      }
   }

   private static class PatternSerializer implements TypeSerializer<Pattern> {
      private PatternSerializer() {
      }

      public Pattern deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
         try {
            return Pattern.compile(value.getString());
         } catch (PatternSyntaxException var4) {
            throw new ObjectMappingException(var4);
         }
      }

      public void serialize(TypeToken<?> type, Pattern obj, ConfigurationNode value) throws ObjectMappingException {
         value.setValue(obj.pattern());
      }
   }

   private static class StringSerializer implements TypeSerializer<String> {
      private StringSerializer() {
      }

      public String deserialize(TypeToken<?> type, ConfigurationNode value) throws InvalidTypeException {
         return value.getString();
      }

      public void serialize(TypeToken<?> type, String obj, ConfigurationNode value) {
         value.setValue(obj);
      }
   }

   private static class URISerializer implements TypeSerializer<URI> {
      private URISerializer() {
      }

      public URI deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
         String plainUri = value.getString();
         if (plainUri == null) {
            throw new ObjectMappingException("No value present in node " + value);
         } else {
            try {
               URI uri = new URI(plainUri);
               return uri;
            } catch (URISyntaxException var6) {
               throw new ObjectMappingException("Invalid URI string provided for " + value.getKey() + ": got " + plainUri);
            }
         }
      }

      public void serialize(TypeToken<?> type, URI obj, ConfigurationNode value) throws ObjectMappingException {
         value.setValue(obj.toString());
      }
   }

   private static class URLSerializer implements TypeSerializer<URL> {
      private URLSerializer() {
      }

      public URL deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
         String plainUrl = value.getString();
         if (plainUrl == null) {
            throw new ObjectMappingException("No value present in node " + value);
         } else {
            try {
               URL url = new URL(plainUrl);
               return url;
            } catch (MalformedURLException var6) {
               throw new ObjectMappingException("Invalid URL string provided for " + value.getKey() + ": got " + plainUrl);
            }
         }
      }

      public void serialize(TypeToken<?> type, URL obj, ConfigurationNode value) throws ObjectMappingException {
         value.setValue(obj.toString());
      }
   }

   private static class UUIDSerializer implements TypeSerializer<UUID> {
      private UUIDSerializer() {
      }

      public UUID deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
         try {
            return UUID.fromString(value.getString());
         } catch (IllegalArgumentException var4) {
            throw new ObjectMappingException("Value not a UUID", var4);
         }
      }

      public void serialize(TypeToken<?> type, UUID obj, ConfigurationNode value) throws ObjectMappingException {
         value.setValue(obj.toString());
      }
   }
}
