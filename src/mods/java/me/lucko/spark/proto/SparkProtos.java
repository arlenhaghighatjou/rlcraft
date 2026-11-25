package me.lucko.spark.proto;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import me.lucko.spark.lib.protobuf.AbstractMessageLite;
import me.lucko.spark.lib.protobuf.ByteString;
import me.lucko.spark.lib.protobuf.CodedInputStream;
import me.lucko.spark.lib.protobuf.ExtensionRegistryLite;
import me.lucko.spark.lib.protobuf.GeneratedMessageLite;
import me.lucko.spark.lib.protobuf.Internal;
import me.lucko.spark.lib.protobuf.InvalidProtocolBufferException;
import me.lucko.spark.lib.protobuf.MapEntryLite;
import me.lucko.spark.lib.protobuf.MapFieldLite;
import me.lucko.spark.lib.protobuf.MessageLiteOrBuilder;
import me.lucko.spark.lib.protobuf.Parser;
import me.lucko.spark.lib.protobuf.WireFormat;

public final class SparkProtos {
   private SparkProtos() {
   }

   public static void registerAllExtensions(ExtensionRegistryLite registry) {
   }

   public static final class CommandSenderMetadata extends GeneratedMessageLite<SparkProtos.CommandSenderMetadata, SparkProtos.CommandSenderMetadata.Builder> implements SparkProtos.CommandSenderMetadataOrBuilder {
      public static final int TYPE_FIELD_NUMBER = 1;
      private int type_;
      public static final int NAME_FIELD_NUMBER = 2;
      private String name_ = "";
      public static final int UNIQUE_ID_FIELD_NUMBER = 3;
      private String uniqueId_ = "";
      private static final SparkProtos.CommandSenderMetadata DEFAULT_INSTANCE;
      private static volatile Parser<SparkProtos.CommandSenderMetadata> PARSER;

      private CommandSenderMetadata() {
      }

      public int getTypeValue() {
         return this.type_;
      }

      public SparkProtos.CommandSenderMetadata.Type getType() {
         SparkProtos.CommandSenderMetadata.Type result = SparkProtos.CommandSenderMetadata.Type.forNumber(this.type_);
         return result == null ? SparkProtos.CommandSenderMetadata.Type.UNRECOGNIZED : result;
      }

      private void setTypeValue(int value) {
         this.type_ = value;
      }

      private void setType(SparkProtos.CommandSenderMetadata.Type value) {
         this.type_ = value.getNumber();
      }

      private void clearType() {
         this.type_ = 0;
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

      public String getUniqueId() {
         return this.uniqueId_;
      }

      public ByteString getUniqueIdBytes() {
         return ByteString.copyFromUtf8(this.uniqueId_);
      }

      private void setUniqueId(String value) {
         value.getClass();
         this.uniqueId_ = value;
      }

      private void clearUniqueId() {
         this.uniqueId_ = getDefaultInstance().getUniqueId();
      }

      private void setUniqueIdBytes(ByteString value) {
         checkByteStringIsUtf8(value);
         this.uniqueId_ = value.toStringUtf8();
      }

      public static SparkProtos.CommandSenderMetadata parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (SparkProtos.CommandSenderMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.CommandSenderMetadata parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.CommandSenderMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.CommandSenderMetadata parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (SparkProtos.CommandSenderMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.CommandSenderMetadata parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.CommandSenderMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.CommandSenderMetadata parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (SparkProtos.CommandSenderMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.CommandSenderMetadata parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.CommandSenderMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.CommandSenderMetadata parseFrom(InputStream input) throws IOException {
         return (SparkProtos.CommandSenderMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.CommandSenderMetadata parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.CommandSenderMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.CommandSenderMetadata parseDelimitedFrom(InputStream input) throws IOException {
         return (SparkProtos.CommandSenderMetadata)parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.CommandSenderMetadata parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.CommandSenderMetadata)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.CommandSenderMetadata parseFrom(CodedInputStream input) throws IOException {
         return (SparkProtos.CommandSenderMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.CommandSenderMetadata parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.CommandSenderMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.CommandSenderMetadata.Builder newBuilder() {
         return (SparkProtos.CommandSenderMetadata.Builder)DEFAULT_INSTANCE.createBuilder();
      }

      public static SparkProtos.CommandSenderMetadata.Builder newBuilder(SparkProtos.CommandSenderMetadata prototype) {
         return (SparkProtos.CommandSenderMetadata.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
      }

      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch(method) {
         case NEW_MUTABLE_INSTANCE:
            return new SparkProtos.CommandSenderMetadata();
         case NEW_BUILDER:
            return new SparkProtos.CommandSenderMetadata.Builder();
         case BUILD_MESSAGE_INFO:
            Object[] objects = new Object[]{"type_", "name_", "uniqueId_"};
            String info = "\u0000\u0003\u0000\u0000\u0001\u0003\u0003\u0000\u0000\u0000\u0001\f\u0002Ȉ\u0003Ȉ";
            return newMessageInfo(DEFAULT_INSTANCE, info, objects);
         case GET_DEFAULT_INSTANCE:
            return DEFAULT_INSTANCE;
         case GET_PARSER:
            Parser<SparkProtos.CommandSenderMetadata> parser = PARSER;
            if (parser == null) {
               synchronized(SparkProtos.CommandSenderMetadata.class) {
                  parser = PARSER;
                  if (parser == null) {
                     parser = new GeneratedMessageLite.DefaultInstanceBasedParser<SparkProtos.CommandSenderMetadata>(DEFAULT_INSTANCE);
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

      public static SparkProtos.CommandSenderMetadata getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<SparkProtos.CommandSenderMetadata> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         SparkProtos.CommandSenderMetadata defaultInstance = new SparkProtos.CommandSenderMetadata();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(SparkProtos.CommandSenderMetadata.class, defaultInstance);
      }

      public static final class Builder extends GeneratedMessageLite.Builder<SparkProtos.CommandSenderMetadata, SparkProtos.CommandSenderMetadata.Builder> implements SparkProtos.CommandSenderMetadataOrBuilder {
         private Builder() {
            super(SparkProtos.CommandSenderMetadata.DEFAULT_INSTANCE);
         }

         public int getTypeValue() {
            return ((SparkProtos.CommandSenderMetadata)this.instance).getTypeValue();
         }

         public SparkProtos.CommandSenderMetadata.Builder setTypeValue(int value) {
            this.copyOnWrite();
            ((SparkProtos.CommandSenderMetadata)this.instance).setTypeValue(value);
            return this;
         }

         public SparkProtos.CommandSenderMetadata.Type getType() {
            return ((SparkProtos.CommandSenderMetadata)this.instance).getType();
         }

         public SparkProtos.CommandSenderMetadata.Builder setType(SparkProtos.CommandSenderMetadata.Type value) {
            this.copyOnWrite();
            ((SparkProtos.CommandSenderMetadata)this.instance).setType(value);
            return this;
         }

         public SparkProtos.CommandSenderMetadata.Builder clearType() {
            this.copyOnWrite();
            ((SparkProtos.CommandSenderMetadata)this.instance).clearType();
            return this;
         }

         public String getName() {
            return ((SparkProtos.CommandSenderMetadata)this.instance).getName();
         }

         public ByteString getNameBytes() {
            return ((SparkProtos.CommandSenderMetadata)this.instance).getNameBytes();
         }

         public SparkProtos.CommandSenderMetadata.Builder setName(String value) {
            this.copyOnWrite();
            ((SparkProtos.CommandSenderMetadata)this.instance).setName(value);
            return this;
         }

         public SparkProtos.CommandSenderMetadata.Builder clearName() {
            this.copyOnWrite();
            ((SparkProtos.CommandSenderMetadata)this.instance).clearName();
            return this;
         }

         public SparkProtos.CommandSenderMetadata.Builder setNameBytes(ByteString value) {
            this.copyOnWrite();
            ((SparkProtos.CommandSenderMetadata)this.instance).setNameBytes(value);
            return this;
         }

         public String getUniqueId() {
            return ((SparkProtos.CommandSenderMetadata)this.instance).getUniqueId();
         }

         public ByteString getUniqueIdBytes() {
            return ((SparkProtos.CommandSenderMetadata)this.instance).getUniqueIdBytes();
         }

         public SparkProtos.CommandSenderMetadata.Builder setUniqueId(String value) {
            this.copyOnWrite();
            ((SparkProtos.CommandSenderMetadata)this.instance).setUniqueId(value);
            return this;
         }

         public SparkProtos.CommandSenderMetadata.Builder clearUniqueId() {
            this.copyOnWrite();
            ((SparkProtos.CommandSenderMetadata)this.instance).clearUniqueId();
            return this;
         }

         public SparkProtos.CommandSenderMetadata.Builder setUniqueIdBytes(ByteString value) {
            this.copyOnWrite();
            ((SparkProtos.CommandSenderMetadata)this.instance).setUniqueIdBytes(value);
            return this;
         }
      }

      public static enum Type implements Internal.EnumLite {
         OTHER(0),
         PLAYER(1),
         UNRECOGNIZED(-1);

         public static final int OTHER_VALUE = 0;
         public static final int PLAYER_VALUE = 1;
         private static final Internal.EnumLiteMap<SparkProtos.CommandSenderMetadata.Type> internalValueMap = new Internal.EnumLiteMap<SparkProtos.CommandSenderMetadata.Type>() {
            public SparkProtos.CommandSenderMetadata.Type findValueByNumber(int number) {
               return SparkProtos.CommandSenderMetadata.Type.forNumber(number);
            }
         };
         private final int value;

         public final int getNumber() {
            if (this == UNRECOGNIZED) {
               throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
            } else {
               return this.value;
            }
         }

         /** @deprecated */
         @Deprecated
         public static SparkProtos.CommandSenderMetadata.Type valueOf(int value) {
            return forNumber(value);
         }

         public static SparkProtos.CommandSenderMetadata.Type forNumber(int value) {
            switch(value) {
            case 0:
               return OTHER;
            case 1:
               return PLAYER;
            default:
               return null;
            }
         }

         public static Internal.EnumLiteMap<SparkProtos.CommandSenderMetadata.Type> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return SparkProtos.CommandSenderMetadata.Type.TypeVerifier.INSTANCE;
         }

         private Type(int value) {
            this.value = value;
         }

         private static final class TypeVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new SparkProtos.CommandSenderMetadata.Type.TypeVerifier();

            private TypeVerifier() {
            }

            public boolean isInRange(int number) {
               return SparkProtos.CommandSenderMetadata.Type.forNumber(number) != null;
            }
         }
      }
   }

   public interface CommandSenderMetadataOrBuilder extends MessageLiteOrBuilder {
      int getTypeValue();

      SparkProtos.CommandSenderMetadata.Type getType();

      String getName();

      ByteString getNameBytes();

      String getUniqueId();

      ByteString getUniqueIdBytes();
   }

   public static final class HeapData extends GeneratedMessageLite<SparkProtos.HeapData, SparkProtos.HeapData.Builder> implements SparkProtos.HeapDataOrBuilder {
      public static final int METADATA_FIELD_NUMBER = 1;
      private SparkProtos.HeapMetadata metadata_;
      public static final int ENTRIES_FIELD_NUMBER = 2;
      private Internal.ProtobufList<SparkProtos.HeapEntry> entries_ = emptyProtobufList();
      private static final SparkProtos.HeapData DEFAULT_INSTANCE;
      private static volatile Parser<SparkProtos.HeapData> PARSER;

      private HeapData() {
      }

      public boolean hasMetadata() {
         return this.metadata_ != null;
      }

      public SparkProtos.HeapMetadata getMetadata() {
         return this.metadata_ == null ? SparkProtos.HeapMetadata.DEFAULT_INSTANCE : this.metadata_;
      }

      private void setMetadata(SparkProtos.HeapMetadata value) {
         value.getClass();
         this.metadata_ = value;
      }

      private void mergeMetadata(SparkProtos.HeapMetadata value) {
         value.getClass();
         if (this.metadata_ != null && this.metadata_ != SparkProtos.HeapMetadata.DEFAULT_INSTANCE) {
            this.metadata_ = (SparkProtos.HeapMetadata)((SparkProtos.HeapMetadata.Builder)SparkProtos.HeapMetadata.newBuilder(this.metadata_).mergeFrom(value)).buildPartial();
         } else {
            this.metadata_ = value;
         }

      }

      private void clearMetadata() {
         this.metadata_ = null;
      }

      public List<SparkProtos.HeapEntry> getEntriesList() {
         return this.entries_;
      }

      public List<? extends SparkProtos.HeapEntryOrBuilder> getEntriesOrBuilderList() {
         return this.entries_;
      }

      public int getEntriesCount() {
         return this.entries_.size();
      }

      public SparkProtos.HeapEntry getEntries(int index) {
         return (SparkProtos.HeapEntry)this.entries_.get(index);
      }

      public SparkProtos.HeapEntryOrBuilder getEntriesOrBuilder(int index) {
         return (SparkProtos.HeapEntryOrBuilder)this.entries_.get(index);
      }

      private void ensureEntriesIsMutable() {
         Internal.ProtobufList<SparkProtos.HeapEntry> tmp = this.entries_;
         if (!tmp.isModifiable()) {
            this.entries_ = GeneratedMessageLite.mutableCopy(tmp);
         }

      }

      private void setEntries(int index, SparkProtos.HeapEntry value) {
         value.getClass();
         this.ensureEntriesIsMutable();
         this.entries_.set(index, value);
      }

      private void addEntries(SparkProtos.HeapEntry value) {
         value.getClass();
         this.ensureEntriesIsMutable();
         this.entries_.add(value);
      }

      private void addEntries(int index, SparkProtos.HeapEntry value) {
         value.getClass();
         this.ensureEntriesIsMutable();
         this.entries_.add(index, value);
      }

      private void addAllEntries(Iterable<? extends SparkProtos.HeapEntry> values) {
         this.ensureEntriesIsMutable();
         AbstractMessageLite.addAll(values, this.entries_);
      }

      private void clearEntries() {
         this.entries_ = emptyProtobufList();
      }

      private void removeEntries(int index) {
         this.ensureEntriesIsMutable();
         this.entries_.remove(index);
      }

      public static SparkProtos.HeapData parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.HeapData parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.HeapData parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.HeapData parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.HeapData parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.HeapData parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.HeapData parseFrom(InputStream input) throws IOException {
         return (SparkProtos.HeapData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.HeapData parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.HeapData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.HeapData parseDelimitedFrom(InputStream input) throws IOException {
         return (SparkProtos.HeapData)parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.HeapData parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.HeapData)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.HeapData parseFrom(CodedInputStream input) throws IOException {
         return (SparkProtos.HeapData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.HeapData parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.HeapData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.HeapData.Builder newBuilder() {
         return (SparkProtos.HeapData.Builder)DEFAULT_INSTANCE.createBuilder();
      }

      public static SparkProtos.HeapData.Builder newBuilder(SparkProtos.HeapData prototype) {
         return (SparkProtos.HeapData.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
      }

      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch(method) {
         case NEW_MUTABLE_INSTANCE:
            return new SparkProtos.HeapData();
         case NEW_BUILDER:
            return new SparkProtos.HeapData.Builder();
         case BUILD_MESSAGE_INFO:
            Object[] objects = new Object[]{"metadata_", "entries_", SparkProtos.HeapEntry.class};
            String info = "\u0000\u0002\u0000\u0000\u0001\u0002\u0002\u0000\u0001\u0000\u0001\t\u0002\u001b";
            return newMessageInfo(DEFAULT_INSTANCE, info, objects);
         case GET_DEFAULT_INSTANCE:
            return DEFAULT_INSTANCE;
         case GET_PARSER:
            Parser<SparkProtos.HeapData> parser = PARSER;
            if (parser == null) {
               synchronized(SparkProtos.HeapData.class) {
                  parser = PARSER;
                  if (parser == null) {
                     parser = new GeneratedMessageLite.DefaultInstanceBasedParser<SparkProtos.HeapData>(DEFAULT_INSTANCE);
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

      public static SparkProtos.HeapData getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<SparkProtos.HeapData> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         SparkProtos.HeapData defaultInstance = new SparkProtos.HeapData();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(SparkProtos.HeapData.class, defaultInstance);
      }

      public static final class Builder extends GeneratedMessageLite.Builder<SparkProtos.HeapData, SparkProtos.HeapData.Builder> implements SparkProtos.HeapDataOrBuilder {
         private Builder() {
            super(SparkProtos.HeapData.DEFAULT_INSTANCE);
         }

         public boolean hasMetadata() {
            return ((SparkProtos.HeapData)this.instance).hasMetadata();
         }

         public SparkProtos.HeapMetadata getMetadata() {
            return ((SparkProtos.HeapData)this.instance).getMetadata();
         }

         public SparkProtos.HeapData.Builder setMetadata(SparkProtos.HeapMetadata value) {
            this.copyOnWrite();
            ((SparkProtos.HeapData)this.instance).setMetadata(value);
            return this;
         }

         public SparkProtos.HeapData.Builder setMetadata(SparkProtos.HeapMetadata.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.HeapData)this.instance).setMetadata((SparkProtos.HeapMetadata)builderForValue.build());
            return this;
         }

         public SparkProtos.HeapData.Builder mergeMetadata(SparkProtos.HeapMetadata value) {
            this.copyOnWrite();
            ((SparkProtos.HeapData)this.instance).mergeMetadata(value);
            return this;
         }

         public SparkProtos.HeapData.Builder clearMetadata() {
            this.copyOnWrite();
            ((SparkProtos.HeapData)this.instance).clearMetadata();
            return this;
         }

         public List<SparkProtos.HeapEntry> getEntriesList() {
            return Collections.unmodifiableList(((SparkProtos.HeapData)this.instance).getEntriesList());
         }

         public int getEntriesCount() {
            return ((SparkProtos.HeapData)this.instance).getEntriesCount();
         }

         public SparkProtos.HeapEntry getEntries(int index) {
            return ((SparkProtos.HeapData)this.instance).getEntries(index);
         }

         public SparkProtos.HeapData.Builder setEntries(int index, SparkProtos.HeapEntry value) {
            this.copyOnWrite();
            ((SparkProtos.HeapData)this.instance).setEntries(index, value);
            return this;
         }

         public SparkProtos.HeapData.Builder setEntries(int index, SparkProtos.HeapEntry.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.HeapData)this.instance).setEntries(index, (SparkProtos.HeapEntry)builderForValue.build());
            return this;
         }

         public SparkProtos.HeapData.Builder addEntries(SparkProtos.HeapEntry value) {
            this.copyOnWrite();
            ((SparkProtos.HeapData)this.instance).addEntries(value);
            return this;
         }

         public SparkProtos.HeapData.Builder addEntries(int index, SparkProtos.HeapEntry value) {
            this.copyOnWrite();
            ((SparkProtos.HeapData)this.instance).addEntries(index, value);
            return this;
         }

         public SparkProtos.HeapData.Builder addEntries(SparkProtos.HeapEntry.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.HeapData)this.instance).addEntries((SparkProtos.HeapEntry)builderForValue.build());
            return this;
         }

         public SparkProtos.HeapData.Builder addEntries(int index, SparkProtos.HeapEntry.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.HeapData)this.instance).addEntries(index, (SparkProtos.HeapEntry)builderForValue.build());
            return this;
         }

         public SparkProtos.HeapData.Builder addAllEntries(Iterable<? extends SparkProtos.HeapEntry> values) {
            this.copyOnWrite();
            ((SparkProtos.HeapData)this.instance).addAllEntries(values);
            return this;
         }

         public SparkProtos.HeapData.Builder clearEntries() {
            this.copyOnWrite();
            ((SparkProtos.HeapData)this.instance).clearEntries();
            return this;
         }

         public SparkProtos.HeapData.Builder removeEntries(int index) {
            this.copyOnWrite();
            ((SparkProtos.HeapData)this.instance).removeEntries(index);
            return this;
         }
      }
   }

   public interface HeapDataOrBuilder extends MessageLiteOrBuilder {
      boolean hasMetadata();

      SparkProtos.HeapMetadata getMetadata();

      List<SparkProtos.HeapEntry> getEntriesList();

      SparkProtos.HeapEntry getEntries(int var1);

      int getEntriesCount();
   }

   public static final class HeapEntry extends GeneratedMessageLite<SparkProtos.HeapEntry, SparkProtos.HeapEntry.Builder> implements SparkProtos.HeapEntryOrBuilder {
      public static final int ORDER_FIELD_NUMBER = 1;
      private int order_;
      public static final int INSTANCES_FIELD_NUMBER = 2;
      private int instances_;
      public static final int SIZE_FIELD_NUMBER = 3;
      private long size_;
      public static final int TYPE_FIELD_NUMBER = 4;
      private String type_ = "";
      private static final SparkProtos.HeapEntry DEFAULT_INSTANCE;
      private static volatile Parser<SparkProtos.HeapEntry> PARSER;

      private HeapEntry() {
      }

      public int getOrder() {
         return this.order_;
      }

      private void setOrder(int value) {
         this.order_ = value;
      }

      private void clearOrder() {
         this.order_ = 0;
      }

      public int getInstances() {
         return this.instances_;
      }

      private void setInstances(int value) {
         this.instances_ = value;
      }

      private void clearInstances() {
         this.instances_ = 0;
      }

      public long getSize() {
         return this.size_;
      }

      private void setSize(long value) {
         this.size_ = value;
      }

      private void clearSize() {
         this.size_ = 0L;
      }

      public String getType() {
         return this.type_;
      }

      public ByteString getTypeBytes() {
         return ByteString.copyFromUtf8(this.type_);
      }

      private void setType(String value) {
         value.getClass();
         this.type_ = value;
      }

      private void clearType() {
         this.type_ = getDefaultInstance().getType();
      }

      private void setTypeBytes(ByteString value) {
         checkByteStringIsUtf8(value);
         this.type_ = value.toStringUtf8();
      }

      public static SparkProtos.HeapEntry parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapEntry)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.HeapEntry parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapEntry)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.HeapEntry parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapEntry)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.HeapEntry parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapEntry)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.HeapEntry parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapEntry)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.HeapEntry parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapEntry)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.HeapEntry parseFrom(InputStream input) throws IOException {
         return (SparkProtos.HeapEntry)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.HeapEntry parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.HeapEntry)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.HeapEntry parseDelimitedFrom(InputStream input) throws IOException {
         return (SparkProtos.HeapEntry)parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.HeapEntry parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.HeapEntry)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.HeapEntry parseFrom(CodedInputStream input) throws IOException {
         return (SparkProtos.HeapEntry)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.HeapEntry parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.HeapEntry)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.HeapEntry.Builder newBuilder() {
         return (SparkProtos.HeapEntry.Builder)DEFAULT_INSTANCE.createBuilder();
      }

      public static SparkProtos.HeapEntry.Builder newBuilder(SparkProtos.HeapEntry prototype) {
         return (SparkProtos.HeapEntry.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
      }

      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch(method) {
         case NEW_MUTABLE_INSTANCE:
            return new SparkProtos.HeapEntry();
         case NEW_BUILDER:
            return new SparkProtos.HeapEntry.Builder();
         case BUILD_MESSAGE_INFO:
            Object[] objects = new Object[]{"order_", "instances_", "size_", "type_"};
            String info = "\u0000\u0004\u0000\u0000\u0001\u0004\u0004\u0000\u0000\u0000\u0001\u0004\u0002\u0004\u0003\u0002\u0004Ȉ";
            return newMessageInfo(DEFAULT_INSTANCE, info, objects);
         case GET_DEFAULT_INSTANCE:
            return DEFAULT_INSTANCE;
         case GET_PARSER:
            Parser<SparkProtos.HeapEntry> parser = PARSER;
            if (parser == null) {
               synchronized(SparkProtos.HeapEntry.class) {
                  parser = PARSER;
                  if (parser == null) {
                     parser = new GeneratedMessageLite.DefaultInstanceBasedParser<SparkProtos.HeapEntry>(DEFAULT_INSTANCE);
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

      public static SparkProtos.HeapEntry getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<SparkProtos.HeapEntry> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         SparkProtos.HeapEntry defaultInstance = new SparkProtos.HeapEntry();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(SparkProtos.HeapEntry.class, defaultInstance);
      }

      public static final class Builder extends GeneratedMessageLite.Builder<SparkProtos.HeapEntry, SparkProtos.HeapEntry.Builder> implements SparkProtos.HeapEntryOrBuilder {
         private Builder() {
            super(SparkProtos.HeapEntry.DEFAULT_INSTANCE);
         }

         public int getOrder() {
            return ((SparkProtos.HeapEntry)this.instance).getOrder();
         }

         public SparkProtos.HeapEntry.Builder setOrder(int value) {
            this.copyOnWrite();
            ((SparkProtos.HeapEntry)this.instance).setOrder(value);
            return this;
         }

         public SparkProtos.HeapEntry.Builder clearOrder() {
            this.copyOnWrite();
            ((SparkProtos.HeapEntry)this.instance).clearOrder();
            return this;
         }

         public int getInstances() {
            return ((SparkProtos.HeapEntry)this.instance).getInstances();
         }

         public SparkProtos.HeapEntry.Builder setInstances(int value) {
            this.copyOnWrite();
            ((SparkProtos.HeapEntry)this.instance).setInstances(value);
            return this;
         }

         public SparkProtos.HeapEntry.Builder clearInstances() {
            this.copyOnWrite();
            ((SparkProtos.HeapEntry)this.instance).clearInstances();
            return this;
         }

         public long getSize() {
            return ((SparkProtos.HeapEntry)this.instance).getSize();
         }

         public SparkProtos.HeapEntry.Builder setSize(long value) {
            this.copyOnWrite();
            ((SparkProtos.HeapEntry)this.instance).setSize(value);
            return this;
         }

         public SparkProtos.HeapEntry.Builder clearSize() {
            this.copyOnWrite();
            ((SparkProtos.HeapEntry)this.instance).clearSize();
            return this;
         }

         public String getType() {
            return ((SparkProtos.HeapEntry)this.instance).getType();
         }

         public ByteString getTypeBytes() {
            return ((SparkProtos.HeapEntry)this.instance).getTypeBytes();
         }

         public SparkProtos.HeapEntry.Builder setType(String value) {
            this.copyOnWrite();
            ((SparkProtos.HeapEntry)this.instance).setType(value);
            return this;
         }

         public SparkProtos.HeapEntry.Builder clearType() {
            this.copyOnWrite();
            ((SparkProtos.HeapEntry)this.instance).clearType();
            return this;
         }

         public SparkProtos.HeapEntry.Builder setTypeBytes(ByteString value) {
            this.copyOnWrite();
            ((SparkProtos.HeapEntry)this.instance).setTypeBytes(value);
            return this;
         }
      }
   }

   public interface HeapEntryOrBuilder extends MessageLiteOrBuilder {
      int getOrder();

      int getInstances();

      long getSize();

      String getType();

      ByteString getTypeBytes();
   }

   public static final class HeapMetadata extends GeneratedMessageLite<SparkProtos.HeapMetadata, SparkProtos.HeapMetadata.Builder> implements SparkProtos.HeapMetadataOrBuilder {
      public static final int CREATOR_FIELD_NUMBER = 1;
      private SparkProtos.CommandSenderMetadata creator_;
      public static final int PLATFORM_METADATA_FIELD_NUMBER = 2;
      private SparkProtos.PlatformMetadata platformMetadata_;
      private static final SparkProtos.HeapMetadata DEFAULT_INSTANCE;
      private static volatile Parser<SparkProtos.HeapMetadata> PARSER;

      private HeapMetadata() {
      }

      public boolean hasCreator() {
         return this.creator_ != null;
      }

      public SparkProtos.CommandSenderMetadata getCreator() {
         return this.creator_ == null ? SparkProtos.CommandSenderMetadata.DEFAULT_INSTANCE : this.creator_;
      }

      private void setCreator(SparkProtos.CommandSenderMetadata value) {
         value.getClass();
         this.creator_ = value;
      }

      private void mergeCreator(SparkProtos.CommandSenderMetadata value) {
         value.getClass();
         if (this.creator_ != null && this.creator_ != SparkProtos.CommandSenderMetadata.DEFAULT_INSTANCE) {
            this.creator_ = (SparkProtos.CommandSenderMetadata)((SparkProtos.CommandSenderMetadata.Builder)SparkProtos.CommandSenderMetadata.newBuilder(this.creator_).mergeFrom(value)).buildPartial();
         } else {
            this.creator_ = value;
         }

      }

      private void clearCreator() {
         this.creator_ = null;
      }

      public boolean hasPlatformMetadata() {
         return this.platformMetadata_ != null;
      }

      public SparkProtos.PlatformMetadata getPlatformMetadata() {
         return this.platformMetadata_ == null ? SparkProtos.PlatformMetadata.DEFAULT_INSTANCE : this.platformMetadata_;
      }

      private void setPlatformMetadata(SparkProtos.PlatformMetadata value) {
         value.getClass();
         this.platformMetadata_ = value;
      }

      private void mergePlatformMetadata(SparkProtos.PlatformMetadata value) {
         value.getClass();
         if (this.platformMetadata_ != null && this.platformMetadata_ != SparkProtos.PlatformMetadata.DEFAULT_INSTANCE) {
            this.platformMetadata_ = (SparkProtos.PlatformMetadata)((SparkProtos.PlatformMetadata.Builder)SparkProtos.PlatformMetadata.newBuilder(this.platformMetadata_).mergeFrom(value)).buildPartial();
         } else {
            this.platformMetadata_ = value;
         }

      }

      private void clearPlatformMetadata() {
         this.platformMetadata_ = null;
      }

      public static SparkProtos.HeapMetadata parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.HeapMetadata parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.HeapMetadata parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.HeapMetadata parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.HeapMetadata parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.HeapMetadata parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.HeapMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.HeapMetadata parseFrom(InputStream input) throws IOException {
         return (SparkProtos.HeapMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.HeapMetadata parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.HeapMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.HeapMetadata parseDelimitedFrom(InputStream input) throws IOException {
         return (SparkProtos.HeapMetadata)parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.HeapMetadata parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.HeapMetadata)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.HeapMetadata parseFrom(CodedInputStream input) throws IOException {
         return (SparkProtos.HeapMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.HeapMetadata parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.HeapMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.HeapMetadata.Builder newBuilder() {
         return (SparkProtos.HeapMetadata.Builder)DEFAULT_INSTANCE.createBuilder();
      }

      public static SparkProtos.HeapMetadata.Builder newBuilder(SparkProtos.HeapMetadata prototype) {
         return (SparkProtos.HeapMetadata.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
      }

      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch(method) {
         case NEW_MUTABLE_INSTANCE:
            return new SparkProtos.HeapMetadata();
         case NEW_BUILDER:
            return new SparkProtos.HeapMetadata.Builder();
         case BUILD_MESSAGE_INFO:
            Object[] objects = new Object[]{"creator_", "platformMetadata_"};
            String info = "\u0000\u0002\u0000\u0000\u0001\u0002\u0002\u0000\u0000\u0000\u0001\t\u0002\t";
            return newMessageInfo(DEFAULT_INSTANCE, info, objects);
         case GET_DEFAULT_INSTANCE:
            return DEFAULT_INSTANCE;
         case GET_PARSER:
            Parser<SparkProtos.HeapMetadata> parser = PARSER;
            if (parser == null) {
               synchronized(SparkProtos.HeapMetadata.class) {
                  parser = PARSER;
                  if (parser == null) {
                     parser = new GeneratedMessageLite.DefaultInstanceBasedParser<SparkProtos.HeapMetadata>(DEFAULT_INSTANCE);
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

      public static SparkProtos.HeapMetadata getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<SparkProtos.HeapMetadata> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         SparkProtos.HeapMetadata defaultInstance = new SparkProtos.HeapMetadata();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(SparkProtos.HeapMetadata.class, defaultInstance);
      }

      public static final class Builder extends GeneratedMessageLite.Builder<SparkProtos.HeapMetadata, SparkProtos.HeapMetadata.Builder> implements SparkProtos.HeapMetadataOrBuilder {
         private Builder() {
            super(SparkProtos.HeapMetadata.DEFAULT_INSTANCE);
         }

         public boolean hasCreator() {
            return ((SparkProtos.HeapMetadata)this.instance).hasCreator();
         }

         public SparkProtos.CommandSenderMetadata getCreator() {
            return ((SparkProtos.HeapMetadata)this.instance).getCreator();
         }

         public SparkProtos.HeapMetadata.Builder setCreator(SparkProtos.CommandSenderMetadata value) {
            this.copyOnWrite();
            ((SparkProtos.HeapMetadata)this.instance).setCreator(value);
            return this;
         }

         public SparkProtos.HeapMetadata.Builder setCreator(SparkProtos.CommandSenderMetadata.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.HeapMetadata)this.instance).setCreator((SparkProtos.CommandSenderMetadata)builderForValue.build());
            return this;
         }

         public SparkProtos.HeapMetadata.Builder mergeCreator(SparkProtos.CommandSenderMetadata value) {
            this.copyOnWrite();
            ((SparkProtos.HeapMetadata)this.instance).mergeCreator(value);
            return this;
         }

         public SparkProtos.HeapMetadata.Builder clearCreator() {
            this.copyOnWrite();
            ((SparkProtos.HeapMetadata)this.instance).clearCreator();
            return this;
         }

         public boolean hasPlatformMetadata() {
            return ((SparkProtos.HeapMetadata)this.instance).hasPlatformMetadata();
         }

         public SparkProtos.PlatformMetadata getPlatformMetadata() {
            return ((SparkProtos.HeapMetadata)this.instance).getPlatformMetadata();
         }

         public SparkProtos.HeapMetadata.Builder setPlatformMetadata(SparkProtos.PlatformMetadata value) {
            this.copyOnWrite();
            ((SparkProtos.HeapMetadata)this.instance).setPlatformMetadata(value);
            return this;
         }

         public SparkProtos.HeapMetadata.Builder setPlatformMetadata(SparkProtos.PlatformMetadata.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.HeapMetadata)this.instance).setPlatformMetadata((SparkProtos.PlatformMetadata)builderForValue.build());
            return this;
         }

         public SparkProtos.HeapMetadata.Builder mergePlatformMetadata(SparkProtos.PlatformMetadata value) {
            this.copyOnWrite();
            ((SparkProtos.HeapMetadata)this.instance).mergePlatformMetadata(value);
            return this;
         }

         public SparkProtos.HeapMetadata.Builder clearPlatformMetadata() {
            this.copyOnWrite();
            ((SparkProtos.HeapMetadata)this.instance).clearPlatformMetadata();
            return this;
         }
      }
   }

   public interface HeapMetadataOrBuilder extends MessageLiteOrBuilder {
      boolean hasCreator();

      SparkProtos.CommandSenderMetadata getCreator();

      boolean hasPlatformMetadata();

      SparkProtos.PlatformMetadata getPlatformMetadata();
   }

   public static final class MemoryUsage extends GeneratedMessageLite<SparkProtos.MemoryUsage, SparkProtos.MemoryUsage.Builder> implements SparkProtos.MemoryUsageOrBuilder {
      public static final int USED_FIELD_NUMBER = 1;
      private long used_;
      public static final int COMMITTED_FIELD_NUMBER = 2;
      private long committed_;
      public static final int MAX_FIELD_NUMBER = 3;
      private long max_;
      private static final SparkProtos.MemoryUsage DEFAULT_INSTANCE;
      private static volatile Parser<SparkProtos.MemoryUsage> PARSER;

      private MemoryUsage() {
      }

      public long getUsed() {
         return this.used_;
      }

      private void setUsed(long value) {
         this.used_ = value;
      }

      private void clearUsed() {
         this.used_ = 0L;
      }

      public long getCommitted() {
         return this.committed_;
      }

      private void setCommitted(long value) {
         this.committed_ = value;
      }

      private void clearCommitted() {
         this.committed_ = 0L;
      }

      public long getMax() {
         return this.max_;
      }

      private void setMax(long value) {
         this.max_ = value;
      }

      private void clearMax() {
         this.max_ = 0L;
      }

      public static SparkProtos.MemoryUsage parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (SparkProtos.MemoryUsage)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.MemoryUsage parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.MemoryUsage)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.MemoryUsage parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (SparkProtos.MemoryUsage)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.MemoryUsage parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.MemoryUsage)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.MemoryUsage parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (SparkProtos.MemoryUsage)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.MemoryUsage parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.MemoryUsage)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.MemoryUsage parseFrom(InputStream input) throws IOException {
         return (SparkProtos.MemoryUsage)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.MemoryUsage parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.MemoryUsage)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.MemoryUsage parseDelimitedFrom(InputStream input) throws IOException {
         return (SparkProtos.MemoryUsage)parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.MemoryUsage parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.MemoryUsage)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.MemoryUsage parseFrom(CodedInputStream input) throws IOException {
         return (SparkProtos.MemoryUsage)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.MemoryUsage parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.MemoryUsage)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.MemoryUsage.Builder newBuilder() {
         return (SparkProtos.MemoryUsage.Builder)DEFAULT_INSTANCE.createBuilder();
      }

      public static SparkProtos.MemoryUsage.Builder newBuilder(SparkProtos.MemoryUsage prototype) {
         return (SparkProtos.MemoryUsage.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
      }

      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch(method) {
         case NEW_MUTABLE_INSTANCE:
            return new SparkProtos.MemoryUsage();
         case NEW_BUILDER:
            return new SparkProtos.MemoryUsage.Builder();
         case BUILD_MESSAGE_INFO:
            Object[] objects = new Object[]{"used_", "committed_", "max_"};
            String info = "\u0000\u0003\u0000\u0000\u0001\u0003\u0003\u0000\u0000\u0000\u0001\u0002\u0002\u0002\u0003\u0002";
            return newMessageInfo(DEFAULT_INSTANCE, info, objects);
         case GET_DEFAULT_INSTANCE:
            return DEFAULT_INSTANCE;
         case GET_PARSER:
            Parser<SparkProtos.MemoryUsage> parser = PARSER;
            if (parser == null) {
               synchronized(SparkProtos.MemoryUsage.class) {
                  parser = PARSER;
                  if (parser == null) {
                     parser = new GeneratedMessageLite.DefaultInstanceBasedParser<SparkProtos.MemoryUsage>(DEFAULT_INSTANCE);
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

      public static SparkProtos.MemoryUsage getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<SparkProtos.MemoryUsage> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         SparkProtos.MemoryUsage defaultInstance = new SparkProtos.MemoryUsage();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(SparkProtos.MemoryUsage.class, defaultInstance);
      }

      public static final class Builder extends GeneratedMessageLite.Builder<SparkProtos.MemoryUsage, SparkProtos.MemoryUsage.Builder> implements SparkProtos.MemoryUsageOrBuilder {
         private Builder() {
            super(SparkProtos.MemoryUsage.DEFAULT_INSTANCE);
         }

         public long getUsed() {
            return ((SparkProtos.MemoryUsage)this.instance).getUsed();
         }

         public SparkProtos.MemoryUsage.Builder setUsed(long value) {
            this.copyOnWrite();
            ((SparkProtos.MemoryUsage)this.instance).setUsed(value);
            return this;
         }

         public SparkProtos.MemoryUsage.Builder clearUsed() {
            this.copyOnWrite();
            ((SparkProtos.MemoryUsage)this.instance).clearUsed();
            return this;
         }

         public long getCommitted() {
            return ((SparkProtos.MemoryUsage)this.instance).getCommitted();
         }

         public SparkProtos.MemoryUsage.Builder setCommitted(long value) {
            this.copyOnWrite();
            ((SparkProtos.MemoryUsage)this.instance).setCommitted(value);
            return this;
         }

         public SparkProtos.MemoryUsage.Builder clearCommitted() {
            this.copyOnWrite();
            ((SparkProtos.MemoryUsage)this.instance).clearCommitted();
            return this;
         }

         public long getMax() {
            return ((SparkProtos.MemoryUsage)this.instance).getMax();
         }

         public SparkProtos.MemoryUsage.Builder setMax(long value) {
            this.copyOnWrite();
            ((SparkProtos.MemoryUsage)this.instance).setMax(value);
            return this;
         }

         public SparkProtos.MemoryUsage.Builder clearMax() {
            this.copyOnWrite();
            ((SparkProtos.MemoryUsage)this.instance).clearMax();
            return this;
         }
      }
   }

   public interface MemoryUsageOrBuilder extends MessageLiteOrBuilder {
      long getUsed();

      long getCommitted();

      long getMax();
   }

   public static final class PlatformMetadata extends GeneratedMessageLite<SparkProtos.PlatformMetadata, SparkProtos.PlatformMetadata.Builder> implements SparkProtos.PlatformMetadataOrBuilder {
      public static final int TYPE_FIELD_NUMBER = 1;
      private int type_;
      public static final int NAME_FIELD_NUMBER = 2;
      private String name_ = "";
      public static final int VERSION_FIELD_NUMBER = 3;
      private String version_ = "";
      public static final int MINECRAFT_VERSION_FIELD_NUMBER = 4;
      private String minecraftVersion_ = "";
      public static final int N_CPUS_FIELD_NUMBER = 5;
      private int nCpus_;
      public static final int HEAPUSAGE_FIELD_NUMBER = 6;
      private SparkProtos.MemoryUsage heapUsage_;
      public static final int SPARK_VERSION_FIELD_NUMBER = 7;
      private int sparkVersion_;
      private static final SparkProtos.PlatformMetadata DEFAULT_INSTANCE;
      private static volatile Parser<SparkProtos.PlatformMetadata> PARSER;

      private PlatformMetadata() {
      }

      public int getTypeValue() {
         return this.type_;
      }

      public SparkProtos.PlatformMetadata.Type getType() {
         SparkProtos.PlatformMetadata.Type result = SparkProtos.PlatformMetadata.Type.forNumber(this.type_);
         return result == null ? SparkProtos.PlatformMetadata.Type.UNRECOGNIZED : result;
      }

      private void setTypeValue(int value) {
         this.type_ = value;
      }

      private void setType(SparkProtos.PlatformMetadata.Type value) {
         this.type_ = value.getNumber();
      }

      private void clearType() {
         this.type_ = 0;
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

      public String getVersion() {
         return this.version_;
      }

      public ByteString getVersionBytes() {
         return ByteString.copyFromUtf8(this.version_);
      }

      private void setVersion(String value) {
         value.getClass();
         this.version_ = value;
      }

      private void clearVersion() {
         this.version_ = getDefaultInstance().getVersion();
      }

      private void setVersionBytes(ByteString value) {
         checkByteStringIsUtf8(value);
         this.version_ = value.toStringUtf8();
      }

      public String getMinecraftVersion() {
         return this.minecraftVersion_;
      }

      public ByteString getMinecraftVersionBytes() {
         return ByteString.copyFromUtf8(this.minecraftVersion_);
      }

      private void setMinecraftVersion(String value) {
         value.getClass();
         this.minecraftVersion_ = value;
      }

      private void clearMinecraftVersion() {
         this.minecraftVersion_ = getDefaultInstance().getMinecraftVersion();
      }

      private void setMinecraftVersionBytes(ByteString value) {
         checkByteStringIsUtf8(value);
         this.minecraftVersion_ = value.toStringUtf8();
      }

      public int getNCpus() {
         return this.nCpus_;
      }

      private void setNCpus(int value) {
         this.nCpus_ = value;
      }

      private void clearNCpus() {
         this.nCpus_ = 0;
      }

      public boolean hasHeapUsage() {
         return this.heapUsage_ != null;
      }

      public SparkProtos.MemoryUsage getHeapUsage() {
         return this.heapUsage_ == null ? SparkProtos.MemoryUsage.DEFAULT_INSTANCE : this.heapUsage_;
      }

      private void setHeapUsage(SparkProtos.MemoryUsage value) {
         value.getClass();
         this.heapUsage_ = value;
      }

      private void mergeHeapUsage(SparkProtos.MemoryUsage value) {
         value.getClass();
         if (this.heapUsage_ != null && this.heapUsage_ != SparkProtos.MemoryUsage.DEFAULT_INSTANCE) {
            this.heapUsage_ = (SparkProtos.MemoryUsage)((SparkProtos.MemoryUsage.Builder)SparkProtos.MemoryUsage.newBuilder(this.heapUsage_).mergeFrom(value)).buildPartial();
         } else {
            this.heapUsage_ = value;
         }

      }

      private void clearHeapUsage() {
         this.heapUsage_ = null;
      }

      public int getSparkVersion() {
         return this.sparkVersion_;
      }

      private void setSparkVersion(int value) {
         this.sparkVersion_ = value;
      }

      private void clearSparkVersion() {
         this.sparkVersion_ = 0;
      }

      public static SparkProtos.PlatformMetadata parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (SparkProtos.PlatformMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.PlatformMetadata parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.PlatformMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.PlatformMetadata parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (SparkProtos.PlatformMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.PlatformMetadata parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.PlatformMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.PlatformMetadata parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (SparkProtos.PlatformMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.PlatformMetadata parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.PlatformMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.PlatformMetadata parseFrom(InputStream input) throws IOException {
         return (SparkProtos.PlatformMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.PlatformMetadata parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.PlatformMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.PlatformMetadata parseDelimitedFrom(InputStream input) throws IOException {
         return (SparkProtos.PlatformMetadata)parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.PlatformMetadata parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.PlatformMetadata)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.PlatformMetadata parseFrom(CodedInputStream input) throws IOException {
         return (SparkProtos.PlatformMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.PlatformMetadata parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.PlatformMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.PlatformMetadata.Builder newBuilder() {
         return (SparkProtos.PlatformMetadata.Builder)DEFAULT_INSTANCE.createBuilder();
      }

      public static SparkProtos.PlatformMetadata.Builder newBuilder(SparkProtos.PlatformMetadata prototype) {
         return (SparkProtos.PlatformMetadata.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
      }

      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch(method) {
         case NEW_MUTABLE_INSTANCE:
            return new SparkProtos.PlatformMetadata();
         case NEW_BUILDER:
            return new SparkProtos.PlatformMetadata.Builder();
         case BUILD_MESSAGE_INFO:
            Object[] objects = new Object[]{"type_", "name_", "version_", "minecraftVersion_", "nCpus_", "heapUsage_", "sparkVersion_"};
            String info = "\u0000\u0007\u0000\u0000\u0001\u0007\u0007\u0000\u0000\u0000\u0001\f\u0002Ȉ\u0003Ȉ\u0004Ȉ\u0005\u0004\u0006\t\u0007\u0004";
            return newMessageInfo(DEFAULT_INSTANCE, info, objects);
         case GET_DEFAULT_INSTANCE:
            return DEFAULT_INSTANCE;
         case GET_PARSER:
            Parser<SparkProtos.PlatformMetadata> parser = PARSER;
            if (parser == null) {
               synchronized(SparkProtos.PlatformMetadata.class) {
                  parser = PARSER;
                  if (parser == null) {
                     parser = new GeneratedMessageLite.DefaultInstanceBasedParser<SparkProtos.PlatformMetadata>(DEFAULT_INSTANCE);
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

      public static SparkProtos.PlatformMetadata getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<SparkProtos.PlatformMetadata> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         SparkProtos.PlatformMetadata defaultInstance = new SparkProtos.PlatformMetadata();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(SparkProtos.PlatformMetadata.class, defaultInstance);
      }

      public static final class Builder extends GeneratedMessageLite.Builder<SparkProtos.PlatformMetadata, SparkProtos.PlatformMetadata.Builder> implements SparkProtos.PlatformMetadataOrBuilder {
         private Builder() {
            super(SparkProtos.PlatformMetadata.DEFAULT_INSTANCE);
         }

         public int getTypeValue() {
            return ((SparkProtos.PlatformMetadata)this.instance).getTypeValue();
         }

         public SparkProtos.PlatformMetadata.Builder setTypeValue(int value) {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).setTypeValue(value);
            return this;
         }

         public SparkProtos.PlatformMetadata.Type getType() {
            return ((SparkProtos.PlatformMetadata)this.instance).getType();
         }

         public SparkProtos.PlatformMetadata.Builder setType(SparkProtos.PlatformMetadata.Type value) {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).setType(value);
            return this;
         }

         public SparkProtos.PlatformMetadata.Builder clearType() {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).clearType();
            return this;
         }

         public String getName() {
            return ((SparkProtos.PlatformMetadata)this.instance).getName();
         }

         public ByteString getNameBytes() {
            return ((SparkProtos.PlatformMetadata)this.instance).getNameBytes();
         }

         public SparkProtos.PlatformMetadata.Builder setName(String value) {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).setName(value);
            return this;
         }

         public SparkProtos.PlatformMetadata.Builder clearName() {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).clearName();
            return this;
         }

         public SparkProtos.PlatformMetadata.Builder setNameBytes(ByteString value) {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).setNameBytes(value);
            return this;
         }

         public String getVersion() {
            return ((SparkProtos.PlatformMetadata)this.instance).getVersion();
         }

         public ByteString getVersionBytes() {
            return ((SparkProtos.PlatformMetadata)this.instance).getVersionBytes();
         }

         public SparkProtos.PlatformMetadata.Builder setVersion(String value) {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).setVersion(value);
            return this;
         }

         public SparkProtos.PlatformMetadata.Builder clearVersion() {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).clearVersion();
            return this;
         }

         public SparkProtos.PlatformMetadata.Builder setVersionBytes(ByteString value) {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).setVersionBytes(value);
            return this;
         }

         public String getMinecraftVersion() {
            return ((SparkProtos.PlatformMetadata)this.instance).getMinecraftVersion();
         }

         public ByteString getMinecraftVersionBytes() {
            return ((SparkProtos.PlatformMetadata)this.instance).getMinecraftVersionBytes();
         }

         public SparkProtos.PlatformMetadata.Builder setMinecraftVersion(String value) {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).setMinecraftVersion(value);
            return this;
         }

         public SparkProtos.PlatformMetadata.Builder clearMinecraftVersion() {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).clearMinecraftVersion();
            return this;
         }

         public SparkProtos.PlatformMetadata.Builder setMinecraftVersionBytes(ByteString value) {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).setMinecraftVersionBytes(value);
            return this;
         }

         public int getNCpus() {
            return ((SparkProtos.PlatformMetadata)this.instance).getNCpus();
         }

         public SparkProtos.PlatformMetadata.Builder setNCpus(int value) {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).setNCpus(value);
            return this;
         }

         public SparkProtos.PlatformMetadata.Builder clearNCpus() {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).clearNCpus();
            return this;
         }

         public boolean hasHeapUsage() {
            return ((SparkProtos.PlatformMetadata)this.instance).hasHeapUsage();
         }

         public SparkProtos.MemoryUsage getHeapUsage() {
            return ((SparkProtos.PlatformMetadata)this.instance).getHeapUsage();
         }

         public SparkProtos.PlatformMetadata.Builder setHeapUsage(SparkProtos.MemoryUsage value) {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).setHeapUsage(value);
            return this;
         }

         public SparkProtos.PlatformMetadata.Builder setHeapUsage(SparkProtos.MemoryUsage.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).setHeapUsage((SparkProtos.MemoryUsage)builderForValue.build());
            return this;
         }

         public SparkProtos.PlatformMetadata.Builder mergeHeapUsage(SparkProtos.MemoryUsage value) {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).mergeHeapUsage(value);
            return this;
         }

         public SparkProtos.PlatformMetadata.Builder clearHeapUsage() {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).clearHeapUsage();
            return this;
         }

         public int getSparkVersion() {
            return ((SparkProtos.PlatformMetadata)this.instance).getSparkVersion();
         }

         public SparkProtos.PlatformMetadata.Builder setSparkVersion(int value) {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).setSparkVersion(value);
            return this;
         }

         public SparkProtos.PlatformMetadata.Builder clearSparkVersion() {
            this.copyOnWrite();
            ((SparkProtos.PlatformMetadata)this.instance).clearSparkVersion();
            return this;
         }
      }

      public static enum Type implements Internal.EnumLite {
         SERVER(0),
         CLIENT(1),
         PROXY(2),
         UNRECOGNIZED(-1);

         public static final int SERVER_VALUE = 0;
         public static final int CLIENT_VALUE = 1;
         public static final int PROXY_VALUE = 2;
         private static final Internal.EnumLiteMap<SparkProtos.PlatformMetadata.Type> internalValueMap = new Internal.EnumLiteMap<SparkProtos.PlatformMetadata.Type>() {
            public SparkProtos.PlatformMetadata.Type findValueByNumber(int number) {
               return SparkProtos.PlatformMetadata.Type.forNumber(number);
            }
         };
         private final int value;

         public final int getNumber() {
            if (this == UNRECOGNIZED) {
               throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
            } else {
               return this.value;
            }
         }

         /** @deprecated */
         @Deprecated
         public static SparkProtos.PlatformMetadata.Type valueOf(int value) {
            return forNumber(value);
         }

         public static SparkProtos.PlatformMetadata.Type forNumber(int value) {
            switch(value) {
            case 0:
               return SERVER;
            case 1:
               return CLIENT;
            case 2:
               return PROXY;
            default:
               return null;
            }
         }

         public static Internal.EnumLiteMap<SparkProtos.PlatformMetadata.Type> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return SparkProtos.PlatformMetadata.Type.TypeVerifier.INSTANCE;
         }

         private Type(int value) {
            this.value = value;
         }

         private static final class TypeVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new SparkProtos.PlatformMetadata.Type.TypeVerifier();

            private TypeVerifier() {
            }

            public boolean isInRange(int number) {
               return SparkProtos.PlatformMetadata.Type.forNumber(number) != null;
            }
         }
      }
   }

   public interface PlatformMetadataOrBuilder extends MessageLiteOrBuilder {
      int getTypeValue();

      SparkProtos.PlatformMetadata.Type getType();

      String getName();

      ByteString getNameBytes();

      String getVersion();

      ByteString getVersionBytes();

      String getMinecraftVersion();

      ByteString getMinecraftVersionBytes();

      int getNCpus();

      boolean hasHeapUsage();

      SparkProtos.MemoryUsage getHeapUsage();

      int getSparkVersion();
   }

   public static final class SamplerData extends GeneratedMessageLite<SparkProtos.SamplerData, SparkProtos.SamplerData.Builder> implements SparkProtos.SamplerDataOrBuilder {
      public static final int METADATA_FIELD_NUMBER = 1;
      private SparkProtos.SamplerMetadata metadata_;
      public static final int THREADS_FIELD_NUMBER = 2;
      private Internal.ProtobufList<SparkProtos.ThreadNode> threads_ = emptyProtobufList();
      public static final int CLASS_SOURCES_FIELD_NUMBER = 3;
      private MapFieldLite<String, String> classSources_ = MapFieldLite.<String, String>emptyMapField();
      private static final SparkProtos.SamplerData DEFAULT_INSTANCE;
      private static volatile Parser<SparkProtos.SamplerData> PARSER;

      private SamplerData() {
      }

      public boolean hasMetadata() {
         return this.metadata_ != null;
      }

      public SparkProtos.SamplerMetadata getMetadata() {
         return this.metadata_ == null ? SparkProtos.SamplerMetadata.DEFAULT_INSTANCE : this.metadata_;
      }

      private void setMetadata(SparkProtos.SamplerMetadata value) {
         value.getClass();
         this.metadata_ = value;
      }

      private void mergeMetadata(SparkProtos.SamplerMetadata value) {
         value.getClass();
         if (this.metadata_ != null && this.metadata_ != SparkProtos.SamplerMetadata.DEFAULT_INSTANCE) {
            this.metadata_ = (SparkProtos.SamplerMetadata)((SparkProtos.SamplerMetadata.Builder)SparkProtos.SamplerMetadata.newBuilder(this.metadata_).mergeFrom(value)).buildPartial();
         } else {
            this.metadata_ = value;
         }

      }

      private void clearMetadata() {
         this.metadata_ = null;
      }

      public List<SparkProtos.ThreadNode> getThreadsList() {
         return this.threads_;
      }

      public List<? extends SparkProtos.ThreadNodeOrBuilder> getThreadsOrBuilderList() {
         return this.threads_;
      }

      public int getThreadsCount() {
         return this.threads_.size();
      }

      public SparkProtos.ThreadNode getThreads(int index) {
         return (SparkProtos.ThreadNode)this.threads_.get(index);
      }

      public SparkProtos.ThreadNodeOrBuilder getThreadsOrBuilder(int index) {
         return (SparkProtos.ThreadNodeOrBuilder)this.threads_.get(index);
      }

      private void ensureThreadsIsMutable() {
         Internal.ProtobufList<SparkProtos.ThreadNode> tmp = this.threads_;
         if (!tmp.isModifiable()) {
            this.threads_ = GeneratedMessageLite.mutableCopy(tmp);
         }

      }

      private void setThreads(int index, SparkProtos.ThreadNode value) {
         value.getClass();
         this.ensureThreadsIsMutable();
         this.threads_.set(index, value);
      }

      private void addThreads(SparkProtos.ThreadNode value) {
         value.getClass();
         this.ensureThreadsIsMutable();
         this.threads_.add(value);
      }

      private void addThreads(int index, SparkProtos.ThreadNode value) {
         value.getClass();
         this.ensureThreadsIsMutable();
         this.threads_.add(index, value);
      }

      private void addAllThreads(Iterable<? extends SparkProtos.ThreadNode> values) {
         this.ensureThreadsIsMutable();
         AbstractMessageLite.addAll(values, this.threads_);
      }

      private void clearThreads() {
         this.threads_ = emptyProtobufList();
      }

      private void removeThreads(int index) {
         this.ensureThreadsIsMutable();
         this.threads_.remove(index);
      }

      private MapFieldLite<String, String> internalGetClassSources() {
         return this.classSources_;
      }

      private MapFieldLite<String, String> internalGetMutableClassSources() {
         if (!this.classSources_.isMutable()) {
            this.classSources_ = this.classSources_.mutableCopy();
         }

         return this.classSources_;
      }

      public int getClassSourcesCount() {
         return this.internalGetClassSources().size();
      }

      public boolean containsClassSources(String key) {
         key.getClass();
         return this.internalGetClassSources().containsKey(key);
      }

      /** @deprecated */
      @Deprecated
      public Map<String, String> getClassSources() {
         return this.getClassSourcesMap();
      }

      public Map<String, String> getClassSourcesMap() {
         return Collections.unmodifiableMap(this.internalGetClassSources());
      }

      public String getClassSourcesOrDefault(String key, String defaultValue) {
         key.getClass();
         Map<String, String> map = this.internalGetClassSources();
         return map.containsKey(key) ? (String)map.get(key) : defaultValue;
      }

      public String getClassSourcesOrThrow(String key) {
         key.getClass();
         Map<String, String> map = this.internalGetClassSources();
         if (!map.containsKey(key)) {
            throw new IllegalArgumentException();
         } else {
            return (String)map.get(key);
         }
      }

      private Map<String, String> getMutableClassSourcesMap() {
         return this.internalGetMutableClassSources();
      }

      public static SparkProtos.SamplerData parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (SparkProtos.SamplerData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.SamplerData parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.SamplerData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.SamplerData parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (SparkProtos.SamplerData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.SamplerData parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.SamplerData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.SamplerData parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (SparkProtos.SamplerData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.SamplerData parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.SamplerData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.SamplerData parseFrom(InputStream input) throws IOException {
         return (SparkProtos.SamplerData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.SamplerData parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.SamplerData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.SamplerData parseDelimitedFrom(InputStream input) throws IOException {
         return (SparkProtos.SamplerData)parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.SamplerData parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.SamplerData)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.SamplerData parseFrom(CodedInputStream input) throws IOException {
         return (SparkProtos.SamplerData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.SamplerData parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.SamplerData)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.SamplerData.Builder newBuilder() {
         return (SparkProtos.SamplerData.Builder)DEFAULT_INSTANCE.createBuilder();
      }

      public static SparkProtos.SamplerData.Builder newBuilder(SparkProtos.SamplerData prototype) {
         return (SparkProtos.SamplerData.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
      }

      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch(method) {
         case NEW_MUTABLE_INSTANCE:
            return new SparkProtos.SamplerData();
         case NEW_BUILDER:
            return new SparkProtos.SamplerData.Builder();
         case BUILD_MESSAGE_INFO:
            Object[] objects = new Object[]{"metadata_", "threads_", SparkProtos.ThreadNode.class, "classSources_", SparkProtos.SamplerData.ClassSourcesDefaultEntryHolder.defaultEntry};
            String info = "\u0000\u0003\u0000\u0000\u0001\u0003\u0003\u0001\u0001\u0000\u0001\t\u0002\u001b\u00032";
            return newMessageInfo(DEFAULT_INSTANCE, info, objects);
         case GET_DEFAULT_INSTANCE:
            return DEFAULT_INSTANCE;
         case GET_PARSER:
            Parser<SparkProtos.SamplerData> parser = PARSER;
            if (parser == null) {
               synchronized(SparkProtos.SamplerData.class) {
                  parser = PARSER;
                  if (parser == null) {
                     parser = new GeneratedMessageLite.DefaultInstanceBasedParser<SparkProtos.SamplerData>(DEFAULT_INSTANCE);
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

      public static SparkProtos.SamplerData getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<SparkProtos.SamplerData> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         SparkProtos.SamplerData defaultInstance = new SparkProtos.SamplerData();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(SparkProtos.SamplerData.class, defaultInstance);
      }

      public static final class Builder extends GeneratedMessageLite.Builder<SparkProtos.SamplerData, SparkProtos.SamplerData.Builder> implements SparkProtos.SamplerDataOrBuilder {
         private Builder() {
            super(SparkProtos.SamplerData.DEFAULT_INSTANCE);
         }

         public boolean hasMetadata() {
            return ((SparkProtos.SamplerData)this.instance).hasMetadata();
         }

         public SparkProtos.SamplerMetadata getMetadata() {
            return ((SparkProtos.SamplerData)this.instance).getMetadata();
         }

         public SparkProtos.SamplerData.Builder setMetadata(SparkProtos.SamplerMetadata value) {
            this.copyOnWrite();
            ((SparkProtos.SamplerData)this.instance).setMetadata(value);
            return this;
         }

         public SparkProtos.SamplerData.Builder setMetadata(SparkProtos.SamplerMetadata.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.SamplerData)this.instance).setMetadata((SparkProtos.SamplerMetadata)builderForValue.build());
            return this;
         }

         public SparkProtos.SamplerData.Builder mergeMetadata(SparkProtos.SamplerMetadata value) {
            this.copyOnWrite();
            ((SparkProtos.SamplerData)this.instance).mergeMetadata(value);
            return this;
         }

         public SparkProtos.SamplerData.Builder clearMetadata() {
            this.copyOnWrite();
            ((SparkProtos.SamplerData)this.instance).clearMetadata();
            return this;
         }

         public List<SparkProtos.ThreadNode> getThreadsList() {
            return Collections.unmodifiableList(((SparkProtos.SamplerData)this.instance).getThreadsList());
         }

         public int getThreadsCount() {
            return ((SparkProtos.SamplerData)this.instance).getThreadsCount();
         }

         public SparkProtos.ThreadNode getThreads(int index) {
            return ((SparkProtos.SamplerData)this.instance).getThreads(index);
         }

         public SparkProtos.SamplerData.Builder setThreads(int index, SparkProtos.ThreadNode value) {
            this.copyOnWrite();
            ((SparkProtos.SamplerData)this.instance).setThreads(index, value);
            return this;
         }

         public SparkProtos.SamplerData.Builder setThreads(int index, SparkProtos.ThreadNode.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.SamplerData)this.instance).setThreads(index, (SparkProtos.ThreadNode)builderForValue.build());
            return this;
         }

         public SparkProtos.SamplerData.Builder addThreads(SparkProtos.ThreadNode value) {
            this.copyOnWrite();
            ((SparkProtos.SamplerData)this.instance).addThreads(value);
            return this;
         }

         public SparkProtos.SamplerData.Builder addThreads(int index, SparkProtos.ThreadNode value) {
            this.copyOnWrite();
            ((SparkProtos.SamplerData)this.instance).addThreads(index, value);
            return this;
         }

         public SparkProtos.SamplerData.Builder addThreads(SparkProtos.ThreadNode.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.SamplerData)this.instance).addThreads((SparkProtos.ThreadNode)builderForValue.build());
            return this;
         }

         public SparkProtos.SamplerData.Builder addThreads(int index, SparkProtos.ThreadNode.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.SamplerData)this.instance).addThreads(index, (SparkProtos.ThreadNode)builderForValue.build());
            return this;
         }

         public SparkProtos.SamplerData.Builder addAllThreads(Iterable<? extends SparkProtos.ThreadNode> values) {
            this.copyOnWrite();
            ((SparkProtos.SamplerData)this.instance).addAllThreads(values);
            return this;
         }

         public SparkProtos.SamplerData.Builder clearThreads() {
            this.copyOnWrite();
            ((SparkProtos.SamplerData)this.instance).clearThreads();
            return this;
         }

         public SparkProtos.SamplerData.Builder removeThreads(int index) {
            this.copyOnWrite();
            ((SparkProtos.SamplerData)this.instance).removeThreads(index);
            return this;
         }

         public int getClassSourcesCount() {
            return ((SparkProtos.SamplerData)this.instance).getClassSourcesMap().size();
         }

         public boolean containsClassSources(String key) {
            key.getClass();
            return ((SparkProtos.SamplerData)this.instance).getClassSourcesMap().containsKey(key);
         }

         public SparkProtos.SamplerData.Builder clearClassSources() {
            this.copyOnWrite();
            ((SparkProtos.SamplerData)this.instance).getMutableClassSourcesMap().clear();
            return this;
         }

         public SparkProtos.SamplerData.Builder removeClassSources(String key) {
            key.getClass();
            this.copyOnWrite();
            ((SparkProtos.SamplerData)this.instance).getMutableClassSourcesMap().remove(key);
            return this;
         }

         /** @deprecated */
         @Deprecated
         public Map<String, String> getClassSources() {
            return this.getClassSourcesMap();
         }

         public Map<String, String> getClassSourcesMap() {
            return Collections.unmodifiableMap(((SparkProtos.SamplerData)this.instance).getClassSourcesMap());
         }

         public String getClassSourcesOrDefault(String key, String defaultValue) {
            key.getClass();
            Map<String, String> map = ((SparkProtos.SamplerData)this.instance).getClassSourcesMap();
            return map.containsKey(key) ? (String)map.get(key) : defaultValue;
         }

         public String getClassSourcesOrThrow(String key) {
            key.getClass();
            Map<String, String> map = ((SparkProtos.SamplerData)this.instance).getClassSourcesMap();
            if (!map.containsKey(key)) {
               throw new IllegalArgumentException();
            } else {
               return (String)map.get(key);
            }
         }

         public SparkProtos.SamplerData.Builder putClassSources(String key, String value) {
            key.getClass();
            value.getClass();
            this.copyOnWrite();
            ((SparkProtos.SamplerData)this.instance).getMutableClassSourcesMap().put(key, value);
            return this;
         }

         public SparkProtos.SamplerData.Builder putAllClassSources(Map<String, String> values) {
            this.copyOnWrite();
            ((SparkProtos.SamplerData)this.instance).getMutableClassSourcesMap().putAll(values);
            return this;
         }
      }

      private static final class ClassSourcesDefaultEntryHolder {
         static final MapEntryLite<String, String> defaultEntry = MapEntryLite.<String, String>newDefaultInstance(WireFormat.FieldType.STRING, "", WireFormat.FieldType.STRING, "");

         private ClassSourcesDefaultEntryHolder() {
         }
      }
   }

   public interface SamplerDataOrBuilder extends MessageLiteOrBuilder {
      boolean hasMetadata();

      SparkProtos.SamplerMetadata getMetadata();

      List<SparkProtos.ThreadNode> getThreadsList();

      SparkProtos.ThreadNode getThreads(int var1);

      int getThreadsCount();

      int getClassSourcesCount();

      boolean containsClassSources(String var1);

      /** @deprecated */
      @Deprecated
      Map<String, String> getClassSources();

      Map<String, String> getClassSourcesMap();

      String getClassSourcesOrDefault(String var1, String var2);

      String getClassSourcesOrThrow(String var1);
   }

   public static final class SamplerMetadata extends GeneratedMessageLite<SparkProtos.SamplerMetadata, SparkProtos.SamplerMetadata.Builder> implements SparkProtos.SamplerMetadataOrBuilder {
      public static final int CREATOR_FIELD_NUMBER = 1;
      private SparkProtos.CommandSenderMetadata creator_;
      public static final int START_TIME_FIELD_NUMBER = 2;
      private long startTime_;
      public static final int INTERVAL_FIELD_NUMBER = 3;
      private int interval_;
      public static final int THREAD_DUMPER_FIELD_NUMBER = 4;
      private SparkProtos.SamplerMetadata.ThreadDumper threadDumper_;
      public static final int DATA_AGGREGATOR_FIELD_NUMBER = 5;
      private SparkProtos.SamplerMetadata.DataAggregator dataAggregator_;
      public static final int COMMENT_FIELD_NUMBER = 6;
      private String comment_ = "";
      public static final int PLATFORM_METADATA_FIELD_NUMBER = 7;
      private SparkProtos.PlatformMetadata platformMetadata_;
      private static final SparkProtos.SamplerMetadata DEFAULT_INSTANCE;
      private static volatile Parser<SparkProtos.SamplerMetadata> PARSER;

      private SamplerMetadata() {
      }

      public boolean hasCreator() {
         return this.creator_ != null;
      }

      public SparkProtos.CommandSenderMetadata getCreator() {
         return this.creator_ == null ? SparkProtos.CommandSenderMetadata.DEFAULT_INSTANCE : this.creator_;
      }

      private void setCreator(SparkProtos.CommandSenderMetadata value) {
         value.getClass();
         this.creator_ = value;
      }

      private void mergeCreator(SparkProtos.CommandSenderMetadata value) {
         value.getClass();
         if (this.creator_ != null && this.creator_ != SparkProtos.CommandSenderMetadata.DEFAULT_INSTANCE) {
            this.creator_ = (SparkProtos.CommandSenderMetadata)((SparkProtos.CommandSenderMetadata.Builder)SparkProtos.CommandSenderMetadata.newBuilder(this.creator_).mergeFrom(value)).buildPartial();
         } else {
            this.creator_ = value;
         }

      }

      private void clearCreator() {
         this.creator_ = null;
      }

      public long getStartTime() {
         return this.startTime_;
      }

      private void setStartTime(long value) {
         this.startTime_ = value;
      }

      private void clearStartTime() {
         this.startTime_ = 0L;
      }

      public int getInterval() {
         return this.interval_;
      }

      private void setInterval(int value) {
         this.interval_ = value;
      }

      private void clearInterval() {
         this.interval_ = 0;
      }

      public boolean hasThreadDumper() {
         return this.threadDumper_ != null;
      }

      public SparkProtos.SamplerMetadata.ThreadDumper getThreadDumper() {
         return this.threadDumper_ == null ? SparkProtos.SamplerMetadata.ThreadDumper.DEFAULT_INSTANCE : this.threadDumper_;
      }

      private void setThreadDumper(SparkProtos.SamplerMetadata.ThreadDumper value) {
         value.getClass();
         this.threadDumper_ = value;
      }

      private void mergeThreadDumper(SparkProtos.SamplerMetadata.ThreadDumper value) {
         value.getClass();
         if (this.threadDumper_ != null && this.threadDumper_ != SparkProtos.SamplerMetadata.ThreadDumper.DEFAULT_INSTANCE) {
            this.threadDumper_ = (SparkProtos.SamplerMetadata.ThreadDumper)((SparkProtos.SamplerMetadata.ThreadDumper.Builder)SparkProtos.SamplerMetadata.ThreadDumper.newBuilder(this.threadDumper_).mergeFrom(value)).buildPartial();
         } else {
            this.threadDumper_ = value;
         }

      }

      private void clearThreadDumper() {
         this.threadDumper_ = null;
      }

      public boolean hasDataAggregator() {
         return this.dataAggregator_ != null;
      }

      public SparkProtos.SamplerMetadata.DataAggregator getDataAggregator() {
         return this.dataAggregator_ == null ? SparkProtos.SamplerMetadata.DataAggregator.DEFAULT_INSTANCE : this.dataAggregator_;
      }

      private void setDataAggregator(SparkProtos.SamplerMetadata.DataAggregator value) {
         value.getClass();
         this.dataAggregator_ = value;
      }

      private void mergeDataAggregator(SparkProtos.SamplerMetadata.DataAggregator value) {
         value.getClass();
         if (this.dataAggregator_ != null && this.dataAggregator_ != SparkProtos.SamplerMetadata.DataAggregator.DEFAULT_INSTANCE) {
            this.dataAggregator_ = (SparkProtos.SamplerMetadata.DataAggregator)((SparkProtos.SamplerMetadata.DataAggregator.Builder)SparkProtos.SamplerMetadata.DataAggregator.newBuilder(this.dataAggregator_).mergeFrom(value)).buildPartial();
         } else {
            this.dataAggregator_ = value;
         }

      }

      private void clearDataAggregator() {
         this.dataAggregator_ = null;
      }

      public String getComment() {
         return this.comment_;
      }

      public ByteString getCommentBytes() {
         return ByteString.copyFromUtf8(this.comment_);
      }

      private void setComment(String value) {
         value.getClass();
         this.comment_ = value;
      }

      private void clearComment() {
         this.comment_ = getDefaultInstance().getComment();
      }

      private void setCommentBytes(ByteString value) {
         checkByteStringIsUtf8(value);
         this.comment_ = value.toStringUtf8();
      }

      public boolean hasPlatformMetadata() {
         return this.platformMetadata_ != null;
      }

      public SparkProtos.PlatformMetadata getPlatformMetadata() {
         return this.platformMetadata_ == null ? SparkProtos.PlatformMetadata.DEFAULT_INSTANCE : this.platformMetadata_;
      }

      private void setPlatformMetadata(SparkProtos.PlatformMetadata value) {
         value.getClass();
         this.platformMetadata_ = value;
      }

      private void mergePlatformMetadata(SparkProtos.PlatformMetadata value) {
         value.getClass();
         if (this.platformMetadata_ != null && this.platformMetadata_ != SparkProtos.PlatformMetadata.DEFAULT_INSTANCE) {
            this.platformMetadata_ = (SparkProtos.PlatformMetadata)((SparkProtos.PlatformMetadata.Builder)SparkProtos.PlatformMetadata.newBuilder(this.platformMetadata_).mergeFrom(value)).buildPartial();
         } else {
            this.platformMetadata_ = value;
         }

      }

      private void clearPlatformMetadata() {
         this.platformMetadata_ = null;
      }

      public static SparkProtos.SamplerMetadata parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (SparkProtos.SamplerMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.SamplerMetadata parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.SamplerMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.SamplerMetadata parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (SparkProtos.SamplerMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.SamplerMetadata parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.SamplerMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.SamplerMetadata parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (SparkProtos.SamplerMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.SamplerMetadata parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.SamplerMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.SamplerMetadata parseFrom(InputStream input) throws IOException {
         return (SparkProtos.SamplerMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.SamplerMetadata parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.SamplerMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.SamplerMetadata parseDelimitedFrom(InputStream input) throws IOException {
         return (SparkProtos.SamplerMetadata)parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.SamplerMetadata parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.SamplerMetadata)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.SamplerMetadata parseFrom(CodedInputStream input) throws IOException {
         return (SparkProtos.SamplerMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.SamplerMetadata parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.SamplerMetadata)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.SamplerMetadata.Builder newBuilder() {
         return (SparkProtos.SamplerMetadata.Builder)DEFAULT_INSTANCE.createBuilder();
      }

      public static SparkProtos.SamplerMetadata.Builder newBuilder(SparkProtos.SamplerMetadata prototype) {
         return (SparkProtos.SamplerMetadata.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
      }

      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch(method) {
         case NEW_MUTABLE_INSTANCE:
            return new SparkProtos.SamplerMetadata();
         case NEW_BUILDER:
            return new SparkProtos.SamplerMetadata.Builder();
         case BUILD_MESSAGE_INFO:
            Object[] objects = new Object[]{"creator_", "startTime_", "interval_", "threadDumper_", "dataAggregator_", "comment_", "platformMetadata_"};
            String info = "\u0000\u0007\u0000\u0000\u0001\u0007\u0007\u0000\u0000\u0000\u0001\t\u0002\u0002\u0003\u0004\u0004\t\u0005\t\u0006Ȉ\u0007\t";
            return newMessageInfo(DEFAULT_INSTANCE, info, objects);
         case GET_DEFAULT_INSTANCE:
            return DEFAULT_INSTANCE;
         case GET_PARSER:
            Parser<SparkProtos.SamplerMetadata> parser = PARSER;
            if (parser == null) {
               synchronized(SparkProtos.SamplerMetadata.class) {
                  parser = PARSER;
                  if (parser == null) {
                     parser = new GeneratedMessageLite.DefaultInstanceBasedParser<SparkProtos.SamplerMetadata>(DEFAULT_INSTANCE);
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

      public static SparkProtos.SamplerMetadata getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<SparkProtos.SamplerMetadata> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         SparkProtos.SamplerMetadata defaultInstance = new SparkProtos.SamplerMetadata();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(SparkProtos.SamplerMetadata.class, defaultInstance);
      }

      public static final class Builder extends GeneratedMessageLite.Builder<SparkProtos.SamplerMetadata, SparkProtos.SamplerMetadata.Builder> implements SparkProtos.SamplerMetadataOrBuilder {
         private Builder() {
            super(SparkProtos.SamplerMetadata.DEFAULT_INSTANCE);
         }

         public boolean hasCreator() {
            return ((SparkProtos.SamplerMetadata)this.instance).hasCreator();
         }

         public SparkProtos.CommandSenderMetadata getCreator() {
            return ((SparkProtos.SamplerMetadata)this.instance).getCreator();
         }

         public SparkProtos.SamplerMetadata.Builder setCreator(SparkProtos.CommandSenderMetadata value) {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).setCreator(value);
            return this;
         }

         public SparkProtos.SamplerMetadata.Builder setCreator(SparkProtos.CommandSenderMetadata.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).setCreator((SparkProtos.CommandSenderMetadata)builderForValue.build());
            return this;
         }

         public SparkProtos.SamplerMetadata.Builder mergeCreator(SparkProtos.CommandSenderMetadata value) {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).mergeCreator(value);
            return this;
         }

         public SparkProtos.SamplerMetadata.Builder clearCreator() {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).clearCreator();
            return this;
         }

         public long getStartTime() {
            return ((SparkProtos.SamplerMetadata)this.instance).getStartTime();
         }

         public SparkProtos.SamplerMetadata.Builder setStartTime(long value) {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).setStartTime(value);
            return this;
         }

         public SparkProtos.SamplerMetadata.Builder clearStartTime() {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).clearStartTime();
            return this;
         }

         public int getInterval() {
            return ((SparkProtos.SamplerMetadata)this.instance).getInterval();
         }

         public SparkProtos.SamplerMetadata.Builder setInterval(int value) {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).setInterval(value);
            return this;
         }

         public SparkProtos.SamplerMetadata.Builder clearInterval() {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).clearInterval();
            return this;
         }

         public boolean hasThreadDumper() {
            return ((SparkProtos.SamplerMetadata)this.instance).hasThreadDumper();
         }

         public SparkProtos.SamplerMetadata.ThreadDumper getThreadDumper() {
            return ((SparkProtos.SamplerMetadata)this.instance).getThreadDumper();
         }

         public SparkProtos.SamplerMetadata.Builder setThreadDumper(SparkProtos.SamplerMetadata.ThreadDumper value) {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).setThreadDumper(value);
            return this;
         }

         public SparkProtos.SamplerMetadata.Builder setThreadDumper(SparkProtos.SamplerMetadata.ThreadDumper.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).setThreadDumper((SparkProtos.SamplerMetadata.ThreadDumper)builderForValue.build());
            return this;
         }

         public SparkProtos.SamplerMetadata.Builder mergeThreadDumper(SparkProtos.SamplerMetadata.ThreadDumper value) {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).mergeThreadDumper(value);
            return this;
         }

         public SparkProtos.SamplerMetadata.Builder clearThreadDumper() {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).clearThreadDumper();
            return this;
         }

         public boolean hasDataAggregator() {
            return ((SparkProtos.SamplerMetadata)this.instance).hasDataAggregator();
         }

         public SparkProtos.SamplerMetadata.DataAggregator getDataAggregator() {
            return ((SparkProtos.SamplerMetadata)this.instance).getDataAggregator();
         }

         public SparkProtos.SamplerMetadata.Builder setDataAggregator(SparkProtos.SamplerMetadata.DataAggregator value) {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).setDataAggregator(value);
            return this;
         }

         public SparkProtos.SamplerMetadata.Builder setDataAggregator(SparkProtos.SamplerMetadata.DataAggregator.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).setDataAggregator((SparkProtos.SamplerMetadata.DataAggregator)builderForValue.build());
            return this;
         }

         public SparkProtos.SamplerMetadata.Builder mergeDataAggregator(SparkProtos.SamplerMetadata.DataAggregator value) {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).mergeDataAggregator(value);
            return this;
         }

         public SparkProtos.SamplerMetadata.Builder clearDataAggregator() {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).clearDataAggregator();
            return this;
         }

         public String getComment() {
            return ((SparkProtos.SamplerMetadata)this.instance).getComment();
         }

         public ByteString getCommentBytes() {
            return ((SparkProtos.SamplerMetadata)this.instance).getCommentBytes();
         }

         public SparkProtos.SamplerMetadata.Builder setComment(String value) {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).setComment(value);
            return this;
         }

         public SparkProtos.SamplerMetadata.Builder clearComment() {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).clearComment();
            return this;
         }

         public SparkProtos.SamplerMetadata.Builder setCommentBytes(ByteString value) {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).setCommentBytes(value);
            return this;
         }

         public boolean hasPlatformMetadata() {
            return ((SparkProtos.SamplerMetadata)this.instance).hasPlatformMetadata();
         }

         public SparkProtos.PlatformMetadata getPlatformMetadata() {
            return ((SparkProtos.SamplerMetadata)this.instance).getPlatformMetadata();
         }

         public SparkProtos.SamplerMetadata.Builder setPlatformMetadata(SparkProtos.PlatformMetadata value) {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).setPlatformMetadata(value);
            return this;
         }

         public SparkProtos.SamplerMetadata.Builder setPlatformMetadata(SparkProtos.PlatformMetadata.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).setPlatformMetadata((SparkProtos.PlatformMetadata)builderForValue.build());
            return this;
         }

         public SparkProtos.SamplerMetadata.Builder mergePlatformMetadata(SparkProtos.PlatformMetadata value) {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).mergePlatformMetadata(value);
            return this;
         }

         public SparkProtos.SamplerMetadata.Builder clearPlatformMetadata() {
            this.copyOnWrite();
            ((SparkProtos.SamplerMetadata)this.instance).clearPlatformMetadata();
            return this;
         }
      }

      public static final class DataAggregator extends GeneratedMessageLite<SparkProtos.SamplerMetadata.DataAggregator, SparkProtos.SamplerMetadata.DataAggregator.Builder> implements SparkProtos.SamplerMetadata.DataAggregatorOrBuilder {
         public static final int TYPE_FIELD_NUMBER = 1;
         private int type_;
         public static final int THREAD_GROUPER_FIELD_NUMBER = 2;
         private int threadGrouper_;
         public static final int TICK_LENGTH_THRESHOLD_FIELD_NUMBER = 3;
         private long tickLengthThreshold_;
         private static final SparkProtos.SamplerMetadata.DataAggregator DEFAULT_INSTANCE;
         private static volatile Parser<SparkProtos.SamplerMetadata.DataAggregator> PARSER;

         private DataAggregator() {
         }

         public int getTypeValue() {
            return this.type_;
         }

         public SparkProtos.SamplerMetadata.DataAggregator.Type getType() {
            SparkProtos.SamplerMetadata.DataAggregator.Type result = SparkProtos.SamplerMetadata.DataAggregator.Type.forNumber(this.type_);
            return result == null ? SparkProtos.SamplerMetadata.DataAggregator.Type.UNRECOGNIZED : result;
         }

         private void setTypeValue(int value) {
            this.type_ = value;
         }

         private void setType(SparkProtos.SamplerMetadata.DataAggregator.Type value) {
            this.type_ = value.getNumber();
         }

         private void clearType() {
            this.type_ = 0;
         }

         public int getThreadGrouperValue() {
            return this.threadGrouper_;
         }

         public SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper getThreadGrouper() {
            SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper result = SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper.forNumber(this.threadGrouper_);
            return result == null ? SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper.UNRECOGNIZED : result;
         }

         private void setThreadGrouperValue(int value) {
            this.threadGrouper_ = value;
         }

         private void setThreadGrouper(SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper value) {
            this.threadGrouper_ = value.getNumber();
         }

         private void clearThreadGrouper() {
            this.threadGrouper_ = 0;
         }

         public long getTickLengthThreshold() {
            return this.tickLengthThreshold_;
         }

         private void setTickLengthThreshold(long value) {
            this.tickLengthThreshold_ = value;
         }

         private void clearTickLengthThreshold() {
            this.tickLengthThreshold_ = 0L;
         }

         public static SparkProtos.SamplerMetadata.DataAggregator parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (SparkProtos.SamplerMetadata.DataAggregator)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static SparkProtos.SamplerMetadata.DataAggregator parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SparkProtos.SamplerMetadata.DataAggregator)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static SparkProtos.SamplerMetadata.DataAggregator parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (SparkProtos.SamplerMetadata.DataAggregator)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static SparkProtos.SamplerMetadata.DataAggregator parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SparkProtos.SamplerMetadata.DataAggregator)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static SparkProtos.SamplerMetadata.DataAggregator parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (SparkProtos.SamplerMetadata.DataAggregator)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static SparkProtos.SamplerMetadata.DataAggregator parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SparkProtos.SamplerMetadata.DataAggregator)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static SparkProtos.SamplerMetadata.DataAggregator parseFrom(InputStream input) throws IOException {
            return (SparkProtos.SamplerMetadata.DataAggregator)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static SparkProtos.SamplerMetadata.DataAggregator parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SparkProtos.SamplerMetadata.DataAggregator)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static SparkProtos.SamplerMetadata.DataAggregator parseDelimitedFrom(InputStream input) throws IOException {
            return (SparkProtos.SamplerMetadata.DataAggregator)parseDelimitedFrom(DEFAULT_INSTANCE, input);
         }

         public static SparkProtos.SamplerMetadata.DataAggregator parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SparkProtos.SamplerMetadata.DataAggregator)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static SparkProtos.SamplerMetadata.DataAggregator parseFrom(CodedInputStream input) throws IOException {
            return (SparkProtos.SamplerMetadata.DataAggregator)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static SparkProtos.SamplerMetadata.DataAggregator parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SparkProtos.SamplerMetadata.DataAggregator)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static SparkProtos.SamplerMetadata.DataAggregator.Builder newBuilder() {
            return (SparkProtos.SamplerMetadata.DataAggregator.Builder)DEFAULT_INSTANCE.createBuilder();
         }

         public static SparkProtos.SamplerMetadata.DataAggregator.Builder newBuilder(SparkProtos.SamplerMetadata.DataAggregator prototype) {
            return (SparkProtos.SamplerMetadata.DataAggregator.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
         }

         protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch(method) {
            case NEW_MUTABLE_INSTANCE:
               return new SparkProtos.SamplerMetadata.DataAggregator();
            case NEW_BUILDER:
               return new SparkProtos.SamplerMetadata.DataAggregator.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{"type_", "threadGrouper_", "tickLengthThreshold_"};
               String info = "\u0000\u0003\u0000\u0000\u0001\u0003\u0003\u0000\u0000\u0000\u0001\f\u0002\f\u0003\u0002";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<SparkProtos.SamplerMetadata.DataAggregator> parser = PARSER;
               if (parser == null) {
                  synchronized(SparkProtos.SamplerMetadata.DataAggregator.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<SparkProtos.SamplerMetadata.DataAggregator>(DEFAULT_INSTANCE);
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

         public static SparkProtos.SamplerMetadata.DataAggregator getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<SparkProtos.SamplerMetadata.DataAggregator> parser() {
            return DEFAULT_INSTANCE.getParserForType();
         }

         static {
            SparkProtos.SamplerMetadata.DataAggregator defaultInstance = new SparkProtos.SamplerMetadata.DataAggregator();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(SparkProtos.SamplerMetadata.DataAggregator.class, defaultInstance);
         }

         public static final class Builder extends GeneratedMessageLite.Builder<SparkProtos.SamplerMetadata.DataAggregator, SparkProtos.SamplerMetadata.DataAggregator.Builder> implements SparkProtos.SamplerMetadata.DataAggregatorOrBuilder {
            private Builder() {
               super(SparkProtos.SamplerMetadata.DataAggregator.DEFAULT_INSTANCE);
            }

            public int getTypeValue() {
               return ((SparkProtos.SamplerMetadata.DataAggregator)this.instance).getTypeValue();
            }

            public SparkProtos.SamplerMetadata.DataAggregator.Builder setTypeValue(int value) {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.DataAggregator)this.instance).setTypeValue(value);
               return this;
            }

            public SparkProtos.SamplerMetadata.DataAggregator.Type getType() {
               return ((SparkProtos.SamplerMetadata.DataAggregator)this.instance).getType();
            }

            public SparkProtos.SamplerMetadata.DataAggregator.Builder setType(SparkProtos.SamplerMetadata.DataAggregator.Type value) {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.DataAggregator)this.instance).setType(value);
               return this;
            }

            public SparkProtos.SamplerMetadata.DataAggregator.Builder clearType() {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.DataAggregator)this.instance).clearType();
               return this;
            }

            public int getThreadGrouperValue() {
               return ((SparkProtos.SamplerMetadata.DataAggregator)this.instance).getThreadGrouperValue();
            }

            public SparkProtos.SamplerMetadata.DataAggregator.Builder setThreadGrouperValue(int value) {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.DataAggregator)this.instance).setThreadGrouperValue(value);
               return this;
            }

            public SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper getThreadGrouper() {
               return ((SparkProtos.SamplerMetadata.DataAggregator)this.instance).getThreadGrouper();
            }

            public SparkProtos.SamplerMetadata.DataAggregator.Builder setThreadGrouper(SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper value) {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.DataAggregator)this.instance).setThreadGrouper(value);
               return this;
            }

            public SparkProtos.SamplerMetadata.DataAggregator.Builder clearThreadGrouper() {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.DataAggregator)this.instance).clearThreadGrouper();
               return this;
            }

            public long getTickLengthThreshold() {
               return ((SparkProtos.SamplerMetadata.DataAggregator)this.instance).getTickLengthThreshold();
            }

            public SparkProtos.SamplerMetadata.DataAggregator.Builder setTickLengthThreshold(long value) {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.DataAggregator)this.instance).setTickLengthThreshold(value);
               return this;
            }

            public SparkProtos.SamplerMetadata.DataAggregator.Builder clearTickLengthThreshold() {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.DataAggregator)this.instance).clearTickLengthThreshold();
               return this;
            }
         }

         public static enum ThreadGrouper implements Internal.EnumLite {
            BY_NAME(0),
            BY_POOL(1),
            AS_ONE(2),
            UNRECOGNIZED(-1);

            public static final int BY_NAME_VALUE = 0;
            public static final int BY_POOL_VALUE = 1;
            public static final int AS_ONE_VALUE = 2;
            private static final Internal.EnumLiteMap<SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper> internalValueMap = new Internal.EnumLiteMap<SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper>() {
               public SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper findValueByNumber(int number) {
                  return SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper.forNumber(number);
               }
            };
            private final int value;

            public final int getNumber() {
               if (this == UNRECOGNIZED) {
                  throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
               } else {
                  return this.value;
               }
            }

            /** @deprecated */
            @Deprecated
            public static SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper valueOf(int value) {
               return forNumber(value);
            }

            public static SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper forNumber(int value) {
               switch(value) {
               case 0:
                  return BY_NAME;
               case 1:
                  return BY_POOL;
               case 2:
                  return AS_ONE;
               default:
                  return null;
               }
            }

            public static Internal.EnumLiteMap<SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper> internalGetValueMap() {
               return internalValueMap;
            }

            public static Internal.EnumVerifier internalGetVerifier() {
               return SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper.ThreadGrouperVerifier.INSTANCE;
            }

            private ThreadGrouper(int value) {
               this.value = value;
            }

            private static final class ThreadGrouperVerifier implements Internal.EnumVerifier {
               static final Internal.EnumVerifier INSTANCE = new SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper.ThreadGrouperVerifier();

               private ThreadGrouperVerifier() {
               }

               public boolean isInRange(int number) {
                  return SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper.forNumber(number) != null;
               }
            }
         }

         public static enum Type implements Internal.EnumLite {
            SIMPLE(0),
            TICKED(1),
            UNRECOGNIZED(-1);

            public static final int SIMPLE_VALUE = 0;
            public static final int TICKED_VALUE = 1;
            private static final Internal.EnumLiteMap<SparkProtos.SamplerMetadata.DataAggregator.Type> internalValueMap = new Internal.EnumLiteMap<SparkProtos.SamplerMetadata.DataAggregator.Type>() {
               public SparkProtos.SamplerMetadata.DataAggregator.Type findValueByNumber(int number) {
                  return SparkProtos.SamplerMetadata.DataAggregator.Type.forNumber(number);
               }
            };
            private final int value;

            public final int getNumber() {
               if (this == UNRECOGNIZED) {
                  throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
               } else {
                  return this.value;
               }
            }

            /** @deprecated */
            @Deprecated
            public static SparkProtos.SamplerMetadata.DataAggregator.Type valueOf(int value) {
               return forNumber(value);
            }

            public static SparkProtos.SamplerMetadata.DataAggregator.Type forNumber(int value) {
               switch(value) {
               case 0:
                  return SIMPLE;
               case 1:
                  return TICKED;
               default:
                  return null;
               }
            }

            public static Internal.EnumLiteMap<SparkProtos.SamplerMetadata.DataAggregator.Type> internalGetValueMap() {
               return internalValueMap;
            }

            public static Internal.EnumVerifier internalGetVerifier() {
               return SparkProtos.SamplerMetadata.DataAggregator.Type.TypeVerifier.INSTANCE;
            }

            private Type(int value) {
               this.value = value;
            }

            private static final class TypeVerifier implements Internal.EnumVerifier {
               static final Internal.EnumVerifier INSTANCE = new SparkProtos.SamplerMetadata.DataAggregator.Type.TypeVerifier();

               private TypeVerifier() {
               }

               public boolean isInRange(int number) {
                  return SparkProtos.SamplerMetadata.DataAggregator.Type.forNumber(number) != null;
               }
            }
         }
      }

      public interface DataAggregatorOrBuilder extends MessageLiteOrBuilder {
         int getTypeValue();

         SparkProtos.SamplerMetadata.DataAggregator.Type getType();

         int getThreadGrouperValue();

         SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper getThreadGrouper();

         long getTickLengthThreshold();
      }

      public static final class ThreadDumper extends GeneratedMessageLite<SparkProtos.SamplerMetadata.ThreadDumper, SparkProtos.SamplerMetadata.ThreadDumper.Builder> implements SparkProtos.SamplerMetadata.ThreadDumperOrBuilder {
         public static final int TYPE_FIELD_NUMBER = 1;
         private int type_;
         public static final int IDS_FIELD_NUMBER = 2;
         private Internal.LongList ids_ = emptyLongList();
         private int idsMemoizedSerializedSize = -1;
         public static final int PATTERNS_FIELD_NUMBER = 3;
         private Internal.ProtobufList<String> patterns_ = GeneratedMessageLite.<String>emptyProtobufList();
         private static final SparkProtos.SamplerMetadata.ThreadDumper DEFAULT_INSTANCE;
         private static volatile Parser<SparkProtos.SamplerMetadata.ThreadDumper> PARSER;

         private ThreadDumper() {
         }

         public int getTypeValue() {
            return this.type_;
         }

         public SparkProtos.SamplerMetadata.ThreadDumper.Type getType() {
            SparkProtos.SamplerMetadata.ThreadDumper.Type result = SparkProtos.SamplerMetadata.ThreadDumper.Type.forNumber(this.type_);
            return result == null ? SparkProtos.SamplerMetadata.ThreadDumper.Type.UNRECOGNIZED : result;
         }

         private void setTypeValue(int value) {
            this.type_ = value;
         }

         private void setType(SparkProtos.SamplerMetadata.ThreadDumper.Type value) {
            this.type_ = value.getNumber();
         }

         private void clearType() {
            this.type_ = 0;
         }

         public List<Long> getIdsList() {
            return this.ids_;
         }

         public int getIdsCount() {
            return this.ids_.size();
         }

         public long getIds(int index) {
            return this.ids_.getLong(index);
         }

         private void ensureIdsIsMutable() {
            Internal.LongList tmp = this.ids_;
            if (!tmp.isModifiable()) {
               this.ids_ = GeneratedMessageLite.mutableCopy(tmp);
            }

         }

         private void setIds(int index, long value) {
            this.ensureIdsIsMutable();
            this.ids_.setLong(index, value);
         }

         private void addIds(long value) {
            this.ensureIdsIsMutable();
            this.ids_.addLong(value);
         }

         private void addAllIds(Iterable<? extends Long> values) {
            this.ensureIdsIsMutable();
            AbstractMessageLite.addAll(values, this.ids_);
         }

         private void clearIds() {
            this.ids_ = emptyLongList();
         }

         public List<String> getPatternsList() {
            return this.patterns_;
         }

         public int getPatternsCount() {
            return this.patterns_.size();
         }

         public String getPatterns(int index) {
            return (String)this.patterns_.get(index);
         }

         public ByteString getPatternsBytes(int index) {
            return ByteString.copyFromUtf8((String)this.patterns_.get(index));
         }

         private void ensurePatternsIsMutable() {
            Internal.ProtobufList<String> tmp = this.patterns_;
            if (!tmp.isModifiable()) {
               this.patterns_ = GeneratedMessageLite.mutableCopy(tmp);
            }

         }

         private void setPatterns(int index, String value) {
            value.getClass();
            this.ensurePatternsIsMutable();
            this.patterns_.set(index, value);
         }

         private void addPatterns(String value) {
            value.getClass();
            this.ensurePatternsIsMutable();
            this.patterns_.add(value);
         }

         private void addAllPatterns(Iterable<String> values) {
            this.ensurePatternsIsMutable();
            AbstractMessageLite.addAll(values, this.patterns_);
         }

         private void clearPatterns() {
            this.patterns_ = GeneratedMessageLite.<String>emptyProtobufList();
         }

         private void addPatternsBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.ensurePatternsIsMutable();
            this.patterns_.add(value.toStringUtf8());
         }

         public static SparkProtos.SamplerMetadata.ThreadDumper parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (SparkProtos.SamplerMetadata.ThreadDumper)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static SparkProtos.SamplerMetadata.ThreadDumper parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SparkProtos.SamplerMetadata.ThreadDumper)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static SparkProtos.SamplerMetadata.ThreadDumper parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (SparkProtos.SamplerMetadata.ThreadDumper)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static SparkProtos.SamplerMetadata.ThreadDumper parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SparkProtos.SamplerMetadata.ThreadDumper)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static SparkProtos.SamplerMetadata.ThreadDumper parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (SparkProtos.SamplerMetadata.ThreadDumper)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static SparkProtos.SamplerMetadata.ThreadDumper parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SparkProtos.SamplerMetadata.ThreadDumper)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static SparkProtos.SamplerMetadata.ThreadDumper parseFrom(InputStream input) throws IOException {
            return (SparkProtos.SamplerMetadata.ThreadDumper)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static SparkProtos.SamplerMetadata.ThreadDumper parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SparkProtos.SamplerMetadata.ThreadDumper)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static SparkProtos.SamplerMetadata.ThreadDumper parseDelimitedFrom(InputStream input) throws IOException {
            return (SparkProtos.SamplerMetadata.ThreadDumper)parseDelimitedFrom(DEFAULT_INSTANCE, input);
         }

         public static SparkProtos.SamplerMetadata.ThreadDumper parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SparkProtos.SamplerMetadata.ThreadDumper)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static SparkProtos.SamplerMetadata.ThreadDumper parseFrom(CodedInputStream input) throws IOException {
            return (SparkProtos.SamplerMetadata.ThreadDumper)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static SparkProtos.SamplerMetadata.ThreadDumper parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SparkProtos.SamplerMetadata.ThreadDumper)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static SparkProtos.SamplerMetadata.ThreadDumper.Builder newBuilder() {
            return (SparkProtos.SamplerMetadata.ThreadDumper.Builder)DEFAULT_INSTANCE.createBuilder();
         }

         public static SparkProtos.SamplerMetadata.ThreadDumper.Builder newBuilder(SparkProtos.SamplerMetadata.ThreadDumper prototype) {
            return (SparkProtos.SamplerMetadata.ThreadDumper.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
         }

         protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch(method) {
            case NEW_MUTABLE_INSTANCE:
               return new SparkProtos.SamplerMetadata.ThreadDumper();
            case NEW_BUILDER:
               return new SparkProtos.SamplerMetadata.ThreadDumper.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{"type_", "ids_", "patterns_"};
               String info = "\u0000\u0003\u0000\u0000\u0001\u0003\u0003\u0000\u0002\u0000\u0001\f\u0002%\u0003Ț";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<SparkProtos.SamplerMetadata.ThreadDumper> parser = PARSER;
               if (parser == null) {
                  synchronized(SparkProtos.SamplerMetadata.ThreadDumper.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<SparkProtos.SamplerMetadata.ThreadDumper>(DEFAULT_INSTANCE);
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

         public static SparkProtos.SamplerMetadata.ThreadDumper getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<SparkProtos.SamplerMetadata.ThreadDumper> parser() {
            return DEFAULT_INSTANCE.getParserForType();
         }

         static {
            SparkProtos.SamplerMetadata.ThreadDumper defaultInstance = new SparkProtos.SamplerMetadata.ThreadDumper();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(SparkProtos.SamplerMetadata.ThreadDumper.class, defaultInstance);
         }

         public static final class Builder extends GeneratedMessageLite.Builder<SparkProtos.SamplerMetadata.ThreadDumper, SparkProtos.SamplerMetadata.ThreadDumper.Builder> implements SparkProtos.SamplerMetadata.ThreadDumperOrBuilder {
            private Builder() {
               super(SparkProtos.SamplerMetadata.ThreadDumper.DEFAULT_INSTANCE);
            }

            public int getTypeValue() {
               return ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).getTypeValue();
            }

            public SparkProtos.SamplerMetadata.ThreadDumper.Builder setTypeValue(int value) {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).setTypeValue(value);
               return this;
            }

            public SparkProtos.SamplerMetadata.ThreadDumper.Type getType() {
               return ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).getType();
            }

            public SparkProtos.SamplerMetadata.ThreadDumper.Builder setType(SparkProtos.SamplerMetadata.ThreadDumper.Type value) {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).setType(value);
               return this;
            }

            public SparkProtos.SamplerMetadata.ThreadDumper.Builder clearType() {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).clearType();
               return this;
            }

            public List<Long> getIdsList() {
               return Collections.unmodifiableList(((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).getIdsList());
            }

            public int getIdsCount() {
               return ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).getIdsCount();
            }

            public long getIds(int index) {
               return ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).getIds(index);
            }

            public SparkProtos.SamplerMetadata.ThreadDumper.Builder setIds(int index, long value) {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).setIds(index, value);
               return this;
            }

            public SparkProtos.SamplerMetadata.ThreadDumper.Builder addIds(long value) {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).addIds(value);
               return this;
            }

            public SparkProtos.SamplerMetadata.ThreadDumper.Builder addAllIds(Iterable<? extends Long> values) {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).addAllIds(values);
               return this;
            }

            public SparkProtos.SamplerMetadata.ThreadDumper.Builder clearIds() {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).clearIds();
               return this;
            }

            public List<String> getPatternsList() {
               return Collections.unmodifiableList(((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).getPatternsList());
            }

            public int getPatternsCount() {
               return ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).getPatternsCount();
            }

            public String getPatterns(int index) {
               return ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).getPatterns(index);
            }

            public ByteString getPatternsBytes(int index) {
               return ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).getPatternsBytes(index);
            }

            public SparkProtos.SamplerMetadata.ThreadDumper.Builder setPatterns(int index, String value) {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).setPatterns(index, value);
               return this;
            }

            public SparkProtos.SamplerMetadata.ThreadDumper.Builder addPatterns(String value) {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).addPatterns(value);
               return this;
            }

            public SparkProtos.SamplerMetadata.ThreadDumper.Builder addAllPatterns(Iterable<String> values) {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).addAllPatterns(values);
               return this;
            }

            public SparkProtos.SamplerMetadata.ThreadDumper.Builder clearPatterns() {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).clearPatterns();
               return this;
            }

            public SparkProtos.SamplerMetadata.ThreadDumper.Builder addPatternsBytes(ByteString value) {
               this.copyOnWrite();
               ((SparkProtos.SamplerMetadata.ThreadDumper)this.instance).addPatternsBytes(value);
               return this;
            }
         }

         public static enum Type implements Internal.EnumLite {
            ALL(0),
            SPECIFIC(1),
            REGEX(2),
            UNRECOGNIZED(-1);

            public static final int ALL_VALUE = 0;
            public static final int SPECIFIC_VALUE = 1;
            public static final int REGEX_VALUE = 2;
            private static final Internal.EnumLiteMap<SparkProtos.SamplerMetadata.ThreadDumper.Type> internalValueMap = new Internal.EnumLiteMap<SparkProtos.SamplerMetadata.ThreadDumper.Type>() {
               public SparkProtos.SamplerMetadata.ThreadDumper.Type findValueByNumber(int number) {
                  return SparkProtos.SamplerMetadata.ThreadDumper.Type.forNumber(number);
               }
            };
            private final int value;

            public final int getNumber() {
               if (this == UNRECOGNIZED) {
                  throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
               } else {
                  return this.value;
               }
            }

            /** @deprecated */
            @Deprecated
            public static SparkProtos.SamplerMetadata.ThreadDumper.Type valueOf(int value) {
               return forNumber(value);
            }

            public static SparkProtos.SamplerMetadata.ThreadDumper.Type forNumber(int value) {
               switch(value) {
               case 0:
                  return ALL;
               case 1:
                  return SPECIFIC;
               case 2:
                  return REGEX;
               default:
                  return null;
               }
            }

            public static Internal.EnumLiteMap<SparkProtos.SamplerMetadata.ThreadDumper.Type> internalGetValueMap() {
               return internalValueMap;
            }

            public static Internal.EnumVerifier internalGetVerifier() {
               return SparkProtos.SamplerMetadata.ThreadDumper.Type.TypeVerifier.INSTANCE;
            }

            private Type(int value) {
               this.value = value;
            }

            private static final class TypeVerifier implements Internal.EnumVerifier {
               static final Internal.EnumVerifier INSTANCE = new SparkProtos.SamplerMetadata.ThreadDumper.Type.TypeVerifier();

               private TypeVerifier() {
               }

               public boolean isInRange(int number) {
                  return SparkProtos.SamplerMetadata.ThreadDumper.Type.forNumber(number) != null;
               }
            }
         }
      }

      public interface ThreadDumperOrBuilder extends MessageLiteOrBuilder {
         int getTypeValue();

         SparkProtos.SamplerMetadata.ThreadDumper.Type getType();

         List<Long> getIdsList();

         int getIdsCount();

         long getIds(int var1);

         List<String> getPatternsList();

         int getPatternsCount();

         String getPatterns(int var1);

         ByteString getPatternsBytes(int var1);
      }
   }

   public interface SamplerMetadataOrBuilder extends MessageLiteOrBuilder {
      boolean hasCreator();

      SparkProtos.CommandSenderMetadata getCreator();

      long getStartTime();

      int getInterval();

      boolean hasThreadDumper();

      SparkProtos.SamplerMetadata.ThreadDumper getThreadDumper();

      boolean hasDataAggregator();

      SparkProtos.SamplerMetadata.DataAggregator getDataAggregator();

      String getComment();

      ByteString getCommentBytes();

      boolean hasPlatformMetadata();

      SparkProtos.PlatformMetadata getPlatformMetadata();
   }

   public static final class StackTraceNode extends GeneratedMessageLite<SparkProtos.StackTraceNode, SparkProtos.StackTraceNode.Builder> implements SparkProtos.StackTraceNodeOrBuilder {
      public static final int TIME_FIELD_NUMBER = 1;
      private double time_;
      public static final int CHILDREN_FIELD_NUMBER = 2;
      private Internal.ProtobufList<SparkProtos.StackTraceNode> children_ = emptyProtobufList();
      public static final int CLASS_NAME_FIELD_NUMBER = 3;
      private String className_ = "";
      public static final int METHOD_NAME_FIELD_NUMBER = 4;
      private String methodName_ = "";
      public static final int PARENT_LINE_NUMBER_FIELD_NUMBER = 5;
      private int parentLineNumber_;
      public static final int LINE_NUMBER_FIELD_NUMBER = 6;
      private int lineNumber_;
      public static final int METHOD_DESC_FIELD_NUMBER = 7;
      private String methodDesc_ = "";
      private static final SparkProtos.StackTraceNode DEFAULT_INSTANCE;
      private static volatile Parser<SparkProtos.StackTraceNode> PARSER;

      private StackTraceNode() {
      }

      public double getTime() {
         return this.time_;
      }

      private void setTime(double value) {
         this.time_ = value;
      }

      private void clearTime() {
         this.time_ = 0.0D;
      }

      public List<SparkProtos.StackTraceNode> getChildrenList() {
         return this.children_;
      }

      public List<? extends SparkProtos.StackTraceNodeOrBuilder> getChildrenOrBuilderList() {
         return this.children_;
      }

      public int getChildrenCount() {
         return this.children_.size();
      }

      public SparkProtos.StackTraceNode getChildren(int index) {
         return (SparkProtos.StackTraceNode)this.children_.get(index);
      }

      public SparkProtos.StackTraceNodeOrBuilder getChildrenOrBuilder(int index) {
         return (SparkProtos.StackTraceNodeOrBuilder)this.children_.get(index);
      }

      private void ensureChildrenIsMutable() {
         Internal.ProtobufList<SparkProtos.StackTraceNode> tmp = this.children_;
         if (!tmp.isModifiable()) {
            this.children_ = GeneratedMessageLite.mutableCopy(tmp);
         }

      }

      private void setChildren(int index, SparkProtos.StackTraceNode value) {
         value.getClass();
         this.ensureChildrenIsMutable();
         this.children_.set(index, value);
      }

      private void addChildren(SparkProtos.StackTraceNode value) {
         value.getClass();
         this.ensureChildrenIsMutable();
         this.children_.add(value);
      }

      private void addChildren(int index, SparkProtos.StackTraceNode value) {
         value.getClass();
         this.ensureChildrenIsMutable();
         this.children_.add(index, value);
      }

      private void addAllChildren(Iterable<? extends SparkProtos.StackTraceNode> values) {
         this.ensureChildrenIsMutable();
         AbstractMessageLite.addAll(values, this.children_);
      }

      private void clearChildren() {
         this.children_ = emptyProtobufList();
      }

      private void removeChildren(int index) {
         this.ensureChildrenIsMutable();
         this.children_.remove(index);
      }

      public String getClassName() {
         return this.className_;
      }

      public ByteString getClassNameBytes() {
         return ByteString.copyFromUtf8(this.className_);
      }

      private void setClassName(String value) {
         value.getClass();
         this.className_ = value;
      }

      private void clearClassName() {
         this.className_ = getDefaultInstance().getClassName();
      }

      private void setClassNameBytes(ByteString value) {
         checkByteStringIsUtf8(value);
         this.className_ = value.toStringUtf8();
      }

      public String getMethodName() {
         return this.methodName_;
      }

      public ByteString getMethodNameBytes() {
         return ByteString.copyFromUtf8(this.methodName_);
      }

      private void setMethodName(String value) {
         value.getClass();
         this.methodName_ = value;
      }

      private void clearMethodName() {
         this.methodName_ = getDefaultInstance().getMethodName();
      }

      private void setMethodNameBytes(ByteString value) {
         checkByteStringIsUtf8(value);
         this.methodName_ = value.toStringUtf8();
      }

      public int getParentLineNumber() {
         return this.parentLineNumber_;
      }

      private void setParentLineNumber(int value) {
         this.parentLineNumber_ = value;
      }

      private void clearParentLineNumber() {
         this.parentLineNumber_ = 0;
      }

      public int getLineNumber() {
         return this.lineNumber_;
      }

      private void setLineNumber(int value) {
         this.lineNumber_ = value;
      }

      private void clearLineNumber() {
         this.lineNumber_ = 0;
      }

      public String getMethodDesc() {
         return this.methodDesc_;
      }

      public ByteString getMethodDescBytes() {
         return ByteString.copyFromUtf8(this.methodDesc_);
      }

      private void setMethodDesc(String value) {
         value.getClass();
         this.methodDesc_ = value;
      }

      private void clearMethodDesc() {
         this.methodDesc_ = getDefaultInstance().getMethodDesc();
      }

      private void setMethodDescBytes(ByteString value) {
         checkByteStringIsUtf8(value);
         this.methodDesc_ = value.toStringUtf8();
      }

      public static SparkProtos.StackTraceNode parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (SparkProtos.StackTraceNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.StackTraceNode parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.StackTraceNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.StackTraceNode parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (SparkProtos.StackTraceNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.StackTraceNode parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.StackTraceNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.StackTraceNode parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (SparkProtos.StackTraceNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.StackTraceNode parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.StackTraceNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.StackTraceNode parseFrom(InputStream input) throws IOException {
         return (SparkProtos.StackTraceNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.StackTraceNode parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.StackTraceNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.StackTraceNode parseDelimitedFrom(InputStream input) throws IOException {
         return (SparkProtos.StackTraceNode)parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.StackTraceNode parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.StackTraceNode)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.StackTraceNode parseFrom(CodedInputStream input) throws IOException {
         return (SparkProtos.StackTraceNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.StackTraceNode parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.StackTraceNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.StackTraceNode.Builder newBuilder() {
         return (SparkProtos.StackTraceNode.Builder)DEFAULT_INSTANCE.createBuilder();
      }

      public static SparkProtos.StackTraceNode.Builder newBuilder(SparkProtos.StackTraceNode prototype) {
         return (SparkProtos.StackTraceNode.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
      }

      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch(method) {
         case NEW_MUTABLE_INSTANCE:
            return new SparkProtos.StackTraceNode();
         case NEW_BUILDER:
            return new SparkProtos.StackTraceNode.Builder();
         case BUILD_MESSAGE_INFO:
            Object[] objects = new Object[]{"time_", "children_", SparkProtos.StackTraceNode.class, "className_", "methodName_", "parentLineNumber_", "lineNumber_", "methodDesc_"};
            String info = "\u0000\u0007\u0000\u0000\u0001\u0007\u0007\u0000\u0001\u0000\u0001\u0000\u0002\u001b\u0003Ȉ\u0004Ȉ\u0005\u0004\u0006\u0004\u0007Ȉ";
            return newMessageInfo(DEFAULT_INSTANCE, info, objects);
         case GET_DEFAULT_INSTANCE:
            return DEFAULT_INSTANCE;
         case GET_PARSER:
            Parser<SparkProtos.StackTraceNode> parser = PARSER;
            if (parser == null) {
               synchronized(SparkProtos.StackTraceNode.class) {
                  parser = PARSER;
                  if (parser == null) {
                     parser = new GeneratedMessageLite.DefaultInstanceBasedParser<SparkProtos.StackTraceNode>(DEFAULT_INSTANCE);
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

      public static SparkProtos.StackTraceNode getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<SparkProtos.StackTraceNode> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         SparkProtos.StackTraceNode defaultInstance = new SparkProtos.StackTraceNode();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(SparkProtos.StackTraceNode.class, defaultInstance);
      }

      public static final class Builder extends GeneratedMessageLite.Builder<SparkProtos.StackTraceNode, SparkProtos.StackTraceNode.Builder> implements SparkProtos.StackTraceNodeOrBuilder {
         private Builder() {
            super(SparkProtos.StackTraceNode.DEFAULT_INSTANCE);
         }

         public double getTime() {
            return ((SparkProtos.StackTraceNode)this.instance).getTime();
         }

         public SparkProtos.StackTraceNode.Builder setTime(double value) {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).setTime(value);
            return this;
         }

         public SparkProtos.StackTraceNode.Builder clearTime() {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).clearTime();
            return this;
         }

         public List<SparkProtos.StackTraceNode> getChildrenList() {
            return Collections.unmodifiableList(((SparkProtos.StackTraceNode)this.instance).getChildrenList());
         }

         public int getChildrenCount() {
            return ((SparkProtos.StackTraceNode)this.instance).getChildrenCount();
         }

         public SparkProtos.StackTraceNode getChildren(int index) {
            return ((SparkProtos.StackTraceNode)this.instance).getChildren(index);
         }

         public SparkProtos.StackTraceNode.Builder setChildren(int index, SparkProtos.StackTraceNode value) {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).setChildren(index, value);
            return this;
         }

         public SparkProtos.StackTraceNode.Builder setChildren(int index, SparkProtos.StackTraceNode.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).setChildren(index, (SparkProtos.StackTraceNode)builderForValue.build());
            return this;
         }

         public SparkProtos.StackTraceNode.Builder addChildren(SparkProtos.StackTraceNode value) {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).addChildren(value);
            return this;
         }

         public SparkProtos.StackTraceNode.Builder addChildren(int index, SparkProtos.StackTraceNode value) {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).addChildren(index, value);
            return this;
         }

         public SparkProtos.StackTraceNode.Builder addChildren(SparkProtos.StackTraceNode.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).addChildren((SparkProtos.StackTraceNode)builderForValue.build());
            return this;
         }

         public SparkProtos.StackTraceNode.Builder addChildren(int index, SparkProtos.StackTraceNode.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).addChildren(index, (SparkProtos.StackTraceNode)builderForValue.build());
            return this;
         }

         public SparkProtos.StackTraceNode.Builder addAllChildren(Iterable<? extends SparkProtos.StackTraceNode> values) {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).addAllChildren(values);
            return this;
         }

         public SparkProtos.StackTraceNode.Builder clearChildren() {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).clearChildren();
            return this;
         }

         public SparkProtos.StackTraceNode.Builder removeChildren(int index) {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).removeChildren(index);
            return this;
         }

         public String getClassName() {
            return ((SparkProtos.StackTraceNode)this.instance).getClassName();
         }

         public ByteString getClassNameBytes() {
            return ((SparkProtos.StackTraceNode)this.instance).getClassNameBytes();
         }

         public SparkProtos.StackTraceNode.Builder setClassName(String value) {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).setClassName(value);
            return this;
         }

         public SparkProtos.StackTraceNode.Builder clearClassName() {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).clearClassName();
            return this;
         }

         public SparkProtos.StackTraceNode.Builder setClassNameBytes(ByteString value) {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).setClassNameBytes(value);
            return this;
         }

         public String getMethodName() {
            return ((SparkProtos.StackTraceNode)this.instance).getMethodName();
         }

         public ByteString getMethodNameBytes() {
            return ((SparkProtos.StackTraceNode)this.instance).getMethodNameBytes();
         }

         public SparkProtos.StackTraceNode.Builder setMethodName(String value) {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).setMethodName(value);
            return this;
         }

         public SparkProtos.StackTraceNode.Builder clearMethodName() {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).clearMethodName();
            return this;
         }

         public SparkProtos.StackTraceNode.Builder setMethodNameBytes(ByteString value) {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).setMethodNameBytes(value);
            return this;
         }

         public int getParentLineNumber() {
            return ((SparkProtos.StackTraceNode)this.instance).getParentLineNumber();
         }

         public SparkProtos.StackTraceNode.Builder setParentLineNumber(int value) {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).setParentLineNumber(value);
            return this;
         }

         public SparkProtos.StackTraceNode.Builder clearParentLineNumber() {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).clearParentLineNumber();
            return this;
         }

         public int getLineNumber() {
            return ((SparkProtos.StackTraceNode)this.instance).getLineNumber();
         }

         public SparkProtos.StackTraceNode.Builder setLineNumber(int value) {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).setLineNumber(value);
            return this;
         }

         public SparkProtos.StackTraceNode.Builder clearLineNumber() {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).clearLineNumber();
            return this;
         }

         public String getMethodDesc() {
            return ((SparkProtos.StackTraceNode)this.instance).getMethodDesc();
         }

         public ByteString getMethodDescBytes() {
            return ((SparkProtos.StackTraceNode)this.instance).getMethodDescBytes();
         }

         public SparkProtos.StackTraceNode.Builder setMethodDesc(String value) {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).setMethodDesc(value);
            return this;
         }

         public SparkProtos.StackTraceNode.Builder clearMethodDesc() {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).clearMethodDesc();
            return this;
         }

         public SparkProtos.StackTraceNode.Builder setMethodDescBytes(ByteString value) {
            this.copyOnWrite();
            ((SparkProtos.StackTraceNode)this.instance).setMethodDescBytes(value);
            return this;
         }
      }
   }

   public interface StackTraceNodeOrBuilder extends MessageLiteOrBuilder {
      double getTime();

      List<SparkProtos.StackTraceNode> getChildrenList();

      SparkProtos.StackTraceNode getChildren(int var1);

      int getChildrenCount();

      String getClassName();

      ByteString getClassNameBytes();

      String getMethodName();

      ByteString getMethodNameBytes();

      int getParentLineNumber();

      int getLineNumber();

      String getMethodDesc();

      ByteString getMethodDescBytes();
   }

   public static final class ThreadNode extends GeneratedMessageLite<SparkProtos.ThreadNode, SparkProtos.ThreadNode.Builder> implements SparkProtos.ThreadNodeOrBuilder {
      public static final int NAME_FIELD_NUMBER = 1;
      private String name_ = "";
      public static final int TIME_FIELD_NUMBER = 2;
      private double time_;
      public static final int CHILDREN_FIELD_NUMBER = 3;
      private Internal.ProtobufList<SparkProtos.StackTraceNode> children_ = emptyProtobufList();
      private static final SparkProtos.ThreadNode DEFAULT_INSTANCE;
      private static volatile Parser<SparkProtos.ThreadNode> PARSER;

      private ThreadNode() {
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

      public double getTime() {
         return this.time_;
      }

      private void setTime(double value) {
         this.time_ = value;
      }

      private void clearTime() {
         this.time_ = 0.0D;
      }

      public List<SparkProtos.StackTraceNode> getChildrenList() {
         return this.children_;
      }

      public List<? extends SparkProtos.StackTraceNodeOrBuilder> getChildrenOrBuilderList() {
         return this.children_;
      }

      public int getChildrenCount() {
         return this.children_.size();
      }

      public SparkProtos.StackTraceNode getChildren(int index) {
         return (SparkProtos.StackTraceNode)this.children_.get(index);
      }

      public SparkProtos.StackTraceNodeOrBuilder getChildrenOrBuilder(int index) {
         return (SparkProtos.StackTraceNodeOrBuilder)this.children_.get(index);
      }

      private void ensureChildrenIsMutable() {
         Internal.ProtobufList<SparkProtos.StackTraceNode> tmp = this.children_;
         if (!tmp.isModifiable()) {
            this.children_ = GeneratedMessageLite.mutableCopy(tmp);
         }

      }

      private void setChildren(int index, SparkProtos.StackTraceNode value) {
         value.getClass();
         this.ensureChildrenIsMutable();
         this.children_.set(index, value);
      }

      private void addChildren(SparkProtos.StackTraceNode value) {
         value.getClass();
         this.ensureChildrenIsMutable();
         this.children_.add(value);
      }

      private void addChildren(int index, SparkProtos.StackTraceNode value) {
         value.getClass();
         this.ensureChildrenIsMutable();
         this.children_.add(index, value);
      }

      private void addAllChildren(Iterable<? extends SparkProtos.StackTraceNode> values) {
         this.ensureChildrenIsMutable();
         AbstractMessageLite.addAll(values, this.children_);
      }

      private void clearChildren() {
         this.children_ = emptyProtobufList();
      }

      private void removeChildren(int index) {
         this.ensureChildrenIsMutable();
         this.children_.remove(index);
      }

      public static SparkProtos.ThreadNode parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (SparkProtos.ThreadNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.ThreadNode parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.ThreadNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.ThreadNode parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (SparkProtos.ThreadNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.ThreadNode parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.ThreadNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.ThreadNode parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (SparkProtos.ThreadNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkProtos.ThreadNode parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (SparkProtos.ThreadNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkProtos.ThreadNode parseFrom(InputStream input) throws IOException {
         return (SparkProtos.ThreadNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.ThreadNode parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.ThreadNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.ThreadNode parseDelimitedFrom(InputStream input) throws IOException {
         return (SparkProtos.ThreadNode)parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.ThreadNode parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.ThreadNode)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.ThreadNode parseFrom(CodedInputStream input) throws IOException {
         return (SparkProtos.ThreadNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkProtos.ThreadNode parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (SparkProtos.ThreadNode)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkProtos.ThreadNode.Builder newBuilder() {
         return (SparkProtos.ThreadNode.Builder)DEFAULT_INSTANCE.createBuilder();
      }

      public static SparkProtos.ThreadNode.Builder newBuilder(SparkProtos.ThreadNode prototype) {
         return (SparkProtos.ThreadNode.Builder)DEFAULT_INSTANCE.createBuilder(prototype);
      }

      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch(method) {
         case NEW_MUTABLE_INSTANCE:
            return new SparkProtos.ThreadNode();
         case NEW_BUILDER:
            return new SparkProtos.ThreadNode.Builder();
         case BUILD_MESSAGE_INFO:
            Object[] objects = new Object[]{"name_", "time_", "children_", SparkProtos.StackTraceNode.class};
            String info = "\u0000\u0003\u0000\u0000\u0001\u0003\u0003\u0000\u0001\u0000\u0001Ȉ\u0002\u0000\u0003\u001b";
            return newMessageInfo(DEFAULT_INSTANCE, info, objects);
         case GET_DEFAULT_INSTANCE:
            return DEFAULT_INSTANCE;
         case GET_PARSER:
            Parser<SparkProtos.ThreadNode> parser = PARSER;
            if (parser == null) {
               synchronized(SparkProtos.ThreadNode.class) {
                  parser = PARSER;
                  if (parser == null) {
                     parser = new GeneratedMessageLite.DefaultInstanceBasedParser<SparkProtos.ThreadNode>(DEFAULT_INSTANCE);
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

      public static SparkProtos.ThreadNode getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<SparkProtos.ThreadNode> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         SparkProtos.ThreadNode defaultInstance = new SparkProtos.ThreadNode();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(SparkProtos.ThreadNode.class, defaultInstance);
      }

      public static final class Builder extends GeneratedMessageLite.Builder<SparkProtos.ThreadNode, SparkProtos.ThreadNode.Builder> implements SparkProtos.ThreadNodeOrBuilder {
         private Builder() {
            super(SparkProtos.ThreadNode.DEFAULT_INSTANCE);
         }

         public String getName() {
            return ((SparkProtos.ThreadNode)this.instance).getName();
         }

         public ByteString getNameBytes() {
            return ((SparkProtos.ThreadNode)this.instance).getNameBytes();
         }

         public SparkProtos.ThreadNode.Builder setName(String value) {
            this.copyOnWrite();
            ((SparkProtos.ThreadNode)this.instance).setName(value);
            return this;
         }

         public SparkProtos.ThreadNode.Builder clearName() {
            this.copyOnWrite();
            ((SparkProtos.ThreadNode)this.instance).clearName();
            return this;
         }

         public SparkProtos.ThreadNode.Builder setNameBytes(ByteString value) {
            this.copyOnWrite();
            ((SparkProtos.ThreadNode)this.instance).setNameBytes(value);
            return this;
         }

         public double getTime() {
            return ((SparkProtos.ThreadNode)this.instance).getTime();
         }

         public SparkProtos.ThreadNode.Builder setTime(double value) {
            this.copyOnWrite();
            ((SparkProtos.ThreadNode)this.instance).setTime(value);
            return this;
         }

         public SparkProtos.ThreadNode.Builder clearTime() {
            this.copyOnWrite();
            ((SparkProtos.ThreadNode)this.instance).clearTime();
            return this;
         }

         public List<SparkProtos.StackTraceNode> getChildrenList() {
            return Collections.unmodifiableList(((SparkProtos.ThreadNode)this.instance).getChildrenList());
         }

         public int getChildrenCount() {
            return ((SparkProtos.ThreadNode)this.instance).getChildrenCount();
         }

         public SparkProtos.StackTraceNode getChildren(int index) {
            return ((SparkProtos.ThreadNode)this.instance).getChildren(index);
         }

         public SparkProtos.ThreadNode.Builder setChildren(int index, SparkProtos.StackTraceNode value) {
            this.copyOnWrite();
            ((SparkProtos.ThreadNode)this.instance).setChildren(index, value);
            return this;
         }

         public SparkProtos.ThreadNode.Builder setChildren(int index, SparkProtos.StackTraceNode.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.ThreadNode)this.instance).setChildren(index, (SparkProtos.StackTraceNode)builderForValue.build());
            return this;
         }

         public SparkProtos.ThreadNode.Builder addChildren(SparkProtos.StackTraceNode value) {
            this.copyOnWrite();
            ((SparkProtos.ThreadNode)this.instance).addChildren(value);
            return this;
         }

         public SparkProtos.ThreadNode.Builder addChildren(int index, SparkProtos.StackTraceNode value) {
            this.copyOnWrite();
            ((SparkProtos.ThreadNode)this.instance).addChildren(index, value);
            return this;
         }

         public SparkProtos.ThreadNode.Builder addChildren(SparkProtos.StackTraceNode.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.ThreadNode)this.instance).addChildren((SparkProtos.StackTraceNode)builderForValue.build());
            return this;
         }

         public SparkProtos.ThreadNode.Builder addChildren(int index, SparkProtos.StackTraceNode.Builder builderForValue) {
            this.copyOnWrite();
            ((SparkProtos.ThreadNode)this.instance).addChildren(index, (SparkProtos.StackTraceNode)builderForValue.build());
            return this;
         }

         public SparkProtos.ThreadNode.Builder addAllChildren(Iterable<? extends SparkProtos.StackTraceNode> values) {
            this.copyOnWrite();
            ((SparkProtos.ThreadNode)this.instance).addAllChildren(values);
            return this;
         }

         public SparkProtos.ThreadNode.Builder clearChildren() {
            this.copyOnWrite();
            ((SparkProtos.ThreadNode)this.instance).clearChildren();
            return this;
         }

         public SparkProtos.ThreadNode.Builder removeChildren(int index) {
            this.copyOnWrite();
            ((SparkProtos.ThreadNode)this.instance).removeChildren(index);
            return this;
         }
      }
   }

   public interface ThreadNodeOrBuilder extends MessageLiteOrBuilder {
      String getName();

      ByteString getNameBytes();

      double getTime();

      List<SparkProtos.StackTraceNode> getChildrenList();

      SparkProtos.StackTraceNode getChildren(int var1);

      int getChildrenCount();
   }
}
