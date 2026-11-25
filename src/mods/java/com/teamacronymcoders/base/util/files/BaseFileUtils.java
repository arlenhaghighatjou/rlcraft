package com.teamacronymcoders.base.util.files;

import com.teamacronymcoders.base.util.Platform;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;

public class BaseFileUtils {
   public BaseFileUtils() {
   }

   public static void copyFromJar(Class<?> jarClass, String filename, File to) {
      URL url = jarClass.getResource("/assets/" + filename);

      try {
         FileUtils.copyURLToFile(url, to);
      } catch (IOException var5) {
         throw new RuntimeException(var5);
      }
   }

   public static void zipFolderContents(File directory, File zipfile) throws IOException {
      URI base = directory.toURI();
      Deque<File> queue = new LinkedList();
      queue.push(directory);
      OutputStream out = new FileOutputStream(zipfile);
      Closeable res = out;

      try {
         ZipOutputStream zout = new ZipOutputStream(out);
         res = zout;

         while(!queue.isEmpty()) {
            directory = (File)queue.pop();
            File[] files = directory.listFiles();
            if (files != null) {
               for(File child : files) {
                  String name = base.relativize(child.toURI()).getPath();
                  if (child.isDirectory()) {
                     queue.push(child);
                     name = name.endsWith("/") ? name : name + "/";
                     zout.putNextEntry(new ZipEntry(name));
                  } else {
                     zout.putNextEntry(new ZipEntry(name));
                     copy(child, zout);
                     zout.closeEntry();
                  }
               }
            }
         }
      } finally {
         res.close();
      }

   }

   private static void copy(InputStream in, OutputStream out) throws IOException {
      byte[] buffer = new byte[1024];

      while(true) {
         int readCount = in.read(buffer);
         if (readCount < 0) {
            return;
         }

         out.write(buffer, 0, readCount);
      }
   }

   private static void copy(File file, OutputStream out) throws IOException {
      InputStream in = new FileInputStream(file);
      Throwable var3 = null;

      try {
         copy(in, out);
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (in != null) {
            if (var3 != null) {
               try {
                  in.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               in.close();
            }
         }

      }

   }

   public static void safeDelete(File file) {
      try {
         file.delete();
      } catch (Exception var2) {
         Platform.attemptLogErrorToCurrentMod("Deleting file " + file.getAbsolutePath() + " failed.");
      }

   }

   public static void safeDeleteDirectory(File file) {
      try {
         FileUtils.deleteDirectory(file);
      } catch (Exception var2) {
         Platform.attemptLogErrorToCurrentMod("Deleting directory " + file.getAbsolutePath() + " failed.");
      }

   }

   public static void createFolder(File file) {
      if (!file.exists() && !file.mkdirs()) {
         Platform.attemptLogErrorToCurrentMod("Couldn't create folder called: " + file.getName());
      }

   }

   public static String readFileToString(File file) {
      String string = "";

      try {
         string = FileUtils.readFileToString(file, Charset.forName("UTF-8"));
      } catch (IOException var3) {
         Platform.attemptLogExceptionToCurrentMod(var3);
      }

      return string;
   }

   public static void writeStringToFile(String string, File file) {
      boolean exists = file.exists();
      if (!exists) {
         try {
            file.getParentFile().mkdirs();
            exists = file.createNewFile();
         } catch (IOException var5) {
            Platform.attemptLogExceptionToCurrentMod(var5);
         }
      }

      if (exists) {
         try {
            FileUtils.writeStringToFile(file, string, StandardCharsets.UTF_8);
         } catch (IOException var4) {
            Platform.attemptLogExceptionToCurrentMod(var4);
         }
      } else {
         Platform.attemptLogErrorToCurrentMod("Couldn't create File: " + file.getName());
      }

   }

   public static void createFile(File file) {
      createFolder(file.getParentFile());

      try {
         file.createNewFile();
      } catch (IOException var2) {
         Platform.attemptLogErrorToCurrentMod("Couldn't create File " + file.getName());
      }

   }
}
