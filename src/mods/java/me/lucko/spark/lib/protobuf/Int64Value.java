package me.lucko.spark.lib.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Int64Value extends GeneratedMessageLite<Int64Value, Int64Value.Builder> implements Int64ValueOrBuilder {
   public static final int VALUE_FIELD_NUMBER = 1;
   private long value_;
   private static final Int64Value DEFAULT_INSTANCE;
   private static volatile Parser<Int64Value> PARSER;

   private Int64Value() {
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

   public static Int64Value parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Int64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Int64Value parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Int64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Int64Value parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Int64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Int64Value parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Int64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Int64Value parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Int64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Int64Value parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Int64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Int64Value parseFrom(InputStream input) throws IOException {
      return (Int64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static Int64Value parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Int64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Int64Value parseDelimitedFrom(InputStream input) throws IOException {
      return (Int64Value)parseDelimitedFrom(DEFAULT_INSTANCE, input);
   }

   public static Int64Value parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Int64Value)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Int64Value parseFrom(CodedInputStream input) throws IOException {
      return (Int64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static Int64Value parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Int64Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Int64Value.Builder newBuilder() {
      return (Int64Value.Builder)DEFAULT_INSTANCE.createBuilder();
   }

   public static Int64Value.Builder newBuilder(Int64Value prototype) {
      return (Int64Value.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
   }

   protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
      switch(method) {
      case NEW_MUTABLE_INSTANCE:
         return new Int64Value();
      case NEW_BUILDER:
         return new Int64Value.Builder();
      case BUILD_MESSAGE_INFO:
         Object[] objects = new Object[]{"value_"};
         String info = "\u0000\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0000\u0000\u0001\u0002";
         return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      case GET_DEFAULT_INSTANCE:
         return DEFAULT_INSTANCE;
      case GET_PARSER:
         Parser<Int64Value> parser = PARSER;
         if (parser == null) {
            synchronized(Int64Value.class) {
               parser = PARSER;
               if (parser == null) {
                  parser = new GeneratedMessageLite.DefaultInstanceBasedParser<Int64Value>(DEFAULT_INSTANCE);
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

   public static Int64Value getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Int64Value of(long value) {
      return (Int64Value)newBuilder().setValue(value).build();
   }

   public static Parser<Int64Value> parser() {
      return DEFAULT_INSTANCE.getParserForType();
   }

   static {
      Int64Value defaultInstance = new Int64Value();
      DEFAULT_INSTANCE = defaultInstance;
      GeneratedMessageLite.registerDefaultInstance(Int64Value.class, defaultInstance);
   }

   public static final class Builder extends GeneratedMessageLite.Builder<Int64Value, Int64Value.Builder> implements Int64ValueOrBuilder {
      private Builder() {
         super(Int64Value.DEFAULT_INSTANCE);
      }

      public long getValue() {
         return ((Int64Value)this.instance).getValue();
      }

      public Int64Value.Builder setValue(long value) {
         this.copyOnWrite();
         ((Int64Value)this.instance).setValue(value);
         return this;
      }

      public Int64Value.Builder clearValue() {
         this.copyOnWrite();
         ((Int64Value)this.instance).clearValue();
         return this;
      }
   }
}
