package me.lucko.spark.lib.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class StringValue extends GeneratedMessageLite<StringValue, StringValue.Builder> implements StringValueOrBuilder {
   public static final int VALUE_FIELD_NUMBER = 1;
   private String value_ = "";
   private static final StringValue DEFAULT_INSTANCE;
   private static volatile Parser<StringValue> PARSER;

   private StringValue() {
   }

   public String getValue() {
      return this.value_;
   }

   public ByteString getValueBytes() {
      return ByteString.copyFromUtf8(this.value_);
   }

   private void setValue(String value) {
      value.getClass();
      this.value_ = value;
   }

   private void clearValue() {
      this.value_ = getDefaultInstance().getValue();
   }

   private void setValueBytes(ByteString value) {
      checkByteStringIsUtf8(value);
      this.value_ = value.toStringUtf8();
   }

   public static StringValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (StringValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static StringValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (StringValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static StringValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (StringValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static StringValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (StringValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static StringValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (StringValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static StringValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (StringValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static StringValue parseFrom(InputStream input) throws IOException {
      return (StringValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static StringValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (StringValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static StringValue parseDelimitedFrom(InputStream input) throws IOException {
      return (StringValue)parseDelimitedFrom(DEFAULT_INSTANCE, input);
   }

   public static StringValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (StringValue)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static StringValue parseFrom(CodedInputStream input) throws IOException {
      return (StringValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static StringValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (StringValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static StringValue.Builder newBuilder() {
      return (StringValue.Builder)DEFAULT_INSTANCE.createBuilder();
   }

   public static StringValue.Builder newBuilder(StringValue prototype) {
      return (StringValue.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
   }

   protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
      switch(method) {
      case NEW_MUTABLE_INSTANCE:
         return new StringValue();
      case NEW_BUILDER:
         return new StringValue.Builder();
      case BUILD_MESSAGE_INFO:
         Object[] objects = new Object[]{"value_"};
         String info = "\u0000\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0000\u0000\u0001Ȉ";
         return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      case GET_DEFAULT_INSTANCE:
         return DEFAULT_INSTANCE;
      case GET_PARSER:
         Parser<StringValue> parser = PARSER;
         if (parser == null) {
            synchronized(StringValue.class) {
               parser = PARSER;
               if (parser == null) {
                  parser = new GeneratedMessageLite.DefaultInstanceBasedParser<StringValue>(DEFAULT_INSTANCE);
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

   public static StringValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static StringValue of(String value) {
      return (StringValue)newBuilder().setValue(value).build();
   }

   public static Parser<StringValue> parser() {
      return DEFAULT_INSTANCE.getParserForType();
   }

   static {
      StringValue defaultInstance = new StringValue();
      DEFAULT_INSTANCE = defaultInstance;
      GeneratedMessageLite.registerDefaultInstance(StringValue.class, defaultInstance);
   }

   public static final class Builder extends GeneratedMessageLite.Builder<StringValue, StringValue.Builder> implements StringValueOrBuilder {
      private Builder() {
         super(StringValue.DEFAULT_INSTANCE);
      }

      public String getValue() {
         return ((StringValue)this.instance).getValue();
      }

      public ByteString getValueBytes() {
         return ((StringValue)this.instance).getValueBytes();
      }

      public StringValue.Builder setValue(String value) {
         this.copyOnWrite();
         ((StringValue)this.instance).setValue(value);
         return this;
      }

      public StringValue.Builder clearValue() {
         this.copyOnWrite();
         ((StringValue)this.instance).clearValue();
         return this;
      }

      public StringValue.Builder setValueBytes(ByteString value) {
         this.copyOnWrite();
         ((StringValue)this.instance).setValueBytes(value);
         return this;
      }
   }
}
