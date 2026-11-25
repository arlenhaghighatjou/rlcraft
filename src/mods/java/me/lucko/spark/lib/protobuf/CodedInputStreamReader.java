package me.lucko.spark.lib.protobuf;

import java.io.IOException;
import java.util.List;
import java.util.Map;

final class CodedInputStreamReader implements Reader {
   private static final int FIXED32_MULTIPLE_MASK = 3;
   private static final int FIXED64_MULTIPLE_MASK = 7;
   private static final int NEXT_TAG_UNSET = 0;
   private final CodedInputStream input;
   private int tag;
   private int endGroupTag;
   private int nextTag = 0;

   public static CodedInputStreamReader forCodedInput(CodedInputStream input) {
      return input.wrapper != null ? input.wrapper : new CodedInputStreamReader(input);
   }

   private CodedInputStreamReader(CodedInputStream input) {
      this.input = (CodedInputStream)Internal.checkNotNull(input, "input");
      this.input.wrapper = this;
   }

   public boolean shouldDiscardUnknownFields() {
      return this.input.shouldDiscardUnknownFields();
   }

   public int getFieldNumber() throws IOException {
      if (this.nextTag != 0) {
         this.tag = this.nextTag;
         this.nextTag = 0;
      } else {
         this.tag = this.input.readTag();
      }

      return this.tag != 0 && this.tag != this.endGroupTag ? WireFormat.getTagFieldNumber(this.tag) : Integer.MAX_VALUE;
   }

   public int getTag() {
      return this.tag;
   }

   public boolean skipField() throws IOException {
      return !this.input.isAtEnd() && this.tag != this.endGroupTag ? this.input.skipField(this.tag) : false;
   }

   private void requireWireType(int requiredWireType) throws IOException {
      if (WireFormat.getTagWireType(this.tag) != requiredWireType) {
         throw InvalidProtocolBufferException.invalidWireType();
      }
   }

   public double readDouble() throws IOException {
      this.requireWireType(1);
      return this.input.readDouble();
   }

   public float readFloat() throws IOException {
      this.requireWireType(5);
      return this.input.readFloat();
   }

   public long readUInt64() throws IOException {
      this.requireWireType(0);
      return this.input.readUInt64();
   }

   public long readInt64() throws IOException {
      this.requireWireType(0);
      return this.input.readInt64();
   }

   public int readInt32() throws IOException {
      this.requireWireType(0);
      return this.input.readInt32();
   }

   public long readFixed64() throws IOException {
      this.requireWireType(1);
      return this.input.readFixed64();
   }

   public int readFixed32() throws IOException {
      this.requireWireType(5);
      return this.input.readFixed32();
   }

   public boolean readBool() throws IOException {
      this.requireWireType(0);
      return this.input.readBool();
   }

   public String readString() throws IOException {
      this.requireWireType(2);
      return this.input.readString();
   }

   public String readStringRequireUtf8() throws IOException {
      this.requireWireType(2);
      return this.input.readStringRequireUtf8();
   }

   public <T> T readMessage(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException {
      this.requireWireType(2);
      return (T)this.readMessage(Protobuf.getInstance().schemaFor(clazz), extensionRegistry);
   }

   public <T> T readMessageBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      this.requireWireType(2);
      return (T)this.readMessage(schema, extensionRegistry);
   }

   public <T> T readGroup(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException {
      this.requireWireType(3);
      return (T)this.readGroup(Protobuf.getInstance().schemaFor(clazz), extensionRegistry);
   }

   public <T> T readGroupBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      this.requireWireType(3);
      return (T)this.readGroup(schema, extensionRegistry);
   }

   private <T> T readMessage(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      int size = this.input.readUInt32();
      if (this.input.recursionDepth >= this.input.recursionLimit) {
         throw InvalidProtocolBufferException.recursionLimitExceeded();
      } else {
         int prevLimit = this.input.pushLimit(size);
         T message = schema.newInstance();
         ++this.input.recursionDepth;
         schema.mergeFrom(message, this, extensionRegistry);
         schema.makeImmutable(message);
         this.input.checkLastTagWas(0);
         --this.input.recursionDepth;
         this.input.popLimit(prevLimit);
         return message;
      }
   }

   private <T> T readGroup(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      int prevEndGroupTag = this.endGroupTag;
      this.endGroupTag = WireFormat.makeTag(WireFormat.getTagFieldNumber(this.tag), 4);

      Object var5;
      try {
         T message = schema.newInstance();
         schema.mergeFrom(message, this, extensionRegistry);
         schema.makeImmutable(message);
         if (this.tag != this.endGroupTag) {
            throw InvalidProtocolBufferException.parseFailure();
         }

         var5 = message;
      } finally {
         this.endGroupTag = prevEndGroupTag;
      }

      return (T)var5;
   }

   public ByteString readBytes() throws IOException {
      this.requireWireType(2);
      return this.input.readBytes();
   }

   public int readUInt32() throws IOException {
      this.requireWireType(0);
      return this.input.readUInt32();
   }

   public int readEnum() throws IOException {
      this.requireWireType(0);
      return this.input.readEnum();
   }

   public int readSFixed32() throws IOException {
      this.requireWireType(5);
      return this.input.readSFixed32();
   }

   public long readSFixed64() throws IOException {
      this.requireWireType(1);
      return this.input.readSFixed64();
   }

   public int readSInt32() throws IOException {
      this.requireWireType(0);
      return this.input.readSInt32();
   }

   public long readSInt64() throws IOException {
      this.requireWireType(0);
      return this.input.readSInt64();
   }

   public void readDoubleList(List<Double> target) throws IOException {
      if (target instanceof DoubleArrayList) {
         DoubleArrayList plist = (DoubleArrayList)target;
         switch(WireFormat.getTagWireType(this.tag)) {
         case 1:
            int nextTag;
            while(true) {
               plist.addDouble(this.input.readDouble());
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            this.verifyPackedFixed64Length(bytes);
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               plist.addDouble(this.input.readDouble());
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            return;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch(WireFormat.getTagWireType(this.tag)) {
         case 1:
            int nextTag;
            while(true) {
               target.add(Double.valueOf(this.input.readDouble()));
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            this.verifyPackedFixed64Length(bytes);
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               target.add(Double.valueOf(this.input.readDouble()));
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            return;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   public void readFloatList(List<Float> target) throws IOException {
      if (target instanceof FloatArrayList) {
         FloatArrayList plist = (FloatArrayList)target;
         switch(WireFormat.getTagWireType(this.tag)) {
         case 2:
            int bytes = this.input.readUInt32();
            this.verifyPackedFixed32Length(bytes);
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               plist.addFloat(this.input.readFloat());
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            return;
         case 5:
            int nextTag;
            while(true) {
               plist.addFloat(this.input.readFloat());
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch(WireFormat.getTagWireType(this.tag)) {
         case 2:
            int bytes = this.input.readUInt32();
            this.verifyPackedFixed32Length(bytes);
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               target.add(Float.valueOf(this.input.readFloat()));
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            return;
         case 5:
            int nextTag;
            while(true) {
               target.add(Float.valueOf(this.input.readFloat()));
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   public void readUInt64List(List<Long> target) throws IOException {
      if (target instanceof LongArrayList) {
         LongArrayList plist = (LongArrayList)target;
         switch(WireFormat.getTagWireType(this.tag)) {
         case 0:
            int nextTag;
            while(true) {
               plist.addLong(this.input.readUInt64());
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               plist.addLong(this.input.readUInt64());
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            this.requirePosition(endPos);
            break;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch(WireFormat.getTagWireType(this.tag)) {
         case 0:
            int nextTag;
            while(true) {
               target.add(Long.valueOf(this.input.readUInt64()));
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               target.add(Long.valueOf(this.input.readUInt64()));
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            this.requirePosition(endPos);
            break;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      }

   }

   public void readInt64List(List<Long> target) throws IOException {
      if (target instanceof LongArrayList) {
         LongArrayList plist = (LongArrayList)target;
         switch(WireFormat.getTagWireType(this.tag)) {
         case 0:
            int nextTag;
            while(true) {
               plist.addLong(this.input.readInt64());
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               plist.addLong(this.input.readInt64());
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            this.requirePosition(endPos);
            break;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch(WireFormat.getTagWireType(this.tag)) {
         case 0:
            int nextTag;
            while(true) {
               target.add(Long.valueOf(this.input.readInt64()));
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               target.add(Long.valueOf(this.input.readInt64()));
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            this.requirePosition(endPos);
            break;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      }

   }

   public void readInt32List(List<Integer> target) throws IOException {
      if (target instanceof IntArrayList) {
         IntArrayList plist = (IntArrayList)target;
         switch(WireFormat.getTagWireType(this.tag)) {
         case 0:
            int nextTag;
            while(true) {
               plist.addInt(this.input.readInt32());
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               plist.addInt(this.input.readInt32());
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            this.requirePosition(endPos);
            break;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch(WireFormat.getTagWireType(this.tag)) {
         case 0:
            int nextTag;
            while(true) {
               target.add(Integer.valueOf(this.input.readInt32()));
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               target.add(Integer.valueOf(this.input.readInt32()));
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            this.requirePosition(endPos);
            break;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      }

   }

   public void readFixed64List(List<Long> target) throws IOException {
      if (target instanceof LongArrayList) {
         LongArrayList plist = (LongArrayList)target;
         switch(WireFormat.getTagWireType(this.tag)) {
         case 1:
            int nextTag;
            while(true) {
               plist.addLong(this.input.readFixed64());
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            this.verifyPackedFixed64Length(bytes);
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               plist.addLong(this.input.readFixed64());
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            return;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch(WireFormat.getTagWireType(this.tag)) {
         case 1:
            int nextTag;
            while(true) {
               target.add(Long.valueOf(this.input.readFixed64()));
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            this.verifyPackedFixed64Length(bytes);
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               target.add(Long.valueOf(this.input.readFixed64()));
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            return;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   public void readFixed32List(List<Integer> target) throws IOException {
      if (target instanceof IntArrayList) {
         IntArrayList plist = (IntArrayList)target;
         switch(WireFormat.getTagWireType(this.tag)) {
         case 2:
            int bytes = this.input.readUInt32();
            this.verifyPackedFixed32Length(bytes);
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               plist.addInt(this.input.readFixed32());
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            return;
         case 5:
            int nextTag;
            while(true) {
               plist.addInt(this.input.readFixed32());
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch(WireFormat.getTagWireType(this.tag)) {
         case 2:
            int bytes = this.input.readUInt32();
            this.verifyPackedFixed32Length(bytes);
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               target.add(Integer.valueOf(this.input.readFixed32()));
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            return;
         case 5:
            int nextTag;
            while(true) {
               target.add(Integer.valueOf(this.input.readFixed32()));
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   public void readBoolList(List<Boolean> target) throws IOException {
      if (target instanceof BooleanArrayList) {
         BooleanArrayList plist = (BooleanArrayList)target;
         switch(WireFormat.getTagWireType(this.tag)) {
         case 0:
            int nextTag;
            while(true) {
               plist.addBoolean(this.input.readBool());
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               plist.addBoolean(this.input.readBool());
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            this.requirePosition(endPos);
            break;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch(WireFormat.getTagWireType(this.tag)) {
         case 0:
            int nextTag;
            while(true) {
               target.add(Boolean.valueOf(this.input.readBool()));
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               target.add(Boolean.valueOf(this.input.readBool()));
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            this.requirePosition(endPos);
            break;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      }

   }

   public void readStringList(List<String> target) throws IOException {
      this.readStringListInternal(target, false);
   }

   public void readStringListRequireUtf8(List<String> target) throws IOException {
      this.readStringListInternal(target, true);
   }

   public void readStringListInternal(List<String> target, boolean requireUtf8) throws IOException {
      if (WireFormat.getTagWireType(this.tag) != 2) {
         throw InvalidProtocolBufferException.invalidWireType();
      } else if (target instanceof LazyStringList && !requireUtf8) {
         LazyStringList lazyList = (LazyStringList)target;

         int nextTag;
         while(true) {
            lazyList.add(this.readBytes());
            if (this.input.isAtEnd()) {
               return;
            }

            nextTag = this.input.readTag();
            if (nextTag != this.tag) {
               break;
            }
         }

         this.nextTag = nextTag;
      } else {
         int nextTag;
         while(true) {
            target.add(requireUtf8 ? this.readStringRequireUtf8() : this.readString());
            if (this.input.isAtEnd()) {
               return;
            }

            nextTag = this.input.readTag();
            if (nextTag != this.tag) {
               break;
            }
         }

         this.nextTag = nextTag;
      }
   }

   public <T> void readMessageList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException {
      Schema<T> schema = Protobuf.getInstance().schemaFor(targetType);
      this.readMessageList(target, schema, extensionRegistry);
   }

   public <T> void readMessageList(List<T> target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      if (WireFormat.getTagWireType(this.tag) != 2) {
         throw InvalidProtocolBufferException.invalidWireType();
      } else {
         int listTag = this.tag;

         int nextTag;
         while(true) {
            target.add(this.readMessage(schema, extensionRegistry));
            if (this.input.isAtEnd() || this.nextTag != 0) {
               return;
            }

            nextTag = this.input.readTag();
            if (nextTag != listTag) {
               break;
            }
         }

         this.nextTag = nextTag;
      }
   }

   public <T> void readGroupList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException {
      Schema<T> schema = Protobuf.getInstance().schemaFor(targetType);
      this.readGroupList(target, schema, extensionRegistry);
   }

   public <T> void readGroupList(List<T> target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      if (WireFormat.getTagWireType(this.tag) != 3) {
         throw InvalidProtocolBufferException.invalidWireType();
      } else {
         int listTag = this.tag;

         int nextTag;
         while(true) {
            target.add(this.readGroup(schema, extensionRegistry));
            if (this.input.isAtEnd() || this.nextTag != 0) {
               return;
            }

            nextTag = this.input.readTag();
            if (nextTag != listTag) {
               break;
            }
         }

         this.nextTag = nextTag;
      }
   }

   public void readBytesList(List<ByteString> target) throws IOException {
      if (WireFormat.getTagWireType(this.tag) != 2) {
         throw InvalidProtocolBufferException.invalidWireType();
      } else {
         int nextTag;
         while(true) {
            target.add(this.readBytes());
            if (this.input.isAtEnd()) {
               return;
            }

            nextTag = this.input.readTag();
            if (nextTag != this.tag) {
               break;
            }
         }

         this.nextTag = nextTag;
      }
   }

   public void readUInt32List(List<Integer> target) throws IOException {
      if (target instanceof IntArrayList) {
         IntArrayList plist = (IntArrayList)target;
         switch(WireFormat.getTagWireType(this.tag)) {
         case 0:
            int nextTag;
            while(true) {
               plist.addInt(this.input.readUInt32());
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               plist.addInt(this.input.readUInt32());
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            this.requirePosition(endPos);
            break;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch(WireFormat.getTagWireType(this.tag)) {
         case 0:
            int nextTag;
            while(true) {
               target.add(Integer.valueOf(this.input.readUInt32()));
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               target.add(Integer.valueOf(this.input.readUInt32()));
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            this.requirePosition(endPos);
            break;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      }

   }

   public void readEnumList(List<Integer> target) throws IOException {
      if (target instanceof IntArrayList) {
         IntArrayList plist = (IntArrayList)target;
         switch(WireFormat.getTagWireType(this.tag)) {
         case 0:
            int nextTag;
            while(true) {
               plist.addInt(this.input.readEnum());
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               plist.addInt(this.input.readEnum());
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            this.requirePosition(endPos);
            break;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch(WireFormat.getTagWireType(this.tag)) {
         case 0:
            int nextTag;
            while(true) {
               target.add(Integer.valueOf(this.input.readEnum()));
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               target.add(Integer.valueOf(this.input.readEnum()));
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            this.requirePosition(endPos);
            break;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      }

   }

   public void readSFixed32List(List<Integer> target) throws IOException {
      if (target instanceof IntArrayList) {
         IntArrayList plist = (IntArrayList)target;
         switch(WireFormat.getTagWireType(this.tag)) {
         case 2:
            int bytes = this.input.readUInt32();
            this.verifyPackedFixed32Length(bytes);
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               plist.addInt(this.input.readSFixed32());
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            return;
         case 5:
            int nextTag;
            while(true) {
               plist.addInt(this.input.readSFixed32());
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch(WireFormat.getTagWireType(this.tag)) {
         case 2:
            int bytes = this.input.readUInt32();
            this.verifyPackedFixed32Length(bytes);
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               target.add(Integer.valueOf(this.input.readSFixed32()));
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            return;
         case 5:
            int nextTag;
            while(true) {
               target.add(Integer.valueOf(this.input.readSFixed32()));
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   public void readSFixed64List(List<Long> target) throws IOException {
      if (target instanceof LongArrayList) {
         LongArrayList plist = (LongArrayList)target;
         switch(WireFormat.getTagWireType(this.tag)) {
         case 1:
            int nextTag;
            while(true) {
               plist.addLong(this.input.readSFixed64());
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            this.verifyPackedFixed64Length(bytes);
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               plist.addLong(this.input.readSFixed64());
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            return;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch(WireFormat.getTagWireType(this.tag)) {
         case 1:
            int nextTag;
            while(true) {
               target.add(Long.valueOf(this.input.readSFixed64()));
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            this.verifyPackedFixed64Length(bytes);
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               target.add(Long.valueOf(this.input.readSFixed64()));
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            return;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   public void readSInt32List(List<Integer> target) throws IOException {
      if (target instanceof IntArrayList) {
         IntArrayList plist = (IntArrayList)target;
         switch(WireFormat.getTagWireType(this.tag)) {
         case 0:
            int nextTag;
            while(true) {
               plist.addInt(this.input.readSInt32());
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               plist.addInt(this.input.readSInt32());
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            this.requirePosition(endPos);
            break;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch(WireFormat.getTagWireType(this.tag)) {
         case 0:
            int nextTag;
            while(true) {
               target.add(Integer.valueOf(this.input.readSInt32()));
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               target.add(Integer.valueOf(this.input.readSInt32()));
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            this.requirePosition(endPos);
            break;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      }

   }

   public void readSInt64List(List<Long> target) throws IOException {
      if (target instanceof LongArrayList) {
         LongArrayList plist = (LongArrayList)target;
         switch(WireFormat.getTagWireType(this.tag)) {
         case 0:
            int nextTag;
            while(true) {
               plist.addLong(this.input.readSInt64());
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               plist.addLong(this.input.readSInt64());
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            this.requirePosition(endPos);
            break;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch(WireFormat.getTagWireType(this.tag)) {
         case 0:
            int nextTag;
            while(true) {
               target.add(Long.valueOf(this.input.readSInt64()));
               if (this.input.isAtEnd()) {
                  return;
               }

               nextTag = this.input.readTag();
               if (nextTag != this.tag) {
                  break;
               }
            }

            this.nextTag = nextTag;
            return;
         case 2:
            int bytes = this.input.readUInt32();
            int endPos = this.input.getTotalBytesRead() + bytes;

            while(true) {
               target.add(Long.valueOf(this.input.readSInt64()));
               if (this.input.getTotalBytesRead() >= endPos) {
                  break;
               }
            }

            this.requirePosition(endPos);
            break;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
         }
      }

   }

   private void verifyPackedFixed64Length(int bytes) throws IOException {
      if ((bytes & 7) != 0) {
         throw InvalidProtocolBufferException.parseFailure();
      }
   }

   public <K, V> void readMap(Map<K, V> target, MapEntryLite.Metadata<K, V> metadata, ExtensionRegistryLite extensionRegistry) throws IOException {
      this.requireWireType(2);
      int size = this.input.readUInt32();
      int prevLimit = this.input.pushLimit(size);
      K key = metadata.defaultKey;
      V value = metadata.defaultValue;

      try {
         while(true) {
            int number = this.getFieldNumber();
            if (number == Integer.MAX_VALUE || this.input.isAtEnd()) {
               target.put(key, value);
               return;
            }

            try {
               switch(number) {
               case 1:
                  key = (K)this.readField(metadata.keyType, (Class)null, (ExtensionRegistryLite)null);
                  break;
               case 2:
                  value = (V)this.readField(metadata.valueType, metadata.defaultValue.getClass(), extensionRegistry);
                  break;
               default:
                  if (!this.skipField()) {
                     throw new InvalidProtocolBufferException("Unable to parse map entry.");
                  }
               }
            } catch (InvalidProtocolBufferException.InvalidWireTypeException var13) {
               if (!this.skipField()) {
                  throw new InvalidProtocolBufferException("Unable to parse map entry.");
               }
            }
         }
      } finally {
         this.input.popLimit(prevLimit);
      }
   }

   private Object readField(WireFormat.FieldType fieldType, Class<?> messageType, ExtensionRegistryLite extensionRegistry) throws IOException {
      switch(fieldType) {
      case BOOL:
         return this.readBool();
      case BYTES:
         return this.readBytes();
      case DOUBLE:
         return this.readDouble();
      case ENUM:
         return this.readEnum();
      case FIXED32:
         return this.readFixed32();
      case FIXED64:
         return this.readFixed64();
      case FLOAT:
         return this.readFloat();
      case INT32:
         return this.readInt32();
      case INT64:
         return this.readInt64();
      case MESSAGE:
         return this.readMessage(messageType, extensionRegistry);
      case SFIXED32:
         return this.readSFixed32();
      case SFIXED64:
         return this.readSFixed64();
      case SINT32:
         return this.readSInt32();
      case SINT64:
         return this.readSInt64();
      case STRING:
         return this.readStringRequireUtf8();
      case UINT32:
         return this.readUInt32();
      case UINT64:
         return this.readUInt64();
      default:
         throw new RuntimeException("unsupported field type.");
      }
   }

   private void verifyPackedFixed32Length(int bytes) throws IOException {
      if ((bytes & 3) != 0) {
         throw InvalidProtocolBufferException.parseFailure();
      }
   }

   private void requirePosition(int expectedPosition) throws IOException {
      if (this.input.getTotalBytesRead() != expectedPosition) {
         throw InvalidProtocolBufferException.truncatedMessage();
      }
   }
}
