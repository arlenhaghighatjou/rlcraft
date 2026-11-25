package net.ilexiconn.llibrary.server.util;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import net.ilexiconn.llibrary.LLibrary;
import org.apache.commons.io.IOUtils;

public class WebUtils {
   public static final String PASTEBIN_URL_PREFIX = "http://pastebin.com/raw.php?i=";
   private static final ListeningExecutorService EXECUTOR = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(1, 1, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue()));

   public WebUtils() {
   }

   public static String readPastebin(String pasteID) {
      return readURL("http://pastebin.com/raw.php?i=" + pasteID);
   }

   public static List<String> readPastebinAsList(String pasteID) {
      return readURLAsList("http://pastebin.com/raw.php?i=" + pasteID);
   }

   public static String readURL(String url) {
      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader((new URL(url)).openStream()));
         Throwable var2 = null;

         String var3;
         try {
            var3 = IOUtils.toString(reader);
         } catch (Throwable var13) {
            var2 = var13;
            throw var13;
         } finally {
            if (reader != null) {
               if (var2 != null) {
                  try {
                     reader.close();
                  } catch (Throwable var12) {
                     var2.addSuppressed(var12);
                  }
               } else {
                  reader.close();
               }
            }

         }

         return var3;
      } catch (IOException var15) {
         LLibrary.LOGGER.error("Failed to receive data from URL: {}", url, var15);
         return null;
      }
   }

   public static List<String> readURLAsList(String url) {
      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader((new URL(url)).openStream()));
         Throwable var2 = null;

         List var3;
         try {
            var3 = IOUtils.readLines(reader);
         } catch (Throwable var13) {
            var2 = var13;
            throw var13;
         } finally {
            if (reader != null) {
               if (var2 != null) {
                  try {
                     reader.close();
                  } catch (Throwable var12) {
                     var2.addSuppressed(var12);
                  }
               } else {
                  reader.close();
               }
            }

         }

         return var3;
      } catch (IOException var15) {
         LLibrary.LOGGER.error("Failed to receive data from URL: {}", url, var15);
         return null;
      }
   }

   public static BufferedImage downloadImage(String url) {
      try {
         BufferedInputStream in = new BufferedInputStream((new URL(url)).openStream());
         Throwable var2 = null;

         BufferedImage var3;
         try {
            var3 = ImageIO.read(in);
         } catch (Throwable var13) {
            var2 = var13;
            throw var13;
         } finally {
            if (in != null) {
               if (var2 != null) {
                  try {
                     in.close();
                  } catch (Throwable var12) {
                     var2.addSuppressed(var12);
                  }
               } else {
                  in.close();
               }
            }

         }

         return var3;
      } catch (IOException var15) {
         LLibrary.LOGGER.error("Failed to receive data from URL: {}", url, var15);
         return null;
      }
   }

   public static ListenableFuture<String> readURLAsync(String url) {
      return EXECUTOR.submit(() -> {
         return readURL(url);
      });
   }

   public static ListenableFuture<List<String>> readURLAsListAsync(String url) {
      return EXECUTOR.submit(() -> {
         return readURLAsList(url);
      });
   }

   public static ListenableFuture<String> readPastebinAsync(String pasteID) {
      return EXECUTOR.submit(() -> {
         return readPastebin(pasteID);
      });
   }

   public static ListenableFuture<List<String>> readPastebinAsListAsync(String pasteID) {
      return EXECUTOR.submit(() -> {
         return readURLAsList(pasteID);
      });
   }
}
