package me.lucko.spark.lib.protobuf;

final class RawMessageInfo implements MessageInfo {
   private final MessageLite defaultInstance;
   private final String info;
   private final Object[] objects;
   private final int flags;

   RawMessageInfo(MessageLite defaultInstance, String info, Object[] objects) {
      this.defaultInstance = defaultInstance;
      this.info = info;
      this.objects = objects;

      int value;
      try {
         value = info.charAt(0);
      } catch (StringIndexOutOfBoundsException var11) {
         char[] infoChars = info.toCharArray();
         info = new String(infoChars);

         try {
            value = info.charAt(0);
         } catch (StringIndexOutOfBoundsException var10) {
            try {
               char[] infoChars2 = new char[info.length()];
               info.getChars(0, info.length(), infoChars2, 0);
               info = new String(infoChars2);
               value = info.charAt(0);
            } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException var9) {
               throw new IllegalStateException(String.format("Failed parsing '%s' with charArray.length of %d", info, infoChars.length), var9);
            }
         }
      }

      int position = 1;
      if (value < 55296) {
         this.flags = value;
      } else {
         int result = value & 8191;

         int shift;
         for(shift = 13; (value = info.charAt(position++)) >= 55296; shift += 13) {
            result |= (value & 8191) << shift;
         }

         this.flags = result | value << shift;
      }

   }

   String getStringInfo() {
      return this.info;
   }

   Object[] getObjects() {
      return this.objects;
   }

   public MessageLite getDefaultInstance() {
      return this.defaultInstance;
   }

   public ProtoSyntax getSyntax() {
      return (this.flags & 1) == 1 ? ProtoSyntax.PROTO2 : ProtoSyntax.PROTO3;
   }

   public boolean isMessageSetWireFormat() {
      return (this.flags & 2) == 2;
   }
}
