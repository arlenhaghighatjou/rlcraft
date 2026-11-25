package me.lucko.spark.lib.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

public final class EnumValue extends GeneratedMessageLite<EnumValue, EnumValue.Builder> implements EnumValueOrBuilder {
   public static final int NAME_FIELD_NUMBER = 1;
   private String name_ = "";
   public static final int NUMBER_FIELD_NUMBER = 2;
   private int number_;
   public static final int OPTIONS_FIELD_NUMBER = 3;
   private Internal.ProtobufList<Option> options_ = emptyProtobufList();
   private static final EnumValue DEFAULT_INSTANCE;
   private static volatile Parser<EnumValue> PARSER;

   private EnumValue() {
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

   public int getNumber() {
      return this.number_;
   }

   private void setNumber(int value) {
      this.number_ = value;
   }

   private void clearNumber() {
      this.number_ = 0;
   }

   public List<Option> getOptionsList() {
      return this.options_;
   }

   public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
      return this.options_;
   }

   public int getOptionsCount() {
      return this.options_.size();
   }

   public Option getOptions(int index) {
      return (Option)this.options_.get(index);
   }

   public OptionOrBuilder getOptionsOrBuilder(int index) {
      return (OptionOrBuilder)this.options_.get(index);
   }

   private void ensureOptionsIsMutable() {
      Internal.ProtobufList<Option> tmp = this.options_;
      if (!tmp.isModifiable()) {
         this.options_ = GeneratedMessageLite.mutableCopy(tmp);
      }

   }

   private void setOptions(int index, Option value) {
      value.getClass();
      this.ensureOptionsIsMutable();
      this.options_.set(index, value);
   }

   private void addOptions(Option value) {
      value.getClass();
      this.ensureOptionsIsMutable();
      this.options_.add(value);
   }

   private void addOptions(int index, Option value) {
      value.getClass();
      this.ensureOptionsIsMutable();
      this.options_.add(index, value);
   }

   private void addAllOptions(Iterable<? extends Option> values) {
      this.ensureOptionsIsMutable();
      AbstractMessageLite.addAll(values, this.options_);
   }

   private void clearOptions() {
      this.options_ = emptyProtobufList();
   }

   private void removeOptions(int index) {
      this.ensureOptionsIsMutable();
      this.options_.remove(index);
   }

   public static EnumValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (EnumValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static EnumValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (EnumValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static EnumValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (EnumValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static EnumValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (EnumValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static EnumValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (EnumValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static EnumValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (EnumValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static EnumValue parseFrom(InputStream input) throws IOException {
      return (EnumValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static EnumValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (EnumValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static EnumValue parseDelimitedFrom(InputStream input) throws IOException {
      return (EnumValue)parseDelimitedFrom(DEFAULT_INSTANCE, input);
   }

   public static EnumValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (EnumValue)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static EnumValue parseFrom(CodedInputStream input) throws IOException {
      return (EnumValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static EnumValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (EnumValue)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static EnumValue.Builder newBuilder() {
      return (EnumValue.Builder)DEFAULT_INSTANCE.createBuilder();
   }

   public static EnumValue.Builder newBuilder(EnumValue prototype) {
      return (EnumValue.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
   }

   protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
      switch(method) {
      case NEW_MUTABLE_INSTANCE:
         return new EnumValue();
      case NEW_BUILDER:
         return new EnumValue.Builder();
      case BUILD_MESSAGE_INFO:
         Object[] objects = new Object[]{"name_", "number_", "options_", Option.class};
         String info = "\u0000\u0003\u0000\u0000\u0001\u0003\u0003\u0000\u0001\u0000\u0001Ȉ\u0002\u0004\u0003\u001b";
         return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      case GET_DEFAULT_INSTANCE:
         return DEFAULT_INSTANCE;
      case GET_PARSER:
         Parser<EnumValue> parser = PARSER;
         if (parser == null) {
            synchronized(EnumValue.class) {
               parser = PARSER;
               if (parser == null) {
                  parser = new GeneratedMessageLite.DefaultInstanceBasedParser<EnumValue>(DEFAULT_INSTANCE);
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

   public static EnumValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<EnumValue> parser() {
      return DEFAULT_INSTANCE.getParserForType();
   }

   static {
      EnumValue defaultInstance = new EnumValue();
      DEFAULT_INSTANCE = defaultInstance;
      GeneratedMessageLite.registerDefaultInstance(EnumValue.class, defaultInstance);
   }

   public static final class Builder extends GeneratedMessageLite.Builder<EnumValue, EnumValue.Builder> implements EnumValueOrBuilder {
      private Builder() {
         super(EnumValue.DEFAULT_INSTANCE);
      }

      public String getName() {
         return ((EnumValue)this.instance).getName();
      }

      public ByteString getNameBytes() {
         return ((EnumValue)this.instance).getNameBytes();
      }

      public EnumValue.Builder setName(String value) {
         this.copyOnWrite();
         ((EnumValue)this.instance).setName(value);
         return this;
      }

      public EnumValue.Builder clearName() {
         this.copyOnWrite();
         ((EnumValue)this.instance).clearName();
         return this;
      }

      public EnumValue.Builder setNameBytes(ByteString value) {
         this.copyOnWrite();
         ((EnumValue)this.instance).setNameBytes(value);
         return this;
      }

      public int getNumber() {
         return ((EnumValue)this.instance).getNumber();
      }

      public EnumValue.Builder setNumber(int value) {
         this.copyOnWrite();
         ((EnumValue)this.instance).setNumber(value);
         return this;
      }

      public EnumValue.Builder clearNumber() {
         this.copyOnWrite();
         ((EnumValue)this.instance).clearNumber();
         return this;
      }

      public List<Option> getOptionsList() {
         return Collections.unmodifiableList(((EnumValue)this.instance).getOptionsList());
      }

      public int getOptionsCount() {
         return ((EnumValue)this.instance).getOptionsCount();
      }

      public Option getOptions(int index) {
         return ((EnumValue)this.instance).getOptions(index);
      }

      public EnumValue.Builder setOptions(int index, Option value) {
         this.copyOnWrite();
         ((EnumValue)this.instance).setOptions(index, value);
         return this;
      }

      public EnumValue.Builder setOptions(int index, Option.Builder builderForValue) {
         this.copyOnWrite();
         ((EnumValue)this.instance).setOptions(index, (Option)builderForValue.build());
         return this;
      }

      public EnumValue.Builder addOptions(Option value) {
         this.copyOnWrite();
         ((EnumValue)this.instance).addOptions(value);
         return this;
      }

      public EnumValue.Builder addOptions(int index, Option value) {
         this.copyOnWrite();
         ((EnumValue)this.instance).addOptions(index, value);
         return this;
      }

      public EnumValue.Builder addOptions(Option.Builder builderForValue) {
         this.copyOnWrite();
         ((EnumValue)this.instance).addOptions((Option)builderForValue.build());
         return this;
      }

      public EnumValue.Builder addOptions(int index, Option.Builder builderForValue) {
         this.copyOnWrite();
         ((EnumValue)this.instance).addOptions(index, (Option)builderForValue.build());
         return this;
      }

      public EnumValue.Builder addAllOptions(Iterable<? extends Option> values) {
         this.copyOnWrite();
         ((EnumValue)this.instance).addAllOptions(values);
         return this;
      }

      public EnumValue.Builder clearOptions() {
         this.copyOnWrite();
         ((EnumValue)this.instance).clearOptions();
         return this;
      }

      public EnumValue.Builder removeOptions(int index) {
         this.copyOnWrite();
         ((EnumValue)this.instance).removeOptions(index);
         return this;
      }
   }
}
