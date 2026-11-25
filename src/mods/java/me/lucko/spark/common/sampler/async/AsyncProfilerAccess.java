package me.lucko.spark.common.sampler.async;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.logging.Level;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.util.TemporaryFiles;
import me.lucko.spark.lib.asyncprofiler.AsyncProfiler;

public enum AsyncProfilerAccess {
   INSTANCE;

   private final AsyncProfiler profiler;
   private final Exception setupException;

   private AsyncProfilerAccess() {
      Exception setupException = null;

      AsyncProfiler profiler;
      try {
         profiler = load();
         ensureCpuEventSupported(profiler);
      } catch (Exception var6) {
         profiler = null;
         setupException = var6;
      }

      this.profiler = profiler;
      this.setupException = setupException;
   }

   public AsyncProfiler getProfiler() {
      if (this.profiler == null) {
         throw new UnsupportedOperationException("async-profiler not supported", this.setupException);
      } else {
         return this.profiler;
      }
   }

   public boolean checkSupported(SparkPlatform platform) {
      if (this.setupException != null) {
         if (this.setupException instanceof AsyncProfilerAccess.UnsupportedSystemException) {
            platform.getPlugin().log(Level.INFO, "The async-profiler engine is not supported for your os/arch (" + this.setupException.getMessage() + "), so the built-in Java engine will be used instead.");
         } else {
            platform.getPlugin().log(Level.WARNING, "Unable to initialise the async-profiler engine: " + this.setupException.getMessage());
            platform.getPlugin().log(Level.WARNING, "Please see here for more information: https://spark.lucko.me/docs/misc/Using-async-profiler");
            this.setupException.printStackTrace();
         }
      }

      return this.profiler != null;
   }

   private static AsyncProfiler load() throws Exception {
      String os = System.getProperty("os.name").toLowerCase(Locale.ROOT).replace(" ", "");
      String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
      Multimap<String, String> supported = ImmutableSetMultimap.builder().put("linux", "amd64").put("macosx", "amd64").put("macosx", "aarch64").build();
      if (!supported.containsEntry(os, arch)) {
         throw new AsyncProfilerAccess.UnsupportedSystemException(os, arch);
      } else {
         String resource = os + "/libasyncProfiler.so";
         URL profilerResource = AsyncProfilerAccess.class.getClassLoader().getResource(resource);
         if (profilerResource == null) {
            throw new IllegalStateException("Could not find " + resource + " in spark jar file");
         } else {
            Path extractPath = TemporaryFiles.create("spark-", "-libasyncProfiler.so.tmp");
            InputStream in = profilerResource.openStream();

            try {
               Files.copy(in, extractPath, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
            } catch (Throwable var11) {
               if (in != null) {
                  try {
                     in.close();
                  } catch (Throwable var9) {
                     var11.addSuppressed(var9);
                  }
               }

               throw var11;
            }

            if (in != null) {
               in.close();
            }

            try {
               return AsyncProfiler.getInstance(extractPath.toAbsolutePath().toString());
            } catch (UnsatisfiedLinkError var10) {
               throw new RuntimeException("A runtime error occurred whilst loading the native library", var10);
            }
         }
      }
   }

   private static void ensureCpuEventSupported(AsyncProfiler profiler) throws Exception {
      String resp = profiler.execute("check,event=cpu").trim();
      if (!resp.equalsIgnoreCase("ok")) {
         throw new UnsupportedOperationException("CPU event is not supported");
      }
   }

   private static final class UnsupportedSystemException extends UnsupportedOperationException {
      public UnsupportedSystemException(String os, String arch) {
         super(os + '/' + arch);
      }
   }
}
