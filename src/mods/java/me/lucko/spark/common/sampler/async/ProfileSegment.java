package me.lucko.spark.common.sampler.async;

public class ProfileSegment {
   private final int nativeThreadId;
   private final String threadName;
   private final AsyncStackTraceElement[] stackTrace;
   private final long time;

   public ProfileSegment(int nativeThreadId, String threadName, AsyncStackTraceElement[] stackTrace, long time) {
      this.nativeThreadId = nativeThreadId;
      this.threadName = threadName;
      this.stackTrace = stackTrace;
      this.time = time;
   }

   public int getNativeThreadId() {
      return this.nativeThreadId;
   }

   public String getThreadName() {
      return this.threadName;
   }

   public AsyncStackTraceElement[] getStackTrace() {
      return this.stackTrace;
   }

   public long getTime() {
      return this.time;
   }
}
