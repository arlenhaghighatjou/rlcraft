package me.lucko.spark.lib.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Int32Value extends GeneratedMessageLite<Int32Value, Int32Value.Builder> implements Int32ValueOrBuilder {
   public static final int VALUE_FIELD_NUMBER = 1;
   private int value_;
   private static final Int32Value DEFAULT_INSTANCE;
   private static volatile Parser<Int32Value> PARSER;

   private Int32Value() {
   }

   public int getValue() {
      return this.value_;
   }

   private void setValue(int value) {
      this.value_ = value;
   }

   private void clearValue() {
      this.value_ = 0;
   }

   public static Int32Value parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Int32Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Int32Value parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Int32Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Int32Value parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Int32Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Int32Value parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Int32Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Int32Value parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Int32Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Int32Value parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Int32Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Int32Value parseFrom(InputStream input) throws IOException {
      return (Int32Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static Int32Value parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Int32Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Int32Value parseDelimitedFrom(InputStream input) throws IOException {
      return (Int32Value)parseDelimitedFrom(DEFAULT_INSTANCE, input);
   }

   public static Int32Value parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Int32Value)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Int32Value parseFrom(CodedInputStream input) throws IOException {
      return (Int32Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static Int32Value parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Int32Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Int32Value.Builder newBuilder() {
      return (Int32Value.Builder)DEFAULT_INSTANCE.createBuilder();
   }

   public static Int32Value.Builder newBuilder(Int32Value prototype) {
      return (Int32Value.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
   }

   protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
      switch(method) {
      case NEW_MUTABLE_INSTANCE:
         return new Int32Value();
      case NEW_BUILDER:
         return new Int32Value.Builder();
      case BUILD_MESSAGE_INFO:
         Object[] objects = new Object[]{"value_"};
         String info = "\u0000\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0000\u0000\u0001\u0004";
         return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      case GET_DEFAULT_INSTANCE:
         return DEFAULT_INSTANCE;
      case GET_PARSER:
         Parser<Int32Value> parser = PARSER;
         if (parser == null) {
            synchronized(Int32Value.class) {
               parser = PARSER;
               if (parser == null) {
                  parser = new GeneratedMessageLite.DefaultInstanceBasedParser<Int32Value>(DEFAULT_INSTANCE);
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

   public static Int32Value getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Int32Value of(int value) {
      return (Int32Value)newBuilder().setValue(value).build();
   }

   public static Parser<Int32Value> parser() {
      return DEFAULT_INSTANCE.getParserForType();
   }

   static {
      Int32Value defaultInstance = new Int32Value();
      DEFAULT_INSTANCE = defaultInstance;
      GeneratedMessageLite.registerDefaultInstance(Int32Value.class, defaultInstance);
   }

   public static final class Builder extends GeneratedMessageLite.Builder<Int32Value, Int32Value.Builder> implements Int32ValueOrBuilder {
      private Builder() {
         super(Int32Value.DEFAULT_INSTANCE);
      }

      public int getValue() {
         return ((Int32Value)this.instance).getValue();
      }

      public Int32Value.Builder setValue(int value) {
         this.copyOnWrite();
         ((Int32Value)this.instance).setValue(value);
         return this;
      }

      public Int32Value.Builder clearValue() {
         this.copyOnWrite();
         ((Int32Value)this.instance).clearValue();
         return this;
      }
   }
}
