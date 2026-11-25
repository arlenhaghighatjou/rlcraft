package me.lucko.spark.lib.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class BytesValue extends GeneratedMessageLite<BytesValue, BytesValue.Builder> implements BytesValueOrBuilder {
   public static final int VALUE_FIELD_NUMBER = 1;
   private ByteString value_ = ByteString.EMPTY;
   private static final BytesValue DEFAULT_INSTANCE;
   private static volatile Parser<BytesValue> PARSER;

   private BytesValue() {
   }

   public ByteString getValue() {
      return this.value_;
   }

   private void setValue(ByteString value) {
      value.getClass();
      this.value_ = value;
   }

   private void clearValue() {
      this.value_ = getDefaultInstance().getValue();
   }

   public static BytesValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (BytesValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static BytesValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (BytesValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static BytesValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (BytesValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static BytesValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (BytesValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static BytesValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (BytesValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static BytesValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (BytesValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static BytesValue parseFrom(InputStream input) throws IOException {
      return (BytesValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static BytesValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (BytesValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static BytesValue parseDelimitedFrom(InputStream input) throws IOException {
      return (BytesValue)parseDelimitedFrom(DEFAULT_INSTANCE, input);
   }

   public static BytesValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (BytesValue)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static BytesValue parseFrom(CodedInputStream input) throws IOException {
      return (BytesValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static BytesValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (BytesValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static BytesValue.Builder newBuilder() {
      return (BytesValue.Builder)DEFAULT_INSTANCE.createBuilder();
   }

   public static BytesValue.Builder newBuilder(BytesValue prototype) {
      return (BytesValue.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
   }

   protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
      switch(method) {
      case NEW_MUTABLE_INSTANCE:
         return new BytesValue();
      case NEW_BUILDER:
         return new BytesValue.Builder();
      case BUILD_MESSAGE_INFO:
         Object[] objects = new Object[]{"value_"};
         String info = "\u0000\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0000\u0000\u0001\n";
         return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      case GET_DEFAULT_INSTANCE:
         return DEFAULT_INSTANCE;
      case GET_PARSER:
         Parser<BytesValue> parser = PARSER;
         if (parser == null) {
            synchronized(BytesValue.class) {
               parser = PARSER;
               if (parser == null) {
                  parser = new GeneratedMessageLite.DefaultInstanceBasedParser<BytesValue>(DEFAULT_INSTANCE);
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

   public static BytesValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static BytesValue of(ByteString value) {
      return (BytesValue)newBuilder().setValue(value).build();
   }

   public static Parser<BytesValue> parser() {
      return DEFAULT_INSTANCE.getParserForType();
   }

   static {
      BytesValue defaultInstance = new BytesValue();
      DEFAULT_INSTANCE = defaultInstance;
      GeneratedMessageLite.registerDefaultInstance(BytesValue.class, defaultInstance);
   }

   public static final class Builder extends GeneratedMessageLite.Builder<BytesValue, BytesValue.Builder> implements BytesValueOrBuilder {
      private Builder() {
         super(BytesValue.DEFAULT_INSTANCE);
      }

      public ByteString getValue() {
         return ((BytesValue)this.instance).getValue();
      }

      public BytesValue.Builder setValue(ByteString value) {
         this.copyOnWrite();
         ((BytesValue)this.instance).setValue(value);
         return this;
      }

      public BytesValue.Builder clearValue() {
         this.copyOnWrite();
         ((BytesValue)this.instance).clearValue();
         return this;
      }
   }
}
