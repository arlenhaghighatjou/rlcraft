package me.lucko.spark.lib.protobuf;

interface MessageInfoFactory {
   boolean isSupported(Class<?> var1);

   MessageInfo messageInfoFor(Class<?> var1);
}
