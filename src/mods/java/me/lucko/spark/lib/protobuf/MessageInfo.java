package me.lucko.spark.lib.protobuf;

interface MessageInfo {
   ProtoSyntax getSyntax();

   boolean isMessageSetWireFormat();

   MessageLite getDefaultInstance();
}
