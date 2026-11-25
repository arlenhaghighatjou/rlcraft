package me.lucko.spark.lib.protobuf;

final class NewInstanceSchemaLite implements NewInstanceSchema {
   NewInstanceSchemaLite() {
   }

   public Object newInstance(Object defaultInstance) {
      return ((GeneratedMessageLite)defaultInstance).dynamicMethod(GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE);
   }
}
