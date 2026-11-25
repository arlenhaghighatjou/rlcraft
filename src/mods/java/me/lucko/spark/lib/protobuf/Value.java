package me.lucko.spark.lib.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Value extends GeneratedMessageLite<Value, Value.Builder> implements ValueOrBuilder {
   private int kindCase_ = 0;
   private Object kind_;
   public static final int NULL_VALUE_FIELD_NUMBER = 1;
   public static final int NUMBER_VALUE_FIELD_NUMBER = 2;
   public static final int STRING_VALUE_FIELD_NUMBER = 3;
   public static final int BOOL_VALUE_FIELD_NUMBER = 4;
   public static final int STRUCT_VALUE_FIELD_NUMBER = 5;
   public static final int LIST_VALUE_FIELD_NUMBER = 6;
   private static final Value DEFAULT_INSTANCE;
   private static volatile Parser<Value> PARSER;

   private Value() {
   }

   public Value.KindCase getKindCase() {
      return Value.KindCase.forNumber(this.kindCase_);
   }

   private void clearKind() {
      this.kindCase_ = 0;
      this.kind_ = null;
   }

   public boolean hasNullValue() {
      return this.kindCase_ == 1;
   }

   public int getNullValueValue() {
      return this.kindCase_ == 1 ? ((Integer)this.kind_).intValue() : 0;
   }

   public NullValue getNullValue() {
      if (this.kindCase_ == 1) {
         NullValue result = NullValue.forNumber(((Integer)this.kind_).intValue());
         return result == null ? NullValue.UNRECOGNIZED : result;
      } else {
         return NullValue.NULL_VALUE;
      }
   }

   private void setNullValueValue(int value) {
      this.kindCase_ = 1;
      this.kind_ = value;
   }

   private void setNullValue(NullValue value) {
      this.kind_ = value.getNumber();
      this.kindCase_ = 1;
   }

   private void clearNullValue() {
      if (this.kindCase_ == 1) {
         this.kindCase_ = 0;
         this.kind_ = null;
      }

   }

   public boolean hasNumberValue() {
      return this.kindCase_ == 2;
   }

   public double getNumberValue() {
      return this.kindCase_ == 2 ? ((Double)this.kind_).doubleValue() : 0.0D;
   }

   private void setNumberValue(double value) {
      this.kindCase_ = 2;
      this.kind_ = value;
   }

   private void clearNumberValue() {
      if (this.kindCase_ == 2) {
         this.kindCase_ = 0;
         this.kind_ = null;
      }

   }

   public boolean hasStringValue() {
      return this.kindCase_ == 3;
   }

   public String getStringValue() {
      String ref = "";
      if (this.kindCase_ == 3) {
         ref = (String)this.kind_;
      }

      return ref;
   }

   public ByteString getStringValueBytes() {
      String ref = "";
      if (this.kindCase_ == 3) {
         ref = (String)this.kind_;
      }

      return ByteString.copyFromUtf8(ref);
   }

   private void setStringValue(String value) {
      value.getClass();
      this.kindCase_ = 3;
      this.kind_ = value;
   }

   private void clearStringValue() {
      if (this.kindCase_ == 3) {
         this.kindCase_ = 0;
         this.kind_ = null;
      }

   }

   private void setStringValueBytes(ByteString value) {
      checkByteStringIsUtf8(value);
      this.kind_ = value.toStringUtf8();
      this.kindCase_ = 3;
   }

   public boolean hasBoolValue() {
      return this.kindCase_ == 4;
   }

   public boolean getBoolValue() {
      return this.kindCase_ == 4 ? ((Boolean)this.kind_).booleanValue() : false;
   }

   private void setBoolValue(boolean value) {
      this.kindCase_ = 4;
      this.kind_ = value;
   }

   private void clearBoolValue() {
      if (this.kindCase_ == 4) {
         this.kindCase_ = 0;
         this.kind_ = null;
      }

   }

   public boolean hasStructValue() {
      return this.kindCase_ == 5;
   }

   public Struct getStructValue() {
      return this.kindCase_ == 5 ? (Struct)this.kind_ : Struct.getDefaultInstance();
   }

   private void setStructValue(Struct value) {
      value.getClass();
      this.kind_ = value;
      this.kindCase_ = 5;
   }

   private void mergeStructValue(Struct value) {
      value.getClass();
      if (this.kindCase_ == 5 && this.kind_ != Struct.getDefaultInstance()) {
         this.kind_ = ((Struct.Builder)Struct.newBuilder((Struct)this.kind_).mergeFrom(value)).buildPartial();
      } else {
         this.kind_ = value;
      }

      this.kindCase_ = 5;
   }

   private void clearStructValue() {
      if (this.kindCase_ == 5) {
         this.kindCase_ = 0;
         this.kind_ = null;
      }

   }

   public boolean hasListValue() {
      return this.kindCase_ == 6;
   }

   public ListValue getListValue() {
      return this.kindCase_ == 6 ? (ListValue)this.kind_ : ListValue.getDefaultInstance();
   }

   private void setListValue(ListValue value) {
      value.getClass();
      this.kind_ = value;
      this.kindCase_ = 6;
   }

   private void mergeListValue(ListValue value) {
      value.getClass();
      if (this.kindCase_ == 6 && this.kind_ != ListValue.getDefaultInstance()) {
         this.kind_ = ((ListValue.Builder)ListValue.newBuilder((ListValue)this.kind_).mergeFrom(value)).buildPartial();
      } else {
         this.kind_ = value;
      }

      this.kindCase_ = 6;
   }

   private void clearListValue() {
      if (this.kindCase_ == 6) {
         this.kindCase_ = 0;
         this.kind_ = null;
      }

   }

   public static Value parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Value parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Value parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Value parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Value parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static Value parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static Value parseFrom(InputStream input) throws IOException {
      return (Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static Value parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Value parseDelimitedFrom(InputStream input) throws IOException {
      return (Value)parseDelimitedFrom(DEFAULT_INSTANCE, input);
   }

   public static Value parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Value)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Value parseFrom(CodedInputStream input) throws IOException {
      return (Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static Value parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Value)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static Value.Builder newBuilder() {
      return (Value.Builder)DEFAULT_INSTANCE.createBuilder();
   }

   public static Value.Builder newBuilder(Value prototype) {
      return (Value.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
   }

   protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
      switch(method) {
      case NEW_MUTABLE_INSTANCE:
         return new Value();
      case NEW_BUILDER:
         return new Value.Builder();
      case BUILD_MESSAGE_INFO:
         Object[] objects = new Object[]{"kind_", "kindCase_", Struct.class, ListValue.class};
         String info = "\u0000\u0006\u0001\u0000\u0001\u0006\u0006\u0000\u0000\u0000\u0001?\u0000\u00023\u0000\u0003Ȼ\u0000\u0004:\u0000\u0005<\u0000\u0006<\u0000";
         return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      case GET_DEFAULT_INSTANCE:
         return DEFAULT_INSTANCE;
      case GET_PARSER:
         Parser<Value> parser = PARSER;
         if (parser == null) {
            synchronized(Value.class) {
               parser = PARSER;
               if (parser == null) {
                  parser = new GeneratedMessageLite.DefaultInstanceBasedParser<Value>(DEFAULT_INSTANCE);
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

   public static Value getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Value> parser() {
      return DEFAULT_INSTANCE.getParserForType();
   }

   static {
      Value defaultInstance = new Value();
      DEFAULT_INSTANCE = defaultInstance;
      GeneratedMessageLite.registerDefaultInstance(Value.class, defaultInstance);
   }

   public static final class Builder extends GeneratedMessageLite.Builder<Value, Value.Builder> implements ValueOrBuilder {
      private Builder() {
         super(Value.DEFAULT_INSTANCE);
      }

      public Value.KindCase getKindCase() {
         return ((Value)this.instance).getKindCase();
      }

      public Value.Builder clearKind() {
         this.copyOnWrite();
         ((Value)this.instance).clearKind();
         return this;
      }

      public boolean hasNullValue() {
         return ((Value)this.instance).hasNullValue();
      }

      public int getNullValueValue() {
         return ((Value)this.instance).getNullValueValue();
      }

      public Value.Builder setNullValueValue(int value) {
         this.copyOnWrite();
         ((Value)this.instance).setNullValueValue(value);
         return this;
      }

      public NullValue getNullValue() {
         return ((Value)this.instance).getNullValue();
      }

      public Value.Builder setNullValue(NullValue value) {
         this.copyOnWrite();
         ((Value)this.instance).setNullValue(value);
         return this;
      }

      public Value.Builder clearNullValue() {
         this.copyOnWrite();
         ((Value)this.instance).clearNullValue();
         return this;
      }

      public boolean hasNumberValue() {
         return ((Value)this.instance).hasNumberValue();
      }

      public double getNumberValue() {
         return ((Value)this.instance).getNumberValue();
      }

      public Value.Builder setNumberValue(double value) {
         this.copyOnWrite();
         ((Value)this.instance).setNumberValue(value);
         return this;
      }

      public Value.Builder clearNumberValue() {
         this.copyOnWrite();
         ((Value)this.instance).clearNumberValue();
         return this;
      }

      public boolean hasStringValue() {
         return ((Value)this.instance).hasStringValue();
      }

      public String getStringValue() {
         return ((Value)this.instance).getStringValue();
      }

      public ByteString getStringValueBytes() {
         return ((Value)this.instance).getStringValueBytes();
      }

      public Value.Builder setStringValue(String value) {
         this.copyOnWrite();
         ((Value)this.instance).setStringValue(value);
         return this;
      }

      public Value.Builder clearStringValue() {
         this.copyOnWrite();
         ((Value)this.instance).clearStringValue();
         return this;
      }

      public Value.Builder setStringValueBytes(ByteString value) {
         this.copyOnWrite();
         ((Value)this.instance).setStringValueBytes(value);
         return this;
      }

      public boolean hasBoolValue() {
         return ((Value)this.instance).hasBoolValue();
      }

      public boolean getBoolValue() {
         return ((Value)this.instance).getBoolValue();
      }

      public Value.Builder setBoolValue(boolean value) {
         this.copyOnWrite();
         ((Value)this.instance).setBoolValue(value);
         return this;
      }

      public Value.Builder clearBoolValue() {
         this.copyOnWrite();
         ((Value)this.instance).clearBoolValue();
         return this;
      }

      public boolean hasStructValue() {
         return ((Value)this.instance).hasStructValue();
      }

      public Struct getStructValue() {
         return ((Value)this.instance).getStructValue();
      }

      public Value.Builder setStructValue(Struct value) {
         this.copyOnWrite();
         ((Value)this.instance).setStructValue(value);
         return this;
      }

      public Value.Builder setStructValue(Struct.Builder builderForValue) {
         this.copyOnWrite();
         ((Value)this.instance).setStructValue((Struct)builderForValue.build());
         return this;
      }

      public Value.Builder mergeStructValue(Struct value) {
         this.copyOnWrite();
         ((Value)this.instance).mergeStructValue(value);
         return this;
      }

      public Value.Builder clearStructValue() {
         this.copyOnWrite();
         ((Value)this.instance).clearStructValue();
         return this;
      }

      public boolean hasListValue() {
         return ((Value)this.instance).hasListValue();
      }

      public ListValue getListValue() {
         return ((Value)this.instance).getListValue();
      }

      public Value.Builder setListValue(ListValue value) {
         this.copyOnWrite();
         ((Value)this.instance).setListValue(value);
         return this;
      }

      public Value.Builder setListValue(ListValue.Builder builderForValue) {
         this.copyOnWrite();
         ((Value)this.instance).setListValue((ListValue)builderForValue.build());
         return this;
      }

      public Value.Builder mergeListValue(ListValue value) {
         this.copyOnWrite();
         ((Value)this.instance).mergeListValue(value);
         return this;
      }

      public Value.Builder clearListValue() {
         this.copyOnWrite();
         ((Value)this.instance).clearListValue();
         return this;
      }
   }

   public static enum KindCase {
      NULL_VALUE(1),
      NUMBER_VALUE(2),
      STRING_VALUE(3),
      BOOL_VALUE(4),
      STRUCT_VALUE(5),
      LIST_VALUE(6),
      KIND_NOT_SET(0);

      private final int value;

      private KindCase(int value) {
         this.value = value;
      }

      /** @deprecated */
      @Deprecated
      public static Value.KindCase valueOf(int value) {
         return forNumber(value);
      }

      public static Value.KindCase forNumber(int value) {
         switch(value) {
         case 0:
            return KIND_NOT_SET;
         case 1:
            return NULL_VALUE;
         case 2:
            return NUMBER_VALUE;
         case 3:
            return STRING_VALUE;
         case 4:
            return BOOL_VALUE;
         case 5:
            return STRUCT_VALUE;
         case 6:
            return LIST_VALUE;
         default:
            return null;
         }
      }

      public int getNumber() {
         return this.value;
      }
   }
}
