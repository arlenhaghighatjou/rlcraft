package me.lucko.spark.common.util;

import java.util.Arrays;
import java.util.stream.Stream;

public final class ThreadFinder {
   private static final ThreadGroup ROOT_THREAD_GROUP;
   private int approxActiveCount;

   public ThreadFinder() {
      this.approxActiveCount = ROOT_THREAD_GROUP.activeCount();
   }

   public Stream<Thread> getThreads() {
      Thread[] threads;
      int len;
      for(threads = new Thread[this.approxActiveCount + 10]; (len = ROOT_THREAD_GROUP.enumerate(threads, true)) == threads.length; threads = new Thread[threads.length * 2]) {
         ;
      }

      this.approxActiveCount = len;
      return Arrays.stream(threads, 0, len);
   }

   static {
      ThreadGroup rootGroup;
      ThreadGroup parentGroup;
      for(rootGroup = Thread.currentThread().getThreadGroup(); (parentGroup = rootGroup.getParent()) != null; rootGroup = parentGroup) {
         ;
      }

      ROOT_THREAD_GROUP = rootGroup;
   }
}
