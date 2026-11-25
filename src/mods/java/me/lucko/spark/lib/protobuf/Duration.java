package me.lucko.spark.lib.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Duration extends GeneratedMessageLite<Duration, Duration.Builder> implements DurationOrBuilder {
   public static final int SECONDS_FIELD_NUMBER = 1;
   private long seconds_;
   public static final int NANOS_FIELD_NUMBER = 2;
   private int nanos_;
   private static final Duration DEFAULT_INSTANCE;
   private static volatile Parser<Duration> PARSER;

   private Duration() {
   }

   public long getSeconds() {
      return this.seconds_;
   }

   private void setSeconds(long value) {
      this.seconds_ = value;
   }

   private void clearSeconds() {
      this.seconds_ = 0L;
   }

   public int getNanos() {
      return this.nanos_;
   }

   private void setNanos(int value) {
      this.nanos_ = value;
   }

   private void clearNanos() {
      this.nanos_ = 0;
   }

   public static Duration parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Duration)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Duration parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Duration)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Duration parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Duration)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Duration parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Duration)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Duration parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Duration)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Duration parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Duration)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Duration parseFrom(InputStream input) throws IOException {
      return (Duration)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static Duration parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Duration)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Duration parseDelimitedFrom(InputStream input) throws IOException {
      return (Duration)parseDelimitedFrom(DEFAULT_INSTANCE, input);
   }

   public static Duration parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Duration)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Duration parseFrom(CodedInputStream input) throws IOException {
      return (Duration)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static Duration parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Duration)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Duration.Builder newBuilder() {
      return (Duration.Builder)DEFAULT_INSTANCE.createBuilder();
   }

   public static Duration.Builder newBuilder(Duration prototype) {
      return (Duration.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
   }

   protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
      switch(method) {
      case NEW_MUTABLE_INSTANCE:
         return new Duration();
      case NEW_BUILDER:
         return new Duration.Builder();
      case BUILD_MESSAGE_INFO:
         Object[] objects = new Object[]{"seconds_", "nanos_"};
         String info = "\u0000\u0002\u0000\u0000\u0001\u0002\u0002\u0000\u0000\u0000\u0001\u0002\u0002\u0004";
         return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      case GET_DEFAULT_INSTANCE:
         return DEFAULT_INSTANCE;
      case GET_PARSER:
         Parser<Duration> parser = PARSER;
         if (parser == null) {
            synchronized(Duration.class) {
               parser = PARSER;
               if (parser == null) {
                  parser = new GeneratedMessageLite.DefaultInstanceBasedParser<Duration>(DEFAULT_INSTANCE);
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

   public static Duration getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Duration> parser() {
      return DEFAULT_INSTANCE.getParserForType();
   }

   static {
      Duration defaultInstance = new Duration();
      DEFAULT_INSTANCE = defaultInstance;
      GeneratedMessageLite.registerDefaultInstance(Duration.class, defaultInstance);
   }

   public static final class Builder extends GeneratedMessageLite.Builder<Duration, Duration.Builder> implements DurationOrBuilder {
      private Builder() {
         super(Duration.DEFAULT_INSTANCE);
      }

      public long getSeconds() {
         return ((Duration)this.instance).getSeconds();
      }

      public Duration.Builder setSeconds(long value) {
         this.copyOnWrite();
         ((Duration)this.instance).setSeconds(value);
         return this;
      }

      public Duration.Builder clearSeconds() {
         this.copyOnWrite();
         ((Duration)this.instance).clearSeconds();
         return this;
      }

      public int getNanos() {
         return ((Duration)this.instance).getNanos();
      }

      public Duration.Builder setNanos(int value) {
         this.copyOnWrite();
         ((Duration)this.instance).setNanos(value);
         return this;
      }

      public Duration.Builder clearNanos() {
         this.copyOnWrite();
         ((Duration)this.instance).clearNanos();
         return this;
      }
   }
}
