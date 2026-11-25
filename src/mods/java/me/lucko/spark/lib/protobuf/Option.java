package me.lucko.spark.lib.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Option extends GeneratedMessageLite<Option, Option.Builder> implements OptionOrBuilder {
   public static final int NAME_FIELD_NUMBER = 1;
   private String name_ = "";
   public static final int VALUE_FIELD_NUMBER = 2;
   private Any value_;
   private static final Option DEFAULT_INSTANCE;
   private static volatile Parser<Option> PARSER;

   private Option() {
   }

   public String getName() {
      return this.name_;
   }

   public ByteString getNameBytes() {
      return ByteString.copyFromUtf8(this.name_);
   }

   private void setName(String value) {
      value.getClass();
      this.name_ = value;
   }

   private void clearName() {
      this.name_ = getDefaultInstance().getName();
   }

   private void setNameBytes(ByteString value) {
      checkByteStringIsUtf8(value);
      this.name_ = value.toStringUtf8();
   }

   public boolean hasValue() {
      return this.value_ != null;
   }

   public Any getValue() {
      return this.value_ == null ? Any.getDefaultInstance() : this.value_;
   }

   private void setValue(Any value) {
      value.getClass();
      this.value_ = value;
   }

   private void mergeValue(Any value) {
      value.getClass();
      if (this.value_ != null && this.value_ != Any.getDefaultInstance()) {
         this.value_ = (Any)((Any.Builder)Any.newBuilder(this.value_).mergeFrom(value)).buildPartial();
      } else {
         this.value_ = value;
      }

   }

   private void clearValue() {
      this.value_ = null;
   }

   public static Option parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Option)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Option parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Option)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Option parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Option)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Option parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Option)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Option parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Option)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Option parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Option)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Option parseFrom(InputStream input) throws IOException {
      return (Option)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static Option parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Option)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Option parseDelimitedFrom(InputStream input) throws IOException {
      return (Option)parseDelimitedFrom(DEFAULT_INSTANCE, input);
   }

   public static Option parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Option)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Option parseFrom(CodedInputStream input) throws IOException {
      return (Option)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static Option parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Option)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Option.Builder newBuilder() {
      return (Option.Builder)DEFAULT_INSTANCE.createBuilder();
   }

   public static Option.Builder newBuilder(Option prototype) {
      return (Option.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
   }

   protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
      switch(method) {
      case NEW_MUTABLE_INSTANCE:
         return new Option();
      case NEW_BUILDER:
         return new Option.Builder();
      case BUILD_MESSAGE_INFO:
         Object[] objects = new Object[]{"name_", "value_"};
         String info = "\u0000\u0002\u0000\u0000\u0001\u0002\u0002\u0000\u0000\u0000\u0001Ȉ\u0002\t";
         return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      case GET_DEFAULT_INSTANCE:
         return DEFAULT_INSTANCE;
      case GET_PARSER:
         Parser<Option> parser = PARSER;
         if (parser == null) {
            synchronized(Option.class) {
               parser = PARSER;
               if (parser == null) {
                  parser = new GeneratedMessageLite.DefaultInstanceBasedParser<Option>(DEFAULT_INSTANCE);
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

   public static Option getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Option> parser() {
      return DEFAULT_INSTANCE.getParserForType();
   }

   static {
      Option defaultInstance = new Option();
      DEFAULT_INSTANCE = defaultInstance;
      GeneratedMessageLite.registerDefaultInstance(Option.class, defaultInstance);
   }

   public static final class Builder extends GeneratedMessageLite.Builder<Option, Option.Builder> implements OptionOrBuilder {
      private Builder() {
         super(Option.DEFAULT_INSTANCE);
      }

      public String getName() {
         return ((Option)this.instance).getName();
      }

      public ByteString getNameBytes() {
         return ((Option)this.instance).getNameBytes();
      }

      public Option.Builder setName(String value) {
         this.copyOnWrite();
         ((Option)this.instance).setName(value);
         return this;
      }

      public Option.Builder clearName() {
         this.copyOnWrite();
         ((Option)this.instance).clearName();
         return this;
      }

      public Option.Builder setNameBytes(ByteString value) {
         this.copyOnWrite();
         ((Option)this.instance).setNameBytes(value);
         return this;
      }

      public boolean hasValue() {
         return ((Option)this.instance).hasValue();
      }

      public Any getValue() {
         return ((Option)this.instance).getValue();
      }

      public Option.Builder setValue(Any value) {
         this.copyOnWrite();
         ((Option)this.instance).setValue(value);
         return this;
      }

      public Option.Builder setValue(Any.Builder builderForValue) {
         this.copyOnWrite();
         ((Option)this.instance).setValue((Any)builderForValue.build());
         return this;
      }

      public Option.Builder mergeValue(Any value) {
         this.copyOnWrite();
         ((Option)this.instance).mergeValue(value);
         return this;
      }

      public Option.Builder clearValue() {
         this.copyOnWrite();
         ((Option)this.instance).clearValue();
         return this;
      }
   }
}
