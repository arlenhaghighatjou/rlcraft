package me.lucko.spark.lib.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

public final class Method extends GeneratedMessageLite<Method, Method.Builder> implements MethodOrBuilder {
   public static final int NAME_FIELD_NUMBER = 1;
   private String name_ = "";
   public static final int REQUEST_TYPE_URL_FIELD_NUMBER = 2;
   private String requestTypeUrl_ = "";
   public static final int REQUEST_STREAMING_FIELD_NUMBER = 3;
   private boolean requestStreaming_;
   public static final int RESPONSE_TYPE_URL_FIELD_NUMBER = 4;
   private String responseTypeUrl_ = "";
   public static final int RESPONSE_STREAMING_FIELD_NUMBER = 5;
   private boolean responseStreaming_;
   public static final int OPTIONS_FIELD_NUMBER = 6;
   private Internal.ProtobufList<Option> options_ = emptyProtobufList();
   public static final int SYNTAX_FIELD_NUMBER = 7;
   private int syntax_;
   private static final Method DEFAULT_INSTANCE;
   private static volatile Parser<Method> PARSER;

   private Method() {
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

   public String getRequestTypeUrl() {
      return this.requestTypeUrl_;
   }

   public ByteString getRequestTypeUrlBytes() {
      return ByteString.copyFromUtf8(this.requestTypeUrl_);
   }

   private void setRequestTypeUrl(String value) {
      value.getClass();
      this.requestTypeUrl_ = value;
   }

   private void clearRequestTypeUrl() {
      this.requestTypeUrl_ = getDefaultInstance().getRequestTypeUrl();
   }

   private void setRequestTypeUrlBytes(ByteString value) {
      checkByteStringIsUtf8(value);
      this.requestTypeUrl_ = value.toStringUtf8();
   }

   public boolean getRequestStreaming() {
      return this.requestStreaming_;
   }

   private void setRequestStreaming(boolean value) {
      this.requestStreaming_ = value;
   }

   private void clearRequestStreaming() {
      this.requestStreaming_ = false;
   }

   public String getResponseTypeUrl() {
      return this.responseTypeUrl_;
   }

   public ByteString getResponseTypeUrlBytes() {
      return ByteString.copyFromUtf8(this.responseTypeUrl_);
   }

   private void setResponseTypeUrl(String value) {
      value.getClass();
      this.responseTypeUrl_ = value;
   }

   private void clearResponseTypeUrl() {
      this.responseTypeUrl_ = getDefaultInstance().getResponseTypeUrl();
   }

   private void setResponseTypeUrlBytes(ByteString value) {
      checkByteStringIsUtf8(value);
      this.responseTypeUrl_ = value.toStringUtf8();
   }

   public boolean getResponseStreaming() {
      return this.responseStreaming_;
   }

   private void setResponseStreaming(boolean value) {
      this.responseStreaming_ = value;
   }

   private void clearResponseStreaming() {
      this.responseStreaming_ = false;
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

   public int getSyntaxValue() {
      return this.syntax_;
   }

   public Syntax getSyntax() {
      Syntax result = Syntax.forNumber(this.syntax_);
      return result == null ? Syntax.UNRECOGNIZED : result;
   }

   private void setSyntaxValue(int value) {
      this.syntax_ = value;
   }

   private void setSyntax(Syntax value) {
      this.syntax_ = value.getNumber();
   }

   private void clearSyntax() {
      this.syntax_ = 0;
   }

   public static Method parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Method)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Method parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Method)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Method parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Method)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Method parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Method)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Method parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Method)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Method parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Method)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Method parseFrom(InputStream input) throws IOException {
      return (Method)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static Method parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Method)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Method parseDelimitedFrom(InputStream input) throws IOException {
      return (Method)parseDelimitedFrom(DEFAULT_INSTANCE, input);
   }

   public static Method parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Method)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Method parseFrom(CodedInputStream input) throws IOException {
      return (Method)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static Method parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Method)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Method.Builder newBuilder() {
      return (Method.Builder)DEFAULT_INSTANCE.createBuilder();
   }

   public static Method.Builder newBuilder(Method prototype) {
      return (Method.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
   }

   protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
      switch(method) {
      case NEW_MUTABLE_INSTANCE:
         return new Method();
      case NEW_BUILDER:
         return new Method.Builder();
      case BUILD_MESSAGE_INFO:
         Object[] objects = new Object[]{"name_", "requestTypeUrl_", "requestStreaming_", "responseTypeUrl_", "responseStreaming_", "options_", Option.class, "syntax_"};
         String info = "\u0000\u0007\u0000\u0000\u0001\u0007\u0007\u0000\u0001\u0000\u0001Ȉ\u0002Ȉ\u0003\u0007\u0004Ȉ\u0005\u0007\u0006\u001b\u0007\f";
         return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      case GET_DEFAULT_INSTANCE:
         return DEFAULT_INSTANCE;
      case GET_PARSER:
         Parser<Method> parser = PARSER;
         if (parser == null) {
            synchronized(Method.class) {
               parser = PARSER;
               if (parser == null) {
                  parser = new GeneratedMessageLite.DefaultInstanceBasedParser<Method>(DEFAULT_INSTANCE);
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

   public static Method getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Method> parser() {
      return DEFAULT_INSTANCE.getParserForType();
   }

   static {
      Method defaultInstance = new Method();
      DEFAULT_INSTANCE = defaultInstance;
      GeneratedMessageLite.registerDefaultInstance(Method.class, defaultInstance);
   }

   public static final class Builder extends GeneratedMessageLite.Builder<Method, Method.Builder> implements MethodOrBuilder {
      private Builder() {
         super(Method.DEFAULT_INSTANCE);
      }

      public String getName() {
         return ((Method)this.instance).getName();
      }

      public ByteString getNameBytes() {
         return ((Method)this.instance).getNameBytes();
      }

      public Method.Builder setName(String value) {
         this.copyOnWrite();
         ((Method)this.instance).setName(value);
         return this;
      }

      public Method.Builder clearName() {
         this.copyOnWrite();
         ((Method)this.instance).clearName();
         return this;
      }

      public Method.Builder setNameBytes(ByteString value) {
         this.copyOnWrite();
         ((Method)this.instance).setNameBytes(value);
         return this;
      }

      public String getRequestTypeUrl() {
         return ((Method)this.instance).getRequestTypeUrl();
      }

      public ByteString getRequestTypeUrlBytes() {
         return ((Method)this.instance).getRequestTypeUrlBytes();
      }

      public Method.Builder setRequestTypeUrl(String value) {
         this.copyOnWrite();
         ((Method)this.instance).setRequestTypeUrl(value);
         return this;
      }

      public Method.Builder clearRequestTypeUrl() {
         this.copyOnWrite();
         ((Method)this.instance).clearRequestTypeUrl();
         return this;
      }

      public Method.Builder setRequestTypeUrlBytes(ByteString value) {
         this.copyOnWrite();
         ((Method)this.instance).setRequestTypeUrlBytes(value);
         return this;
      }

      public boolean getRequestStreaming() {
         return ((Method)this.instance).getRequestStreaming();
      }

      public Method.Builder setRequestStreaming(boolean value) {
         this.copyOnWrite();
         ((Method)this.instance).setRequestStreaming(value);
         return this;
      }

      public Method.Builder clearRequestStreaming() {
         this.copyOnWrite();
         ((Method)this.instance).clearRequestStreaming();
         return this;
      }

      public String getResponseTypeUrl() {
         return ((Method)this.instance).getResponseTypeUrl();
      }

      public ByteString getResponseTypeUrlBytes() {
         return ((Method)this.instance).getResponseTypeUrlBytes();
      }

      public Method.Builder setResponseTypeUrl(String value) {
         this.copyOnWrite();
         ((Method)this.instance).setResponseTypeUrl(value);
         return this;
      }

      public Method.Builder clearResponseTypeUrl() {
         this.copyOnWrite();
         ((Method)this.instance).clearResponseTypeUrl();
         return this;
      }

      public Method.Builder setResponseTypeUrlBytes(ByteString value) {
         this.copyOnWrite();
         ((Method)this.instance).setResponseTypeUrlBytes(value);
         return this;
      }

      public boolean getResponseStreaming() {
         return ((Method)this.instance).getResponseStreaming();
      }

      public Method.Builder setResponseStreaming(boolean value) {
         this.copyOnWrite();
         ((Method)this.instance).setResponseStreaming(value);
         return this;
      }

      public Method.Builder clearResponseStreaming() {
         this.copyOnWrite();
         ((Method)this.instance).clearResponseStreaming();
         return this;
      }

      public List<Option> getOptionsList() {
         return Collections.unmodifiableList(((Method)this.instance).getOptionsList());
      }

      public int getOptionsCount() {
         return ((Method)this.instance).getOptionsCount();
      }

      public Option getOptions(int index) {
         return ((Method)this.instance).getOptions(index);
      }

      public Method.Builder setOptions(int index, Option value) {
         this.copyOnWrite();
         ((Method)this.instance).setOptions(index, value);
         return this;
      }

      public Method.Builder setOptions(int index, Option.Builder builderForValue) {
         this.copyOnWrite();
         ((Method)this.instance).setOptions(index, (Option)builderForValue.build());
         return this;
      }

      public Method.Builder addOptions(Option value) {
         this.copyOnWrite();
         ((Method)this.instance).addOptions(value);
         return this;
      }

      public Method.Builder addOptions(int index, Option value) {
         this.copyOnWrite();
         ((Method)this.instance).addOptions(index, value);
         return this;
      }

      public Method.Builder addOptions(Option.Builder builderForValue) {
         this.copyOnWrite();
         ((Method)this.instance).addOptions((Option)builderForValue.build());
         return this;
      }

      public Method.Builder addOptions(int index, Option.Builder builderForValue) {
         this.copyOnWrite();
         ((Method)this.instance).addOptions(index, (Option)builderForValue.build());
         return this;
      }

      public Method.Builder addAllOptions(Iterable<? extends Option> values) {
         this.copyOnWrite();
         ((Method)this.instance).addAllOptions(values);
         return this;
      }

      public Method.Builder clearOptions() {
         this.copyOnWrite();
         ((Method)this.instance).clearOptions();
         return this;
      }

      public Method.Builder removeOptions(int index) {
         this.copyOnWrite();
         ((Method)this.instance).removeOptions(index);
         return this;
      }

      public int getSyntaxValue() {
         return ((Method)this.instance).getSyntaxValue();
      }

      public Method.Builder setSyntaxValue(int value) {
         this.copyOnWrite();
         ((Method)this.instance).setSyntaxValue(value);
         return this;
      }

      public Syntax getSyntax() {
         return ((Method)this.instance).getSyntax();
      }

      public Method.Builder setSyntax(Syntax value) {
         this.copyOnWrite();
         ((Method)this.instance).setSyntax(value);
         return this;
      }

      public Method.Builder clearSyntax() {
         this.copyOnWrite();
         ((Method)this.instance).clearSyntax();
         return this;
      }
   }
}
