package me.lucko.spark.lib.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class DoubleValue extends GeneratedMessageLite<DoubleValue, DoubleValue.Builder> implements DoubleValueOrBuilder {
   public static final int VALUE_FIELD_NUMBER = 1;
   private double value_;
   private static final DoubleValue DEFAULT_INSTANCE;
   private static volatile Parser<DoubleValue> PARSER;

   private DoubleValue() {
   }

   public double getValue() {
      return this.value_;
   }

   private void setValue(double value) {
      this.value_ = value;
   }

   private void clearValue() {
      this.value_ = 0.0D;
   }

   public static DoubleValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (DoubleValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static DoubleValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (DoubleValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static DoubleValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (DoubleValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static DoubleValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (DoubleValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static DoubleValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (DoubleValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static DoubleValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (DoubleValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static DoubleValue parseFrom(InputStream input) throws IOException {
      return (DoubleValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static DoubleValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (DoubleValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static DoubleValue parseDelimitedFrom(InputStream input) throws IOException {
      return (DoubleValue)parseDelimitedFrom(DEFAULT_INSTANCE, input);
   }

   public static DoubleValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (DoubleValue)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static DoubleValue parseFrom(CodedInputStream input) throws IOException {
      return (DoubleValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static DoubleValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (DoubleValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static DoubleValue.Builder newBuilder() {
      return (DoubleValue.Builder)DEFAULT_INSTANCE.createBuilder();
   }

   public static DoubleValue.Builder newBuilder(DoubleValue prototype) {
      return (DoubleValue.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
   }

   protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
      switch(method) {
      case NEW_MUTABLE_INSTANCE:
         return new DoubleValue();
      case NEW_BUILDER:
         return new DoubleValue.Builder();
      case BUILD_MESSAGE_INFO:
         Object[] objects = new Object[]{"value_"};
         String info = "\u0000\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0000\u0000\u0001\u0000";
         return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      case GET_DEFAULT_INSTANCE:
         return DEFAULT_INSTANCE;
      case GET_PARSER:
         Parser<DoubleValue> parser = PARSER;
         if (parser == null) {
            synchronized(DoubleValue.class) {
               parser = PARSER;
               if (parser == null) {
                  parser = new GeneratedMessageLite.DefaultInstanceBasedParser<DoubleValue>(DEFAULT_INSTANCE);
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

   public static DoubleValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static DoubleValue of(double value) {
      return (DoubleValue)newBuilder().setValue(value).build();
   }

   public static Parser<DoubleValue> parser() {
      return DEFAULT_INSTANCE.getParserForType();
   }

   static {
      DoubleValue defaultInstance = new DoubleValue();
      DEFAULT_INSTANCE = defaultInstance;
      GeneratedMessageLite.registerDefaultInstance(DoubleValue.class, defaultInstance);
   }

   public static final class Builder extends GeneratedMessageLite.Builder<DoubleValue, DoubleValue.Builder> implements DoubleValueOrBuilder {
      private Builder() {
         super(DoubleValue.DEFAULT_INSTANCE);
      }

      public double getValue() {
         return ((DoubleValue)this.instance).getValue();
      }

      public DoubleValue.Builder setValue(double value) {
         this.copyOnWrite();
         ((DoubleValue)this.instance).setValue(value);
         return this;
      }

      public DoubleValue.Builder clearValue() {
         this.copyOnWrite();
         ((DoubleValue)this.instance).clearValue();
         return this;
      }
   }
}
