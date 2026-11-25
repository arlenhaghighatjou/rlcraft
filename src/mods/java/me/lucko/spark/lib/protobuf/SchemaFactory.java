package me.lucko.spark.lib.protobuf;

interface SchemaFactory {
   <T> Schema<T> createSchema(Class<T> var1);
}
