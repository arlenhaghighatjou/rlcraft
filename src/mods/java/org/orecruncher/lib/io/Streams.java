package org.orecruncher.lib.io;

import com.google.common.io.ByteStreams;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javax.annotation.Nonnull;
import net.minecraft.util.ResourceLocation;
import org.orecruncher.lib.LibLog;

public class Streams {
   public Streams() {
   }

   public static void copy(@Nonnull InputStream iStream, @Nonnull OutputStream oStream) throws IOException {
      ByteStreams.copy(iStream, oStream);
   }

   public static void copy(@Nonnull InputStreamReader iStream, @Nonnull OutputStreamWriter oStream) throws IOException {
      char[] buffer = new char[4096];

      int len;
      while((len = iStream.read(buffer)) != -1) {
         oStream.write(buffer, 0, len);
      }

   }

   public static void copy(@Nonnull InputStream stream, @Nonnull File outFile) {
      try {
         FileOutputStream oFile = new FileOutputStream(outFile, false);
         Throwable var3 = null;

         try {
            copy(stream, oFile);
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (oFile != null) {
               if (var3 != null) {
                  try {
                     oFile.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  oFile.close();
               }
            }

         }
      } catch (Throwable var15) {
         LibLog.log().error("Unable to copy stream to file " + outFile.getName(), var15);
      }

   }

   public static String readResourceAsString(ResourceLocation resource) throws Exception {
      String assetPath = String.format("/assets/%s/%s", resource.getResourceDomain(), resource.getResourcePath());
      InputStream stream = Streams.class.getResourceAsStream(assetPath);
      Throwable var3 = null;

      String var8;
      try {
         if (stream == null) {
            return "";
         }

         BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
         Throwable var5 = null;

         try {
            StringBuilder source = new StringBuilder();

            String line;
            while((line = reader.readLine()) != null) {
               source.append(line).append('\n');
            }

            var8 = source.toString();
         } catch (Throwable var32) {
            var5 = var32;
            throw var32;
         } finally {
            if (reader != null) {
               if (var5 != null) {
                  try {
                     reader.close();
                  } catch (Throwable var31) {
                     var5.addSuppressed(var31);
                  }
               } else {
                  reader.close();
               }
            }

         }
      } catch (Throwable var34) {
         var3 = var34;
         throw var34;
      } finally {
         if (stream != null) {
            if (var3 != null) {
               try {
                  stream.close();
               } catch (Throwable var30) {
                  var3.addSuppressed(var30);
               }
            } else {
               stream.close();
            }
         }

      }

      return var8;
   }
}
