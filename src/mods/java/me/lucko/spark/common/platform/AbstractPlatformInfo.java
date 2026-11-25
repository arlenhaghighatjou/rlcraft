package me.lucko.spark.common.platform;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

public abstract class AbstractPlatformInfo implements PlatformInfo {
   public AbstractPlatformInfo() {
   }

   public int getNCpus() {
      return Runtime.getRuntime().availableProcessors();
   }

   public MemoryUsage getHeapUsage() {
      return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
   }
}
