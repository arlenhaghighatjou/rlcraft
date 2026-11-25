package me.lucko.spark.common.platform;

import java.lang.management.MemoryUsage;
import me.lucko.spark.proto.SparkProtos;

public interface PlatformInfo {
   PlatformInfo.Type getType();

   String getName();

   String getVersion();

   String getMinecraftVersion();

   int getNCpus();

   MemoryUsage getHeapUsage();

   default int getSparkVersion() {
      return 1;
   }

   default PlatformInfo.Data toData() {
      return new PlatformInfo.Data(this.getType(), this.getName(), this.getVersion(), this.getMinecraftVersion(), this.getNCpus(), this.getHeapUsage(), this.getSparkVersion());
   }

   public static final class Data {
      private final PlatformInfo.Type type;
      private final String name;
      private final String version;
      private final String minecraftVersion;
      private final int nCpus;
      private final MemoryUsage heapUsage;
      private final int sparkVersion;

      public Data(PlatformInfo.Type type, String name, String version, String minecraftVersion, int nCpus, MemoryUsage heapUsage, int sparkVersion) {
         this.type = type;
         this.name = name;
         this.version = version;
         this.minecraftVersion = minecraftVersion;
         this.nCpus = nCpus;
         this.heapUsage = heapUsage;
         this.sparkVersion = sparkVersion;
      }

      public PlatformInfo.Type getType() {
         return this.type;
      }

      public String getName() {
         return this.name;
      }

      public String getVersion() {
         return this.version;
      }

      public String getMinecraftVersion() {
         return this.minecraftVersion;
      }

      public int getNCpus() {
         return this.nCpus;
      }

      public MemoryUsage getHeapUsage() {
         return this.heapUsage;
      }

      public int getSparkVersion() {
         return this.sparkVersion;
      }

      public SparkProtos.MemoryUsage getHeapUsageProto() {
         return (SparkProtos.MemoryUsage)SparkProtos.MemoryUsage.newBuilder().setUsed(this.heapUsage.getUsed()).setCommitted(this.heapUsage.getCommitted()).setMax(this.heapUsage.getMax()).build();
      }

      public SparkProtos.PlatformMetadata toProto() {
         SparkProtos.PlatformMetadata.Builder proto = SparkProtos.PlatformMetadata.newBuilder().setType(this.type.toProto()).setName(this.name).setVersion(this.version).setNCpus(this.nCpus).setHeapUsage(this.getHeapUsageProto()).setSparkVersion(this.sparkVersion);
         if (this.minecraftVersion != null) {
            proto.setMinecraftVersion(this.minecraftVersion);
         }

         return (SparkProtos.PlatformMetadata)proto.build();
      }
   }

   public static enum Type {
      SERVER(SparkProtos.PlatformMetadata.Type.SERVER),
      CLIENT(SparkProtos.PlatformMetadata.Type.CLIENT),
      PROXY(SparkProtos.PlatformMetadata.Type.PROXY);

      private final SparkProtos.PlatformMetadata.Type type;

      private Type(SparkProtos.PlatformMetadata.Type type) {
         this.type = type;
      }

      public SparkProtos.PlatformMetadata.Type toProto() {
         return this.type;
      }
   }
}
