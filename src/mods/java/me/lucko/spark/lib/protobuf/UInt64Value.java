package me.lucko.spark.lib.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class UInt64Value extends GeneratedMessageLite<UInt64Value, UInt64Value.Builder> implements UInt64ValueOrBuilder {
   public static final int VALUE_FIELD_NUMBER = 1;
   private long value_;
   private static final UInt64Value DEFAULT_INSTANCE;
   private static volatile Parser<UInt64Value> PARSER;

   private UInt64Value() {
   }

   public long getValue() {
      return this.value_;
   }

   private void setValue(long value) {
      this.value_ = value;
   }

   private void clearValue() {
      this.value_ = 0L;
   }

   public static UInt64Value parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (UInt64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static UInt64Value parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (UInt64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static UInt64Value parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (UInt64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static UInt64Value parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (UInt64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static UInt64Value parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (UInt64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static UInt64Value parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (UInt64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static UInt64Value parseFrom(InputStream input) throws IOException {
      return (UInt64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static UInt64Value parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (UInt64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static UInt64Value parseDelimitedFrom(InputStream input) throws IOException {
      return (UInt64Value)parseDelimitedFrom(DEFAULT_INSTANCE, input);
   }

   public static UInt64Value parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (UInt64Value)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static UInt64Value parseFrom(CodedInputStream input) throws IOException {
      return (UInt64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static UInt64Value parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (UInt64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static UInt64Value.Builder newBuilder() {
      return (UInt64Value.Builder)DEFAULT_INSTANCE.createBuilder();
   }

   public static UInt64Value.Builder newBuilder(UInt64Value prototype) {
      return (UInt64Value.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
   }

   protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
      switch(method) {
      case NEW_MUTABLE_INSTANCE:
         return new UInt64Value();
      case NEW_BUILDER:
         return new UInt64Value.Builder();
      case BUILD_MESSAGE_INFO:
         Object[] objects = new Object[]{"value_"};
         String info = "\u0000\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0000\u0000\u0001\u0003";
         return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      case GET_DEFAULT_INSTANCE:
         return DEFAULT_INSTANCE;
      case GET_PARSER:
         Parser<UInt64Value> parser = PARSER;
         if (parser == null) {
            synchronized(UInt64Value.class) {
               parser = PARSER;
               if (parser == null) {
                  parser = new GeneratedMessageLite.DefaultInstanceBasedParser<UInt64Value>(DEFAULT_INSTANCE);
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

   public static UInt64Value getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static UInt64Value of(long value) {
      return (UInt64Value)newBuilder().setValue(value).build();
   }

   public static Parser<UInt64Value> parser() {
      return DEFAULT_INSTANCE.getParserForType();
   }

   static {
      UInt64Value defaultInstance = new UInt64Value();
      DEFAULT_INSTANCE = defaultInstance;
      GeneratedMessageLite.registerDefaultInstance(UInt64Value.class, defaultInstance);
   }

   public static final class Builder extends GeneratedMessageLite.Builder<UInt64Value, UInt64Value.Builder> implements UInt64ValueOrBuilder {
      private Builder() {
         super(UInt64Value.DEFAULT_INSTANCE);
      }

      public long getValue() {
         return ((UInt64Value)this.instance).getValue();
      }

      public UInt64Value.Builder setValue(long value) {
         this.copyOnWrite();
         ((UInt64Value)this.instance).setValue(value);
         return this;
      }

      public UInt64Value.Builder clearValue() {
         this.copyOnWrite();
         ((UInt64Value)this.instance).clearValue();
         return this;
      }
   }
}
