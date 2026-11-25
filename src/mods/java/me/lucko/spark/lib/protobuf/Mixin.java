package me.lucko.spark.lib.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Mixin extends GeneratedMessageLite<Mixin, Mixin.Builder> implements MixinOrBuilder {
   public static final int NAME_FIELD_NUMBER = 1;
   private String name_ = "";
   public static final int ROOT_FIELD_NUMBER = 2;
   private String root_ = "";
   private static final Mixin DEFAULT_INSTANCE;
   private static volatile Parser<Mixin> PARSER;

   private Mixin() {
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

   public String getRoot() {
      return this.root_;
   }

   public ByteString getRootBytes() {
      return ByteString.copyFromUtf8(this.root_);
   }

   private void setRoot(String value) {
      value.getClass();
      this.root_ = value;
   }

   private void clearRoot() {
      this.root_ = getDefaultInstance().getRoot();
   }

   private void setRootBytes(ByteString value) {
      checkByteStringIsUtf8(value);
      this.root_ = value.toStringUtf8();
   }

   public static Mixin parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Mixin)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Mixin parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Mixin)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Mixin parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Mixin)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Mixin parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Mixin)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Mixin parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Mixin)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Mixin parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Mixin)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Mixin parseFrom(InputStream input) throws IOException {
      return (Mixin)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static Mixin parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Mixin)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Mixin parseDelimitedFrom(InputStream input) throws IOException {
      return (Mixin)parseDelimitedFrom(DEFAULT_INSTANCE, input);
   }

   public static Mixin parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Mixin)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Mixin parseFrom(CodedInputStream input) throws IOException {
      return (Mixin)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static Mixin parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Mixin)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Mixin.Builder newBuilder() {
      return (Mixin.Builder)DEFAULT_INSTANCE.createBuilder();
   }

   public static Mixin.Builder newBuilder(Mixin prototype) {
      return (Mixin.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
   }

   protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
      switch(method) {
      case NEW_MUTABLE_INSTANCE:
         return new Mixin();
      case NEW_BUILDER:
         return new Mixin.Builder();
      case BUILD_MESSAGE_INFO:
         Object[] objects = new Object[]{"name_", "root_"};
         String info = "\u0000\u0002\u0000\u0000\u0001\u0002\u0002\u0000\u0000\u0000\u0001Ȉ\u0002Ȉ";
         return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      case GET_DEFAULT_INSTANCE:
         return DEFAULT_INSTANCE;
      case GET_PARSER:
         Parser<Mixin> parser = PARSER;
         if (parser == null) {
            synchronized(Mixin.class) {
               parser = PARSER;
               if (parser == null) {
                  parser = new GeneratedMessageLite.DefaultInstanceBasedParser<Mixin>(DEFAULT_INSTANCE);
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

   public static Mixin getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Mixin> parser() {
      return DEFAULT_INSTANCE.getParserForType();
   }

   static {
      Mixin defaultInstance = new Mixin();
      DEFAULT_INSTANCE = defaultInstance;
      GeneratedMessageLite.registerDefaultInstance(Mixin.class, defaultInstance);
   }

   public static final class Builder extends GeneratedMessageLite.Builder<Mixin, Mixin.Builder> implements MixinOrBuilder {
      private Builder() {
         super(Mixin.DEFAULT_INSTANCE);
      }

      public String getName() {
         return ((Mixin)this.instance).getName();
      }

      public ByteString getNameBytes() {
         return ((Mixin)this.instance).getNameBytes();
      }

      public Mixin.Builder setName(String value) {
         this.copyOnWrite();
         ((Mixin)this.instance).setName(value);
         return this;
      }

      public Mixin.Builder clearName() {
         this.copyOnWrite();
         ((Mixin)this.instance).clearName();
         return this;
      }

      public Mixin.Builder setNameBytes(ByteString value) {
         this.copyOnWrite();
         ((Mixin)this.instance).setNameBytes(value);
         return this;
      }

      public String getRoot() {
         return ((Mixin)this.instance).getRoot();
      }

      public ByteString getRootBytes() {
         return ((Mixin)this.instance).getRootBytes();
      }

      public Mixin.Builder setRoot(String value) {
         this.copyOnWrite();
         ((Mixin)this.instance).setRoot(value);
         return this;
      }

      public Mixin.Builder clearRoot() {
         this.copyOnWrite();
         ((Mixin)this.instance).clearRoot();
         return this;
      }

      public Mixin.Builder setRootBytes(ByteString value) {
         this.copyOnWrite();
         ((Mixin)this.instance).setRootBytes(value);
         return this;
      }
   }
}
