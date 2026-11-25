package me.lucko.spark.lib.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;

public final class Struct extends GeneratedMessageLite<Struct, Struct.Builder> implements StructOrBuilder {
   public static final int FIELDS_FIELD_NUMBER = 1;
   private MapFieldLite<String, Value> fields_ = MapFieldLite.<String, Value>emptyMapField();
   private static final Struct DEFAULT_INSTANCE;
   private static volatile Parser<Struct> PARSER;

   private Struct() {
   }

   private MapFieldLite<String, Value> internalGetFields() {
      return this.fields_;
   }

   private MapFieldLite<String, Value> internalGetMutableFields() {
      if (!this.fields_.isMutable()) {
         this.fields_ = this.fields_.mutableCopy();
      }

      return this.fields_;
   }

   public int getFieldsCount() {
      return this.internalGetFields().size();
   }

   public boolean containsFields(String key) {
      key.getClass();
      return this.internalGetFields().containsKey(key);
   }

   /** @deprecated */
   @Deprecated
   public Map<String, Value> getFields() {
      return this.getFieldsMap();
   }

   public Map<String, Value> getFieldsMap() {
      return Collections.unmodifiableMap(this.internalGetFields());
   }

   public Value getFieldsOrDefault(String key, Value defaultValue) {
      key.getClass();
      Map<String, Value> map = this.internalGetFields();
      return map.containsKey(key) ? (Value)map.get(key) : defaultValue;
   }

   public Value getFieldsOrThrow(String key) {
      key.getClass();
      Map<String, Value> map = this.internalGetFields();
      if (!map.containsKey(key)) {
         throw new IllegalArgumentException();
      } else {
         return (Value)map.get(key);
      }
   }

   private Map<String, Value> getMutableFieldsMap() {
      return this.internalGetMutableFields();
   }

   public static Struct parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Struct)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Struct parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Struct)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Struct parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Struct)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Struct parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Struct)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Struct parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Struct)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Struct parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Struct)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Struct parseFrom(InputStream input) throws IOException {
      return (Struct)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static Struct parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Struct)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Struct parseDelimitedFrom(InputStream input) throws IOException {
      return (Struct)parseDelimitedFrom(DEFAULT_INSTANCE, input);
   }

   public static Struct parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Struct)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Struct parseFrom(CodedInputStream input) throws IOException {
      return (Struct)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static Struct parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Struct)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Struct.Builder newBuilder() {
      return (Struct.Builder)DEFAULT_INSTANCE.createBuilder();
   }

   public static Struct.Builder newBuilder(Struct prototype) {
      return (Struct.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
   }

   protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
      switch(method) {
      case NEW_MUTABLE_INSTANCE:
         return new Struct();
      case NEW_BUILDER:
         return new Struct.Builder();
      case BUILD_MESSAGE_INFO:
         Object[] objects = new Object[]{"fields_", Struct.FieldsDefaultEntryHolder.defaultEntry};
         String info = "\u0000\u0001\u0000\u0000\u0001\u0001\u0001\u0001\u0000\u0000\u00012";
         return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      case GET_DEFAULT_INSTANCE:
         return DEFAULT_INSTANCE;
      case GET_PARSER:
         Parser<Struct> parser = PARSER;
         if (parser == null) {
            synchronized(Struct.class) {
               parser = PARSER;
               if (parser == null) {
                  parser = new GeneratedMessageLite.DefaultInstanceBasedParser<Struct>(DEFAULT_INSTANCE);
                  PARSER = parser;
               }
            }
         }

         return parser;
      case GET_MEMOIZED_IS_INITIALIZED:
         return 1;
      case SET_MEMOIZED_IS_INITIALIZED:
         return null;
      default:
         throw new UnsupportedOperationException();
      }
   }

   public static Struct getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Struct> parser() {
      return DEFAULT_INSTANCE.getParserForType();
   }

   static {
      Struct defaultInstance = new Struct();
      DEFAULT_INSTANCE = defaultInstance;
      GeneratedMessageLite.registerDefaultInstance(Struct.class, defaultInstance);
   }

   public static final class Builder extends GeneratedMessageLite.Builder<Struct, Struct.Builder> implements StructOrBuilder {
      private Builder() {
         super(Struct.DEFAULT_INSTANCE);
      }

      public int getFieldsCount() {
         return ((Struct)this.instance).getFieldsMap().size();
      }

      public boolean containsFields(String key) {
         key.getClass();
         return ((Struct)this.instance).getFieldsMap().containsKey(key);
      }

      public Struct.Builder clearFields() {
         this.copyOnWrite();
         ((Struct)this.instance).getMutableFieldsMap().clear();
         return this;
      }

      public Struct.Builder removeFields(String key) {
         key.getClass();
         this.copyOnWrite();
         ((Struct)this.instance).getMutableFieldsMap().remove(key);
         return this;
      }

      /** @deprecated */
      @Deprecated
      public Map<String, Value> getFields() {
         return this.getFieldsMap();
      }

      public Map<String, Value> getFieldsMap() {
         return Collections.unmodifiableMap(((Struct)this.instance).getFieldsMap());
      }

      public Value getFieldsOrDefault(String key, Value defaultValue) {
         key.getClass();
         Map<String, Value> map = ((Struct)this.instance).getFieldsMap();
         return map.containsKey(key) ? (Value)map.get(key) : defaultValue;
      }

      public Value getFieldsOrThrow(String key) {
         key.getClass();
         Map<String, Value> map = ((Struct)this.instance).getFieldsMap();
         if (!map.containsKey(key)) {
            throw new IllegalArgumentException();
         } else {
            return (Value)map.get(key);
         }
      }

      public Struct.Builder putFields(String key, Value value) {
         key.getClass();
         value.getClass();
         this.copyOnWrite();
         ((Struct)this.instance).getMutableFieldsMap().put(key, value);
         return this;
      }

      public Struct.Builder putAllFields(Map<String, Value> values) {
         this.copyOnWrite();
         ((Struct)this.instance).getMutableFieldsMap().putAll(values);
         return this;
      }
   }

   private static final class FieldsDefaultEntryHolder {
      static final MapEntryLite<String, Value> defaultEntry = MapEntryLite.<String, Value>newDefaultInstance(WireFormat.FieldType.STRING, "", WireFormat.FieldType.MESSAGE, Value.getDefaultInstance());

      private FieldsDefaultEntryHolder() {
      }
   }
}
